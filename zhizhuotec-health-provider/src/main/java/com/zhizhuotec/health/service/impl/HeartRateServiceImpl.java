package com.zhizhuotec.health.service.impl;

import javax.annotation.Resource;

import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.zhizhuotec.health.entity.HeartRate;
import com.zhizhuotec.health.mapper.BaseMapper;
import com.zhizhuotec.health.mapper.HeartRateMapper;
import com.zhizhuotec.health.service.HeartRateService;

@Service
@Transactional(rollbackFor = Exception.class, readOnly = false)
public class HeartRateServiceImpl extends BaseServiceImpl<HeartRate> implements HeartRateService {

	@Override
	@Resource
	public void setMapper(BaseMapper<HeartRate> mapper) {
		super.setMapper(mapper);
	}

	@Resource
	private HeartRateMapper heartRateMapper;

	public void setHeartRateMapper(HeartRateMapper heartRateMapper) {
		this.heartRateMapper = heartRateMapper;
	}
}
