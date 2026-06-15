<script setup>
import { ref, reactive, computed, onMounted } from "vue";
import { useCarbonStore } from "@/stores/carbon";
import { ElMessage } from "element-plus";
import { Plus, Delete, View, Download } from "@element-plus/icons-vue";
import EmissionBreakdown from "@/components/common/EmissionBreakdown.vue";

const carbonStore = useCarbonStore();

const activeStep = ref(0);
const loading = ref(false);
const result = ref(null);

const farmInfo = reactive({
  name: "",
  location: "",
  area: 100,
  contact: "",
  phone: "",
});

const cropSystem = reactive({
  crops: [
    {
      id: 1,
      type: "rice",
      area: 60,
      rotation: "single",
      yieldPerHa: 8.5,
    },
  ],
});

const livestock = reactive({
  animals: [
    {
      id: 1,
      type: "cattle",
      count: 50,
      weight: 500,
    },
  ],
});

const inputMaterials = reactive({
  fertilizers: [
    {
      id: 1,
      type: "nitrogen",
      amount: 200,
      unit: "kg/ha",
    },
  ],
  pesticides: [
    {
      id: 1,
      type: "herbicide",
      amount: 5,
      unit: "L/ha",
    },
  ],
});

const energyConsumption = reactive({
  irrigation: {
    electricity: 3000,
    unit: "kWh",
  },
  machinery: {
    diesel: 500,
    unit: "L",
  },
  other: [],
});

const cropTypes = [
  { label: "水稻", value: "rice" },
  { label: "小麦", value: "wheat" },
  { label: "玉米", value: "corn" },
  { label: "大豆", value: "soybean" },
  { label: "蔬菜", value: "vegetable" },
  { label: "水果", value: "fruit" },
];

const rotationTypes = [
  { label: "单季", value: "single" },
  { label: "双季", value: "double" },
  { label: "三季", value: "triple" },
  { label: "轮作", value: "rotation" },
];

const livestockTypes = [
  { label: "牛", value: "cattle" },
  { label: "猪", value: "pig" },
  { label: "羊", value: "sheep" },
  { label: "鸡", value: "chicken" },
  { label: "鸭", value: "duck" },
];

const fertilizerTypes = [
  { label: "氮肥", value: "nitrogen" },
  { label: "磷肥", value: "phosphorus" },
  { label: "钾肥", value: "potassium" },
  { label: "复合肥", value: "compound" },
  { label: "有机肥", value: "organic" },
];

const pesticideTypes = [
  { label: "除草剂", value: "herbicide" },
  { label: "杀虫剂", value: "insecticide" },
  { label: "杀菌剂", value: "fungicide" },
  { label: "植物生长调节剂", value: "regulator" },
];

const steps = [
  { title: "农场信息", description: "基本信息" },
  { title: "种植制度", description: "作物配置" },
  { title: "养殖规模", description: "牲畜信息" },
  { title: "农资投入", description: "化肥农药" },
  { title: "能耗记录", description: "能源消耗" },
  { title: "碳排放清单", description: "核算结果" },
];

const totalCropArea = computed(() => {
  return cropSystem.crops.reduce((sum, crop) => sum + crop.area, 0);
});

const totalLivestock = computed(() => {
  return livestock.animals.reduce((sum, animal) => sum + animal.count, 0);
});

function addCrop() {
  const id = Date.now();
  cropSystem.crops.push({
    id,
    type: "wheat",
    area: 0,
    rotation: "single",
    yieldPerHa: 0,
  });
}

function removeCrop(id) {
  if (cropSystem.crops.length > 1) {
    cropSystem.crops = cropSystem.crops.filter((crop) => crop.id !== id);
  }
}

function addAnimal() {
  const id = Date.now();
  livestock.animals.push({
    id,
    type: "pig",
    count: 0,
    weight: 0,
  });
}

function removeAnimal(id) {
  if (livestock.animals.length > 1) {
    livestock.animals = livestock.animals.filter((animal) => animal.id !== id);
  }
}

function addFertilizer() {
  const id = Date.now();
  inputMaterials.fertilizers.push({
    id,
    type: "phosphorus",
    amount: 0,
    unit: "kg/ha",
  });
}

function removeFertilizer(id) {
  if (inputMaterials.fertilizers.length > 1) {
    inputMaterials.fertilizers = inputMaterials.fertilizers.filter((f) => f.id !== id);
  }
}

function addPesticide() {
  const id = Date.now();
  inputMaterials.pesticides.push({
    id,
    type: "insecticide",
    amount: 0,
    unit: "L/ha",
  });
}

function removePesticide(id) {
  if (inputMaterials.pesticides.length > 1) {
    inputMaterials.pesticides = inputMaterials.pesticides.filter((p) => p.id !== id);
  }
}

function addEnergyItem() {
  const id = Date.now();
  energyConsumption.other.push({
    id,
    type: "coal",
    amount: 0,
    unit: "kg",
  });
}

function removeEnergyItem(id) {
  energyConsumption.other = energyConsumption.other.filter((item) => item.id !== id);
}

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

function goToStep(step) {
  activeStep.value = step;
}

async function handleCalculate() {
  const formData = {
    farmInfo: { ...farmInfo },
    cropSystem: {
      crops: cropSystem.crops.map((crop) => ({ ...crop })),
      totalArea: totalCropArea.value,
    },
    livestock: {
      animals: livestock.animals.map((animal) => ({ ...animal })),
      totalCount: totalLivestock.value,
    },
    inputMaterials: {
      fertilizers: inputMaterials.fertilizers.map((f) => ({ ...f })),
      pesticides: inputMaterials.pesticides.map((p) => ({ ...p })),
    },
    energyConsumption: {
      irrigation: { ...energyConsumption.irrigation },
      machinery: { ...energyConsumption.machinery },
      other: energyConsumption.other.map((item) => ({ ...item })),
    },
  };

  loading.value = true;
  try {
    const res = await carbonStore.calculateInventory(formData);
    if (res.success) {
      result.value = res.data;
      activeStep.value = 5;
      ElMessage.success("碳盘查计算完成");
    } else {
      ElMessage.error(res.message || "计算失败");
    }
  } catch (error) {
    ElMessage.error("计算失败，请稍后重试");
  } finally {
    loading.value = false;
  }
}

function handleReset() {
  activeStep.value = 0;
  result.value = null;
  farmInfo.name = "";
  farmInfo.location = "";
  farmInfo.area = 100;
  farmInfo.contact = "";
  farmInfo.phone = "";
  cropSystem.crops = [
    { id: 1, type: "rice", area: 60, rotation: "single", yieldPerHa: 8.5 },
  ];
  livestock.animals = [
    { id: 1, type: "cattle", count: 50, weight: 500 },
  ];
  inputMaterials.fertilizers = [
    { id: 1, type: "nitrogen", amount: 200, unit: "kg/ha" },
  ];
  inputMaterials.pesticides = [
    { id: 1, type: "herbicide", amount: 5, unit: "L/ha" },
  ];
  energyConsumption.irrigation.electricity = 3000;
  energyConsumption.machinery.diesel = 500;
  energyConsumption.other = [];
}

function getCropLabel(value) {
  const found = cropTypes.find((item) => item.value === value);
  return found ? found.label : value;
}

function getLivestockLabel(value) {
  const found = livestockTypes.find((item) => item.value === value);
  return found ? found.label : value;
}

function getFertilizerLabel(value) {
  const found = fertilizerTypes.find((item) => item.value === value);
  return found ? found.label : value;
}

function getPesticideLabel(value) {
  const found = pesticideTypes.find((item) => item.value === value);
  return found ? found.label : value;
}

onMounted(() => {
  carbonStore.clearError();
});
</script>

<template>
  <div class="carbon-inventory">
    <div class="page-header">
      <div>
        <h2>碳盘查</h2>
        <p>农业碳排放核算与分析</p>
      </div>
      <div class="header-actions">
        <el-button @click="handleReset">重置</el-button>
        <el-button
          type="primary"
          :loading="loading"
          :disabled="activeStep !== 5"
          @click="handleCalculate"
        >
          执行核算
        </el-button>
      </div>
    </div>

    <div class="inventory-content">
      <div class="steps-section">
        <el-steps :active="activeStep" direction="vertical" finish-status="success">
          <el-step
            v-for="(step, index) in steps"
            :key="index"
            :title="step.title"
            :description="step.description"
            @click="goToStep(index)"
          />
        </el-steps>
      </div>

      <div class="form-section">
        <el-card v-if="activeStep === 0" class="step-card">
          <template #header>
            <div class="card-header">
              <span>农场基本信息</span>
              <el-tag type="info">步骤 1/6</el-tag>
            </div>
          </template>

          <el-form :model="farmInfo" label-width="120px">
            <el-form-item label="农场名称" required>
              <el-input v-model="farmInfo.name" placeholder="请输入农场名称" />
            </el-form-item>

            <el-form-item label="农场位置" required>
              <el-input v-model="farmInfo.location" placeholder="请输入农场位置" />
            </el-form-item>

            <el-form-item label="农场面积 (ha)" required>
              <el-input-number
                v-model="farmInfo.area"
                :min="0"
                :step="10"
                style="width: 100%"
              />
            </el-form-item>

            <el-form-item label="联系人">
              <el-input v-model="farmInfo.contact" placeholder="请输入联系人姓名" />
            </el-form-item>

            <el-form-item label="联系电话">
              <el-input v-model="farmInfo.phone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-form>
        </el-card>

        <el-card v-if="activeStep === 1" class="step-card">
          <template #header>
            <div class="card-header">
              <span>种植制度配置</span>
              <el-tag type="info">步骤 2/6</el-tag>
            </div>
          </template>

          <div class="crop-summary">
            <el-statistic title="总种植面积" :value="totalCropArea" suffix="ha" />
            <el-statistic title="作物种类" :value="cropSystem.crops.length" suffix="种" />
          </div>

          <div class="crop-list">
            <div
              v-for="(crop, index) in cropSystem.crops"
              :key="crop.id"
              class="crop-item"
            >
              <div class="crop-header">
                <span>作物 {{ index + 1 }}</span>
                <el-button
                  v-if="cropSystem.crops.length > 1"
                  type="danger"
                  :icon="Delete"
                  link
                  @click="removeCrop(crop.id)"
                >
                  删除
                </el-button>
              </div>

              <el-form :model="crop" label-width="100px">
                <el-row :gutter="20">
                  <el-col :span="12">
                    <el-form-item label="作物类型">
                      <el-select v-model="crop.type" style="width: 100%">
                        <el-option
                          v-for="item in cropTypes"
                          :key="item.value"
                          :label="item.label"
                          :value="item.value"
                        />
                      </el-select>
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="种植面积">
                      <el-input-number
                        v-model="crop.area"
                        :min="0"
                        :step="5"
                        style="width: 100%"
                      />
                    </el-form-item>
                  </el-col>
                </el-row>

                <el-row :gutter="20">
                  <el-col :span="12">
                    <el-form-item label="轮作方式">
                      <el-select v-model="crop.rotation" style="width: 100%">
                        <el-option
                          v-for="item in rotationTypes"
                          :key="item.value"
                          :label="item.label"
                          :value="item.value"
                        />
                      </el-select>
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="单产 (t/ha)">
                      <el-input-number
                        v-model="crop.yieldPerHa"
                        :min="0"
                        :step="0.5"
                        :precision="1"
                        style="width: 100%"
                      />
                    </el-form-item>
                  </el-col>
                </el-row>
              </el-form>
            </div>
          </div>

          <el-button type="primary" :icon="Plus" @click="addCrop" style="width: 100%">
            添加作物
          </el-button>
        </el-card>

        <el-card v-if="activeStep === 2" class="step-card">
          <template #header>
            <div class="card-header">
              <span>养殖规模</span>
              <el-tag type="info">步骤 3/6</el-tag>
            </div>
          </template>

          <div class="livestock-summary">
            <el-statistic title="总牲畜数量" :value="totalLivestock" suffix="头" />
            <el-statistic title="牲畜种类" :value="livestock.animals.length" suffix="种" />
          </div>

          <div class="animal-list">
            <div
              v-for="(animal, index) in livestock.animals"
              :key="animal.id"
              class="animal-item"
            >
              <div class="animal-header">
                <span>牲畜 {{ index + 1 }}</span>
                <el-button
                  v-if="livestock.animals.length > 1"
                  type="danger"
                  :icon="Delete"
                  link
                  @click="removeAnimal(animal.id)"
                >
                  删除
                </el-button>
              </div>

              <el-form :model="animal" label-width="100px">
                <el-row :gutter="20">
                  <el-col :span="8">
                    <el-form-item label="牲畜类型">
                      <el-select v-model="animal.type" style="width: 100%">
                        <el-option
                          v-for="item in livestockTypes"
                          :key="item.value"
                          :label="item.label"
                          :value="item.value"
                        />
                      </el-select>
                    </el-form-item>
                  </el-col>
                  <el-col :span="8">
                    <el-form-item label="数量">
                      <el-input-number
                        v-model="animal.count"
                        :min="0"
                        :step="10"
                        style="width: 100%"
                      />
                    </el-form-item>
                  </el-col>
                  <el-col :span="8">
                    <el-form-item label="平均体重 (kg)">
                      <el-input-number
                        v-model="animal.weight"
                        :min="0"
                        :step="50"
                        style="width: 100%"
                      />
                    </el-form-item>
                  </el-col>
                </el-row>
              </el-form>
            </div>
          </div>

          <el-button type="primary" :icon="Plus" @click="addAnimal" style="width: 100%">
            添加牲畜
          </el-button>
        </el-card>

        <el-card v-if="activeStep === 3" class="step-card">
          <template #header>
            <div class="card-header">
              <span>农资投入记录</span>
              <el-tag type="info">步骤 4/6</el-tag>
            </div>
          </template>

          <div class="materials-section">
            <h4>化肥使用</h4>
            <div class="material-list">
              <div
                v-for="(fertilizer, index) in inputMaterials.fertilizers"
                :key="fertilizer.id"
                class="material-item"
              >
                <el-form :model="fertilizer" label-width="80px">
                  <el-row :gutter="20" align="middle">
                    <el-col :span="8">
                      <el-form-item label="化肥类型">
                        <el-select v-model="fertilizer.type" style="width: 100%">
                          <el-option
                            v-for="item in fertilizerTypes"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value"
                          />
                        </el-select>
                      </el-form-item>
                    </el-col>
                    <el-col :span="8">
                      <el-form-item label="用量">
                        <el-input-number
                          v-model="fertilizer.amount"
                          :min="0"
                          :step="10"
                          style="width: 100%"
                        />
                      </el-form-item>
                    </el-col>
                    <el-col :span="6">
                      <el-form-item label="单位">
                        <el-select v-model="fertilizer.unit" style="width: 100%">
                          <el-option label="kg/ha" value="kg/ha" />
                          <el-option label="t/ha" value="t/ha" />
                        </el-select>
                      </el-form-item>
                    </el-col>
                    <el-col :span="2">
                      <el-button
                        v-if="inputMaterials.fertilizers.length > 1"
                        type="danger"
                        :icon="Delete"
                        link
                        @click="removeFertilizer(fertilizer.id)"
                      />
                    </el-col>
                  </el-row>
                </el-form>
              </div>
            </div>
            <el-button type="primary" :icon="Plus" @click="addFertilizer" style="width: 100%; margin-top: 16px;">
              添加化肥
            </el-button>
          </div>

          <el-divider />

          <div class="materials-section">
            <h4>农药使用</h4>
            <div class="material-list">
              <div
                v-for="(pesticide, index) in inputMaterials.pesticides"
                :key="pesticide.id"
                class="material-item"
              >
                <el-form :model="pesticide" label-width="80px">
                  <el-row :gutter="20" align="middle">
                    <el-col :span="8">
                      <el-form-item label="农药类型">
                        <el-select v-model="pesticide.type" style="width: 100%">
                          <el-option
                            v-for="item in pesticideTypes"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value"
                          />
                        </el-select>
                      </el-form-item>
                    </el-col>
                    <el-col :span="8">
                      <el-form-item label="用量">
                        <el-input-number
                          v-model="pesticide.amount"
                          :min="0"
                          :step="1"
                          :precision="1"
                          style="width: 100%"
                        />
                      </el-form-item>
                    </el-col>
                    <el-col :span="6">
                      <el-form-item label="单位">
                        <el-select v-model="pesticide.unit" style="width: 100%">
                          <el-option label="L/ha" value="L/ha" />
                          <el-option label="kg/ha" value="kg/ha" />
                        </el-select>
                      </el-form-item>
                    </el-col>
                    <el-col :span="2">
                      <el-button
                        v-if="inputMaterials.pesticides.length > 1"
                        type="danger"
                        :icon="Delete"
                        link
                        @click="removePesticide(pesticide.id)"
                      />
                    </el-col>
                  </el-row>
                </el-form>
              </div>
            </div>
            <el-button type="primary" :icon="Plus" @click="addPesticide" style="width: 100%; margin-top: 16px;">
              添加农药
            </el-button>
          </div>
        </el-card>

        <el-card v-if="activeStep === 4" class="step-card">
          <template #header>
            <div class="card-header">
              <span>能耗记录</span>
              <el-tag type="info">步骤 5/6</el-tag>
            </div>
          </template>

          <div class="energy-section">
            <h4>灌溉用电</h4>
            <el-form :model="energyConsumption.irrigation" label-width="120px">
              <el-form-item label="用电量 (kWh)">
                <el-input-number
                  v-model="energyConsumption.irrigation.electricity"
                  :min="0"
                  :step="500"
                  style="width: 100%"
                />
              </el-form-item>
            </el-form>
          </div>

          <el-divider />

          <div class="energy-section">
            <h4>农机燃油</h4>
            <el-form :model="energyConsumption.machinery" label-width="120px">
              <el-form-item label="柴油用量 (L)">
                <el-input-number
                  v-model="energyConsumption.machinery.diesel"
                  :min="0"
                  :step="100"
                  style="width: 100%"
                />
              </el-form-item>
            </el-form>
          </div>

          <el-divider />

          <div class="energy-section">
            <h4>其他能耗</h4>
            <div v-if="energyConsumption.other.length > 0" class="other-energy-list">
              <div
                v-for="(item, index) in energyConsumption.other"
                :key="item.id"
                class="energy-item"
              >
                <el-form :model="item" label-width="80px">
                  <el-row :gutter="20" align="middle">
                    <el-col :span="8">
                      <el-form-item label="能源类型">
                        <el-select v-model="item.type" style="width: 100%">
                          <el-option label="煤炭" value="coal" />
                          <el-option label="天然气" value="natural_gas" />
                          <el-option label="汽油" value="gasoline" />
                          <el-option label="液化石油气" value="lpg" />
                        </el-select>
                      </el-form-item>
                    </el-col>
                    <el-col :span="8">
                      <el-form-item label="用量">
                        <el-input-number
                          v-model="item.amount"
                          :min="0"
                          :step="100"
                          style="width: 100%"
                        />
                      </el-form-item>
                    </el-col>
                    <el-col :span="6">
                      <el-form-item label="单位">
                        <el-select v-model="item.unit" style="width: 100%">
                          <el-option label="kg" value="kg" />
                          <el-option label="L" value="L" />
                          <el-option label="m³" value="m3" />
                        </el-select>
                      </el-form-item>
                    </el-col>
                    <el-col :span="2">
                      <el-button
                        type="danger"
                        :icon="Delete"
                        link
                        @click="removeEnergyItem(item.id)"
                      />
                    </el-col>
                  </el-row>
                </el-form>
              </div>
            </div>
            <el-button type="primary" :icon="Plus" @click="addEnergyItem" style="width: 100%; margin-top: 16px;">
              添加其他能耗
            </el-button>
          </div>
        </el-card>

        <el-card v-if="activeStep === 5" class="step-card result-card">
          <template #header>
            <div class="card-header">
              <span>碳排放清单</span>
              <el-tag type="success">步骤 6/6</el-tag>
            </div>
          </template>

          <div v-if="result" class="result-content">
            <div class="result-summary">
              <el-row :gutter="20">
                <el-col :span="6">
                  <el-statistic title="总碳排放" :value="result.totalEmissions" suffix="tCO2e" />
                </el-col>
                <el-col :span="6">
                  <el-statistic title="范围一排放" :value="result.scope1" suffix="tCO2e" />
                </el-col>
                <el-col :span="6">
                  <el-statistic title="范围二排放" :value="result.scope2" suffix="tCO2e" />
                </el-col>
                <el-col :span="6">
                  <el-statistic title="范围三排放" :value="result.scope3" suffix="tCO2e" />
                </el-col>
              </el-row>
            </div>

            <el-divider />

            <div class="result-details">
              <el-row :gutter="20">
                <el-col :span="12">
                  <h4>排放结构分析</h4>
                  <EmissionBreakdown :data="result.emissionBreakdown" />
                </el-col>
                <el-col :span="12">
                  <h4>碳排放强度</h4>
                  <div class="intensity-info">
                    <el-descriptions :column="1" border>
                      <el-descriptions-item label="碳排放强度">
                        {{ result.carbonIntensity }} kg CO2e/kg粮食
                      </el-descriptions-item>
                      <el-descriptions-item label="单位面积排放">
                        {{ result.emissionPerHa }} tCO2e/ha
                      </el-descriptions-item>
                      <el-descriptions-item label="碳汇量">
                        {{ result.carbonSequestration }} tCO2e
                      </el-descriptions-item>
                      <el-descriptions-item label="净排放">
                        {{ result.netEmissions }} tCO2e
                      </el-descriptions-item>
                    </el-descriptions>
                  </div>
                </el-col>
              </el-row>
            </div>

            <el-divider />

            <div class="result-actions">
              <el-button type="primary" :icon="View">查看详情</el-button>
              <el-button :icon="Download">导出报告</el-button>
            </div>
          </div>

          <div v-else class="empty-result">
            <el-empty description="请先填写碳盘查数据并执行核算" />
          </div>
        </el-card>

        <div class="step-actions">
          <el-button v-if="activeStep > 0" @click="prevStep">
            上一步
          </el-button>
          <el-button
            v-if="activeStep < 4"
            type="primary"
            @click="nextStep"
          >
            下一步
          </el-button>
          <el-button
            v-if="activeStep === 4"
            type="primary"
            :loading="loading"
            @click="handleCalculate"
          >
            执行核算
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.carbon-inventory {
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
  gap: 12px;
}

.inventory-content {
  display: flex;
  gap: 24px;
}

.steps-section {
  width: 200px;
  flex-shrink: 0;
}

.steps-section :deep(.el-steps) {
  height: 100%;
}

.steps-section :deep(.el-step) {
  cursor: pointer;
}

.steps-section :deep(.el-step__head):hover {
  color: #409eff;
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

.crop-summary,
.livestock-summary {
  display: flex;
  gap: 40px;
  margin-bottom: 24px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
}

.crop-item,
.animal-item {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 16px;
}

.crop-header,
.animal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  font-weight: 500;
}

.materials-section h4,
.energy-section h4 {
  margin-bottom: 16px;
  color: #303133;
}

.material-item,
.energy-item {
  margin-bottom: 16px;
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
}

.result-card {
  min-height: 400px;
}

.result-summary {
  margin-bottom: 24px;
}

.result-details {
  margin-bottom: 24px;
}

.result-details h4 {
  margin-bottom: 16px;
  color: #303133;
}

.intensity-info {
  margin-top: 16px;
}

.result-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
}

.empty-result {
  padding: 60px 0;
}

.step-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 20px;
}
</style>