package cn.iocoder.yudao.module.crm.dal.mysql.trade;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.MPJLambdaWrapperX;
import cn.iocoder.yudao.module.crm.controller.admin.trade.vo.CrmTradeSamplePageReqVO;
import cn.iocoder.yudao.module.crm.dal.dataobject.trade.CrmTradeSampleDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CrmTradeSampleMapper extends BaseMapperX<CrmTradeSampleDO> {

    default CrmTradeSampleDO selectByNo(String no) {
        return selectOne(CrmTradeSampleDO::getNo, no);
    }

    default Long selectCountByRfqId(Long rfqId) {
        return selectCount(CrmTradeSampleDO::getRfqId, rfqId);
    }

    default PageResult<CrmTradeSampleDO> selectPage(CrmTradeSamplePageReqVO reqVO) {
        return selectPage(reqVO, new MPJLambdaWrapperX<CrmTradeSampleDO>()
                .eqIfPresent(CrmTradeSampleDO::getCustomerId, reqVO.getCustomerId())
                .eqIfPresent(CrmTradeSampleDO::getBusinessId, reqVO.getBusinessId())
                .eqIfPresent(CrmTradeSampleDO::getRfqId, reqVO.getRfqId())
                .eqIfPresent(CrmTradeSampleDO::getOwnerUserId, reqVO.getOwnerUserId())
                .eqIfPresent(CrmTradeSampleDO::getStatus, reqVO.getStatus())
                .eqIfPresent(CrmTradeSampleDO::getPaymentStatus, reqVO.getPaymentStatus())
                .eqIfPresent(CrmTradeSampleDO::getApprovalStatus, reqVO.getApprovalStatus())
                .likeIfPresent(CrmTradeSampleDO::getNo, reqVO.getNo())
                .orderByDesc(CrmTradeSampleDO::getId));
    }

}
