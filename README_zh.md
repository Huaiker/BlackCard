# BlackCard

> 🌐 : [English](README.md)

[![Minecraft 1.20.1](https://img.shields.io/badge/Minecraft-1.20.1-green?logo=minecraft)](https://www.minecraft.net/)
[![Forge 47.4.13+](https://img.shields.io/badge/Forge-47.4.13%2B-orange)](https://files.minecraftforge.net/)
[![JEI Compatible](https://img.shields.io/badge/JEI-Compatible-blue)](https://github.com/mezz/JustEnoughItems)
![Languages](https://img.shields.io/badge/Languages-13-brightgreen)

一个通用物品凭证模组 —— 黑卡可以合成任意物品！支持三种类型：**单物品黑卡**、**标签黑卡** 和 **模组黑卡**。

---

## 🃏 黑卡类型

### 1. 单物品黑卡 (`black_card`)

指定一个确切的物品进行合成。直接设置目标物品ID和数量。

**NBT 字段：**

| 字段 | 类型 | 说明 |
|------|------|------|
| `targetItem` | 字符串 (TAG_STRING) | 目标物品的命名空间 ID，例如 `"minecraft:diamond"` |
| `targetCount` | 整数 (TAG_INT) | 合成的物品数量。必须在 1 及以上|

**指令示例 — 获取一张合成 32 个钻石的单物品黑卡：**
```
/give @p blackcard:black_card{targetItem:"minecraft:diamond",targetCount:32}
```

---

### 2. 标签黑卡 (`tag_black_card`)

可以合成指定标签（Tag）下的任意物品。使用 **Shift + 滚轮** 切换标签内的物品。

**NBT 字段：**

| 字段 | 类型 | 说明 |
|------|------|------|
| `targetTag` | 字符串 (TAG_STRING) | 标签的命名空间 ID，例如 `"minecraft:logs"` |
| `targetItem` | 字符串 (TAG_STRING) | 自动同步 — 当前选中的标签内物品 |
| `targetCount` | 整数 (TAG_INT) | 合成的物品数量。必须在 1 到物品最大堆叠数之间 |
| `currentItemIndex` | 整数 (TAG_INT) | 当前在标签内的选中索引（从 0 开始）。通过 Shift+滚轮切换 |

> `targetItem` 会根据 `targetTag` 和 `currentItemIndex` 自动同步。手动只需设置 `targetTag` 和 `targetCount`。

**指令示例 — 获取一张合成 `minecraft:logs` 标签物品的标签黑卡（64个）：**
```
/give @p blackcard:tag_black_card{targetTag:"minecraft:logs",targetCount:64,currentItemIndex:0}
```

---

### 3. 模组黑卡 (`mod_black_card`)

可以合成指定模组（通过 modid）下的任意物品。使用 **Shift + 滚轮** 切换模组内的物品。

**NBT 字段：**

| 字段 | 类型 | 说明 |
|------|------|------|
| `targetMod` | 字符串 (TAG_STRING) | 模组 ID（命名空间），例如 `"minecraft"`、`"blackcard"`、`"create"` |
| `targetItem` | 字符串 (TAG_STRING) | 自动同步 — 当前选中的模组内物品 |
| `targetCount` | 整数 (TAG_INT) | 合成的物品数量。必须在 1 到物品最大堆叠数之间 |
| `currentItemIndex` | 整数 (TAG_INT) | 当前在模组内的选中索引（从 0 开始）。通过 Shift+滚轮切换 |

> `targetItem` 会根据 `targetMod` 和 `currentItemIndex` 自动同步。手动只需设置 `targetMod` 和 `targetCount`。

**指令示例 — 获取一张合成 `create` 模组物品的模组黑卡（1个）：**
```
/give @p blackcard:mod_black_card{targetMod:"create",targetCount:1,currentItemIndex:0}
```

---

## 📋 NBT 字段总览

| 字段 | 适用黑卡 | 是否必填 | 是否自动同步 | 说明 |
|------|---------|---------|-------------|------|
| `targetItem` | 单物品 / 标签 / 模组 | 单物品：✅ 标签/模组：❌ | 标签/模组：✅ | 要合成的物品命名空间 ID |
| `targetCount` | 单物品 / 标签 / 模组 | ✅ | ❌ | 合成数量（1 ~ 物品最大堆叠数） |
| `targetTag` | 仅标签黑卡 | ✅ | ❌ | 物品标签 ID |
| `targetMod` | 仅模组黑卡 | ✅ | ❌ | 模组 ID |
| `currentItemIndex` | 标签 / 模组 | ❌（默认 0） | ❌ | 当前选中索引（从 0 开始） |

> ⚠️ 如果 `targetCount` 小于 1 黑卡将无法工作。

---

## 🔧 使用方法

1. **将**黑卡单独放入任意合成栏
2. 配方系统读取 NBT 数据并输出指定物品
3. 黑卡本身**不会被消耗** —— 它是可重复使用的凭证
4. 标签黑卡和模组黑卡持卡时使用 **Shift + 滚轮** 可切换可用物品

---

## 🌍 本地化（13 种语言）

✅ 完全支持以下语言：

| 语言 | 代码 |
|------|------|
| English | `en_us` |
| 简体中文 | `zh_cn` |
| 繁體中文 (台灣) | `zh_tw` |
| 繁體中文 (香港) | `zh_hk` |
| 日本語 | `ja_jp` |
| 한국어 | `ko_kr` |
| Español | `es_es` |
| Português (Brasil) | `pt_br` |
| Português (Portugal) | `pt_pt` |
| Русский | `ru_ru` |
| Deutsch | `de_de` |
| Français | `fr_fr` |
| Italiano | `it_it` |

> 💬 **想添加更多？** 欢迎贡献！

---

## 📦 安装

### 选项 1：下载预构建版本（推荐）

1. 安装 [Minecraft Forge 1.20.1（版本 47.4.13 或更新）](https://files.minecraftforge.net/net/minecraftforge/forge/index_1.20.1.html)
2. 前往 [Releases 页面](https://github.com/yourname/blackcard/releases)
3. 下载最新的文件，名称类似 `blackcard-1.x.x.jar`
4. 将其放入你的 Minecraft `mods` 文件夹：
   - **Windows**: `%appdata%\.minecraft\mods\`
   - **macOS**: `~/Library/Application Support/minecraft/mods/`
   - **Linux**: `~/.minecraft/mods/`
5. 使用 Forge 配置文件启动 Minecraft

---

### 选项 2：从源码构建

1. **安装 Java 17** — 从 [Adoptium](https://adoptium.net/) 下载
2. **下载源代码** — 从 GitHub 克隆或下载 ZIP
3. **构建** — 运行 `./gradlew build`（macOS/Linux）或 `gradlew.bat`（Windows）
4. **找到 jar 文件** — 位于 `build/libs/blackcard-1.x.x.jar`
5. **复制到 `mods` 文件夹** 并使用 Forge 启动

---

## ⚙️ 配置

| 配置项 | 默认值 | 说明 |
|-------|--------|------|
| `enableCustomDisplayName` | `true` | 开启时，黑卡名称显示为「[物品] 黑卡」（例如「钻石 黑卡」）；关闭时仅显示「黑卡」 |

配置文件位置：`config/blackcard-common.toml`

---

## 📄 许可证

本项目基于 MIT 许可证发布。
