package cn.iocoder.yudao.module.crm.service.trade;

import cn.iocoder.yudao.module.crm.controller.admin.customer.vo.customer.CrmCustomerImportReqVO;
import cn.iocoder.yudao.module.crm.controller.admin.customer.vo.customer.CrmCustomerImportRespVO;
import cn.iocoder.yudao.module.crm.controller.admin.trade.vo.CrmTradeCustomerImportExcelVO;

import java.util.List;

/**
 * 外贸客户 Excel 导入 Service。
 *
 * @author FLEXFANG
 */
public interface CrmTradeCustomerImportService {

    CrmCustomerImportRespVO importCustomers(List<CrmTradeCustomerImportExcelVO> rows,
                                            CrmCustomerImportReqVO importReqVO);

}
