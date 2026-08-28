package cn.iocoder.yudao.module.crm.service.trade;

import cn.iocoder.yudao.module.crm.controller.admin.trade.vo.CrmTradeWorkbenchRespVO;

/**
 * 外贸 CRM 工作台 Service。
 *
 * @author FLEXFANG
 */
public interface CrmTradeWorkbenchService {

    CrmTradeWorkbenchRespVO getWorkbench(Long userId);

}
