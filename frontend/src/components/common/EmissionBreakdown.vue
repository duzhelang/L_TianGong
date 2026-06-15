<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from "vue";
import * as echarts from "echarts";

const props = defineProps({
  data: {
    type: Array,
    default: () => [],
  },
  height: {
    type: String,
    default: "300px",
  },
  showDrilldown: {
    type: Boolean,
    default: true,
  },
});

const emit = defineEmits(["drilldown"]);

const chartRef = ref(null);
let chart = null;

const defaultColors = [
  "#409EFF",
  "#67C23A",
  "#E6A23C",
  "#F56C6C",
  "#909399",
  "#8B5CF6",
  "#EC4899",
  "#14B8A6",
];

const selectedCategory = ref(null);
const drilldownData = ref([]);

const chartOption = computed(() => {
  const data = selectedCategory.value ? drilldownData.value : props.data;

  return {
    tooltip: {
      trigger: "item",
      formatter: (params) => {
        return `${params.name}<br/>
                排放量: ${params.value} tCO2e<br/>
                占比: ${params.percent}%`;
      },
    },
    legend: {
      orient: "vertical",
      right: "5%",
      top: "center",
      itemGap: 12,
      textStyle: {
        color: "#606266",
        fontSize: 12,
      },
    },
    series: [
      {
        name: "排放结构",
        type: "pie",
        radius: ["35%", "65%"],
        center: ["35%", "50%"],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 8,
          borderColor: "#fff",
          borderWidth: 2,
        },
        label: {
          show: true,
          position: "outside",
          formatter: "{b}\n{d}%",
          fontSize: 11,
          color: "#606266",
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 13,
            fontWeight: "bold",
          },
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: "rgba(0, 0, 0, 0.2)",
          },
        },
        labelLine: {
          show: true,
          length: 10,
          length2: 15,
        },
        data: data.map((d, index) => ({
          value: d.value,
          name: d.name,
          itemStyle: {
            color: d.color || defaultColors[index % defaultColors.length],
          },
        })),
      },
    ],
  };
});

function initChart() {
  if (chartRef.value) {
    chart = echarts.init(chartRef.value);
    chart.setOption(chartOption.value);

    if (props.showDrilldown) {
      chart.on("click", (params) => {
        handleDrilldown(params.name);
      });
    }
  }
}

function updateChart() {
  if (chart) {
    chart.setOption(chartOption.value, true);
  }
}

function handleDrilldown(category) {
  if (selectedCategory.value === category) {
    selectedCategory.value = null;
    drilldownData.value = [];
  } else {
    selectedCategory.value = category;
    drilldownData.value = getDrilldownData(category);
  }

  emit("drilldown", {
    category,
    data: drilldownData.value,
  });
}

function getDrilldownData(category) {
  const drilldownMap = {
    化肥施用: [
      { name: "氮肥", value: 60 },
      { name: "磷肥", value: 25 },
      { name: "钾肥", value: 10 },
      { name: "复合肥", value: 5 },
    ],
    稻田甲烷: [
      { name: "单季稻", value: 55 },
      { name: "双季稻", value: 35 },
      { name: "再生稻", value: 10 },
    ],
    农机作业: [
      { name: "拖拉机", value: 45 },
      { name: "收割机", value: 30 },
      { name: "插秧机", value: 15 },
      { name: "其他", value: 10 },
    ],
    灌溉用电: [
      { name: "水泵", value: 70 },
      { name: "喷灌设备", value: 20 },
      { name: "其他", value: 10 },
    ],
    其他: [
      { name: "农药", value: 40 },
      { name: "种子", value: 30 },
      { name: "包装", value: 20 },
      { name: "运输", value: 10 },
    ],
  };

  return drilldownMap[category] || [];
}

function goBack() {
  selectedCategory.value = null;
  drilldownData.value = [];
  updateChart();
}

function handleResize() {
  if (chart) {
    chart.resize();
  }
}

onMounted(() => {
  initChart();
  window.addEventListener("resize", handleResize);
});

onUnmounted(() => {
  window.removeEventListener("resize", handleResize);
  if (chart) {
    chart.dispose();
    chart = null;
  }
});

watch(
  () => props.data,
  () => {
    selectedCategory.value = null;
    drilldownData.value = [];
    updateChart();
  },
  { deep: true }
);
</script>

<template>
  <div class="emission-breakdown" :style="{ height }">
    <div class="breakdown-header">
      <div class="header-left">
        <el-button
          v-if="selectedCategory"
          type="primary"
          link
          @click="goBack"
        >
          <el-icon><ArrowLeft /></el-icon> 返回
        </el-button>
        <span class="current-category" v-if="selectedCategory">
          {{ selectedCategory }} - 细分结构
        </span>
      </div>
    </div>

    <div
      ref="chartRef"
      class="chart-container"
      :style="{ height: selectedCategory ? 'calc(100% - 40px)' : '100%' }"
    ></div>

    <div v-if="showDrilldown && !selectedCategory" class="drilldown-hint">
      <el-icon><InfoFilled /></el-icon>
      <span>点击扇区查看详细数据</span>
    </div>
  </div>
</template>

<style scoped>
.emission-breakdown {
  width: 100%;
  position: relative;
}

.breakdown-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  min-height: 32px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.current-category {
  font-size: 13px;
  color: #606266;
  font-weight: 500;
}

.chart-container {
  width: 100%;
}

.drilldown-hint {
  position: absolute;
  bottom: 8px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #909399;
  background: rgba(255, 255, 255, 0.8);
  padding: 4px 12px;
  border-radius: 12px;
}
</style>