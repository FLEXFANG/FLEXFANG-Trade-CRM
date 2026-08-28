package cn.iocoder.yudao.module.crm.service.trade;

import cn.iocoder.yudao.module.crm.controller.admin.trade.vo.CrmTradeRfqItemReqVO;
import cn.iocoder.yudao.module.crm.controller.admin.trade.vo.CrmTradeRfqSaveReqVO;
import cn.iocoder.yudao.module.crm.dal.dataobject.business.CrmBusinessDO;
import cn.iocoder.yudao.module.crm.dal.dataobject.trade.CrmTradeRfqDO;
import cn.iocoder.yudao.module.crm.dal.mysql.trade.CrmTradeQuotationMapper;
import cn.iocoder.yudao.module.crm.dal.mysql.trade.CrmTradeRfqItemMapper;
import cn.iocoder.yudao.module.crm.dal.mysql.trade.CrmTradeRfqMapper;
import cn.iocoder.yudao.module.crm.dal.mysql.trade.CrmTradeSampleMapper;
import cn.iocoder.yudao.module.crm.service.business.CrmBusinessService;
import cn.iocoder.yudao.module.crm.service.customer.CrmCustomerService;
import cn.iocoder.yudao.module.crm.service.product.CrmProductService;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
class CrmTradeRfqServiceImplTest {

    @InjectMocks private CrmTradeRfqServiceImpl rfqService;
    @Mock private CrmTradeRfqMapper rfqMapper;
    @Mock private CrmTradeRfqItemMapper rfqItemMapper;
    @Mock private CrmTradeSampleMapper sampleMapper;
    @Mock private CrmTradeQuotationMapper quotationMapper;
    @Mock private CrmCustomerService customerService;
    @Mock private CrmBusinessService businessService;
    @Mock private CrmProductService productService;
    @Mock private AdminUserApi adminUserApi;

    @Test
    void createRfq_savesHeaderAndSnapshotItems() {
        CrmTradeRfqSaveReqVO reqVO = reqVO();
        when(businessService.validateBusiness(20L)).thenReturn(CrmBusinessDO.builder().id(20L).customerId(10L).build());
        when(rfqMapper.selectByNo("RFQ-001")).thenReturn(null);
        doAnswer(invocation -> { CrmTradeRfqDO rfq = invocation.getArgument(0); rfq.setId(1L); return 1; })
                .when(rfqMapper).insert(any(CrmTradeRfqDO.class));
        Long id = rfqService.createRfq(reqVO);
        assertEquals(1L, id);
        verify(customerService).validateCustomer(10L);
        verify(productService).validProductList(List.of(100L));
        verify(rfqItemMapper).insertBatch(anyCollection());
        ArgumentCaptor<CrmTradeRfqDO> captor = ArgumentCaptor.forClass(CrmTradeRfqDO.class);
        verify(rfqMapper).insert(captor.capture());
        assertEquals("USD", captor.getValue().getCurrency());
    }

    @Test
    void updateRfq_replacesItems() {
        CrmTradeRfqSaveReqVO reqVO = reqVO(); reqVO.setId(1L);
        CrmTradeRfqDO existing = CrmTradeRfqDO.builder().id(1L).no("RFQ-001").customerId(10L).build();
        when(rfqMapper.selectById(1L)).thenReturn(existing);
        when(rfqMapper.selectByNo("RFQ-001")).thenReturn(existing);
        when(businessService.validateBusiness(20L)).thenReturn(CrmBusinessDO.builder().id(20L).customerId(10L).build());
        rfqService.updateRfq(reqVO);
        verify(rfqItemMapper).deleteByRfqId(1L);
        verify(rfqItemMapper).insertBatch(anyCollection());
    }

    @Test
    void createRfq_rejectsBusinessFromDifferentCustomer() {
        when(businessService.validateBusiness(20L)).thenReturn(CrmBusinessDO.builder().id(20L).customerId(999L).build());
        assertThrows(RuntimeException.class, () -> rfqService.createRfq(reqVO()));
    }

    @Test
    void createRfq_rejectsDuplicateNo() {
        when(businessService.validateBusiness(20L)).thenReturn(CrmBusinessDO.builder().id(20L).customerId(10L).build());
        when(rfqMapper.selectByNo("RFQ-001")).thenReturn(CrmTradeRfqDO.builder().id(99L).no("RFQ-001").build());
        assertThrows(RuntimeException.class, () -> rfqService.createRfq(reqVO()));
    }

    @Test
    void deleteRfq_rejectsWhenSampleExists() {
        when(rfqMapper.selectById(1L)).thenReturn(CrmTradeRfqDO.builder().id(1L).build());
        when(sampleMapper.selectCountByRfqId(1L)).thenReturn(1L);
        assertThrows(RuntimeException.class, () -> rfqService.deleteRfq(1L));
        verify(rfqMapper, never()).deleteById(1L);
    }

    @Test
    void deleteRfq_rejectsWhenQuotationExists() {
        when(rfqMapper.selectById(1L)).thenReturn(CrmTradeRfqDO.builder().id(1L).build());
        when(sampleMapper.selectCountByRfqId(1L)).thenReturn(0L);
        when(quotationMapper.selectCountByRfqId(1L)).thenReturn(1L);
        assertThrows(RuntimeException.class, () -> rfqService.deleteRfq(1L));
        verify(rfqMapper, never()).deleteById(1L);
    }

    private static CrmTradeRfqSaveReqVO reqVO() {
        CrmTradeRfqItemReqVO item = new CrmTradeRfqItemReqVO();
        item.setProductId(100L); item.setProductName("F901"); item.setQuantity(1000); item.setTargetPrice(new BigDecimal("15.50"));
        CrmTradeRfqSaveReqVO reqVO = new CrmTradeRfqSaveReqVO();
        reqVO.setNo("RFQ-001"); reqVO.setCustomerId(10L); reqVO.setBusinessId(20L); reqVO.setOwnerUserId(30L);
        reqVO.setStatus("QUOTING"); reqVO.setCurrency("USD"); reqVO.setIncoterm("FOB"); reqVO.setItems(List.of(item));
        return reqVO;
    }

}
