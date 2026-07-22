package com.yuyuto.no_title_mod.industry.energy_genertator;

import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.widget.*;
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

public class EnergyGeneratorBlockEntity extends BlockEntity implements INTEnergyNodeManagements, INTEnergyGenerator, IUIHolder {

    private final NTEnergyNode energyNode = new NTEnergyNode(); //電力ネットワークノード
    private NTEnergyNetwork network; //ノードが所属するネットワーク(ネットワークに接続されているノードで同一)
    private double mechanicalPower; // 発電機が現在受け取ってる機械出力
    private double maxOutputCurrent;
    private int soundTick;
    // =======================NBT系は触れたらダメ=========================
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag){
        tag.put("EnergyNode", energyNode.saveNBT());
        super.saveAdditional(tag);
    }

    @Override
    public void load(@NotNull CompoundTag tag){
        super.load(tag);
        energyNode.loadNBT(tag.getCompound("EnergyNode"));
    }
    //=================================================================

    public EnergyGeneratorBlockEntity(BlockPos pos, BlockState state){
        super(ModBlockEntities.ENERGY_GENERATOR.get(), pos, state);
        energyNode.setType(NTEnergyNodeType.GENERATOR);
    }

    @Override
    public double getMaxOutputCurrent() {
        return maxOutputCurrent;
    }

    @Override
    public NTEnergyNode getNode() {
        return energyNode;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level == null || level.isClientSide) {
           return;
        }
        buildNetwork();
    }

    @Override
    public void updateEnergyNode(){
        energyNode.setPower(NTEnergyManager.calculatePower(energyNode.getVoltage(), energyNode.getCurrent()));
    }

    public void buildNetwork(){
        if (level == null){
            return;
        }
        network = NTEnergyNetworkManager.createNetwork(level, worldPosition);
    }

    @Override
    public void setRemoved() {
        if (network != null) {
            NTEnergyNetworkManager.rebuildNetwork(level, network);
        }
        super.setRemoved();
    }

    // Generator process
    @SuppressWarnings("unused")
    public static void tick(Level level, BlockPos pos, BlockState state,@NotNull EnergyGeneratorBlockEntity entity){
        entity.mechanicalPower = 0;
        for (Direction dir : Direction.values()) {
            BlockEntity be = level.getBlockEntity(pos.relative(dir));
            if (be instanceof INTMechanicalPowerSource source) {
                entity.mechanicalPower = source.getMechanicalPower();
                break;
            }
        }
        if (entity.mechanicalPower <= 0) {
            return;
        }
        if (++entity.soundTick >= 20) {
            entity.soundTick = 0;
            level.playSound(null, pos, SoundEvents.BEACON_AMBIENT, SoundSource.BLOCKS, 0.4F, 1.0F);
        }
        if (level instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.ELECTRIC_SPARK, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 2, 0.1, 0.05, 0.1, 0.01);
        }
    }

    @Override
    public void generateEnergy() {

        if(network == null){
            return;
        }
        if(mechanicalPower <= 0){
            energyNode.setVoltage(0);
            maxOutputCurrent = 0;
            return;
        }
        final double efficiency = 0.95;
        final double voltage = 120;
        energyNode.setVoltage(voltage);
        double electricalPower = mechanicalPower * efficiency;
        maxOutputCurrent = electricalPower / voltage;
    }

    //GUI
    private @NotNull WidgetGroup createUIWidgets(){
        WidgetGroup group = new WidgetGroup(0, 0, 176, 166);
        group.addWidget(new ImageWidget(0, 0, 176, 130, new ResourceTexture(NTGuiTextures.GENERATOR)));
        group.addWidget(new LabelWidget(8, 6, Component.translatable("text.notitlemod.energy_generator")));
        group.addWidget(new LabelWidget(10, 23, () -> "Mechanical: " + String.format("%.1f", mechanicalPower) + "W"));
        group.addWidget(new LabelWidget(10, 43, () -> "Voltage: " + String.format("%.1f",energyNode.getVoltage()) + "V"));
        group.addWidget(new LabelWidget(10, 63, () -> "Current: " + String.format("%.1f",energyNode.getCurrent()) + "A"));
        group.addWidget(new LabelWidget(10, 83, () -> "Power: " + String.format("%.1f",energyNode.getPower()) + "W"));
        return group;
    }

    @Override
    public ModularUI createUI(Player entityPlayer) {
        return new ModularUI(createUIWidgets(), this, entityPlayer);
    }

    @Override
    public boolean isInvalid() {
        return false;
    }

    @Override
    public boolean isRemote() {
        return false;
    }

    @Override
    public void markAsDirty() {

    }
}