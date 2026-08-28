package cn.iocoder.yudao.module.crm.controller.admin.trade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 外贸 RFQ 创建/修改 Request VO")
@Data
public class CrmTradeRfqSaveReqVO {

    @Schema(description = "编号；更新时必填", example = "1024")
    private Long id;

    @Schema(description = "RFQ 编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "RFQ-2026-0001")
    @NotBlank(message = "RFQ 编号不能为空")
    @Size(max = 64, message = "RFQ 编号长度不能超过 64 个字符")
    private String no;

    @Schema(description = "客户编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "客户编号不能为空")
    private Long customerId;

    @Schema(description = "商机编号")
    private Long businessId;

    @Schema(description = "负责人编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "负责人不能为空")
    private Long ownerUserId;

    @Schema(description = "来源渠道", example = "EMAIL")
    @Size(max = 32, message = "来源渠道长度不能超过 32 个字符")
    private String sourceChannel;

    @Schema(description = "收到询价时间")
    private LocalDateTime receivedTime;

    @Schema(description = "客户要求回复时间")
    private LocalDateTime dueTime;

    @Schema(description = "状态", example = "QUOTING")
    @NotBlank(message = "RFQ 状态不能为空")
    @Pattern(regexp = "NEW|QUALIFYING|QUOTING|QUOTED|WON|LOST|CANCELLED", message = "RFQ 状态不合法")
    private String status;

    @Schema(description = "币种", example = "USD")
    @Size(max = 8, message = "币种长度不能超过 8 个字符")
    private String currency;

    @Schema(description = "贸易条款", example = "FOB")
    @Size(max = 16, message = "贸易条款长度不能超过 16 个字符")
    private String incoterm;

    @Schema(description = "目的港", example = "Callao")
    @Size(max = 128, message = "目的港长度不能超过 128 个字符")
    private String destinationPort;

    @Schema(description = "认证要求", example = "DOT / ECE / QCVN")
    @Size(max = 512, message = "认证要求长度不能超过 512 个字符")
    private String certificationRequirement;

    @Schema(description = "备注")
    @Size(max = 1000, message = "备注长度不能超过 1000 个字符")
    private String remark;

    @Schema(description = "询价明细", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "RFQ 至少需要一个产品明细")
    @Valid
    private List<CrmTradeRfqItemReqVO> items;

}
