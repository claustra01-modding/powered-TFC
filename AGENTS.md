# Powered TFC 仕様

Createの動力設備をTerraFirmaCraftの熱・送風・mechanical power・表示・エンチャント素材へ接続する、Minecraft 1.21.1 / NeoForge向け互換Modである。

## 共通開発ルール

- 仕様ファイル名は大文字の `AGENTS.md` に統一する。
- 本書は現在維持すべき仕様を記録し、挙動、対応バージョン、依存関係、ID、登録内容、有効化条件、生成規則、検証手順の変更と同時に更新する。
- READMEは利用者向けの短い概要、配布先、build入口に絞り、詳細仕様を重複掲載しない。
- ライセンスと第三者表示はrootの `LICENCE` 一つへ統合し、別のlicense/noticeファイルを作らない。
- 現在値は `gradle.properties`、Gradle設定、Mod metadata、実装コード、同梱dataを正本とし、別バージョンの記憶ではなく対象版の公式ソース、依存ソース、実JAR/dataで確認する。
- 公開API、registry、tag、dataを優先し、Mixinやreflectionは必要な対象へ限定する。任意依存のクラスは、そのModがない通常ロード経路から参照しない。
- client専用クラスをcommon/server側から参照せず、専用サーバーのclass loadingを考慮する。公開済みIDとconfig keyは互換性を優先する。
- 依存JAR、展開物、解析・生成scriptは `.tmp/` に置いてGit管理外にし、BOMなしUTF-8のJSONを使う。
- `.gradle/`、`build/`、`run/`、IDE metadata、依存JARを変更対象に含めず、無関係な既存差分を編集しない。
- 変更前に既存の登録、命名、resource配置を確認し、依頼外のrename、format変更、依存・version更新を混ぜない。
- 通常は `./gradlew compileJava`、完了時は `./gradlew build` を実行する。data変更時は全JSONをparseし、optional連携とMixin変更では対象Modの有無と専用サーバー安全性を確認する。
- Minecraftクライアントはランタイム確認が必要な変更または明示依頼時だけ起動し、未実施の検証は理由と範囲を報告する。

## 基本情報

- Mod IDは `poweredtfc`、Java packageは `net.claustra01.poweredtfc`。
- Minecraftは `1.21.1`、NeoForgeは `21.1.235`、Javaは `21`。
- Create 6.0.xとTFC 4.2.xは必須依存、More Create Burners（`moreburners`）、Create: Low-Heated（`createlowheated`）、JEI（`jei`、clientのみ）は任意依存とする。Createのmetadata上の対応範囲は `[6.0.0,6.1.0)`、TFCは `[4.2.5,4.3.0)`、開発・検証版は `gradle.properties` の各versionとする。
- versionと依存versionの現在値は `gradle.properties` を正とする。

## 連携仕様

- Create Blaze Burnerは直上へTFC heatを供給する。既定温度はSmouldering 80℃、Fading 1100℃、Kindled 1300℃、Seething 1800℃で、common configから変更可能にする。
- 水平方向へ送風中のEncased Fanは、TFC `IBellowsConsumer` の既定offset先へ `baseAir + abs(speed) * speedMultiplier` の空気を供給する。既定値は100と1.0で、停止中と垂直送風では供給しない。
- More Create Burners連携はpseudo Mixinとして任意に読み込む。実heatと最大heatの比からKindled温度まで線形補間し、upgrade済みElectric BurnerだけSeething温度を上限にする。redstone制御後の値を上限超過させない。
- Create: Low-Heated連携はpseudo Mixinとして任意に読み込み、点火中のBasic Burnerは直上へ480℃のTFC heatを供給する。`createlowheated:burner_starters`へ`tfc:firestarter`と`tfc:flint_and_pyrite`を追加し、TFCの着火具でもBasic Burnerを点火できるようにする。
- `poweredtfc:create_to_tfc_converter` はFACING面をCreate入力、反対面をTFC出力として、CreateのRPMを同じ角速度のTFC rad/tickへ変換する。Create側のstress impactは既定64 SUでcommon configの `mechanical_converters.createToTfcStressImpact` から変更できる。TFC側では出力面だけを接続面とするSourceNodeとして登録し、既存TFC networkと不正に競合した場合はTFC標準挙動に従ってブロックを破壊する。
- `poweredtfc:tfc_to_create_converter` はFACING面をCreate出力、反対面に直接隣接するTFC `RotatingBlockEntity` を入力として、TFC rad/tickを同じ角速度のCreate RPMへ変換する。Create側のstress capacityは既定64 SUでcommon configの `mechanical_converters.tfcToCreateStressCapacity` から変更できる。自己変換の直接loopを避けるため、入力が `create_to_tfc_converter` 自身の場合は出力しない。
- 両converterは赤石信号を受けている間は変換を停止する。FACING面だけにCreate shaftを持ち、TFC面は常にその反対面とする。block item、Create基本creative tab、self-drop loot、shapeless crafting recipeを登録する。Create→TFC converterはClutch、小さいCogwheel、`#tfc:axles`を各1個、TFC→Create converterはClutch、小さいCogwheel、Create Shaftを各1個材料とする。
- Create Sequenced Assemblyの主入力はTFC custom ingredientによるheat条件を使用できる。条件は工程開始時だけ判定し、開始後に要求温度を下回っても工程を継続する。heat条件の有無を問わず、加熱可能な主入力の温度と熱容量を中間アイテムへ引き継いでTFCの時間基準で自然冷却させる。完成時はその時点の温度を抽選済み出力へ引き継ぎ、以後は出力自身の熱容量で冷却する。出力が加熱不可能な場合はheatを付与しない。
- 921.00006℃以上の `tfc:raw_iron_bloom` はMechanical Pressによる3工程・3 loopsで `tfc:refined_iron_bloom` になり、同温度以上の `tfc:refined_iron_bloom` は同じ工程数で `tfc:metal/ingot/wrought_iron` になる。
- JEIがあるclientでは、主入力と表示出力がともにTFC heat対応のSequenced Assembly出力tooltipへ、工程中の自然冷却と完成時の温度引き継ぎを英語で表示する。
- Create Display Linkへ `poweredtfc:tfc_crucible` と `poweredtfc:tfc_blast_furnace` を登録し、TFC CrucibleとBlast Furnaceの温度を数値またはTFC色名で表示する。
- Create Basinの`inputTank`は、`BasinBlockEntity#addBehaviours()`内のCreate標準input tank生成直後へMixinを挿入し、`SmartFluidTankBehaviour.INPUT`・容量1000 mBの4区画へ差し替える。fluid update callbackはBasinの`notifyChangeOfContents`へ接続し、Create標準のbehaviour登録・fluid capability構築には差し替え後のtankを使わせる。
- Create Basin recipeの`BasinRecipe#getMaxFluidInputCount()`もMixinで4へ拡張する。これにより3または4個のfluid ingredientを持つrecipeがCreateのvalidationで除外されず、Create JEIの`BasinCategory`が持つ可変fluid slot描画で表示できる。fluid outputの上限は変更しない。
- Enchantment Tableのlapis slotとshift-click判定は `#poweredtfc:enchanting_lapis` を受け付ける。同tagにはvanilla lapisと `tfc:gem/lapis_lazuli` を含め、tag欠落時も両IDをfallbackとして扱う。
- Create 6.0.10、Create: Low-Heated、More Create Burnersおよび本Modのconverter itemへTFC 4.2.5のitem size/weightをdataで定義する。各分類は `#poweredtfc:item_size/create_<size>_<weight>` と対応する `poweredtfc:tfc/item_size/create_<size>_<weight>.json` の組にし、任意依存のtag entryはすべて `required: false` とする。
- item size/weightはTFC自身の同種itemまたはblockを最優先し、類似物がない場合だけ形状と用途で決める。TFC側で個別定義のない通常blockはsmall/lightとして扱う。Createのgearbox/vertical gearbox、shaft、fluid pipe、pump、fan、press、windmill bearingおよび本Modconverterは、対応するTFC gear box、axle、steel pipe、steel pump、bellows、trip hammer、windmillと同じsmall/lightにする。
- 粉はtiny/very_light、Super GlueはTFC glueと同じtiny/light、nuggetはvery_small/very_light、slabとrope/cardboardはsmall/very_light、ore pieceとcrushed oreはsmall/medium、rail/trackはlarge/very_light、ingot・sheet・bucketはlarge/mediumとする。toolboxはTFC tool rackと同じlarge/very_heavy、fluid tank・basin・item vaultはLarge Vesselと同じhuge/heavy、doorはvery_large/heavy、sail・water wheel・minecart/contraptionはvery_large/very_heavy、MillstoneはQuernと同じvery_large/medium、burnerはfirepit attachmentと同じvery_large/heavyにする。clock、lamp/nixie tube、sign/placard、schematic table、table cloth、armor、toolも対応するTFC分類へ合わせる。
- 対象版Createの翻訳済みitem/block IDを各1分類へ網羅し、Create: Low-HeatedのBasic Burner、More Create Burnersの全item、本Modのconverter 2種も重複なく分類する。item-only IDには暗黙fallbackを許さず、全IDを明示分類する。対象JARの変更時は `.tmp/tools/regenerate_create_item_sizes.py` で再生成し、未分類・重複、方向/色/材質variant間の不一致、TFC参照定義の変更を検証する。任意entryは `required: false` として、実在しないIDがresource reloadを失敗させないことを保つ。

## 実装上の境界

- 熱・送風処理はserver側tickだけで実行する。
- TFCからCreateへのmechanical power読取とCreate network更新はserver側だけで行う。CreateからTFCへのSourceNodeはTFC network lifecycleに合わせてload/unloadし、clientでは同期済みCreate speedから表示回転を更新する。
- More Create Burners、Create: Low-Heated、JEIを必須依存へ変えず、各任意連携クラスを対象Modなしの通常ロード経路から参照しない。
- config値の範囲とkeyは既存world/config互換のため維持する。
