<script setup>
import { ref, reactive, computed } from "vue";
import { useCarbonStore } from "@/stores/carbon";
import { ElMessage } from "element-plus";
import {
  Cpu,
  DataAnalysis,
  Document,
  TrendCharts,
  Warning,
  CircleCheck,
  Setting,
} from "@element-plus/icons-vue";
import CarbonChart from "@/components/common/CarbonChart.vue";

const carbonStore = useCarbonStore();

const activeTab = ref("calculator");
const loading = ref(false);
const calculationResult = ref(null);
const uncertaintyResult = ref(null);
const reductionSuggestions = ref([]);

const calculatorForm = reactive({
  category: "agriculture",
  type: "fertilizer",
  quantity: 1000,
  unit: "kg",
  region: "north_china",
  season: "spring",
});

const categories = [
  { label: "农业", value: "agriculture" },
  { label: "林业", value: "forestry" },
  { label: "畜牧业", value: "livestock" },
  { label: "能源", value: "energy" },
];

const emissionTypes = {
  agriculture: [
    { label: "化肥施用", value: "fertilizer" },
    { label: "稻田甲烷", value: "paddy_methane" },
    { label: "农机作业", value: "machinery" },
    { label: "农药使用", value: "pesticide" },
  ],
  forestry: [
    { label: "林木碳汇", value: "tree_sequestration" },
    { label: "土壤碳汇", value: "soil_sequestration" },
    { label: "枯死木分解", value: "dead_wood" },
  ],
  livestock: [
    { label: "肠道发酵", value: "enteric_fermentation" },
    { label: "粪便管理", value: "manure_management" },
    { label: "饲料生产", value: "feed_production" },
  ],
  energy: [
    { label: "电力消耗", value: "electricity" },
    { label: "燃油消耗", value: "fuel" },
    { label: "煤炭消耗", value: "coal" },
  ],
};

const regionOptions = [
  { label: "华北", value: "north_china" },
  { label: "华东", value: "east_china" },
  { label: "华南", value: "south_china" },
  { label: "华中", value: "central_china" },
  { label: "西南", value: "southwest" },
  { label: "西北", value: "northwest" },
  { label: "东北", value: "northeast" },
];

const seasonOptions = [
  { label: "春季", value: "spring" },
  { label: "夏季", value: "summer" },
  { label: "秋季", value: "autumn" },
  { label: "冬季", value: "winter" },
];

const emissionFactors = ref([
  {
    id: 1,
    name: "氮肥施用N2O排放因子",
    category: "agriculture",
    factor: 0.01,
    unit: "kgN2O/kgN",
    source: "IPCC 2006",
    lastUpdated: "2024-01-15",
  },
  {
    id: 2,
    name: "稻田CH4排放因子",
    category: "agriculture",
    factor: 1.3,
    unit: "kgCH4/ha",
    source: "CN_GHG",
    lastUpdated: "2024-02-20",
  },
  {
    id: 3,
    name: "电力排放因子",
    category: "energy",
    factor: 0.5703,
    unit: "tCO2/MWh",
    source: "生态环境部",
    lastUpdated: "2024-03-10",
  },
  {
    id: 4,
    name: "柴油排放因子",
    category: "energy",
    factor: 2.73,
    unit: "kgCO2/L",
    source: "IPCC 2006",
    lastUpdated: "2024-01-15",
  },
]);

const methodologyConfigs = ref([
  {
    id: 1,
    name: "IPCC 2006",
    version: "2019 Refinement",
    status: "active",
    description: "政府间气候变化专门委员会2006年国家温室气体清单指南",
    applicableScenarios: ["agriculture", "forestry", "energy"],
  },
  {
    id: 2,
    name: "中国温室气体清单",
    version: "2024",
    status: "active",
    description: "中国省级温室气体清单编制指南",
    applicableScenarios: ["agriculture", "energy"],
  },
  {
    id: 3,
    name: "VCS",
    version: "v4.2",
    status: "active",
    description: "核证碳标准",
    applicableScenarios: ["forestry", "agriculture"],
  },
]);

async function handleCalculate() {
  loading.value = true;
  try {
    const res = await carbonStore.calculateInventory(calculatorForm);
    if (res.success) {
      calculationResult.value = res.data;
      generateUncertaintyAnalysis();
      generateReductionSuggestions();
      ElMessage.success("计算完成");
    } else {
      ElMessage.error(res.message || "计算失败");
    }
  } catch (error) {
    ElMessage.error("计算失败，请稍后重试");
  } finally {
    loading.value = false;
  }
}

function generateUncertaintyAnalysis() {
  if (calculationResult.value) {
    uncertaintyResult.value = {
      totalUncertainty: 12.5,
      confidenceInterval: {
        lower: calculationResult.value.totalEmissions * 0.875,
        upper: calculationResult.value.totalEmissions * 1.125,
      },
      factors: [
        { name: "排放因子不确定性", contribution: 45, level: "medium" },
        { name: "活动数据不确定性", contribution: 30, level: "low" },
        { name: "模型不确定性", contribution: 15, level: "low" },
        { name: "其他因素", contribution: 10, level: "low" },
      ],
    };
  }
}

function generateReductionSuggestions() {
  if (calculationResult.value) {
    reductionSuggestions.value = [
      {
        id: 1,
        title: "优化施肥管理",
        description: "采用精准施肥技术，减少氮肥用量20%，可降低N2O排放",
        potential: 15,
        cost: "low",
        timeframe: "短期",
      },
      {
        id: 2,
        title: "改进灌溉方式",
        description: "采用间歇灌溉替代持续淹水，减少稻田甲烷排放",
        potential: 25,
        cost: "medium",
        timeframe: "中期",
      },
      {
        id: 3,
        title: "农机电气化",
        description: "将燃油农机替换为电动农机，减少化石燃料消耗",
        potential: 30,
        cost: "high",
        timeframe: "长期",
      },
      {
        id: 4,
        title: "秸秆还田",
        description: "增加秸秆还田比例，提高土壤碳汇能力",
        potential: 20,
        cost: "low",
        timeframe: "短期",
      },
    ];
  }
}

function getCostLabel(cost) {
  const map = { low: "低成本", medium: "中等成本", high: "高成本" };
  return map[cost] || cost;
}

function getCostType(cost) {
  const map = { low: "success", medium: "warning", high: "danger" };
  return map[cost] || "info";
}

function getUncertaintyLevel(level) {
  const map = { low: "success", medium: "warning", high: "danger" };
  return map[level] || "info";
}

function getMethodologyStatus(status) {
  const map = { active: "success", inactive: "info", draft: "warning" };
  return map[status] || "info";
}

function getMethodologyStatusLabel(status) {
  const map = { active: "启用", inactive: "停用", draft: "草稿" };
  return map[status] || status;
}
</script>

<template>
  <div class="carbon-engine">
    <div class="page-header">
      <div>
        <h2>碳核算引擎</h2>
        <p>碳排放因子库与核算模型</p>
      </div>
    </div>

    <el-tabs v-model="activeTab" class="engine-tabs">
      <el-tab-pane label="碳计算器" name="calculator">
        <div class="calculator-content">
          <el-card class="calculator-form">
            <template #header>
              <div class="card-header">
                <el-icon><Cpu /></el-icon>
                <span>碳排放计算</span>
              </div>
            </template>

            <el-form :model="calculatorForm" label-width="100px">
              <el-form-item label="排放类别">
                <el-select v-model="calculatorForm.category" style="width: 100%">
                  <el-option
                    v-for="item in categories"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
              </el-form-item>

              <el-form-item label="排放类型">
                <el-select v-model="calculatorForm.type" style="width: 100%">
                  <el-option
                    v-for="item in emissionTypes[calculatorForm.category]"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
              </el-form-item>

              <el-form-item label="数量">
                <el-input-number
                  v-model="calculatorForm.quantity"
                  :min="0"
                  :step="100"
                  style="width: 100%"
                />
              </el-form-item>

              <el-form-item label="单位">
                <el-select v-model="calculatorForm.unit" style="width: 100%">
                  <el-option label="千克 (kg)" value="kg" />
                  <el-option label="吨 (t)" value="t" />
                  <el-option label="千瓦时 (kWh)" value="kWh" />
                  <el-option label="公顷 (ha)" value="ha" />
                </el-select>
              </el-form-item>

              <el-form-item label="地区">
                <el-select v-model="calculatorForm.region" style="width: 100%">
                  <el-option
                    v-for="item in regionOptions"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
              </el-form-item>

              <el-form-item label="季节">
                <el-select v-model="calculatorForm.season" style="width: 100%">
                  <el-option
                    v-for="item in seasonOptions"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
              </el-form-item>

              <el-form-item>
                <el-button
                  type="primary"
                  :loading="loading"
                  @click="handleCalculate"
                  style="width: 100%"
                >
                  开始计算
                </el-button>
              </el-form-item>
            </el-form>
          </el-card>

          <div class="result-section">
            <el-card class="calculator-result">
              <template #header>
                <div class="card-header">
                  <el-icon><DataAnalysis /></el-icon>
                  <span>计算结果</span>
                </div>
              </template>

              <div v-if="calculationResult" class="result-content">
                <div class="result-summary">
                  <el-statistic
                    title="总排放量"
                    :value="calculationResult.totalEmissions"
                    suffix="tCO2e"
                  />
                  <el-statistic
                    title="排放强度"
                    :value="calculationResult.intensity"
                    suffix="kgCO2e/kg"
                  />
                </div>

                <el-divider />

                <div class="result-details">
                  <h4>排放分解</h4>
                  <CarbonChart
                    type="pie"
                    :data="calculationResult.breakdown"
                  />
                </div>
              </div>

              <div v-else class="empty-result">
                <el-empty description="请填写参数并执行计算" />
              </div>
            </el-card>

            <el-card v-if="uncertaintyResult" class="uncertainty-card">
              <template #header>
                <div class="card-header">
                  <el-icon><Warning /></el-icon>
                  <span>不确定性分析</span>
                </div>
              </template>

              <div class="uncertainty-content">
                <div class="uncertainty-summary">
                  <el-progress
                    type="dashboard"
                    :percentage="100 - uncertaintyResult.totalUncertainty"
                    :color="[
                      { color: '#f56c6c', percentage: 60 },
                      { color: '#e6a23c', percentage: 80 },
                      { color: '#67c23a', percentage: 100 },
                    ]"
                  />
                  <div class="uncertainty-label">
                    <div class="confidence">置信度</div>
                    <div class="range">
                      {{ uncertaintyResult.confidenceInterval.lower.toFixed(1) }} -
                      {{ uncertaintyResult.confidenceInterval.upper.toFixed(1) }} tCO2e
                    </div>
                  </div>
                </div>

                <el-divider />

                <div class="uncertainty-factors">
                  <h4>不确定性来源</h4>
                  <div
                    v-for="factor in uncertaintyResult.factors"
                    :key="factor.name"
                    class="factor-item"
                  >
                    <div class="factor-header">
                      <span>{{ factor.name }}</span>
                      <el-tag :type="getUncertaintyLevel(factor.level)" size="small">
                        {{ factor.contribution }}%
                      </el-tag>
                    </div>
                    <el-progress
                      :percentage="factor.contribution"
                      :show-text="false"
                      :stroke-width="8"
                    />
                  </div>
                </div>
              </div>
            </el-card>
          </div>
        </div>
      </el-tab-pane>

      <el-tab-pane label="排放因子库" name="factors">
        <el-card>
          <template #header>
            <div class="card-header">
              <el-icon><Document /></el-icon>
              <span>排放因子库</span>
            </div>
          </template>

          <el-table :data="emissionFactors" stripe>
            <el-table-column prop="name" label="因子名称" min-width="200" />
            <el-table-column label="类别" width="120">
              <template #default="{ row }">
                <el-tag size="small">
                  {{ categories.find((c) => c.value === row.category)?.label || row.category }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="factor" label="因子值" width="120" />
            <el-table-column prop="unit" label="单位" width="150" />
            <el-table-column prop="source" label="来源" width="150" />
            <el-table-column prop="lastUpdated" label="更新时间" width="120" />
          </el-table>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="核算模型" name="models">
        <el-card>
          <template #header>
            <div class="card-header">
              <el-icon><Cpu /></el-icon>
              <span>核算模型配置</span>
            </div>
          </template>

          <div class="model-list">
            <el-card
              v-for="model in methodologyConfigs"
              :key="model.id"
              class="model-card"
              shadow="hover"
            >
              <div class="model-header">
                <div class="model-title">
                  <h4>{{ model.name }}</h4>
                  <el-tag
                    :type="getMethodologyStatus(model.status)"
                    size="small"
                  >
                    {{ getMethodologyStatusLabel(model.status) }}
                  </el-tag>
                </div>
                <div class="model-version">版本: {{ model.version }}</div>
              </div>

              <div class="model-description">
                {{ model.description }}
              </div>

              <div class="model-scenarios">
                <span class="label">适用场景:</span>
                <el-tag
                  v-for="scenario in model.applicableScenarios"
                  :key="scenario"
                  size="small"
                  style="margin-right: 8px;"
                >
                  {{ categories.find((c) => c.value === scenario)?.label || scenario }}
                </el-tag>
              </div>

              <div class="model-actions">
                <el-button type="primary" link>
                  <el-icon><Setting /></el-icon>配置
                </el-button>
                <el-button type="success" link>
                  <el-icon><View /></el-icon>查看
                </el-button>
              </div>
            </el-card>
          </div>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="减排建议" name="suggestions">
        <el-card>
          <template #header>
            <div class="card-header">
              <el-icon><TrendCharts /></el-icon>
              <span>减排建议</span>
            </div>
          </template>

          <div v-if="reductionSuggestions.length > 0" class="suggestions-list">
            <el-card
              v-for="suggestion in reductionSuggestions"
              :key="suggestion.id"
              class="suggestion-card"
              shadow="hover"
            >
              <div class="suggestion-header">
                <h4>{{ suggestion.title }}</h4>
                <div class="suggestion-meta">
                  <el-tag :type="getCostType(suggestion.cost)" size="small">
                    {{ getCostLabel(suggestion.cost) }}
                  </el-tag>
                  <el-tag type="info" size="small">
                    {{ suggestion.timeframe }}
                  </el-tag>
                </div>
              </div>

              <div class="suggestion-description">
                {{ suggestion.description }}
              </div>

              <div class="suggestion-potential">
                <span class="label">减排潜力:</span>
                <el-progress
                  :percentage="suggestion.potential"
                  :stroke-width="10"
                  :format="(percentage) => `${percentage}%`"
                />
              </div>
            </el-card>
          </div>

          <div v-else class="empty-suggestions">
            <el-empty description="请先执行碳核算以获取减排建议" />
          </div>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<style scoped>
.carbon-engine {
  padding: 20px;
}

.page-header {
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

.engine-tabs {
  background: white;
  border-radius: 8px;
  padding: 20px;
}

.calculator-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.result-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
}

.result-summary {
  display: flex;
  justify-content: space-around;
  margin-bottom: 20px;
}

.result-details h4 {
  margin-bottom: 16px;
  color: #303133;
}

.empty-result {
  padding: 40px 0;
}

.uncertainty-content {
  padding: 16px 0;
}

.uncertainty-summary {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 24px;
  margin-bottom: 20px;
}

.uncertainty-label {
  text-align: center;
}

.uncertainty-label .confidence {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.uncertainty-label .range {
  font-size: 13px;
  color: #909399;
}

.uncertainty-factors h4 {
  margin-bottom: 16px;
  color: #303133;
}

.factor-item {
  margin-bottom: 16px;
}

.factor-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.factor-header span {
  font-size: 14px;
  color: #606266;
}

.model-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
}

.model-card {
  margin-bottom: 0;
}

.model-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.model-title {
  display: flex;
  align-items: center;
  gap: 12px;
}

.model-title h4 {
  margin: 0;
  font-size: 16px;
  color: #303133;
}

.model-version {
  font-size: 12px;
  color: #909399;
}

.model-description {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  margin-bottom: 16px;
}

.model-scenarios {
  margin-bottom: 16px;
}

.model-scenarios .label {
  font-size: 13px;
  color: #909399;
  margin-right: 8px;
}

.model-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.suggestions-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
}

.suggestion-card {
  margin-bottom: 0;
}

.suggestion-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.suggestion-header h4 {
  margin: 0;
  font-size: 16px;
  color: #303133;
}

.suggestion-meta {
  display: flex;
  gap: 8px;
}

.suggestion-description {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  margin-bottom: 16px;
}

.suggestion-potential {
  margin-top: 16px;
}

.suggestion-potential .label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 8px;
  display: block;
}

.empty-suggestions {
  padding: 60px 0;
}
</style>