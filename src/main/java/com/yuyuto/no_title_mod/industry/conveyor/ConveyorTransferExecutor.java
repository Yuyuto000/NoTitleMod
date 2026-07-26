package com.yuyuto.no_title_mod.industry.conveyor;

import com.yuyuto.no_title_mod.api.utils.InventoryTransferHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.NotNull;

public class ConveyorTransferExecutor {

    public static void execute(ConveyorTransferQueue.@NotNull Request request){

        ServerLevel level = request.level();
        BlockEntity target = level.getBlockEntity(request.target());
        BlockEntity source = level.getBlockEntity(request.from());
        if(!(source instanceof ConveyorBlockEntity conveyor)) return;
        if(target == null){
            conveyor.dropItem();
            return;
        }
        target.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
                    ItemStack remain = InventoryTransferHelper.insertItem(handler, request.stack());
                    if(remain.isEmpty()){
                        conveyor.removeItem();
                    }
                });
    }
}
