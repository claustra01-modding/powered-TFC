# Powered TFC 仕様

Createの動力設備をTerraFirmaCraftの熱・送風・表示・エンチャント素材へ接続する、Minecraft 1.21.1 / NeoForge向け互換Modである。

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
- Minecraftは `1.21.1`、NeoForgeは `21.1.219`、Javaは `21`。
- Create 6.0.xとTFCは必須依存、More Create Burners（`moreburners`）とCreate: Low-Heated（`createlowheated`）は任意依存とする。Createのmetadata上の対応範囲は `[6.0.0,6.1.0)`、開発・検証版は `gradle.properties` の `create_version` とする。
- versionと依存versionの現在値は `gradle.properties` を正とする。

## 連携仕様

- Create Blaze Burnerは直上へTFC heatを供給する。既定温度はSmouldering 80℃、Fading 1100℃、Kindled 1300℃、Seething 1800℃で、common configから変更可能にする。
- 水平方向へ送風中のEncased Fanは、TFC `IBellowsConsumer` の既定offset先へ `baseAir + abs(speed) * speedMultiplier` の空気を供給する。既定値は100と1.0で、停止中と垂直送風では供給しない。
- More Create Burners連携はpseudo Mixinとして任意に読み込む。実heatと最大heatの比からKindled温度まで線形補間し、upgrade済みElectric BurnerだけSeething温度を上限にする。redstone制御後の値を上限超過させない。
- Create: Low-Heated連携はpseudo Mixinとして任意に読み込み、点火中のBasic Burnerは直上へ480℃のTFC heatを供給する。
- Create Display Linkへ `poweredtfc:tfc_crucible` と `poweredtfc:tfc_blast_furnace` を登録し、TFC CrucibleとBlast Furnaceの温度を数値またはTFC色名で表示する。
- Enchantment Tableのlapis slotとshift-click判定は `#poweredtfc:enchanting_lapis` を受け付ける。同tagにはvanilla lapisと `tfc:gem/lapis_lazuli` を含め、tag欠落時も両IDをfallbackとして扱う。

## 実装上の境界

- 熱・送風処理はserver側tickだけで実行する。
- More Create BurnersとCreate: Low-Heatedを必須依存またはcompile-timeの通常参照へ変えない。
- config値の範囲とkeyは既存world/config互換のため維持する。
