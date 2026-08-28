package cn.iocoder.yudao.module.crm.dal.dataobject.trade;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 外贸报价主表，按 no + revision 保存不可变历史版本。 */
@TableName("crm_trade_quotation")
@KeySequence("crm_trade_quotation_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrmTradeQuotationDO extends BaseDO {

    @TableId
    private Long id;
    private String no;
    private Integer revision;
    private Long previousQuotationId;
    private Long rfqId;
    private Long customerId;
    private Long businessId;
    private Long ownerUserId;
    private String status;
    private LocalDateTime issueTime;
    private LocalDateTime validUntil;
    private String currency;
    private String incoterm;
    private String destinationPort;
    private String paymentTerms;
    private Integer leadTimeDays;
    private String packagingTerms;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal freight;
    private BigDecimal insurance;
    private BigDecimal otherCharge;
    private BigDecimal totalAmount;
    private LocalDateTime sentTime;
    private LocalDateTime acceptedTime;
    private String rejectionReason;
    private String remark;

}
