# 版本与发布

Random 体量小、无 CI，**版本号与构建全部在本地完成**。

## 版本号体系

- 语义化三段式 `x.y.z`（当前 `1.1.1`），需要同步两处：`mod.json` 的 `version` 与 `build.gradle` 的 `version`。
- 发布产物名由 `releaseVersion` 项目属性决定：`Random-<releaseVersion>.jar`。该属性默认值是旧的 `1.1.0`，发版时必须显式传参：
  ```powershell
  .\gradlew.bat releaseBuild -PreleaseVersion=1.1.2
  ```

## 本地发布步骤

1. 更新 `mod.json` 与 `build.gradle` 的版本号。
2. 完整构建并自测：
   ```powershell
   .\gradlew.bat releaseBuild -PreleaseVersion=<x.y.z>
   ```
   `releaseBuild` = `test`（含 `randomAlgorithmTest` 回归）+ `deploy` + `copyReleaseToDist`。
3. 核实产物存在且大小合理：
   - `dist/Random-<x.y.z>.jar`
   - `../构建/Random/Random-<x.y.z>.jar`

## 产物链

```text
classes ──► d8InputJar ──► dexAndroid ──► jarMerged ──► deploy + copyReleaseToDist
            (仅 classes，    (D8 --min-api 14  (classes + classes.dex
             无依赖可打包)    --release --lib)  + mod.json，命名 Random-<ver>.jar)
```

- 依赖全是 `compileOnly`，`d8InputJar` 不需要内嵌任何库（与 Neon 的多依赖合并不同）。
- `dexAndroid` 会把 compile classpath 上的 jar 全部作为 `--lib` 传入，保证 Mindustry/Arc 接口不被当作外部类。
- `jarMerged` 产物是唯一可分发形态：含 `mod.json`、桌面主类与 `classes.dex`，桌面与安卓通用。

## Release 资产安全规则

Mindustry 游戏内安装器会取 Release API 返回的**第一个 `.jar`**，且不按操作系统挑资产。因此：

- 一个 Release 必须有且只有一个 `.jar` 资产，即 `Random-<x.y.z>.jar`（含 `mod.json` + 桌面主类 + `classes.dex`）。
- 严禁把 `Random-d8-input.jar` 等中间产物传上 Release。
- 发布后用 API 复查：
  ```bash
  gh release view <tag> --json assets
  ```
  发现多余 `.jar` 立即删除。
