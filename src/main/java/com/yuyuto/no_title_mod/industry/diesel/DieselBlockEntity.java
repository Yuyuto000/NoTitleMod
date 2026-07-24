package com.yuyuto.no_title_mod.industry.diesel;

import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.widget.ImageWidget;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.SlotWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.gui.widget.custom.PlayerInventoryWidget;
import com.lowdragmc.lowdraglib.utils.Position;
import com.yuyuto.no_title_mod.api.energy.INTMechanicalPowerSource;
import com.yuyuto.no_title_mod.api.utils.ItemTransferWrapper;
import com.yuyuto.no_title_mod.gui.NTGuiTextures;
import com.yuyuto.no_title_mod.industry.material.FuelMaterials;
import com.yuyuto.no_title_mod.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class DieselBlockEntity extends BlockEntity implements INTMechanicalPowerSource, IUIHolder {

    private int burnTime;
    private double rpm = 0;
    private int maxBurnTime;
    private static final double MAX_RPM = 1800;
    private static final double RESPONSE = 0.05;
    private static final double TORQUE = 250;
    private final ItemStackHandler inventory = new ItemStackHandler(1);
    private final ItemTransferWrapper itemTransfer =  new ItemTransferWrapper(inventory);
    private int effectTick;
    private int soundTick;

    // =======================NBT系は触れたらダメ=========================
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag){

        tag.put("inventory", inventory.serializeNBT());
        tag.putInt("BurnTime", burnTime);
        tag.putInt("MaxBurnTime", maxBurnTime);
        super.saveAdditional(tag);
    }

    @Override
    public void load(@NotNull CompoundTag tag){
        super.load(tag);
        inventory.deserializeNBT(tag.getCompound("inventory"));
        burnTime = tag.getInt("BurnTime");
        maxBurnTime = tag.getInt("MaxBurnTime");
    }
    //=================================================================

    public DieselBlockEntity (BlockPos pos, BlockState state) {
        super(ModBlockEntities.DIESEL.get(), pos, state);
    }

    @Override
    public double getMechanicalPower() {
        return TORQUE * (2 * Math.PI * rpm / 60.0);
    }

    @SuppressWarnings("unused")
    public static void tick(Level level, BlockPos pos, BlockState state, @NotNull DieselBlockEntity entity) {
        /*
          ここではディーゼルの回転量を指数関数的に増減させるリアルな動きを趣味レーションしている。
          実際の数値計算
          例えば:
          > rpm = 0
          > target = 1800
          なら、
          > 差 = 1800
          > 1800 × 0.05 = 90
          > rpm = 90
          次は
          > 差 = 1710
          > 1710 × 0.05 = 85.5
          > rpm = 175.5
          さらに
          > 差 = 1624.5
          +81........という感じで
          =============================================
          0 -> 90 -> 176 -> 257 -> 334 -> 407 -> 477 ->
          ...
          -> 1790 -> 1795 -> 1798 -> 1800
          =============================================
          みたいに滑らかに近づく。燃料が切れると
          > targetRPM = 0
          になるため、
          ============================================
          1800　-> 1710 -> 1624 -> 1543 -> 1466 -> ...
          ============================================
          と自然に止まる。
         */

        entity.consumeFuel();
        if (entity.burnTime > 0) {
            entity.burnTime--;
        }
        double targetRPM = entity.burnTime > 0 ? MAX_RPM : 0;
        entity.rpm += (targetRPM - entity.rpm) * RESPONSE;
        entity.rpm = Mth.clamp(entity.rpm, 0, MAX_RPM);
        if(entity.rpm < 20){
            return;
        }
        float pitch = (float) (0.7F + entity.rpm / MAX_RPM * 0.6F);
        if (++entity.soundTick >= 20) {
            entity.soundTick = 0;
            level.playSound(null, pos, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 0.5F, pitch);
            level.playSound(null, pos, SoundEvents.LAVA_AMBIENT, SoundSource.BLOCKS, 0.5F, pitch);
        }
        if (++entity.effectTick >= 5) {
            entity.effectTick = 0;
            if (level instanceof ServerLevel server) {
                server.sendParticles(ParticleTypes.LARGE_SMOKE, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 2, 0.1, 0.05, 0.1, 0.01);
            }
        }
    }

    private void consumeFuel(){
        if (burnTime > 0){
            return;
        }
        ItemStack stack = inventory.getStackInSlot(0);
        if (stack.isEmpty()){
            return;
        }
        int burn = FuelMaterials.getBurnTime(stack);
        if (burn <= 0){
            return;
        }
        stack.shrink(1);
        burnTime = burn;
        maxBurnTime = burn;
    }

    private @NotNull WidgetGroup createUIWidget(){
        WidgetGroup group = new WidgetGroup(0, 0, 176, 166);
        group.addWidget(new ImageWidget(0, 0, 176, 166, new ResourceTexture(NTGuiTextures.MACHINE_INVENTORY)));
        group.addWidget(new SlotWidget(itemTransfer, 0, 80, 30, true, true));
        group.addWidget(new LabelWidget(8, 6, Component.translatable("text.notitlemod.diesel")));
        group.addWidget(new LabelWidget(6, 50, () -> "RPM: " + String.format("%.0f", rpm)));
        group.addWidget(new LabelWidget(6, 70, () -> "Power: " + String.format("%.1f", getMechanicalPower()) + " W"));
        PlayerInventoryWidget inventoryWidget = new PlayerInventoryWidget();
        inventoryWidget.setSelfPosition(new Position(2, 83));
        group.addWidget(inventoryWidget);
        return group;
    }

    @Override
    public ModularUI createUI(Player entityPlayer){
        return new ModularUI(createUIWidget(), this, entityPlayer);
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
