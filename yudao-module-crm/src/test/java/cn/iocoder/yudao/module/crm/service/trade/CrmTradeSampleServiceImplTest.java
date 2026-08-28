package cn.iocoder.yudao.module.crm.service.trade;

import cn.iocoder.yudao.module.crm.controller.admin.trade.vo.CrmTradeSampleItemReqVO;
import cn.iocoder.yudao.module.crm.controller.admin.trade.vo.CrmTradeSampleSaveReqVO;
import cn.iocoder.yudao.module.crm.dal.dataobject.business.CrmBusinessDO;
import cn.iocoder.yudao.module.crm.dal.dataobject.trade.CrmTradeRfqDO;
import cn.iocoder.yudao.module.crm.dal.dataobject.trade.CrmTradeSampleDO;
import cn.iocoder.yudao.module.crm.dal.mysql.trade.CrmTradeSampleItemMapper;
import cn.iocoder.yudao.module.crm.dal.mysql.trade.CrmTradeSampleMapper;
import cn.iocoder.yudao.module.crm.service.business.CrmBusinessService;
import cn.iocoder.yudao.module.crm.service.customer.CrmCustomerService;
import cn.iocoder.yudao.module.crm.service.product.CrmProductService;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrmTradeSampleServiceImplTest {

    @InjectMocks private CrmTradeSampleServiceImpl sampleService;
    @Mock private CrmTradeSampleMapper sampleMapper;
    @Mock private CrmTradeSampleItemMapper sampleItemMapper;
    @Mock private CrmCustomerService customerService;
    @Mock private CrmBusinessService businessService;
    @Mock private CrmTradeRfqService rfqService;
    @Mock private CrmProductService productService;
    @Mock private AdminUserApi adminUserApi;

    @Test
    void createSample_linkedRfq_savesCommercialAndLogisticsState() {
        CrmTradeSampleSaveReqVO reqVO = reqVO();
        when(businessService.validateBusiness(20L)).thenReturn(CrmBusinessDO.builder().id(20L).customerId(10L).build());
        when(rfqService.validateRfq(40L)).thenReturn(CrmTradeRfqDO.builder().id(40L).customerId(10L).build());
        when(sampleMapper.selectByNo("SMP-001")).thenReturn(null);
        doAnswer(invocation -> { CrmTradeSampleDO sample = invocation.getArgument(0); sample.setId(1L); return 1; })
                .when(sampleMapper).insert(any(CrmTradeSampleDO.class));

        Long id = sampleService.createSample(reqVO);

        assertEquals(1L, id);
        verify(customerService).validateCustomer(10L);
        verify(rfqService).validateRfq(40L);
        verify(productService).validProductList(List.of(100L));
        verify(sampleItemMapper).insertBatch(anyCollection());
    }

    @Test
    void createSample_withoutRfq_isAllowed() {
        CrmTradeSampleSaveReqVO reqVO = reqVO();
        reqVO.setRfqId(null);
        when(businessService.validateBusiness(20L)).thenReturn(CrmBusinessDO.builder().id(20L).customerId(10L).build());
        doAnswer(invocation -> { CrmTradeSampleDO sample = invocation.getArgument(0); sample.setId(2L); return 1; })
                .when(sampleMapper).insert(any(CrmTradeSampleDO.class));

        assertEquals(2L, sampleService.createSample(reqVO));
        verifyNoInteractions(rfqService);
    }

    @Test
    void createSample_rejectsRfqFromDifferentCustomer() {
        CrmTradeSampleSaveReqVO reqVO = reqVO();
        when(businessService.validateBusiness(20L)).thenReturn(CrmBusinessDO.builder().id(20L).customerId(10L).build());
        when(rfqService.validateRfq(40L)).thenReturn(CrmTradeRfqDO.builder().id(40L).customerId(999L).build());

        assertThrows(RuntimeException.class, () -> sampleService.createSample(reqVO));
        verify(sampleMapper, never()).insert(any(CrmTradeSampleDO.class));
    }

    @Test
    void updateSample_replacesItems() {
        CrmTradeSampleSaveReqVO reqVO = reqVO();
        reqVO.setId(1L);
        CrmTradeSampleDO existing = CrmTradeSampleDO.builder().id(1L).no("SMP-001").customerId(10L).build();
        when(sampleMapper.selectById(1L)).thenReturn(existing);
        when(sampleMapper.selectByNo("SMP-001")).thenReturn(existing);
        when(businessService.validateBusiness(20L)).thenReturn(CrmBusinessDO.builder().id(20L).customerId(10L).build());
        when(rfqService.validateRfq(40L)).thenReturn(CrmTradeRfqDO.builder().id(40L).customerId(10L).build());

        sampleService.updateSample(reqVO);

        verify(sampleMapper).updateById(any(CrmTradeSampleDO.class));
        verify(sampleItemMapper).deleteBySampleId(1L);
        verify(sampleItemMapper).insertBatch(anyCollection());
    }

    private static CrmTradeSampleSaveReqVO reqVO() {
        CrmTradeSampleItemReqVO item = new CrmTradeSampleItemReqVO();
        item.setProductId(100L);
        item.setProductName("F901 Flip-up Helmet");
        item.setSpecification("Matte black / clear visor");
        item.setColor("Matte Black");
        item.setQuantity(2);
        item.setUnitPrice(new BigDecimal("17.00"));

        CrmTradeSampleSaveReqVO reqVO = new CrmTradeSampleSaveReqVO();
        reqVO.setNo("SMP-001");
        reqVO.setCustomerId(10L);
        reqVO.setBusinessId(20L);
        reqVO.setRfqId(40L);
        reqVO.setOwnerUserId(30L);
        reqVO.setStatus("QUOTED");
        reqVO.setFee(new BigDecimal("34.00"));
        reqVO.setFreight(new BigDecimal("25.00"));
        reqVO.setCurrency("USD");
        reqVO.setRefundableOnOrder(true);
        reqVO.setPaymentStatus("UNPAID");
        reqVO.setApprovalStatus("PENDING");
        reqVO.setItems(List.of(item));
        return reqVO;
    }

}
