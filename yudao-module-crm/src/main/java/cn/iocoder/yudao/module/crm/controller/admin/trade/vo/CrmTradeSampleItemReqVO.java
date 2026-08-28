package cn.iocoder.yudao.module.crm.controller.admin.trade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 外贸样品明细")
@Data
public class CrmTradeSampleItemReqVO {

    private Long productId;

    @NotBlank(message = "样品产品名称不能为空")
    @Size(max = 255, message = "产品名称长度不能超过 255 个字符")
    private String productName;

    @Size(max = 512, message = "规格长度不能超过 512 个字符")
    private String specification;

    @Size(max = 128, message = "颜色长度不能超过 128 个字符")
    private String color;

    @Positive(message = "样品数量必须大于 0")
    private Integer quantity;

    @DecimalMin(value = "0.0", inclusive = true, message = "样品单价不能小于 0")
    private BigDecimal unitPrice;

}
