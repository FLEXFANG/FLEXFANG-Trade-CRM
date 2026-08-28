package cn.iocoder.yudao.module.crm.controller.admin.trade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 外贸 RFQ 明细")
@Data
public class CrmTradeRfqItemReqVO {

    @Schema(description = "CRM 产品编号", example = "1024")
    private Long productId;

    @Schema(description = "产品名称快照", requiredMode = Schema.RequiredMode.REQUIRED, example = "F901 Flip-up Helmet")
    @NotBlank(message = "产品名称不能为空")
    @Size(max = 255, message = "产品名称长度不能超过 255 个字符")
    private String productName;

    @Schema(description = "规格/颜色/镜片等要求")
    @Size(max = 512, message = "规格长度不能超过 512 个字符")
    private String specification;

    @Schema(description = "询价数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "1000")
    @Positive(message = "询价数量必须大于 0")
    private Integer quantity;

    @Schema(description = "目标单价", example = "15.50")
    @DecimalMin(value = "0.0", inclusive = true, message = "目标单价不能小于 0")
    private BigDecimal targetPrice;

    @Schema(description = "明细备注")
    @Size(max = 512, message = "明细备注长度不能超过 512 个字符")
    private String remark;

}
