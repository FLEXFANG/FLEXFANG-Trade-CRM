package cn.iocoder.yudao.module.crm.controller.admin.trade.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.validation.InEnum;
import cn.iocoder.yudao.module.crm.enums.common.CrmSceneTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 外贸客户筛选分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class CrmTradeCustomerPageReqVO extends PageParam {

    @Schema(description = "客户名称")
    private String name;

    @Schema(description = "手机号")
    private String mobile;

    @Schema(description = "CRM 数据场景：我负责的/我参与的等")
    @InEnum(CrmSceneTypeEnum.class)
    private Integer sceneType;

    @Schema(description = "联系状态：1 今天需联系，2 已逾期，3 今天已联系")
    private Integer contactStatus;

    @Schema(description = "ISO 国家/地区代码", example = "CO")
    @Size(max = 8)
    private String countryCode;

    @Schema(description = "客户类型", example = "IMPORTER")
    @Size(max = 32)
    private String companyType;

    @Schema(description = "来源渠道", example = "META")
    @Size(max = 32)
    private String sourceChannel;

    @Schema(description = "是否有进口经验")
    private Boolean importExperience;

    @Schema(description = "最低客户评分", example = "70")
    @Min(0) @Max(100)
    private Integer minLeadScore;

    @Schema(description = "最高风险评分", example = "50")
    @Min(0) @Max(100)
    private Integer maxRiskScore;

    @Schema(description = "最低整柜概率", example = "60")
    @Min(0) @Max(100)
    private Integer minFclProbability;

    @Schema(description = "整柜潜力", example = "40HQ")
    @Size(max = 16)
    private String containerPotential;

    @Schema(description = "只看有明确下一步动作的客户")
    private Boolean hasNextAction;

}
