package cn.iocoder.yudao.module.crm.controller.admin.trade;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.crm.controller.admin.customer.vo.customer.CrmCustomerImportReqVO;
import cn.iocoder.yudao.module.crm.controller.admin.customer.vo.customer.CrmCustomerImportRespVO;
import cn.iocoder.yudao.module.crm.controller.admin.trade.vo.CrmTradeCustomerImportExcelVO;
import cn.iocoder.yudao.module.crm.service.trade.CrmTradeCustomerImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 外贸 CRM 客户")
@RestController
@RequestMapping("/crm/trade-customer")
@Validated
public class CrmTradeCustomerController {

    @Resource
    private CrmTradeCustomerImportService importService;

    @GetMapping("/get-import-template")
    @Operation(summary = "下载外贸客户导入模板")
    public void importTemplate(HttpServletResponse response) throws IOException {
        CrmTradeCustomerImportExcelVO demo = CrmTradeCustomerImportExcelVO.builder()
                .name("Andes Helmets SAS")
                .email("buyer@example.com")
                .countryCode("CO")
                .city("Medellin")
                .companyType("IMPORTER")
                .sourceChannel("META")
                .website("https://example.com")
                .whatsapp("+573001234567")
                .importExperience("YES")
                .annualPurchaseVolume(new BigDecimal("150000"))
                .targetProducts("Flip-up helmet")
                .expectedMoq(1000)
                .targetPrice(new BigDecimal("15.50"))
                .currency("USD")
                .certificationRequirement("DOT")
                .incoterm("FOB")
                .destinationPort("Cartagena")
                .containerPotential("40HQ")
                .fclProbability(75)
                .leadScore(82)
                .riskScore(20)
                .nextAction("Confirm annual purchase plan")
                .build();
        ExcelUtils.write(response, "外贸客户导入模板.xlsx", "Trade CRM", CrmTradeCustomerImportExcelVO.class, List.of(demo));
    }

    @PostMapping("/import")
    @Operation(summary = "导入外贸客户与外贸档案")
    @PreAuthorize("@ss.hasPermission('crm:customer:import')")
    public CommonResult<CrmCustomerImportRespVO> importExcel(@Valid CrmCustomerImportReqVO importReqVO)
            throws Exception {
        List<CrmTradeCustomerImportExcelVO> rows = ExcelUtils.read(importReqVO.getFile(), CrmTradeCustomerImportExcelVO.class);
        return success(importService.importCustomers(rows, importReqVO));
    }

}
