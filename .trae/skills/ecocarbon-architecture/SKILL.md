---
name: "ecocarbon-architecture"
description: "EcoCarbon-MRV 项目的系统架构文档路径索引（Spring Boot 3.3.5 + Vue 3.5 + MySQL 8.x）。涵盖项目结构、API、数据库、前端、业务逻辑、开发规范、指南等全部文档位置。当询问架构、功能、API、规范、技术细节时调用以定位对应文档。"
---

# ecocarbon-architecture — 文档路径索引

## 检测到的技术栈

- **后端**: Spring Boot 3.3.5 (Java 17)
- **前端**: Vue 3.5 + Vite 6.x
- **数据库**: MySQL 8.x
- **ORM**: Spring Data JPA
- **缓存**: Redis

## docs/ 目录总览

| 分类 | 目录 | 文档数 |
|------|------|--------|
| 架构概览 | [architecture/](../../docs/architecture/) | 3 个文件 |
| API文档 | [api/](../../docs/api/) | 3 个文件 |
| 数据库 | [database/](../../docs/database/) | 2 个文件 |
| 前端 | [frontend/](../../docs/frontend/) | 0 个文件 |
| 算法层 | [algorithm/](../../docs/algorithm/) | 0 个文件 |
| 业务功能 | [business/](../../docs/business/) | 1 个文件 |
| 开发规范 | [standards/](../../docs/standards/) | 3 个文件 |
| 指南 | [guides/](../../docs/guides/) | 3 个文件 |

## 文档路径索引

### 架构文档

| 文档 | 路径 | 内容 |
|------|------|------|
| 项目概览 | [architecture/overview.md](../../docs/architecture/overview.md) | 项目概览、技术栈、目录结构 |
| 后端模块 | [architecture/backend-modules.md](../../docs/architecture/backend-modules.md) | 后端分层架构、模块划分 |
| 前端概览 | [architecture/frontend-overview.md](../../docs/architecture/frontend-overview.md) | 前端入口、配置、页面组件清单 |

### API 文档

| 文档 | 路径 | 内容 |
|------|------|------|
| 端点清单 | [api/endpoints.md](../../docs/api/endpoints.md) | Spring Boot REST API 端点列表 |
| 认证 | [api/auth.md](../../docs/api/auth.md) | 认证授权机制（JWT） |
| 响应格式 | [api/unified-response.md](../../docs/api/unified-response.md) | 统一响应结构定义 |

### 数据库文档

| 文档 | 路径 | 内容 |
|------|------|------|
| 表结构 | [database/schema.md](../../docs/database/schema.md) | 表清单、字段说明、实体映射关系 |
| 初始化 | [database/initialization.md](../../docs/database/initialization.md) | 数据库初始化步骤、建表说明 |

### 业务功能文档

| 文档 | 路径 | 内容 |
|------|------|------|
| 数据流转 | [business/data-flow.md](../../docs/business/data-flow.md) | 核心数据流转说明 |

### 开发规范

| 文档 | 路径 | 内容 |
|------|------|------|
| 通用规范 | [standards/开发规范.md](../../docs/standards/开发规范.md) | 通用编码规范 |
| 前端规范 | [standards/前端开发规范.md](../../docs/standards/前端开发规范.md) | 前端编码规范 |
| 后端规范 | [standards/后端开发规范.md](../../docs/standards/后端开发规范.md) | 后端编码规范 |

### 指南文档

| 文档 | 路径 | 内容 |
|------|------|------|
| 启动指南 | [guides/startup.md](../../docs/guides/startup.md) | 项目启动步骤 |
| 配置说明 | [guides/configuration.md](../../docs/guides/configuration.md) | application.yml 关键配置项 |
| 依赖清单 | [guides/dependencies.md](../../docs/guides/dependencies.md) | 所有依赖及版本 |

> 数据资产相关文档索引见 [ecocarbon-data-assets](../ecocarbon-data-assets/SKILL.md) 技能
