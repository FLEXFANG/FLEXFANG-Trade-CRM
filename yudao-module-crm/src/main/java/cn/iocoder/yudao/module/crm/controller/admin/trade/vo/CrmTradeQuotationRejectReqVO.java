package cn.iocoder.yudao.module.crm.controller.admin.trade.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CrmTradeQuotationRejectReqVO {

    @NotNull(message = "报价编号不能为空")
    private Long id;

    @NotBlank(message = "拒绝原因不能为空")
    @Size(max = 512, message = "拒绝原因长度不能超过 512 个字符")
    private String reason;

}
