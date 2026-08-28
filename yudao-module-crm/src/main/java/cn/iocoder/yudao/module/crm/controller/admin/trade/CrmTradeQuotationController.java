package cn.iocoder.yudao.module.crm.controller.admin.trade;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.crm.controller.admin.trade.vo.*;
import cn.iocoder.yudao.module.crm.dal.dataobject.trade.CrmTradeQuotationDO;
import cn.iocoder.yudao.module.crm.service.trade.CrmTradeQuotationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - CRM 外贸报价")
@RestController
@RequestMapping("/crm/trade-quotation")
@Validated
public class CrmTradeQuotationController {

    @Resource private CrmTradeQuotationService quotationService;

    @PostMapping("/create")
    @Operation(summary = "创建外贸报价草稿")
    @PreAuthorize("@ss.hasPermission('crm:trade-quotation:create')")
    public CommonResult<Long> create(@Valid @RequestBody CrmTradeQuotationSaveReqVO reqVO) {
        return success(quotationService.createQuotation(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改外贸报价草稿")
    @PreAuthorize("@ss.hasPermission('crm:trade-quotation:update')")
    public CommonResult<Boolean> update(@Valid @RequestBody CrmTradeQuotationSaveReqVO reqVO) {
        quotationService.updateQuotation(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除外贸报价草稿")
    @PreAuthorize("@ss.hasPermission('crm:trade-quotation:delete')")
    public CommonResult<Boolean> delete(@RequestParam("id") Long id) {
        quotationService.deleteQuotation(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取外贸报价")
    @PreAuthorize("@ss.hasPermission('crm:trade-quotation:query')")
    public CommonResult<CrmTradeQuotationRespVO> get(@RequestParam("id") Long id) {
        CrmTradeQuotationDO quotation = quotationService.validateQuotation(id);
        CrmTradeQuotationRespVO respVO = BeanUtils.toBean(quotation, CrmTradeQuotationRespVO.class);
        respVO.setItems(BeanUtils.toBean(quotationService.getQuotationItems(id), CrmTradeQuotationItemReqVO.class));
        return success(respVO);
    }

    @GetMapping("/page")
    @Operation(summary = "获得外贸报价分页")
    @PreAuthorize("@ss.hasPermission('crm:trade-quotation:query')")
    public CommonResult<PageResult<CrmTradeQuotationRespVO>> page(@Valid CrmTradeQuotationPageReqVO reqVO) {
        return success(BeanUtils.toBean(quotationService.getQuotationPage(reqVO), CrmTradeQuotationRespVO.class));
    }

    @PutMapping("/send")
    @Operation(summary = "标记报价已发送")
    @PreAuthorize("@ss.hasPermission('crm:trade-quotation:status')")
    public CommonResult<Boolean> send(@RequestParam("id") Long id) {
        quotationService.sendQuotation(id);
        return success(true);
    }

    @PutMapping("/accept")
    @Operation(summary = "标记客户接受报价")
    @PreAuthorize("@ss.hasPermission('crm:trade-quotation:status')")
    public CommonResult<Boolean> accept(@RequestParam("id") Long id) {
        quotationService.acceptQuotation(id);
        return success(true);
    }

    @PutMapping("/reject")
    @Operation(summary = "标记客户拒绝报价")
    @PreAuthorize("@ss.hasPermission('crm:trade-quotation:status')")
    public CommonResult<Boolean> reject(@Valid @RequestBody CrmTradeQuotationRejectReqVO reqVO) {
        quotationService.rejectQuotation(reqVO.getId(), reqVO.getReason());
        return success(true);
    }

    @PostMapping("/revise")
    @Operation(summary = "生成下一版报价")
    @Parameter(name = "id", required = true)
    @PreAuthorize("@ss.hasPermission('crm:trade-quotation:revise')")
    public CommonResult<Long> revise(@RequestParam("id") Long id) {
        return success(quotationService.reviseQuotation(id));
    }

    @PutMapping("/cancel")
    @Operation(summary = "取消报价")
    @PreAuthorize("@ss.hasPermission('crm:trade-quotation:status')")
    public CommonResult<Boolean> cancel(@RequestParam("id") Long id) {
        quotationService.cancelQuotation(id);
        return success(true);
    }

}
