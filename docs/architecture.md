# 架构总览

改 `src/` 下的任何代码前先读本文。Random 是纯客户端的"表现层随机化"模组：只替换文字与主贴图的显示，不触碰任何游戏逻辑字段，离开世界后全部恢复。

## 入口与生命周期

`mod.json` 的 `main` 指向 `random.RandomMod`（`hidden: true`，不在模组列表突出显示）。核心流程：

1. **init()**：`RandomSettings.registerDefaults()` 注册设置默认值；headless 服务器直接返回（纯客户端）。创建 StateStore / TextResolver / InstructionTextController / TextureController 四个组件。
2. **事件挂钩**：`ClientLoadEvent`（替换数据库/内容详情对话框；独立形态时注册设置分类）、`WorldLoadEvent`（启动随机化）、`ResetEvent`（全部还原）、`SaveWriteEvent`（存档身份迁移）、`Trigger.update`（动画物品帧重挂）。

## 核心类与数据流

```text
WorldLoadEvent
  |- RandomStateStore.beginWorld()   确定世界身份：save:<存档路径> / map:<地图路径> / session:<会话随机>
  |- cacheKey = algorithm版本 | 身份 | contentSignature | text | texture
  |- RandomTextResolver.begin()              文本池置换 + 同步 localizedName
  |- RandomInstructionTextController.begin() 说明类 bundle 文案置换
  |- RandomTextureController.begin()         方块/物品/单位/区块主贴图置换（fitScale 等比适配）
  `- installTextDialogs()                    仅当原版 dialog 未被其他模组替换时换成 Random*Dialog
ResetEvent  -> 四组件 reset() + 恢复原 dialog + 刷新 Mi2u 缓存
SaveWriteEvent -> postSaveMigration() 把会话身份的映射迁移到存档身份
```

- `RandomPermutationController`：唯一随机算法。FNV-1a 由 `clientSeed|cacheKey|kind` 派生种子，`derangement()` 洗牌并尽量消除不动点（128 次尝试后退化为轮转）；`isPermutation` / `digest` 供缓存校验。
- `RandomStateStore`：映射持久化在 `<设置数据目录>/random-mod/mappings/`，文件名为 `SHA-256(cacheKey|kind)`；`ALGORITHM_VERSION` 编入 cacheKey，算法升级自动作废旧缓存。
- `RandomTextResolver`：按 name / description / details / database-category 分类建池，覆盖 StatCat、Stat 与全部 `UnlockableContent`（含模组内容）；活动期间把置换名写入 `content.localizedName`，reset 时还原。
- `RandomInstructionTextController`：只动前缀 `hint. / gz. / onset. / lst. / lenum. / lglobal.` 且不含 `{0}` 格式参数的 bundle key，直接改写 `Core.bundle` 的属性表并在 reset 时还原。
- `mindustry.ui.dialogs.RandomDatabaseDialog` / `RandomContentInfoDialog`：对游戏内部包的扩展，只改显示文本与搜索谓词（`resolver.matches()` 同时匹配原名与随机名）。安装前校验当前 dialog 是原版类型，不覆盖其他模组的包装。
- `refreshMi2uContentCaches()`：映射变化后对 `mi2-utilities-java` 补发 `ContentInitEvent`，刷新其静态内容缓存。

## 设置系统

独立形态只有两个开关加一把隐藏种子：`random-text`、`random-texture`（均默认关闭）、`random-client-seed`（自动生成的 UUID，不暴露 UI）。独立运行时经 `ui.settings.addCategory("@random.settings", Icon.refresh, ...)` 挂分类。

## Neon 聚合形态

主类带 `public static boolean bekBundled` 与空的 `bekBuildSettings(...)`（无独立设置项，符合 Neon 收口约定）。`bekBundled == true` 时：

- 不注册设置分类；改为向主菜单 `buttons` 表追加具名按钮 `random-bundled-button`（文案 `@random.bundled.button`，"千万别点"）。
- 点击按钮调用 `enableBundledRandom()`，之后进入的世界同时启用文本与贴图随机化，并弹出 `@random.bundled.hint` 提示。不点则完全不介入。

## 目录速查

```text
src/main/java/random/                     模组本体（入口、设置、置换四组件）
src/main/java/mindustry/ui/dialogs/       游戏内部包扩展：RandomDatabaseDialog / RandomContentInfoDialog
src/main/resources/bundles/               文案（en / zh_CN / zh_TW）
src/test/java/random/RandomAlgorithmTest  main() 断言式回归
```

## 设计约束（务必保持）

- 只改显示：`fullIcon` / `uiIcon` / `region` / 文本 getter，不改任何参与玩法的内容字段。
- 一切可逆：World 范围内的修改必须在 `ResetEvent` 完整还原。
- 不与他模组争抢：dialog 已被占用时降级关闭文本随机化并 `Log.warn`，不覆盖。
- 随机结果必须确定可复现：同一 seed + cacheKey 永远得到同一映射（回归任务守护）。
