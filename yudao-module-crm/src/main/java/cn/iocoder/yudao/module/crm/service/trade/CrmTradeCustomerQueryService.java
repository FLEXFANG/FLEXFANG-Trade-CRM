package cn.iocoder.yudao.module.crm.service.trade;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.crm.controller.admin.trade.vo.CrmTradeCustomerDetailRespVO;
import cn.iocoder.yudao.module.crm.controller.admin.trade.vo.CrmTradeCustomerPageReqVO;
import cn.iocoder.yudao.module.crm.controller.admin.trade.vo.CrmTradeCustomerRespVO;

/**
 * 外贸客户查询 Service。
 *
 * @author FLEXFANG
 */
public interface CrmTradeCustomerQueryService {

    PageResult<CrmTradeCustomerRespVO> getTradeCustomerPage(CrmTradeCustomerPageReqVO reqVO, Long userId);

    CrmTradeCustomerDetailRespVO getTradeCustomerDetail(Long customerId);

}
