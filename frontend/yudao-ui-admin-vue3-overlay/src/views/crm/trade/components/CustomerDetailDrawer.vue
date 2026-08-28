<template>
  <el-drawer v-model="visible" :title="detail?.customer?.customerName || '外贸客户详情'" size="760px">
    <div v-loading="loading">
      <template v-if="detail?.customer">
        <el-row :gutter="12" class="mb-16px">
          <el-col :span="8">
            <el-card shadow="never">
              <div class="text-13px text-gray-500">客户评分</div>
              <div class="mt-8px text-26px font-600">{{ value(detail.customer.leadScore) }}</div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="never">
              <div class="text-13px text-gray-500">整柜概率</div>
              <div class="mt-8px text-26px font-600">{{ percent(detail.customer.fclProbability) }}</div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="never">
              <div class="text-13px text-gray-500">风险评分</div>
              <div class="mt-8px text-26px font-600">{{ value(detail.customer.riskScore) }}</div>
            </el-card>
          </el-col>
        </el-row>

        <ContentWrap title="客户与外贸画像">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="国家/地区">
              {{ detail.customer.countryCode || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="城市">
              {{ [detail.customer.region, detail.customer.city].filter(Boolean).join(' / ') || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="买家类型">
              {{ detail.customer.companyType || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="来源渠道">
              {{ detail.customer.sourceChannel || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="邮箱">
              {{ detail.customer.email || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="WhatsApp">
              {{ detail.customer.whatsapp || detail.customer.mobile || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="进口经验">
              {{ detail.customer.importExperience === undefined ? '-' : detail.customer.importExperience ? '有' : '无' }}
            </el-descriptions-item>
            <el-descriptions-item label="整柜潜力">
              {{ detail.customer.containerPotential || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="目标 MOQ">
              {{ value(detail.customer.expectedMoq) }}
            </el-descriptions-item>
            <el-descriptions-item label="目标价">
              {{ money(detail.customer.targetPrice, detail.customer.currency) }}
            </el-descriptions-item>
            <el-descriptions-item label="贸易条款">
              {{ detail.customer.incoterm || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="目的港">
              {{ detail.customer.destinationPort || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="认证要求" :span="2">
              {{ detail.customer.certificationRequirement || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="目标产品" :span="2">
              {{ detail.customer.targetProducts || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="下一步动作" :span="2">
              <span class="font-600">{{ detail.customer.nextAction || '未设置' }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="下次联系" :span="2">
              {{ formatNullableDate(detail.customer.contactNextTime) }}
            </el-descriptions-item>
            <el-descriptions-item label="官网" :span="2">
              <el-link
                v-if="detail.customer.website"
                :href="detail.customer.website"
                target="_blank"
                type="primary"
              >
                {{ detail.customer.website }}
              </el-link>
              <span v-else>-</span>
            </el-descriptions-item>
          </el-descriptions>
        </ContentWrap>

        <ContentWrap title="最近跟进">
          <template #header>
            <div class="flex w-full items-center justify-between">
              <span>最近跟进</span>
              <el-button type="primary" @click="openFollowUp">
                <Icon class="mr-5px" icon="ep:plus" />
                添加跟进
              </el-button>
            </div>
          </template>
          <el-empty v-if="!detail.recentFollowUps?.length" description="暂无跟进记录" />
          <el-timeline v-else>
            <el-timeline-item
              v-for="item in detail.recentFollowUps"
              :key="item.id"
              :timestamp="formatNullableDate(item.createTime)"
              placement="top"
            >
              <el-card shadow="never">
                <div class="mb-8px flex items-center gap-8px">
                  <dict-tag :type="DICT_TYPE.CRM_FOLLOW_UP_TYPE" :value="item.type" />
                  <span v-if="item.nextTime" class="text-12px text-gray-500">
                    下次：{{ formatNullableDate(item.nextTime) }}
                  </span>
                </div>
                <div class="whitespace-pre-wrap">{{ item.content }}</div>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </ContentWrap>
      </template>
    </div>
  </el-drawer>

  <FollowUpRecordForm ref="followUpFormRef" @success="loadDetail" />
</template>

<script lang="ts" setup>
import { DICT_TYPE } from '@/utils/dict'
import { formatNullableDate } from '@/utils/formatTime'
import { BizTypeEnum } from '@/api/crm/permission'
import * as TradeApi from '@/api/crm/trade'
import FollowUpRecordForm from '@/views/crm/followup/FollowUpRecordForm.vue'

defineOptions({ name: 'CrmTradeCustomerDetailDrawer' })

const visible = ref(false)
const loading = ref(false)
const customerId = ref<number>()
const detail = ref<TradeApi.TradeCustomerDetail>()
const followUpFormRef = ref<InstanceType<typeof FollowUpRecordForm>>()

const value = (input?: number) => (input === undefined || input === null ? '-' : input)
const percent = (input?: number) => (input === undefined || input === null ? '-' : `${input}%`)
const money = (input?: number, currency?: string) => {
  if (input === undefined || input === null) return '-'
  return `${currency || ''} ${input}`.trim()
}

const loadDetail = async () => {
  if (!customerId.value) return
  loading.value = true
  try {
    detail.value = await TradeApi.getTradeCustomerDetail(customerId.value)
  } finally {
    loading.value = false
  }
}

const open = async (id: number) => {
  customerId.value = id
  visible.value = true
  detail.value = undefined
  await loadDetail()
}
defineExpose({ open })

const openFollowUp = () => {
  if (!customerId.value) return
  followUpFormRef.value?.open(BizTypeEnum.CRM_CUSTOMER, customerId.value)
}
</script>
