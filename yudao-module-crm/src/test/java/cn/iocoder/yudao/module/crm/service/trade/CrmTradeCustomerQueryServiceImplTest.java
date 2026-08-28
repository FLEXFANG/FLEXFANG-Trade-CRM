package cn.iocoder.yudao.module.crm.service.trade;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.crm.controller.admin.customer.vo.customer.CrmCustomerPageReqVO;
import cn.iocoder.yudao.module.crm.controller.admin.trade.vo.CrmTradeCustomerPageReqVO;
import cn.iocoder.yudao.module.crm.controller.admin.trade.vo.CrmTradeCustomerRespVO;
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
class CrmTradeCustomerQueryServiceImplTest {

    @InjectMocks
    private CrmTradeCustomerQueryServiceImpl queryService;

    @Mock
    private CrmCustomerService customerService;
    @Mock
    private CrmTradeProfileMapper tradeProfileMapper;

    @Test
    void getTradeCustomerPage_respectsNativePermissionQueryAndFiltersTradeProfile() {
        CrmCustomerDO a = CrmCustomerDO.builder().id(10L).name("Andes")
                .contactNextTime(LocalDateTime.now().minusDays(1)).build();
        CrmCustomerDO b = CrmCustomerDO.builder().id(20L).name("Lima")
                .contactNextTime(LocalDateTime.now().plusDays(2)).build();
        when(customerService.getCustomerPage(org.mockito.ArgumentMatchers.any(CrmCustomerPageReqVO.class), eq(99L)))
                .thenReturn(new PageResult<>(List.of(a, b), 2L));
        when(tradeProfileMapper.selectListByBiz(eq(CrmBizTypeEnum.CRM_CUSTOMER.getType()), anyCollection()))
                .thenReturn(List.of(
                        CrmTradeProfileDO.builder().bizId(10L).countryCode("CO").companyType("IMPORTER")
                                .sourceChannel("META").leadScore(90).riskScore(20).fclProbability(80)
                                .containerPotential("40HQ").build(),
                        CrmTradeProfileDO.builder().bizId(20L).countryCode("PE").companyType("DISTRIBUTOR")
                                .sourceChannel("EMAIL").leadScore(55).riskScore(30).fclProbability(20)
                                .containerPotential("LCL").build()));

        CrmTradeCustomerPageReqVO reqVO = new CrmTradeCustomerPageReqVO();
        reqVO.setPageNo(1);
        reqVO.setPageSize(20);
        reqVO.setSceneType(CrmSceneTypeEnum.OWNER.getType());
        reqVO.setCountryCode("co");
        reqVO.setCompanyType("importer");
        reqVO.setMinLeadScore(70);
        reqVO.setMaxRiskScore(50);
        reqVO.setMinFclProbability(60);

        PageResult<CrmTradeCustomerRespVO> result = queryService.getTradeCustomerPage(reqVO, 99L);

        assertEquals(1L, result.getTotal());
        assertEquals(10L, result.getList().get(0).getCustomerId());
        assertEquals("CO", result.getList().get(0).getCountryCode());

        ArgumentCaptor<CrmCustomerPageReqVO> nativeReq = ArgumentCaptor.forClass(CrmCustomerPageReqVO.class);
        verify(customerService).getCustomerPage(nativeReq.capture(), eq(99L));
        assertEquals(PAGE_SIZE_NONE, nativeReq.getValue().getPageSize());
        assertEquals(CrmSceneTypeEnum.OWNER.getType(), nativeReq.getValue().getSceneType());
    }

    @Test
    void getTradeCustomerPage_paginatesAfterTradeFiltering() {
        CrmCustomerDO a = CrmCustomerDO.builder().id(10L).name("A").build();
        CrmCustomerDO b = CrmCustomerDO.builder().id(20L).name("B").build();
        CrmCustomerDO c = CrmCustomerDO.builder().id(30L).name("C").build();
        when(customerService.getCustomerPage(org.mockito.ArgumentMatchers.any(CrmCustomerPageReqVO.class), eq(99L)))
                .thenReturn(new PageResult<>(List.of(a, b, c), 3L));
        when(tradeProfileMapper.selectListByBiz(eq(CrmBizTypeEnum.CRM_CUSTOMER.getType()), anyCollection()))
                .thenReturn(List.of(
                        CrmTradeProfileDO.builder().bizId(10L).leadScore(90).build(),
                        CrmTradeProfileDO.builder().bizId(20L).leadScore(80).build(),
                        CrmTradeProfileDO.builder().bizId(30L).leadScore(70).build()));

        CrmTradeCustomerPageReqVO reqVO = new CrmTradeCustomerPageReqVO();
        reqVO.setPageNo(2);
        reqVO.setPageSize(1);
        reqVO.setMinLeadScore(70);

        PageResult<CrmTradeCustomerRespVO> result = queryService.getTradeCustomerPage(reqVO, 99L);
        assertEquals(3L, result.getTotal());
        assertEquals(20L, result.getList().get(0).getCustomerId());
    }

}
