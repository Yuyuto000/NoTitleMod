package com.yuyuto.no_title_mod.api.utils;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

/**
 * インベントリ間のアイテム搬入出を共通化するユーティリティクラス。
 *
 * <p>
 * このクラスはNoTitleの物流システムの中核となる。
 * ベルトコンベア・ロボットアーム・パイプなど、
 * 「アイテムを運ぶ装置」はこのクラスのみを経由して
 * インベントリへアクセスすることを推奨する。
 * </p>
 *
 * <p>
 * Minecraft Forge 1.20.1
 * </p>
 */
public final class InventoryTransferHelper {

    /**
     * Utilityクラスのためインスタンス化禁止。
     */
    private InventoryTransferHelper() {
    }

    /**
     * 指定されたインベントリへアイテムを挿入する。
     *
     * <p>
     * 全スロットを順番に探索し、
     * 挿入可能な場所へ出来る限りアイテムを格納する。
     * </p>
     *
     * @param handler 挿入先インベントリ
     * @param stack 挿入するアイテム
     *
     * @return 挿入後に余ったItemStack。
     *         全て入った場合はItemStack.EMPTY。
     */
    public static ItemStack insertItem(@NotNull IItemHandler handler, @NotNull ItemStack stack) {

        ItemStack remain = stack.copy();
        for(int i = 0; i < handler.getSlots(); i++) {
            remain = handler.insertItem(i, remain, false);
            if(remain.isEmpty()) return ItemStack.EMPTY;
        }
        return remain;
    }

    /**
     * 指定されたインベントリからアイテムを搬出する。
     *
     * <p>
     * 最初に搬出可能なスロットを探索し、
     * 指定数量だけ取り出す。
     * </p>
     *
     * @param handler 搬出元インベントリ
     * @param amount 搬出数量
     *
     * @return 搬出したItemStack。
     *         搬出できない場合はItemStack.EMPTY。
     */
    public static ItemStack extractItem(@NotNull IItemHandler handler, int amount) {

        for(int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.extractItem(i, amount, false);
            if(!stack.isEmpty()) return stack;
        }
        return ItemStack.EMPTY;
    }

    /**
     * アイテムを挿入可能なスロットを検索する。
     *
     * @param handler 対象インベントリ
     * @param stack 挿入予定アイテム
     *
     * @return スロット番号。
     *         見つからない場合は-1。
     */
    public static int findInsertSlot(@NotNull IItemHandler handler, ItemStack stack) {

        for(int i = 0; i < handler.getSlots(); i++) {
            ItemStack remain = handler.insertItem(i, stack, true);
            if(remain.getCount() != stack.getCount()) return i;
        }
        return -1;
    }

    /**
     * 搬出可能なスロットを検索する。
     *
     * @param handler 対象インベントリ
     *
     * @return スロット番号。
     *         存在しない場合は-1。
     */
    public static int findExtractSlot(@NotNull IItemHandler handler) {

        for(int i = 0; i < handler.getSlots(); i++) {
            if(!handler.getStackInSlot(i).isEmpty()) return i;
        }
        return -1;
    }

    /**
     * 指定アイテムを1つ以上挿入可能か判定する。
     *
     * @param handler 対象インベントリ
     * @param stack 判定対象アイテム
     *
     * @return 挿入可能ならtrue。
     */
    public static boolean canInsert(IItemHandler handler, ItemStack stack) {
        return findInsertSlot(handler, stack) >= 0;
    }

    /**
     * アイテムを搬出可能か判定する。
     *
     * @param handler 対象インベントリ
     *
     * @return 搬出可能ならtrue。
     */
    public static boolean canExtract(IItemHandler handler) {
        return findExtractSlot(handler) >= 0;
    }

     /**
     * インベントリ間でアイテムを搬送する。
     *
     * <p>
     * Conveyor・RobotArm・Pipeなど物流システムは
     * 基本的にこのメソッドを呼び出すだけでよい。
     * </p>
     *
     * @param source 搬出元
     * @param target 搬入先
     * @param amount 搬送数量
     *
     * @return 搬送成功ならtrue。
     */
    public static boolean transfer(IItemHandler source, IItemHandler target, int amount) {

        ItemStack extracted = extractItem(source, amount);
        if(extracted.isEmpty()) return false;
        ItemStack remain = insertItem(target, extracted);
        if(!remain.isEmpty()) {
            // 入らなかった分を戻す
            insertItem(source, remain);
        }
        return remain.isEmpty();
    }
}