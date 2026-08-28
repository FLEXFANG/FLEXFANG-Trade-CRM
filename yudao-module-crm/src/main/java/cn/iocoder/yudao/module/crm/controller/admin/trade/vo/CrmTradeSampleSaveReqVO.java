package cn.iocoder.yudao.module.crm.controller.admin.trade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 外贸样品创建/修改 Request VO")
@Data
public class CrmTradeSampleSaveReqVO {

    private Long id;

    @NotBlank(message = "样品编号不能为空")
    @Size(max = 64, message = "样品编号长度不能超过 64 个字符")
    private String no;

    @NotNull(message = "客户编号不能为空")
    private Long customerId;

    private Long businessId;
    private Long rfqId;

    @NotNull(message = "负责人不能为空")
    private Long ownerUserId;

    @NotBlank(message = "样品状态不能为空")
    @Pattern(regexp = "REQUESTED|QUOTED|PAID|PREPARING|SENT|RECEIVED|APPROVED|REJECTED|CANCELLED", message = "样品状态不合法")
    private String status;

    private LocalDateTime requestTime;

    @DecimalMin(value = "0.0", inclusive = true, message = "样品费不能小于 0")
    private BigDecimal fee;

    @DecimalMin(value = "0.0", inclusive = true, message = "运费不能小于 0")
    private BigDecimal freight;

    @NotBlank(message = "币种不能为空")
    @Size(max = 8, message = "币种长度不能超过 8 个字符")
    private String currency;

    @NotNull(message = "是否下单返还样品费不能为空")
    private Boolean refundableOnOrder;

    @NotBlank(message = "付款状态不能为空")
    @Pattern(regexp = "UNPAID|PAID|WAIVED|REFUND_PENDING|REFUNDED", message = "付款状态不合法")
    private String paymentStatus;

    @Size(max = 128, message = "物流商长度不能超过 128 个字符")
    private String carrier;

    @Size(max = 128, message = "物流单号长度不能超过 128 个字符")
    private String trackingNo;

    private LocalDateTime shippedTime;
    private LocalDateTime receivedTime;

    @NotBlank(message = "样品确认状态不能为空")
    @Pattern(regexp = "PENDING|APPROVED|REJECTED", message = "样品确认状态不合法")
    private String approvalStatus;

    @Size(max = 1000, message = "样品反馈长度不能超过 1000 个字符")
    private String feedback;

    @Size(max = 1000, message = "备注长度不能超过 1000 个字符")
    private String remark;

    @NotEmpty(message = "样品至少需要一个产品明细")
    @Valid
    private List<CrmTradeSampleItemReqVO> items;

}
