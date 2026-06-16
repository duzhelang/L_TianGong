---
alwaysApply: false
description: 必须使用规则的详细配置，包含编码前置检查、本地目录管理、工具使用规范、技术栈、命名规范、API规范、任务分配、优先级、文档更新、技能触发、代码审查等详细说明。
---
# EcoCarbon-MRV 必须使用规则详细配置

> 本文件包含必须使用规则的详细配置。索引见 required-rules.md。

## 一、编码前置检查详细配置

### 检查流程

开始编码前，必须按顺序完成以下检查：

1. **技能检查**：调用 `find-skills` skill 检索适用技能
   - 目的：确认是否有现成的技能可以辅助完成任务
   - 示例：用户询问"如何优化React性能" → 调用 `npx skills find react performance`

2. **架构了解**：调用 `ecocarbon-architecture` skill 了解项目架构和模块依赖
   - 目的：理解项目整体结构，避免修改影响其他模块
   - 示例：用户要求"添加新的API端点" → 先了解后端模块划分

3. **数据结构**：调用 `ecocarbon-data-assets` skill 了解数据模型和表结构
   - 目的：理解数据流转，确保数据操作正确
   - 示例：用户要求"修改用户表" → 先了解现有表结构和关联关系

4. **编码规范**：调用 `karpathy-guidelines` skill 避免常见编码错误
   - 目的：遵循最佳实践，减少代码质量问题
   - 示例：任何编码任务前都应了解避免过度工程化、保持代码简洁等原则

5. **查看技术限制文档**：开始编码前调用相关技能查阅项目技术限制文档
   - 目的：了解项目特定的技术约束和限制
   - 示例：查看数据库连接限制、API调用频率限制等

### 检查原则

- **必须按顺序执行**：每个检查都有其特定目的，跳过可能导致问题
- **简单任务可简化**：对于单文件修改等简单任务，可跳过部分检查
- **复杂任务必须完整执行**：涉及多模块、数据库变更等复杂任务，必须完成所有检查

## 二、本地目录管理详细配置

### 目录结构

| 目录 | 说明 | Git管理方式 |
|------|------|------------|
| `docs/` | 项目本地文档（架构、API、数据库设计等） | `.git/info/exclude`，不提交远程，本地 IDE/检索工具可正常访问 |
| `.trae/` | 规则、技能、智能体配置、任务持久化等 | `.git/info/exclude`，不提交远程，本地 IDE/检索工具可正常访问 |

### Git排除配置

这些目录通过 `.git/info/exclude` 文件排除，该文件不提交到仓库。新开发者需参照项目配置文档的设置指引执行。

### 访问方式

- **Glob 和 Grep**：无法找到这些目录中的文件（遵循 `.gitignore` 规则）
- **LS 和 Read**：可以正常访问这些目录中的文件（直接读取文件系统）
- **检查目录是否存在时**：必须使用 `LS` 工具

### 文档目录结构

`docs/` 目录包含以下子目录：

| 分类 | 目录 | 文档数 |
|------|------|--------|
| 架构概览 | architecture/ | 3 个文件 |
| API文档 | api/ | 3 个文件 |
| 数据库 | database/ | 2 个文件 |
| 前端 | frontend/ | 0 个文件 |
| 算法层 | algorithm/ | 0 个文件 |
| 业务功能 | business/ | 1 个文件 |
| 开发规范 | standards/ | 3 个文件 |
| 指南 | guides/ | 3 个文件 |

## 三、工具使用规范详细配置

### 文件系统工具选择

| 场景 | 推荐工具 | 原因 |
|------|----------|------|
| 检查目录/文件是否存在 | `LS` | 直接读取文件系统，不受 Git 排除规则影响 |
| 按文件名模式查找文件 | `Glob` | 遵循 `.gitignore` 规则，被排除的文件不会显示 |
| 搜索文件内容 | `Grep` | 遵循 `.gitignore` 规则 |
| 读取已知路径的文件 | `Read` | 直接读取，不受 Git 排除规则影响 |

### 重要说明

`docs/`、`.trae/`、`secrets/` 等通过 `.git/info/exclude` 排除的目录：
- `Glob` 和 `Grep` 无法找到这些目录中的文件
- `LS` 和 `Read` 可以正常访问这些目录中的文件
- 检查这些目录是否存在时，**必须使用 `LS` 工具**

### 工具使用示例

#### 检查目录是否存在
```bash
# 正确：使用LS工具
LS path="d:\SOLO\L_TianGong\docs"

# 错误：使用Glob工具（可能找不到.git/info/exclude排除的目录）
Glob pattern="docs/*"
```

#### 查找文件
```bash
# 查找Java文件（遵循.gitignore规则）
Glob pattern="**/*.java"

# 查找docs目录下的文件（需要使用LS）
LS path="d:\SOLO\L_TianGong\docs"
```

#### 搜索文件内容
```bash
# 搜索Controller类（遵循.gitignore规则）
Grep pattern="class.*Controller"

# 搜索docs目录下的内容（需要使用LS+Read）
LS path="d:\SOLO\L_TianGong\docs"
# 然后使用Read读取具体文件
```

## 四、技术栈详细配置

### 后端技术栈
- Spring Boot 3.3.5 (Java 17)
- Spring Data JPA
- MySQL 8.x
- Redis

### 前端技术栈
- Vue 3.5
- Vite 6.x
- Axios

### 构建工具
- 后端: Maven
- 前端: npm

## 五、命名规范详细配置

### 后端命名规范
- 包名: `com.ecocarbon.mrv`
- Controller: `{模块}Controller`（如 `ProjectController`）
- Service: `{模块}Service`（如 `ProjectService`）
- Repository: `{模块}Repository`（如 `ProjectRepository`）
- Entity: `{实体名}`（如 `Project`）
- DTO: `{操作}{模块}Request/Response`（如 `CreateProjectRequest`、`ProjectResponse`）

### 前端命名规范
- 组件: PascalCase（如 `CarbonChart.vue`）
- API函数: camelCase（如 `getProjects`）
- Store: camelCase（如 `useAuthStore`）

## 六、API规范详细配置

### RESTful风格
- 使用标准HTTP方法：GET、POST、PUT、DELETE
- URL格式: `/api/{模块}/{资源}`（如 `/api/projects`、`/api/projects/{id}`）

### 统一响应格式
```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

## 七、任务分配规则详细配置

### 直接执行条件（无需调度）
1. 单文件修改（< 30行）
2. 查询、搜索、解释
3. 简单配置变更

### 调度执行条件
1. 新功能开发
2. 多文件联动修改（3+文件）
3. 数据库结构变更
4. 安全相关变更

## 八、优先级详细配置

### P0 紧急
- 线上故障
- 安全漏洞

### P1 高
- 阻塞其他开发的功能

### P2 中
- 常规功能开发

### P3 低
- 代码清理
- 文档完善

## 九、文档更新原则详细配置

### 及时性
代码变更后立即检查并更新文档，避免文档滞后。

### 准确性
文档内容必须与代码实现一致，不得有误导性描述。

### 完整性
变更涉及的文档应全部检查，不遗漏相关文档。

### 最小化
只更新受影响的文档，不做无关修改。

## 十、技能触发规则详细配置

### ecocarbon-architecture
- 触发条件: 询问架构、功能、API、规范、技术细节
- 用途: 定位系统架构文档
- 示例: "系统架构是什么？"、"API有哪些端点？"、"后端模块怎么划分？"、"开发规范是什么？"

### ecocarbon-data-assets
- 触发条件: 询问数据、模型、训练、数据库内容
- 用途: 定位数据资产文档
- 示例: "数据库有哪些表？"、"数据怎么流转？"、"排放因子存在哪里？"、"训练数据在哪里？"

### ecocarbon-git
- 触发条件: `/git` 指令、提交代码、推送变更、查看 git 状态
- 用途: Git 提交推送工作流
- 示例: "/git"、"提交代码"、"推送变更"、"git 状态"、"commit"

### 技能调用方式
当用户询问相关问题时，使用 Skill 工具调用对应技能获取文档路径索引。

## 十一、代码审查要点详细配置

### 1. 分层架构规范
检查代码是否符合Controller → Service → Repository的分层架构。

### 2. SQL注入风险
检查是否有直接拼接SQL的情况，确保使用参数化查询。

### 3. 未处理的异常
检查是否有try-catch块，确保异常被适当处理。

### 4. 硬编码的敏感信息
检查是否有硬编码的密码、密钥等敏感信息。

### 5. 冗余代码
检查是否有未使用的变量、函数或重复代码。