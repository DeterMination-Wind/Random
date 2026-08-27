# 开发指南

环境、日常构建命令与代码约束。仓库级硬性约束见根目录 [AGENTS.md](../AGENTS.md)，两者冲突时以 AGENTS.md 为准。

## 环境

- JDK 17+（`settings.gradle` 启动即校验，低于 17 直接失败；`build.gradle` 用 `options.release.set(17)` 出字节码）。
- Mindustry / Arc 编译类路径优先取工作区本地检出：`../Mindustry-master/core/build/libs/core-release.jar` 与 `../Arc/arc-core/build/libs/arc-core-1.0.jar`；不存在时退回 JitPack `v159`（可用 `-PmindustryVersion=` 覆盖）。依赖均为 `compileOnly`，最终 jar 不内嵌任何库。
- 安卓 dex 需要 D8：探测顺序 `D8_PATH` → `ANDROID_SDK_ROOT` / `ANDROID_HOME` 的 `build-tools` → 工作区 `commandlinetools-win-*` 目录。

## 常用命令

```powershell
# 一条龙：跑测试 + 出合并 jar + 复制到 dist/ 与 ../构建/Random
.\gradlew.bat releaseBuild

# 只编译检查
.\gradlew.bat compileJava

# 全部测试（等价于单跑下面的回归任务）
.\gradlew.bat test

# 单跑算法回归
.\gradlew.bat randomAlgorithmTest
```

产物链 `releaseBuild` = `test` + `deploy` + `copyReleaseToDist`，细节见 [release.md](release.md)。

## 代码风格要点

- 遵循 Mindustry 源码风格：大括号换行、`camelCase` / `PascalCase`、ASCII 标识符；文件编码 UTF-8。
- 模组本体代码只放 `random` 包；对游戏内部类的扩展（子类化 `DatabaseDialog` / `ContentInfoDialog`）必须放在 `mindustry.ui.dialogs` 同名包下。
- 玩家可见文案一律走 bundle，三份语言文件同步；不硬编码。
- 新增随机维度时：新增 `kind` 字符串 + 复用 `RandomPermutationController.mapping`，并把 `kind` 加进 `RandomStateStore` 的迁移名单（如适用）。

## 调试建议

- 随机结果"不对"时先确认三件事：开关是否开着、cacheKey 各段（存档身份 / 内容签名 / 开关组合）是否变化、`random-mod/mappings/` 下的缓存文件是否残留旧算法版本。
- 贴图显示错位通常是 `fitScale` 相关，改 `RandomTextureController` 后跑 `randomAlgorithmTest`（含等比缩放断言）。
- 数据库没被随机化：查日志中的 `disabled because another mod owns the database or content dialog`，说明有其他模组先替换了 dialog，属预期降级。
