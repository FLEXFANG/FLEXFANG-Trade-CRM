package cn.iocoder.yudao.module.crm.controller.admin.trade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 外贸 CRM 工作台 Response VO")
@Data
public class CrmTradeWorkbenchRespVO {

    @Schema(description = "核心汇总")
    private Summary summary;

    @Schema(description = "优先跟进客户")
    private List<PriorityCustomer> priorityCustomers;

    @Schema(description = "国家分布")
    private List<Distribution> countryDistribution;

    @Schema(description = "来源渠道分布")
    private List<Distribution> sourceDistribution;

    @Data
    public static class Summary {
        @Schema(description = "我负责的客户总数")
        private Long totalCustomers;
        @Schema(description = "今天需要跟进")
        private Long dueToday;
        @Schema(description = "已逾期未跟进")
        private Long overdue;
        @Schema(description = "高优先级客户")
        private Long highPriority;
        @Schema(description = "高整柜潜力客户")
        private Long highFclPotential;
        @Schema(description = "高风险客户")
        private Long highRisk;
    }

    @Data
    public static class PriorityCustomer {
        private Long customerId;
        private String customerName;
        private String countryCode;
        private String companyType;
        private String sourceChannel;
        private Integer leadScore;
        private Integer riskScore;
        private Integer fclProbability;
        private String containerPotential;
        private String qualityGrade;
        private Integer priorityScore;
        private LocalDateTime contactLastTime;
        private LocalDateTime contactNextTime;
        private String nextAction;
    }

    @Data
    public static class Distribution {
        private String key;
        private Long count;
    }

}
