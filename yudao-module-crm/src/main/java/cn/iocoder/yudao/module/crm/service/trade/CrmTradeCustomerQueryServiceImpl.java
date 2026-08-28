package cn.iocoder.yudao.module.crm.service.trade;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.crm.controller.admin.customer.vo.customer.CrmCustomerPageReqVO;
import cn.iocoder.yudao.module.crm.controller.admin.trade.vo.CrmTradeCustomerDetailRespVO;
import cn.iocoder.yudao.module.crm.controller.admin.trade.vo.CrmTradeCustomerPageReqVO;
import cn.iocoder.yudao.module.crm.controller.admin.trade.vo.CrmTradeCustomerRespVO;
import cn.iocoder.yudao.module.crm.dal.dataobject.customer.CrmCustomerDO;
import cn.iocoder.yudao.module.crm.dal.dataobject.followup.CrmFollowUpRecordDO;
import cn.iocoder.yudao.module.crm.dal.dataobject.trade.CrmTradeProfileDO;
import cn.iocoder.yudao.module.crm.dal.mysql.trade.CrmTradeProfileMapper;
import cn.iocoder.yudao.module.crm.enums.common.CrmBizTypeEnum;
import cn.iocoder.yudao.module.crm.service.customer.CrmCustomerService;
import cn.iocoder.yudao.module.crm.service.followup.CrmFollowUpRecordService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.pojo.PageParam.PAGE_SIZE_NONE;

/**
 * 外贸客户查询 Service 实现。
 *
 * <p>v0.1 先从原生 CRM 权限过滤后的客户集合中做外贸字段筛选，保证不绕过原生数据权限。
 * 后续数据量明显增大时，再下沉为数据库 Join 分页。</p>
 *
 * @author FLEXFANG
 */
@Service
@Validated
public class CrmTradeCustomerQueryServiceImpl implements CrmTradeCustomerQueryService {

    private static final int RECENT_FOLLOW_UP_LIMIT = 20;

    @Resource
    private CrmCustomerService customerService;
    @Resource
    private CrmTradeProfileMapper tradeProfileMapper;
    @Resource
    private CrmFollowUpRecordService followUpRecordService;

    @Override
    public PageResult<CrmTradeCustomerRespVO> getTradeCustomerPage(CrmTradeCustomerPageReqVO reqVO, Long userId) {
        List<CrmCustomerDO> customers = getPermittedCustomers(reqVO, userId);
        if (customers.isEmpty()) {
            return PageResult.empty();
        }
        Map<Long, CrmTradeProfileDO> profileMap = getProfileMap(customers);

        List<CrmTradeCustomerRespVO> filtered = customers.stream()
                .map(customer -> buildCustomer(customer, profileMap.get(customer.getId())))
                .filter(customer -> matchesTradeFilters(customer, reqVO))
                .sorted(customerComparator())
                .toList();

        long total = filtered.size();
        int fromIndex = Math.min((reqVO.getPageNo() - 1) * reqVO.getPageSize(), filtered.size());
        int toIndex = Math.min(fromIndex + reqVO.getPageSize(), filtered.size());
        return new PageResult<>(new ArrayList<>(filtered.subList(fromIndex, toIndex)), total);
    }

    @Override
    public CrmTradeCustomerDetailRespVO getTradeCustomerDetail(Long customerId) {
        // 调用原生 Customer Service：由其 @CrmPermission(READ) 统一执行客户数据权限校验。
        CrmCustomerDO customer = customerService.getCustomer(customerId);
        if (customer == null) {
            return null;
        }
        CrmTradeProfileDO profile = tradeProfileMapper.selectByBiz(CrmBizTypeEnum.CRM_CUSTOMER.getType(), customerId);
        List<CrmFollowUpRecordDO> followUps = followUpRecordService.getFollowUpRecordByBiz(
                CrmBizTypeEnum.CRM_CUSTOMER.getType(), Collections.singleton(customerId));

        CrmTradeCustomerDetailRespVO detail = new CrmTradeCustomerDetailRespVO();
        detail.setCustomer(buildCustomer(customer, profile));
        detail.setRecentFollowUps(followUps.stream()
                .sorted(Comparator.comparing(CrmFollowUpRecordDO::getCreateTime,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(RECENT_FOLLOW_UP_LIMIT)
                .map(this::buildFollowUp)
                .toList());
        return detail;
    }

    private List<CrmCustomerDO> getPermittedCustomers(CrmTradeCustomerPageReqVO reqVO, Long userId) {
        CrmCustomerPageReqVO nativeReq = new CrmCustomerPageReqVO();
        nativeReq.setPageSize(PAGE_SIZE_NONE);
        nativeReq.setName(reqVO.getName());
        nativeReq.setMobile(reqVO.getMobile());
        nativeReq.setSceneType(reqVO.getSceneType());
        nativeReq.setContactStatus(reqVO.getContactStatus());
        return customerService.getCustomerPage(nativeReq, userId).getList();
    }

    private Map<Long, CrmTradeProfileDO> getProfileMap(List<CrmCustomerDO> customers) {
        List<Long> customerIds = customers.stream().map(CrmCustomerDO::getId).toList();
        return tradeProfileMapper.selectListByBiz(CrmBizTypeEnum.CRM_CUSTOMER.getType(), customerIds).stream()
                .collect(Collectors.toMap(CrmTradeProfileDO::getBizId, Function.identity(), (first, ignored) -> first));
    }

    private boolean matchesTradeFilters(CrmTradeCustomerRespVO customer, CrmTradeCustomerPageReqVO reqVO) {
        if (!matchesText(customer.getCountryCode(), reqVO.getCountryCode())) return false;
        if (!matchesText(customer.getCompanyType(), reqVO.getCompanyType())) return false;
        if (!matchesText(customer.getSourceChannel(), reqVO.getSourceChannel())) return false;
        if (reqVO.getImportExperience() != null && !Objects.equals(customer.getImportExperience(), reqVO.getImportExperience())) return false;
        if (reqVO.getMinLeadScore() != null && valueOrMinusOne(customer.getLeadScore()) < reqVO.getMinLeadScore()) return false;
        if (reqVO.getMaxRiskScore() != null && valueOrMax(customer.getRiskScore()) > reqVO.getMaxRiskScore()) return false;
        if (reqVO.getMinFclProbability() != null && valueOrMinusOne(customer.getFclProbability()) < reqVO.getMinFclProbability()) return false;
        if (!matchesText(customer.getContainerPotential(), reqVO.getContainerPotential())) return false;
        if (Boolean.TRUE.equals(reqVO.getHasNextAction()) && StrUtil.isBlank(customer.getNextAction())) return false;
        if (Boolean.FALSE.equals(reqVO.getHasNextAction()) && StrUtil.isNotBlank(customer.getNextAction())) return false;
        return true;
    }

    private Comparator<CrmTradeCustomerRespVO> customerComparator() {
        return Comparator
                .comparing((CrmTradeCustomerRespVO customer) -> isOverdue(customer), Comparator.reverseOrder())
                .thenComparing(customer -> isDueToday(customer), Comparator.reverseOrder())
                .thenComparing(customer -> valueOrMinusOne(customer.getLeadScore()), Comparator.reverseOrder())
                .thenComparing(CrmTradeCustomerRespVO::getContactNextTime,
                        Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(CrmTradeCustomerRespVO::getCustomerId, Comparator.reverseOrder());
    }

    private boolean isOverdue(CrmTradeCustomerRespVO customer) {
        return customer.getContactNextTime() != null
                && customer.getContactNextTime().isBefore(LocalDateTime.now().toLocalDate().atStartOfDay());
    }

    private boolean isDueToday(CrmTradeCustomerRespVO customer) {
        if (customer.getContactNextTime() == null) return false;
        LocalDateTime start = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        return !customer.getContactNextTime().isBefore(start) && customer.getContactNextTime().isBefore(end);
    }

    private CrmTradeCustomerRespVO buildCustomer(CrmCustomerDO customer, CrmTradeProfileDO profile) {
        CrmTradeCustomerRespVO resp = new CrmTradeCustomerRespVO();
        resp.setCustomerId(customer.getId());
        resp.setCustomerName(customer.getName());
        resp.setMobile(customer.getMobile());
        resp.setTelephone(customer.getTelephone());
        resp.setEmail(customer.getEmail());
        resp.setOwnerUserId(customer.getOwnerUserId());
        resp.setDealStatus(customer.getDealStatus());
        resp.setContactLastTime(customer.getContactLastTime());
        resp.setContactLastContent(customer.getContactLastContent());
        resp.setContactNextTime(customer.getContactNextTime());
        if (profile == null) {
            return resp;
        }
        resp.setCountryCode(profile.getCountryCode());
        resp.setRegion(profile.getRegion());
        resp.setCity(profile.getCity());
        resp.setCompanyType(profile.getCompanyType());
        resp.setSourceChannel(profile.getSourceChannel());
        resp.setWebsite(profile.getWebsite());
        resp.setWhatsapp(profile.getWhatsapp());
        resp.setLinkedin(profile.getLinkedin());
        resp.setImportExperience(profile.getImportExperience());
        resp.setAnnualPurchaseVolume(profile.getAnnualPurchaseVolume());
        resp.setTargetProducts(profile.getTargetProducts());
        resp.setExpectedMoq(profile.getExpectedMoq());
        resp.setTargetPrice(profile.getTargetPrice());
        resp.setCurrency(profile.getCurrency());
        resp.setCertificationRequirement(profile.getCertificationRequirement());
        resp.setIncoterm(profile.getIncoterm());
        resp.setDestinationPort(profile.getDestinationPort());
        resp.setContainerPotential(profile.getContainerPotential());
        resp.setFclProbability(profile.getFclProbability());
        resp.setLeadScore(profile.getLeadScore());
        resp.setRiskScore(profile.getRiskScore());
        resp.setNextAction(profile.getNextAction());
        resp.setLostReason(profile.getLostReason());
        return resp;
    }

    private CrmTradeCustomerDetailRespVO.FollowUp buildFollowUp(CrmFollowUpRecordDO record) {
        CrmTradeCustomerDetailRespVO.FollowUp item = new CrmTradeCustomerDetailRespVO.FollowUp();
        item.setId(record.getId());
        item.setType(record.getType());
        item.setContent(record.getContent());
        item.setNextTime(record.getNextTime());
        item.setCreateTime(record.getCreateTime());
        return item;
    }

    private boolean matchesText(String actual, String expected) {
        if (StrUtil.isBlank(expected)) return true;
        return actual != null && actual.equalsIgnoreCase(expected.trim());
    }

    private int valueOrMinusOne(Integer value) {
        return value == null ? -1 : value;
    }

    private int valueOrMax(Integer value) {
        return value == null ? Integer.MAX_VALUE : value;
    }

}
