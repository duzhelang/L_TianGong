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

const defaultData = [
  { name: "耕地", value: 35, color: "#67C23A" },
  { name: "林地", value: 28, color: "#409EFF" },
  { name: "草地", value: 15, color: "#E6A23C" },
  { name: "水域", value: 8, color: "#909399" },
  { name: "建设用地", value: 10, color: "#F56C6C" },
  { name: "未利用地", value: 4, color: "#C0C4CC" },
];

function initChart() {
  if (!chartRef.value) return;

  chart = echarts.init(chartRef.value);

  const chartData = props.data?.series ? props.data.series : defaultData;

  const option = {
    tooltip: {
      trigger: "item",
      formatter: "{a} <br/>{b}: {c} ({d}%)",
    },
    legend: {
      orient: "vertical",
      right: "5%",
      top: "center",
      itemWidth: 10,
      itemHeight: 10,
      textStyle: {
        color: "#606266",
      },
    },
    series: [
      {
        name: "土地利用",
        type: "pie",
        radius: ["40%", "70%"],
        center: ["40%", "50%"],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: "#fff",
          borderWidth: 2,
        },
        label: {
          show: false,
          position: "center",
        },
        emphasis: {
          label: {
            show: true,
            fontSize: "18",
            fontWeight: "bold",
          },
        },
        labelLine: {
          show: false,
        },
        data: chartData.map((item) => ({
          value: item.value,
          name: item.name,
          itemStyle: {
            color: item.color,
          },
        })),
      },
    ],
  };

  chart.setOption(option);

  chart.on("click", (params) => {
    console.log("点击了:", params.name);
  });
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