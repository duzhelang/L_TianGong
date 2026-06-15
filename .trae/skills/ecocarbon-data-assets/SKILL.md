---
name: "ecocarbon-data-assets"
description: "EcoCarbon-MRV 项目的数据资产文档路径索引（MySQL 8.x + Redis）。涵盖数据文件、数据库表、模型存储、训练数据、数据流转等全部文档位置。当询问数据、模型、训练、数据库内容时调用以定位对应文档。"
---

# ecocarbon-data-assets — 文档路径索引

## 检测到的数据相关技术栈

- **数据库**: MySQL 8.x
- **ORM框架**: Spring Data JPA
- **缓存**: Redis
- **数据文件目录**: data/（待建设）

## docs/ 数据相关目录总览

| 分类 | 目录 | 文档数 |
|------|------|--------|
| 数据资产 | [data-assets/](../../docs/data-assets/) | 3 个文件 |
| 数据库 | [database/](../../docs/database/) | 2 个文件 |

## 文档路径索引

### 数据资产文档

| 文档 | 路径 | 内容 |
|------|------|------|
| 数据目录总览 | [data-assets/overview.md](../../docs/data-assets/overview.md) | 运行时数据目录结构 |
| 模型文件 | [data-assets/model-files.md](../../docs/data-assets/model-files.md) | 碳核算模型文件存储 |
| 训练数据 | [data-assets/training-data.md](../../docs/data-assets/training-data.md) | 训练数据集位置与格式 |

### 数据库文档

| 文档 | 路径 | 内容 |
|------|------|------|
| 表结构 | [database/schema.md](../../docs/database/schema.md) | 数据库表结构说明 |
| 初始化 | [database/initialization.md](../../docs/database/initialization.md) | 数据库初始化步骤 |

### 业务数据流转

| 文档 | 路径 | 内容 |
|------|------|------|
| 数据流转 | [business/data-flow.md](../../docs/business/data-flow.md) | 数据在系统中的完整流转路径 |

> 系统整体架构文档索引见 [ecocarbon-architecture](../ecocarbon-architecture/SKILL.md) 技能
