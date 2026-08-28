package cn.iocoder.yudao.module.crm.dal.mysql.trade;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.crm.dal.dataobject.trade.CrmTradeQuotationItemDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CrmTradeQuotationItemMapper extends BaseMapperX<CrmTradeQuotationItemDO> {

    default List<CrmTradeQuotationItemDO> selectListByQuotationId(Long quotationId) {
        return selectList(CrmTradeQuotationItemDO::getQuotationId, quotationId);
    }

    default int deleteByQuotationId(Long quotationId) {
        return delete(CrmTradeQuotationItemDO::getQuotationId, quotationId);
    }

}
