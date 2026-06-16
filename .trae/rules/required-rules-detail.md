---
alwaysApply: false
description: 必须使用规则的详细配置，包含技术栈、命名规范、API规范、任务分配、优先级、文档更新、技能触发、代码审查等详细说明。
---
# EcoCarbon-MRV 必须使用规则详细配置

> 本文件包含必须使用规则的详细配置。索引见 required-rules.md。

## 一、技术栈详细配置

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

## 二、命名规范详细配置

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

## 三、API规范详细配置

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

## 四、任务分配规则详细配置

### 直接执行条件（无需调度）
1. 单文件修改（< 30行）
2. 查询、搜索、解释
3. 简单配置变更

### 调度执行条件
1. 新功能开发
2. 多文件联动修改（3+文件）
3. 数据库结构变更
4. 安全相关变更

## 五、优先级详细配置

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

## 六、文档更新原则详细配置

### 及时性
代码变更后立即检查并更新文档，避免文档滞后。

### 准确性
文档内容必须与代码实现一致，不得有误导性描述。

### 完整性
变更涉及的文档应全部检查，不遗漏相关文档。

### 最小化
只更新受影响的文档，不做无关修改。

## 七、技能触发规则详细配置

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

## 八、代码审查要点详细配置

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