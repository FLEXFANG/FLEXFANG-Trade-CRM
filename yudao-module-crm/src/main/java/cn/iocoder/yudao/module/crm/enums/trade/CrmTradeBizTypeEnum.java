package cn.iocoder.yudao.module.crm.enums.trade;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import cn.iocoder.yudao.module.crm.enums.common.CrmBizTypeEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * 外贸档案支持的 CRM 业务类型。
 *
 * <p>第一阶段只允许挂载在线索、客户、商机，避免外贸档案被误用于联系人、合同、产品、回款等对象。</p>
 *
 * @author FLEXFANG
 */
@RequiredArgsConstructor
@Getter
public enum CrmTradeBizTypeEnum implements ArrayValuable<Integer> {

    CRM_CLUE(CrmBizTypeEnum.CRM_CLUE.getType()),
    CRM_CUSTOMER(CrmBizTypeEnum.CRM_CUSTOMER.getType()),
    CRM_BUSINESS(CrmBizTypeEnum.CRM_BUSINESS.getType());

    public static final Integer[] ARRAYS = Arrays.stream(values())
            .map(CrmTradeBizTypeEnum::getType)
            .toArray(Integer[]::new);

    private final Integer type;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}
