<script setup>
import { ref, onMounted, onUnmounted, watch } from "vue";
import * as echarts from "echarts";

const props = defineProps({
  data: {
    type: Object,
    default: () => ({}),
  },
  height: {
    type: String,
    default: "300px",
  },
});

const chartRef = ref(null);
let chart = null;

function generateHeatmapData() {
  const data = [];
  const hours = ["1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"];
  const regions = ["东北", "华北", "华东", "华南", "西南", "西北"];

  for (let i = 0; i < regions.length; i++) {
    for (let j = 0; j < hours.length; j++) {
      const value = Math.round(Math.random() * 500 + 300);
      data.push([j, i, value]);
    }
  }
  return { data, hours, regions };
}

function initChart() {
  if (!chartRef.value) return;

  chart = echarts.init(chartRef.value);

  const { data, hours, regions } = generateHeatmapData();

  const option = {
    tooltip: {
      position: "top",
      formatter: (params) => {
        return `${regions[params.value[1]]} - ${hours[params.value[0]]}<br/>NPP: ${params.value[2]} gC/m²`;
      },
    },
    grid: {
      left: "3%",
      right: "10%",
      bottom: "10%",
      top: "3%",
      containLabel: true,
    },
    xAxis: {
      type: "category",
      data: hours,
      splitArea: {
        show: true,
      },
      axisLine: {
        lineStyle: { color: "#E4E7ED" },
      },
      axisLabel: {
        color: "#909399",
      },
    },
    yAxis: {
      type: "category",
      data: regions,
      splitArea: {
        show: true,
      },
      axisLine: {
        lineStyle: { color: "#E4E7ED" },
      },
      axisLabel: {
        color: "#909399",
      },
    },
    visualMap: {
      min: 300,
      max: 800,
      calculable: true,
      orient: "vertical",
      right: "0%",
      top: "center",
      inRange: {
        color: ["#f7f7f7", "#d4eac7", "#a6d96a", "#66bd63", "#1a9850", "#006837"],
      },
      textStyle: {
        color: "#606266",
      },
    },
    series: [
      {
        name: "NPP",
        type: "heatmap",
        data: data,
        label: {
          show: false,
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

  chart.setOption(option);
}

function handleResize() {
  chart?.resize();
}

watch(
  () => props.data,
  () => {
    if (chart) {
      chart.dispose();
    }
    initChart();
  },
  { deep: true }
);

onMounted(() => {
  initChart();
  window.addEventListener("resize", handleResize);
});

onUnmounted(() => {
  window.removeEventListener("resize", handleResize);
  chart?.dispose();
});
</script>

<template>
  <div ref="chartRef" :style="{ height: height, width: '100%' }"></div>
</template>