<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { VideoPlay, Calendar, Search, Refresh, Download, Star, View, ChatDotRound, Share } from '@element-plus/icons-vue'

const loading = ref(false)
const activeCategory = ref('all')
const searchKeyword = ref('')

const categories = ref([
  { label: '全部', value: 'all' },
  { label: '环保宣传', value: 'environmental' },
  { label: '培训视频', value: 'training' },
  { label: '纪录片', value: 'documentary' },
  { label: '案例分享', value: 'case-study' }
])

const shortDramas = ref([
  {
    id: 1,
    title: '碳中和之路',
    category: 'documentary',
    duration: '15:30',
    views: 1250,
    likes: 89,
    comments: 23,
    cover: 'https://picsum.photos/300/200?random=1',
    videoUrl: 'https://www.w3schools.com/html/mov_bbb.mp4',
    description: '探索中国碳中和目标的实现路径，从政策到实践的全面解读。',
    tags: ['碳中和', '政策解读', '可持续发展']
  },
  {
    id: 2,
    title: '农业碳汇监测指南',
    category: 'training',
    duration: '22:15',
    views: 890,
    likes: 67,
    comments: 15,
    cover: 'https://picsum.photos/300/200?random=2',
    videoUrl: 'https://www.w3schools.com/html/movie.mp4',
    description: '详细介绍农业碳汇监测的技术方法和操作流程。',
    tags: ['农业碳汇', '监测技术', '操作指南']
  },
  {
    id: 3,
    title: '绿色能源革命',
    category: 'environmental',
    duration: '18:45',
    views: 2100,
    likes: 156,
    comments: 42,
    cover: 'https://picsum.photos/300/200?random=3',
    videoUrl: 'https://www.w3schools.com/html/mov_bbb.mp4',
    description: '展示可再生能源技术如何推动能源结构转型。',
    tags: ['可再生能源', '绿色能源', '技术革新']
  },
  {
    id: 4,
    title: '碳交易市场解析',
    category: 'case-study',
    duration: '25:10',
    views: 1560,
    likes: 112,
    comments: 38,
    cover: 'https://picsum.photos/300/200?random=4',
    videoUrl: 'https://www.w3schools.com/html/movie.mp4',
    description: '深入分析国内外碳交易市场的运作机制和发展趋势。',
    tags: ['碳交易', '市场机制', '投资分析']
  },
  {
    id: 5,
    title: '生态修复案例',
    category: 'case-study',
    duration: '20:30',
    views: 980,
    likes: 78,
    comments: 21,
    cover: 'https://picsum.photos/300/200?random=5',
    videoUrl: 'https://www.w3schools.com/html/mov_bbb.mp4',
    description: '展示成功的生态修复项目案例和成效评估。',
    tags: ['生态修复', '环境保护', '成功案例']
  },
  {
    id: 6,
    title: '低碳生活指南',
    category: 'environmental',
    duration: '12:20',
    views: 3200,
    likes: 245,
    comments: 67,
    cover: 'https://picsum.photos/300/200?random=6',
    videoUrl: 'https://www.w3schools.com/html/movie.mp4',
    description: '日常生活中的低碳实践技巧和环保小贴士。',
    tags: ['低碳生活', '环保技巧', '日常实践']
  }
])

const currentVideo = ref(null)
const showVideoDialog = ref(false)

const filteredDramas = computed(() => {
  let dramas = shortDramas.value
  
  if (activeCategory.value !== 'all') {
    dramas = dramas.filter(d => d.category === activeCategory.value)
  }
  
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    dramas = dramas.filter(d => 
      d.title.toLowerCase().includes(keyword) ||
      d.description.toLowerCase().includes(keyword) ||
      d.tags.some(tag => tag.toLowerCase().includes(keyword))
    )
  }
  
  return dramas
})

function getCategoryLabel(category) {
  const labels = {
    environmental: '环保宣传',
    training: '培训视频',
    documentary: '纪录片',
    'case-study': '案例分享'
  }
  return labels[category] || category
}

function getCategoryColor(category) {
  const colors = {
    environmental: '#67C23A',
    training: '#409EFF',
    documentary: '#E6A23C',
    'case-study': '#F56C6C'
  }
  return colors[category] || '#909399'
}

function formatViews(views) {
  if (views >= 10000) {
    return (views / 10000).toFixed(1) + '万'
  }
  return views.toString()
}

function playVideo(drama) {
  currentVideo.value = drama
  showVideoDialog.value = true
}

function closeVideo() {
  showVideoDialog.value = false
  currentVideo.value = null
}

function handleCategoryChange(category) {
  activeCategory.value = category
}

function handleSearch() {
  // 搜索逻辑已在computed中处理
}

function handleRefresh() {
  loading.value = true
  setTimeout(() => {
    loading.value = false
  }, 1000)
}

function handleExport() {
  console.log('导出短剧数据')
}

onMounted(() => {
  // 初始化加载数据
})
</script>

<template>
  <div class="short-drama" v-loading="loading">
    <div class="page-header">
      <div class="header-left">
        <h2>短剧中心</h2>
        <p>环保宣传、培训教育、案例分享视频资源</p>
      </div>
      <div class="header-actions">
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
        <el-button :icon="Download" @click="handleExport">导出</el-button>
      </div>
    </div>

    <div class="content-section">
      <!-- 搜索和筛选区域 -->
      <div class="filter-section">
        <div class="search-box">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索短剧标题、描述或标签"
            :prefix-icon="Search"
            clearable
            @keyup.enter="handleSearch"
          />
        </div>
        <div class="category-tags">
          <el-tag
            v-for="category in categories"
            :key="category.value"
            :type="activeCategory === category.value ? '' : 'info'"
            :effect="activeCategory === category.value ? 'dark' : 'plain'"
            @click="handleCategoryChange(category.value)"
            class="category-tag"
          >
            {{ category.label }}
          </el-tag>
        </div>
      </div>

      <!-- 短剧列表 -->
      <div class="drama-grid">
        <el-row :gutter="20">
          <el-col 
            v-for="drama in filteredDramas" 
            :key="drama.id" 
            :xs="24" 
            :sm="12" 
            :md="8" 
            :lg="6"
          >
            <el-card 
              class="drama-card" 
              shadow="hover" 
              @click="playVideo(drama)"
            >
              <div class="card-cover">
                <img :src="drama.cover" :alt="drama.title" class="cover-image">
                <div class="duration-badge">{{ drama.duration }}</div>
                <div class="play-overlay">
                  <el-icon :size="48" color="#fff">
                    <VideoPlay />
                  </el-icon>
                </div>
              </div>
              
              <div class="card-content">
                <h3 class="drama-title">{{ drama.title }}</h3>
                <el-tag 
                  :color="getCategoryColor(drama.category)" 
                  size="small" 
                  effect="dark"
                  class="category-badge"
                >
                  {{ getCategoryLabel(drama.category) }}
                </el-tag>
                <p class="drama-description">{{ drama.description }}</p>
                
                <div class="drama-stats">
                  <span class="stat-item">
                    <el-icon><View /></el-icon>
                    {{ formatViews(drama.views) }}
                  </span>
                  <span class="stat-item">
                    <el-icon><Star /></el-icon>
                    {{ drama.likes }}
                  </span>
                  <span class="stat-item">
                    <el-icon><ChatDotRound /></el-icon>
                    {{ drama.comments }}
                  </span>
                </div>
                
                <div class="drama-tags">
                  <el-tag 
                    v-for="tag in drama.tags.slice(0, 2)" 
                    :key="tag" 
                    size="small" 
                    type="info"
                    effect="plain"
                    class="tag-item"
                  >
                    {{ tag }}
                  </el-tag>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 空状态 -->
      <div v-if="filteredDramas.length === 0" class="empty-state">
        <el-empty description="暂无匹配的短剧内容">
          <el-button type="primary" @click="activeCategory = 'all'; searchKeyword = ''">
            查看全部短剧
          </el-button>
        </el-empty>
      </div>
    </div>

    <!-- 视频播放对话框 -->
    <el-dialog
      v-model="showVideoDialog"
      :title="currentVideo?.title"
      width="80%"
      top="5vh"
      @close="closeVideo"
    >
      <div class="video-player-container" v-if="currentVideo">
        <video 
          ref="videoPlayer"
          :src="currentVideo.videoUrl"
          controls
          autoplay
          class="video-player"
        >
          您的浏览器不支持视频播放
        </video>
        
        <div class="video-info">
          <div class="video-meta">
            <el-tag 
              :color="getCategoryColor(currentVideo.category)" 
              size="small" 
              effect="dark"
            >
              {{ getCategoryLabel(currentVideo.category) }}
            </el-tag>
            <span class="video-duration">
              <el-icon><Calendar /></el-icon>
              {{ currentVideo.duration }}
            </span>
          </div>
          
          <p class="video-description">{{ currentVideo.description }}</p>
          
          <div class="video-stats">
            <span class="stat-item">
              <el-icon><View /></el-icon>
              {{ formatViews(currentVideo.views) }} 次观看
            </span>
            <span class="stat-item">
              <el-icon><Star /></el-icon>
              {{ currentVideo.likes }} 点赞
            </span>
            <span class="stat-item">
              <el-icon><ChatDotRound /></el-icon>
              {{ currentVideo.comments }} 评论
            </span>
            <span class="stat-item">
              <el-icon><Share /></el-icon>
              分享
            </span>
          </div>
          
          <div class="video-tags">
            <el-tag 
              v-for="tag in currentVideo.tags" 
              :key="tag" 
              size="small" 
              type="info"
              effect="plain"
            >
              {{ tag }}
            </el-tag>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<style scoped>
.short-drama {
  padding: 20px;
  height: 100%;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #ebeef5;
}

.header-left h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  color: #303133;
}

.header-left p {
  margin: 0;
  font-size: 14px;
  color: #909399;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.content-section {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
}

.filter-section {
  margin-bottom: 24px;
}

.search-box {
  margin-bottom: 16px;
  max-width: 400px;
}

.category-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.category-tag {
  cursor: pointer;
  transition: all 0.3s;
}

.category-tag:hover {
  transform: translateY(-2px);
}

.drama-grid {
  margin-top: 20px;
}

.drama-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.3s;
  border-radius: 8px;
  overflow: hidden;
}

.drama-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.1);
}

.card-cover {
  position: relative;
  height: 160px;
  overflow: hidden;
}

.cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.drama-card:hover .cover-image {
  transform: scale(1.05);
}

.duration-badge {
  position: absolute;
  bottom: 8px;
  right: 8px;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 12px;
}

.play-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.drama-card:hover .play-overlay {
  opacity: 1;
}

.card-content {
  padding: 16px;
}

.drama-title {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #303133;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.category-badge {
  margin-bottom: 8px;
  border: none;
}

.drama-description {
  margin: 0 0 12px 0;
  font-size: 13px;
  color: #606266;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.drama-stats {
  display: flex;
  gap: 16px;
  margin-bottom: 12px;
  font-size: 12px;
  color: #909399;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.drama-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.tag-item {
  margin: 0;
}

.video-player-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.video-player {
  width: 100%;
  max-height: 60vh;
  background: #000;
  border-radius: 8px;
}

.video-info {
  padding: 0 8px;
}

.video-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.video-duration {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  color: #606266;
}

.video-description {
  margin: 0 0 16px 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}

.video-stats {
  display: flex;
  gap: 24px;
  margin-bottom: 16px;
  font-size: 14px;
  color: #606266;
}

.video-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.empty-state {
  padding: 60px 0;
  text-align: center;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
  
  .header-actions {
    width: 100%;
    justify-content: flex-end;
  }
  
  .video-player-container {
    gap: 12px;
  }
  
  .video-stats {
    flex-wrap: wrap;
    gap: 12px;
  }
}
</style>