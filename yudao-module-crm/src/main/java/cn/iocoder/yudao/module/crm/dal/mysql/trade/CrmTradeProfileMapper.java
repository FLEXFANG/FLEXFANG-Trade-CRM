package cn.iocoder.yudao.module.crm.dal.mysql.trade;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.crm.dal.dataobject.trade.CrmTradeProfileDO;
import org.apache.ibatis.annotations.Mapper;

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

}
