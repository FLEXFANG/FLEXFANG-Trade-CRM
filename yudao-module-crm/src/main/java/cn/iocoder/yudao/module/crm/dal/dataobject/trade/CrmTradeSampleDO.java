package cn.iocoder.yudao.module.crm.dal.dataobject.trade;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 外贸样品主表。 */
@TableName("crm_trade_sample")
@KeySequence("crm_trade_sample_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrmTradeSampleDO extends BaseDO {

    @TableId
    private Long id;
    private String no;
    private Long customerId;
    private Long businessId;
    private Long rfqId;
    private Long ownerUserId;
    private String status;
    private LocalDateTime requestTime;
    private BigDecimal fee;
    private BigDecimal freight;
    private String currency;
    private Boolean refundableOnOrder;
    private String paymentStatus;
    private String carrier;
    private String trackingNo;
    private LocalDateTime shippedTime;
    private LocalDateTime receivedTime;
    private String approvalStatus;
    private String feedback;
    private String remark;

}
