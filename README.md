# BlackCard

> 🌐 : [中文版](README_zh.md)

[![Minecraft 1.20.1](https://img.shields.io/badge/Minecraft-1.20.1-green?logo=minecraft)](https://www.minecraft.net/)
[![Forge 47.4.13+](https://img.shields.io/badge/Forge-47.4.13%2B-orange)](https://files.minecraftforge.net/)
[![JEI Compatible](https://img.shields.io/badge/JEI-Compatible-blue)](https://github.com/mezz/JustEnoughItems)
![Languages](https://img.shields.io/badge/Languages-13-brightgreen)

A universal item voucher mod — Black Cards can craft any item! Supports three types: **Item Black Card**, **Tag Black Card**, and **Mod Black Card**.

---

## 🃏 Card Types

### 1. Item Black Card (`black_card`)

Specifies a single exact item to craft. You set the target item ID and count directly.

**NBT Fields:**

| Field | Type | Description |
|-------|------|-------------|
| `targetItem` | String (TAG_STRING) | The namespaced ID of the target item, e.g. `"minecraft:diamond"` |
| `targetCount` | Integer (TAG_INT) | The number of items to craft. Must be between 1 and the item's max stack size |

**Example Command — Give an Item Black Card for 32 Diamonds:**
```
/give @p blackcard:black_card{targetItem:"minecraft:diamond",targetCount:32}
```

---

### 2. Tag Black Card (`tag_black_card`)

Crafts any item that belongs to a specified tag. Use **Shift + Scroll Wheel** to cycle through all items in the tag.

**NBT Fields:**

| Field | Type | Description |
|-------|------|-------------|
| `targetTag` | String (TAG_STRING) | The namespaced tag ID, e.g. `"minecraft:logs"` |
| `targetItem` | String (TAG_STRING) | Auto-synced — the currently selected item from the tag |
| `targetCount` | Integer (TAG_INT) | The number of items to craft. Must be between 1 and the item's max stack size |
| `currentItemIndex` | Integer (TAG_INT) | The current selection index within the tag (0-based). Changed via Shift+Scroll |

> `targetItem` is automatically synchronized from `targetTag` and `currentItemIndex`. You only need to set `targetTag` and `targetCount` manually.

**Example Command — Give a Tag Black Card for `minecraft:logs` tag (64 items):**
```
/give @p blackcard:tag_black_card{targetTag:"minecraft:logs",targetCount:64,currentItemIndex:0}
```

---

### 3. Mod Black Card (`mod_black_card`)

Crafts any item that belongs to a specified mod (by modid). Use **Shift + Scroll Wheel** to cycle through all items from that mod.

**NBT Fields:**

| Field | Type | Description |
|-------|------|-------------|
| `targetMod` | String (TAG_STRING) | The mod ID (namespace), e.g. `"minecraft"`, `"blackcard"`, `"create"` |
| `targetItem` | String (TAG_STRING) | Auto-synced — the currently selected item from the mod |
| `targetCount` | Integer (TAG_INT) | The number of items to craft. Must be between 1 and the item's max stack size |
| `currentItemIndex` | Integer (TAG_INT) | The current selection index within the mod (0-based). Changed via Shift+Scroll |

> `targetItem` is automatically synchronized from `targetMod` and `currentItemIndex`. You only need to set `targetMod` and `targetCount` manually.

**Example Command — Give a Mod Black Card for the `create` mod (1 item):**
```
/give @p blackcard:mod_black_card{targetMod:"create",targetCount:1,currentItemIndex:0}
```

---

## 📋 NBT Fields Summary

| Field | Card Types | Required | Auto-synced | Description |
|-------|-----------|----------|-------------|-------------|
| `targetItem` | Item / Tag / Mod | Item: ✅ Tag/Mod: ❌ | Tag/Mod: ✅ | The namespaced item ID to craft |
| `targetCount` | Item / Tag / Mod | ✅ | ❌ | Number of items to craft (1 ~ max stack size) |
| `targetTag` | Tag only | ✅ | ❌ | The tag ID to select items from |
| `targetMod` | Mod only | ✅ | ❌ | The mod ID to select items from |
| `currentItemIndex` | Tag / Mod | ❌ (default 0) | ❌ | Current selection index (0-based) |

> ⚠️ If `targetCount` is less than 1 or exceeds the item's max stack size, the Black Card will not function.

---

## 🔧 How It Works

1. **Place** a Black Card alone in any crafting grid
2. The recipe system reads the NBT data and outputs the specified item
3. The Black Card itself is **not consumed** — it acts as a reusable voucher
4. For Tag/Mod Black Cards, use **Shift + Scroll Wheel** while holding the card to cycle through available items

---

## 🌍 Localization (13 Languages)

✅ Fully supports the following languages:

| Language | Code |
|--------|------|
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

> 💬 **Want to add more?** Contributions welcome!

---

## 📦 Installation

### Option 1: Download Pre-built Release (Recommended)

1. Install [Minecraft Forge for 1.20.1 (version 47.4.13 or newer)](https://files.minecraftforge.net/net/minecraftforge/forge/index_1.20.1.html)
2. Go to the [Releases page](https://github.com/yourname/blackcard/releases)
3. Download the latest file named like `blackcard-1.x.x.jar`
4. Place it into your Minecraft `mods` folder:
   - **Windows**: `%appdata%\.minecraft\mods\`
   - **macOS**: `~/Library/Application Support/minecraft/mods/`
   - **Linux**: `~/.minecraft/mods/`
5. Launch Minecraft using the Forge profile

---

### Option 2: Build from Source

1. **Install Java 17** — Download from [Adoptium](https://adoptium.net/)
2. **Download the source code** — Clone or download ZIP from GitHub
3. **Build** — Run `./gradlew build` (macOS/Linux) or `gradlew.bat` (Windows)
4. **Find the jar** — Located in `build/libs/blackcard-1.x.x.jar`
5. **Copy to `mods` folder** and launch with Forge

---

## ⚙️ Configuration

| Config Key | Default | Description |
|-----------|---------|-------------|
| `enableCustomDisplayName` | `true` | When enabled, the card name shows as "[Item] BlackCard" (e.g. "Diamond BlackCard"). When disabled, it just shows "Black Card". |

Config file location: `config/blackcard-common.toml`

---

## 📄 License

This project is licensed under the MIT License.
