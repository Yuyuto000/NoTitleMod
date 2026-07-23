package com.yuyuto.no_title_mod.industry.energy_genertator;

import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.widget.*;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.yuyuto.no_title_mod.NoTitleMod;
import com.yuyuto.no_title_mod.api.energy.INTMechanicalPowerSource;
import com.yuyuto.no_title_mod.api.energy.NTEnergyAPI;
import com.yuyuto.no_title_mod.api.energy.NTEnergyStorage;
import com.yuyuto.no_title_mod.gui.NTGuiTextures;
import com.yuyuto.no_title_mod.industry.energy_cable.EnergyCableBlockEntity;
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
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

public class EnergyGeneratorBlockEntity extends BlockEntity implements IUIHolder {

    /*
     * =========================
     * Energy System
     * =========================
     */
    // 発電機内部FE
    private final NTEnergyStorage energyStorage =
            new NTEnergyStorage(
                    100000, //容量
                    0,   //入力
                    2000    //出力
            );
    /*
     * 機械入力
     */
    private double mechanicalPower = 0;
    private final LazyOptional<IEnergyStorage> energyHandler = LazyOptional.of(() -> energyStorage);
    /*
     * 発電表示用
     */
    private double generatedEnergy = 0;
    private int soundTick;
    private static final double POWER_THRESHOLD = 0.1;

    public EnergyGeneratorBlockEntity(BlockPos pos, BlockState state){
        super(ModBlockEntities.ENERGY_GENERATOR.get(), pos, state);
    }

    /*
     * =========================
     * NBT
     * =========================
     */
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag){
        tag.putInt("Energy", energyStorage.getEnergyStored());
        super.saveAdditional(tag);
    }

    @Override
    public void load(@NotNull CompoundTag tag){
        super.load(tag);
        energyStorage.receiveEnergy(tag.getInt("Energy"), false);
    }

    /*
     * =========================
     * Tick
     * =========================
     */
    @SuppressWarnings("unused")
    public static void tick(Level level, BlockPos pos, BlockState state, @NotNull EnergyGeneratorBlockEntity entity){

        /*
         * 動力探索
         */
        entity.findMechanicalPower(level,pos);

        /*
         * 動力なし
         */
        if(entity.mechanicalPower <= POWER_THRESHOLD){
            entity.soundTick = 0;
            return;
        }

        /*
         * 発電処理
         */
        entity.generateEnergy();
        entity.outputEnergy();
        if (level.getGameTime() % 20 == 0){
            NoTitleMod.LOGGER.info("[Generator] FE={} Output={}", entity.energyStorage.getEnergyStored(), entity.generatedEnergy);
        }

        /*
         * 演出
         */
        if(++entity.soundTick >= 20){
            entity.soundTick = 0;
            level.playSound(null, pos, SoundEvents.BEACON_AMBIENT, SoundSource.BLOCKS, 0.4F, 1.0F);
        }
        if(level instanceof ServerLevel server){
            server.sendParticles(ParticleTypes.ELECTRIC_SPARK, pos.getX()+0.5, pos.getY()+1, pos.getZ()+0.5, 2, 0.1, 0.05, 0.1, 0.01);
        }
        entity.setChanged();
    }

    /*
     * =========================
     * Mechanical Power
     * =========================
     */
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

    /*
     * =========================
     * Energy Generation
     * =========================
     */
    private void generateEnergy(){

        /*
         * 基本発電量
         *
         * 機械出力をFEへ変換
         */
        double baseEnergy = mechanicalPower * 0.8;
        /*
         * NTEnergyAPIによる変動
         */
        if (level != null) {
            generatedEnergy = NTEnergyAPI.calculateGeneration(baseEnergy, level.getGameTime(), 0.05);
        }
        /*
         * FEへ変換
         */
        int output = (int)Math.max(generatedEnergy, 0);
        energyStorage.addEnergy(output);
    }

    private void outputEnergy(){

        if(level == null){
            return;
        }
        for(Direction direction : Direction.values()){
            BlockEntity target = level.getBlockEntity(worldPosition.relative(direction));
            if(!(target instanceof EnergyCableBlockEntity)){
                continue;
            }
            target.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite()).ifPresent(storage -> {
                int amount = energyStorage.extractEnergy(2000, true);
                int accepted = storage.receiveEnergy(amount, false);
                if(accepted > 0){
                    NoTitleMod.LOGGER.info("[Generator] Send {} FE -> {}", accepted, target.getClass().getSimpleName());
                }
                energyStorage.extractEnergy(accepted, false);
            });
        }
    }

    /*
     * =========================
     * FE Access
     * =========================
     */
    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability, Direction side){
        if(capability == ForgeCapabilities.ENERGY){
            return energyHandler.cast();
        }
        return super.getCapability(capability, side);
    }

    public EnergyStorage getEnergyStorage(){
        return energyStorage;
    }

    @Override
    public void invalidateCaps(){
        super.invalidateCaps();
        energyHandler.invalidate();
    }

    /*
     * =========================
     * GUI
     * =========================
     */
    private @NotNull WidgetGroup createUIWidgets(){
        WidgetGroup group = new WidgetGroup(0, 0, 176, 166);
        group.addWidget(new ImageWidget(0, 0, 176, 130, new ResourceTexture(NTGuiTextures.GENERATOR)));
        group.addWidget(new LabelWidget(8, 6, Component.translatable("text.notitlemod.energy_generator")));
        group.addWidget(new LabelWidget(10, 30, () -> "Mechanical: " + String.format("%.2f", mechanicalPower) + " W"));
        group.addWidget(new LabelWidget(10, 55, () -> "Energy: " + energyStorage.getEnergyStored() + " FE"));
        group.addWidget(new LabelWidget(10, 80, () -> "Output: " + String.format("%.2f", generatedEnergy) + " FE/t"));
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
}