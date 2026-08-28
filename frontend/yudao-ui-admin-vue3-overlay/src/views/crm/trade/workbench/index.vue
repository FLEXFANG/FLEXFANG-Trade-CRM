<template>
  <div v-loading="loading">
    <el-row :gutter="12" class="mb-15px">
      <el-col v-for="item in summaryCards" :key="item.key" :xs="12" :sm="8" :lg="4">
        <el-card class="mb-12px cursor-pointer" shadow="never" @click="openCard(item.key)">
          <div class="text-13px text-gray-500">{{ item.label }}</div>
          <div class="mt-8px text-28px font-600">{{ item.value }}</div>
        </el-card>
      </el-col>
    </el-row>

    <ContentWrap title="优先跟进客户" message="综合客户评分、整柜概率、风险与跟进时效排序">
      <el-empty v-if="!workbench?.priorityCustomers?.length" description="暂无优先客户" />
      <el-table v-else :data="workbench.priorityCustomers" stripe>
        <el-table-column fixed="left" label="客户" min-width="190">
          <template #default="scope">
            <el-link :underline="false" type="primary" @click="openDetail(scope.row.customerId)">
              {{ scope.row.customerName }}
            </el-link>
            <div class="mt-3px text-12px text-gray-500">{{ scope.row.countryCode || '-' }} · {{ scope.row.companyType || '未分类' }}</div>
          </template>
        </el-table-column>
        <el-table-column label="等级" prop="qualityGrade" width="75" align="center" />
        <el-table-column label="优先分" prop="priorityScore" width="90" align="center" />
        <el-table-column label="客户评分" prop="leadScore" width="95" align="center" />
        <el-table-column label="FCL" width="90" align="center">
          <template #default="scope">{{ percentage(scope.row.fclProbability) }}</template>
        </el-table-column>
        <el-table-column label="风险" prop="riskScore" width="80" align="center" />
        <el-table-column label="柜型" prop="containerPotential" width="85" />
        <el-table-column label="来源" prop="sourceChannel" width="110" />
        <el-table-column label="下一步动作" prop="nextAction" min-width="240" />
        <el-table-column label="下次联系" min-width="165">
          <template #default="scope">{{ formatNullableDate(scope.row.contactNextTime) }}</template>
        </el-table-column>
        <el-table-column fixed="right" label="操作" width="90" align="center">
          <template #default="scope">
            <el-button link type="primary" @click="openDetail(scope.row.customerId)">跟进</el-button>
          </template>
        </el-table-column>
      </el-table>
    </ContentWrap>

    <el-row :gutter="12">
      <el-col :xs="24" :lg="12">
        <ContentWrap title="国家分布">
          <el-table :data="workbench?.countryDistribution || []" size="small">
            <el-table-column label="国家/地区" prop="key" />
            <el-table-column label="客户数" prop="count" width="100" align="right" />
          </el-table>
        </ContentWrap>
      </el-col>
      <el-col :xs="24" :lg="12">
        <ContentWrap title="来源渠道">
          <el-table :data="workbench?.sourceDistribution || []" size="small">
            <el-table-column label="渠道" prop="key" />
            <el-table-column label="客户数" prop="count" width="100" align="right" />
          </el-table>
        </ContentWrap>
      </el-col>
    </el-row>
  </div>

  <CustomerDetailDrawer ref="detailDrawerRef" />
</template>

<script lang="ts" setup>
import { formatNullableDate } from '@/utils/formatTime'
import * as TradeApi from '@/api/crm/trade'
import CustomerDetailDrawer from '../components/CustomerDetailDrawer.vue'

defineOptions({ name: 'CrmTradeWorkbench' })

const router = useRouter()
const loading = ref(true)
const workbench = ref<TradeApi.TradeWorkbench>()
const detailDrawerRef = ref<InstanceType<typeof CustomerDetailDrawer>>()

const summaryCards = computed(() => {
  const s = workbench.value?.summary
  return [
    { key: 'total', label: '我的客户', value: s?.totalCustomers || 0 },
    { key: 'today', label: '今天需跟进', value: s?.dueToday || 0 },
    { key: 'overdue', label: '逾期未跟进', value: s?.overdue || 0 },
    { key: 'priority', label: '高优先级', value: s?.highPriority || 0 },
    { key: 'fcl', label: '高整柜潜力', value: s?.highFclPotential || 0 },
    { key: 'risk', label: '高风险', value: s?.highRisk || 0 }
  ]
})

const loadWorkbench = async () => {
  loading.value = true
  try {
    workbench.value = await TradeApi.getTradeWorkbench()
  } finally {
    loading.value = false
  }
}

const openCard = (key: string) => {
  const query: Record<string, string> = {}
  if (key === 'today') query.contactStatus = '1'
  if (key === 'overdue') query.contactStatus = '2'
  if (key === 'priority') query.minLeadScore = '70'
  if (key === 'fcl') query.minFclProbability = '60'
  router.push({ path: '/crm/trade/customer', query })
}

const openDetail = (id: number) => detailDrawerRef.value?.open(id)
const percentage = (value?: number) => (value == null ? '-' : `${value}%`)

onMounted(loadWorkbench)
</script>
