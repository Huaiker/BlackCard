# BlackCard - Minecraft 1.20.1 Forge

[![Minecraft 1.20.1](https://img.shields.io/badge/Minecraft-1.20.1-green?logo=minecraft)](https://www.minecraft.net/)
[![Forge 47.4.13+](https://img.shields.io/badge/Forge-47.4.13%2B-orange)](https://files.minecraftforge.net/)
[![Version 2.0.0](https://img.shields.io/badge/Version-2.0.0-blue)]()

通用物品凭证模组 —— 黑卡可以合成任意物品！支持多物品绑定、黑名单过滤、Cell⁴ 联动。

[English](README.md)

---

## 🃏 黑卡类型

### 1. 单物品黑卡 (`black_card`)

绑定一个或多个具体物品，合成时产出当前选中的非黑名单物品。当绑定多个物品时，**Shift + 滚轮** 切换物品。

```mcfunction
# 单个物品
/give @p blackcard:black_card{bcitem:"minecraft:diamond",targetCount:1}

# 多个物品（列表格式，与 Cell⁴ 对齐）
/give @p blackcard:black_card{bcitem:["minecraft:diamond","minecraft:iron_ingot"],targetCount:1}

# 带黑名单
/give @p blackcard:black_card{bcitem:"minecraft:diamond",targetCount:1,bcblacklist:["minecraft:bedrock"]}
```

### 2. 标签黑卡 (`tag_black_card`)

绑定一个或多个物品标签，**Shift + 滚轮** 切换标签内物品。

```mcfunction
# 单个标签
/give @p blackcard:tag_black_card{bctag:"minecraft:logs",targetCount:1}

# 多个标签
/give @p blackcard:tag_black_card{bctag:["minecraft:logs","forge:ingots/iron"],targetCount:1}

# 带黑名单
/give @p blackcard:tag_black_card{bctag:"minecraft:logs",targetCount:1,bcblacklist:"minecraft:crimson_stem"}
```

### 3. 模组黑卡 (`mod_black_card`)

绑定一个或多个模组ID，**Shift + 滚轮** 切换模组内物品。

```mcfunction
# 单个模组
/give @p blackcard:mod_black_card{bcmodid:"mekanism",targetCount:1}

# 多个模组
/give @p blackcard:mod_black_card{bcmodid:["mekanism","thermal"],targetCount:1}

# 带黑名单
/give @p blackcard:mod_black_card{bcmodid:"mekanism",targetCount:1,bcblacklist:["mekanism:creative_bin"]}
```

---

## 📋 NBT 键对照表

| 新键（v2.0） | 旧键（兼容） | 说明 | Cell⁴ 对应键 |
|:---:|:---:|:---|:---:|
| `bcitem` | `targetItem` | 物品ID（字符串或列表） | `cell4item` |
| `bctag` | `targetTag` | 标签ID（字符串或列表） | `cell4tag` |
| `bcmodid` | `targetMod` | 模组ID（字符串或列表） | `cell4modid` |
| `bcblacklist` | — | 黑名单物品ID（字符串或列表） | `cell4blacklist` |
| `targetCount` | `targetCount` | 合成输出数量（整数，默认1） | — |
| `currentItemIndex` | `currentItemIndex` | 当前循环索引（多值绑定的黑卡） | — |
| `targetItem` | `targetItem` | 当前选中的物品ID（自动同步） | — |

> 所有标识符键支持**单字符串**和**字符串列表**两种格式，与 Cell⁴ 的 NBT 格式完全一致。旧版键自动兼容。

---

## 🚫 黑名单

所有黑卡支持 `bcblacklist` 键排除特定物品：

- 合成时**跳过**黑名单物品
- 多值绑定时**循环跳过**黑名单物品
- Tooltip 中以红色标记显示

```mcfunction
# 单个黑名单物品
/give @p blackcard:black_card{bcitem:"minecraft:diamond",bcblacklist:"minecraft:bedrock"}

# 多个黑名单物品
/give @p blackcard:mod_black_card{bcmodid:"mekanism",bcblacklist:["mekanism:creative_bin","mekanism:creative_energy_cube"]}
```

---

## 🔗 Cell⁴ 联动

### 合成配方

黑卡 + AE2 物品元件外壳（`ae2:item_cell_housing`） → Cell⁴ 无限元件

| 输入 | + | → 输出 |
|:---|:---:|:---|
| Black Card | AE2 Cell Housing | Cell⁴ Infinity Item Cell |
| Tag Black Card | AE2 Cell Housing | Cell⁴ Infinity Tag Cell |
| Mod Black Card | AE2 Cell Housing | Cell⁴ Infinity ModID Cell |

### NBT 自动转换

合成时黑卡的 NBT 自动转为 Cell⁴ 格式：

| BlackCard | Cell⁴ | 说明 |
|:---:|:---:|:---|
| `bcitem` | `cell4item` | 物品标识符 |
| `bctag` | `cell4tag` | 标签标识符 |
| `bcmodid` | `cell4modid` | 模组ID标识符 |
| `bcblacklist` | `cell4blacklist` | 黑名单 |
| `targetCount` | 丢弃 | Cell⁴ 无限元件无需数量 |

### 前置模组

- **AE2** v15.0.0+ — 提供 `item_cell_housing`
- **Cell⁴** v1.0.0+ — 提供无限元件

---

## 🔧 使用方法

1. **合成**：将黑卡单独放入合成栏，即可产出指定物品（黑卡不消耗）
2. **切换**：手持物品黑卡（多物品绑定）、标签黑卡或模组黑卡时，**Shift + 滚轮** 切换物品
3. **联动**：黑卡 + AE2 元件外壳 → Cell⁴ 无限元件

---

## 📦 依赖

| 模组 | 必需 | 版本 |
|:---:|:---:|:---|
| Minecraft | ✅ | 1.20.1 |
| Forge | ✅ | 47.4.13+ |
| JEI | ❌ | 15+ |
| AE2 | ❌ | 15.0.0–16.0.0 |
| Cell⁴ | ❌ | 1.0.0+ |

---

## 🌍 语言

English · 简体中文 · 日本語 · 한국어

---

## ⚙️ 配置

| 配置项 | 默认值 | 说明 |
|:---|:---:|:---|
| `enableCustomDisplayName` | `true` | 开启：显示「[物品] 黑卡」；关闭：仅显示「黑卡」 |

配置文件：`config/blackcard-common.toml`

---

## 📄 许可证

MIT
