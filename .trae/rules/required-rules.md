---
alwaysApply: true
description: 每次任务都必须遵循的核心规则索引，包含编码前置检查、本地目录管理、工具使用规范、技术栈、命名规范、API规范、任务分配、优先级、文档更新、技能触发、代码审查等必须使用规则。
---

# EcoCarbon-MRV 必须使用规则

> 本文件包含每次任务都必须遵循的核心规则索引。详细配置见 [required-rules-detail.md](required-rules-detail.md)。

## 一、编码前置检查（必须执行）

开始编码前，必须按顺序完成以下检查：

1. **技能检查**：调用 `find-skills` skill 检索适用技能
2. **架构了解**：调用 `ecocarbon-architecture` skill 了解项目架构和模块依赖
3. **数据结构**：调用 `ecocarbon-data-assets` skill 了解数据模型和表结构
4. **编码规范**：调用 `karpathy-guidelines` skill 避免常见编码错误
5. **查看技术限制文档**：开始编码前调用相关技能查阅项目技术限制文档

详见 [编码前置检查详细配置](required-rules-detail.md#一编码前置检查详细配置)

## 二、本地目录管理（必须了解）

以下目录通过 `.git/info/exclude` 排除（该文件不提交到仓库）：

| 目录 | 说明 | Git管理方式 |
|------|------|------------|
| `docs/` | 项目本地文档（架构、API、数据库设计等） | `.git/info/exclude`，不提交远程，本地 IDE/检索工具可正常访问 |
| `.trae/` | 规则、技能、智能体配置、任务持久化等 | `.git/info/exclude`，不提交远程，本地 IDE/检索工具可正常访问 |

详见 [本地目录管理详细配置](required-rules-detail.md#二本地目录管理详细配置)

## 三、工具使用规范（必须遵循）

根据场景选择正确的工具：

| 场景 | 推荐工具 | 原因 |
|------|----------|------|
| 检查目录/文件是否存在 | `LS` | 直接读取文件系统，不受 Git 排除规则影响 |
| 按文件名模式查找文件 | `Glob` | 遵循 `.gitignore` 规则，被排除的文件不会显示 |
| 搜索文件内容 | `Grep` | 遵循 `.gitignore` 规则 |
| 读取已知路径的文件 | `Read` | 直接读取，不受 Git 排除规则影响 |

**重要**：`docs/`、`.trae/`、`secrets/` 等通过 `.git/info/exclude` 排除的目录：
- `Glob` 和 `Grep` 无法找到这些目录中的文件
- `LS` 和 `Read` 可以正常访问这些目录中的文件
- 检查这些目录是否存在时，**必须使用 `LS` 工具**

详见 [工具使用规范详细配置](required-rules-detail.md#三工具使用规范详细配置)

## 四、技术栈（必须遵循）

详见 [技术栈详细配置](required-rules-detail.md#四技术栈详细配置)

## 五、命名规范（必须遵循）

详见 [命名规范详细配置](required-rules-detail.md#五命名规范详细配置)

## 六、API规范（必须遵循）

详见 [API规范详细配置](required-rules-detail.md#六api规范详细配置)

## 七、任务分配规则（必须执行）

详见 [任务分配规则详细配置](required-rules-detail.md#七任务分配规则详细配置)

## 八、优先级（必须执行）

详见 [优先级详细配置](required-rules-detail.md#八优先级详细配置)

## 九、文档更新原则（必须执行）

详见 [文档更新原则详细配置](required-rules-detail.md#九文档更新原则详细配置)

## 十、技能触发规则（必须执行）

详见 [技能触发规则详细配置](required-rules-detail.md#十技能触发规则详细配置)

## 十一、代码审查要点（必须执行）

详见 [代码审查要点详细配置](required-rules-detail.md#十一代码审查要点详细配置)