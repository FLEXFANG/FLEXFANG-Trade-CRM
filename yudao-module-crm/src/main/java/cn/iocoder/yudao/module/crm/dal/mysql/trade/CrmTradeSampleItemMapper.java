package cn.iocoder.yudao.module.crm.dal.mysql.trade;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.crm.dal.dataobject.trade.CrmTradeSampleItemDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CrmTradeSampleItemMapper extends BaseMapperX<CrmTradeSampleItemDO> {

    default List<CrmTradeSampleItemDO> selectListBySampleId(Long sampleId) {
        return selectList(CrmTradeSampleItemDO::getSampleId, sampleId);
    }

    default int deleteBySampleId(Long sampleId) {
        return delete(CrmTradeSampleItemDO::getSampleId, sampleId);
    }

}
