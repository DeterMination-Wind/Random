# 术语表

按主题归类的 Random 术语。每条说明它在 Random 语境下的准确含义。

## 随机机制

### derangement（错位排列）
没有任何元素留在原位的置换。Random 用它保证"传送带不会随机到自己"。128 次洗牌尝试失败后退化为整体轮转（单元素池除外）。

### client seed（客户端种子）
存在设置 `random-client-seed` 里的 UUID，首次运行自动生成。它是所有置换的根种子；同一台客户端对同一世界永远给出同一套随机结果。

### 世界身份（identity）
cacheKey 里的身份段，决定"哪些世界共享映射"：`save:<存档绝对路径>`（有存档时）、`map:<地图文件路径>`（无存档的地图局）、`session:<会话UUID>:<序号>:<地图名>`（临时局）。

### content signature（内容签名）
对当前加载的全部内容集（类型、数量、id、名字、隐藏位、图标尺寸）计算的 SHA-256 摘要。装了新模组或内容变化时签名改变，cacheKey 随之作废，重新生成映射。

### cacheKey
`algorithm=<版本>|identity=<身份>|content=<签名>|text=<开关>|texture=<开关>` 的组合串。映射缓存按 `SHA-256(cacheKey|kind)` 命名存放，任何一段变化都会产生全新映射。

### ALGORITHM_VERSION
写死在 `RandomStateStore` 的算法版本号（当前为 2），编入 cacheKey。算法升级时递增，旧缓存文件自然失效（不删除）。

### kind
映射的分类维度：`name` / `description` / `details` / `database-category` / `instruction` / `texture`。同一 cacheKey 下各 kind 独立置换、独立缓存。

## 表现层

### instruction text（说明文案）
bundle 里前缀为 `hint.` / `gz.` / `onset.` / `lst.` / `lenum.` / `lglobal.` 的教学与逻辑指令文案。只打乱不含 `{0}` 格式参数的条目，避免占位符错位。

### localizedName 同步
原版放置 HUD 等界面直接读 `content.localizedName` 字段。随机化期间把置换名写入该字段、reset 时还原，保证这些界面也显示随机名。

### fitScale
`RandomTextureController` 的等比缩放系数：把来源贴图按目标槽位宽高取 `min(宽比, 高比)` 缩放，防止随机互换后图标拉伸变形。

### fullIcon / uiIcon
内容的完整图标与 UI 小图标。贴图随机化同时替换这两个字段以及方块 `region` / `variantRegions`、单位 `region`，其余字段一律不动。

## 项目形态

### bekBundled
`RandomMod` 上的静态布尔标记，表示"当前运行在 Neon 聚合环境中"。为 `true` 时不注册独立设置，改由主菜单按钮启用随机化。

### 千万别点（bundled button）
Neon 聚合形态下追加到主菜单的具名按钮（`random-bundled-button`，文案 `@random.bundled.button`）。点击后对本进程及之后的世界启用文本 + 贴图随机化——名字即警告。

### Mi2u 兼容
`mi2-utilities-java` 模组缓存了静态内容展示组件。Random 的映射建立或还原后会补发 `ContentInitEvent`，触发其缓存刷新。
