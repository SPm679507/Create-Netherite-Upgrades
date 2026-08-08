# Create Netherite

[Create Netherite](https://github.com/von-Neumann101/Create-Netherite-Upgrades) 是一个面向 Minecraft 1.20.1 Forge 的 [Create](https://github.com/Creators-of-Create/Create) 附属模组，为流体运输、储存和蒸汽动力提供下界合金强化版本。

## 功能

### 强力机械泵

- 注册 ID：`createnetherite:powerful_mechanical_pump`
- 保留 Create 机械泵的放置、旋转、流向和管网连接行为。
- 默认基础应力消耗为 `16 SU/RPM`。
- 默认压力倍率为 `8x`，用于提高 Create 流体管网的实际吞吐量。
- 压力倍率和应力消耗均可通过服务端配置调整。

### 下界合金流体储罐

- 注册 ID：`createnetherite:netherite_fluid_tank`
- 保留 Create 流体储罐的窗口、流体交互、比较器和多方块行为。
- 默认单方块容量是普通流体储罐的 `16x`，倍率可配置。
- 支持在竖直放置时自动补齐多方块储罐的一整层。

### 下界合金蒸汽引擎与下界合金锅炉

- 注册 ID：`createnetherite:netherite_steam_engine`
- 下界合金流体储罐与下界合金蒸汽引擎可以组成下界合金锅炉。
- 锅炉沿用 Create 的尺寸、热源、效率和 `1–18` 级等级规则。
- 蒸汽引擎基础应力容量为 `2048 SU/RPM`，是 Create 原版蒸汽引擎的 `2x`。
- 每个锅炉等级需要 `12 mB/t` 水，原版锅炉为 `10 mB/t`。
- 下界合金锅炉不能混用普通流体储罐或普通蒸汽引擎。

### 下界合金板

- 注册 ID：`createnetherite:netherite_sheet`
- 使用 Create 动力冲压机将下界合金锭压制成下界合金板。
- 用于合成模组中的下界合金机械。

## 配方

| 产物 | 材料 | 数量 |
| --- | --- | ---: |
| 下界合金板 | 1 个下界合金锭，经动力冲压 | 1 |
| 强力机械泵 | 3 个机械泵 + 2 个下界合金板 | 3 |
| 下界合金流体储罐 | 3 个流体储罐 + 2 个下界合金板 | 3 |
| 下界合金蒸汽引擎 | 3 个蒸汽引擎 + 2 个下界合金板 | 3 |

## 环境要求

- Minecraft `1.20.1`
- Forge `47.1.x`（开发版本为 `47.1.33`）
- Create `6.0.8+`
- Java `17`

当前 `main` 分支构建不兼容 NeoForge 1.21.1。

## 安装

1. 安装 Minecraft 1.20.1 Forge 与 Create 6.0.8 或更高兼容版本。
2. 将本模组 JAR 放入游戏或服务端的 `mods` 目录。
3. 启动游戏。服务端与客户端都需要安装本模组。

## 配置

服务端配置文件位于世界目录：

```text
serverconfig/createnetherite-server.toml
```

| 配置项 | 默认值 | 作用 |
| --- | ---: | --- |
| `powerfulPumpStressImpact` | `16.0` | 强力机械泵的基础应力消耗 |
| `powerfulPumpPressureMultiplier` | `8.0` | 强力机械泵的压力倍率 |
| `netheriteFluidTankCapacityMultiplier` | `16` | 下界合金储罐相对普通储罐的容量倍率 |

修改配置后请重启世界或服务器，确保已有动能网络、流体管网和储罐重新计算。

## 开发构建

Windows：

```powershell
.\gradlew.bat build
```

Linux 或 macOS：

```bash
./gradlew build
```

构建产物位于 `build/libs/`。

## 许可证

本项目使用 [MIT License](LICENSE)。
