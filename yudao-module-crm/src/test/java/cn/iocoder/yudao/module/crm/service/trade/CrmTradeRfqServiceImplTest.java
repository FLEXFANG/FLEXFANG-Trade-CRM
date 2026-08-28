package cn.iocoder.yudao.module.crm.service.trade;

import cn.iocoder.yudao.module.crm.controller.admin.trade.vo.CrmTradeRfqItemReqVO;
import cn.iocoder.yudao.module.crm.controller.admin.trade.vo.CrmTradeRfqSaveReqVO;
import cn.iocoder.yudao.module.crm.dal.dataobject.business.CrmBusinessDO;
import cn.iocoder.yudao.module.crm.dal.dataobject.trade.CrmTradeRfqDO;
import cn.iocoder.yudao.module.crm.dal.mysql.trade.CrmTradeRfqItemMapper;
import cn.iocoder.yudao.module.crm.dal.mysql.trade.CrmTradeRfqMapper;
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

    @InjectMocks
    private CrmTradeRfqServiceImpl rfqService;

    @Mock private CrmTradeRfqMapper rfqMapper;
    @Mock private CrmTradeRfqItemMapper rfqItemMapper;
    @Mock private CrmCustomerService customerService;
    @Mock private CrmBusinessService businessService;
    @Mock private CrmProductService productService;
    @Mock private AdminUserApi adminUserApi;

    @Test
    void createRfq_savesHeaderAndSnapshotItems() {
        CrmTradeRfqSaveReqVO reqVO = reqVO();
        when(businessService.validateBusiness(20L)).thenReturn(CrmBusinessDO.builder().id(20L).customerId(10L).build());
        when(rfqMapper.selectByNo("RFQ-001")).thenReturn(null);
        doAnswer(invocation -> {
            CrmTradeRfqDO rfq = invocation.getArgument(0);
            rfq.setId(1L);
            return 1;
        }).when(rfqMapper).insert(any(CrmTradeRfqDO.class));

        Long id = rfqService.createRfq(reqVO);

        assertEquals(1L, id);
        verify(customerService).validateCustomer(10L);
        verify(businessService).validateBusiness(20L);
        verify(adminUserApi).validateUser(30L);
        verify(productService).validProductList(List.of(100L));
        verify(rfqItemMapper).insertBatch(anyCollection());
        ArgumentCaptor<CrmTradeRfqDO> captor = ArgumentCaptor.forClass(CrmTradeRfqDO.class);
        verify(rfqMapper).insert(captor.capture());
        assertEquals("USD", captor.getValue().getCurrency());
        assertEquals("FOB", captor.getValue().getIncoterm());
        assertEquals("Callao", captor.getValue().getDestinationPort());
    }

    @Test
    void updateRfq_replacesItems() {
        CrmTradeRfqSaveReqVO reqVO = reqVO();
        reqVO.setId(1L);
        CrmTradeRfqDO existing = CrmTradeRfqDO.builder().id(1L).no("RFQ-001").customerId(10L).build();
        when(rfqMapper.selectById(1L)).thenReturn(existing);
        when(rfqMapper.selectByNo("RFQ-001")).thenReturn(existing);
        when(businessService.validateBusiness(20L)).thenReturn(CrmBusinessDO.builder().id(20L).customerId(10L).build());
        when(rfqMapper.updateById(any(CrmTradeRfqDO.class))).thenReturn(1);

        rfqService.updateRfq(reqVO);

        verify(rfqMapper).updateById(any(CrmTradeRfqDO.class));
        verify(rfqItemMapper).deleteByRfqId(1L);
        verify(rfqItemMapper).insertBatch(anyCollection());
    }

    @Test
    void createRfq_rejectsBusinessFromDifferentCustomer() {
        CrmTradeRfqSaveReqVO reqVO = reqVO();
        when(businessService.validateBusiness(20L)).thenReturn(CrmBusinessDO.builder().id(20L).customerId(999L).build());

        assertThrows(RuntimeException.class, () -> rfqService.createRfq(reqVO));
        verify(rfqMapper, never()).insert(any(CrmTradeRfqDO.class));
    }

    @Test
    void createRfq_rejectsDuplicateNo() {
        CrmTradeRfqSaveReqVO reqVO = reqVO();
        when(businessService.validateBusiness(20L)).thenReturn(CrmBusinessDO.builder().id(20L).customerId(10L).build());
        when(rfqMapper.selectByNo("RFQ-001")).thenReturn(CrmTradeRfqDO.builder().id(99L).no("RFQ-001").build());

        assertThrows(RuntimeException.class, () -> rfqService.createRfq(reqVO));
        verify(rfqMapper, never()).insert(any(CrmTradeRfqDO.class));
    }

    private static CrmTradeRfqSaveReqVO reqVO() {
        CrmTradeRfqItemReqVO item = new CrmTradeRfqItemReqVO();
        item.setProductId(100L);
        item.setProductName("F901 Flip-up Helmet");
        item.setSpecification("Matte black / clear visor");
        item.setQuantity(1000);
        item.setTargetPrice(new BigDecimal("15.50"));

        CrmTradeRfqSaveReqVO reqVO = new CrmTradeRfqSaveReqVO();
        reqVO.setNo("RFQ-001");
        reqVO.setCustomerId(10L);
        reqVO.setBusinessId(20L);
        reqVO.setOwnerUserId(30L);
        reqVO.setSourceChannel("EMAIL");
        reqVO.setStatus("QUOTING");
        reqVO.setCurrency("USD");
        reqVO.setIncoterm("FOB");
        reqVO.setDestinationPort("Callao");
        reqVO.setCertificationRequirement("DOT");
        reqVO.setItems(List.of(item));
        return reqVO;
    }

}
