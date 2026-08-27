# 测试指南

Random 有一个小型自动化回归 + 以手测为主的验证方式。

## 自动化任务

`build.gradle` 注册了一个 main() 断言式的 JavaExec 回归任务，并挂在 `test` 的 `dependsOn` 上（不允许摘掉）：

| 任务 | 主类 | 覆盖内容 |
| --- | --- | --- |
| `randomAlgorithmTest` | `random.RandomAlgorithmTest` | 置换完整性（无重复/遗漏）、不动点规避（含单元素退化池）、cacheKey/digest 稳定性与敏感性、文本 sanitize 保留格式、说明文案选择范围（前缀 + 格式参数排除）、`fitScale` 等比缩放 |

```powershell
.\gradlew.bat test                # 全部（即该回归）
.\gradlew.bat randomAlgorithmTest # 单跑
```

改 `RandomPermutationController` / `RandomStateStore` / `RandomTextResolver.sanitize` / `RandomInstructionTextController` / `RandomTextureController.fitScale` 后必须先跑；纯函数逻辑优先补进该测试类而不是新建框架。

## 手测清单

发版或大改动前，独立形态至少覆盖：

1. **默认关闭**：不开启任何开关进世界，一切表现与原版一致。
2. **文本随机化**：开启 `random-text` 进世界——数据库分类/条目名、内容详情（描述/数据/详情）、统计页 Stat 名称被打乱；搜索框仍能用**原名**搜到内容；退出世界后全部恢复。
3. **贴图随机化**：开启 `random-texture` 进世界——方块（含变体贴图）、物品（含动画物品逐帧）、单位、区块图标互换且大小适配（无拉伸爆边）；放置菜单与数据库图标一致；退出后恢复。
4. **同档复现**：同一存档两次进入，随机结果完全一致；新开一局结果不同。
5. **说明文案**：教程提示与逻辑指令 tooltip（`hint.` / `lst.` / `lenum.` 等前缀）被打乱，普通菜单文字不受影响，含 `{0}` 参数的文案不被选中。
6. **存档迁移**：会话中随机化后存盘退出，重进该存档沿用同一套映射。
7. **共存**：确认与其他替换 dialog 的模组（如有）不冲突——被占用时文本随机化自动关闭并给出警告日志。

## Neon 聚合形态手测

并入 Neon 构建后额外验证：

1. 独立设置分类**不出现**在 Neon 设置页；Neon 设置页无报错分组。
2. 主菜单出现"千万别点"按钮且只出现一次（切换分辨率后不重复追加）。
3. 点击按钮弹出提示，之后再进世界文本 + 贴图同时生效；不点击则完全不生效。
