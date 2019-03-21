package com.zhizhuotec.health.service.impl;

import javax.annotation.Resource;

import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.zhizhuotec.health.entity.Electrocardiogram;
import com.zhizhuotec.health.mapper.BaseMapper;
import com.zhizhuotec.health.mapper.ElectrocardiogramMapper;
import com.zhizhuotec.health.service.ElectrocardiogramService;

@Service
@Transactional(rollbackFor = Exception.class, readOnly = false)
public class ElectrocardiogramServiceImpl extends BaseServiceImpl<Electrocardiogram>
		implements ElectrocardiogramService {

	@Override
	@Resource
	public void setMapper(BaseMapper<Electrocardiogram> mapper) {
		super.setMapper(mapper);
	}

	@Resource
	private ElectrocardiogramMapper electrocardiogramMapper;

	public void setElectrocardiogramMapper(ElectrocardiogramMapper electrocardiogramMapper) {
		this.electrocardiogramMapper = electrocardiogramMapper;
	}

}
