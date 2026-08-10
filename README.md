# Random / 随机

Mindustry 的小型混沌抽签机：名字和主贴图随机，逻辑不乱。  
A tiny chaos lottery for Mindustry: names and main sprites shuffle, logic stays sane.

打开两个设置，然后进入世界。传送带长得像炮塔？恭喜，功能正常。  
Enable both settings and enter a world. If a conveyor looks like a turret, congratulations: it works.

## 功能 Features

- **文本打乱 Text shuffle**：随机打乱数据库文本、战役与液体描述、统计与分类名，以及游戏内说明提示（`hint.`、`gz.`、`onset.` 等前缀，不含带格式参数的动态提示）。Randomizes database text, campaign and liquid descriptions, stat/category names, and in-game instructional tooltips (`hint.`/`gz.`/`onset.`-prefixed keys; format-argument strings are left alone).
- **贴图打乱 Texture shuffle**：随机打乱方块、物品、单位和战役区块图标。Randomizes block, item, unit and campaign sector icons.
- **确定性打乱**：基于客户端种子（`random-client-seed`），同一存档每次进入结果一致。Deterministic per client seed; the same world always maps the same way.
- **纯客户端**：不修改逻辑，离开世界后自动还原。Client-only; everything is restored on world exit.
- **Neon 内嵌模式**：作为组件嵌入 Neon 运行时（`bekBundled`）不添加独立设置页，而是向 Neon 主菜单注入一个“千万别点”按钮；点击后本局与后续世界同时启用文本与贴图打乱。When bundled inside Neon, Random adds a "Do Not Click" / 「千万别点」button to Neon's main menu that enables both layers for the next world and beyond.

## 版本历史 Changelog

- **v1.1.1**：发布构建版本号更新。Release build version bump.
- **v1.1.0**：新增游戏内说明提示（`hint.`/`gz.`/`onset.` 等）文本打乱；新增 Neon 内嵌模式（`bekBundled` 与「千万别点」按钮）；打乱范围扩展至统计与数据库分类/标签。Added instructional-tooltip scrambling, Neon embedded mode, and stat/database category coverage.
- **v1.0.0**：首个发布。Initial release.

## 构建 Build

`./gradlew releaseBuild`：运行算法测试，产出桌面 + Android 合并包到 `dist/` 与 `构建/Random`。Runs tests and outputs the merged desktop+Android jar.
