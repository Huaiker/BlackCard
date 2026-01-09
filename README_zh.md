# BlackCard
<<<<<<< HEAD
> 🌐 : [English](README.md)

[![Minecraft 1.20.1](https://img.shields.io/badge/Minecraft-1.20.1-green?logo=minecraft)](https://www.minecraft.net/)
[![Forge 47.4.13+](https://img.shields.io/badge/Forge-47.4.13%2B-orange)](https://files.minecraftforge.net/)
[![JEI Compatible](https://img.shields.io/badge/JEI-Compatible-blue)](https://github.com/mezz/JustEnoughItems)
![Languages](https://img.shields.io/badge/Languages-13-brightgreen)

## 黑卡物品配置说明

黑卡的行为由两个自定义 NBT 标签控制：

- `targetItem`：目标物品的命名空间 ID（例如 `"minecraft:diamond"`）。
- `targetCount`：合成的物品数量。**必须是介于 1 和目标物品最大堆叠数之间的整数（含端点）。**

> ⚠️ 如果 `targetCount` 小于 1 或超过该物品的最大堆叠数，黑卡将无法工作。
---
> - 轻量，无性能影响
> - 兼容 Forge 1.20.1

---

## 🌍 本地化（13 种语言）

✅ 完全支持以下语言：

| 语言 | 代码 |
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

> 💬 **想添加更多？** 欢迎贡献！参见 [如何翻译](#-help-translate)。

---

## 📦 安装

你有两种方式安装 BlackCard：

### 选项 1：下载预构建版本（推荐大多数玩家）

1. 安装 [Minecraft Forge for 1.20.1（版本 47.4.13 或更新）](https://files.minecraftforge.net/net/minecraftforge/forge/index_1.20.1.html)
2. 前往 [Releases 页面](https://github.com/yourname/blackcard/releases)
3. 下载最新的文件，名称类似 `blackcard-1.x.x.jar`
4. 将其放入你的 Minecraft `mods` 文件夹：
   - **Windows**: `%appdata%\.minecraft\mods\`
   - **macOS**: `～/Library/Application Support/minecraft/mods/`
   - **Linux**: `～/.minecraft/mods/`
5. 使用 Forge 配置文件启动 Minecraft

> ✅ 这是最简单的方式 —— 无需技术知识。

---

### 选项 2：从源码构建（适用于开发者或自定义版本）

如果你想修改代码或自行构建模组：

1. **安装 Java 17**  
   如果尚未安装，请下载并安装 [Java 17 (JDK)](https://adoptium.net/)。

2. **下载源代码**  
   点击本 GitHub 页面上的绿色 **"Code"** 按钮，然后选择 **"Download ZIP"**，或使用 Git 克隆仓库。

3. **打开项目文件夹**  
   （如果使用了 ZIP）解压后，在任意文件管理器中打开该文件夹。

4. **运行构建脚本**  
   - **Windows**: 双击名为 `gradlew.bat` 的文件  
   - **macOS / Linux**: 在文件夹中打开终端并运行 `./gradlew build`

5. **找到构建好的模组文件**  
   构建完成后，进入项目内的 `build/libs/` 文件夹。  
   你会看到一个类似 `blackcard-1.x.x.jar` 的文件。

6. **将其复制到你的 `mods` 文件夹**（路径同选项 1）

7. 使用 Forge 启动 Minecraft —— 你的自定义构建版本将会被加载！

---
