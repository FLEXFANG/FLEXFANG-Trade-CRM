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

    @Override
    public Long copyTradeProfile(Integer sourceBizType, Long sourceBizId, Integer targetBizType, Long targetBizId) {
        CrmTradeProfileDO sourceProfile = getTradeProfile(sourceBizType, sourceBizId);
        if (sourceProfile == null) {
            return null;
        }

        CrmTradeProfileDO targetProfile = BeanUtils.toBean(sourceProfile, CrmTradeProfileDO.class);
        targetProfile.setId(null);
        targetProfile.setBizType(targetBizType);
        targetProfile.setBizId(targetBizId);
        targetProfile.clean();
        targetProfile.setDeleted(null);

        CrmTradeProfileDO existingTarget = getTradeProfile(targetBizType, targetBizId);
        if (existingTarget != null) {
            targetProfile.setId(existingTarget.getId());
            tradeProfileMapper.updateById(targetProfile);
            return existingTarget.getId();
        }
        tradeProfileMapper.insert(targetProfile);
        return targetProfile.getId();
    }

}
