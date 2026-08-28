package cn.iocoder.yudao.module.crm.dal.mysql.trade;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.crm.dal.dataobject.trade.CrmTradeRfqItemDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CrmTradeRfqItemMapper extends BaseMapperX<CrmTradeRfqItemDO> {

    default List<CrmTradeRfqItemDO> selectListByRfqId(Long rfqId) {
        return selectList(CrmTradeRfqItemDO::getRfqId, rfqId);
    }

    default int deleteByRfqId(Long rfqId) {
        return delete(CrmTradeRfqItemDO::getRfqId, rfqId);
    }

}
