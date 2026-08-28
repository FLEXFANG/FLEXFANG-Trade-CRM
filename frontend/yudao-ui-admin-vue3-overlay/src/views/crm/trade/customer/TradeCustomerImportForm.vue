<template>
  <Dialog v-model="dialogVisible" title="外贸客户导入" width="440">
    <div class="my-10px flex items-center">
      <span class="mr-10px">负责人</span>
      <el-select v-model="ownerUserId" class="!w-280px" clearable placeholder="不选择则进入公海">
        <el-option
          v-for="item in userOptions"
          :key="item.id"
          :label="item.nickname"
          :value="item.id"
        />
      </el-select>
    </div>
    <el-upload
      ref="uploadRef"
      v-model:file-list="fileList"
      :auto-upload="false"
      :disabled="formLoading"
      :limit="1"
      :on-exceed="handleExceed"
      accept=".xlsx,.xls"
      action="none"
      drag
    >
      <Icon icon="ep:upload" />
      <div class="el-upload__text">将 Excel 拖到这里，或<em>点击选择</em></div>
      <template #tip>
        <div class="el-upload__tip text-center">
          <div>
            <el-checkbox v-model="updateSupport" />
            客户名称重复时更新现有客户
          </div>
          <span>支持国家、买家类型、来源、MOQ、价格、认证、贸易条款、整柜概率等外贸字段。</span>
          <el-link :underline="false" type="primary" @click="downloadTemplate">下载外贸模板</el-link>
        </div>
      </template>
    </el-upload>
    <template #footer>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">导 入</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import type { UploadUserFile } from 'element-plus'
import * as TradeApi from '@/api/crm/trade'
import * as UserApi from '@/api/system/user'
import { getCurrentUserId } from '@/utils/auth'
import download from '@/utils/download'

defineOptions({ name: 'CrmTradeCustomerImportForm' })

const message = useMessage()
const dialogVisible = ref(false)
const formLoading = ref(false)
const uploadRef = ref()
const fileList = ref<UploadUserFile[]>([])
const updateSupport = ref(false)
const ownerUserId = ref<number>()
const userOptions = ref<UserApi.UserVO[]>([])

const open = async () => {
  dialogVisible.value = true
  await resetForm()
  userOptions.value = await UserApi.getSimpleUserList()
  ownerUserId.value = getCurrentUserId()
}
defineExpose({ open })

const emits = defineEmits(['success'])

const submitForm = async () => {
  if (!fileList.value.length || !fileList.value[0].raw) {
    message.error('请先选择 Excel 文件')
    return
  }
  formLoading.value = true
  try {
    const formData = new FormData()
    formData.append('updateSupport', String(updateSupport.value))
    formData.append('file', fileList.value[0].raw as Blob)
    if (ownerUserId.value !== undefined) {
      formData.append('ownerUserId', String(ownerUserId.value))
    }
    const response: any = await TradeApi.importTradeCustomers(formData)
    if (response?.code !== 0) {
      message.error(response?.msg || '导入失败')
      return
    }
    const data = response.data
    const failed = Object.keys(data.failureCustomerNames || {})
    let text = `新增：${data.createCustomerNames?.length || 0}；更新：${data.updateCustomerNames?.length || 0}；失败：${failed.length}`
    if (failed.length) {
      text += '<br/>' + failed.map((name) => `${name}: ${data.failureCustomerNames[name]}`).join('<br/>')
    }
    await message.alert(text)
    dialogVisible.value = false
    emits('success')
  } finally {
    formLoading.value = false
  }
}

const resetForm = async () => {
  fileList.value = []
  updateSupport.value = false
  ownerUserId.value = undefined
  await nextTick()
  uploadRef.value?.clearFiles()
}

const handleExceed = () => message.error('最多上传一个文件')

const downloadTemplate = async () => {
  const data = await TradeApi.importTradeCustomerTemplate()
  download.excel(data, 'FLEXFANG-外贸客户导入模板.xlsx')
}
</script>
