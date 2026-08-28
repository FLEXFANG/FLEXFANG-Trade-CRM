package cn.iocoder.yudao.module.crm.controller.admin.trade.vo;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CrmTradeQuotationSaveReqVOTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll static void setUp() { factory = Validation.buildDefaultValidatorFactory(); validator = factory.getValidator(); }
    @AfterAll static void close() { factory.close(); }

    @Test void validQuotation_passesValidation() { assertTrue(validator.validate(validReqVO()).isEmpty()); }

    @Test
    void invalidIncoterm_isRejected() {
        CrmTradeQuotationSaveReqVO reqVO = validReqVO();
        reqVO.setIncoterm("INVALID");
        assertFalse(validator.validate(reqVO).isEmpty());
    }

    @Test
    void emptyItems_isRejected() {
        CrmTradeQuotationSaveReqVO reqVO = validReqVO();
        reqVO.setItems(List.of());
        assertTrue(validator.validate(reqVO).stream().anyMatch(v -> v.getPropertyPath().toString().equals("items")));
    }

    @Test
    void negativeCharges_areRejected() {
        CrmTradeQuotationSaveReqVO reqVO = validReqVO();
        reqVO.setFreight(new BigDecimal("-1"));
        assertTrue(validator.validate(reqVO).stream().anyMatch(v -> v.getPropertyPath().toString().equals("freight")));
    }

    @Test
    void zeroQuantity_isRejected() {
        CrmTradeQuotationSaveReqVO reqVO = validReqVO();
        reqVO.getItems().get(0).setQuantity(0);
        assertTrue(validator.validate(reqVO).stream().anyMatch(v -> v.getPropertyPath().toString().contains("quantity")));
    }

    private static CrmTradeQuotationSaveReqVO validReqVO() {
        CrmTradeQuotationItemReqVO item = new CrmTradeQuotationItemReqVO();
        item.setProductName("F901");
        item.setQuantity(100);
        item.setUnitPrice(new BigDecimal("15.50"));
        CrmTradeQuotationSaveReqVO reqVO = new CrmTradeQuotationSaveReqVO();
        reqVO.setNo("Q-001");
        reqVO.setCustomerId(1L);
        reqVO.setOwnerUserId(2L);
        reqVO.setCurrency("USD");
        reqVO.setIncoterm("FOB");
        reqVO.setItems(List.of(item));
        return reqVO;
    }

}
