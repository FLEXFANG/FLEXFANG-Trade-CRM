package cn.iocoder.yudao.module.crm.dal.mysql.trade;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.MPJLambdaWrapperX;
import cn.iocoder.yudao.module.crm.controller.admin.trade.vo.CrmTradeQuotationPageReqVO;
import cn.iocoder.yudao.module.crm.dal.dataobject.trade.CrmTradeQuotationDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CrmTradeQuotationMapper extends BaseMapperX<CrmTradeQuotationDO> {

    default Integer selectMaxRevision(String no) {
        List<CrmTradeQuotationDO> list = selectList(new MPJLambdaWrapperX<CrmTradeQuotationDO>()
                .eq(CrmTradeQuotationDO::getNo, no)
                .orderByDesc(CrmTradeQuotationDO::getRevision));
        return list.isEmpty() ? 0 : list.get(0).getRevision();
    }

    default Long selectCountByRfqId(Long rfqId) {
        return selectCount(CrmTradeQuotationDO::getRfqId, rfqId);
    }

    default PageResult<CrmTradeQuotationDO> selectPage(CrmTradeQuotationPageReqVO reqVO) {
        return selectPage(reqVO, new MPJLambdaWrapperX<CrmTradeQuotationDO>()
                .likeIfPresent(CrmTradeQuotationDO::getNo, reqVO.getNo())
                .eqIfPresent(CrmTradeQuotationDO::getRfqId, reqVO.getRfqId())
                .eqIfPresent(CrmTradeQuotationDO::getCustomerId, reqVO.getCustomerId())
                .eqIfPresent(CrmTradeQuotationDO::getBusinessId, reqVO.getBusinessId())
                .eqIfPresent(CrmTradeQuotationDO::getOwnerUserId, reqVO.getOwnerUserId())
                .eqIfPresent(CrmTradeQuotationDO::getStatus, reqVO.getStatus())
                .eqIfPresent(CrmTradeQuotationDO::getCurrency, reqVO.getCurrency())
                .orderByDesc(CrmTradeQuotationDO::getId));
    }

}
