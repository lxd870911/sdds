package com.risen.dao;

import com.jeecms.common.hibernate4.Updater;
import com.jeecms.common.page.Pagination;
import com.risen.entity.RisenDevparty;
import com.risen.entity.RisenPartyChange;

public interface RisenDevpartyDao {
	public Pagination getPage(int pageNo, int pageSize);

	public RisenDevparty findById(Integer id);

	public RisenDevparty save(RisenDevparty bean);

	public RisenDevparty updateByUpdater(Updater<RisenDevparty> updater);

	public RisenDevparty deleteById(Integer id);
	Pagination getPage(int pageNo, int pageSize,String departId);
	RisenDevparty update(RisenDevparty bean);
	
	public RisenDevparty updateWithRisenPartyChange(RisenPartyChange bean);
	
	public Pagination getAllActives(String departIds, int pageNo, int pageSize);
}