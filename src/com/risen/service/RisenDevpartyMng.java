package com.risen.service;

import com.jeecms.common.page.Pagination;
import com.risen.entity.RisenDevparty;
import com.risen.entity.RisenPartyChange;

public interface RisenDevpartyMng {
	public Pagination getPage(int pageNo, int pageSize);

	public RisenDevparty findById(Integer id);

	public RisenDevparty save(RisenDevparty bean);

	public RisenDevparty update(RisenDevparty bean);

	public RisenDevparty deleteById(Integer id);
	
	public RisenDevparty[] deleteByIds(Integer[] ids);
	public Pagination getPage(int pageNo, int pageSize,String departId);
	
	public RisenDevparty updateWithRisenPartyChange(RisenPartyChange bean);
	//获取入党积极分子
	public Pagination getAllActives(Integer departId,int pageNo, int pageSize);
}