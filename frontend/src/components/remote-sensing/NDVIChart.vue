<script setup>
import { ref, onMounted, watch } from "vue";
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

const defaultData = {
  xAxis: ["1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"],
  series: [
    {
      name: "2024年",
      data: [0.35, 0.42, 0.55, 0.68, 0.78, 0.85, 0.82, 0.79, 0.72, 0.65, 0.48, 0.38],
      color: "#67C23A",
    },
    {
      name: "2023年",
      data: [0.32, 0.38, 0.52, 0.65, 0.75, 0.82, 0.78, 0.75, 0.68, 0.62, 0.45, 0.35],
      color: "#C0C4CC",
    },
  ],
};

function initChart() {
  if (!chartRef.value) return;

  chart = echarts.init(chartRef.value);

  const chartData = props.data?.xAxis ? props.data : defaultData;

  const option = {
    tooltip: {
      trigger: "axis",
      axisPointer: {
        type: "cross",
      },
    },
    legend: {
      data: chartData.series.map((s) => s.name),
      top: 0,
    },
    grid: {
      left: "3%",
      right: "4%",
      bottom: "3%",
      top: "15%",
      containLabel: true,
    },
    xAxis: {
      type: "category",
      data: chartData.xAxis,
      axisLine: {
        lineStyle: { color: "#E4E7ED" },
      },
      axisLabel: {
        color: "#909399",
      },
    },
    yAxis: {
      type: "value",
      name: "NDVI",
      min: 0,
      max: 1,
      axisLine: {
        show: false,
      },
      axisTick: {
        show: false,
      },
      axisLabel: {
        color: "#909399",
      },
      splitLine: {
        lineStyle: { color: "#EBEEF5" },
      },
    },
    series: chartData.series.map((s) => ({
      name: s.name,
      type: "line",
      data: s.data,
      smooth: true,
      symbol: "circle",
      symbolSize: 8,
      itemStyle: {
        color: s.color,
      },
      lineStyle: {
        width: 3,
      },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: s.color + "40" },
          { offset: 1, color: s.color + "10" },
        ]),
      },
      markPoint: {
        data: [
          { type: "max", name: "最大值" },
          { type: "min", name: "最小值" },
        ],
        symbolSize: 45,
      },
      markLine: {
        data: [{ type: "average", name: "平均值" }],
      },
    })),
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