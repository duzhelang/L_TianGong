# EcoCarbon-MRV 项目规则

## 项目简介

EcoCarbon-MRV 是面向碳中和的多源环境智能监测与决策平台，以农业全场景碳汇计量与减排优化为首个垂直应用。

## 技术栈

- **后端**: Spring Boot 3.3.5 (Java 17) + Spring Data JPA + MySQL 8.x + Redis
- **前端**: Vue 3.5 + Vite 6.x + Axios
- **构建工具**: Maven (后端) + npm (前端)

## 命名规范

### 后端
- 包名: `com.ecocarbon.mrv`
- Controller: `{模块}Controller`
- Service: `{模块}Service`
- Repository: `{模块}Repository`
- Entity: `{实体名}`
- DTO: `{操作}{模块}Request/Response`

### 前端
- 组件: PascalCase (`CarbonChart.vue`)
- API函数: camelCase (`getProjects`)
- Store: camelCase (`useAuthStore`)

## API规范

- RESTful风格
- URL: `/api/{模块}/{资源}`
- 统一响应格式: `{ code, message, data }`

## 分支管理

- `main`: 生产分支
- `develop`: 开发分支
- `feature/*`: 功能分支
- `fix/*`: 修复分支

## 提交规范

```
<type>(<scope>): <subject>
```

type: feat/fix/docs/style/refactor/test/chore

## 代码审查要点

1. 是否符合分层架构规范
2. 是否有SQL注入风险
3. 是否有未处理的异常
4. 是否有硬编码的敏感信息
5. 是否有冗余代码
