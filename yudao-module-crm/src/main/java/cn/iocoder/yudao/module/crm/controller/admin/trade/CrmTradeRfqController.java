package cn.iocoder.yudao.module.crm.controller.admin.trade;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.crm.controller.admin.trade.vo.*;
import cn.iocoder.yudao.module.crm.dal.dataobject.trade.CrmTradeRfqDO;
import cn.iocoder.yudao.module.crm.service.trade.CrmTradeRfqService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - CRM 外贸 RFQ")
@RestController
@RequestMapping("/crm/trade-rfq")
@Validated
public class CrmTradeRfqController {

    @Resource
    private CrmTradeRfqService rfqService;

    @PostMapping("/create")
    @Operation(summary = "创建外贸 RFQ")
    @PreAuthorize("@ss.hasPermission('crm:trade-rfq:create')")
    public CommonResult<Long> createRfq(@Valid @RequestBody CrmTradeRfqSaveReqVO reqVO) {
        return success(rfqService.createRfq(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新外贸 RFQ")
    @PreAuthorize("@ss.hasPermission('crm:trade-rfq:update')")
    public CommonResult<Boolean> updateRfq(@Valid @RequestBody CrmTradeRfqSaveReqVO reqVO) {
        rfqService.updateRfq(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除外贸 RFQ")
    @Parameter(name = "id", required = true)
    @PreAuthorize("@ss.hasPermission('crm:trade-rfq:delete')")
    public CommonResult<Boolean> deleteRfq(@RequestParam("id") Long id) {
        rfqService.deleteRfq(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取外贸 RFQ")
    @Parameter(name = "id", required = true)
    @PreAuthorize("@ss.hasPermission('crm:trade-rfq:query')")
    public CommonResult<CrmTradeRfqRespVO> getRfq(@RequestParam("id") Long id) {
        CrmTradeRfqDO rfq = rfqService.validateRfq(id);
        CrmTradeRfqRespVO respVO = BeanUtils.toBean(rfq, CrmTradeRfqRespVO.class);
        respVO.setItems(BeanUtils.toBean(rfqService.getRfqItems(id), CrmTradeRfqItemReqVO.class));
        return success(respVO);
    }

    @GetMapping("/page")
    @Operation(summary = "获得外贸 RFQ 分页")
    @PreAuthorize("@ss.hasPermission('crm:trade-rfq:query')")
    public CommonResult<PageResult<CrmTradeRfqRespVO>> getRfqPage(@Valid CrmTradeRfqPageReqVO pageReqVO) {
        return success(BeanUtils.toBean(rfqService.getRfqPage(pageReqVO), CrmTradeRfqRespVO.class));
    }

}
