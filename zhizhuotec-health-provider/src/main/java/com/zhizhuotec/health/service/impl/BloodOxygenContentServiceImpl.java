package com.zhizhuotec.health.service.impl;

import javax.annotation.Resource;

import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.zhizhuotec.health.entity.BloodOxygenContent;
import com.zhizhuotec.health.mapper.BaseMapper;
import com.zhizhuotec.health.mapper.BloodOxygenContentMapper;
import com.zhizhuotec.health.service.BloodOxygenContentService;

@Service
@Transactional(rollbackFor = Exception.class, readOnly = false)
public class BloodOxygenContentServiceImpl extends BaseServiceImpl<BloodOxygenContent>
		implements BloodOxygenContentService {

	@Override
	@Resource
	public void setMapper(BaseMapper<BloodOxygenContent> mapper) {
		super.setMapper(mapper);
	}

	@Resource
	private BloodOxygenContentMapper bloodOxygenContentMapper;

	public void setBloodOxygenContentMapper(BloodOxygenContentMapper bloodOxygenContentMapper) {
		this.bloodOxygenContentMapper = bloodOxygenContentMapper;
	}

}
