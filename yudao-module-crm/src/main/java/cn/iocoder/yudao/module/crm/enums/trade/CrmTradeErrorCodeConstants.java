package cn.iocoder.yudao.module.crm.enums.trade;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/** FLEXFANG 外贸 CRM 扩展错误码。 */
public interface CrmTradeErrorCodeConstants {

    ErrorCode TRADE_RFQ_NOT_EXISTS = new ErrorCode(1_020_014_000, "外贸询价不存在");
    ErrorCode TRADE_RFQ_NO_EXISTS = new ErrorCode(1_020_014_001, "外贸询价编号已存在");
    ErrorCode TRADE_RFQ_BUSINESS_CUSTOMER_MISMATCH = new ErrorCode(1_020_014_002, "询价关联的商机不属于该客户");

}
