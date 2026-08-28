package cn.iocoder.yudao.module.crm.dal.dataobject.trade;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/** 外贸 RFQ 询价主表。 */
@TableName("crm_trade_rfq")
@KeySequence("crm_trade_rfq_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrmTradeRfqDO extends BaseDO {

    @TableId
    private Long id;
    private String no;
    private Long customerId;
    private Long businessId;
    private Long ownerUserId;
    private String sourceChannel;
    private LocalDateTime receivedTime;
    private LocalDateTime dueTime;
    private String status;
    private String currency;
    private String incoterm;
    private String destinationPort;
    private String certificationRequirement;
    private String remark;

}
