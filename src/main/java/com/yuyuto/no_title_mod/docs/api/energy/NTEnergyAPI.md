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
- Resistance(抵抗)
- Voltage(電圧)
- ID

**このクラスを使用するクラス**
- EnergyGeneratorBlockEntity
- ConsumerのBlockEntity
---
### NodeType
-> 電気回路を使用するブロックの種類を区分する列挙型クラス。\
**列挙内容**
- GENERATOR
- EDGER
- CONSUMER

**このクラスを使用するクラス**
- EnergyGeneratorBlockEntity
- ConsumerのBlockEntityクラス
---
### INTEnergyGenerator
-> Generatorであることを明示するクラス。インターフェース。\
**要求メソッド内容**
- getOutputVoltage

**このクラスを使用するクラス**
- EnergyGeneratorBlockEntity
---
### INTEnergyConsumer
-> 電気使用マシン共通のメソッドを記述。インタフェース。\
**要求メソッド内容**
- getResistance\

**このクラスを使用するクラス**
- ConsumerのBlockEntityクラス
---
### INTEnergyCable　
-> ケーブルの共通要求メソッド。
**要求メソッド内容**
- getCurrentEdge\

**このクラスを使用するクラス**
- EnergyCableBlockEntityクラス
---
### Edge
-> ケーブルのRecordクラス。基本的にNodeやEdgeなどの接続間の移動処理は基本的にTransferが行うこととなる。\
**フィールド内容**
- Set<BlockPos> next // 前後上下左右のうちNodeもしくはEdgeがいるPosを記入する
- transferのinstance // Nodeの受け渡し時に使用\

**このクラスを使用するクラス**
- EnergyCableBlockEntityクラス
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
Transfer.transfer()
↓
Generator.voltage = NodeType.CONSUMER.voltage <- consumer.voltage
↓
Consumer1.tick()へ
```
※Generator->Consumerの場合の通常フローはこうだが、Consumer -> Consumerのフローはこうなる。
```md
Consumer1.tick()
↓
ConsumerのNode更新
↓
if(isPowered())
↓
仕事開始
↓(同時並列実行)
Transfer.transfer()
↓
consumer1.voltage == consumer2.voltage
↓
(Consumer2.tick()へ続く)

```
## Block設置
```md
onBlockPlace()
↓
Node生成 or Edge生成
↓
接続更新処理(Edgeのみ)
```
## Block破壊
```md
BlockBreak
↓
Node削除 or Edge削除
↓
接続更新処理(Edgeのみ)
```
## Transfer.transfer()
```md
transfer(Node.voltage voltage)
↓
BFS
↓
コピー
```