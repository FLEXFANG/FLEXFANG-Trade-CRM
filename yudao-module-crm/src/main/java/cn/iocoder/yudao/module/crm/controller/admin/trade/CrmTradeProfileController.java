package cn.iocoder.yudao.module.crm.controller.admin.trade;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.common.validation.InEnum;
import cn.iocoder.yudao.module.crm.controller.admin.trade.vo.CrmTradeProfileRespVO;
import cn.iocoder.yudao.module.crm.controller.admin.trade.vo.CrmTradeProfileSaveReqVO;
import cn.iocoder.yudao.module.crm.dal.dataobject.trade.CrmTradeProfileDO;
import cn.iocoder.yudao.module.crm.enums.trade.CrmTradeBizTypeEnum;
import cn.iocoder.yudao.module.crm.service.trade.CrmTradeProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - CRM 外贸档案")
@RestController
@RequestMapping("/crm/trade-profile")
@Validated
public class CrmTradeProfileController {

    @Resource
    private CrmTradeProfileService tradeProfileService;

    @GetMapping("/get")
    @Operation(summary = "获取外贸档案")
    @Parameter(name = "bizType", description = "CRM 业务类型：1 线索、2 客户、4 商机", required = true)
    @Parameter(name = "bizId", description = "CRM 业务编号", required = true)
    @PreAuthorize("@ss.hasPermission('crm:trade-profile:query')")
    public CommonResult<CrmTradeProfileRespVO> getTradeProfile(
            @RequestParam("bizType")
            @InEnum(value = CrmTradeBizTypeEnum.class, message = "业务类型仅支持线索、客户、商机") Integer bizType,
            @RequestParam("bizId") Long bizId) {
        CrmTradeProfileDO profile = tradeProfileService.getTradeProfile(bizType, bizId);
        return success(BeanUtils.toBean(profile, CrmTradeProfileRespVO.class));
    }

    @PutMapping("/save")
    @Operation(summary = "新增或更新外贸档案")
    @PreAuthorize("@ss.hasPermission('crm:trade-profile:update')")
    public CommonResult<Long> saveTradeProfile(@Valid @RequestBody CrmTradeProfileSaveReqVO saveReqVO) {
        return success(tradeProfileService.saveTradeProfile(saveReqVO));
    }

}
