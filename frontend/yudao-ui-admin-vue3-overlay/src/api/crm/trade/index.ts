import request from '@/config/axios'

export interface TradeCustomer {
  customerId: number
  customerName: string
  mobile?: string
  telephone?: string
  email?: string
  ownerUserId?: number
  dealStatus?: boolean
  contactLastTime?: string | Date
  contactLastContent?: string
  contactNextTime?: string | Date
  countryCode?: string
  region?: string
  city?: string
  companyType?: string
  sourceChannel?: string
  website?: string
  whatsapp?: string
  linkedin?: string
  importExperience?: boolean
  annualPurchaseVolume?: number
  targetProducts?: string
  expectedMoq?: number
  targetPrice?: number
  currency?: string
  certificationRequirement?: string
  incoterm?: string
  destinationPort?: string
  containerPotential?: string
  fclProbability?: number
  leadScore?: number
  riskScore?: number
  nextAction?: string
  lostReason?: string
}

export interface TradeCustomerPageReq {
  pageNo: number
  pageSize: number
  sceneType?: number
  contactStatus?: number
  name?: string
  mobile?: string
  countryCode?: string
  companyType?: string
  sourceChannel?: string
  importExperience?: boolean
  minLeadScore?: number
  maxRiskScore?: number
  minFclProbability?: number
  containerPotential?: string
  hasNextAction?: boolean
}

export interface TradeFollowUp {
  id: number
  type: number
  content: string
  nextTime?: string | Date
  createTime?: string | Date
}

export interface TradeCustomerDetail {
  customer: TradeCustomer
  recentFollowUps: TradeFollowUp[]
}

export interface TradeWorkbenchSummary {
  totalCustomers: number
  dueToday: number
  overdue: number
  highPriority: number
  highFclPotential: number
  highRisk: number
}

export interface TradePriorityCustomer {
  customerId: number
  customerName: string
  countryCode?: string
  companyType?: string
  sourceChannel?: string
  leadScore?: number
  riskScore?: number
  fclProbability?: number
  containerPotential?: string
  qualityGrade?: string
  priorityScore?: number
  contactLastTime?: string | Date
  contactNextTime?: string | Date
  nextAction?: string
}

export interface TradeDistribution {
  key: string
  count: number
}

export interface TradeWorkbench {
  summary: TradeWorkbenchSummary
  priorityCustomers: TradePriorityCustomer[]
  countryDistribution: TradeDistribution[]
  sourceDistribution: TradeDistribution[]
}

export const getTradeWorkbench = async (): Promise<TradeWorkbench> => {
  return await request.get({ url: '/crm/trade-workbench/get' })
}

export const getTradeCustomerPage = async (params: TradeCustomerPageReq) => {
  return await request.get({ url: '/crm/trade-customer/page', params })
}

export const getTradeCustomerDetail = async (id: number): Promise<TradeCustomerDetail> => {
  return await request.get({ url: '/crm/trade-customer/get', params: { id } })
}

export const importTradeCustomerTemplate = () => {
  return request.download({ url: '/crm/trade-customer/get-import-template' })
}

export const importTradeCustomers = async (formData: FormData) => {
  return await request.upload({ url: '/crm/trade-customer/import', data: formData })
}
