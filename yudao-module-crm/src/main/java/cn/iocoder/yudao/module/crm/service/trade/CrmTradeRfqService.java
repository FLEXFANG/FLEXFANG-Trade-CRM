package cn.iocoder.yudao.module.crm.service.trade;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.crm.controller.admin.trade.vo.CrmTradeRfqPageReqVO;
import cn.iocoder.yudao.module.crm.controller.admin.trade.vo.CrmTradeRfqSaveReqVO;
import cn.iocoder.yudao.module.crm.dal.dataobject.trade.CrmTradeRfqDO;
import cn.iocoder.yudao.module.crm.dal.dataobject.trade.CrmTradeRfqItemDO;
import jakarta.validation.Valid;

import java.util.List;

public interface CrmTradeRfqService {

    Long createRfq(@Valid CrmTradeRfqSaveReqVO createReqVO);

    void updateRfq(@Valid CrmTradeRfqSaveReqVO updateReqVO);

    void deleteRfq(Long id);

    CrmTradeRfqDO getRfq(Long id);

    CrmTradeRfqDO validateRfq(Long id);

    PageResult<CrmTradeRfqDO> getRfqPage(CrmTradeRfqPageReqVO pageReqVO);

    List<CrmTradeRfqItemDO> getRfqItems(Long rfqId);

}
