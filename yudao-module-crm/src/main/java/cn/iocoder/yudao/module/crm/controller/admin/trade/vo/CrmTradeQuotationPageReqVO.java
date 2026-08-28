package cn.iocoder.yudao.module.crm.controller.admin.trade.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 外贸报价分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class CrmTradeQuotationPageReqVO extends PageParam {

    private String no;
    private Long rfqId;
    private Long customerId;
    private Long businessId;
    private Long ownerUserId;
    private String status;
    private String currency;

}
