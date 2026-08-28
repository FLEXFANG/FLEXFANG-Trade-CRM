package cn.iocoder.yudao.module.crm.controller.admin.trade;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.crm.controller.admin.trade.vo.CrmTradeWorkbenchRespVO;
import cn.iocoder.yudao.module.crm.service.trade.CrmTradeWorkbenchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "管理后台 - 外贸 CRM 工作台")
@RestController
@RequestMapping("/crm/trade-workbench")
@Validated
public class CrmTradeWorkbenchController {

    @Resource
    private CrmTradeWorkbenchService workbenchService;

    @GetMapping("/get")
    @Operation(summary = "获得当前销售人员的外贸 CRM 工作台")
    @PreAuthorize("@ss.hasPermission('crm:customer:query')")
    public CommonResult<CrmTradeWorkbenchRespVO> getWorkbench() {
        return success(workbenchService.getWorkbench(getLoginUserId()));
    }

}
