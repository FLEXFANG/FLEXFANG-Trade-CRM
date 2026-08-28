package cn.iocoder.yudao.module.crm.controller.admin.trade;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.crm.controller.admin.trade.vo.*;
import cn.iocoder.yudao.module.crm.dal.dataobject.trade.CrmTradeSampleDO;
import cn.iocoder.yudao.module.crm.service.trade.CrmTradeSampleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - CRM 外贸样品")
@RestController
@RequestMapping("/crm/trade-sample")
@Validated
public class CrmTradeSampleController {

    @Resource
    private CrmTradeSampleService sampleService;

    @PostMapping("/create")
    @Operation(summary = "创建外贸样品记录")
    @PreAuthorize("@ss.hasPermission('crm:trade-sample:create')")
    public CommonResult<Long> createSample(@Valid @RequestBody CrmTradeSampleSaveReqVO reqVO) {
        return success(sampleService.createSample(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新外贸样品记录")
    @PreAuthorize("@ss.hasPermission('crm:trade-sample:update')")
    public CommonResult<Boolean> updateSample(@Valid @RequestBody CrmTradeSampleSaveReqVO reqVO) {
        sampleService.updateSample(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除外贸样品记录")
    @Parameter(name = "id", required = true)
    @PreAuthorize("@ss.hasPermission('crm:trade-sample:delete')")
    public CommonResult<Boolean> deleteSample(@RequestParam("id") Long id) {
        sampleService.deleteSample(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取外贸样品记录")
    @Parameter(name = "id", required = true)
    @PreAuthorize("@ss.hasPermission('crm:trade-sample:query')")
    public CommonResult<CrmTradeSampleRespVO> getSample(@RequestParam("id") Long id) {
        CrmTradeSampleDO sample = sampleService.validateSample(id);
        CrmTradeSampleRespVO respVO = BeanUtils.toBean(sample, CrmTradeSampleRespVO.class);
        respVO.setItems(BeanUtils.toBean(sampleService.getSampleItems(id), CrmTradeSampleItemReqVO.class));
        return success(respVO);
    }

    @GetMapping("/page")
    @Operation(summary = "获得外贸样品分页")
    @PreAuthorize("@ss.hasPermission('crm:trade-sample:query')")
    public CommonResult<PageResult<CrmTradeSampleRespVO>> getSamplePage(@Valid CrmTradeSamplePageReqVO pageReqVO) {
        return success(BeanUtils.toBean(sampleService.getSamplePage(pageReqVO), CrmTradeSampleRespVO.class));
    }

}
