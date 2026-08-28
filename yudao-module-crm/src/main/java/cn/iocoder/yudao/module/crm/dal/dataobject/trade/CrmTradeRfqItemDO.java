package cn.iocoder.yudao.module.crm.dal.dataobject.trade;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

/** 外贸 RFQ 询价明细快照。 */
@TableName("crm_trade_rfq_item")
@KeySequence("crm_trade_rfq_item_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrmTradeRfqItemDO extends BaseDO {

    @TableId
    private Long id;
    private Long rfqId;
    private Long productId;
    private String productName;
    private String specification;
    private Integer quantity;
    private BigDecimal targetPrice;
    private String remark;

}
