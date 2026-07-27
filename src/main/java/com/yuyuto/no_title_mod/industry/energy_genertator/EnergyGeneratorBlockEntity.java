package com.yuyuto.no_title_mod.industry.energy_genertator;

import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.widget.*;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.yuyuto.no_title_mod.NoTitleMod;
import com.yuyuto.no_title_mod.api.energy.*;
import com.yuyuto.no_title_mod.gui.NTGuiTextures;
import com.yuyuto.no_title_mod.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class EnergyGeneratorBlockEntity extends BlockEntity implements INTEnergyNode, IUIHolder {

    // 機械内部変数
    private double mechanicalPower = 0;
    private double energy = 0;
    private NTEnergyPacket packet;
    private int soundTick;
    private static final double POWER_THRESHOLD = 0.1;

    public EnergyGeneratorBlockEntity(BlockPos pos, BlockState state){
        super(ModBlockEntities.ENERGY_GENERATOR.get(), pos, state);
    }

    // NBT
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag){
        if(packet != null) tag.put("Packet", packet.save());
        super.saveAdditional(tag);
    }

    @Override
    public void load(@NotNull CompoundTag tag){
        super.load(tag);
        if(tag.contains("Packet")) packet = NTEnergyPacket.load(tag.getCompound("Packet"));
    }

    // Tick
    @SuppressWarnings("unused")
    public static void tick(Level level, BlockPos pos, BlockState state, @NotNull EnergyGeneratorBlockEntity entity){

        entity.findMechanicalPower(level,pos);
        if(entity.mechanicalPower <= POWER_THRESHOLD){
            entity.soundTick = 0;
            return;
        }

        // 発電
        entity.packet = entity.generatePacket();
        if(level.getGameTime() % 20 == 0) NoTitleMod.LOGGER.info("[Generator] pos={},energy={},time={}", entity.worldPosition, Objects.requireNonNull(entity.packet).energy(), entity.packet.time());
        NTEnergyTransfer.transfer(level, entity.worldPosition, new HashSet<>(), entity.packet);

        // 演出
        if(++entity.soundTick >= 20){
            entity.soundTick = 0;
            level.playSound(null, pos, SoundEvents.BEACON_AMBIENT, SoundSource.BLOCKS, 0.4F, 1.0F);
        }
        if(level instanceof ServerLevel server){
            server.sendParticles(ParticleTypes.ELECTRIC_SPARK, pos.getX()+0.5, pos.getY()+1, pos.getZ()+0.5, 2, 0.1, 0.05, 0.1, 0.01);
        }
        entity.setChanged();
    }

    // 発電元動力
    private void findMechanicalPower(Level level, BlockPos pos){
        mechanicalPower = 0;
        for(Direction dir : Direction.values()){
            BlockEntity be = level.getBlockEntity(pos.relative(dir));
            if(be instanceof INTMechanicalPowerSource source){
                mechanicalPower = source.getMechanicalPower();
                return;
            }
        }
    }

    // 発電処理
    private @Nullable NTEnergyPacket generatePacket(){
        if (level == null) return null;
        energy = NTEnergyCalculation.calculateGeneratedVoltage(mechanicalPower, level.getGameTime());
        return new NTEnergyPacket(energy, new HashSet<>(Set.of(worldPosition)), level.getGameTime());
    }

    // GUI
    private @NotNull WidgetGroup createUIWidgets(){
        WidgetGroup group = new WidgetGroup(0, 0, 176, 166);
        group.addWidget(new ImageWidget(0, 0, 176, 130, new ResourceTexture(NTGuiTextures.MONITORING)));
        group.addWidget(new LabelWidget(8, 6, Component.translatable("text.notitlemod.energy_generator")));
        group.addWidget(new LabelWidget(10, 30, () -> "Mechanical: " + String.format("%.2f", mechanicalPower) + " W"));
        group.addWidget(new LabelWidget(10, 45, () -> "Output: " + String.format("%.1f", energy) + " FE/t"));
        return group;
    }

    @Override
    public ModularUI createUI(Player player){
        return new ModularUI(createUIWidgets(), this, player);
    }

    @Override
    public boolean isInvalid(){
        return false;
    }

    @Override
    public boolean isRemote(){
        return false;
    }

    @Override
    public void markAsDirty(){

    }

    @Override
    public NTEnergyNodeType getNodeType() {
        return NTEnergyNodeType.GENERATOR;
    }

    @Override
    public NTEnergyPacket getPacket() {
        return packet;
    }

    @Override
    public BlockPos getPos() {
        return worldPosition;
    }

    @Override
    public void receivePacket(NTEnergyPacket packet) {
        this.packet = packet;
        setChanged();
    }
}