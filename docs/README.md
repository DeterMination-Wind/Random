# Random 文档

Random（随机）的分类文档。文档以中文为主，功能名保留英文本名，便于与代码和设置界面对照。

## 从哪里开始

| 你是 | 从这里开始 |
| --- | --- |
| 玩家 | 直接打开设置里的"随机打乱文本 / 随机打乱贴图"即可，无需读文档 |
| 想了解 Random 怎么实现的人 | [架构总览](architecture.md) |
| 想改 Random 代码 | [开发指南](development.md) 与[架构总览](architecture.md) |
| 准备发版 | [版本与发布](release.md) |
| 改完代码要验证 | [测试指南](testing.md) |
| 遇到不认识的词 | [术语表](glossary.md) |

## 文档地图

```text
docs/
|-- README.md         本页：文档导航
|-- architecture.md   架构总览：入口、生命周期、置换管线、Neon 聚合形态
|-- development.md    开发指南：环境、构建命令、代码约束
|-- release.md        版本与发布：版本号、产物链、复制约定
|-- testing.md        测试指南：回归任务、手测清单
`-- glossary.md       术语表
```

## 相关文件

- [AGENTS.md](../AGENTS.md)：仓库维护约束（双形态契约、构建纪律、验证清单）。
- [README.md](../README.md)：项目主页（中英双语）。
- `src/main/resources/bundles/bundle*.properties`：玩家可见文案（en / zh_CN / zh_TW 三份）。

## 维护约定

- Random 是小模组，文档保持六件套、每篇简短；新增子系统时先更新 [architecture.md](architecture.md) 再扩写对应篇目。
- 玩家可见文案新增 key 时，`bundle.properties`、`bundle_zh_CN.properties`、`bundle_zh_TW.properties` 三份必须同步。
- 文档只描述默认行为；Random 的随机结果因种子与世界而异，不要在文档中记录具体置换结果。
