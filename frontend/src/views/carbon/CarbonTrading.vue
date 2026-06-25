<template>
  <div class="carbon-trading">
    <el-tabs v-model="activeTab" type="border-card">
      <!-- 碳资产管理 -->
      <el-tab-pane label="碳资产管理" name="assets">
        <div class="stats-row">
          <el-row :gutter="20">
            <el-col :span="6">
              <div class="stat-card gradient-blue">
                <div class="stat-icon">
                  <el-icon><Coin /></el-icon>
                </div>
                <div class="stat-content">
                  <div class="stat-value">{{ assetStats.total }}</div>
                  <div class="stat-label">总资产数</div>
                </div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-card gradient-green">
                <div class="stat-icon">
                  <el-icon><CircleCheck /></el-icon>
                </div>
                <div class="stat-content">
                  <div class="stat-value">{{ assetStats.active }}</div>
                  <div class="stat-label">活跃资产</div>
                </div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-card gradient-orange">
                <div class="stat-icon">
                  <el-icon><Remove /></el-icon>
                </div>
                <div class="stat-content">
                  <div class="stat-value">{{ assetStats.retired }}</div>
                  <div class="stat-label">已退役资产</div>
                </div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-card gradient-purple">
                <div class="stat-icon">
                  <el-icon><Money /></el-icon>
                </div>
                <div class="stat-content">
                  <div class="stat-value">{{ formatCurrency(assetStats.totalValue) }}</div>
                  <div class="stat-label">总资产价值</div>
                </div>
              </div>
            </el-col>
          </el-row>
        </div>

        <div class="filter-bar">
          <el-row :gutter="20" align="middle">
            <el-col :span="6">
              <el-select v-model="assetFilter.type" placeholder="资产类型" clearable>
                <el-option label="CCER" value="CCER" />
                <el-option label="VCS" value="VCS" />
                <el-option label="GoldStandard" value="GoldStandard" />
              </el-select>
            </el-col>
            <el-col :span="6">
              <el-select v-model="assetFilter.status" placeholder="状态" clearable>
                <el-option label="活跃" value="ACTIVE" />
                <el-option label="已退役" value="RETIRED" />
                <el-option label="已交易" value="TRADED" />
              </el-select>
            </el-col>
            <el-col :span="8">
              <el-input v-model="assetFilter.keyword" placeholder="搜索资产编码或名称" clearable>
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
            </el-col>
            <el-col :span="4">
              <el-button type="primary" v-permission="'trading:create'" @click="showAssetDialog()">
                <el-icon><Plus /></el-icon>
                新增资产
              </el-button>
            </el-col>
          </el-row>
        </div>

        <el-table :data="filteredAssets" v-loading="loading" stripe>
          <el-table-column prop="assetCode" label="资产编码" width="140" />
          <el-table-column prop="assetName" label="资产名称" min-width="150" />
          <el-table-column prop="assetType" label="类型" width="120">
            <template #default="{ row }">
              <el-tag :type="getAssetTypeTag(row.assetType)">{{ row.assetType }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="120" align="right">
            <template #default="{ row }">
              {{ formatNumber(row.quantity) }}
            </template>
          </el-table-column>
          <el-table-column prop="unit" label="单位" width="100" />
          <el-table-column prop="vintageYear" label="年份" width="100" align="center" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusTag(row.status)">{{ getStatusLabel(row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" @click="viewAsset(row)">查看</el-button>
              <el-button link type="primary" v-permission="'trading:edit'" @click="showAssetDialog(row)">编辑</el-button>
              <el-button link type="success" v-permission="'trading:trade'" @click="tradeAsset(row)" :disabled="row.status !== 'ACTIVE'">交易</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination">
          <el-pagination
            v-model:current-page="assetPagination.page"
            v-model:page-size="assetPagination.size"
            :page-sizes="[10, 20, 50, 100]"
            :total="assetPagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleAssetSizeChange"
            @current-change="handleAssetPageChange"
          />
        </div>
      </el-tab-pane>

      <!-- 交易市场 -->
      <el-tab-pane label="交易市场" name="market">
        <div class="market-section">
          <h3>实时碳价行情</h3>
          <el-row :gutter="20">
            <el-col :span="8" v-for="price in carbonPrices" :key="price.id">
              <div class="price-card">
                <div class="price-header">
                  <span class="exchange">{{ price.exchange }}</span>
                  <span class="product">{{ price.productName }}</span>
                </div>
                <div class="price-main">
                  <span class="current-price" :class="price.change >= 0 ? 'up' : 'down'">
                    {{ formatCurrency(price.price) }}
                  </span>
                  <span class="change" :class="price.change >= 0 ? 'up' : 'down'">
                    {{ price.change >= 0 ? '+' : '' }}{{ price.change }}%
                  </span>
                </div>
                <div class="price-detail">
                  <div class="detail-item">
                    <span>开盘</span>
                    <span>{{ formatCurrency(price.openPrice) }}</span>
                  </div>
                  <div class="detail-item">
                    <span>收盘</span>
                    <span>{{ formatCurrency(price.closePrice) }}</span>
                  </div>
                  <div class="detail-item">
                    <span>最高</span>
                    <span>{{ formatCurrency(price.highPrice) }}</span>
                  </div>
                  <div class="detail-item">
                    <span>最低</span>
                    <span>{{ formatCurrency(price.lowPrice) }}</span>
                  </div>
                </div>
                <div class="price-footer">
                  <span>成交量: {{ formatNumber(price.volume) }}</span>
                  <span>成交额: {{ formatCurrency(price.turnover) }}</span>
                </div>
              </div>
            </el-col>
          </el-row>
        </div>

        <div class="chart-section">
          <el-row :gutter="20">
            <el-col :span="16">
              <el-card>
                <template #header>
                  <span>碳价趋势图</span>
                </template>
                <div ref="priceChartRef" class="chart-container"></div>
              </el-card>
            </el-col>
            <el-col :span="8">
              <el-card>
                <template #header>
                  <span>交易下单</span>
                </template>
                <el-form :model="orderForm" label-width="80px">
                  <el-form-item label="选择资产">
                    <el-select v-model="orderForm.assetId" placeholder="请选择资产">
                      <el-option
                        v-for="asset in activeAssets"
                        :key="asset.id"
                        :label="`${asset.assetName} (${asset.assetCode})`"
                        :value="asset.id"
                      />
                    </el-select>
                  </el-form-item>
                  <el-form-item label="订单类型">
                    <el-radio-group v-model="orderForm.orderType">
                      <el-radio-button value="BUY">买入</el-radio-button>
                      <el-radio-button value="SELL">卖出</el-radio-button>
                    </el-radio-group>
                  </el-form-item>
                  <el-form-item label="数量(tCO2e)">
                    <el-input-number v-model="orderForm.quantity" :min="1" :step="100" />
                  </el-form-item>
                  <el-form-item label="价格(元)">
                    <el-input-number v-model="orderForm.price" :min="0.01" :step="0.1" :precision="2" />
                  </el-form-item>
                  <el-form-item label="总金额">
                    <span class="total-amount">{{ formatCurrency(orderForm.quantity * orderForm.price) }}</span>
                  </el-form-item>
                  <el-form-item>
                    <el-button type="primary" v-permission="'trading:trade'" @click="submitOrder" :loading="orderLoading">
                      提交订单
                    </el-button>
                  </el-form-item>
                </el-form>
              </el-card>
            </el-col>
          </el-row>
        </div>

        <div class="recent-transactions">
          <el-card>
            <template #header>
              <span>最近交易记录</span>
            </template>
            <el-table :data="recentTransactions" stripe>
              <el-table-column prop="transactionCode" label="交易编码" width="140" />
              <el-table-column prop="assetName" label="资产" min-width="150" />
              <el-table-column prop="orderType" label="类型" width="80">
                <template #default="{ row }">
                  <el-tag :type="row.orderType === 'BUY' ? 'success' : 'danger'">
                    {{ row.orderType === 'BUY' ? '买入' : '卖出' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="quantity" label="数量" width="120" align="right">
                <template #default="{ row }">
                  {{ formatNumber(row.quantity) }}
                </template>
              </el-table-column>
              <el-table-column prop="price" label="价格" width="120" align="right">
                <template #default="{ row }">
                  {{ formatCurrency(row.price) }}
                </template>
              </el-table-column>
              <el-table-column prop="totalAmount" label="总金额" width="140" align="right">
                <template #default="{ row }">
                  {{ formatCurrency(row.totalAmount) }}
                </template>
              </el-table-column>
              <el-table-column prop="status" label="状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="getTransactionStatusTag(row.status)">
                    {{ getTransactionStatusLabel(row.status) }}
                  </el-tag>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </div>
      </el-tab-pane>

      <!-- 交易记录 -->
      <el-tab-pane label="交易记录" name="transactions">
        <div class="filter-bar">
          <el-row :gutter="20" align="middle">
            <el-col :span="4">
              <el-select v-model="transactionFilter.orderType" placeholder="订单类型" clearable>
                <el-option label="买入" value="BUY" />
                <el-option label="卖出" value="SELL" />
              </el-select>
            </el-col>
            <el-col :span="4">
              <el-select v-model="transactionFilter.status" placeholder="状态" clearable>
                <el-option label="待处理" value="PENDING" />
                <el-option label="已提交" value="SUBMITTED" />
                <el-option label="已匹配" value="MATCHED" />
                <el-option label="已结算" value="SETTLED" />
                <el-option label="已取消" value="CANCELLED" />
              </el-select>
            </el-col>
            <el-col :span="8">
              <el-date-picker
                v-model="transactionFilter.dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
              />
            </el-col>
            <el-col :span="4">
              <el-input v-model="transactionFilter.keyword" placeholder="搜索交易编码" clearable>
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
            </el-col>
            <el-col :span="4">
              <el-button type="primary" @click="fetchTransactions">查询</el-button>
            </el-col>
          </el-row>
        </div>

        <el-table :data="filteredTransactions" v-loading="loading" stripe>
          <el-table-column prop="transactionCode" label="交易编码" width="140" />
          <el-table-column prop="assetName" label="资产" min-width="150" />
          <el-table-column prop="orderType" label="类型" width="80">
            <template #default="{ row }">
              <el-tag :type="row.orderType === 'BUY' ? 'success' : 'danger'">
                {{ row.orderType === 'BUY' ? '买入' : '卖出' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量(tCO2e)" width="140" align="right">
            <template #default="{ row }">
              {{ formatNumber(row.quantity) }}
            </template>
          </el-table-column>
          <el-table-column prop="price" label="价格(元)" width="120" align="right">
            <template #default="{ row }">
              {{ formatCurrency(row.price) }}
            </template>
          </el-table-column>
          <el-table-column prop="totalAmount" label="总金额(元)" width="140" align="right">
            <template #default="{ row }">
              {{ formatCurrency(row.totalAmount) }}
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getTransactionStatusTag(row.status)">
                {{ getTransactionStatusLabel(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="submittedAt" label="提交时间" width="180">
            <template #default="{ row }">
              {{ formatDateTime(row.submittedAt) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" @click="viewTransaction(row)">详情</el-button>
              <el-button
                link
                type="danger"
                v-if="row.status === 'PENDING' || row.status === 'SUBMITTED'"
                @click="cancelTransaction(row)"
              >
                取消
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination">
          <el-pagination
            v-model:current-page="transactionPagination.page"
            v-model:page-size="transactionPagination.size"
            :page-sizes="[10, 20, 50, 100]"
            :total="transactionPagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleTransactionSizeChange"
            @current-change="handleTransactionPageChange"
          />
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 资产详情对话框 -->
    <el-dialog v-model="assetDetailVisible" title="资产详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="资产编码">{{ currentAsset.assetCode }}</el-descriptions-item>
        <el-descriptions-item label="资产名称">{{ currentAsset.assetName }}</el-descriptions-item>
        <el-descriptions-item label="资产类型">{{ currentAsset.assetType }}</el-descriptions-item>
        <el-descriptions-item label="数量">{{ formatNumber(currentAsset.quantity) }} {{ currentAsset.unit }}</el-descriptions-item>
        <el-descriptions-item label="年份">{{ currentAsset.vintageYear }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ getStatusLabel(currentAsset.status) }}</el-descriptions-item>
        <el-descriptions-item label="签发日期">{{ formatDate(currentAsset.issuanceDate) }}</el-descriptions-item>
        <el-descriptions-item label="到期日期">{{ formatDate(currentAsset.expiryDate) }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 新增/编辑资产对话框 -->
    <el-dialog v-model="assetDialogVisible" :title="assetDialogTitle" width="600px">
      <el-form :model="assetForm" label-width="100px" :rules="assetRules" ref="assetFormRef">
        <el-form-item label="资产编码" prop="assetCode">
          <el-input v-model="assetForm.assetCode" placeholder="请输入资产编码" />
        </el-form-item>
        <el-form-item label="资产名称" prop="assetName">
          <el-input v-model="assetForm.assetName" placeholder="请输入资产名称" />
        </el-form-item>
        <el-form-item label="资产类型" prop="assetType">
          <el-select v-model="assetForm.assetType" placeholder="请选择资产类型">
            <el-option label="CCER" value="CCER" />
            <el-option label="VCS" value="VCS" />
            <el-option label="GoldStandard" value="GoldStandard" />
          </el-select>
        </el-form-item>
        <el-form-item label="数量" prop="quantity">
          <el-input-number v-model="assetForm.quantity" :min="1" :step="100" />
        </el-form-item>
        <el-form-item label="单位">
          <el-input v-model="assetForm.unit" disabled />
        </el-form-item>
        <el-form-item label="年份" prop="vintageYear">
          <el-date-picker v-model="assetForm.vintageYear" type="year" placeholder="选择年份" />
        </el-form-item>
        <el-form-item label="签发日期" prop="issuanceDate">
          <el-date-picker v-model="assetForm.issuanceDate" type="date" placeholder="选择签发日期" />
        </el-form-item>
        <el-form-item label="到期日期" prop="expiryDate">
          <el-date-picker v-model="assetForm.expiryDate" type="date" placeholder="选择到期日期" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assetDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveAsset" :loading="loading">保存</el-button>
      </template>
    </el-dialog>

    <!-- 交易详情对话框 -->
    <el-dialog v-model="transactionDetailVisible" title="交易详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="交易编码">{{ currentTransaction.transactionCode }}</el-descriptions-item>
        <el-descriptions-item label="资产">{{ currentTransaction.assetName }}</el-descriptions-item>
        <el-descriptions-item label="订单类型">
          <el-tag :type="currentTransaction.orderType === 'BUY' ? 'success' : 'danger'">
            {{ currentTransaction.orderType === 'BUY' ? '买入' : '卖出' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="数量">{{ formatNumber(currentTransaction.quantity) }} tCO2e</el-descriptions-item>
        <el-descriptions-item label="价格">{{ formatCurrency(currentTransaction.price) }}</el-descriptions-item>
        <el-descriptions-item label="总金额">{{ formatCurrency(currentTransaction.totalAmount) }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getTransactionStatusTag(currentTransaction.status)">
            {{ getTransactionStatusLabel(currentTransaction.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="交易所">{{ currentTransaction.exchange }}</el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ formatDateTime(currentTransaction.submittedAt) }}</el-descriptions-item>
        <el-descriptions-item label="匹配时间">{{ formatDateTime(currentTransaction.matchedAt) }}</el-descriptions-item>
        <el-descriptions-item label="结算时间">{{ formatDateTime(currentTransaction.settledAt) }}</el-descriptions-item>
      </el-descriptions>
      <div class="status-flow">
        <el-steps :active="getTransactionStep(currentTransaction.status)" finish-status="success">
          <el-step title="待处理" />
          <el-step title="已提交" />
          <el-step title="已匹配" />
          <el-step title="已结算" />
        </el-steps>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted, watch } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus, Search, Coin, CircleCheck, Remove, Money } from "@element-plus/icons-vue";
import * as echarts from "echarts";
import {
  getAssets,
  getAssetById,
  createAsset,
  updateAsset,
  getTransactions,
  createTransaction,
  cancelTransaction as cancelTransactionApi,
  getCarbonPrices,
  getCarbonPriceTrend,
  getTradingStats,
} from "@/api/trading";

const activeTab = ref("assets");
const loading = ref(false);
const orderLoading = ref(false);
const priceChartRef = ref(null);
const assetFormRef = ref(null);
const priceChart = ref(null);

const assetStats = reactive({
  total: 0,
  active: 0,
  retired: 0,
  totalValue: 0,
});

const assetFilter = reactive({
  type: "",
  status: "",
  keyword: "",
});

const assetPagination = reactive({
  page: 1,
  size: 10,
  total: 0,
});

const transactionFilter = reactive({
  orderType: "",
  status: "",
  dateRange: null,
  keyword: "",
});

const transactionPagination = reactive({
  page: 1,
  size: 10,
  total: 0,
});

const assets = ref([]);
const transactions = ref([]);
const carbonPrices = ref([]);
const recentTransactions = ref([]);

const assetDetailVisible = ref(false);
const assetDialogVisible = ref(false);
const assetDialogTitle = ref("新增资产");
const transactionDetailVisible = ref(false);

const currentAsset = reactive({
  id: null,
  assetCode: "",
  assetName: "",
  assetType: "",
  quantity: 0,
  unit: "tCO2e",
  vintageYear: "",
  issuanceDate: "",
  expiryDate: "",
  status: "",
});

const assetForm = reactive({
  id: null,
  assetCode: "",
  assetName: "",
  assetType: "CCER",
  quantity: 1000,
  unit: "tCO2e",
  vintageYear: "",
  issuanceDate: "",
  expiryDate: "",
});

const currentTransaction = reactive({
  id: null,
  transactionCode: "",
  assetName: "",
  orderType: "",
  quantity: 0,
  price: 0,
  totalAmount: 0,
  status: "",
  exchange: "",
  submittedAt: "",
  matchedAt: "",
  settledAt: "",
});

const orderForm = reactive({
  assetId: null,
  orderType: "BUY",
  quantity: 1000,
  price: 50,
});

const assetRules = {
  assetCode: [{ required: true, message: "请输入资产编码", trigger: "blur" }],
  assetName: [{ required: true, message: "请输入资产名称", trigger: "blur" }],
  assetType: [{ required: true, message: "请选择资产类型", trigger: "change" }],
  quantity: [{ required: true, message: "请输入数量", trigger: "blur" }],
  vintageYear: [{ required: true, message: "请选择年份", trigger: "change" }],
  issuanceDate: [{ required: true, message: "请选择签发日期", trigger: "change" }],
};

const mockAssets = [
  {
    id: 1,
    assetCode: "CCER-2024-001",
    assetName: "北京林业碳汇项目",
    assetType: "CCER",
    quantity: 50000,
    unit: "tCO2e",
    vintageYear: 2024,
    issuanceDate: "2024-01-15",
    expiryDate: "2034-01-15",
    status: "ACTIVE",
    ownerId: 1,
  },
  {
    id: 2,
    assetCode: "VCS-2023-002",
    assetName: "河北风电减排项目",
    assetType: "VCS",
    quantity: 30000,
    unit: "tCO2e",
    vintageYear: 2023,
    issuanceDate: "2023-06-20",
    expiryDate: "2033-06-20",
    status: "ACTIVE",
    ownerId: 1,
  },
  {
    id: 3,
    assetCode: "GS-2024-003",
    assetName: "四川水电项目",
    assetType: "GoldStandard",
    quantity: 20000,
    unit: "tCO2e",
    vintageYear: 2024,
    issuanceDate: "2024-03-10",
    expiryDate: "2034-03-10",
    status: "ACTIVE",
    ownerId: 2,
  },
  {
    id: 4,
    assetCode: "CCER-2022-004",
    assetName: "内蒙古草原碳汇项目",
    assetType: "CCER",
    quantity: 40000,
    unit: "tCO2e",
    vintageYear: 2022,
    issuanceDate: "2022-09-01",
    expiryDate: "2032-09-01",
    status: "RETIRED",
    ownerId: 2,
  },
  {
    id: 5,
    assetCode: "VCS-2024-005",
    assetName: "广东光伏减排项目",
    assetType: "VCS",
    quantity: 15000,
    unit: "tCO2e",
    vintageYear: 2024,
    issuanceDate: "2024-05-18",
    expiryDate: "2034-05-18",
    status: "ACTIVE",
    ownerId: 3,
  },
];

const mockTransactions = [
  {
    id: 1,
    transactionCode: "TXN-20240601-001",
    assetId: 1,
    assetName: "北京林业碳汇项目",
    orderType: "BUY",
    quantity: 5000,
    price: 52.5,
    totalAmount: 262500,
    currency: "CNY",
    exchange: "北京绿色交易所",
    counterpartyId: 2,
    status: "SETTLED",
    submittedAt: "2024-06-01 10:30:00",
    matchedAt: "2024-06-01 11:15:00",
    settledAt: "2024-06-02 09:00:00",
  },
  {
    id: 2,
    transactionCode: "TXN-20240602-002",
    assetId: 2,
    assetName: "河北风电减排项目",
    orderType: "SELL",
    quantity: 3000,
    price: 48.8,
    totalAmount: 146400,
    currency: "CNY",
    exchange: "上海环境能源交易所",
    counterpartyId: 3,
    status: "MATCHED",
    submittedAt: "2024-06-02 14:20:00",
    matchedAt: "2024-06-02 15:30:00",
    settledAt: null,
  },
  {
    id: 3,
    transactionCode: "TXN-20240603-003",
    assetId: 3,
    assetName: "四川水电项目",
    orderType: "BUY",
    quantity: 10000,
    price: 65.0,
    totalAmount: 650000,
    currency: "CNY",
    exchange: "广州碳排放权交易所",
    counterpartyId: 1,
    status: "SUBMITTED",
    submittedAt: "2024-06-03 09:45:00",
    matchedAt: null,
    settledAt: null,
  },
  {
    id: 4,
    transactionCode: "TXN-20240604-004",
    assetId: 5,
    assetName: "广东光伏减排项目",
    orderType: "BUY",
    quantity: 2000,
    price: 55.0,
    totalAmount: 110000,
    currency: "CNY",
    exchange: "深圳排放权交易所",
    counterpartyId: 2,
    status: "PENDING",
    submittedAt: "2024-06-04 16:00:00",
    matchedAt: null,
    settledAt: null,
  },
];

const mockPrices = [
  {
    id: 1,
    exchange: "北京绿色交易所",
    productCode: "BEA",
    productName: "北京碳配额",
    price: 58.5,
    openPrice: 57.2,
    closePrice: 58.5,
    highPrice: 59.0,
    lowPrice: 56.8,
    volume: 125000,
    turnover: 7312500,
    change: 2.27,
    priceDate: "2024-06-04",
  },
  {
    id: 2,
    exchange: "上海环境能源交易所",
    productCode: "SHEA",
    productName: "上海碳配额",
    price: 52.3,
    openPrice: 53.1,
    closePrice: 52.3,
    highPrice: 53.5,
    lowPrice: 51.8,
    volume: 98000,
    turnover: 5125400,
    change: -1.51,
    priceDate: "2024-06-04",
  },
  {
    id: 3,
    exchange: "广州碳排放权交易所",
    productCode: "GDEA",
    productName: "广东碳配额",
    price: 65.8,
    openPrice: 64.5,
    closePrice: 65.8,
    highPrice: 66.2,
    lowPrice: 64.0,
    volume: 156000,
    turnover: 10264800,
    change: 2.02,
    priceDate: "2024-06-04",
  },
];

const mockPriceTrend = [
  { date: "2024-05-28", bea: 55.2, shea: 53.5, gdea: 62.3 },
  { date: "2024-05-29", bea: 55.8, shea: 53.2, gdea: 63.1 },
  { date: "2024-05-30", bea: 56.1, shea: 52.8, gdea: 63.8 },
  { date: "2024-05-31", bea: 56.5, shea: 52.5, gdea: 64.2 },
  { date: "2024-06-01", bea: 57.0, shea: 52.9, gdea: 64.5 },
  { date: "2024-06-02", bea: 57.2, shea: 53.1, gdea: 65.0 },
  { date: "2024-06-03", bea: 57.8, shea: 52.8, gdea: 65.5 },
  { date: "2024-06-04", bea: 58.5, shea: 52.3, gdea: 65.8 },
];

const allFilteredAssets = computed(() => {
  let result = [...assets.value];
  if (assetFilter.type) {
    result = result.filter((item) => item.assetType === assetFilter.type);
  }
  if (assetFilter.status) {
    result = result.filter((item) => item.status === assetFilter.status);
  }
  if (assetFilter.keyword) {
    const keyword = assetFilter.keyword.toLowerCase();
    result = result.filter(
      (item) =>
        item.assetCode.toLowerCase().includes(keyword) ||
        item.assetName.toLowerCase().includes(keyword)
    );
  }
  return result;
});

watch(allFilteredAssets, (val) => {
  assetPagination.total = val.length;
}, { immediate: true });

const filteredAssets = computed(() => {
  const start = (assetPagination.page - 1) * assetPagination.size;
  const end = start + assetPagination.size;
  return allFilteredAssets.value.slice(start, end);
});

const allFilteredTransactions = computed(() => {
  let result = [...transactions.value];
  if (transactionFilter.orderType) {
    result = result.filter((item) => item.orderType === transactionFilter.orderType);
  }
  if (transactionFilter.status) {
    result = result.filter((item) => item.status === transactionFilter.status);
  }
  if (transactionFilter.keyword) {
    const keyword = transactionFilter.keyword.toLowerCase();
    result = result.filter((item) => item.transactionCode.toLowerCase().includes(keyword));
  }
  return result;
});

watch(allFilteredTransactions, (val) => {
  transactionPagination.total = val.length;
}, { immediate: true });

const filteredTransactions = computed(() => {
  const start = (transactionPagination.page - 1) * transactionPagination.size;
  const end = start + transactionPagination.size;
  return allFilteredTransactions.value.slice(start, end);
});

const activeAssets = computed(() => {
  return assets.value.filter((item) => item.status === "ACTIVE");
});

function getAssetTypeTag(type) {
  const map = {
    CCER: "",
    VCS: "success",
    GoldStandard: "warning",
  };
  return map[type] || "";
}

function getStatusTag(status) {
  const map = {
    ACTIVE: "success",
    RETIRED: "info",
    TRADED: "warning",
  };
  return map[status] || "";
}

function getStatusLabel(status) {
  const map = {
    ACTIVE: "活跃",
    RETIRED: "已退役",
    TRADED: "已交易",
  };
  return map[status] || status;
}

function getTransactionStatusTag(status) {
  const map = {
    PENDING: "info",
    SUBMITTED: "",
    MATCHED: "warning",
    SETTLED: "success",
    CANCELLED: "danger",
  };
  return map[status] || "";
}

function getTransactionStatusLabel(status) {
  const map = {
    PENDING: "待处理",
    SUBMITTED: "已提交",
    MATCHED: "已匹配",
    SETTLED: "已结算",
    CANCELLED: "已取消",
  };
  return map[status] || status;
}

function getTransactionStep(status) {
  const map = {
    PENDING: 0,
    SUBMITTED: 1,
    MATCHED: 2,
    SETTLED: 3,
    CANCELLED: -1,
  };
  return map[status] || 0;
}

function formatNumber(num) {
  if (!num) return "0";
  return num.toLocaleString();
}

function formatCurrency(num) {
  if (!num) return "¥0.00";
  return "¥" + num.toLocaleString("zh-CN", { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

function formatDate(dateStr) {
  if (!dateStr) return "-";
  return dateStr.split(" ")[0];
}

function formatDateTime(dateStr) {
  if (!dateStr) return "-";
  return dateStr;
}

function viewAsset(row) {
  Object.assign(currentAsset, row);
  assetDetailVisible.value = true;
}

function showAssetDialog(asset = null) {
  if (asset) {
    assetDialogTitle.value = "编辑资产";
    Object.assign(assetForm, asset);
  } else {
    assetDialogTitle.value = "新增资产";
    Object.assign(assetForm, {
      id: null,
      assetCode: "",
      assetName: "",
      assetType: "CCER",
      quantity: 1000,
      unit: "tCO2e",
      vintageYear: "",
      issuanceDate: "",
      expiryDate: "",
    });
  }
  assetDialogVisible.value = true;
}

function tradeAsset(row) {
  activeTab.value = "market";
  orderForm.assetId = row.id;
}

async function saveAsset() {
  try {
    await assetFormRef.value.validate();
    loading.value = true;

    if (assetForm.id) {
      const index = assets.value.findIndex((item) => item.id === assetForm.id);
      if (index !== -1) {
        assets.value[index] = { ...assetForm };
      }
      ElMessage.success("资产更新成功");
    } else {
      const newAsset = {
        ...assetForm,
        id: Date.now(),
        status: "ACTIVE",
      };
      assets.value.unshift(newAsset);
      ElMessage.success("资产创建成功");
    }
    assetDialogVisible.value = false;
    updateAssetStats();
  } catch (error) {
    console.error("表单验证失败:", error);
  } finally {
    loading.value = false;
  }
}

function updateAssetStats() {
  assetStats.total = assets.value.length;
  assetStats.active = assets.value.filter((item) => item.status === "ACTIVE").length;
  assetStats.retired = assets.value.filter((item) => item.status === "RETIRED").length;
  assetStats.totalValue = assets.value.reduce((sum, item) => sum + item.quantity * 50, 0);
}

async function submitOrder() {
  if (!orderForm.assetId) {
    ElMessage.warning("请选择资产");
    return;
  }
  if (orderForm.quantity <= 0) {
    ElMessage.warning("请输入有效数量");
    return;
  }
  if (orderForm.price <= 0) {
    ElMessage.warning("请输入有效价格");
    return;
  }

  try {
    await ElMessageBox.confirm(
      `确认${orderForm.orderType === "BUY" ? "买入" : "卖出"}？数量: ${orderForm.quantity} tCO2e，价格: ¥${orderForm.price}`,
      "确认订单",
      { type: "warning" }
    );

    orderLoading.value = true;

    const selectedAsset = assets.value.find((item) => item.id === orderForm.assetId);
    const newTransaction = {
      id: Date.now(),
      transactionCode: `TXN-${new Date().toISOString().slice(0, 10).replace(/-/g, "")}-${String(transactions.value.length + 1).padStart(3, "0")}`,
      assetId: orderForm.assetId,
      assetName: selectedAsset?.assetName || "",
      orderType: orderForm.orderType,
      quantity: orderForm.quantity,
      price: orderForm.price,
      totalAmount: orderForm.quantity * orderForm.price,
      currency: "CNY",
      exchange: "北京绿色交易所",
      counterpartyId: null,
      status: "PENDING",
      submittedAt: new Date().toISOString().replace("T", " ").slice(0, 19),
      matchedAt: null,
      settledAt: null,
    };

    transactions.value.unshift(newTransaction);
    recentTransactions.value = transactions.value.slice(0, 5);
    ElMessage.success("订单提交成功");

    orderForm.assetId = null;
    orderForm.quantity = 1000;
    orderForm.price = 50;
  } catch {
    // 用户取消
  } finally {
    orderLoading.value = false;
  }
}

function viewTransaction(row) {
  Object.assign(currentTransaction, row);
  transactionDetailVisible.value = true;
}

async function cancelTransaction(row) {
  try {
    await ElMessageBox.confirm("确认取消此交易？", "确认取消", { type: "warning" });

    const index = transactions.value.findIndex((item) => item.id === row.id);
    if (index !== -1) {
      transactions.value[index].status = "CANCELLED";
    }
    recentTransactions.value = transactions.value.slice(0, 5);
    ElMessage.success("交易已取消");
  } catch {
    // 用户取消
  }
}

function handleAssetSizeChange(size) {
  assetPagination.size = size;
  assetPagination.page = 1;
}

function handleAssetPageChange(page) {
  assetPagination.page = page;
}

function handleTransactionSizeChange(size) {
  transactionPagination.size = size;
  transactionPagination.page = 1;
}

function handleTransactionPageChange(page) {
  transactionPagination.page = page;
}

function initPriceChart() {
  if (!priceChartRef.value) return;

  priceChart.value = echarts.init(priceChartRef.value);

  const dates = mockPriceTrend.map((item) => item.date);
  const beaData = mockPriceTrend.map((item) => item.bea);
  const sheaData = mockPriceTrend.map((item) => item.shea);
  const gdeaData = mockPriceTrend.map((item) => item.gdea);

  const option = {
    tooltip: {
      trigger: "axis",
    },
    legend: {
      data: ["北京碳配额", "上海碳配额", "广东碳配额"],
    },
    grid: {
      left: "3%",
      right: "4%",
      bottom: "3%",
      containLabel: true,
    },
    xAxis: {
      type: "category",
      data: dates,
      axisLabel: {
        rotate: 45,
      },
    },
    yAxis: {
      type: "value",
      name: "价格(元/吨)",
    },
    series: [
      {
        name: "北京碳配额",
        type: "line",
        data: beaData,
        smooth: true,
        itemStyle: { color: "#409EFF" },
      },
      {
        name: "上海碳配额",
        type: "line",
        data: sheaData,
        smooth: true,
        itemStyle: { color: "#67C23A" },
      },
      {
        name: "广东碳配额",
        type: "line",
        data: gdeaData,
        smooth: true,
        itemStyle: { color: "#E6A23C" },
      },
    ],
  };

  priceChart.value.setOption(option);
}

function fetchData() {
  loading.value = true;

  assets.value = mockAssets;
  transactions.value = mockTransactions;
  carbonPrices.value = mockPrices;
  recentTransactions.value = mockTransactions.slice(0, 5);

  updateAssetStats();
  assetPagination.total = mockAssets.length;
  transactionPagination.total = mockTransactions.length;

  setTimeout(() => {
    loading.value = false;
    if (activeTab.value === "market") {
      initPriceChart();
    }
  }, 500);
}

watch(activeTab, (newVal) => {
  if (newVal === "market") {
    setTimeout(initPriceChart, 100);
  }
});

onMounted(() => {
  fetchData();
});

onUnmounted(() => {
  if (priceChart.value) {
    priceChart.value.dispose();
    priceChart.value = null;
  }
});
</script>

<style scoped>
.carbon-trading {
  padding: 20px;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  padding: 20px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 15px;
  color: white;
}

.gradient-blue {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.gradient-green {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.gradient-orange {
  background: linear-gradient(135deg, #fa8c16 0%, #fadb14 100%);
}

.gradient-purple {
  background: linear-gradient(135deg, #722ed1 0%, #b37feb 100%);
}

.stat-icon {
  font-size: 36px;
  opacity: 0.8;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
}

.stat-label {
  font-size: 14px;
  opacity: 0.9;
}

.filter-bar {
  margin-bottom: 20px;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 8px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.market-section {
  margin-bottom: 30px;
}

.market-section h3 {
  margin-bottom: 20px;
  font-size: 18px;
  color: #303133;
}

.price-card {
  padding: 20px;
  background: white;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  margin-bottom: 20px;
}

.price-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
}

.exchange {
  font-weight: bold;
  color: #303133;
}

.product {
  color: #909399;
}

.price-main {
  display: flex;
  align-items: baseline;
  gap: 10px;
  margin-bottom: 15px;
}

.current-price {
  font-size: 32px;
  font-weight: bold;
}

.current-price.up {
  color: #f56c6c;
}

.current-price.down {
  color: #67c23a;
}

.change {
  font-size: 16px;
}

.change.up {
  color: #f56c6c;
}

.change.down {
  color: #67c23a;
}

.price-detail {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
  margin-bottom: 15px;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  color: #606266;
  font-size: 14px;
}

.price-footer {
  display: flex;
  justify-content: space-between;
  color: #909399;
  font-size: 12px;
  padding-top: 15px;
  border-top: 1px solid #ebeef5;
}

.chart-section {
  margin-bottom: 30px;
}

.chart-container {
  height: 400px;
}

.total-amount {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
}

.status-flow {
  margin-top: 30px;
  padding: 20px;
}

.recent-transactions {
  margin-top: 20px;
}
</style>
