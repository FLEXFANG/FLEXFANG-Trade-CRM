package cn.iocoder.yudao.module.crm.service.clue;

import cn.iocoder.yudao.module.crm.dal.dataobject.clue.CrmClueDO;
import cn.iocoder.yudao.module.crm.dal.mysql.clue.CrmClueMapper;
import cn.iocoder.yudao.module.crm.enums.common.CrmBizTypeEnum;
import cn.iocoder.yudao.module.crm.service.customer.CrmCustomerService;
import cn.iocoder.yudao.module.crm.service.customer.bo.CrmCustomerCreateReqBO;
import cn.iocoder.yudao.module.crm.service.followup.CrmFollowUpRecordService;
import cn.iocoder.yudao.module.crm.service.permission.CrmPermissionService;
import cn.iocoder.yudao.module.crm.service.trade.CrmTradeProfileService;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrmClueTradeProfileTransformTest {

    @InjectMocks
    private CrmClueServiceImpl clueService;

    @Mock
    private CrmClueMapper clueMapper;
    @Mock
    private CrmCustomerService customerService;
    @Mock
    private CrmPermissionService crmPermissionService;
    @Mock
    private CrmFollowUpRecordService followUpRecordService;
    @Mock
    private CrmTradeProfileService tradeProfileService;
    @Mock
    private AdminUserApi adminUserApi;

    @Test
    void transformClue_copiesTradeProfileToCreatedCustomer() {
        Long clueId = 10L;
        Long userId = 20L;
        Long customerId = 30L;
        CrmClueDO clue = CrmClueDO.builder()
                .id(clueId)
                .name("Colombia helmet importer")
                .ownerUserId(userId)
                .transformStatus(false)
                .build();
        when(clueMapper.selectById(clueId)).thenReturn(clue);
        when(customerService.createCustomer(any(CrmCustomerCreateReqBO.class), eq(userId))).thenReturn(customerId);

        clueService.transformClue(clueId, userId);

        verify(tradeProfileService).copyTradeProfile(
                CrmBizTypeEnum.CRM_CLUE.getType(), clueId,
                CrmBizTypeEnum.CRM_CUSTOMER.getType(), customerId);
        ArgumentCaptor<CrmClueDO> updateCaptor = ArgumentCaptor.forClass(CrmClueDO.class);
        verify(clueMapper).updateById(updateCaptor.capture());
        assertTrue(updateCaptor.getValue().getTransformStatus());
        assertEquals(customerId, updateCaptor.getValue().getCustomerId());
        verify(followUpRecordService).getFollowUpRecordByBiz(eq(CrmBizTypeEnum.CRM_CLUE.getType()), any());
    }

}
