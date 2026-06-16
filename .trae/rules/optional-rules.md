---
alwaysApply: false
description: 按需调用的详细规则，包含分支管理、提交规范、智能体角色、协作流程、文档变更映射表、技能触发示例、强制触发速查、任务大纲格式等。
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

详见 [技能触发规则详细配置](required-rules-detail.md#十技能触发规则详细配置)

## 七、强制触发速查（按需调用）

### 场景-动作映射表

| 场景 | 必须触发 |
|------|----------|
| 多步骤/复杂需求/长任务 | `analysis-planner` 需求分析 → **任务大纲**（高层次设计） → `writing-plans` 详细计划 |
| 存在书面执行计划 | `executing-plans` 或 `subagent-driven-development` |
| 3+ 独立任务 | `dispatching-parallel-agents` |
| 代码变更涉及文档映射表中的类型 | 文档自动更新流程（参考 task-persistence-doc-sync.md） |
| 任务创建/更新/归档 | 任务持久化流程（参考 task-persistence-doc-sync.md） |
| 数据库结构变更 | `doc-manager` + `ecocarbon-data-assets` 更新 |
| 性能优化 | 基准测试 + 代码审查 |
| API 接口变更 | `doc-manager` + `ecocarbon-architecture` 更新 |

### 使用说明

1. **识别场景**：根据当前任务特征，匹配上表中的场景
2. **执行触发**：按照"必须触发"列中的流程执行
3. **记录决策**：记录触发原因和执行结果，供后续复盘

## 八、任务大纲格式（按需调用）

### 高层次设计模板

```markdown
# 任务大纲：[任务标题]

## 需求概述
[用户需求的简要描述]

## 技术方案概要
- 涉及模块：[前端/后端/数据库/算法]
- 核心设计：[关键技术决策]
- 影响范围：[受影响的功能和文件]

## 子任务划分
1. [子任务1] - 粗粒度描述
2. [子任务2] - 粗粒度描述
3. [子任务3] - 粗粒度描述

## 依赖关系
[子任务之间的依赖顺序]

## 风险评估
[潜在风险和应对策略]
```

### 使用说明

1. **适用场景**：复杂任务、多步骤任务、涉及多模块的任务
2. **创建时机**：在需求分析完成后，开始详细计划前
3. **审批流程**：任务大纲完成后，需用户确认后再进入详细计划阶段

## 九、命令执行规范（按需调用）

### 沙箱环境命令执行规范

详见 [sandbox-workflow.md](docs/guides/sandbox-workflow.md)。

每次执行 `RunCommand` 前，先查阅该文档。

### 常用命令示例

```bash
# 后端构建
mvn clean package

# 前端构建
npm run build

# 启动后端服务
mvn spring-boot:run

# 启动前端开发服务器
npm run dev
```