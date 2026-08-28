package cn.iocoder.yudao.module.crm.controller.admin.trade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 外贸客户 Response VO")
@Data
public class CrmTradeCustomerRespVO {

    private Long customerId;
    private String customerName;
    private String mobile;
    private String telephone;
    private String email;
    private Long ownerUserId;
    private Boolean dealStatus;
    private LocalDateTime contactLastTime;
    private String contactLastContent;
    private LocalDateTime contactNextTime;

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
    private String containerPotential;
    private Integer fclProbability;
    private Integer leadScore;
    private Integer riskScore;
    private String nextAction;
    private String lostReason;

}
