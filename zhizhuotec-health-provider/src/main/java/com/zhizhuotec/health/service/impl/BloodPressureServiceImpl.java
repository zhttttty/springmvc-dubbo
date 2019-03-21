package com.zhizhuotec.health.service.impl;

import javax.annotation.Resource;

import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.zhizhuotec.health.entity.BloodPressure;
import com.zhizhuotec.health.mapper.BaseMapper;
import com.zhizhuotec.health.mapper.BloodPressureMapper;
import com.zhizhuotec.health.service.BloodPressureService;

@Service
@Transactional(rollbackFor = Exception.class, readOnly = false)
public class BloodPressureServiceImpl extends BaseServiceImpl<BloodPressure> implements BloodPressureService {

	@Override
	@Resource
	public void setMapper(BaseMapper<BloodPressure> mapper) {
		super.setMapper(mapper);
	}

	@Resource
	private BloodPressureMapper bloodPressureMapper;

	public void setBloodPressureMapper(BloodPressureMapper bloodPressureMapper) {
		this.bloodPressureMapper = bloodPressureMapper;
	}

}
