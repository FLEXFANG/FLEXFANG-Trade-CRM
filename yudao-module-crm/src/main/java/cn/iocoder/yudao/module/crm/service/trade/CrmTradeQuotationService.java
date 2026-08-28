package cn.iocoder.yudao.module.crm.service.trade;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.crm.controller.admin.trade.vo.CrmTradeQuotationPageReqVO;
import cn.iocoder.yudao.module.crm.controller.admin.trade.vo.CrmTradeQuotationSaveReqVO;
import cn.iocoder.yudao.module.crm.dal.dataobject.trade.CrmTradeQuotationDO;
import cn.iocoder.yudao.module.crm.dal.dataobject.trade.CrmTradeQuotationItemDO;
import jakarta.validation.Valid;

import java.util.List;

public interface CrmTradeQuotationService {

    Long createQuotation(@Valid CrmTradeQuotationSaveReqVO createReqVO);

    void updateQuotation(@Valid CrmTradeQuotationSaveReqVO updateReqVO);

    void deleteQuotation(Long id);

    CrmTradeQuotationDO getQuotation(Long id);

    CrmTradeQuotationDO validateQuotation(Long id);

    PageResult<CrmTradeQuotationDO> getQuotationPage(CrmTradeQuotationPageReqVO pageReqVO);

    List<CrmTradeQuotationItemDO> getQuotationItems(Long quotationId);

    void sendQuotation(Long id);

    void acceptQuotation(Long id);

    void rejectQuotation(Long id, String reason);

    Long reviseQuotation(Long id);

    void cancelQuotation(Long id);

}
