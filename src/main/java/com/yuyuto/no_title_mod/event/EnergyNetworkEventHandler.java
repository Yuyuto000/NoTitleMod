package com.yuyuto.no_title_mod.event;

import com.yuyuto.no_title_mod.NoTitleMod;
import com.yuyuto.no_title_mod.api.energy.INTEnergyConsumer;
import com.yuyuto.no_title_mod.api.energy.INTEnergyGenerator;
import com.yuyuto.no_title_mod.api.energy.NTEnergyCircuitManager;
import com.yuyuto.no_title_mod.industry.energy_cable.EnergyCableBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(modid = NoTitleMod.MODID)
public class EnergyNetworkEventHandler {

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.@NotNull EntityPlaceEvent event){

        Level level = (Level) event.getLevel();
        if(level.isClientSide()) return;
        BlockEntity be = level.getBlockEntity(event.getPos());
        if(be instanceof INTEnergyGenerator || be instanceof INTEnergyConsumer || be instanceof EnergyCableBlockEntity){
            NTEnergyCircuitManager.markDirty(level, event.getPos());
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.@NotNull BreakEvent event){
        Level level = (Level) event.getLevel();
        if(level.isClientSide()) return;
        NTEnergyCircuitManager.markDirty(level, event.getPos());
    }
}
