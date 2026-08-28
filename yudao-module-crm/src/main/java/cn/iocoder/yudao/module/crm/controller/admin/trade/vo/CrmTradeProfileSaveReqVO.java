package cn.iocoder.yudao.module.crm.controller.admin.trade.vo;

import cn.iocoder.yudao.framework.common.validation.InEnum;
import cn.iocoder.yudao.module.crm.enums.trade.CrmTradeBizTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - CRM 外贸档案新增/修改 Request VO")
@Data
public class CrmTradeProfileSaveReqVO {

    @Schema(description = "CRM 业务类型：1 线索、2 客户、4 商机", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "业务类型不能为空")
    @InEnum(value = CrmTradeBizTypeEnum.class, message = "业务类型仅支持线索、客户、商机")
    private Integer bizType;

    @Schema(description = "CRM 业务编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "业务编号不能为空")
    private Long bizId;

    @Schema(description = "ISO 国家/地区代码", example = "CO")
    @Size(max = 8, message = "国家/地区代码长度不能超过 8 个字符")
    private String countryCode;

    @Schema(description = "州/省/大区", example = "Antioquia")
    @Size(max = 128, message = "州/省/大区长度不能超过 128 个字符")
    private String region;

    @Schema(description = "城市", example = "Medellin")
    @Size(max = 128, message = "城市长度不能超过 128 个字符")
    private String city;

    @Schema(description = "客户类型", example = "IMPORTER")
    @Size(max = 32, message = "客户类型长度不能超过 32 个字符")
    private String companyType;

    @Schema(description = "来源渠道", example = "META")
    @Size(max = 32, message = "来源渠道长度不能超过 32 个字符")
    private String sourceChannel;

    @Schema(description = "公司网站", example = "https://example.com")
    @Size(max = 512, message = "网站长度不能超过 512 个字符")
    private String website;

    @Schema(description = "WhatsApp", example = "+573001234567")
    @Size(max = 64, message = "WhatsApp 长度不能超过 64 个字符")
    private String whatsapp;

    @Schema(description = "LinkedIn", example = "https://linkedin.com/company/example")
    @Size(max = 512, message = "LinkedIn 长度不能超过 512 个字符")
    private String linkedin;

    @Schema(description = "是否有进口经验", example = "true")
    private Boolean importExperience;

    @Schema(description = "预估年采购额", example = "150000.00")
    private BigDecimal annualPurchaseVolume;

    @Schema(description = "目标产品", example = "F901 flip-up helmet, open-face helmet")
    @Size(max = 1000, message = "目标产品长度不能超过 1000 个字符")
    private String targetProducts;

    @Schema(description = "预期 MOQ", example = "100")
    @Min(value = 0, message = "MOQ 不能小于 0")
    private Integer expectedMoq;

    @Schema(description = "目标单价", example = "15.50")
    private BigDecimal targetPrice;

    @Schema(description = "币种", example = "USD")
    @Size(max = 8, message = "币种长度不能超过 8 个字符")
    private String currency;

    @Schema(description = "认证要求", example = "DOT / ECE / QCVN")
    @Size(max = 512, message = "认证要求长度不能超过 512 个字符")
    private String certificationRequirement;

    @Schema(description = "贸易条款", example = "FOB")
    @Size(max = 16, message = "贸易条款长度不能超过 16 个字符")
    private String incoterm;

    @Schema(description = "目的港", example = "Callao")
    @Size(max = 128, message = "目的港长度不能超过 128 个字符")
    private String destinationPort;

    @Schema(description = "样品状态", example = "SENT")
    @Size(max = 32, message = "样品状态长度不能超过 32 个字符")
    private String sampleStatus;

    @Schema(description = "集装箱潜力", example = "40HQ")
    @Size(max = 16, message = "集装箱潜力长度不能超过 16 个字符")
    private String containerPotential;

    @Schema(description = "整柜成交概率 0-100", example = "75")
    @Min(value = 0, message = "整柜成交概率不能小于 0")
    @Max(value = 100, message = "整柜成交概率不能大于 100")
    private Integer fclProbability;

    @Schema(description = "线索评分 0-100", example = "82")
    @Min(value = 0, message = "线索评分不能小于 0")
    @Max(value = 100, message = "线索评分不能大于 100")
    private Integer leadScore;

    @Schema(description = "风险评分 0-100", example = "20")
    @Min(value = 0, message = "风险评分不能小于 0")
    @Max(value = 100, message = "风险评分不能大于 100")
    private Integer riskScore;

    @Schema(description = "下一步动作", example = "Send revised FOB quotation on Friday")
    @Size(max = 512, message = "下一步动作长度不能超过 512 个字符")
    private String nextAction;

    @Schema(description = "丢单原因", example = "Target price not achievable")
    @Size(max = 512, message = "丢单原因长度不能超过 512 个字符")
    private String lostReason;

}
