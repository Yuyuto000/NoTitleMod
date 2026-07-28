package com.yuyuto.no_title_mod.api.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Node間の電圧転送を行うメソッドクラス
 * <p>
 * このクラスでは探索のみ行う。
 */

public class NTEnergyTransfer {

    public static void transfer(Level level, BlockPos myPos, @NotNull NTEnergyPacket packet){
        // 準備-起点posをガチャンとset(myPosとvisited)
        // あと自分のposをしっかりとvisitorにぶち込む
        Set<BlockPos> visited = new HashSet<>(packet.visited());
        visited.add(myPos);
        // packetちゃんの中の送り主の住所をGeoGuesserする
        // myPosの位置を占拠して周囲6方向を凝視
        for (Direction direction : Direction.values()) {
            BlockPos target = myPos.relative(direction);
            // visitedのどれか1つの住所と調べた住所が同じで転送主とハイタッチしちゃったらto be continued
            if (visited.contains(target)) continue;
            BlockEntity be = level.getBlockEntity(target);
            // 隣がNode持ちの突然変異種じゃなかったら to be continued
            if (!(be instanceof INTEnergyNode node))continue;
            // パケットの送り主の住所と調べた住所が同じで送り主とハイタッチしちゃったらto be continued
            if (packet.visited().contains(target)) continue;
            // Node持ちだけれどNodeType.GENERATORだったらto be continued
            if (node.getNodeType() == NTEnergyNodeType.GENERATOR) continue;
            // Node持ち+NodeType.CONSUMERまたはCABLEだったらPacketをTargetへ～Injection★(代入)
            NTEnergyPacket next = new NTEnergyPacket(packet.energy(), visited, packet.time());
            node.receivePacket(next);
            // 終わる条件->6方向全部見たらにしとこ
            // ここに来たということはおしまいDeath★
        }
    }
}
