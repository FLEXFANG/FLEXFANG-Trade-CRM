package cn.iocoder.yudao.module.crm.service.trade;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.crm.controller.admin.trade.vo.CrmTradeSamplePageReqVO;
import cn.iocoder.yudao.module.crm.controller.admin.trade.vo.CrmTradeSampleSaveReqVO;
import cn.iocoder.yudao.module.crm.dal.dataobject.trade.CrmTradeSampleDO;
import cn.iocoder.yudao.module.crm.dal.dataobject.trade.CrmTradeSampleItemDO;
import jakarta.validation.Valid;

import java.util.List;

public interface CrmTradeSampleService {

    Long createSample(@Valid CrmTradeSampleSaveReqVO createReqVO);

    void updateSample(@Valid CrmTradeSampleSaveReqVO updateReqVO);

    void deleteSample(Long id);

    CrmTradeSampleDO getSample(Long id);

    CrmTradeSampleDO validateSample(Long id);

    PageResult<CrmTradeSampleDO> getSamplePage(CrmTradeSamplePageReqVO pageReqVO);

    List<CrmTradeSampleItemDO> getSampleItems(Long sampleId);

}
