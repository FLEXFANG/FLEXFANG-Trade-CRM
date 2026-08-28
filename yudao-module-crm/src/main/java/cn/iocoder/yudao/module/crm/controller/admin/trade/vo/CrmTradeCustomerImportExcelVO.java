package cn.iocoder.yudao.module.crm.controller.admin.trade.vo;

import cn.idev.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 外贸客户 Excel 导入行。
 *
 * <p>列名采用中英双语，便于外贸团队直接维护。</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrmTradeCustomerImportExcelVO {

    @ExcelProperty("Customer Name / 客户名称")
    private String name;

    @ExcelProperty("Mobile / 手机")
    private String mobile;

    @ExcelProperty("Telephone / 电话")
    private String telephone;

    @ExcelProperty("Email / 邮箱")
    private String email;

    @ExcelProperty("Address / 地址")
    private String detailAddress;

    @ExcelProperty("Remark / 备注")
    private String remark;

    @ExcelProperty("Country Code / 国家代码")
    private String countryCode;

    @ExcelProperty("Region / 州省大区")
    private String region;

    @ExcelProperty("City / 城市")
    private String city;

    @ExcelProperty("Buyer Type / 客户类型")
    private String companyType;

    @ExcelProperty("Source Channel / 来源渠道")
    private String sourceChannel;

    @ExcelProperty("Website / 官网")
    private String website;

    @ExcelProperty("WhatsApp")
    private String whatsapp;

    @ExcelProperty("LinkedIn")
    private String linkedin;

    @ExcelProperty("Import Experience / 有进口经验")
    private String importExperience;

    @ExcelProperty("Annual Purchase / 预估年采购额")
    private BigDecimal annualPurchaseVolume;

    @ExcelProperty("Target Products / 目标产品")
    private String targetProducts;

    @ExcelProperty("Expected MOQ / 预期MOQ")
    private Integer expectedMoq;

    @ExcelProperty("Target Price / 目标单价")
    private BigDecimal targetPrice;

    @ExcelProperty("Currency / 币种")
    private String currency;

    @ExcelProperty("Certification / 认证要求")
    private String certificationRequirement;

    @ExcelProperty("Incoterm / 贸易条款")
    private String incoterm;

    @ExcelProperty("Destination Port / 目的港")
    private String destinationPort;

    @ExcelProperty("Container Potential / 整柜潜力")
    private String containerPotential;

    @ExcelProperty("FCL Probability / 整柜概率")
    private Integer fclProbability;

    @ExcelProperty("Lead Score / 客户评分")
    private Integer leadScore;

    @ExcelProperty("Risk Score / 风险评分")
    private Integer riskScore;

    @ExcelProperty("Next Action / 下一步动作")
    private String nextAction;

}
