package cn.iocoder.yudao.module.crm.service.trade;

import cn.iocoder.yudao.module.crm.controller.admin.trade.vo.CrmTradeProfileSaveReqVO;
import cn.iocoder.yudao.module.crm.dal.dataobject.trade.CrmTradeProfileDO;
import cn.iocoder.yudao.module.crm.dal.mysql.trade.CrmTradeProfileMapper;
import cn.iocoder.yudao.module.crm.enums.common.CrmBizTypeEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrmTradeProfileServiceImplTest {

    @InjectMocks
    private CrmTradeProfileServiceImpl tradeProfileService;

    @Mock
    private CrmTradeProfileMapper tradeProfileMapper;

    @Test
    void saveTradeProfile_create() {
        CrmTradeProfileSaveReqVO reqVO = createReqVO(CrmBizTypeEnum.CRM_CLUE.getType(), 100L);
        when(tradeProfileMapper.selectByBiz(reqVO.getBizType(), reqVO.getBizId())).thenReturn(null);
        doAnswer(invocation -> {
            CrmTradeProfileDO profile = invocation.getArgument(0);
            profile.setId(1000L);
            return 1;
        }).when(tradeProfileMapper).insert(any(CrmTradeProfileDO.class));

        Long id = tradeProfileService.saveTradeProfile(reqVO);

        assertEquals(1000L, id);
        ArgumentCaptor<CrmTradeProfileDO> captor = ArgumentCaptor.forClass(CrmTradeProfileDO.class);
        verify(tradeProfileMapper).insert(captor.capture());
        CrmTradeProfileDO saved = captor.getValue();
        assertEquals(reqVO.getBizType(), saved.getBizType());
        assertEquals(reqVO.getBizId(), saved.getBizId());
        assertEquals("CO", saved.getCountryCode());
        assertEquals("IMPORTER", saved.getCompanyType());
        assertEquals("META", saved.getSourceChannel());
        assertEquals("FOB", saved.getIncoterm());
        assertEquals("40HQ", saved.getContainerPotential());
        assertEquals(80, saved.getFclProbability());
        verify(tradeProfileMapper, never()).updateById(any(CrmTradeProfileDO.class));
    }

    @Test
    void saveTradeProfile_update() {
        CrmTradeProfileSaveReqVO reqVO = createReqVO(CrmBizTypeEnum.CRM_CUSTOMER.getType(), 200L);
        reqVO.setTargetPrice(new BigDecimal("16.25"));
        CrmTradeProfileDO existing = CrmTradeProfileDO.builder()
                .id(2000L)
                .bizType(reqVO.getBizType())
                .bizId(reqVO.getBizId())
                .targetPrice(new BigDecimal("17.00"))
                .build();
        when(tradeProfileMapper.selectByBiz(reqVO.getBizType(), reqVO.getBizId())).thenReturn(existing);
        when(tradeProfileMapper.updateById(any(CrmTradeProfileDO.class))).thenReturn(1);

        Long id = tradeProfileService.saveTradeProfile(reqVO);

        assertEquals(2000L, id);
        ArgumentCaptor<CrmTradeProfileDO> captor = ArgumentCaptor.forClass(CrmTradeProfileDO.class);
        verify(tradeProfileMapper).updateById(captor.capture());
        CrmTradeProfileDO updated = captor.getValue();
        assertEquals(2000L, updated.getId());
        assertEquals(new BigDecimal("16.25"), updated.getTargetPrice());
        assertEquals("Callao", updated.getDestinationPort());
        verify(tradeProfileMapper, never()).insert(any(CrmTradeProfileDO.class));
    }

    @Test
    void getTradeProfile_returnsMapperResult() {
        CrmTradeProfileDO expected = CrmTradeProfileDO.builder()
                .id(3000L)
                .bizType(CrmBizTypeEnum.CRM_BUSINESS.getType())
                .bizId(300L)
                .countryCode("PE")
                .build();
        when(tradeProfileMapper.selectByBiz(expected.getBizType(), expected.getBizId())).thenReturn(expected);

        CrmTradeProfileDO actual = tradeProfileService.getTradeProfile(expected.getBizType(), expected.getBizId());

        assertSame(expected, actual);
        verify(tradeProfileMapper).selectByBiz(expected.getBizType(), expected.getBizId());
    }

    private static CrmTradeProfileSaveReqVO createReqVO(Integer bizType, Long bizId) {
        CrmTradeProfileSaveReqVO reqVO = new CrmTradeProfileSaveReqVO();
        reqVO.setBizType(bizType);
        reqVO.setBizId(bizId);
        reqVO.setCountryCode("CO");
        reqVO.setRegion("Antioquia");
        reqVO.setCity("Medellin");
        reqVO.setCompanyType("IMPORTER");
        reqVO.setSourceChannel("META");
        reqVO.setWebsite("https://example.com");
        reqVO.setWhatsapp("+573001234567");
        reqVO.setImportExperience(true);
        reqVO.setAnnualPurchaseVolume(new BigDecimal("150000.00"));
        reqVO.setTargetProducts("F901 flip-up helmet");
        reqVO.setExpectedMoq(100);
        reqVO.setTargetPrice(new BigDecimal("15.50"));
        reqVO.setCurrency("USD");
        reqVO.setCertificationRequirement("DOT");
        reqVO.setIncoterm("FOB");
        reqVO.setDestinationPort("Callao");
        reqVO.setSampleStatus("SENT");
        reqVO.setContainerPotential("40HQ");
        reqVO.setFclProbability(80);
        reqVO.setLeadScore(85);
        reqVO.setRiskScore(15);
        reqVO.setNextAction("Send revised quotation");
        return reqVO;
    }

}
