---
name: "ecocarbon-git"
description: "EcoCarbon-MRV 项目的 Git 提交推送专用技能。当用户使用 /git 指令、要求提交代码、推送变更、查看 git 状态时触发。自动执行规范化的 git 工作流：状态检查 → 智能暂存 → 规范提交 → 远程推送。"
---

# ecocarbon-git — Git 提交推送工作流

## 触发条件

- 用户输入 `/git` 指令
- 用户要求"提交代码"、"推送变更"、"git 提交"、"commit"
- 用户要求查看 git 状态

## 提交规范

遵循 `<type>(<scope>): <subject>` 格式：

| type | 说明 | 示例 |
|------|------|------|
| `feat` | 新功能 | `feat(carbon): 新增碳汇计算引擎` |
| `fix` | Bug 修复 | `fix(auth): 修复 JWT 过期未刷新` |
| `docs` | 文档变更 | `docs(api): 更新接口文档` |
| `style` | 格式调整 | `style(frontend): 统一组件缩进` |
| `refactor` | 重构 | `refactor(engine): 拆分碳计算模块` |
| `test` | 测试 | `test(calc): 新增碳汇计算单元测试` |
| `chore` | 构建/工具 | `chore: 更新 .gitignore` |

### scope 取值范围

| scope | 模块 |
|-------|------|
| `backend` | 后端通用 |
| `frontend` | 前端通用 |
| `carbon` | 碳计算引擎 |
| `auth` | 认证授权 |
| `api` | API 接口 |
| `db` | 数据库 |
| `config` | 配置文件 |
| `ci` | CI/CD |
| `deps` | 依赖变更 |

## 标准工作流

执行此技能时，按以下步骤顺序执行：

### 第一步：状态检查

```powershell
git status
git log --oneline -5
```

- 查看当前分支、未暂存/未跟踪文件
- 查看最近 5 条提交记录，了解提交风格

### 第二步：差异分析

```powershell
git diff
git diff --cached
```

- 分析工作区和暂存区的变更内容
- 确定变更类型（feat/fix/refactor 等）
- 确定影响范围（scope）

### 第三步：智能暂存

根据变更内容选择性暂存，**禁止使用 `git add -A` 或 `git add .`**：

```powershell
# 按模块分别暂存
git add backend/src/...
git add frontend/src/...
git add .gitignore
```

**安全检查**：
- 确认不包含 `.env`、密钥文件、敏感配置
- 确认不包含 `docs/` 目录（由 .git/info/exclude 控制）
- 确认不包含 `target/`、`node_modules/`、`dist/` 等构建产物

### 第四步：规范提交

使用 HEREDOC 格式提交：

```powershell
git commit -m "$(cat <<'EOF'
<type>(<scope>): <subject>

<可选的详细描述>
EOF
)"
```

**提交消息要求**：
- subject 不超过 50 字符，使用中文
- 不以句号结尾
- 如需详细说明，空一行后写描述

### 第五步：远程推送

```powershell
# 首次推送（设置上游分支）
git push -u origin <branch>

# 后续推送
git push
```

## 分支策略

| 分支 | 用途 | 推送权限 |
|------|------|----------|
| `main` | 生产分支 | 禁止直接推送，必须通过 PR |
| `develop` | 开发分支 | 日常开发推送 |
| `feature/*` | 功能分支 | 从 develop 创建，完成后合并 |
| `fix/*` | 修复分支 | 从 main 或 develop 创建 |

## 特殊指令

### `/git` — 快速提交

完整执行上述五步工作流。

### `/git status` — 仅查看状态

只执行第一步，不提交。

### `/git log` — 查看提交历史

```powershell
git log --oneline -20
```

## .git/info/exclude 配置说明

本项目使用 `.git/info/exclude`（而非 `.gitignore`）排除以下内容：

| 排除项 | 原因 |
|--------|------|
| `docs/` | 文档系统仅本地使用，不提交远程 |
| `PROJECT_REPORT.md` | 项目报告，仅本地参考 |
| `database_init_guide.md` | 数据库指南，仅本地参考 |
| `平台规划.txt` | 规划文档，仅本地参考 |

**重要**：这些文件在本地完整保留，编辑器可正常检索，但不会被 git 跟踪。

## 安全红线

- **永远不要**提交 `.env`、密钥、Token 等敏感信息
- **永远不要**使用 `git push --force` 到 main/develop 分支
- **永远不要**在未检查 diff 的情况下提交
- **永远不要**使用 `git add -A` 或 `git add .`
