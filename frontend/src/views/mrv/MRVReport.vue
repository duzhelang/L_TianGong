<script setup>
import { ref, reactive, computed, onMounted } from "vue";
import { useRoute } from "vue-router";
import { useCarbonStore } from "@/stores/carbon";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  Document,
  Download,
  Printer,
  View,
  Setting,
  Check,
  Loading,
} from "@element-plus/icons-vue";

const route = useRoute();
const carbonStore = useCarbonStore();

const activeStep = ref(0);
const loading = ref(false);
const generating = ref(false);
const previewVisible = ref(false);
const selectedProject = ref(null);

const reportConfig = reactive({
  template: "standard",
  projectId: route.query.projectId || "",
  reportYear: 2024,
  reportType: "annual",
  includeVerification: true,
  includeRecommendations: true,
  includeCharts: true,
  language: "zh-CN",
  format: "pdf",
});

const templates = ref([
  {
    id: "standard",
    name: "标准MRV报告",
    description: "包含完整的监测、报告和核查内容",
    pages: 15,
    icon: Document,
  },
  {
    id: "summary",
    name: "摘要报告",
    description: "简化的碳排放汇总报告",
    pages: 5,
    icon: Document,
  },
  {
    id: "verification",
    name: "核查报告",
    description: "第三方核查专用报告格式",
    pages: 20,
    icon: Document,
  },
  {
    id: "custom",
    name: "自定义报告",
    description: "可自由配置报告内容和格式",
    pages: 10,
    icon: Setting,
  },
]);

const reportYears = computed(() => {
  const currentYear = new Date().getFullYear();
  return Array.from({ length: 5 }, (_, i) => currentYear - i);
});

const reportTypes = [
  { label: "年度报告", value: "annual" },
  { label: "季度报告", value: "quarterly" },
  { label: "月度报告", value: "monthly" },
  { label: "项目报告", value: "project" },
];

const formatOptions = [
  { label: "PDF", value: "pdf" },
  { label: "Word", value: "docx" },
  { label: "HTML", value: "html" },
];

const steps = [
  { title: "选择模板", description: "报告模板" },
  { title: "配置参数", description: "报告设置" },
  { title: "预览确认", description: "检查报告" },
  { title: "生成导出", description: "下载报告" },
];

const reportPreview = ref({
  title: "2024年度碳排放MRV报告",
  sections: [
    { name: "执行摘要", page: 1 },
    { name: "项目概述", page: 3 },
    { name: "监测方法", page: 5 },
    { name: "数据收集", page: 7 },
    { name: "排放核算", page: 9 },
    { name: "核查结果", page: 11 },
    { name: "减排建议", page: 13 },
    { name: "附录", page: 15 },
  ],
  summary: {
    totalEmissions: 12450,
    scope1: 8200,
    scope2: 3100,
    scope3: 1150,
    reduction: 5.2,
  },
});

const generatedReport = ref(null);

function nextStep() {
  if (activeStep.value < steps.length - 1) {
    activeStep.value++;
  }
}

function prevStep() {
  if (activeStep.value > 0) {
    activeStep.value--;
  }
}

function selectTemplate(templateId) {
  reportConfig.template = templateId;
}

function getTemplateIcon(template) {
  return template.icon;
}

async function handlePreview() {
  previewVisible.value = true;
}

async function handleGenerate() {
  generating.value = true;
  try {
    await new Promise((resolve) => setTimeout(resolve, 2000));

    generatedReport.value = {
      id: Date.now(),
      title: `${reportConfig.reportYear}年度碳排放MRV报告`,
      template: reportConfig.template,
      format: reportConfig.format,
      generatedAt: new Date().toISOString(),
      size: "2.4 MB",
      pages: templates.value.find((t) => t.id === reportConfig.template)?.pages || 10,
    };

    activeStep.value = 3;
    ElMessage.success("报告生成成功");
  } catch (error) {
    ElMessage.error("报告生成失败");
  } finally {
    generating.value = false;
  }
}

async function handleDownload() {
  ElMessage.success("报告下载已开始");
}

async function handlePrint() {
  ElMessage.info("正在准备打印...");
}

function handleReset() {
  activeStep.value = 0;
  generatedReport.value = null;
  Object.assign(reportConfig, {
    template: "standard",
    projectId: route.query.projectId || "",
    reportYear: 2024,
    reportType: "annual",
    includeVerification: true,
    includeRecommendations: true,
    includeCharts: true,
    language: "zh-CN",
    format: "pdf",
  });
}

onMounted(async () => {
  if (reportConfig.projectId) {
    await carbonStore.fetchProjectDetail(reportConfig.projectId);
    selectedProject.value = carbonStore.currentProject;
  }
});
</script>

<template>
  <div class="mrv-report">
    <div class="page-header">
      <div>
        <h2>MRV报告生成</h2>
        <p>监测、报告和核查报告生成工具</p>
      </div>
      <el-button @click="handleReset">重置</el-button>
    </div>

    <div class="report-content">
      <div class="steps-section">
        <el-steps :active="activeStep" direction="vertical" finish-status="success">
          <el-step
            v-for="(step, index) in steps"
            :key="index"
            :title="step.title"
            :description="step.description"
          />
        </el-steps>
      </div>

      <div class="form-section">
        <el-card v-if="activeStep === 0" class="step-card">
          <template #header>
            <div class="card-header">
              <span>选择报告模板</span>
              <el-tag type="info">步骤 1/4</el-tag>
            </div>
          </template>

          <div class="template-grid">
            <div
              v-for="template in templates"
              :key="template.id"
              class="template-card"
              :class="{ selected: reportConfig.template === template.id }"
              @click="selectTemplate(template.id)"
            >
              <div class="template-icon">
                <el-icon :size="32"><component :is="getTemplateIcon(template)" /></el-icon>
              </div>
              <div class="template-info">
                <h4>{{ template.name }}</h4>
                <p>{{ template.description }}</p>
                <div class="template-meta">
                  <span>约 {{ template.pages }} 页</span>
                </div>
              </div>
              <div v-if="reportConfig.template === template.id" class="template-check">
                <el-icon><Check /></el-icon>
              </div>
            </div>
          </div>
        </el-card>

        <el-card v-if="activeStep === 1" class="step-card">
          <template #header>
            <div class="card-header">
              <span>配置报告参数</span>
              <el-tag type="info">步骤 2/4</el-tag>
            </div>
          </template>

          <el-form :model="reportConfig" label-width="120px">
            <el-form-item label="报告年度" required>
              <el-select v-model="reportConfig.reportYear" style="width: 100%">
                <el-option
                  v-for="year in reportYears"
                  :key="year"
                  :label="`${year}年`"
                  :value="year"
                />
              </el-select>
            </el-form-item>

            <el-form-item label="报告类型">
              <el-select v-model="reportConfig.reportType" style="width: 100%">
                <el-option
                  v-for="item in reportTypes"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>

            <el-form-item label="输出格式">
              <el-select v-model="reportConfig.format" style="width: 100%">
                <el-option
                  v-for="item in formatOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>

            <el-form-item label="语言">
              <el-select v-model="reportConfig.language" style="width: 100%">
                <el-option label="中文" value="zh-CN" />
                <el-option label="English" value="en-US" />
              </el-select>
            </el-form-item>

            <el-divider />

            <el-form-item label="包含内容">
              <el-checkbox v-model="reportConfig.includeVerification">
                包含核查结果
              </el-checkbox>
              <el-checkbox v-model="reportConfig.includeRecommendations">
                包含减排建议
              </el-checkbox>
              <el-checkbox v-model="reportConfig.includeCharts">
                包含图表
              </el-checkbox>
            </el-form-item>

            <el-form-item v-if="selectedProject" label="关联项目">
              <el-tag>{{ selectedProject.name }}</el-tag>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card v-if="activeStep === 2" class="step-card">
          <template #header>
            <div class="card-header">
              <span>预览确认</span>
              <el-tag type="info">步骤 3/4</el-tag>
            </div>
          </template>

          <div class="preview-section">
            <div class="preview-header">
              <h3>{{ reportPreview.title }}</h3>
              <div class="preview-meta">
                <span>模板: {{ templates.find((t) => t.id === reportConfig.template)?.name }}</span>
                <span>格式: {{ reportConfig.format.toUpperCase() }}</span>
              </div>
            </div>

            <el-divider />

            <div class="preview-summary">
              <h4>报告摘要</h4>
              <el-row :gutter="20">
                <el-col :span="6">
                  <el-statistic title="总排放量" :value="reportPreview.summary.totalEmissions" suffix="tCO2e" />
                </el-col>
                <el-col :span="6">
                  <el-statistic title="范围一" :value="reportPreview.summary.scope1" suffix="tCO2e" />
                </el-col>
                <el-col :span="6">
                  <el-statistic title="范围二" :value="reportPreview.summary.scope2" suffix="tCO2e" />
                </el-col>
                <el-col :span="6">
                  <el-statistic title="减排率" :value="reportPreview.summary.reduction" suffix="%" />
                </el-col>
              </el-row>
            </div>

            <el-divider />

            <div class="preview-toc">
              <h4>目录结构</h4>
              <div class="toc-list">
                <div
                  v-for="section in reportPreview.sections"
                  :key="section.name"
                  class="toc-item"
                >
                  <span class="toc-name">{{ section.name }}</span>
                  <span class="toc-page">第 {{ section.page }} 页</span>
                </div>
              </div>
            </div>
          </div>
        </el-card>

        <el-card v-if="activeStep === 3" class="step-card">
          <template #header>
            <div class="card-header">
              <span>生成完成</span>
              <el-tag type="success">步骤 4/4</el-tag>
            </div>
          </template>

          <div v-if="generatedReport" class="success-section">
            <div class="success-icon">
              <el-icon :size="64" color="#67C23A"><CircleCheck /></el-icon>
            </div>

            <h3>报告生成成功</h3>
            <p>您的MRV报告已准备就绪</p>

            <div class="report-info">
              <el-descriptions :column="2" border>
                <el-descriptions-item label="报告标题">{{ generatedReport.title }}</el-descriptions-item>
                <el-descriptions-item label="生成时间">{{ new Date(generatedReport.generatedAt).toLocaleString() }}</el-descriptions-item>
                <el-descriptions-item label="文件大小">{{ generatedReport.size }}</el-descriptions-item>
                <el-descriptions-item label="页数">{{ generatedReport.pages }} 页</el-descriptions-item>
              </el-descriptions>
            </div>

            <div class="download-actions">
              <el-button type="primary" :icon="Download" @click="handleDownload">
                下载报告
              </el-button>
              <el-button :icon="Printer" @click="handlePrint">
                打印
              </el-button>
              <el-button :icon="View" @click="handlePreview">
                预览
              </el-button>
            </div>
          </div>

          <div v-else class="generating-section">
            <el-icon class="is-loading" :size="48" color="#409EFF"><Loading /></el-icon>
            <h3>正在生成报告...</h3>
            <p>请稍候，这可能需要几分钟时间</p>
          </div>
        </el-card>

        <div class="step-actions">
          <el-button v-if="activeStep > 0 && activeStep < 3" @click="prevStep">
            上一步
          </el-button>
          <el-button
            v-if="activeStep < 2"
            type="primary"
            @click="nextStep"
          >
            下一步
          </el-button>
          <el-button
            v-if="activeStep === 2"
            type="primary"
            :loading="generating"
            @click="handleGenerate"
          >
            生成报告
          </el-button>
          <el-button
            v-if="activeStep === 3"
            type="primary"
            @click="handleReset"
          >
            生成新报告
          </el-button>
        </div>
      </div>
    </div>

    <el-dialog
      v-model="previewVisible"
      title="报告预览"
      width="80%"
      top="5vh"
    >
      <div class="preview-dialog">
        <div class="preview-page">
          <div class="page-header-preview">
            <h1>{{ reportPreview.title }}</h1>
            <p>EcoCarbon-MRV 碳排放监测报告</p>
          </div>

          <div class="page-content-preview">
            <h2>执行摘要</h2>
            <p>本报告概述了 {{ reportConfig.reportYear }} 年度的碳排放监测、报告和核查结果。</p>

            <div class="preview-stats">
              <div class="stat-card">
                <div class="stat-value">{{ reportPreview.summary.totalEmissions }}</div>
                <div class="stat-label">总排放量 (tCO2e)</div>
              </div>
              <div class="stat-card">
                <div class="stat-value">{{ reportPreview.summary.reduction }}%</div>
                <div class="stat-label">减排率</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <template #footer>
        <el-button @click="previewVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleDownload">下载</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.mrv-report {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
}

.page-header h2 {
  font-size: 24px;
  color: #303133;
  margin-bottom: 8px;
}

.page-header p {
  color: #909399;
  font-size: 14px;
}

.report-content {
  display: flex;
  gap: 24px;
}

.steps-section {
  width: 200px;
  flex-shrink: 0;
}

.form-section {
  flex: 1;
  min-width: 0;
}

.step-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.template-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.template-card {
  position: relative;
  border: 2px solid #ebeef5;
  border-radius: 8px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.2s;
}

.template-card:hover {
  border-color: #409EFF;
  background: #ecf5ff;
}

.template-card.selected {
  border-color: #409EFF;
  background: #ecf5ff;
}

.template-icon {
  margin-bottom: 12px;
  color: #409EFF;
}

.template-info h4 {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #303133;
}

.template-info p {
  margin: 0 0 12px 0;
  font-size: 13px;
  color: #909399;
}

.template-meta {
  font-size: 12px;
  color: #909399;
}

.template-check {
  position: absolute;
  top: 12px;
  right: 12px;
  color: #409EFF;
  font-size: 20px;
}

.preview-section {
  padding: 20px 0;
}

.preview-header h3 {
  margin: 0 0 8px 0;
  font-size: 18px;
  color: #303133;
}

.preview-meta {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: #909399;
}

.preview-summary {
  margin: 20px 0;
}

.preview-summary h4 {
  margin: 0 0 16px 0;
  color: #303133;
}

.preview-toc {
  margin: 20px 0;
}

.preview-toc h4 {
  margin: 0 0 16px 0;
  color: #303133;
}

.toc-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.toc-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #f5f7fa;
  border-radius: 8px;
}

.toc-name {
  font-weight: 500;
  color: #303133;
}

.toc-page {
  font-size: 13px;
  color: #909399;
}

.success-section {
  text-align: center;
  padding: 40px 0;
}

.success-icon {
  margin-bottom: 20px;
}

.success-section h3 {
  margin: 0 0 8px 0;
  font-size: 20px;
  color: #303133;
}

.success-section p {
  margin: 0 0 30px 0;
  color: #909399;
}

.report-info {
  margin: 30px 0;
}

.download-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 30px;
}

.generating-section {
  text-align: center;
  padding: 60px 0;
}

.generating-section h3 {
  margin: 20px 0 8px 0;
  font-size: 18px;
  color: #303133;
}

.generating-section p {
  color: #909399;
}

.step-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 20px;
}

.preview-dialog {
  background: #f5f7fa;
  padding: 20px;
  border-radius: 8px;
}

.preview-page {
  background: white;
  max-width: 800px;
  margin: 0 auto;
  padding: 40px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.page-header-preview {
  text-align: center;
  margin-bottom: 40px;
  padding-bottom: 20px;
  border-bottom: 2px solid #409EFF;
}

.page-header-preview h1 {
  margin: 0 0 8px 0;
  font-size: 24px;
  color: #303133;
}

.page-header-preview p {
  margin: 0;
  color: #909399;
}

.page-content-preview h2 {
  margin: 0 0 16px 0;
  font-size: 18px;
  color: #303133;
}

.page-content-preview p {
  margin: 0 0 20px 0;
  color: #606266;
  line-height: 1.6;
}

.preview-stats {
  display: flex;
  gap: 20px;
  margin: 20px 0;
}

.stat-card {
  flex: 1;
  text-align: center;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #409EFF;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 13px;
  color: #909399;
}
</style>