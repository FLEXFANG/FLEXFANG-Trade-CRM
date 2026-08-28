package cn.iocoder.yudao.module.crm.service.trade;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.crm.controller.admin.customer.vo.customer.CrmCustomerPageReqVO;
import cn.iocoder.yudao.module.crm.controller.admin.trade.vo.CrmTradeWorkbenchRespVO;
import cn.iocoder.yudao.module.crm.dal.dataobject.customer.CrmCustomerDO;
import cn.iocoder.yudao.module.crm.dal.dataobject.trade.CrmTradeProfileDO;
import cn.iocoder.yudao.module.crm.dal.mysql.trade.CrmTradeProfileMapper;
import cn.iocoder.yudao.module.crm.enums.common.CrmBizTypeEnum;
import cn.iocoder.yudao.module.crm.enums.common.CrmSceneTypeEnum;
import cn.iocoder.yudao.module.crm.service.customer.CrmCustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.PageParam.PAGE_SIZE_NONE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CrmTradeWorkbenchServiceImplTest {

    @InjectMocks
    private CrmTradeWorkbenchServiceImpl workbenchService;

    @Mock
    private CrmCustomerService customerService;
    @Mock
    private CrmTradeProfileMapper tradeProfileMapper;

    @Test
    void getWorkbench_aggregatesFollowUpAndTradePriority() {
        LocalDateTime now = LocalDateTime.now();
        CrmCustomerDO colombia = CrmCustomerDO.builder()
                .id(10L).name("Andes Helmets")
                .contactNextTime(now.withHour(16).withMinute(0).withSecond(0).withNano(0))
                .build();
        CrmCustomerDO peru = CrmCustomerDO.builder()
                .id(20L).name("Lima Moto")
                .contactNextTime(now.minusDays(2))
                .build();
        CrmCustomerDO mexico = CrmCustomerDO.builder()
                .id(30L).name("MX Rider")
                .contactNextTime(now.plusDays(3))
                .build();
        when(customerService.getCustomerPage(org.mockito.ArgumentMatchers.any(CrmCustomerPageReqVO.class), eq(99L)))
                .thenReturn(new PageResult<>(List.of(colombia, peru, mexico), 3L));

        CrmTradeProfileDO colombiaProfile = CrmTradeProfileDO.builder()
                .bizType(CrmBizTypeEnum.CRM_CUSTOMER.getType()).bizId(10L)
                .countryCode("CO").companyType("IMPORTER").sourceChannel("META")
                .leadScore(90).riskScore(20).fclProbability(80).containerPotential("40HQ")
                .nextAction("Confirm annual purchase plan").build();
        CrmTradeProfileDO peruProfile = CrmTradeProfileDO.builder()
                .bizType(CrmBizTypeEnum.CRM_CUSTOMER.getType()).bizId(20L)
                .countryCode("PE").companyType("DISTRIBUTOR").sourceChannel("EMAIL")
                .leadScore(60).riskScore(80).fclProbability(20).containerPotential("LCL")
                .nextAction("Verify company background").build();
        when(tradeProfileMapper.selectListByBiz(eq(CrmBizTypeEnum.CRM_CUSTOMER.getType()), anyCollection()))
                .thenReturn(List.of(colombiaProfile, peruProfile));

        CrmTradeWorkbenchRespVO result = workbenchService.getWorkbench(99L);

        assertEquals(3L, result.getSummary().getTotalCustomers());
        assertEquals(1L, result.getSummary().getDueToday());
        assertEquals(1L, result.getSummary().getOverdue());
        assertEquals(1L, result.getSummary().getHighPriority());
        assertEquals(1L, result.getSummary().getHighFclPotential());
        assertEquals(1L, result.getSummary().getHighRisk());
        assertEquals(2, result.getPriorityCustomers().size());
        assertEquals(10L, result.getPriorityCustomers().get(0).getCustomerId());
        assertEquals("A", result.getPriorityCustomers().get(0).getQualityGrade());
        assertEquals("CO", result.getCountryDistribution().get(0).getKey());

        ArgumentCaptor<CrmCustomerPageReqVO> reqCaptor = ArgumentCaptor.forClass(CrmCustomerPageReqVO.class);
        verify(customerService).getCustomerPage(reqCaptor.capture(), eq(99L));
        assertEquals(PAGE_SIZE_NONE, reqCaptor.getValue().getPageSize());
        assertEquals(CrmSceneTypeEnum.OWNER.getType(), reqCaptor.getValue().getSceneType());
    }

}
