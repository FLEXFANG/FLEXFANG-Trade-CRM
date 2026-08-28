package cn.iocoder.yudao.module.crm.service.trade;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.crm.controller.admin.customer.vo.customer.CrmCustomerImportExcelVO;
import cn.iocoder.yudao.module.crm.controller.admin.customer.vo.customer.CrmCustomerImportReqVO;
import cn.iocoder.yudao.module.crm.controller.admin.customer.vo.customer.CrmCustomerImportRespVO;
import cn.iocoder.yudao.module.crm.controller.admin.trade.vo.CrmTradeCustomerImportExcelVO;
import cn.iocoder.yudao.module.crm.controller.admin.trade.vo.CrmTradeProfileSaveReqVO;
import cn.iocoder.yudao.module.crm.dal.dataobject.customer.CrmCustomerDO;
import cn.iocoder.yudao.module.crm.dal.mysql.customer.CrmCustomerMapper;
import cn.iocoder.yudao.module.crm.enums.common.CrmBizTypeEnum;
import cn.iocoder.yudao.module.crm.service.customer.CrmCustomerService;
import jakarta.annotation.Resource;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.*;

/**
 * 外贸客户 Excel 导入 Service 实现。
 *
 * <p>复用芋道原生客户导入逻辑处理客户、负责人、数据权限和重复客户；成功后再 Upsert Trade Profile。</p>
 *
 * @author FLEXFANG
 */
@Service
@Validated
public class CrmTradeCustomerImportServiceImpl implements CrmTradeCustomerImportService {

    @Resource
    private CrmCustomerService customerService;
    @Resource
    private CrmCustomerMapper customerMapper;
    @Resource
    private CrmTradeProfileService tradeProfileService;
    @Resource
    private Validator validator;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CrmCustomerImportRespVO importCustomers(List<CrmTradeCustomerImportExcelVO> rows,
                                                   CrmCustomerImportReqVO importReqVO) {
        List<CrmTradeCustomerImportExcelVO> validRows = new ArrayList<>();
        Map<String, String> preValidationFailures = new LinkedHashMap<>();
        if (rows != null) {
            for (CrmTradeCustomerImportExcelVO row : rows) {
                if (row == null || StrUtil.isBlank(row.getName())) {
                    continue;
                }
                String validationError = validateTradeRow(row);
                if (validationError != null) {
                    preValidationFailures.put(row.getName(), validationError);
                } else {
                    validRows.add(row);
                }
            }
        }

        if (CollUtil.isEmpty(validRows)) {
            return CrmCustomerImportRespVO.builder()
                    .createCustomerNames(new ArrayList<>())
                    .updateCustomerNames(new ArrayList<>())
                    .failureCustomerNames(preValidationFailures)
                    .build();
        }

        List<CrmCustomerImportExcelVO> baseRows = validRows.stream().map(this::toBaseCustomerRow).toList();
        CrmCustomerImportRespVO result = customerService.importCustomerList(baseRows, importReqVO);
        result.getFailureCustomerNames().putAll(preValidationFailures);

        Set<String> successfulNames = new HashSet<>();
        successfulNames.addAll(result.getCreateCustomerNames());
        successfulNames.addAll(result.getUpdateCustomerNames());

        for (CrmTradeCustomerImportExcelVO row : validRows) {
            if (!successfulNames.contains(row.getName()) || !hasTradeData(row)) {
                continue;
            }
            CrmCustomerDO customer = customerMapper.selectByCustomerName(row.getName());
            if (customer == null) {
                continue;
            }
            tradeProfileService.saveTradeProfile(toTradeProfile(row, customer.getId()));
        }
        return result;
    }

    private String validateTradeRow(CrmTradeCustomerImportExcelVO row) {
        CrmTradeProfileSaveReqVO profile;
        try {
            profile = toTradeProfile(row, 1L);
        } catch (IllegalArgumentException ex) {
            return ex.getMessage();
        }
        Set<ConstraintViolation<CrmTradeProfileSaveReqVO>> violations = validator.validate(profile);
        return violations.stream().map(ConstraintViolation::getMessage).findFirst().orElse(null);
    }

    private CrmCustomerImportExcelVO toBaseCustomerRow(CrmTradeCustomerImportExcelVO row) {
        return CrmCustomerImportExcelVO.builder()
                .name(row.getName())
                .mobile(row.getMobile())
                .telephone(row.getTelephone())
                .email(row.getEmail())
                .detailAddress(row.getDetailAddress())
                .remark(row.getRemark())
                .build();
    }

    private CrmTradeProfileSaveReqVO toTradeProfile(CrmTradeCustomerImportExcelVO row, Long customerId) {
        CrmTradeProfileSaveReqVO profile = new CrmTradeProfileSaveReqVO();
        profile.setBizType(CrmBizTypeEnum.CRM_CUSTOMER.getType());
        profile.setBizId(customerId);
        profile.setCountryCode(normalizeUpper(row.getCountryCode()));
        profile.setRegion(trim(row.getRegion()));
        profile.setCity(trim(row.getCity()));
        profile.setCompanyType(normalizeUpper(row.getCompanyType()));
        profile.setSourceChannel(normalizeUpper(row.getSourceChannel()));
        profile.setWebsite(trim(row.getWebsite()));
        profile.setWhatsapp(trim(row.getWhatsapp()));
        profile.setLinkedin(trim(row.getLinkedin()));
        profile.setImportExperience(parseBoolean(row.getImportExperience()));
        profile.setAnnualPurchaseVolume(row.getAnnualPurchaseVolume());
        profile.setTargetProducts(trim(row.getTargetProducts()));
        profile.setExpectedMoq(row.getExpectedMoq());
        profile.setTargetPrice(row.getTargetPrice());
        profile.setCurrency(normalizeUpper(row.getCurrency()));
        profile.setCertificationRequirement(trim(row.getCertificationRequirement()));
        profile.setIncoterm(normalizeUpper(row.getIncoterm()));
        profile.setDestinationPort(trim(row.getDestinationPort()));
        profile.setContainerPotential(normalizeUpper(row.getContainerPotential()));
        profile.setFclProbability(row.getFclProbability());
        profile.setLeadScore(row.getLeadScore());
        profile.setRiskScore(row.getRiskScore());
        profile.setNextAction(trim(row.getNextAction()));
        return profile;
    }

    private boolean hasTradeData(CrmTradeCustomerImportExcelVO row) {
        return StreamSupport.hasAny(
                row.getCountryCode(), row.getRegion(), row.getCity(), row.getCompanyType(), row.getSourceChannel(),
                row.getWebsite(), row.getWhatsapp(), row.getLinkedin(), row.getImportExperience(),
                row.getAnnualPurchaseVolume(), row.getTargetProducts(), row.getExpectedMoq(), row.getTargetPrice(),
                row.getCurrency(), row.getCertificationRequirement(), row.getIncoterm(), row.getDestinationPort(),
                row.getContainerPotential(), row.getFclProbability(), row.getLeadScore(), row.getRiskScore(), row.getNextAction());
    }

    private Boolean parseBoolean(String value) {
        if (StrUtil.isBlank(value)) {
            return null;
        }
        String normalized = value.trim().toUpperCase(Locale.ROOT);
        if (Set.of("YES", "Y", "TRUE", "1", "是", "有").contains(normalized)) {
            return true;
        }
        if (Set.of("NO", "N", "FALSE", "0", "否", "无").contains(normalized)) {
            return false;
        }
        throw new IllegalArgumentException("有进口经验仅支持 YES/NO、TRUE/FALSE、是/否");
    }

    private String normalizeUpper(String value) {
        String trimmed = trim(value);
        return trimmed == null ? null : trimmed.toUpperCase(Locale.ROOT);
    }

    private String trim(String value) {
        return StrUtil.isBlank(value) ? null : value.trim();
    }

    /** 小型空值判断工具，避免为导入逻辑引入额外依赖。 */
    private static final class StreamSupport {
        private static boolean hasAny(Object... values) {
            for (Object value : values) {
                if (value instanceof String text) {
                    if (!text.isBlank()) {
                        return true;
                    }
                } else if (value != null) {
                    return true;
                }
            }
            return false;
        }
    }

}
