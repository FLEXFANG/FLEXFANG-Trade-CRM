package cn.iocoder.yudao.module.crm.controller.admin.trade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 外贸客户详情 Response VO")
@Data
public class CrmTradeCustomerDetailRespVO {

    @Schema(description = "客户基础资料 + 外贸画像")
    private CrmTradeCustomerRespVO customer;

    @Schema(description = "最近跟进记录，最多 20 条")
    private List<FollowUp> recentFollowUps;

    @Data
    public static class FollowUp {
        private Long id;
        private Integer type;
        private String content;
        private LocalDateTime nextTime;
        private LocalDateTime createTime;
    }

}
