<template>
  <div class="ai-assistant">
    <div class="sidebar" :class="{ 'is-collapsed': sidebarCollapsed }">
      <div class="sidebar-header">
        <el-button type="primary" :icon="Plus" @click="createNewChat">
          新建对话
        </el-button>
        <el-button :icon="sidebarCollapsed ? Expand : Fold" @click="sidebarCollapsed = !sidebarCollapsed" />
      </div>
      <div class="chat-history">
        <div v-for="(group, category) in chatGroups" :key="category" class="history-group">
          <div class="group-title" @click="toggleGroup(category)">
            <el-icon><ArrowRight /></el-icon>
            <span>{{ category }}</span>
          </div>
          <div v-show="expandedGroups[category]" class="group-items">
            <div
              v-for="chat in group"
              :key="chat.id"
              class="history-item"
              :class="{ active: currentChatId === chat.id }"
              @click="switchChat(chat.id)"
            >
              <el-icon><ChatDotRound /></el-icon>
              <span class="item-title">{{ chat.title }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="main-content">
      <div class="chat-header">
        <h2>{{ currentChatTitle }}</h2>
        <div class="header-actions">
          <el-button :icon="InfoFilled" @click="infoPanelVisible = !infoPanelVisible" />
          <el-button :icon="Download" @click="exportChat">导出</el-button>
        </div>
      </div>

      <div class="chat-messages" ref="messagesContainer">
        <ChatMessage
          v-for="msg in currentMessages"
          :key="msg.id"
          :message="msg"
        />
        <div v-if="isLoading" class="loading-message">
          <div class="loading-avatar">
            <el-avatar :size="36" style="background-color: #67c23a">
              <el-icon><Service /></el-icon>
            </el-avatar>
          </div>
          <div class="loading-bubble">
            <span class="loading-dot"></span>
            <span class="loading-dot"></span>
            <span class="loading-dot"></span>
          </div>
        </div>
      </div>

      <ChatInput @send="handleSendMessage" />
    </div>

    <div v-show="infoPanelVisible" class="info-panel">
      <div class="panel-header">
        <h3>对话信息</h3>
        <el-button :icon="Close" @click="infoPanelVisible = false" />
      </div>
      <div class="panel-content">
        <div class="summary-card">
          <h4>对话摘要</h4>
          <p>{{ currentChatSummary }}</p>
        </div>
        <div class="data-cards">
          <h4>相关数据</h4>
          <el-card v-for="card in dataCards" :key="card.title" class="data-card">
            <template #header>
              <div class="card-header">
                <span>{{ card.title }}</span>
                <el-tag :type="card.tagType">{{ card.tag }}</el-tag>
              </div>
            </template>
            <div class="card-value">{{ card.value }}</div>
            <div class="card-desc">{{ card.desc }}</div>
          </el-card>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, nextTick, onMounted } from 'vue'
import {
  Plus,
  Expand,
  Fold,
  ArrowRight,
  ChatDotRound,
  InfoFilled,
  Download,
  Close,
  Service
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import ChatMessage from '@/components/ai/ChatMessage.vue'
import ChatInput from '@/components/ai/ChatInput.vue'

const sidebarCollapsed = ref(false)
const infoPanelVisible = ref(true)
const currentChatId = ref(1)
const messagesContainer = ref(null)
const isLoading = ref(false)

const expandedGroups = reactive({
  '碳核算咨询': true,
  '减排建议': true,
  '数据分析': false,
  '报告生成': false
})

const chatGroups = reactive({
  '碳核算咨询': [
    { id: 1, title: '农业碳排放降低方案' },
    { id: 2, title: '工业碳排放核算方法' }
  ],
  '减排建议': [
    { id: 3, title: '能源结构优化建议' },
    { id: 4, title: '交通领域减排措施' }
  ],
  '数据分析': [
    { id: 5, title: '月度碳排放趋势分析' }
  ],
  '报告生成': [
    { id: 6, title: '2024年Q1 MRV报告' }
  ]
})

const allMessages = reactive({
  1: [
    {
      id: 1,
      role: 'assistant',
      content: '您好！我是EcoCarbon AI助手，可以帮助您进行碳核算、减排分析和报告生成。请问有什么可以帮您的？',
      timestamp: '2024-01-15 10:00:00'
    },
    {
      id: 2,
      role: 'user',
      content: '如何降低农业碳排放？',
      timestamp: '2024-01-15 10:01:00'
    },
    {
      id: 3,
      role: 'assistant',
      content: '## 农业碳排放降低建议\n\n农业碳排放主要来源于以下几个方面：\n\n### 1. 化肥使用\n- 采用精准施肥技术，减少化肥用量20-30%\n- 使用有机肥替代部分化肥\n- 推广缓释肥料\n\n### 2. 稻田甲烷排放\n- 采用间歇灌溉技术\n- 种植低甲烷排放水稻品种\n- 稻田养鱼/鸭综合种养\n\n### 3. 农机械\n- 使用电动或氢能农机\n- 优化农机作业路线\n- 推广保护性耕作\n\n### 4. 秸秆处理\n- 秸秆还田增加土壤碳汇\n- 秸秆发电或制沼气\n- 避免露天焚烧\n\n需要我详细分析某个具体措施的减排潜力吗？',
      timestamp: '2024-01-15 10:01:30'
    }
  ]
})

const currentMessages = computed(() => allMessages[currentChatId.value] || [])

const currentChatTitle = computed(() => {
  for (const group of Object.values(chatGroups)) {
    const chat = group.find(c => c.id === currentChatId.value)
    if (chat) return chat.title
  }
  return '新对话'
})

const currentChatSummary = computed(() => {
  const messages = currentMessages.value
  if (messages.length === 0) return '暂无对话内容'
  const lastAiMessage = [...messages].reverse().find(m => m.role === 'assistant')
  if (lastAiMessage) {
    return lastAiMessage.content.substring(0, 100) + '...'
  }
  return '对话进行中...'
})

const dataCards = [
  {
    title: '本月碳排放',
    value: '1,234 tCO2e',
    tag: '↓ 12%',
    tagType: 'success',
    desc: '较上月减少168吨'
  },
  {
    title: '减排目标完成度',
    value: '78%',
    tag: '进行中',
    tagType: 'warning',
    desc: '年度目标：2,000 tCO2e'
  },
  {
    title: '碳汇项目收益',
    value: '¥45,600',
    tag: '↑ 8%',
    tagType: 'success',
    desc: '累计收益：¥234,500'
  }
]

const toggleGroup = (category) => {
  expandedGroups[category] = !expandedGroups[category]
}

const switchChat = (chatId) => {
  currentChatId.value = chatId
  scrollToBottom()
}

const createNewChat = () => {
  const newId = Date.now()
  if (!allMessages[newId]) {
    allMessages[newId] = [
      {
        id: 1,
        role: 'assistant',
        content: '您好！我是EcoCarbon AI助手，可以帮助您进行碳核算、减排分析和报告生成。请问有什么可以帮您的？',
        timestamp: new Date().toISOString()
      }
    ]
  }
  currentChatId.value = newId
  chatGroups['碳核算咨询'].unshift({ id: newId, title: '新对话' })
  scrollToBottom()
}

const handleSendMessage = async (text) => {
  const messages = allMessages[currentChatId.value]
  if (!messages) return

  messages.push({
    id: messages.length + 1,
    role: 'user',
    content: text,
    timestamp: new Date().toISOString()
  })

  scrollToBottom()
  isLoading.value = true

  setTimeout(() => {
    const aiResponses = {
      '如何降低碳排放？': '## 碳排放降低综合方案\n\n降低碳排放需要从多个维度入手：\n\n### 能源结构优化\n- 提高可再生能源占比\n- 推广分布式光伏\n- 发展储能技术\n\n### 工业减排\n- 设备能效提升\n- 余热回收利用\n- 碳捕集技术应用\n\n### 交通领域\n- 新能源汽车推广\n- 公共交通优先\n- 绿色物流发展\n\n需要针对某个领域深入分析吗？',
      '分析本月碳排放数据': '## 本月碳排放数据分析\n\n### 总体情况\n- 本月碳排放：1,234 tCO2e\n- 较上月：↓ 12%\n- 较去年同期：↓ 8%\n\n### 排放源分布\n1. 能源活动：68%\n2. 工业生产：22%\n3. 农业活动：7%\n4. 废弃物处理：3%\n\n### 主要减排措施效果\n- 光伏发电：减排约150 tCO2e\n- 节能改造：减排约80 tCO2e\n- 绿色采购：减排约30 tCO2e\n\n建议继续加大光伏发电和节能改造投入。',
      '生成MRV报告': '## MRV报告生成\n\n正在为您准备MRV报告框架：\n\n### 报告结构\n1. **项目概述**\n   - 项目背景\n   - 核算边界\n   - 报告期\n\n2. **数据监测**\n   - 活动水平数据\n   - 排放因子\n   - 监测方法\n\n3. **核算结果**\n   - 总排放量\n   - 减排量\n   - 净减排量\n\n4. **验证声明**\n   - 第三方验证\n   - 合规性说明\n\n需要我生成完整的报告模板吗？',
      '碳汇项目评估建议': '## 碳汇项目评估建议\n\n### 评估维度\n\n#### 1. 生态效益\n- 碳汇增量计算\n- 生物多样性影响\n- 水土保持效果\n\n#### 2. 经济可行性\n- 项目投资成本\n- 碳信用收益\n- 投资回收期\n\n#### 3. 社会效益\n- 就业带动\n- 社区参与\n- 示范效应\n\n#### 4. 风险评估\n- 自然风险\n- 政策风险\n- 市场风险\n\n建议优先评估生态效益和经济可行性，需要详细的评估模板吗？'
    }

    const defaultResponse = '感谢您的提问！作为EcoCarbon AI助手，我可以帮您：\n\n- 分析碳排放数据\n- 提供减排建议\n- 生成MRV报告\n- 评估碳汇项目\n\n请告诉我您具体需要哪方面的帮助？'

    messages.push({
      id: messages.length + 1,
      role: 'assistant',
      content: aiResponses[text] || defaultResponse,
      timestamp: new Date().toISOString()
    })

    isLoading.value = false
    scrollToBottom()
  }, 1500)
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

const exportChat = () => {
  const messages = currentMessages.value
  if (messages.length === 0) {
    ElMessage.warning('当前对话为空')
    return
  }

  let content = `对话标题：${currentChatTitle.value}\n`
  content += `导出时间：${new Date().toLocaleString()}\n\n`
  content += '='.repeat(50) + '\n\n'

  messages.forEach(msg => {
    const role = msg.role === 'user' ? '用户' : 'AI助手'
    const time = new Date(msg.timestamp).toLocaleString()
    content += `[${time}] ${role}:\n${msg.content}\n\n`
  })

  const blob = new Blob([content], { type: 'text/plain;charset=utf-8' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `对话记录_${currentChatTitle.value}_${new Date().toISOString().split('T')[0]}.txt`
  a.click()
  URL.revokeObjectURL(url)
  ElMessage.success('导出成功')
}

onMounted(() => {
  scrollToBottom()
})
</script>

<style scoped>
.ai-assistant {
  display: flex;
  height: 100%;
  background-color: #f5f7fa;
  overflow: hidden;
}

.sidebar {
  width: 240px;
  background-color: #fff;
  border-right: 1px solid #e4e7ed;
  display: flex;
  flex-direction: column;
  transition: width 0.3s;
  flex-shrink: 0;
}

.sidebar.is-collapsed {
  width: 60px;
}

.sidebar-header {
  padding: 16px;
  display: flex;
  gap: 8px;
  border-bottom: 1px solid #e4e7ed;
}

.sidebar-header .el-button:first-child {
  flex: 1;
}

.sidebar.is-collapsed .sidebar-header .el-button:first-child {
  display: none;
}

.chat-history {
  flex: 1;
  overflow-y: auto;
  padding: 8px 0;
}

.history-group {
  margin-bottom: 4px;
}

.group-title {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  cursor: pointer;
  color: #909399;
  font-size: 12px;
  transition: color 0.2s;
}

.group-title:hover {
  color: #409eff;
}

.group-title .el-icon {
  transition: transform 0.2s;
}

.history-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px 10px 32px;
  cursor: pointer;
  transition: all 0.2s;
}

.history-item:hover {
  background-color: #f5f7fa;
}

.history-item.active {
  background-color: #ecf5ff;
  color: #409eff;
}

.item-title {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 14px;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.chat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 24px;
  background-color: #fff;
  border-bottom: 1px solid #e4e7ed;
}

.chat-header h2 {
  margin: 0;
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 24px 0;
  background-color: #f5f7fa;
}

.loading-message {
  display: flex;
  gap: 12px;
  padding: 0 16px;
  margin-bottom: 20px;
}

.loading-bubble {
  background-color: #f4f4f5;
  padding: 12px 20px;
  border-radius: 12px;
  border-top-left-radius: 4px;
  display: flex;
  gap: 6px;
  align-items: center;
}

.loading-dot {
  width: 8px;
  height: 8px;
  background-color: #909399;
  border-radius: 50%;
  animation: loading-bounce 1.4s infinite ease-in-out both;
}

.loading-dot:nth-child(1) {
  animation-delay: -0.32s;
}

.loading-dot:nth-child(2) {
  animation-delay: -0.16s;
}

@keyframes loading-bounce {
  0%, 80%, 100% {
    transform: scale(0);
  }
  40% {
    transform: scale(1);
  }
}

.info-panel {
  width: 300px;
  background-color: #fff;
  border-left: 1px solid #e4e7ed;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  border-bottom: 1px solid #e4e7ed;
}

.panel-header h3 {
  margin: 0;
  font-size: 14px;
  font-weight: 500;
}

.panel-content {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

.summary-card {
  background-color: #f5f7fa;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
}

.summary-card h4 {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: #606266;
}

.summary-card p {
  margin: 0;
  font-size: 13px;
  color: #909399;
  line-height: 1.6;
}

.data-cards h4 {
  margin: 0 0 12px 0;
  font-size: 14px;
  color: #606266;
}

.data-card {
  margin-bottom: 12px;
}

.data-card :deep(.el-card__header) {
  padding: 12px 16px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
}

.card-value {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.card-desc {
  font-size: 12px;
  color: #909399;
}

@media (max-width: 768px) {
  .sidebar {
    display: none;
  }

  .info-panel {
    display: none;
  }
}
</style>
