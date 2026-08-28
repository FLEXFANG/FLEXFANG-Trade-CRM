package cn.iocoder.yudao.module.crm.service.trade;

import cn.iocoder.yudao.module.crm.controller.admin.customer.vo.customer.CrmCustomerPageReqVO;
import cn.iocoder.yudao.module.crm.controller.admin.trade.vo.CrmTradeWorkbenchRespVO;
import cn.iocoder.yudao.module.crm.dal.dataobject.customer.CrmCustomerDO;
import cn.iocoder.yudao.module.crm.dal.dataobject.trade.CrmTradeProfileDO;
import cn.iocoder.yudao.module.crm.dal.mysql.trade.CrmTradeProfileMapper;
import cn.iocoder.yudao.module.crm.enums.common.CrmBizTypeEnum;
import cn.iocoder.yudao.module.crm.enums.common.CrmSceneTypeEnum;
import cn.iocoder.yudao.module.crm.service.customer.CrmCustomerService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.pojo.PageParam.PAGE_SIZE_NONE;

/**
 * 外贸 CRM 工作台 Service 实现。
 *
 * <p>工作台只聚合现有 CRM 客户与 Trade Profile，不修改业务评分。</p>
 *
 * @author FLEXFANG
 */
@Service
@Validated
public class CrmTradeWorkbenchServiceImpl implements CrmTradeWorkbenchService {

    private static final int HIGH_PRIORITY_LEAD_SCORE = 70;
    private static final int HIGH_RISK_SCORE = 70;
    private static final int HIGH_FCL_PROBABILITY = 60;
    private static final int PRIORITY_LIST_LIMIT = 12;
    private static final Set<String> FCL_CONTAINER_TYPES = Set.of("20GP", "40GP", "40HQ");

    @Resource
    private CrmCustomerService customerService;
    @Resource
    private CrmTradeProfileMapper tradeProfileMapper;

    @Override
    public CrmTradeWorkbenchRespVO getWorkbench(Long userId) {
        List<CrmCustomerDO> customers = getOwnedCustomers(userId);
        Map<Long, CrmTradeProfileDO> profileMap = getCustomerProfileMap(customers);

        LocalDate today = LocalDate.now();
        LocalDateTime startOfToday = today.atStartOfDay();
        LocalDateTime startOfTomorrow = today.plusDays(1).atStartOfDay();

        CrmTradeWorkbenchRespVO respVO = new CrmTradeWorkbenchRespVO();
        respVO.setSummary(buildSummary(customers, profileMap, startOfToday, startOfTomorrow));
        respVO.setPriorityCustomers(buildPriorityCustomers(customers, profileMap, startOfToday, startOfTomorrow));
        respVO.setCountryDistribution(buildDistribution(profileMap.values(), CrmTradeProfileDO::getCountryCode));
        respVO.setSourceDistribution(buildDistribution(profileMap.values(), CrmTradeProfileDO::getSourceChannel));
        return respVO;
    }

    private List<CrmCustomerDO> getOwnedCustomers(Long userId) {
        CrmCustomerPageReqVO reqVO = new CrmCustomerPageReqVO();
        reqVO.setPageSize(PAGE_SIZE_NONE);
        reqVO.setSceneType(CrmSceneTypeEnum.OWNER.getType());
        return customerService.getCustomerPage(reqVO, userId).getList();
    }

    private Map<Long, CrmTradeProfileDO> getCustomerProfileMap(List<CrmCustomerDO> customers) {
        if (customers.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Long> customerIds = customers.stream().map(CrmCustomerDO::getId).toList();
        return tradeProfileMapper.selectListByBiz(CrmBizTypeEnum.CRM_CUSTOMER.getType(), customerIds).stream()
                .collect(Collectors.toMap(CrmTradeProfileDO::getBizId, Function.identity(), (first, ignored) -> first));
    }

    private CrmTradeWorkbenchRespVO.Summary buildSummary(List<CrmCustomerDO> customers,
                                                          Map<Long, CrmTradeProfileDO> profileMap,
                                                          LocalDateTime startOfToday,
                                                          LocalDateTime startOfTomorrow) {
        CrmTradeWorkbenchRespVO.Summary summary = new CrmTradeWorkbenchRespVO.Summary();
        summary.setTotalCustomers((long) customers.size());
        summary.setDueToday(customers.stream().filter(customer -> isDueToday(customer, startOfToday, startOfTomorrow)).count());
        summary.setOverdue(customers.stream().filter(customer -> isOverdue(customer, startOfToday)).count());
        summary.setHighPriority(profileMap.values().stream().filter(this::isHighPriority).count());
        summary.setHighFclPotential(profileMap.values().stream().filter(this::isHighFclPotential).count());
        summary.setHighRisk(profileMap.values().stream().filter(this::isHighRisk).count());
        return summary;
    }

    private List<CrmTradeWorkbenchRespVO.PriorityCustomer> buildPriorityCustomers(
            List<CrmCustomerDO> customers,
            Map<Long, CrmTradeProfileDO> profileMap,
            LocalDateTime startOfToday,
            LocalDateTime startOfTomorrow) {
        return customers.stream()
                .filter(customer -> profileMap.containsKey(customer.getId()))
                .map(customer -> buildPriorityCustomer(customer, profileMap.get(customer.getId()),
                        startOfToday, startOfTomorrow))
                .sorted(Comparator.comparing(CrmTradeWorkbenchRespVO.PriorityCustomer::getPriorityScore).reversed()
                        .thenComparing(CrmTradeWorkbenchRespVO.PriorityCustomer::getCustomerId))
                .limit(PRIORITY_LIST_LIMIT)
                .toList();
    }

    private CrmTradeWorkbenchRespVO.PriorityCustomer buildPriorityCustomer(
            CrmCustomerDO customer,
            CrmTradeProfileDO profile,
            LocalDateTime startOfToday,
            LocalDateTime startOfTomorrow) {
        CrmTradeWorkbenchRespVO.PriorityCustomer item = new CrmTradeWorkbenchRespVO.PriorityCustomer();
        item.setCustomerId(customer.getId());
        item.setCustomerName(customer.getName());
        item.setCountryCode(profile.getCountryCode());
        item.setCompanyType(profile.getCompanyType());
        item.setSourceChannel(profile.getSourceChannel());
        item.setLeadScore(profile.getLeadScore());
        item.setRiskScore(profile.getRiskScore());
        item.setFclProbability(profile.getFclProbability());
        item.setContainerPotential(profile.getContainerPotential());
        item.setQualityGrade(calculateQualityGrade(profile));
        item.setPriorityScore(calculatePriorityScore(customer, profile, startOfToday, startOfTomorrow));
        item.setContactLastTime(customer.getContactLastTime());
        item.setContactNextTime(customer.getContactNextTime());
        item.setNextAction(profile.getNextAction());
        return item;
    }

    private int calculatePriorityScore(CrmCustomerDO customer, CrmTradeProfileDO profile,
                                       LocalDateTime startOfToday, LocalDateTime startOfTomorrow) {
        int lead = valueOrZero(profile.getLeadScore());
        int fcl = valueOrZero(profile.getFclProbability());
        int risk = valueOrZero(profile.getRiskScore());
        int timingBonus = 0;
        if (isOverdue(customer, startOfToday)) {
            timingBonus = 20;
        } else if (isDueToday(customer, startOfToday, startOfTomorrow)) {
            timingBonus = 10;
        }
        return lead + fcl / 2 - risk / 2 + timingBonus;
    }

    private String calculateQualityGrade(CrmTradeProfileDO profile) {
        if (profile.getLeadScore() == null && profile.getRiskScore() == null) {
            return "UNSCORED";
        }
        int lead = valueOrZero(profile.getLeadScore());
        int risk = valueOrZero(profile.getRiskScore());
        if (lead >= 80 && risk < 50) {
            return "A";
        }
        if (lead >= 65 && risk < 70) {
            return "B";
        }
        if (lead >= 45) {
            return "C";
        }
        return "D";
    }

    private boolean isHighPriority(CrmTradeProfileDO profile) {
        return valueOrZero(profile.getLeadScore()) >= HIGH_PRIORITY_LEAD_SCORE
                && valueOrZero(profile.getRiskScore()) < HIGH_RISK_SCORE;
    }

    private boolean isHighRisk(CrmTradeProfileDO profile) {
        return valueOrZero(profile.getRiskScore()) >= HIGH_RISK_SCORE;
    }

    private boolean isHighFclPotential(CrmTradeProfileDO profile) {
        return valueOrZero(profile.getFclProbability()) >= HIGH_FCL_PROBABILITY
                || FCL_CONTAINER_TYPES.contains(profile.getContainerPotential());
    }

    private boolean isDueToday(CrmCustomerDO customer, LocalDateTime startOfToday, LocalDateTime startOfTomorrow) {
        LocalDateTime nextTime = customer.getContactNextTime();
        return nextTime != null && !nextTime.isBefore(startOfToday) && nextTime.isBefore(startOfTomorrow);
    }

    private boolean isOverdue(CrmCustomerDO customer, LocalDateTime startOfToday) {
        LocalDateTime nextTime = customer.getContactNextTime();
        return nextTime != null && nextTime.isBefore(startOfToday);
    }

    private List<CrmTradeWorkbenchRespVO.Distribution> buildDistribution(
            Collection<CrmTradeProfileDO> profiles,
            Function<CrmTradeProfileDO, String> keyExtractor) {
        Map<String, Long> counts = profiles.stream()
                .map(keyExtractor)
                .filter(this::hasText)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        return counts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed().thenComparing(Map.Entry::getKey))
                .map(entry -> {
                    CrmTradeWorkbenchRespVO.Distribution distribution = new CrmTradeWorkbenchRespVO.Distribution();
                    distribution.setKey(entry.getKey());
                    distribution.setCount(entry.getValue());
                    return distribution;
                })
                .toList();
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private int valueOrZero(Integer value) {
        return value == null ? 0 : value;
    }

}
