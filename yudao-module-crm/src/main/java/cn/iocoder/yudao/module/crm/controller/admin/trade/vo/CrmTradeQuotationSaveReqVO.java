package cn.iocoder.yudao.module.crm.controller.admin.trade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 外贸报价创建/修改 Request VO")
@Data
public class CrmTradeQuotationSaveReqVO {

    private Long id;

    @NotBlank(message = "报价编号不能为空")
    @Size(max = 64, message = "报价编号长度不能超过 64 个字符")
    private String no;

    private Long rfqId;

    @NotNull(message = "客户编号不能为空")
    private Long customerId;

    private Long businessId;

    @NotNull(message = "负责人不能为空")
    private Long ownerUserId;

    private LocalDateTime issueTime;
    private LocalDateTime validUntil;

    @NotBlank(message = "币种不能为空")
    @Size(max = 8, message = "币种长度不能超过 8 个字符")
    private String currency;

    @Pattern(regexp = "EXW|FOB|CIF|CFR|DDP|DAP", message = "贸易条款不合法")
    private String incoterm;

    @Size(max = 128, message = "目的港长度不能超过 128 个字符")
    private String destinationPort;

    @Size(max = 512, message = "付款条款长度不能超过 512 个字符")
    private String paymentTerms;

    @Min(value = 0, message = "交期天数不能小于 0")
    private Integer leadTimeDays;

    @Size(max = 512, message = "包装条款长度不能超过 512 个字符")
    private String packagingTerms;

    @DecimalMin(value = "0.0", inclusive = true, message = "优惠金额不能小于 0")
    private BigDecimal discountAmount;

    @DecimalMin(value = "0.0", inclusive = true, message = "运费不能小于 0")
    private BigDecimal freight;

    @DecimalMin(value = "0.0", inclusive = true, message = "保险费不能小于 0")
    private BigDecimal insurance;

    @DecimalMin(value = "0.0", inclusive = true, message = "其他费用不能小于 0")
    private BigDecimal otherCharge;

    @Size(max = 1000, message = "备注长度不能超过 1000 个字符")
    private String remark;

    @NotEmpty(message = "报价至少需要一个产品明细")
    @Valid
    private List<CrmTradeQuotationItemReqVO> items;

}
