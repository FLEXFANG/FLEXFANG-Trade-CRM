package cn.iocoder.yudao.module.crm.service.trade;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.crm.controller.admin.trade.vo.CrmTradeProfileSaveReqVO;
import cn.iocoder.yudao.module.crm.dal.dataobject.trade.CrmTradeProfileDO;
import cn.iocoder.yudao.module.crm.dal.mysql.trade.CrmTradeProfileMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;

/**
 * CRM 外贸档案 Service 实现。
 *
 * @author FLEXFANG
 */
@Service
@Validated
public class CrmTradeProfileServiceImpl implements CrmTradeProfileService {

    @Resource
    private CrmTradeProfileMapper tradeProfileMapper;

    @Override
    public CrmTradeProfileDO getTradeProfile(Integer bizType, Long bizId) {
        return tradeProfileMapper.selectByBiz(bizType, bizId);
    }

    @Override
    public Long saveTradeProfile(CrmTradeProfileSaveReqVO saveReqVO) {
        CrmTradeProfileDO dbProfile = getTradeProfile(saveReqVO.getBizType(), saveReqVO.getBizId());
        CrmTradeProfileDO profile = BeanUtils.toBean(saveReqVO, CrmTradeProfileDO.class);
        if (Objects.nonNull(dbProfile)) {
            tradeProfileMapper.updateById(profile.setId(dbProfile.getId()));
            return dbProfile.getId();
        }
        tradeProfileMapper.insert(profile);
        return profile.getId();
    }

}
