package cn.iocoder.yudao.module.crm.controller.admin.trade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 外贸报价 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class CrmTradeQuotationRespVO extends CrmTradeQuotationSaveReqVO {

    private Integer revision;
    private Long previousQuotationId;
    private String status;
    private BigDecimal subtotal;
    private BigDecimal totalAmount;
    private LocalDateTime sentTime;
    private LocalDateTime acceptedTime;
    private String rejectionReason;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
