package cn.iocoder.yudao.module.crm.service.trade;

import cn.iocoder.yudao.module.crm.controller.admin.customer.vo.customer.CrmCustomerImportExcelVO;
import cn.iocoder.yudao.module.crm.controller.admin.customer.vo.customer.CrmCustomerImportReqVO;
import cn.iocoder.yudao.module.crm.controller.admin.customer.vo.customer.CrmCustomerImportRespVO;
import cn.iocoder.yudao.module.crm.controller.admin.trade.vo.CrmTradeCustomerImportExcelVO;
import cn.iocoder.yudao.module.crm.controller.admin.trade.vo.CrmTradeProfileSaveReqVO;
import cn.iocoder.yudao.module.crm.dal.dataobject.customer.CrmCustomerDO;
import cn.iocoder.yudao.module.crm.dal.mysql.customer.CrmCustomerMapper;
import cn.iocoder.yudao.module.crm.service.customer.CrmCustomerService;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrmTradeCustomerImportServiceImplTest {

    @InjectMocks
    private CrmTradeCustomerImportServiceImpl importService;

    @Mock
    private CrmCustomerService customerService;
    @Mock
    private CrmCustomerMapper customerMapper;
    @Mock
    private CrmTradeProfileService tradeProfileService;

    private Validator validator;

    @BeforeEach
    void setUp() throws Exception {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        var field = CrmTradeCustomerImportServiceImpl.class.getDeclaredField("validator");
        field.setAccessible(true);
        field.set(importService, validator);
    }

    @Test
    void importCustomers_importsBaseCustomerAndTradeProfileAndRejectsInvalidTradeValue() {
        CrmTradeCustomerImportExcelVO valid = CrmTradeCustomerImportExcelVO.builder()
                .name("Andes Helmets").email("buyer@example.com")
                .countryCode("co").companyType("importer").sourceChannel("meta")
                .importExperience("YES").leadScore(85).riskScore(20).fclProbability(80)
                .containerPotential("40hq").build();
        CrmTradeCustomerImportExcelVO invalid = CrmTradeCustomerImportExcelVO.builder()
                .name("Bad Row").importExperience("MAYBE").build();
        CrmCustomerImportReqVO reqVO = CrmCustomerImportReqVO.builder().updateSupport(true).ownerUserId(99L).build();

        CrmCustomerImportRespVO baseResult = CrmCustomerImportRespVO.builder()
                .createCustomerNames(new ArrayList<>(List.of("Andes Helmets")))
                .updateCustomerNames(new ArrayList<>())
                .failureCustomerNames(new LinkedHashMap<>())
                .build();
        when(customerService.importCustomerList(anyList(), eq(reqVO))).thenReturn(baseResult);
        when(customerMapper.selectByCustomerName("Andes Helmets"))
                .thenReturn(CrmCustomerDO.builder().id(10L).name("Andes Helmets").build());

        CrmCustomerImportRespVO result = importService.importCustomers(List.of(valid, invalid), reqVO);

        assertEquals(List.of("Andes Helmets"), result.getCreateCustomerNames());
        assertTrue(result.getFailureCustomerNames().containsKey("Bad Row"));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<CrmCustomerImportExcelVO>> baseRowsCaptor = ArgumentCaptor.forClass(List.class);
        verify(customerService).importCustomerList(baseRowsCaptor.capture(), eq(reqVO));
        assertEquals(1, baseRowsCaptor.getValue().size());
        assertEquals("Andes Helmets", baseRowsCaptor.getValue().get(0).getName());

        ArgumentCaptor<CrmTradeProfileSaveReqVO> profileCaptor = ArgumentCaptor.forClass(CrmTradeProfileSaveReqVO.class);
        verify(tradeProfileService).saveTradeProfile(profileCaptor.capture());
        assertEquals(10L, profileCaptor.getValue().getBizId());
        assertEquals("CO", profileCaptor.getValue().getCountryCode());
        assertEquals("IMPORTER", profileCaptor.getValue().getCompanyType());
        assertEquals("META", profileCaptor.getValue().getSourceChannel());
        assertEquals(Boolean.TRUE, profileCaptor.getValue().getImportExperience());
        assertEquals("40HQ", profileCaptor.getValue().getContainerPotential());
        verify(customerMapper, never()).selectByCustomerName("Bad Row");
    }

}
