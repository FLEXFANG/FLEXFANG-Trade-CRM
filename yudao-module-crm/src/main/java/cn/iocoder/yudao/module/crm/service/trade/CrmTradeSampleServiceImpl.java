package cn.iocoder.yudao.module.crm.service.trade;

import cn.hutool.core.lang.Assert;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.crm.controller.admin.trade.vo.CrmTradeSampleItemReqVO;
import cn.iocoder.yudao.module.crm.controller.admin.trade.vo.CrmTradeSamplePageReqVO;
import cn.iocoder.yudao.module.crm.controller.admin.trade.vo.CrmTradeSampleSaveReqVO;
import cn.iocoder.yudao.module.crm.dal.dataobject.business.CrmBusinessDO;
import cn.iocoder.yudao.module.crm.dal.dataobject.trade.CrmTradeRfqDO;
import cn.iocoder.yudao.module.crm.dal.dataobject.trade.CrmTradeSampleDO;
import cn.iocoder.yudao.module.crm.dal.dataobject.trade.CrmTradeSampleItemDO;
import cn.iocoder.yudao.module.crm.dal.mysql.trade.CrmTradeSampleItemMapper;
import cn.iocoder.yudao.module.crm.dal.mysql.trade.CrmTradeSampleMapper;
import cn.iocoder.yudao.module.crm.service.business.CrmBusinessService;
import cn.iocoder.yudao.module.crm.service.customer.CrmCustomerService;
import cn.iocoder.yudao.module.crm.service.product.CrmProductService;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.crm.enums.trade.CrmTradeErrorCodeConstants.*;

@Service
@Validated
public class CrmTradeSampleServiceImpl implements CrmTradeSampleService {

    @Resource private CrmTradeSampleMapper sampleMapper;
    @Resource private CrmTradeSampleItemMapper sampleItemMapper;
    @Resource private CrmCustomerService customerService;
    @Resource private CrmBusinessService businessService;
    @Resource private CrmTradeRfqService rfqService;
    @Resource private CrmProductService productService;
    @Resource private AdminUserApi adminUserApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createSample(CrmTradeSampleSaveReqVO createReqVO) {
        validateRelations(createReqVO);
        validateSampleNoDuplicate(null, createReqVO.getNo());
        CrmTradeSampleDO sample = BeanUtils.toBean(createReqVO, CrmTradeSampleDO.class).setId(null);
        sampleMapper.insert(sample);
        saveItems(sample.getId(), createReqVO.getItems());
        return sample.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSample(CrmTradeSampleSaveReqVO updateReqVO) {
        Assert.notNull(updateReqVO.getId(), "样品编号不能为空");
        validateSample(updateReqVO.getId());
        validateRelations(updateReqVO);
        validateSampleNoDuplicate(updateReqVO.getId(), updateReqVO.getNo());
        sampleMapper.updateById(BeanUtils.toBean(updateReqVO, CrmTradeSampleDO.class));
        sampleItemMapper.deleteBySampleId(updateReqVO.getId());
        saveItems(updateReqVO.getId(), updateReqVO.getItems());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSample(Long id) {
        validateSample(id);
        sampleItemMapper.deleteBySampleId(id);
        sampleMapper.deleteById(id);
    }

    @Override
    public CrmTradeSampleDO getSample(Long id) {
        return sampleMapper.selectById(id);
    }

    @Override
    public CrmTradeSampleDO validateSample(Long id) {
        CrmTradeSampleDO sample = sampleMapper.selectById(id);
        if (sample == null) {
            throw exception(TRADE_SAMPLE_NOT_EXISTS);
        }
        return sample;
    }

    @Override
    public PageResult<CrmTradeSampleDO> getSamplePage(CrmTradeSamplePageReqVO pageReqVO) {
        return sampleMapper.selectPage(pageReqVO);
    }

    @Override
    public List<CrmTradeSampleItemDO> getSampleItems(Long sampleId) {
        return sampleItemMapper.selectListBySampleId(sampleId);
    }

    private void validateRelations(CrmTradeSampleSaveReqVO reqVO) {
        customerService.validateCustomer(reqVO.getCustomerId());
        if (reqVO.getBusinessId() != null) {
            CrmBusinessDO business = businessService.validateBusiness(reqVO.getBusinessId());
            if (!Objects.equals(business.getCustomerId(), reqVO.getCustomerId())) {
                throw exception(TRADE_SAMPLE_BUSINESS_CUSTOMER_MISMATCH);
            }
        }
        if (reqVO.getRfqId() != null) {
            CrmTradeRfqDO rfq = rfqService.validateRfq(reqVO.getRfqId());
            if (!Objects.equals(rfq.getCustomerId(), reqVO.getCustomerId())) {
                throw exception(TRADE_SAMPLE_RFQ_CUSTOMER_MISMATCH);
            }
        }
        adminUserApi.validateUser(reqVO.getOwnerUserId());
        List<Long> productIds = reqVO.getItems().stream()
                .map(CrmTradeSampleItemReqVO::getProductId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (!productIds.isEmpty()) {
            productService.validProductList(productIds);
        }
    }

    private void validateSampleNoDuplicate(Long id, String no) {
        CrmTradeSampleDO existing = sampleMapper.selectByNo(no);
        if (existing != null && !Objects.equals(existing.getId(), id)) {
            throw exception(TRADE_SAMPLE_NO_EXISTS);
        }
    }

    private void saveItems(Long sampleId, List<CrmTradeSampleItemReqVO> items) {
        List<CrmTradeSampleItemDO> itemDOs = items.stream()
                .map(item -> BeanUtils.toBean(item, CrmTradeSampleItemDO.class).setSampleId(sampleId))
                .toList();
        sampleItemMapper.insertBatch(itemDOs);
    }

}
