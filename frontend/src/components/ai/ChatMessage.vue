<template>
  <div class="chat-message" :class="{ 'is-user': isUser }">
    <div class="message-avatar">
      <el-avatar :size="36" :style="{ backgroundColor: isUser ? '#409eff' : '#67c23a' }">
        <el-icon v-if="isUser"><User /></el-icon>
        <el-icon v-else><Service /></el-icon>
      </el-avatar>
    </div>
    <div class="message-content">
      <div class="message-bubble" :class="{ 'user-bubble': isUser, 'ai-bubble': !isUser }">
        <div v-if="isUser" class="message-text">{{ message.content }}</div>
        <div v-else class="message-text markdown-body" v-html="renderMarkdown(message.content)"></div>
      </div>
      <div class="message-time">{{ formatTime(message.timestamp) }}</div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { User, Service } from '@element-plus/icons-vue'

const props = defineProps({
  message: {
    type: Object,
    required: true
  }
})

const isUser = computed(() => props.message.role === 'user')

const formatTime = (timestamp) => {
  if (!timestamp) return ''
  const date = new Date(timestamp)
  const hours = date.getHours().toString().padStart(2, '0')
  const minutes = date.getMinutes().toString().padStart(2, '0')
  return `${hours}:${minutes}`
}

const renderMarkdown = (content) => {
  if (!content) return ''
  let html = content
    .replace(/^### (.*$)/gm, '<h3>$1</h3>')
    .replace(/^## (.*$)/gm, '<h2>$1</h2>')
    .replace(/^# (.*$)/gm, '<h1>$1</h1>')
    .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
    .replace(/\*(.*?)\*/g, '<em>$1</em>')
    .replace(/`(.*?)`/g, '<code>$1</code>')
    .replace(/^- (.*$)/gm, '<li>$1</li>')
    .replace(/(<li>.*<\/li>)/s, '<ul>$1</ul>')
    .replace(/\n\n/g, '</p><p>')
    .replace(/\n/g, '<br>')
  return `<p>${html}</p>`
}
</script>

<style scoped>
.chat-message {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  padding: 0 16px;
}

.chat-message.is-user {
  flex-direction: row-reverse;
}

.message-avatar {
  flex-shrink: 0;
}

.message-content {
  max-width: 70%;
  display: flex;
  flex-direction: column;
}

.is-user .message-content {
  align-items: flex-end;
}

.message-bubble {
  padding: 12px 16px;
  border-radius: 12px;
  line-height: 1.6;
  word-break: break-word;
}

.ai-bubble {
  background-color: #f4f4f5;
  color: #303133;
  border-top-left-radius: 4px;
}

.user-bubble {
  background-color: #409eff;
  color: #ffffff;
  border-top-right-radius: 4px;
}

.message-time {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
  padding: 0 4px;
}

.markdown-body :deep(h1) {
  font-size: 1.5em;
  margin: 0.5em 0;
  font-weight: 600;
}

.markdown-body :deep(h2) {
  font-size: 1.3em;
  margin: 0.5em 0;
  font-weight: 600;
}

.markdown-body :deep(h3) {
  font-size: 1.1em;
  margin: 0.5em 0;
  font-weight: 600;
}

.markdown-body :deep(ul) {
  margin: 0.5em 0;
  padding-left: 1.5em;
}

.markdown-body :deep(li) {
  margin: 0.25em 0;
}

.markdown-body :deep(code) {
  background-color: #e6e8eb;
  padding: 2px 6px;
  border-radius: 4px;
  font-family: monospace;
  font-size: 0.9em;
}

.markdown-body :deep(strong) {
  font-weight: 600;
}

.markdown-body :deep(p) {
  margin: 0.5em 0;
}
</style>
