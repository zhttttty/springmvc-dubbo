package com.zhizhuotec.health.service.impl;

import javax.annotation.Resource;

import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.zhizhuotec.health.entity.Sleeping;
import com.zhizhuotec.health.mapper.BaseMapper;
import com.zhizhuotec.health.mapper.SleepingMapper;
import com.zhizhuotec.health.service.SleepingService;

@Service
@Transactional(rollbackFor = Exception.class, readOnly = false)
public class SleepingServiceImpl extends BaseServiceImpl<Sleeping> implements SleepingService {

	@Override
	@Resource
	public void setMapper(BaseMapper<Sleeping> mapper) {
		super.setMapper(mapper);
	}

	@Resource
	private SleepingMapper sleepingMapper;

	public void setSleepingMapper(SleepingMapper sleepingMapper) {
		this.sleepingMapper = sleepingMapper;
	}

}
