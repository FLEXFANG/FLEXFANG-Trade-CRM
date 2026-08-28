package cn.iocoder.yudao.module.crm.dal.dataobject.trade;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.crm.enums.common.CrmBizTypeEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

/**
 * CRM 外贸档案 DO。
 *
 * <p>通过 bizType + bizId 挂载到现有 CRM 线索、客户、商机上，避免侵入式修改上游核心表。</p>
 *
 * @author FLEXFANG
 */
@TableName("crm_trade_profile")
@KeySequence("crm_trade_profile_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrmTradeProfileDO extends BaseDO {

    /** 主键 */
    @TableId
    private Long id;

    /** CRM 业务类型，见 {@link CrmBizTypeEnum} */
    private Integer bizType;
    /** CRM 业务编号 */
    private Long bizId;

    /** ISO 国家/地区代码，例如 CO、PE、VN */
    private String countryCode;
    /** 州/省/大区 */
    private String region;
    /** 城市 */
    private String city;

    /** 客户类型：IMPORTER / DISTRIBUTOR / WHOLESALER / RETAIL_CHAIN / BRAND / OEM_BUYER / ONLINE_SELLER / OTHER */
    private String companyType;
    /** 来源渠道：SHOPIFY / META / WHATSAPP / EMAIL / LINKEDIN / FOUND / EXHIBITION / REFERRAL / MANUAL */
    private String sourceChannel;

    /** 公司网站 */
    private String website;
    /** WhatsApp */
    private String whatsapp;
    /** LinkedIn */
    private String linkedin;

    /** 是否有进口经验 */
    private Boolean importExperience;
    /** 预估年采购额 */
    private BigDecimal annualPurchaseVolume;

    /** 目标产品，第一版使用文本保存 */
    private String targetProducts;
    /** 预期 MOQ */
    private Integer expectedMoq;
    /** 目标单价 */
    private BigDecimal targetPrice;
    /** 币种，例如 USD、CNY、VND */
    private String currency;

    /** 认证要求 */
    private String certificationRequirement;
    /** 贸易条款：EXW / FOB / CIF / CFR / DDP / DAP */
    private String incoterm;
    /** 目的港 */
    private String destinationPort;

    /** 样品状态 */
    private String sampleStatus;
    /** 集装箱潜力：SAMPLE / LCL / 20GP / 40GP / 40HQ / UNKNOWN */
    private String containerPotential;
    /** 整柜成交概率，0-100 */
    private Integer fclProbability;

    /** 线索评分，0-100 */
    private Integer leadScore;
    /** 风险评分，0-100，越高风险越高 */
    private Integer riskScore;

    /** 下一步动作 */
    private String nextAction;
    /** 丢单原因 */
    private String lostReason;

}
