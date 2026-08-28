package cn.iocoder.yudao.module.crm.controller.admin.trade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - CRM 外贸档案 Response VO")
@Data
public class CrmTradeProfileRespVO {

    @Schema(description = "主键", example = "1024")
    private Long id;

    @Schema(description = "CRM 业务类型：1 线索、2 客户、4 商机", example = "2")
    private Integer bizType;

    @Schema(description = "CRM 业务编号", example = "2048")
    private Long bizId;

    private String countryCode;
    private String region;
    private String city;
    private String companyType;
    private String sourceChannel;
    private String website;
    private String whatsapp;
    private String linkedin;
    private Boolean importExperience;
    private BigDecimal annualPurchaseVolume;
    private String targetProducts;
    private Integer expectedMoq;
    private BigDecimal targetPrice;
    private String currency;
    private String certificationRequirement;
    private String incoterm;
    private String destinationPort;
    private String sampleStatus;
    private String containerPotential;
    private Integer fclProbability;
    private Integer leadScore;
    private Integer riskScore;
    private String nextAction;
    private String lostReason;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
