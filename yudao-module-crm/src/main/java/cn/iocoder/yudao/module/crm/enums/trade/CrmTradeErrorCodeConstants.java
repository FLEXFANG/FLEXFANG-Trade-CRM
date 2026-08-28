package cn.iocoder.yudao.module.crm.enums.trade;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/** FLEXFANG 外贸 CRM 扩展错误码。 */
public interface CrmTradeErrorCodeConstants {

    ErrorCode TRADE_RFQ_NOT_EXISTS = new ErrorCode(1_020_014_000, "外贸询价不存在");
    ErrorCode TRADE_RFQ_NO_EXISTS = new ErrorCode(1_020_014_001, "外贸询价编号已存在");
    ErrorCode TRADE_RFQ_BUSINESS_CUSTOMER_MISMATCH = new ErrorCode(1_020_014_002, "询价关联的商机不属于该客户");
    ErrorCode TRADE_RFQ_DELETE_FAIL_SAMPLE_EXISTS = new ErrorCode(1_020_014_003, "询价已关联系统样品记录，不能删除");
    ErrorCode TRADE_RFQ_DELETE_FAIL_QUOTATION_EXISTS = new ErrorCode(1_020_014_004, "询价已关联系统报价记录，不能删除");

    ErrorCode TRADE_SAMPLE_NOT_EXISTS = new ErrorCode(1_020_015_000, "样品记录不存在");
    ErrorCode TRADE_SAMPLE_NO_EXISTS = new ErrorCode(1_020_015_001, "样品编号已存在");
    ErrorCode TRADE_SAMPLE_BUSINESS_CUSTOMER_MISMATCH = new ErrorCode(1_020_015_002, "样品关联的商机不属于该客户");
    ErrorCode TRADE_SAMPLE_RFQ_CUSTOMER_MISMATCH = new ErrorCode(1_020_015_003, "样品关联的 RFQ 不属于该客户");

    ErrorCode TRADE_QUOTATION_NOT_EXISTS = new ErrorCode(1_020_016_000, "外贸报价不存在");
    ErrorCode TRADE_QUOTATION_NO_EXISTS = new ErrorCode(1_020_016_001, "报价编号已存在，请使用修订功能创建新版本");
    ErrorCode TRADE_QUOTATION_BUSINESS_CUSTOMER_MISMATCH = new ErrorCode(1_020_016_002, "报价关联的商机不属于该客户");
    ErrorCode TRADE_QUOTATION_RFQ_CUSTOMER_MISMATCH = new ErrorCode(1_020_016_003, "报价关联的 RFQ 不属于该客户");
    ErrorCode TRADE_QUOTATION_NOT_EDITABLE = new ErrorCode(1_020_016_004, "只有草稿报价可以修改或删除");
    ErrorCode TRADE_QUOTATION_INVALID_TRANSITION = new ErrorCode(1_020_016_005, "当前报价状态不允许执行该操作");
    ErrorCode TRADE_QUOTATION_DISCOUNT_TOO_LARGE = new ErrorCode(1_020_016_006, "报价优惠金额不能大于产品小计");

}
