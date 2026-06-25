<template>
  <div class="chat-input">
    <div class="quick-tags">
      <el-tag
        v-for="tag in quickQuestions"
        :key="tag"
        class="quick-tag"
        effect="plain"
        @click="$emit('send', tag)"
      >
        {{ tag }}
      </el-tag>
    </div>
    <div class="input-area">
      <el-input
        v-model="inputText"
        type="textarea"
        :rows="2"
        placeholder="输入您的问题，Shift+Enter换行"
        resize="none"
        @keydown="handleKeydown"
      />
      <el-button
        type="primary"
        :icon="Promotion"
        :disabled="!inputText.trim()"
        @click="handleSend"
      >
        发送
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Promotion } from '@element-plus/icons-vue'

defineProps({
  quickQuestions: {
    type: Array,
    default: () => [
      '如何降低碳排放？',
      '分析本月碳排放数据',
      '生成MRV报告',
      '碳汇项目评估建议'
    ]
  }
})

const emit = defineEmits(['send'])

const inputText = ref('')

const handleKeydown = (e) => {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault()
    handleSend()
  }
}

const handleSend = () => {
  const text = inputText.value.trim()
  if (text) {
    emit('send', text)
    inputText.value = ''
  }
}
</script>

<style scoped>
.chat-input {
  border-top: 1px solid #e4e7ed;
  padding: 16px;
  background-color: #fff;
}

.quick-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
}

.quick-tag {
  cursor: pointer;
  transition: all 0.2s;
}

.quick-tag:hover {
  background-color: #ecf5ff;
  border-color: #409eff;
  color: #409eff;
}

.input-area {
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.input-area :deep(.el-textarea__inner) {
  border-radius: 8px;
  padding: 8px 12px;
}

.input-area .el-button {
  height: 40px;
  border-radius: 8px;
  padding: 0 20px;
}
</style>
