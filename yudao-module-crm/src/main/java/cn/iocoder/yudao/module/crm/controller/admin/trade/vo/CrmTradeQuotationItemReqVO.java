package cn.iocoder.yudao.module.crm.controller.admin.trade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 外贸报价明细")
@Data
public class CrmTradeQuotationItemReqVO {

    private Long productId;

    @NotBlank(message = "报价产品名称不能为空")
    @Size(max = 255, message = "产品名称长度不能超过 255 个字符")
    private String productName;

    @Size(max = 512, message = "规格长度不能超过 512 个字符")
    private String specification;

    @NotNull(message = "报价数量不能为空")
    @Positive(message = "报价数量必须大于 0")
    private Integer quantity;

    @NotNull(message = "报价单价不能为空")
    @DecimalMin(value = "0.0", inclusive = true, message = "报价单价不能小于 0")
    private BigDecimal unitPrice;

}
