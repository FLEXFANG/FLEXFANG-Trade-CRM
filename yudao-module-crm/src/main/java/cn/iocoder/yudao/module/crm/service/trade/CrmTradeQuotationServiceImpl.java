package cn.iocoder.yudao.module.crm.service.trade;

import cn.hutool.core.lang.Assert;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.crm.controller.admin.trade.vo.CrmTradeQuotationItemReqVO;
import cn.iocoder.yudao.module.crm.controller.admin.trade.vo.CrmTradeQuotationPageReqVO;
import cn.iocoder.yudao.module.crm.controller.admin.trade.vo.CrmTradeQuotationSaveReqVO;
import cn.iocoder.yudao.module.crm.dal.dataobject.business.CrmBusinessDO;
import cn.iocoder.yudao.module.crm.dal.dataobject.trade.CrmTradeQuotationDO;
import cn.iocoder.yudao.module.crm.dal.dataobject.trade.CrmTradeQuotationItemDO;
import cn.iocoder.yudao.module.crm.dal.dataobject.trade.CrmTradeRfqDO;
import cn.iocoder.yudao.module.crm.dal.mysql.trade.CrmTradeQuotationItemMapper;
import cn.iocoder.yudao.module.crm.dal.mysql.trade.CrmTradeQuotationMapper;
import cn.iocoder.yudao.module.crm.service.business.CrmBusinessService;
import cn.iocoder.yudao.module.crm.service.customer.CrmCustomerService;
import cn.iocoder.yudao.module.crm.service.product.CrmProductService;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.crm.enums.trade.CrmTradeErrorCodeConstants.*;

@Service
@Validated
public class CrmTradeQuotationServiceImpl implements CrmTradeQuotationService {

    private static final String DRAFT = "DRAFT";
    private static final String SENT = "SENT";
    private static final String ACCEPTED = "ACCEPTED";
    private static final String REJECTED = "REJECTED";
    private static final String SUPERSEDED = "SUPERSEDED";
    private static final String CANCELLED = "CANCELLED";

    @Resource private CrmTradeQuotationMapper quotationMapper;
    @Resource private CrmTradeQuotationItemMapper quotationItemMapper;
    @Resource private CrmCustomerService customerService;
    @Resource private CrmBusinessService businessService;
    @Resource private CrmTradeRfqService rfqService;
    @Resource private CrmProductService productService;
    @Resource private AdminUserApi adminUserApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createQuotation(CrmTradeQuotationSaveReqVO createReqVO) {
        validateRelations(createReqVO);
        if (quotationMapper.selectMaxRevision(createReqVO.getNo()) > 0) {
            throw exception(TRADE_QUOTATION_NO_EXISTS);
        }
        CrmTradeQuotationDO quotation = buildQuotation(createReqVO);
        quotation.setId(null);
        quotation.setRevision(1);
        quotation.setStatus(DRAFT);
        quotationMapper.insert(quotation);
        saveItems(quotation.getId(), createReqVO.getItems());
        return quotation.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateQuotation(CrmTradeQuotationSaveReqVO updateReqVO) {
        Assert.notNull(updateReqVO.getId(), "报价编号不能为空");
        CrmTradeQuotationDO existing = validateQuotation(updateReqVO.getId());
        ensureStatus(existing, Set.of(DRAFT));
        if (!Objects.equals(existing.getNo(), updateReqVO.getNo())) {
            throw exception(TRADE_QUOTATION_INVALID_TRANSITION);
        }
        validateRelations(updateReqVO);
        CrmTradeQuotationDO quotation = buildQuotation(updateReqVO);
        quotation.setId(existing.getId());
        quotation.setRevision(existing.getRevision());
        quotation.setStatus(DRAFT);
        quotationMapper.updateById(quotation);
        quotationItemMapper.deleteByQuotationId(existing.getId());
        saveItems(existing.getId(), updateReqVO.getItems());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteQuotation(Long id) {
        CrmTradeQuotationDO quotation = validateQuotation(id);
        ensureStatus(quotation, Set.of(DRAFT));
        quotationItemMapper.deleteByQuotationId(id);
        quotationMapper.deleteById(id);
    }

    @Override public CrmTradeQuotationDO getQuotation(Long id) { return quotationMapper.selectById(id); }

    @Override
    public CrmTradeQuotationDO validateQuotation(Long id) {
        CrmTradeQuotationDO quotation = quotationMapper.selectById(id);
        if (quotation == null) throw exception(TRADE_QUOTATION_NOT_EXISTS);
        return quotation;
    }

    @Override public PageResult<CrmTradeQuotationDO> getQuotationPage(CrmTradeQuotationPageReqVO reqVO) { return quotationMapper.selectPage(reqVO); }

    @Override public List<CrmTradeQuotationItemDO> getQuotationItems(Long quotationId) { return quotationItemMapper.selectListByQuotationId(quotationId); }

    @Override
    public void sendQuotation(Long id) {
        CrmTradeQuotationDO quotation = validateQuotation(id);
        ensureStatus(quotation, Set.of(DRAFT));
        LocalDateTime now = LocalDateTime.now();
        CrmTradeQuotationDO update = new CrmTradeQuotationDO();
        update.setId(id);
        update.setStatus(SENT);
        update.setSentTime(now);
        update.setIssueTime(quotation.getIssueTime() == null ? now : quotation.getIssueTime());
        quotationMapper.updateById(update);
    }

    @Override
    public void acceptQuotation(Long id) {
        CrmTradeQuotationDO quotation = validateQuotation(id);
        ensureStatus(quotation, Set.of(SENT));
        CrmTradeQuotationDO update = new CrmTradeQuotationDO();
        update.setId(id);
        update.setStatus(ACCEPTED);
        update.setAcceptedTime(LocalDateTime.now());
        quotationMapper.updateById(update);
    }

    @Override
    public void rejectQuotation(Long id, String reason) {
        CrmTradeQuotationDO quotation = validateQuotation(id);
        ensureStatus(quotation, Set.of(SENT));
        CrmTradeQuotationDO update = new CrmTradeQuotationDO();
        update.setId(id);
        update.setStatus(REJECTED);
        update.setRejectionReason(reason);
        quotationMapper.updateById(update);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long reviseQuotation(Long id) {
        CrmTradeQuotationDO source = validateQuotation(id);
        ensureStatus(source, Set.of(SENT, REJECTED));
        List<CrmTradeQuotationItemDO> sourceItems = quotationItemMapper.selectListByQuotationId(id);

        CrmTradeQuotationDO supersede = new CrmTradeQuotationDO();
        supersede.setId(id);
        supersede.setStatus(SUPERSEDED);
        quotationMapper.updateById(supersede);

        CrmTradeQuotationDO revision = BeanUtils.toBean(source, CrmTradeQuotationDO.class);
        revision.setId(null);
        revision.clean();
        revision.setDeleted(null);
        revision.setRevision(quotationMapper.selectMaxRevision(source.getNo()) + 1);
        revision.setPreviousQuotationId(source.getId());
        revision.setStatus(DRAFT);
        revision.setSentTime(null);
        revision.setAcceptedTime(null);
        revision.setRejectionReason(null);
        revision.setIssueTime(null);
        quotationMapper.insert(revision);

        List<CrmTradeQuotationItemDO> clonedItems = sourceItems.stream().map(sourceItem -> {
            CrmTradeQuotationItemDO item = BeanUtils.toBean(sourceItem, CrmTradeQuotationItemDO.class);
            item.setId(null);
            item.clean();
            item.setDeleted(null);
            item.setQuotationId(revision.getId());
            return item;
        }).toList();
        if (!clonedItems.isEmpty()) quotationItemMapper.insertBatch(clonedItems);
        return revision.getId();
    }

    @Override
    public void cancelQuotation(Long id) {
        CrmTradeQuotationDO quotation = validateQuotation(id);
        ensureStatus(quotation, Set.of(DRAFT, SENT));
        CrmTradeQuotationDO update = new CrmTradeQuotationDO();
        update.setId(id);
        update.setStatus(CANCELLED);
        quotationMapper.updateById(update);
    }

    private void validateRelations(CrmTradeQuotationSaveReqVO reqVO) {
        customerService.validateCustomer(reqVO.getCustomerId());
        if (reqVO.getBusinessId() != null) {
            CrmBusinessDO business = businessService.validateBusiness(reqVO.getBusinessId());
            if (!Objects.equals(business.getCustomerId(), reqVO.getCustomerId())) {
                throw exception(TRADE_QUOTATION_BUSINESS_CUSTOMER_MISMATCH);
            }
        }
        if (reqVO.getRfqId() != null) {
            CrmTradeRfqDO rfq = rfqService.validateRfq(reqVO.getRfqId());
            if (!Objects.equals(rfq.getCustomerId(), reqVO.getCustomerId())) {
                throw exception(TRADE_QUOTATION_RFQ_CUSTOMER_MISMATCH);
            }
        }
        adminUserApi.validateUser(reqVO.getOwnerUserId());
        List<Long> productIds = reqVO.getItems().stream().map(CrmTradeQuotationItemReqVO::getProductId)
                .filter(Objects::nonNull).distinct().toList();
        if (!productIds.isEmpty()) productService.validProductList(productIds);
    }

    private CrmTradeQuotationDO buildQuotation(CrmTradeQuotationSaveReqVO reqVO) {
        BigDecimal subtotal = reqVO.getItems().stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal discount = zero(reqVO.getDiscountAmount());
        if (discount.compareTo(subtotal) > 0) throw exception(TRADE_QUOTATION_DISCOUNT_TOO_LARGE);
        BigDecimal total = subtotal.subtract(discount)
                .add(zero(reqVO.getFreight()))
                .add(zero(reqVO.getInsurance()))
                .add(zero(reqVO.getOtherCharge()));
        CrmTradeQuotationDO quotation = BeanUtils.toBean(reqVO, CrmTradeQuotationDO.class);
        quotation.setSubtotal(subtotal);
        quotation.setDiscountAmount(discount);
        quotation.setFreight(zero(reqVO.getFreight()));
        quotation.setInsurance(zero(reqVO.getInsurance()));
        quotation.setOtherCharge(zero(reqVO.getOtherCharge()));
        quotation.setTotalAmount(total);
        return quotation;
    }

    private void saveItems(Long quotationId, List<CrmTradeQuotationItemReqVO> items) {
        List<CrmTradeQuotationItemDO> itemDOs = items.stream().map(req -> {
            CrmTradeQuotationItemDO item = BeanUtils.toBean(req, CrmTradeQuotationItemDO.class);
            item.setQuotationId(quotationId);
            item.setLineAmount(req.getUnitPrice().multiply(BigDecimal.valueOf(req.getQuantity())));
            return item;
        }).toList();
        quotationItemMapper.insertBatch(itemDOs);
    }

    private static BigDecimal zero(BigDecimal value) { return value == null ? BigDecimal.ZERO : value; }

    private static void ensureStatus(CrmTradeQuotationDO quotation, Set<String> allowed) {
        if (!allowed.contains(quotation.getStatus())) {
            if (allowed.size() == 1 && allowed.contains(DRAFT)) throw exception(TRADE_QUOTATION_NOT_EDITABLE);
            throw exception(TRADE_QUOTATION_INVALID_TRANSITION);
        }
    }

}
