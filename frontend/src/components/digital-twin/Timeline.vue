<script setup>
import {
  VideoPlay,
  VideoPause
} from '@element-plus/icons-vue'

const props = defineProps({
  currentTime: {
    type: Date,
    required: true
  },
  timeRange: {
    type: Array,
    required: true
  },
  isPlaying: {
    type: Boolean,
    default: false
  },
  playbackSpeed: {
    type: Number,
    default: 1
  }
})

const emit = defineEmits(['update:currentTime', 'toggle-playback', 'change-speed'])

// 时间格式化
function formatTime(date) {
  return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

function formatDate(date) {
  return date.toLocaleDateString('zh-CN')
}

// 播放控制
function togglePlayback() {
  emit('toggle-playback')
}

// 速度控制
function changeSpeed(speed) {
  emit('change-speed', speed)
}

// 时间滑块更新
function onTimeChange(value) {
  emit('update:currentTime', new Date(value))
}
</script>

<template>
  <div class="timeline">
    <div class="timeline-controls">
      <el-button-group size="small">
        <el-button @click="togglePlayback" :type="isPlaying ? 'warning' : 'primary'">
          <el-icon>{{ isPlaying ? VideoPause : VideoPlay }}</el-icon>
          {{ isPlaying ? '暂停' : '播放' }}
        </el-button>
        <el-button @click="changeSpeed(0.5)" :type="playbackSpeed === 0.5 ? 'primary' : ''">0.5x</el-button>
        <el-button @click="changeSpeed(1)" :type="playbackSpeed === 1 ? 'primary' : ''">1x</el-button>
        <el-button @click="changeSpeed(2)" :type="playbackSpeed === 2 ? 'primary' : ''">2x</el-button>
      </el-button-group>
    </div>
    <div class="timeline-slider">
      <el-slider
        :model-value="currentTime.getTime()"
        :min="timeRange[0].getTime()"
        :max="timeRange[1].getTime()"
        :format-tooltip="formatDate"
        :step="3600000"
        @input="onTimeChange"
      />
    </div>
    <div class="timeline-info">
      <span class="time-display">{{ formatDate(currentTime) }} {{ formatTime(currentTime) }}</span>
    </div>
  </div>
</template>

<style scoped>
.timeline {
  display: flex;
  align-items: center;
  padding: 12px 20px;
  background: rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(10px);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  gap: 20px;
}

.timeline-controls {
  flex-shrink: 0;
}

.timeline-slider {
  flex: 1;
}

.timeline-info {
  flex-shrink: 0;
  min-width: 150px;
  text-align: right;
}

.time-display {
  font-size: 14px;
  color: #E6A23C;
  font-weight: 500;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .timeline {
    flex-direction: column;
    gap: 12px;
  }
  
  .timeline-info {
    text-align: center;
  }
}
</style>