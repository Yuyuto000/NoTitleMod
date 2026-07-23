package com.yuyuto.no_title_mod.industry.energy_cable;

import com.yuyuto.no_title_mod.NoTitleMod;
import com.yuyuto.no_title_mod.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

public class EnergyCableBlockEntity extends BlockEntity {

    /*
     * Cable内部FE
     */
    private final EnergyStorage energyStorage =
            new EnergyStorage(
                    5000,
                    1000,
                    1000
            );
    private final LazyOptional<IEnergyStorage> energyHandler = LazyOptional.of(() -> energyStorage);

    public EnergyCableBlockEntity(BlockPos pos, BlockState state){
        super(ModBlockEntities.ENERGY_CABLE.get(), pos, state);
    }

    /*
     * Tick処理
     */
    public static void tick(@NotNull Level level, BlockPos pos, BlockState state, @NotNull EnergyCableBlockEntity entity){
        entity.transferEnergy();
        if (level.getGameTime() % 20 == 0){
            NoTitleMod.LOGGER.info("[Cable {} ] FE={}", pos, entity.energyStorage.getEnergyStored());
        }
        entity.setChanged();
    }

    /*
     * 周囲へFE送信
     */
    private void transferEnergy(){

        int before = energyStorage.getEnergyStored();
        for(Direction direction : Direction.values()){
            BlockEntity target = level.getBlockEntity(worldPosition.relative(direction));
            if(target == null) continue;
            target.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite()).ifPresent(storage -> {
                int amount = energyStorage.extractEnergy(1000, true);
                int accepted = storage.receiveEnergy(amount, false);
                energyStorage.extractEnergy(accepted, false);
            });
        }
        int after = energyStorage.getEnergyStored();
        if(level.getGameTime()%20==0){
            NoTitleMod.LOGGER.info("[Cable] {} -> {}", before, after);
        }
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability, Direction side){

        if(capability == ForgeCapabilities.ENERGY){
            return energyHandler.cast();
        }
        return super.getCapability(capability, side);
    }

    @Override
    public void invalidateCaps(){
        super.invalidateCaps();
        energyHandler.invalidate();
    }

    /*
     * Cable自身のEnergy取得
     */
    public IEnergyStorage getEnergyStorage(){
        return energyStorage;
    }

    /*
     * NBT保存
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
}