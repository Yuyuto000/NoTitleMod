package com.yuyuto.no_title_mod.industry.conveyor;

import com.yuyuto.no_title_mod.api.utils.InventoryTransferHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ConveyorTransferQueue {

    private static final List<Request> REQUESTS = new ArrayList<>();

    public static void stage(ServerLevel level, BlockPos target, @NotNull ItemStack stack, long executeTick){
        REQUESTS.add(new Request(level, target,stack.copy(), executeTick));
    }

    public static void commit(@NotNull ServerLevel level){

        long now = level.getGameTime();
        Iterator<Request> iterator = REQUESTS.iterator();
        while(iterator.hasNext()){
            Request request = iterator.next();
            if(request.level() != level) continue;
            if(request.executeTick() > now) continue;
            execute(request);
            iterator.remove();
        }
    }

    public static void execute(@NotNull Request request){
        BlockEntity target = request.level().getBlockEntity(request.target());
        if (target == null) {
            dropItem(request);
            return;
        }
        LazyOptional<IItemHandler> optional = target.getCapability(ForgeCapabilities.ITEM_HANDLER);
        if (optional.isPresent()) {
            optional.ifPresent(handler -> {
                ItemStack remain = InventoryTransferHelper.insertItem(handler, request.stack().copy());
                if (!remain.isEmpty()) dropItem(request);
            });
        } else {
            dropItem(request);
        }
    }

    private static void dropItem(@NotNull Request request){
        ServerLevel level = request.level();
        BlockPos pos = request.target();
        ItemEntity entity = new ItemEntity(level, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, request.stack());
        level.addFreshEntity(entity);
    }

    public record Request(
            ServerLevel level,
            BlockPos target,
            ItemStack stack,
            long executeTick
    ){}
}