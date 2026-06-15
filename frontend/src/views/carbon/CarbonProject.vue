<script setup>
import { ref, reactive, computed, onMounted } from "vue";
import { useCarbonStore } from "@/stores/carbon";
import { useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  Plus,
  Edit,
  Delete,
  Search,
  View,
  Document,
  Grid,
  List,
} from "@element-plus/icons-vue";

const carbonStore = useCarbonStore();
const router = useRouter();

const projects = ref([]);
const loading = ref(false);
const viewMode = ref("card");
const searchQuery = ref("");
const dialogVisible = ref(false);
const detailDialogVisible = ref(false);
const currentStep = ref(0);
const selectedProject = ref(null);

const projectForm = reactive({
  name: "",
  sceneType: "AGRICULTURE",
  areaName: "",
  areaHa: 100,
  description: "",
  startDate: "",
  endDate: "",
  contactPerson: "",
  contactPhone: "",
  methodology: "",
  baselineYear: 2024,
  targetReduction: 10,
});

const sceneTypes = [
  { label: "农业", value: "AGRICULTURE" },
  { label: "林业", value: "FORESTRY" },
  { label: "畜牧业", value: "LIVESTOCK" },
  { label: "渔业", value: "FISHERY" },
];

const methodologyOptions = [
  { label: "IPCC 2006", value: "IPCC_2006" },
  { label: "中国温室气体清单", value: "CN_GHG" },
  { label: "VCS", value: "VCS" },
  { label: "CCER", value: "CCER" },
  { label: "Gold Standard", value: "GOLD_STANDARD" },
];

const steps = [
  { title: "基本信息", description: "项目基础信息" },
  { title: "项目配置", description: "方法学与目标" },
  { title: "确认信息", description: "检查并提交" },
];

const filteredProjects = computed(() => {
  if (!searchQuery.value) return projects.value;
  return projects.value.filter(
    (project) =>
      project.name.includes(searchQuery.value) ||
      project.areaName.includes(searchQuery.value)
  );
});

onMounted(async () => {
  await fetchProjects();
});

async function fetchProjects() {
  loading.value = true;
  try {
    await carbonStore.fetchProjects();
    projects.value = carbonStore.projects;
  } catch (error) {
    ElMessage.error("获取项目列表失败");
  } finally {
    loading.value = false;
  }
}

function handleCreate() {
  resetForm();
  dialogVisible.value = true;
  currentStep.value = 0;
}

function handleEdit(row) {
  Object.assign(projectForm, {
    id: row.id,
    name: row.name,
    sceneType: row.sceneType,
    areaName: row.areaName,
    areaHa: row.areaHa,
    description: row.description || "",
    startDate: row.startDate || "",
    endDate: row.endDate || "",
    contactPerson: row.contactPerson || "",
    contactPhone: row.contactPhone || "",
    methodology: row.methodology || "",
    baselineYear: row.baselineYear || 2024,
    targetReduction: row.targetReduction || 10,
  });
  dialogVisible.value = true;
  currentStep.value = 0;
}

function handleView(row) {
  selectedProject.value = row;
  detailDialogVisible.value = true;
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm("确定要删除该项目吗？此操作不可恢复。", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });
    ElMessage.success("删除成功");
    await fetchProjects();
  } catch {
    // 取消删除
  }
}

function handleGenerateReport(row) {
  router.push({
    path: "/mrv/report",
    query: { projectId: row.id },
  });
}

function resetForm() {
  Object.assign(projectForm, {
    name: "",
    sceneType: "AGRICULTURE",
    areaName: "",
    areaHa: 100,
    description: "",
    startDate: "",
    endDate: "",
    contactPerson: "",
    contactPhone: "",
    methodology: "",
    baselineYear: 2024,
    targetReduction: 10,
  });
}

function nextStep() {
  if (currentStep.value < steps.length - 1) {
    currentStep.value++;
  }
}

function prevStep() {
  if (currentStep.value > 0) {
    currentStep.value--;
  }
}

async function handleSubmit() {
  if (!projectForm.name || !projectForm.areaName) {
    ElMessage.warning("请填写完整信息");
    return;
  }

  try {
    const result = await carbonStore.createProject({ ...projectForm });
    if (result.success) {
      ElMessage.success("创建成功");
      dialogVisible.value = false;
      await fetchProjects();
    } else {
      ElMessage.error(result.message || "创建失败");
    }
  } catch (error) {
    ElMessage.error("创建失败，请稍后重试");
  }
}

function getSceneTypeLabel(type) {
  const found = sceneTypes.find((item) => item.value === type);
  return found ? found.label : type;
}

function getMethodologyLabel(value) {
  const found = methodologyOptions.find((item) => item.value === value);
  return found ? found.label : value;
}

function getStatusType(status) {
  const statusMap = {
    active: "success",
    completed: "info",
    draft: "warning",
    archived: "danger",
  };
  return statusMap[status] || "info";
}

function getStatusLabel(status) {
  const statusMap = {
    active: "进行中",
    completed: "已完成",
    draft: "草稿",
    archived: "已归档",
  };
  return statusMap[status] || status;
}
</script>

<template>
  <div class="carbon-project">
    <div class="page-header">
      <div>
        <h2>碳项目管理</h2>
        <p>管理碳汇项目与碳减排项目</p>
      </div>
      <div class="header-actions">
        <el-radio-group v-model="viewMode" style="margin-right: 16px;">
          <el-radio-button value="card">
            <el-icon><Grid /></el-icon>
          </el-radio-button>
          <el-radio-button value="table">
            <el-icon><List /></el-icon>
          </el-radio-button>
        </el-radio-group>
        <el-button type="primary" :icon="Plus" @click="handleCreate">
          新建项目
        </el-button>
      </div>
    </div>

    <div class="toolbar">
      <el-input
        v-model="searchQuery"
        placeholder="搜索项目名称或区域"
        :prefix-icon="Search"
        style="width: 300px"
        clearable
      />
      <div class="toolbar-info">
        共 {{ filteredProjects.length }} 个项目
      </div>
    </div>

    <div v-if="viewMode === 'card'" class="project-cards">
      <el-row :gutter="20">
        <el-col
          v-for="project in filteredProjects"
          :key="project.id"
          :xs="24"
          :sm="12"
          :lg="8"
          :xl="6"
        >
          <el-card class="project-card" shadow="hover">
            <div class="card-header">
              <div class="project-title">
                <h4>{{ project.name }}</h4>
                <el-tag :type="getStatusType(project.status || 'active')" size="small">
                  {{ getStatusLabel(project.status || "active") }}
                </el-tag>
              </div>
              <el-dropdown trigger="click">
                <el-button link>
                  <el-icon><More /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="handleView(project)">
                      <el-icon><View /></el-icon>查看详情
                    </el-dropdown-item>
                    <el-dropdown-item @click="handleEdit(project)">
                      <el-icon><Edit /></el-icon>编辑
                    </el-dropdown-item>
                    <el-dropdown-item @click="handleGenerateReport(project)">
                      <el-icon><Document /></el-icon>生成报告
                    </el-dropdown-item>
                    <el-dropdown-item divided @click="handleDelete(project)">
                      <el-icon><Delete /></el-icon>删除
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>

            <div class="card-content">
              <div class="project-info">
                <div class="info-item">
                  <span class="label">项目类型</span>
                  <el-tag size="small">{{ getSceneTypeLabel(project.sceneType) }}</el-tag>
                </div>
                <div class="info-item">
                  <span class="label">区域</span>
                  <span>{{ project.areaName }}</span>
                </div>
                <div class="info-item">
                  <span class="label">面积</span>
                  <span>{{ project.areaHa }} ha</span>
                </div>
                <div class="info-item" v-if="project.methodology">
                  <span class="label">方法学</span>
                  <span>{{ getMethodologyLabel(project.methodology) }}</span>
                </div>
              </div>

              <div class="project-description" v-if="project.description">
                {{ project.description }}
              </div>
            </div>

            <div class="card-footer">
              <el-button type="primary" link @click="handleView(project)">
                查看详情
              </el-button>
              <el-button type="success" link @click="handleGenerateReport(project)">
                生成报告
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <el-card v-if="viewMode === 'table'" class="table-card">
      <el-table :data="filteredProjects" v-loading="loading" stripe>
        <el-table-column prop="name" label="项目名称" min-width="150" />
        <el-table-column label="项目类型" width="120">
          <template #default="{ row }">
            <el-tag>{{ getSceneTypeLabel(row.sceneType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="areaName" label="区域" width="120" />
        <el-table-column prop="areaHa" label="面积 (ha)" width="100" />
        <el-table-column label="方法学" width="120">
          <template #default="{ row }">
            {{ row.methodology ? getMethodologyLabel(row.methodology) : "-" }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status || 'active')" size="small">
              {{ getStatusLabel(row.status || "active") }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" :icon="View" link @click="handleView(row)">
              查看
            </el-button>
            <el-button type="warning" :icon="Edit" link @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="success" :icon="Document" link @click="handleGenerateReport(row)">
              报告
            </el-button>
            <el-button type="danger" :icon="Delete" link @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="projectForm.id ? '编辑项目' : '新建项目'"
      width="600px"
    >
      <el-steps :active="currentStep" finish-status="success" style="margin-bottom: 24px;">
        <el-step
          v-for="(step, index) in steps"
          :key="index"
          :title="step.title"
          :description="step.description"
        />
      </el-steps>

      <div v-if="currentStep === 0">
        <el-form :model="projectForm" label-width="100px">
          <el-form-item label="项目名称" required>
            <el-input v-model="projectForm.name" placeholder="请输入项目名称" />
          </el-form-item>

          <el-form-item label="项目类型">
            <el-select v-model="projectForm.sceneType" style="width: 100%">
              <el-option
                v-for="item in sceneTypes"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="区域名称" required>
            <el-input v-model="projectForm.areaName" placeholder="请输入区域名称" />
          </el-form-item>

          <el-form-item label="面积 (ha)">
            <el-input-number
              v-model="projectForm.areaHa"
              :min="0"
              :step="10"
              style="width: 100%"
            />
          </el-form-item>

          <el-form-item label="项目描述">
            <el-input
              v-model="projectForm.description"
              type="textarea"
              :rows="3"
              placeholder="请输入项目描述"
            />
          </el-form-item>
        </el-form>
      </div>

      <div v-if="currentStep === 1">
        <el-form :model="projectForm" label-width="100px">
          <el-form-item label="核算方法学">
            <el-select v-model="projectForm.methodology" style="width: 100%">
              <el-option
                v-for="item in methodologyOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="基准年份">
            <el-input-number
              v-model="projectForm.baselineYear"
              :min="2000"
              :max="2030"
              style="width: 100%"
            />
          </el-form-item>

          <el-form-item label="减排目标 (%)">
            <el-input-number
              v-model="projectForm.targetReduction"
              :min="0"
              :max="100"
              :step="5"
              style="width: 100%"
            />
          </el-form-item>

          <el-form-item label="开始日期">
            <el-date-picker
              v-model="projectForm.startDate"
              type="date"
              placeholder="选择开始日期"
              style="width: 100%"
            />
          </el-form-item>

          <el-form-item label="结束日期">
            <el-date-picker
              v-model="projectForm.endDate"
              type="date"
              placeholder="选择结束日期"
              style="width: 100%"
            />
          </el-form-item>
        </el-form>
      </div>

      <div v-if="currentStep === 2">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="项目名称">{{ projectForm.name }}</el-descriptions-item>
          <el-descriptions-item label="项目类型">{{ getSceneTypeLabel(projectForm.sceneType) }}</el-descriptions-item>
          <el-descriptions-item label="区域名称">{{ projectForm.areaName }}</el-descriptions-item>
          <el-descriptions-item label="面积">{{ projectForm.areaHa }} ha</el-descriptions-item>
          <el-descriptions-item label="核算方法学">{{ getMethodologyLabel(projectForm.methodology) || "未选择" }}</el-descriptions-item>
          <el-descriptions-item label="基准年份">{{ projectForm.baselineYear }}</el-descriptions-item>
          <el-descriptions-item label="减排目标">{{ projectForm.targetReduction }}%</el-descriptions-item>
          <el-descriptions-item label="项目描述">{{ projectForm.description || "无" }}</el-descriptions-item>
        </el-descriptions>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button v-if="currentStep > 0" @click="prevStep">上一步</el-button>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button
            v-if="currentStep < steps.length - 1"
            type="primary"
            @click="nextStep"
          >
            下一步
          </el-button>
          <el-button
            v-if="currentStep === steps.length - 1"
            type="primary"
            @click="handleSubmit"
          >
            提交
          </el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog
      v-model="detailDialogVisible"
      title="项目详情"
      width="700px"
    >
      <div v-if="selectedProject" class="project-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="项目名称" :span="2">{{ selectedProject.name }}</el-descriptions-item>
          <el-descriptions-item label="项目类型">{{ getSceneTypeLabel(selectedProject.sceneType) }}</el-descriptions-item>
          <el-descriptions-item label="区域">{{ selectedProject.areaName }}</el-descriptions-item>
          <el-descriptions-item label="面积">{{ selectedProject.areaHa }} ha</el-descriptions-item>
          <el-descriptions-item label="方法学">{{ getMethodologyLabel(selectedProject.methodology) || "未选择" }}</el-descriptions-item>
          <el-descriptions-item label="基准年份">{{ selectedProject.baselineYear || "-" }}</el-descriptions-item>
          <el-descriptions-item label="减排目标">{{ selectedProject.targetReduction || 0 }}%</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(selectedProject.status || 'active')">
              {{ getStatusLabel(selectedProject.status || "active") }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="项目描述" :span="2">
            {{ selectedProject.description || "暂无描述" }}
          </el-descriptions-item>
        </el-descriptions>

        <div class="detail-actions">
          <el-button type="primary" :icon="Edit" @click="handleEdit(selectedProject); detailDialogVisible = false;">
            编辑项目
          </el-button>
          <el-button type="success" :icon="Document" @click="handleGenerateReport(selectedProject)">
            生成MRV报告
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<style scoped>
.carbon-project {
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

.header-actions {
  display: flex;
  align-items: center;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.toolbar-info {
  color: #909399;
  font-size: 14px;
}

.project-cards {
  margin-top: 20px;
}

.project-card {
  margin-bottom: 20px;
  height: 100%;
}

.project-card :deep(.el-card__body) {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.project-title {
  flex: 1;
}

.project-title h4 {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #303133;
}

.card-content {
  flex: 1;
}

.project-info {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  margin-bottom: 16px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-item .label {
  font-size: 12px;
  color: #909399;
}

.project-description {
  font-size: 13px;
  color: #606266;
  line-height: 1.6;
  margin-bottom: 16px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}

.table-card {
  margin-top: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.project-detail {
  margin-top: 16px;
}

.detail-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 24px;
}
</style>