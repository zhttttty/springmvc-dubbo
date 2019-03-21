package com.zhizhuotec.health.service.impl;

import javax.annotation.Resource;

import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.zhizhuotec.health.entity.BreathingGuide;
import com.zhizhuotec.health.mapper.BaseMapper;
import com.zhizhuotec.health.mapper.BreathingGuideMapper;
import com.zhizhuotec.health.service.BreathingGuideService;

@Service
@Transactional(rollbackFor = Exception.class, readOnly = false)
public class BreathingGuideServiceImpl extends BaseServiceImpl<BreathingGuide> implements BreathingGuideService {

	@Override
	@Resource
	public void setMapper(BaseMapper<BreathingGuide> mapper) {
		super.setMapper(mapper);
	}

	@Resource
	private BreathingGuideMapper breathingGuideMapper;

	public void setBreathingGuideMapper(BreathingGuideMapper breathingGuideMapper) {
		this.breathingGuideMapper = breathingGuideMapper;
	}

}
