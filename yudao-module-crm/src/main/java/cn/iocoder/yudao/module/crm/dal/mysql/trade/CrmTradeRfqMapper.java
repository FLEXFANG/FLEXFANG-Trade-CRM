package cn.iocoder.yudao.module.crm.dal.mysql.trade;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.MPJLambdaWrapperX;
import cn.iocoder.yudao.module.crm.controller.admin.trade.vo.CrmTradeRfqPageReqVO;
import cn.iocoder.yudao.module.crm.dal.dataobject.trade.CrmTradeRfqDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CrmTradeRfqMapper extends BaseMapperX<CrmTradeRfqDO> {

    default CrmTradeRfqDO selectByNo(String no) {
        return selectOne(CrmTradeRfqDO::getNo, no);
    }

    default PageResult<CrmTradeRfqDO> selectPage(CrmTradeRfqPageReqVO reqVO) {
        return selectPage(reqVO, new MPJLambdaWrapperX<CrmTradeRfqDO>()
                .eqIfPresent(CrmTradeRfqDO::getCustomerId, reqVO.getCustomerId())
                .eqIfPresent(CrmTradeRfqDO::getBusinessId, reqVO.getBusinessId())
                .eqIfPresent(CrmTradeRfqDO::getOwnerUserId, reqVO.getOwnerUserId())
                .eqIfPresent(CrmTradeRfqDO::getStatus, reqVO.getStatus())
                .eqIfPresent(CrmTradeRfqDO::getSourceChannel, reqVO.getSourceChannel())
                .likeIfPresent(CrmTradeRfqDO::getNo, reqVO.getNo())
                .orderByDesc(CrmTradeRfqDO::getId));
    }

}
