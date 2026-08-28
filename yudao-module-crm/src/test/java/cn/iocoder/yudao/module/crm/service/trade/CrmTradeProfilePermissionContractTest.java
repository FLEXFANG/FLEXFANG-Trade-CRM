package cn.iocoder.yudao.module.crm.service.trade;

import cn.iocoder.yudao.module.crm.controller.admin.trade.vo.CrmTradeProfileSaveReqVO;
import cn.iocoder.yudao.module.crm.enums.permission.CrmPermissionLevelEnum;
import cn.iocoder.yudao.module.crm.framework.permission.core.annotations.CrmPermission;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CrmTradeProfilePermissionContractTest {

    @Test
    void getTradeProfile_requiresDynamicReadPermission() throws Exception {
        Method method = CrmTradeProfileServiceImpl.class.getMethod("getTradeProfile", Integer.class, Long.class);
        CrmPermission permission = method.getAnnotation(CrmPermission.class);

        assertNotNull(permission);
        assertEquals("#bizType", permission.bizTypeValue());
        assertEquals("#bizId", permission.bizId());
        assertEquals(CrmPermissionLevelEnum.READ, permission.level());
    }

    @Test
    void saveTradeProfile_requiresDynamicWritePermission() throws Exception {
        Method method = CrmTradeProfileServiceImpl.class.getMethod("saveTradeProfile", CrmTradeProfileSaveReqVO.class);
        CrmPermission permission = method.getAnnotation(CrmPermission.class);

        assertNotNull(permission);
        assertEquals("#saveReqVO.bizType", permission.bizTypeValue());
        assertEquals("#saveReqVO.bizId", permission.bizId());
        assertEquals(CrmPermissionLevelEnum.WRITE, permission.level());
    }

}
