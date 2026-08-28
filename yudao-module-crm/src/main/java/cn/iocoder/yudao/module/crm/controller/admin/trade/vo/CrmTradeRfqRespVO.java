package cn.iocoder.yudao.module.crm.controller.admin.trade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 外贸 RFQ Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class CrmTradeRfqRespVO extends CrmTradeRfqSaveReqVO {

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
