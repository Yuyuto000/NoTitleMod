# クラス責務 : NTEnergyAPI
NTEnergyAPIは電力を使用したEnergyエネルギーを提供する。\
ここでは、当パッケージ内のクラスの責務分割を管理する。
## 基礎想定マップ
```md
Generator Generator
    │         |
   Node      Node
    ├---------┘
   Edge ─ Edge ─ Edge - Node - Consumer - Node - Edge - Node -Consumer
    │
   Node
    │
Consumer
```
### Node
-> GeneratorとConsumerを使用するための必要な数字及び情報を記述する。Recordクラス。\
**フィールド内容**
- NodeType
- NodePacket
- Energy

**このクラスを使用するクラス**
- EnergyGeneratorBlockEntity
- ConsumerのBlockEntity
---
### NodePacket
-> Node間通信に必須の情報を投入するRecordクラス。考え方は通信システムのHTTPと同じ\
**フィールド内容**
- Energy
- BlockPos(住所、ドメイン)
- Tick(送信GameTime)

**このクラスを使用するクラス**
- EnergyGeneratorBlockEntity
- EnergyCableBlockEntity
- ConsumerのBlockEntity
---
### NodeType
-> 電気回路を使用するブロックの種類を区分する列挙型クラス。\
**列挙内容**
- GENERATOR
- CABLE
- CONSUMER

**このクラスを使用するクラス**
- EnergyGeneratorBlockEntity
- EnergyCableBlockEntity
- ConsumerのBlockEntityクラス
---
### INTEnergyNode
-> EnergyNode保持BlockEntityであることを明示するクラス。インターフェース。\
**要求メソッド内容**
- getPacket
- getPos

**このクラスを使用するクラス**
- EnergyGeneratorBlockEntity
- EnergyCableBlockEntity
- ConsumerのBlockEntityクラス
---
### Calculation
-> 電気計算のメソッドを記述したクラス。ノーマルクラス。
**メソッド内容**
- 電流計算
- 電圧計算
- 電力計算

**このクラスを使用するクラス**
- EnergyGeneratorBlockEntity
- Consumer系BlockEntity
- EnergyCableBlockEntity
---
### Transfer
-> 接続されている一帯のvoltageを送り元のvoltageの値と整合するもの。
**メソッド内容**
- transfer(BlockPos, node.voltage) // 送り込み処理。引数のposを元にEdgeをBFS周遊して送り先のNodeType.CONSUMERであるPosを取得し、
  元の引数のnode.voltageの値をコピー元、探索して抽出したPosにあるConsumerのnode.voltageをコピー先としてNodeの値のコピーを行う。
  コピーされる情報はVoltageだけである。

**このクラスを使用するクラス**
- EnergyGeneratorBlockEntity
- EnergyCableBlockEntity
- ConsumerのBlockEntityクラス

---
# ライフサイクル
## 通常tick
```md
Generator.tick()
↓
if(isSourcePowered)
↓
発電
↓
自Node更新
↓
送信パケット生成
setMyPacket()
↓
Transfer.transfer(pos, packet)
Consumer.tick()へ
```
```md
Consumer.tick()
↓
getPacket()
↓
ConsumerのNode更新
↓
if(isPowered())
↓
仕事開始
↓
setPacket()
↓
Transfer.transfer(pos, packet)
```
## Block設置
```md
onBlockPlace()
↓
Node生成
↓
接続更新処理(Edgeのみ)
```
## Block破壊
```md
BlockBreak
↓
Node削除
↓
接続更新処理(Edgeのみ)
```
## Transfer.transfer()
```md
transfer(BlockPos pos, Set<BlockPos> visited, EnergyPacket packet)
↓
6方向見る
↓
コピー
```