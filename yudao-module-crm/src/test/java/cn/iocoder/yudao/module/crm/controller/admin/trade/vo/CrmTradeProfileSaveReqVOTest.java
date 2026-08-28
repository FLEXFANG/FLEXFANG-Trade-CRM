package cn.iocoder.yudao.module.crm.controller.admin.trade.vo;

import cn.iocoder.yudao.module.crm.enums.common.CrmBizTypeEnum;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CrmTradeProfileSaveReqVOTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidator() {
        validatorFactory.close();
    }

    @Test
    void bizType_allowsOnlyClueCustomerBusiness() {
        for (Integer bizType : new Integer[]{
                CrmBizTypeEnum.CRM_CLUE.getType(),
                CrmBizTypeEnum.CRM_CUSTOMER.getType(),
                CrmBizTypeEnum.CRM_BUSINESS.getType()}) {
            CrmTradeProfileSaveReqVO reqVO = minimalReqVO(bizType);
            assertTrue(violationsFor(reqVO, "bizType").isEmpty(), "bizType should be valid: " + bizType);
        }
    }

    @Test
    void bizType_rejectsContactAndOtherCrmObjects() {
        CrmTradeProfileSaveReqVO reqVO = minimalReqVO(CrmBizTypeEnum.CRM_CONTACT.getType());

        assertFalse(violationsFor(reqVO, "bizType").isEmpty());
    }

    @Test
    void scores_rejectValuesOutsideZeroToHundred() {
        CrmTradeProfileSaveReqVO reqVO = minimalReqVO(CrmBizTypeEnum.CRM_CLUE.getType());
        reqVO.setFclProbability(101);
        reqVO.setLeadScore(-1);
        reqVO.setRiskScore(101);

        assertFalse(violationsFor(reqVO, "fclProbability").isEmpty());
        assertFalse(violationsFor(reqVO, "leadScore").isEmpty());
        assertFalse(violationsFor(reqVO, "riskScore").isEmpty());
    }

    private static CrmTradeProfileSaveReqVO minimalReqVO(Integer bizType) {
        CrmTradeProfileSaveReqVO reqVO = new CrmTradeProfileSaveReqVO();
        reqVO.setBizType(bizType);
        reqVO.setBizId(100L);
        return reqVO;
    }

    private static Set<ConstraintViolation<CrmTradeProfileSaveReqVO>> violationsFor(
            CrmTradeProfileSaveReqVO reqVO, String propertyName) {
        Set<ConstraintViolation<CrmTradeProfileSaveReqVO>> violations = validator.validate(reqVO);
        violations.removeIf(violation -> !propertyName.equals(violation.getPropertyPath().toString()));
        return violations;
    }

}
