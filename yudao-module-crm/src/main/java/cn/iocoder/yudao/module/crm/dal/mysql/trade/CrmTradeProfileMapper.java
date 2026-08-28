package cn.iocoder.yudao.module.crm.dal.mysql.trade;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.crm.dal.dataobject.trade.CrmTradeProfileDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * CRM 外贸档案 Mapper。
 *
 * @author FLEXFANG
 */
@Mapper
public interface CrmTradeProfileMapper extends BaseMapperX<CrmTradeProfileDO> {

    default CrmTradeProfileDO selectByBiz(Integer bizType, Long bizId) {
        return selectOne(CrmTradeProfileDO::getBizType, bizType,
                CrmTradeProfileDO::getBizId, bizId);
    }

    default List<CrmTradeProfileDO> selectListByBiz(Integer bizType, Collection<Long> bizIds) {
        if (CollUtil.isEmpty(bizIds)) {
            return Collections.emptyList();
        }
        return selectList(new LambdaQueryWrapperX<CrmTradeProfileDO>()
                .eq(CrmTradeProfileDO::getBizType, bizType)
                .in(CrmTradeProfileDO::getBizId, bizIds));
    }

}
