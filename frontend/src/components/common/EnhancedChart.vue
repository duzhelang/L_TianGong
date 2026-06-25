<script setup>
import { ref, onMounted, onUnmounted, watch, computed, nextTick } from "vue";
import * as echarts from "echarts";

const props = defineProps({
  type: {
    type: String,
    default: "bar",
    validator: (value) => ["line", "bar", "pie", "radar", "scatter", "heatmap", "sankey", "gauge"].includes(value),
  },
  data: {
    type: Object,
    default: () => ({}),
  },
  height: {
    type: String,
    default: "350px",
  },
  title: {
    type: String,
    default: "",
  },
  loading: {
    type: Boolean,
    default: false,
  },
  theme: {
    type: String,
    default: "default",
  },
});

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

const chartOption = computed(() => {
  if (!props.data) return {};

  switch (props.type) {
    case "line":
      return getLineOption();
    case "bar":
      return getBarOption();
    case "pie":
      return getPieOption();
    case "radar":
      return getRadarOption();
    case "scatter":
      return getScatterOption();
    case "heatmap":
      return getHeatmapOption();
    case "sankey":
      return getSankeyOption();
    case "gauge":
      return getGaugeOption();
    default:
      return getBarOption();
  }
});

function getLineOption() {
  const { xAxis, series } = props.data;
  return {
    tooltip: {
      trigger: "axis",
      axisPointer: {
        type: "cross",
        label: {
          backgroundColor: "#6a7985",
        },
      },
    },
    legend: {
      data: series.map((s) => s.name),
      top: 0,
    },
    grid: {
      left: "3%",
      right: "4%",
      bottom: "3%",
      containLabel: true,
    },
    xAxis: {
      type: "category",
      boundaryGap: false,
      data: xAxis,
    },
    yAxis: {
      type: "value",
      name: "tCO2e",
    },
    series: series.map((s, index) => ({
      name: s.name,
      type: "line",
      stack: "Total",
      smooth: true,
      lineStyle: {
        width: 2,
      },
      showSymbol: false,
      areaStyle: {
        opacity: 0.15,
      },
      emphasis: {
        focus: "series",
      },
      data: s.data,
      itemStyle: {
        color: s.color || defaultColors[index % defaultColors.length],
      },
    })),
  };
}

function getBarOption() {
  const { xAxis, series } = props.data;
  return {
    tooltip: {
      trigger: "axis",
      axisPointer: {
        type: "shadow",
      },
    },
    legend: {
      data: series.map((s) => s.name),
      top: 0,
    },
    grid: {
      left: "3%",
      right: "4%",
      bottom: "3%",
      containLabel: true,
    },
    xAxis: {
      type: "category",
      data: xAxis,
    },
    yAxis: {
      type: "value",
      name: "tCO2e",
    },
    series: series.map((s, index) => ({
      name: s.name,
      type: "bar",
      barWidth: "40%",
      data: s.data,
      itemStyle: {
        color: s.color || defaultColors[index % defaultColors.length],
        borderRadius: [4, 4, 0, 0],
      },
    })),
  };
}

function getPieOption() {
  const data = Array.isArray(props.data) ? props.data : props.data.series || [];
  return {
    tooltip: {
      trigger: "item",
      formatter: "{a} <br/>{b}: {c} ({d}%)",
    },
    legend: {
      orient: "vertical",
      left: "left",
      top: "center",
      data: data.map((d) => d.name),
    },
    series: [
      {
        name: "排放结构",
        type: "pie",
        radius: ["40%", "70%"],
        center: ["60%", "50%"],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: "#fff",
          borderWidth: 2,
        },
        label: {
          show: true,
          formatter: "{b}: {d}%",
        },
        emphasis: {
          label: {
            show: true,
            fontSize: "14",
            fontWeight: "bold",
          },
        },
        labelLine: {
          show: true,
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
}

function getRadarOption() {
  const { indicator, series } = props.data;
  return {
    tooltip: {
      trigger: "item",
    },
    legend: {
      data: series.map((s) => s.name),
      top: 0,
    },
    radar: {
      indicator: indicator,
      shape: "circle",
      splitNumber: 5,
      axisName: {
        color: "#606266",
      },
      splitLine: {
        lineStyle: {
          color: [
            "rgba(64, 158, 255, 0.1)",
            "rgba(64, 158, 255, 0.2)",
            "rgba(64, 158, 255, 0.3)",
            "rgba(64, 158, 255, 0.4)",
            "rgba(64, 158, 255, 0.5)",
          ],
        },
      },
      splitArea: {
        show: false,
      },
      axisLine: {
        lineStyle: {
          color: "rgba(64, 158, 255, 0.5)",
        },
      },
    },
    series: series.map((s, index) => ({
      name: s.name,
      type: "radar",
      lineStyle: {
        width: 2,
      },
      areaStyle: {
        opacity: 0.15,
      },
      data: [
        {
          value: s.data,
          name: s.name,
          itemStyle: {
            color: defaultColors[index % defaultColors.length],
          },
          areaStyle: {
            color: defaultColors[index % defaultColors.length],
          },
        },
      ],
    })),
  };
}

function getScatterOption() {
  const { series } = props.data;
  return {
    tooltip: {
      trigger: "item",
      formatter: "{a}<br/>{b}: {c}",
    },
    legend: {
      data: series.map((s) => s.name),
      top: 0,
    },
    grid: {
      left: "3%",
      right: "4%",
      bottom: "3%",
      containLabel: true,
    },
    xAxis: {
      type: "value",
      name: "X",
    },
    yAxis: {
      type: "value",
      name: "Y",
    },
    series: series.map((s, index) => ({
      name: s.name,
      type: "scatter",
      data: s.data,
      symbolSize: s.symbolSize || 10,
      itemStyle: {
        color: s.color || defaultColors[index % defaultColors.length],
      },
    })),
  };
}

function getHeatmapOption() {
  const { xAxis, yAxis, data } = props.data;
  return {
    tooltip: {
      position: "top",
    },
    grid: {
      left: "3%",
      right: "4%",
      bottom: "3%",
      containLabel: true,
    },
    xAxis: {
      type: "category",
      data: xAxis,
      splitArea: {
        show: true,
      },
    },
    yAxis: {
      type: "category",
      data: yAxis,
      splitArea: {
        show: true,
      },
    },
    visualMap: {
      min: 0,
      max: 100,
      calculable: true,
      orient: "horizontal",
      left: "center",
      bottom: "0%",
    },
    series: [
      {
        name: "热力图",
        type: "heatmap",
        data: data,
        label: {
          show: true,
        },
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowColor: "rgba(0, 0, 0, 0.5)",
          },
        },
      },
    ],
  };
}

function getSankeyOption() {
  const { nodes, links } = props.data;
  return {
    tooltip: {
      trigger: "item",
      triggerOn: "mousemove",
    },
    series: [
      {
        type: "sankey",
        data: nodes,
        links: links,
        emphasis: {
          focus: "adjacency",
        },
        lineStyle: {
          color: "gradient",
          curveness: 0.5,
        },
      },
    ],
  };
}

function getGaugeOption() {
  const { value, name } = props.data;
  return {
    series: [
      {
        name: name || "完成率",
        type: "gauge",
        progress: {
          show: true,
        },
        detail: {
          valueAnimation: true,
          formatter: "{value}%",
          color: "#409EFF",
        },
        data: [
          {
            value: value,
            name: name || "完成率",
          },
        ],
      },
    ],
  };
}

function initChart() {
  if (chartRef.value) {
    chart = echarts.init(chartRef.value, props.theme);
    chart.setOption(chartOption.value);
  }
}

function updateChart() {
  if (chart) {
    chart.setOption(chartOption.value, true);
  }
}

function handleResize() {
  if (chart) {
    chart.resize();
  }
}

onMounted(() => {
  nextTick(() => {
    initChart();
  });
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
    updateChart();
  },
  { deep: true }
);

watch(
  () => props.type,
  () => {
    if (chart) {
      chart.dispose();
    }
    initChart();
  }
);

watch(
  () => props.theme,
  () => {
    if (chart) {
      chart.dispose();
    }
    initChart();
  }
);
</script>

<template>
  <div class="enhanced-chart" :style="{ height }">
    <div v-if="title" class="chart-title">{{ title }}</div>
    <div v-if="loading" class="chart-loading">
      <el-icon class="is-loading" :size="32"><Loading /></el-icon>
      <span>加载中...</span>
    </div>
    <div
      v-else
      ref="chartRef"
      class="chart-container"
      :style="{ height: title ? `calc(100% - 40px)` : '100%' }"
    ></div>
  </div>
</template>

<style scoped>
.enhanced-chart {
  width: 100%;
  position: relative;
}

.chart-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
  line-height: 1;
}

.chart-container {
  width: 100%;
}

.chart-loading {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  color: #909399;
}
</style>
