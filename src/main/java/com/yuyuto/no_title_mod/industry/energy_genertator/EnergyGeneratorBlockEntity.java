package com.yuyuto.no_title_mod.industry.energy_genertator;

import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.widget.*;
import com.lowdragmc.lowdraglib.gui.widget.custom.PlayerInventoryWidget;
import com.lowdragmc.lowdraglib.utils.Position;
import com.yuyuto.no_title_mod.NoTitleMod;
import com.yuyuto.no_title_mod.api.energy.*;
import com.yuyuto.no_title_mod.api.utils.ItemTransferWrapper;
import com.yuyuto.no_title_mod.gui.NTGuiTextures;
import com.yuyuto.no_title_mod.industry.material.FuelMaterials;
import com.yuyuto.no_title_mod.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class EnergyGeneratorBlockEntity extends BlockEntity implements INTEnergyNodeManagements, INTEnergyGenerator, IUIHolder {

    private final NTEnergyNode energyNode = new NTEnergyNode(); //電力ネットワークノード
    private NTEnergyNetwork network; //ノードが所属するネットワーク(ネットワークに接続されているノードで同一)
    private final ItemStackHandler inventory = new ItemStackHandler(1); //インベントリ
    private final ItemTransferWrapper itemTransfer = new ItemTransferWrapper(inventory);
    private int burnTime; //残り燃焼時間
    private int maxBurnTime; // GUI表示用

    // =======================NBT系は触れたらダメ=========================
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag){
        NoTitleMod.LOGGER.info("[EnergyGenerator] SAVE START {}", this.getBlockPos());
        tag.put("inventory", inventory.serializeNBT());
        tag.put("EnergyNode", energyNode.saveNBT());
        super.saveAdditional(tag);
        NoTitleMod.LOGGER.info("[EnergyGenerator] SAVE END {}", this.getBlockPos());
    }

    @Override
    public void load(@NotNull CompoundTag tag){
        super.load(tag);
        inventory.deserializeNBT(tag.getCompound("inventory"));
        energyNode.loadNBT(tag.getCompound("EnergyNode"));
    }
    //=================================================================

    public EnergyGeneratorBlockEntity(BlockPos pos, BlockState state){
        super(ModBlockEntities.ENERGY_GENERATOR.get(), pos, state);
        energyNode.setType(NTEnergyNodeType.GENERATOR);
    }

    // NTEnergyNetwork Systems
    @Override
    public void connection(NTEnergyNetwork network) {
        NoTitleMod.LOGGER.info("[EnergyGenerator] EnergyNetwork connected");
        this.network = network;
    }

    @Override
    public void disconnect() {
        NoTitleMod.LOGGER.info("[EnergyGenerator] EnergyNetwork disconnected");
        this.network = null;
    }

    @Override
    public NTEnergyNode getNode() {
        NoTitleMod.LOGGER.info("[EnergyGenerator] MyEnergyNode send");
        return energyNode;
    }

    @Override
    public BlockPos getNodePosition() {
        NoTitleMod.LOGGER.info("[EnergyGenerator] MyEnergyNodePosition send");
        return worldPosition;
    }

    @Override
    public void onLoad() {
        NoTitleMod.LOGGER.info("[EnergyGenerator] loading");
        super.onLoad();
        if (level == null || level.isClientSide) {
           return;
        }
        buildNetwork();
    }

    @Override
    public void updateEnergyNode(){
        NoTitleMod.LOGGER.info("[EnergyGenerator] update EnergyNode");
        energyNode.setPower(NTEnergyManager.calculatePower(energyNode.getVoltage(), energyNode.getCurrent()));
    }

    public void buildNetwork(){
        NoTitleMod.LOGGER.info("[EnergyGenerator] BuildingNetwork");
        if (level == null){
            return;
        }
        network = NTEnergyNetworkManager.createNetwork(level, worldPosition);
    }

    @Override
    public void setRemoved() {
        NoTitleMod.LOGGER.info("[EnergyGenerator] MyEnergyNode removed");
        if (network != null) {
            NTEnergyNetworkManager.rebuildNetwork(level, network);
        }
        super.setRemoved();
    }

    // Generator process
    @SuppressWarnings("unused")
    public static void tick(Level level, BlockPos pos, BlockState state,@NotNull EnergyGeneratorBlockEntity entity){
        entity.consumeFuel();
        if (entity.burnTime > 0){
            entity.burnTime--;
        }
    }

    private void consumeFuel(){
        NoTitleMod.LOGGER.info("[EnergyGenerator] ConsumingFuel");
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

    public double getBurnProgress(){
        if(maxBurnTime == 0){
            return 0;
        }

        return (double)burnTime / maxBurnTime;
    }

    @Override
    public void generateEnergy(){
        NoTitleMod.LOGGER.info("[EnergyGenerator] Generating Energy");
        if (burnTime <= 0){
            return;
        }
        double maxVoltage = 120;
        if(energyNode.getVoltage() < maxVoltage){
            energyNode.setVoltage(energyNode.getVoltage()+1);
        }
        energyNode.setResistance(10);
        energyNode.setCurrent(NTEnergyManager.calculateCurrent(energyNode.getVoltage(), energyNode.getResistance()));
        energyNode.setPower(NTEnergyManager.calculatePower(energyNode.getVoltage(), energyNode.getCurrent()));
    }

    //GUI
    private @NotNull WidgetGroup createUIWidgets(){
        WidgetGroup group = new WidgetGroup(0, 0, 176, 166);
        group.addWidget(new ImageWidget(0, 0, 176, 166, new ResourceTexture(NTGuiTextures.GENERATOR)));
        group.addWidget(new LabelWidget(8, 6, Component.translatable("text.notitlemod.energy_generator")));
        group.addWidget(new SlotWidget(itemTransfer, 0, 80, 30, true, true));
        group.addWidget(new ProgressWidget(this::getBurnProgress, 150, 20, 10, 60, new ResourceTexture(NTGuiTextures.ENERGY_BAR)));
        group.addWidget(new LabelWidget(8, 23, () -> "Voltage: " + energyNode.getVoltage() + "V"));
        group.addWidget(new LabelWidget(8, 43, () -> "Current: " + energyNode.getCurrent() + "A"));
        group.addWidget(new LabelWidget(8, 63, () -> "Power: " + energyNode.getPower() + "W"));
        PlayerInventoryWidget inventoryWidget = new PlayerInventoryWidget();
        inventoryWidget.setSelfPosition(new Position(2, 83));
        group.addWidget(inventoryWidget);
        return group;
    }

    @Override
    public ModularUI createUI(Player entityPlayer) {
        NoTitleMod.LOGGER.info("[EnergyGenerator] Creating UI");
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