package cn.iocoder.yudao.module.crm.controller.admin.trade.vo;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CrmTradeRfqSaveReqVOTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void close() {
        validatorFactory.close();
    }

    @Test
    void validRfq_passesValidation() {
        assertTrue(validator.validate(validReqVO()).isEmpty());
    }

    @Test
    void invalidStatus_isRejected() {
        CrmTradeRfqSaveReqVO reqVO = validReqVO();
        reqVO.setStatus("UNKNOWN");
        assertFalse(violationsFor(reqVO, "status").isEmpty());
    }

    @Test
    void emptyItems_isRejected() {
        CrmTradeRfqSaveReqVO reqVO = validReqVO();
        reqVO.setItems(List.of());
        assertFalse(violationsFor(reqVO, "items").isEmpty());
    }

    @Test
    void zeroQuantity_isRejected() {
        CrmTradeRfqSaveReqVO reqVO = validReqVO();
        reqVO.getItems().get(0).setQuantity(0);
        assertTrue(validator.validate(reqVO).stream()
                .map(v -> v.getPropertyPath().toString())
                .anyMatch(path -> path.contains("quantity")));
    }

    private static CrmTradeRfqSaveReqVO validReqVO() {
        CrmTradeRfqItemReqVO item = new CrmTradeRfqItemReqVO();
        item.setProductName("F901");
        item.setQuantity(100);
        CrmTradeRfqSaveReqVO reqVO = new CrmTradeRfqSaveReqVO();
        reqVO.setNo("RFQ-001");
        reqVO.setCustomerId(1L);
        reqVO.setOwnerUserId(2L);
        reqVO.setStatus("NEW");
        reqVO.setItems(List.of(item));
        return reqVO;
    }

    private static Set<ConstraintViolation<CrmTradeRfqSaveReqVO>> violationsFor(CrmTradeRfqSaveReqVO reqVO, String property) {
        Set<ConstraintViolation<CrmTradeRfqSaveReqVO>> violations = validator.validate(reqVO);
        violations.removeIf(v -> !property.equals(v.getPropertyPath().toString()));
        return violations;
    }

}
