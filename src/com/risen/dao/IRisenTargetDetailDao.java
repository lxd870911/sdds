package com.risen.dao;

import java.util.List;

import com.jeecms.common.page.Pagination;
import com.risen.entity.RisenTargetDetail;

public interface IRisenTargetDetailDao {
	public Pagination getPage(int pageNo, int pageSize);             //分页
	public RisenTargetDetail save(RisenTargetDetail voteQues);       //保存
	public RisenTargetDetail findById(Integer id);                   //查询指定id的信息
	public RisenTargetDetail findByParentId(Integer id,Integer parentId);           //通过父组织和自己的部门编号查询目标详情
	public void delete(Integer id);                         //删除详情
	public RisenTargetDetail update(RisenTargetDetail bean);           //更新
	public Pagination findByOrgId(int pageNo, int pageSize,Integer id);       //通过父组织查询目标详情
	public Pagination findByParentId(int pageNo, int pageSize,Integer id);    //通过父组织查询目标详情
	public Pagination showAllSub(int pageNo, int pageSize,String parentId);  //展示父组织下的所有目标
	public Pagination getMyTarget(int pageNo, int pageSize,String deptId);  //展示自己的目标详情
	public void deleteByPid(Integer id);                               //父组织删除
	public List<RisenTargetDetail> getAllStart(Integer pid,String status);
	public List<RisenTargetDetail> getAllFinishedList(Integer pid,String status);
	/**
	 * 获取报表信息
	 * @author slc 2016-11-28 下午11:23:16
	 * @return
	 */
	public List<RisenTargetDetail>getReportData(Integer orgId);
	/**
	 * 修改加分基数
	 * @author slc 2016-12-3 下午9:02:32
	 * @return
	 */
	public void updateBaseScore(Integer orgId,Integer score);
	/**
	 * 批量修改加分基数
	 * @author slc 2016-12-3 下午9:04:48
	 * @param orgId
	 * @param score
	 * @return
	 */
	public void batchUpdBaseScore(Integer[] orgId,Integer score);
	public int confirmShare(Integer orgId,Integer score);
	/*
	 * 获取未完成的目标情况
	 */
	public List<RisenTargetDetail> getAllUnfinishedList(Integer departId,
			Integer id);
}
