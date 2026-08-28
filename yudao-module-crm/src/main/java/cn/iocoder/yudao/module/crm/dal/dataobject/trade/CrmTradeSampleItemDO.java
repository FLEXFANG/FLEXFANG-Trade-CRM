package cn.iocoder.yudao.module.crm.dal.dataobject.trade;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

/** 外贸样品明细快照。 */
@TableName("crm_trade_sample_item")
@KeySequence("crm_trade_sample_item_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrmTradeSampleItemDO extends BaseDO {

    @TableId
    private Long id;
    private Long sampleId;
    private Long productId;
    private String productName;
    private String specification;
    private String color;
    private Integer quantity;
    private BigDecimal unitPrice;

}
