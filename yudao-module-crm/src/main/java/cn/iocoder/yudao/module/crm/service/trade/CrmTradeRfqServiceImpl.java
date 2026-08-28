package cn.iocoder.yudao.module.crm.service.trade;

import cn.hutool.core.lang.Assert;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.crm.controller.admin.trade.vo.CrmTradeRfqItemReqVO;
import cn.iocoder.yudao.module.crm.controller.admin.trade.vo.CrmTradeRfqPageReqVO;
import cn.iocoder.yudao.module.crm.controller.admin.trade.vo.CrmTradeRfqSaveReqVO;
import cn.iocoder.yudao.module.crm.dal.dataobject.business.CrmBusinessDO;
import cn.iocoder.yudao.module.crm.dal.dataobject.trade.CrmTradeRfqDO;
import cn.iocoder.yudao.module.crm.dal.dataobject.trade.CrmTradeRfqItemDO;
import cn.iocoder.yudao.module.crm.dal.mysql.trade.CrmTradeRfqItemMapper;
import cn.iocoder.yudao.module.crm.dal.mysql.trade.CrmTradeRfqMapper;
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
public class CrmTradeRfqServiceImpl implements CrmTradeRfqService {

    @Resource private CrmTradeRfqMapper rfqMapper;
    @Resource private CrmTradeRfqItemMapper rfqItemMapper;
    @Resource private CrmTradeSampleMapper sampleMapper;
    @Resource private CrmCustomerService customerService;
    @Resource private CrmBusinessService businessService;
    @Resource private CrmProductService productService;
    @Resource private AdminUserApi adminUserApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRfq(CrmTradeRfqSaveReqVO createReqVO) {
        validateRelations(createReqVO);
        validateRfqNoDuplicate(null, createReqVO.getNo());
        CrmTradeRfqDO rfq = BeanUtils.toBean(createReqVO, CrmTradeRfqDO.class).setId(null);
        rfqMapper.insert(rfq);
        saveItems(rfq.getId(), createReqVO.getItems());
        return rfq.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRfq(CrmTradeRfqSaveReqVO updateReqVO) {
        Assert.notNull(updateReqVO.getId(), "询价编号不能为空");
        validateRfq(updateReqVO.getId());
        validateRelations(updateReqVO);
        validateRfqNoDuplicate(updateReqVO.getId(), updateReqVO.getNo());
        rfqMapper.updateById(BeanUtils.toBean(updateReqVO, CrmTradeRfqDO.class));
        rfqItemMapper.deleteByRfqId(updateReqVO.getId());
        saveItems(updateReqVO.getId(), updateReqVO.getItems());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRfq(Long id) {
        validateRfq(id);
        if (sampleMapper.selectCountByRfqId(id) > 0) {
            throw exception(TRADE_RFQ_DELETE_FAIL_SAMPLE_EXISTS);
        }
        rfqItemMapper.deleteByRfqId(id);
        rfqMapper.deleteById(id);
    }

    @Override public CrmTradeRfqDO getRfq(Long id) { return rfqMapper.selectById(id); }

    @Override
    public CrmTradeRfqDO validateRfq(Long id) {
        CrmTradeRfqDO rfq = rfqMapper.selectById(id);
        if (rfq == null) throw exception(TRADE_RFQ_NOT_EXISTS);
        return rfq;
    }

    @Override public PageResult<CrmTradeRfqDO> getRfqPage(CrmTradeRfqPageReqVO pageReqVO) { return rfqMapper.selectPage(pageReqVO); }

    @Override public List<CrmTradeRfqItemDO> getRfqItems(Long rfqId) { return rfqItemMapper.selectListByRfqId(rfqId); }

    private void validateRelations(CrmTradeRfqSaveReqVO reqVO) {
        customerService.validateCustomer(reqVO.getCustomerId());
        if (reqVO.getBusinessId() != null) {
            CrmBusinessDO business = businessService.validateBusiness(reqVO.getBusinessId());
            if (!Objects.equals(business.getCustomerId(), reqVO.getCustomerId())) {
                throw exception(TRADE_RFQ_BUSINESS_CUSTOMER_MISMATCH);
            }
        }
        adminUserApi.validateUser(reqVO.getOwnerUserId());
        List<Long> productIds = reqVO.getItems().stream().map(CrmTradeRfqItemReqVO::getProductId)
                .filter(Objects::nonNull).distinct().toList();
        if (!productIds.isEmpty()) productService.validProductList(productIds);
    }

    private void validateRfqNoDuplicate(Long id, String no) {
        CrmTradeRfqDO existing = rfqMapper.selectByNo(no);
        if (existing != null && !Objects.equals(existing.getId(), id)) throw exception(TRADE_RFQ_NO_EXISTS);
    }

    private void saveItems(Long rfqId, List<CrmTradeRfqItemReqVO> items) {
        rfqItemMapper.insertBatch(items.stream()
                .map(item -> BeanUtils.toBean(item, CrmTradeRfqItemDO.class).setRfqId(rfqId)).toList());
    }

}
