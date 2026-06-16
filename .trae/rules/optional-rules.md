---
alwaysApply: false
description: 按需调用的详细规则，包含分支管理、提交规范、智能体角色、协作流程、文档变更映射表、技能触发示例等。
---

# EcoCarbon-MRV 按需调用规则

> 本文件包含按需调用的详细规则。必须使用规则见 required-rules.md。

## 一、分支管理（按需调用）

- `main`: 生产分支
- `develop`: 开发分支
- `feature/*`: 功能分支
- `fix/*`: 修复分支

## 二、提交规范（按需调用）

```
<type>(<scope>): <subject>
```

type: feat/fix/docs/style/refactor/test/chore

## 三、智能体角色（按需调用）

| 角色 | 职责 | 触发条件 |
|------|------|----------|
| 调度器 | 任务分析、分配、协调 | 所有任务 |
| 分析规划师 | 需求分析、任务拆解 | 复杂任务 |
| 开发智能体 | 按计划编写代码 | 代码编写 |
| 审查智能体 | 代码质量检查 | 代码审查 |
| 文档智能体 | 文档同步更新 | 文档变更 |

## 四、协作流程（按需调用）

```
用户需求 → 调度器分析 → 简单任务直接执行
                      → 复杂任务 → 分析规划 → 开发 → 审查 → 文档 → 交付
```

## 五、文档变更映射表（按需调用）

| 变更类型 | 需要更新的文档 |
|----------|----------------|
| 新增/修改API | api/endpoints.md |
| 数据库表结构变更 | database/schema.md, database/initialization.md |
| 新增/修改实体类 | database/schema.md, architecture/backend-modules.md |
| 前端路由变更 | architecture/frontend-overview.md |
| 前端组件变更 | architecture/frontend-overview.md |
| 配置文件变更 | guides/configuration.md |
| 依赖变更 | guides/dependencies.md |
| 业务逻辑变更 | business/data-flow.md |
| 开发规范变更 | standards/开发规范.md, standards/前端开发规范.md, standards/后端开发规范.md |

## 六、技能触发示例（按需调用）

详见 [技能触发规则详细配置](required-rules-detail.md#七技能触发规则详细配置)