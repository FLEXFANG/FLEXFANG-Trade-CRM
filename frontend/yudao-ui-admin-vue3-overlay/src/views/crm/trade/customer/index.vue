<template>
  <ContentWrap>
    <el-form ref="queryFormRef" :inline="true" :model="queryParams" class="-mb-15px" label-width="78px">
      <el-form-item label="客户名称" prop="name">
        <el-input v-model="queryParams.name" class="!w-200px" clearable placeholder="客户/公司名称" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="国家" prop="countryCode">
        <el-input v-model="queryParams.countryCode" class="!w-130px" clearable placeholder="CO / PE / VN" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="买家类型" prop="companyType">
        <el-select v-model="queryParams.companyType" class="!w-170px" clearable placeholder="全部">
          <el-option v-for="item in buyerTypes" :key="item" :label="item" :value="item" />
        </el-select>
      </el-form-item>
      <el-form-item label="来源" prop="sourceChannel">
        <el-select v-model="queryParams.sourceChannel" class="!w-160px" clearable placeholder="全部">
          <el-option v-for="item in sourceChannels" :key="item" :label="item" :value="item" />
        </el-select>
      </el-form-item>
      <el-form-item label="最低评分" prop="minLeadScore">
        <el-input-number v-model="queryParams.minLeadScore" :max="100" :min="0" class="!w-130px" controls-position="right" />
      </el-form-item>
      <el-form-item label="最高风险" prop="maxRiskScore">
        <el-input-number v-model="queryParams.maxRiskScore" :max="100" :min="0" class="!w-130px" controls-position="right" />
      </el-form-item>
      <el-form-item label="最低FCL" prop="minFclProbability">
        <el-input-number v-model="queryParams.minFclProbability" :max="100" :min="0" class="!w-130px" controls-position="right" />
      </el-form-item>
      <el-form-item label="整柜潜力" prop="containerPotential">
        <el-select v-model="queryParams.containerPotential" class="!w-140px" clearable placeholder="全部">
          <el-option v-for="item in containerTypes" :key="item" :label="item" :value="item" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon class="mr-5px" icon="ep:search" />搜索</el-button>
        <el-button @click="resetQuery"><Icon class="mr-5px" icon="ep:refresh" />重置</el-button>
        <el-button v-hasPermi="['crm:customer:import']" plain type="warning" @click="openImport">
          <Icon class="mr-5px" icon="ep:upload" />外贸 Excel 导入
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-tabs v-model="sceneType" @tab-change="handleSceneChange">
      <el-tab-pane label="我负责的" :name="1" />
      <el-tab-pane label="我参与的" :name="2" />
      <el-tab-pane label="下属负责的" :name="3" />
    </el-tabs>

    <div class="mb-12px flex flex-wrap gap-8px">
      <el-button :type="queryParams.contactStatus === 1 ? 'primary' : 'default'" @click="setContactStatus(1)">今天需跟进</el-button>
      <el-button :type="queryParams.contactStatus === 2 ? 'danger' : 'default'" @click="setContactStatus(2)">已逾期</el-button>
      <el-button :type="queryParams.hasNextAction === true ? 'success' : 'default'" @click="toggleNextAction">有明确下一步</el-button>
      <el-button v-if="queryParams.contactStatus || queryParams.hasNextAction" link @click="clearQuickFilters">清除快捷筛选</el-button>
    </div>

    <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" stripe>
      <el-table-column fixed="left" label="客户" min-width="190">
        <template #default="scope">
          <el-link :underline="false" type="primary" @click="openDetail(scope.row.customerId)">
            {{ scope.row.customerName }}
          </el-link>
          <div class="mt-3px text-12px text-gray-500">{{ scope.row.countryCode || '-' }} · {{ scope.row.companyType || '未分类' }}</div>
        </template>
      </el-table-column>
      <el-table-column label="来源" prop="sourceChannel" width="120" />
      <el-table-column label="WhatsApp / 手机" min-width="150">
        <template #default="scope">{{ scope.row.whatsapp || scope.row.mobile || '-' }}</template>
      </el-table-column>
      <el-table-column label="客户评分" width="105" align="center">
        <template #default="scope"><el-tag effect="plain">{{ score(scope.row.leadScore) }}</el-tag></template>
      </el-table-column>
      <el-table-column label="FCL" width="105" align="center">
        <template #default="scope">{{ percentage(scope.row.fclProbability) }}</template>
      </el-table-column>
      <el-table-column label="风险" width="95" align="center">
        <template #default="scope">{{ score(scope.row.riskScore) }}</template>
      </el-table-column>
      <el-table-column label="柜型" prop="containerPotential" width="90" />
      <el-table-column label="MOQ" prop="expectedMoq" width="90" />
      <el-table-column label="目标价" width="120">
        <template #default="scope">
          {{ scope.row.targetPrice == null ? '-' : `${scope.row.currency || ''} ${scope.row.targetPrice}`.trim() }}
        </template>
      </el-table-column>
      <el-table-column label="下一步动作" prop="nextAction" min-width="220" />
      <el-table-column label="下次联系" min-width="165">
        <template #default="scope">{{ formatNullableDate(scope.row.contactNextTime) }}</template>
      </el-table-column>
      <el-table-column fixed="right" label="操作" width="90" align="center">
        <template #default="scope">
          <el-button link type="primary" @click="openDetail(scope.row.customerId)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <Pagination v-model:limit="queryParams.pageSize" v-model:page="queryParams.pageNo" :total="total" @pagination="getList" />
  </ContentWrap>

  <CustomerDetailDrawer ref="detailDrawerRef" />
  <TradeCustomerImportForm ref="importFormRef" @success="getList" />
</template>

<script lang="ts" setup>
import { formatNullableDate } from '@/utils/formatTime'
import * as TradeApi from '@/api/crm/trade'
import CustomerDetailDrawer from '../components/CustomerDetailDrawer.vue'
import TradeCustomerImportForm from './TradeCustomerImportForm.vue'

defineOptions({ name: 'CrmTradeCustomer' })

const route = useRoute()
const loading = ref(true)
const total = ref(0)
const list = ref<TradeApi.TradeCustomer[]>([])
const queryFormRef = ref()
const detailDrawerRef = ref<InstanceType<typeof CustomerDetailDrawer>>()
const importFormRef = ref<InstanceType<typeof TradeCustomerImportForm>>()
const sceneType = ref(1)

const buyerTypes = ['IMPORTER', 'DISTRIBUTOR', 'WHOLESALER', 'RETAIL_CHAIN', 'BRAND', 'OEM']
const sourceChannels = ['META', 'SHOPIFY', 'WEBSITE', 'WHATSAPP', 'EMAIL', 'EXHIBITION', 'ALIBABA', 'OTHER']
const containerTypes = ['LCL', '20GP', '40GP', '40HQ']

const queryParams = reactive<TradeApi.TradeCustomerPageReq>({
  pageNo: 1,
  pageSize: 20,
  sceneType: 1,
  name: undefined,
  countryCode: undefined,
  companyType: undefined,
  sourceChannel: undefined,
  minLeadScore: undefined,
  maxRiskScore: undefined,
  minFclProbability: undefined,
  containerPotential: undefined,
  contactStatus: undefined,
  hasNextAction: undefined
})

const toNumber = (value: unknown) => {
  const n = Number(value)
  return Number.isFinite(n) ? n : undefined
}

const applyRouteQuery = () => {
  if (route.query.contactStatus) queryParams.contactStatus = toNumber(route.query.contactStatus)
  if (route.query.minLeadScore) queryParams.minLeadScore = toNumber(route.query.minLeadScore)
  if (route.query.minFclProbability) queryParams.minFclProbability = toNumber(route.query.minFclProbability)
}

const getList = async () => {
  loading.value = true
  try {
    const data = await TradeApi.getTradeCustomerPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

const resetQuery = () => {
  queryFormRef.value?.resetFields()
  queryParams.contactStatus = undefined
  queryParams.hasNextAction = undefined
  queryParams.sceneType = sceneType.value
  handleQuery()
}

const handleSceneChange = (value: string | number) => {
  queryParams.sceneType = Number(value)
  handleQuery()
}

const setContactStatus = (status: number) => {
  queryParams.contactStatus = queryParams.contactStatus === status ? undefined : status
  handleQuery()
}

const toggleNextAction = () => {
  queryParams.hasNextAction = queryParams.hasNextAction === true ? undefined : true
  handleQuery()
}

const clearQuickFilters = () => {
  queryParams.contactStatus = undefined
  queryParams.hasNextAction = undefined
  handleQuery()
}

const openDetail = (id: number) => detailDrawerRef.value?.open(id)
const openImport = () => importFormRef.value?.open()
const score = (value?: number) => (value == null ? '-' : value)
const percentage = (value?: number) => (value == null ? '-' : `${value}%`)

onMounted(() => {
  applyRouteQuery()
  getList()
})
</script>
