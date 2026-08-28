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

class CrmTradeSampleSaveReqVOTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll static void setUp() { factory = Validation.buildDefaultValidatorFactory(); validator = factory.getValidator(); }
    @AfterAll static void close() { factory.close(); }

    @Test void validSample_passesValidation() { assertTrue(validator.validate(validReqVO()).isEmpty()); }

    @Test
    void invalidStatus_isRejected() {
        CrmTradeSampleSaveReqVO reqVO = validReqVO();
        reqVO.setStatus("UNKNOWN");
        assertFalse(validator.validate(reqVO).isEmpty());
    }

    @Test
    void negativeMoney_isRejected() {
        CrmTradeSampleSaveReqVO reqVO = validReqVO();
        reqVO.setFee(new BigDecimal("-1"));
        assertTrue(validator.validate(reqVO).stream().anyMatch(v -> v.getPropertyPath().toString().equals("fee")));
    }

    @Test
    void zeroQuantity_isRejected() {
        CrmTradeSampleSaveReqVO reqVO = validReqVO();
        reqVO.getItems().get(0).setQuantity(0);
        assertTrue(validator.validate(reqVO).stream().anyMatch(v -> v.getPropertyPath().toString().contains("quantity")));
    }

    private static CrmTradeSampleSaveReqVO validReqVO() {
        CrmTradeSampleItemReqVO item = new CrmTradeSampleItemReqVO();
        item.setProductName("F901");
        item.setQuantity(1);
        CrmTradeSampleSaveReqVO reqVO = new CrmTradeSampleSaveReqVO();
        reqVO.setNo("SMP-001");
        reqVO.setCustomerId(1L);
        reqVO.setOwnerUserId(2L);
        reqVO.setStatus("REQUESTED");
        reqVO.setCurrency("USD");
        reqVO.setRefundableOnOrder(true);
        reqVO.setPaymentStatus("UNPAID");
        reqVO.setApprovalStatus("PENDING");
        reqVO.setItems(List.of(item));
        return reqVO;
    }

}
