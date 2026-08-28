package cn.iocoder.yudao.module.crm.service.trade;

import cn.iocoder.yudao.module.crm.controller.admin.trade.vo.CrmTradeQuotationItemReqVO;
import cn.iocoder.yudao.module.crm.controller.admin.trade.vo.CrmTradeQuotationSaveReqVO;
import cn.iocoder.yudao.module.crm.dal.dataobject.business.CrmBusinessDO;
import cn.iocoder.yudao.module.crm.dal.dataobject.trade.CrmTradeQuotationDO;
import cn.iocoder.yudao.module.crm.dal.dataobject.trade.CrmTradeQuotationItemDO;
import cn.iocoder.yudao.module.crm.dal.dataobject.trade.CrmTradeRfqDO;
import cn.iocoder.yudao.module.crm.dal.mysql.trade.CrmTradeQuotationItemMapper;
import cn.iocoder.yudao.module.crm.dal.mysql.trade.CrmTradeQuotationMapper;
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
class CrmTradeQuotationServiceImplTest {

    @InjectMocks private CrmTradeQuotationServiceImpl quotationService;
    @Mock private CrmTradeQuotationMapper quotationMapper;
    @Mock private CrmTradeQuotationItemMapper quotationItemMapper;
    @Mock private CrmCustomerService customerService;
    @Mock private CrmBusinessService businessService;
    @Mock private CrmTradeRfqService rfqService;
    @Mock private CrmProductService productService;
    @Mock private AdminUserApi adminUserApi;

    @Test
    void createQuotation_calculatesAuthoritativeTotals() {
        CrmTradeQuotationSaveReqVO reqVO = reqVO();
        when(businessService.validateBusiness(20L)).thenReturn(CrmBusinessDO.builder().id(20L).customerId(10L).build());
        when(rfqService.validateRfq(40L)).thenReturn(CrmTradeRfqDO.builder().id(40L).customerId(10L).build());
        when(quotationMapper.selectMaxRevision("Q-001")).thenReturn(0);
        doAnswer(invocation -> { CrmTradeQuotationDO q = invocation.getArgument(0); q.setId(1L); return 1; })
                .when(quotationMapper).insert(any(CrmTradeQuotationDO.class));

        Long id = quotationService.createQuotation(reqVO);

        assertEquals(1L, id);
        ArgumentCaptor<CrmTradeQuotationDO> captor = ArgumentCaptor.forClass(CrmTradeQuotationDO.class);
        verify(quotationMapper).insert(captor.capture());
        CrmTradeQuotationDO saved = captor.getValue();
        assertEquals(0, new BigDecimal("15500.00").compareTo(saved.getSubtotal()));
        assertEquals(0, new BigDecimal("15925.00").compareTo(saved.getTotalAmount()));
        assertEquals(1, saved.getRevision());
        assertEquals("DRAFT", saved.getStatus());
        verify(productService).validProductList(List.of(100L));
        verify(quotationItemMapper).insertBatch(anyCollection());
    }

    @Test
    void createQuotation_rejectsDiscountGreaterThanSubtotal() {
        CrmTradeQuotationSaveReqVO reqVO = reqVO();
        reqVO.setDiscountAmount(new BigDecimal("20000"));
        when(businessService.validateBusiness(20L)).thenReturn(CrmBusinessDO.builder().id(20L).customerId(10L).build());
        when(rfqService.validateRfq(40L)).thenReturn(CrmTradeRfqDO.builder().id(40L).customerId(10L).build());
        when(quotationMapper.selectMaxRevision("Q-001")).thenReturn(0);

        assertThrows(RuntimeException.class, () -> quotationService.createQuotation(reqVO));
        verify(quotationMapper, never()).insert(any(CrmTradeQuotationDO.class));
    }

    @Test
    void updateQuotation_rejectsSentVersion() {
        CrmTradeQuotationSaveReqVO reqVO = reqVO();
        reqVO.setId(1L);
        when(quotationMapper.selectById(1L)).thenReturn(CrmTradeQuotationDO.builder()
                .id(1L).no("Q-001").revision(1).status("SENT").build());

        assertThrows(RuntimeException.class, () -> quotationService.updateQuotation(reqVO));
        verify(quotationMapper, never()).updateById(any(CrmTradeQuotationDO.class));
    }

    @Test
    void sendThenAccept_enforcesLifecycle() {
        CrmTradeQuotationDO draft = CrmTradeQuotationDO.builder().id(1L).status("DRAFT").build();
        CrmTradeQuotationDO sent = CrmTradeQuotationDO.builder().id(1L).status("SENT").build();
        when(quotationMapper.selectById(1L)).thenReturn(draft, sent);

        quotationService.sendQuotation(1L);
        quotationService.acceptQuotation(1L);

        ArgumentCaptor<CrmTradeQuotationDO> captor = ArgumentCaptor.forClass(CrmTradeQuotationDO.class);
        verify(quotationMapper, times(2)).updateById(captor.capture());
        assertEquals("SENT", captor.getAllValues().get(0).getStatus());
        assertEquals("ACCEPTED", captor.getAllValues().get(1).getStatus());
        assertNotNull(captor.getAllValues().get(1).getAcceptedTime());
    }

    @Test
    void reviseQuotation_clonesSentVersionAndSupersedesOld() {
        CrmTradeQuotationDO source = CrmTradeQuotationDO.builder()
                .id(1L).no("Q-001").revision(1).customerId(10L).ownerUserId(30L)
                .status("SENT").currency("USD").subtotal(new BigDecimal("15500"))
                .discountAmount(BigDecimal.ZERO).freight(new BigDecimal("500"))
                .insurance(BigDecimal.ZERO).otherCharge(new BigDecimal("25"))
                .totalAmount(new BigDecimal("16025")).build();
        CrmTradeQuotationItemDO sourceItem = CrmTradeQuotationItemDO.builder()
                .id(5L).quotationId(1L).productId(100L).productName("F901")
                .quantity(1000).unitPrice(new BigDecimal("15.5")).lineAmount(new BigDecimal("15500")).build();
        when(quotationMapper.selectById(1L)).thenReturn(source);
        when(quotationItemMapper.selectListByQuotationId(1L)).thenReturn(List.of(sourceItem));
        when(quotationMapper.selectMaxRevision("Q-001")).thenReturn(1);
        doAnswer(invocation -> { CrmTradeQuotationDO q = invocation.getArgument(0); q.setId(2L); return 1; })
                .when(quotationMapper).insert(any(CrmTradeQuotationDO.class));

        Long revisionId = quotationService.reviseQuotation(1L);

        assertEquals(2L, revisionId);
        ArgumentCaptor<CrmTradeQuotationDO> updateCaptor = ArgumentCaptor.forClass(CrmTradeQuotationDO.class);
        verify(quotationMapper).updateById(updateCaptor.capture());
        assertEquals("SUPERSEDED", updateCaptor.getValue().getStatus());
        ArgumentCaptor<CrmTradeQuotationDO> insertCaptor = ArgumentCaptor.forClass(CrmTradeQuotationDO.class);
        verify(quotationMapper).insert(insertCaptor.capture());
        assertEquals(2, insertCaptor.getValue().getRevision());
        assertEquals(1L, insertCaptor.getValue().getPreviousQuotationId());
        assertEquals("DRAFT", insertCaptor.getValue().getStatus());
        verify(quotationItemMapper).insertBatch(anyCollection());
    }

    @Test
    void createQuotation_rejectsRfqFromDifferentCustomer() {
        CrmTradeQuotationSaveReqVO reqVO = reqVO();
        when(businessService.validateBusiness(20L)).thenReturn(CrmBusinessDO.builder().id(20L).customerId(10L).build());
        when(rfqService.validateRfq(40L)).thenReturn(CrmTradeRfqDO.builder().id(40L).customerId(999L).build());

        assertThrows(RuntimeException.class, () -> quotationService.createQuotation(reqVO));
        verify(quotationMapper, never()).insert(any(CrmTradeQuotationDO.class));
    }

    private static CrmTradeQuotationSaveReqVO reqVO() {
        CrmTradeQuotationItemReqVO item = new CrmTradeQuotationItemReqVO();
        item.setProductId(100L);
        item.setProductName("F901 Flip-up Helmet");
        item.setSpecification("Matte black / clear visor");
        item.setQuantity(1000);
        item.setUnitPrice(new BigDecimal("15.50"));

        CrmTradeQuotationSaveReqVO reqVO = new CrmTradeQuotationSaveReqVO();
        reqVO.setNo("Q-001");
        reqVO.setRfqId(40L);
        reqVO.setCustomerId(10L);
        reqVO.setBusinessId(20L);
        reqVO.setOwnerUserId(30L);
        reqVO.setCurrency("USD");
        reqVO.setIncoterm("FOB");
        reqVO.setDestinationPort("Callao");
        reqVO.setPaymentTerms("30% deposit, 70% before shipment");
        reqVO.setLeadTimeDays(30);
        reqVO.setDiscountAmount(new BigDecimal("100"));
        reqVO.setFreight(new BigDecimal("500"));
        reqVO.setInsurance(BigDecimal.ZERO);
        reqVO.setOtherCharge(new BigDecimal("25"));
        reqVO.setItems(List.of(item));
        return reqVO;
    }

}
