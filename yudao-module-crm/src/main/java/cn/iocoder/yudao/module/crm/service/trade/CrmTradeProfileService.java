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

    /**
     * 将一个 CRM 业务对象的外贸档案复制到另一个 CRM 业务对象。
     *
     * <p>用于线索转客户等生命周期转换，避免国家、买家类型、MOQ、贸易条款、整柜潜力等外贸信息丢失。</p>
     *
     * @return 目标档案编号；来源没有外贸档案时返回 {@code null}
     */
    Long copyTradeProfile(Integer sourceBizType, Long sourceBizId, Integer targetBizType, Long targetBizId);

}
