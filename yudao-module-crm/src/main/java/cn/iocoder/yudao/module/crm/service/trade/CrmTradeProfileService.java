package cn.iocoder.yudao.module.crm.service.trade;

import cn.iocoder.yudao.module.crm.controller.admin.trade.vo.CrmTradeProfileSaveReqVO;
import cn.iocoder.yudao.module.crm.dal.dataobject.trade.CrmTradeProfileDO;
import jakarta.validation.Valid;

/**
 * CRM 外贸档案 Service。
 *
 * @author FLEXFANG
 */
public interface CrmTradeProfileService {

    /**
     * 获取指定 CRM 业务对象的外贸档案。
     */
    CrmTradeProfileDO getTradeProfile(Integer bizType, Long bizId);

    /**
     * 保存外贸档案。不存在则创建，存在则更新。
     *
     * @return 档案编号
     */
    Long saveTradeProfile(@Valid CrmTradeProfileSaveReqVO saveReqVO);

}
