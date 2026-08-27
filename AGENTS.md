# Random 开发指南（AGENTS.md）

本文件面向在本仓库工作的 AI 代理与人类协作者。面向人类的分类文档（架构 / 开发 / 发布 / 测试 / 术语表）在 [`docs/`](docs/README.md)，两者冲突时以本文件为准。

## 项目定位

- Random 是 Mindustry **纯客户端**娱乐模组：在不破坏逻辑的前提下，随机打乱部分文字（数据库、描述、说明提示）与主贴图（方块/物品/单位/区块）。只改显示，离开世界后恢复；服务器无需安装。
- 独立发布身份 `Random`，主入口 `random.RandomMod`（`mod.json`，`hidden: true`），目标 Mindustry v159 / Java 17。
- 本仓库同时是 Neon 聚合模组中同名子模组的**真源**（source of truth）。Neon 通过同步脚本从本仓库拷入源码；同步方向永远是 Random → Neon，不要在 Neon 里改本模组的同步产物。

## 文件结构

```text
mod.json                      模组描述（name/version/main/hidden）
build.gradle                  构建与产物链（含 D8 安卓 dex）
src/main/java/random/         模组本体：RandomMod 入口、RandomSettings、
                              RandomStateStore、RandomPermutationController、
                              RandomTextResolver、RandomInstructionTextController、
                              RandomTextureController
src/main/java/mindustry/ui/dialogs/   游戏内部包扩展：RandomDatabaseDialog、
                              RandomContentInfoDialog（必须留在该包）
src/main/resources/bundles/   文案（bundle / bundle_zh_CN / bundle_zh_TW 三份同步）
src/test/java/random/         RandomAlgorithmTest（main() 断言式回归）
dist/                         本地发布产物副本（gitignore）
```

## 与 Neon 聚合的关系（bekBundled）

主类维护 Neon 注入契约，改动 `RandomMod` 时不得破坏：

- `public static boolean bekBundled`：聚合环境标记，独立运行恒为 `false`。
- `public void bekBuildSettings(SettingsMenuDialog.SettingsTable table)`：空实现——Random 在 Neon 设置页**没有**独立分组，这是有意的。
- `ui.settings.addCategory(...)` 必须保持在 `if(!bekBundled)` 守卫内。
- 聚合形态的行为入口是主菜单 `random-bundled-button` 按钮 → `enableBundledRandom()`；相关 bundle key（`random.bundled.*`）三份语言文件都要有。

## 构建命令

```powershell
.\gradlew.bat releaseBuild        # 一条龙：test + deploy + copyReleaseToDist
.\gradlew.bat compileJava         # 只编译检查
.\gradlew.bat test                # 全部测试（= randomAlgorithmTest）
.\gradlew.bat randomAlgorithmTest # 单跑算法回归
```

发版产物名取决于 `-PreleaseVersion=<x.y.z>`（默认值是旧的 1.1.0，发版必须显式传参），详见 [docs/release.md](docs/release.md)。

## 代码约束

- 只改显示层：`fullIcon` / `uiIcon` / `region` / 文本 getter；不触碰玩法字段。一切 World 内修改必须在 `ResetEvent` 完整还原。
- 随机必须确定可复现：一律经 `RandomPermutationController`（seed 由 `clientSeed|cacheKey|kind` 派生），不得引入 `new Random()` 裸随机。
- 不与他模组争抢：替换 dialog 前校验原版类型，被占用时降级关闭文本随机化并 `Log.warn`。
- 玩家可见文案走 bundle，三份语言文件同步，不硬编码。
- 对游戏内部类的子类扩展必须放在 `mindustry.ui.dialogs` 包下；模组本体只放 `random` 包。

## 验证清单

改动后至少：

1. `.\gradlew.bat test` 全绿。
2. 独立形态：开关全关进世界无变化；开启后文本/贴图按 [docs/testing.md](docs/testing.md) 手测清单生效、退出恢复。
3. 改了 `RandomMod` 的入口/设置结构后，确认 `bekBundled` 契约成员原样存在，再回 Neon 执行同步并验证聚合形态。

## 提交规范

- 单仓库独立 Git 上下文；提交信息风格沿用历史：`Random: <摘要>`、`release: v<x.y.z>`、`perf:` / `docs:` 等前缀。
- 提交只 add 本次任务相关文件，禁止 `git add -A`。
