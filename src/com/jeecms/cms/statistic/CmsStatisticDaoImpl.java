package com.jeecms.cms.statistic;

import static com.jeecms.cms.statistic.CmsStatistic.SITEID;
import static com.jeecms.cms.statistic.CmsStatistic.ISREPLYED;
import static com.jeecms.cms.statistic.CmsStatistic.USERID;
import static com.jeecms.cms.statistic.CmsStatistic.STATISTIC_BY_DAY;
import static com.jeecms.cms.statistic.CmsStatistic.STATISTIC_BY_MONTH;
import static com.jeecms.cms.statistic.CmsStatistic.STATISTIC_BY_YEAR;
import static com.jeecms.cms.statistic.CmsStatistic.STATISTIC_BY_YEARS;

import static com.jeecms.cms.statistic.CmsStatistic.CHANNELID;
import static com.jeecms.cms.statistic.CmsStatistic.STATUS;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.jeecms.cms.statistic.CmsStatistic.CmsStatisticModel;
import com.jeecms.cms.statistic.CmsStatistic.TimeRange;
import com.jeecms.common.hibernate4.Finder;
import com.jeecms.common.hibernate4.HibernateSimpleDao;
import com.jeecms.core.entity.CmsDepartment;
import com.jeecms.core.manager.CmsDepartmentMng;
import com.utility.date.DateUtil;

@Repository
public class CmsStatisticDaoImpl extends HibernateSimpleDao implements
		CmsStatisticDao {
	public long memberStatistic(TimeRange timeRange) {
		Finder f = createCacheableFinder("select count(*) from CmsUser bean where 1=1");
		if (timeRange != null) {
			if(timeRange.getBegin()!=null){
				f.append(" and bean.registerTime >= :begin");
				f.setParam("begin", timeRange.getBegin());
			}
			if(timeRange.getEnd()!=null){
				f.append(" and bean.registerTime < :end");
				f.setParam("end", timeRange.getEnd());
			}
		}
		f.append(" and bean.admin is false");
		return (Long) find(f).iterator().next();
	}
	
	public long memberStatistic(TimeRange timeRange, String statisticType,Map<String, Object> restrictions,CmsDepartment dept){
		String sql = null;
		String csql = null;
		Integer deptId = (Integer) restrictions.get("deptId");
		Date date= new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		String thisyear= sdf.format(date);
		if(deptId!=null && !deptId.equals(1)){
			csql="select dp.depart_id from jc_department dp start with dp.depart_id="+deptId+" connect by prior dp.depart_id=dp.parent_id";
		}else{
			csql="select dp.depart_id from jc_department dp start with dp.parent_id is null or dp.parent_id='' connect by prior dp.depart_id=dp.parent_id";
		}
		if (!StringUtils.isBlank(statisticType)){
			if (CmsStatisticModel.lastYear.toString().equals(statisticType)) {
				if (timeRange != null) {
					if(timeRange.getBegin()!=null){
						//修改去年开始时间
						Date normalDate = timeRange.getBegin();
						Calendar begin=Calendar.getInstance();
						begin.setTime(normalDate);
						begin.add(Calendar.YEAR,-1);
						Date startDate = begin.getTime();
						String sql2=null;
						if(deptId!=null){
							String sql3=null;
							sql3= "select distinct user_id from jc_user  where 1=1  ";
							sql3+=" and sddscp_zb in("+csql+")";
							sql3+=" or sddscp_dzz in("+csql+")"	;	
						    sql3+=" or sddscp_jgdw in("+csql+")";
						    sql3+=" or depart_id in("+csql+")";
						    sql2="select count(user_id) from jc_user bean where 1=1 and bean.user_id in("+sql3+")";
						    sql2+= " and bean.sddscp_isperororg='1'";
							sql2+=" and bean.register_time >= to_date('"+DateUtil.format(startDate, "yyyy-MM-dd HH:mm:ss")+"','yyyy-mm-dd hh24:mi:ss')";
							sql2+=" and bean.register_time < to_date('"+DateUtil.format(normalDate, "yyyy-MM-dd HH:mm:ss")+"','yyyy-mm-dd hh24:mi:ss')";
							sql2+="  and bean.sddscp_Isvalid='1' and bean.sddscp_isinjob<>'2'";
						}else{
						sql2 = "select count(user_id) from jc_user  where 1=1  ";
						sql2 += " and sddscp_isperororg='1'";
						sql2+=" and register_time >= to_date('"+DateUtil.format(startDate, "yyyy-MM-dd HH:mm:ss")+"','yyyy-mm-dd hh24:mi:ss')";
						sql2+=" and register_time < to_date('"+DateUtil.format(normalDate, "yyyy-MM-dd HH:mm:ss")+"','yyyy-mm-dd hh24:mi:ss')";
						sql2+="  and sddscp_Isvalid='1' and sddscp_isinjob<>'2'";
					    }
						List list = this.getSession().createSQLQuery(sql2).list();
						Object obj = list.get(0);
						return Long.valueOf(obj.toString());
					}
				}
			}else if (CmsStatisticModel.thisYearAdd.toString().equals(statisticType)) {
				if (timeRange != null) {
					if(timeRange.getBegin()!=null){
						if(dept.getSddspoOrglevel().intValue()==4 && dept.getSddspoOrgtype().equals("机关党委")){//全系统增加人数
							sql="select count(*) from jc_floating where SDDSFI_INPROVINCE=1 and SDDSFI_YEAR='"+thisyear+"'";	
						}
						if(dept.getSddspoOrglevel().intValue()==3){//本市增加多少人
							sql="select count(*) from jc_floating where  SDDSFI_ORGID in("+csql+") and  SDDSFI_INCITY=1 and SDDSFI_YEAR='"+thisyear+"'";	
						}
						if(dept.getSddspoOrglevel().intValue()==1){//本县增加多少人
							sql="select count(*) from jc_floating where SDDSFI_ORGID in("+csql+") and SDDSFI_INCOUNTY=1 and SDDSFI_YEAR='"+thisyear+"'";	
						}
					}
				}
				List list = this.getSession().createSQLQuery(sql).list();
				Object obj = list.get(0);
				return Long.valueOf(obj.toString());
			}else if(CmsStatisticModel.thisYearDecrease.toString().equals(statisticType)){
				if (timeRange != null) {
					if(timeRange.getBegin()!=null){
						Date normalDate = timeRange.getBegin();
						Calendar begin=Calendar.getInstance();
						begin.setTime(normalDate);
						begin.add(Calendar.YEAR,1);
						if(dept.getSddspoOrglevel().intValue()==4 && dept.getSddspoOrgtype().equals("机关党委")){//全系统减少人数
							sql="select count(*) from jc_floating where SDDSFI_OUTPROVINCE=1 and SDDSFI_YEAR='"+thisyear+"'";	
						}
						if(dept.getSddspoOrglevel().intValue()==3){//本市减少多少人
							sql="select count(*) from jc_floating where  SDDSFI_ORGID in("+csql+") and  SDDSFI_OUTCITY=1 and SDDSFI_YEAR='"+thisyear+"'";	
						}
						if(dept.getSddspoOrglevel().intValue()==1){//本县减少多少人
							sql="select count(*) from jc_floating where SDDSFI_ORGID in("+csql+") and SDDSFI_OUTCOUNTY=1 and SDDSFI_YEAR='"+thisyear+"'";	
						}
					}
				}
				List list = this.getSession().createSQLQuery(sql).list();
				Object obj = list.get(0);
				return Long.valueOf(obj.toString());
			}else if(CmsStatisticModel.endOfTheYear.toString().equals(statisticType)){
				if (timeRange != null) {
					if(timeRange.getBegin()!=null){
						Date normalDate = timeRange.getBegin();
						Calendar begin=Calendar.getInstance();
						begin.setTime(normalDate);
						begin.add(Calendar.YEAR,-1);
						Date startDate = begin.getTime();
						String sql2=null;
						if(deptId!=null){
							String sql3=null;
							sql3= "select distinct user_id from jc_user  where 1=1  ";
							sql3+=" and sddscp_zb in("+csql+")";
							sql3+=" or sddscp_dzz in("+csql+")"	;	
						    sql3+=" or sddscp_jgdw in("+csql+")";
						    sql3+=" or depart_id in("+csql+")";
						    sql2="select count(user_id) from jc_user bean where 1=1 and bean.user_id in("+sql3+")";
						    sql2+= " and bean.sddscp_isperororg='1'";
							sql2+=" and bean.register_time >= to_date('"+DateUtil.format(startDate, "yyyy-MM-dd HH:mm:ss")+"','yyyy-mm-dd hh24:mi:ss')";
							sql2+=" and bean.register_time < to_date('"+DateUtil.format(normalDate, "yyyy-MM-dd HH:mm:ss")+"','yyyy-mm-dd hh24:mi:ss')";
							sql2+="  and bean.sddscp_Isvalid='1' and bean.sddscp_isinjob<>'2'";
						}else{
						sql2 = "select count(user_id) from jc_user  where 1=1  ";
						sql2 += " and sddscp_isperororg='1'";
						sql2+=" and register_time >= to_date('"+DateUtil.format(startDate, "yyyy-MM-dd HH:mm:ss")+"','yyyy-mm-dd hh24:mi:ss')";
						sql2+=" and register_time < to_date('"+DateUtil.format(normalDate, "yyyy-MM-dd HH:mm:ss")+"','yyyy-mm-dd hh24:mi:ss')";
						sql2+="  and sddscp_Isvalid='1' and sddscp_isinjob<>'2'";
					    }
						List list = this.getSession().createSQLQuery(sql2).list();
						Object obj = list.get(0);
						return Long.valueOf(obj.toString());
					}
				}
				
			}else if(CmsStatisticModel.atTheEndOfTheYear.toString().equals(statisticType)){
				String sql2 = "select count(bean.user_id) from jc_user bean where 1=1  ";
				sql2 += " and bean.sddscp_isperororg='1'";
				if (timeRange != null) {
					if(timeRange.getBegin()!=null){
						Date normalDate = timeRange.getBegin();
						Calendar begin=Calendar.getInstance();
						begin.setTime(normalDate);
						begin.add(Calendar.YEAR,1);
						Date startDate = begin.getTime();
						sql2+=" and bean.register_time < to_date('"+DateUtil.format(startDate, "yyyy-MM-dd HH:mm:ss")+"','yyyy-mm-dd hh24:mi:ss')";
					}
				}
				sql2+=" and bean.sddscp_isinjob<>'2' and bean.sddscp_Isvalid='1' ";
				List list = this.getSession().createSQLQuery(sql2).list();
				Object obj = list.get(0);
				return Long.valueOf(obj.toString());
			}
		}
		return 0L;
	}
	
	public List<Object[]> statisticMemberByTarget(Integer target,String statisticType,Date timeBegin,Date timeEnd,Map<String, Object> restrictions){
		String hql = "";
		Finder f = null;
		if (!StringUtils.isBlank(statisticType)) {
			Integer deptId = (Integer) restrictions.get("deptId");
			Integer deptType = (Integer) restrictions.get("deptType");
			if (statisticType.equals(CmsStatisticModel.lastYear.toString())) {
				hql="select count(bean.id) from CmsUser bean where 1=1";
				f = Finder.create(hql);
				if(timeBegin!=null){
					f.append(" and bean.registerTime <:timeBegin").setParam("timeBegin", timeBegin);
				}
				f.append(" and bean.sddscpChangestype='1' group by bean.sddscpChanges");
				return find(f);
			}else if(statisticType.equals(CmsStatisticModel.thisYearAdd.toString())){
				//target---1：本年新增发展党员 2：外地转入 3：转出外地
				if (target.equals(1)) {
					String sql="select count(bean.user_id) from jc_user bean where 1=1 and bean.sddscp_isperororg='1' and bean.sddscp_changestype='1' and bean.sddscp_Politicaltype='2' and bean.sddscp_isvalid='1' and bean.sddscp_isinjob='1'";
					if (deptId!=null) {
						String cndSql = "(" +
								"select u.user_id from jc_user u where " +
								"u.sddscp_jgdwname in (select dp.depart_name from jc_department dp start with dp.depart_id="+deptId+" connect by prior dp.depart_id=dp.parent_id) or "+
								"u.sddscp_dzzname in (select dp.depart_name from jc_department dp start with dp.depart_id="+deptId+" connect by prior dp.depart_id=dp.parent_id) or "+
								"u.sddscp_zbname in (select dp.depart_name from jc_department dp start with dp.depart_id="+deptId+" connect by prior dp.depart_id=dp.parent_id) or " +
								"u.depart_id in (select dp.depart_id from jc_department dp start with dp.depart_id="+deptId+" connect by prior dp.depart_id=dp.parent_id))";
						sql += " and bean.sddscp_isperororg='1' and bean.user_id in ("+cndSql+")";
					}
					if( timeBegin!=null){
						sql += " and bean.register_time >= to_date('"+DateUtil.format(timeBegin, "yyyy-MM-dd HH:mm:ss")+"','yyyy-mm-dd hh24:mi:ss')";
					}
					List list = this.getSession().createSQLQuery(sql).list();
					return list;
				}else if (target.equals(2)) {
					//deptType---1：区县局机关 2：乡镇/街道/基层所 3：市局机关 4：省局机关
					if (deptType!=null) {
						hql="select count(bean.id) from CmsFloating bean where 1=1";
						f = Finder.create(hql);
						if (deptType.equals(1)) {
							f.append(" and bean.sddsfiIncounty='1'");
						}else if (deptType.equals(2)) {
							f.append(" and bean.sddsfiIncounty='1'");
						}else if (deptType.equals(3)) {
							f.append(" and bean.sddsfiIncity='1'");
						}else if (deptType.equals(4)) {
							f.append(" and bean.sddsfiInprovince='1'");
						}
						if (deptId!=null) {
							boolean isShiju = false;
							String testId = ","+String.valueOf(deptId)+",";
							if(",68,567,1406,1286,1081,1022,1042,1601,1821,1467,1353,2143,2424,1776,1845,2201,2759,".indexOf(testId)>-1){
								isShiju = true;
							}
							List<CmsDepartment> depts = cmsDepartmentMng.getAllDeptById(deptId, isShiju);
							String deptIds = "(";
							for (CmsDepartment cmsDepartment : depts) {
								deptIds = deptIds + "'"+cmsDepartment.getId()+"',";
							}
							deptIds =  StringUtils.stripEnd(deptIds, ",")+")";
							f.append(" and bean.sddsfiOrgid in "+deptIds);
						}
						f.append(" and bean.sddsfiYear =:timeBegin").setParam("timeBegin", DateUtil.format(new Date(), "yyyy"));
						return find(f);
					}
				}
			}else if(statisticType.equals(CmsStatisticModel.thisYearDecrease.toString())){
				//deptType---1：区县局机关 2：乡镇/街道/基层所 3：市局机关 4：省局机关
				if (deptType!=null) {
					hql="select count(bean.id) from CmsFloating bean where 1=1";
					f = Finder.create(hql);
					if (deptType.equals(1)) {
						f.append(" and bean.sddsfiOutcounty='1'");
					}else if (deptType.equals(2)) {
						f.append(" and bean.sddsfiOutcounty='1'");
					}else if (deptType.equals(3)) {
						f.append(" and bean.sddsfiOutcity='1'");
					}else if (deptType.equals(4)) {
						f.append(" and bean.sddsfiOutprovince='1'");
					}
					if (deptId!=null) {
						boolean isShiju = false;
						String testId = ","+String.valueOf(deptId)+",";
						if(",68,567,1406,1286,1081,1022,1042,1601,1821,1467,1353,2143,2424,1776,1845,2201,2759,".indexOf(testId)>-1){
							isShiju = true;
						}
						List<CmsDepartment> depts = cmsDepartmentMng.getAllDeptById(deptId, isShiju);
						String deptIds = "(";
						for (CmsDepartment cmsDepartment : depts) {
							deptIds = deptIds + "'"+cmsDepartment.getId()+"',";
						}
						deptIds =  StringUtils.stripEnd(deptIds, ",")+")";
						f.append(" and bean.sddsfiOrgid in "+deptIds);
					}
					f.append(" and bean.sddsfiYear =:timeBegin").setParam("timeBegin", DateUtil.format(new Date(), "yyyy"));
					return find(f);
				}
			}
		}
		return find(f);
	}
	
	public List<Object[]> statisticMemberByTarget(Integer target,Date timeBegin,Date timeEnd){
		String hql = "";
		if(target==STATISTIC_BY_DAY){
			hql="select count(bean.id),HOUR(bean.registerTime) from CmsUser bean where 1=1 ";
		}else if(target==STATISTIC_BY_MONTH){
			hql="select count(bean.id),DAY(bean.registerTime) from CmsUser bean  where 1=1  ";
		}else if(target==STATISTIC_BY_YEAR){
			hql="select count(bean.id),MONTH(bean.registerTime) from CmsUser bean  where 1=1 ";
		}else if(target==STATISTIC_BY_YEARS){
			hql="select count(bean.id),YEAR(bean.registerTime) from CmsUser bean  where 1=1 ";
		}
		Finder f = Finder.create(hql);
		if(timeBegin!=null){
			f.append(" and bean.registerTime>=:timeBegin").setParam("timeBegin", timeBegin);
		}
		if(timeEnd!=null){
			f.append(" and bean.registerTime<=:timeEnd").setParam("timeEnd", timeEnd);
		}
		f.append(" and bean.admin=false");
		if(target==STATISTIC_BY_DAY){
			f.append(" group by HOUR(bean.registerTime) order by HOUR(bean.registerTime) asc");
		}else if(target==STATISTIC_BY_MONTH){
			f.append(" group by DAY(bean.registerTime) order by DAY(bean.registerTime) asc");
		}else if(target==STATISTIC_BY_YEAR){
			f.append(" group by MONTH(bean.registerTime) order by MONTH(bean.registerTime) asc");
		}else if(target==STATISTIC_BY_YEARS){
			f.append(" group by YEAR(bean.registerTime) order by YEAR(bean.registerTime) asc");
		}
		return find(f);
	}

	public long contentStatistic(TimeRange timeRange,
			Map<String, Object> restrictions) {
		Finder f = createCacheableFinder("select count(*) from Content bean");
		Integer userId = (Integer) restrictions.get(USERID);
		Integer channelId = (Integer) restrictions.get(CHANNELID);
		Byte status = (Byte) restrictions.get(STATUS);
		if (channelId != null) {
			f.append(" join bean.channel channel,Channel parent");
			f.append(" where channel.lft between parent.lft and parent.rgt");
			f.append(" and channel.site.id=parent.site.id");
			f.append(" and parent.id=:parentId");
			f.setParam("parentId", channelId);
		} else {
			f.append(" where bean.site.id=:siteId").setParam("siteId",
					restrictions.get(SITEID));
		}
		if (timeRange != null) {
			if(timeRange.getBegin()!=null){
				f.append(" and bean.contentExt.releaseDate >= :begin");
				f.setParam("begin", timeRange.getBegin());
			}
			if(timeRange.getEnd()!=null){
				f.append(" and bean.contentExt.releaseDate < :end");
				f.setParam("end", timeRange.getEnd());
			}
			
		}
		if (userId != null) {
			f.append(" and bean.user.id=:userId").setParam("userId", userId);
		}
		if(status!=null){
			f.append(" and bean.status=:status").setParam("status", status);
		}
		return (Long) find(f).iterator().next();
	}
	
	public List<Object[]> statisticContentByTarget(Integer target,
			Date timeBegin,Date timeEnd,Map<String, Object> restrictions){
		String hql = "";
		if(target==STATISTIC_BY_DAY){
			hql="select count(bean.id),HOUR(bean.contentExt.releaseDate) from Content bean ";
		}else if(target==STATISTIC_BY_MONTH){
			hql="select count(bean.id),DAY(bean.contentExt.releaseDate) from Content bean  ";
		}else if(target==STATISTIC_BY_YEAR){
			hql="select count(bean.id),MONTH(bean.contentExt.releaseDate) from Content bean ";
		}
		Finder f = createCacheableFinder(hql);
		Integer channelId = (Integer) restrictions.get(CHANNELID);
		Byte status = (Byte) restrictions.get(STATUS);
		if (channelId != null) {
			f.append(" join bean.channel channel,Channel parent");
			f.append(" where channel.lft between parent.lft and parent.rgt");
			f.append(" and channel.site.id=parent.site.id");
			f.append(" and parent.id=:parentId");
			f.setParam("parentId", channelId);
		} else {
			f.append(" where bean.site.id=:siteId").setParam("siteId",
					restrictions.get(SITEID));
		}
		if(timeBegin!=null){
			f.append(" and bean.contentExt.releaseDate>=:timeBegin").setParam("timeBegin", timeBegin);
		}
		if(timeEnd!=null){
			f.append(" and bean.contentExt.releaseDate<=:timeEnd").setParam("timeEnd", timeEnd);
		}
		if(status!=null){
			f.append(" and bean.status=:status").setParam("status", status);
		}
		if(target==STATISTIC_BY_DAY){
			f.append(" group by HOUR(bean.contentExt.releaseDate) order by HOUR(bean.contentExt.releaseDate) asc");
		}else if(target==STATISTIC_BY_MONTH){
			f.append(" group by DAY(bean.contentExt.releaseDate) order by DAY(bean.contentExt.releaseDate) asc");
		}else if(target==STATISTIC_BY_YEAR){
			f.append(" group by MONTH(bean.contentExt.releaseDate) order by MONTH(bean.contentExt.releaseDate) asc");
		}
		return find(f);
	}

	public long commentStatistic(TimeRange timeRange,
			Map<String, Object> restrictions) {
		Finder f = createCacheableFinder("select count(*) from CmsComment bean where bean.site.id=:siteId");
		f.setParam("siteId", restrictions.get(SITEID));
		if (timeRange != null) {
			if(timeRange.getBegin()!=null){
				f.append(" and bean.createTime >= :begin");
				f.setParam("begin", timeRange.getBegin());
			}
			if(timeRange.getEnd()!=null){
				f.append(" and bean.createTime < :end");
				f.setParam("end", timeRange.getEnd());
			}
		}
		Boolean isReplyed = (Boolean) restrictions.get(ISREPLYED);
		if (isReplyed != null) {
			if (isReplyed) {
				f.append(" and bean.replayTime is not null");
			} else {
				f.append(" and bean.replayTime is null");
			}
		}
		return (Long) find(f).iterator().next();
	}
	
	public List<Object[]> statisticCommentByTarget(Integer target,Integer siteId,
			Boolean isReplyed,Date timeBegin,Date timeEnd){
		String hql = "";
		if(target==STATISTIC_BY_DAY){
			hql="select count(bean.id),HOUR(bean.createTime) from CmsComment bean where 1=1 ";
		}else if(target==STATISTIC_BY_MONTH){
			hql="select count(bean.id),DAY(bean.createTime) from CmsComment bean  where 1=1  ";
		}else if(target==STATISTIC_BY_YEAR){
			hql="select count(bean.id),MONTH(bean.createTime) from CmsComment bean  where 1=1 ";
		}else if(target==STATISTIC_BY_YEARS){
			hql="select count(bean.id),YEAR(bean.createTime) from CmsComment bean  where 1=1 ";
		}
		Finder f = Finder.create(hql);
		f.setCacheable(true);
		if(timeBegin!=null){
			f.append(" and bean.createTime>=:timeBegin").setParam("timeBegin", timeBegin);
		}
		if(timeEnd!=null){
			f.append(" and bean.createTime<=:timeEnd").setParam("timeEnd", timeEnd);
		}
		if(siteId!=null){
			f.append(" and bean.site.id=:siteId").setParam("siteId", siteId);
		}
		if (isReplyed != null) {
			if (isReplyed) {
				f.append(" and bean.replayTime is not null");
			} else {
				f.append(" and bean.replayTime is null");
			}
		}
		if(target==STATISTIC_BY_DAY){
			f.append(" group by HOUR(bean.createTime) order by HOUR(bean.createTime) asc");
		}else if(target==STATISTIC_BY_MONTH){
			f.append(" group by DAY(bean.createTime) order by DAY(bean.createTime) asc");
		}else if(target==STATISTIC_BY_YEAR){
			f.append(" group by MONTH(bean.createTime) order by MONTH(bean.createTime) asc");
		}else if(target==STATISTIC_BY_YEARS){
			f.append(" group by YEAR(bean.createTime) order by YEAR(bean.createTime) asc");
		}
		return find(f);
	}

	public long guestbookStatistic(TimeRange timeRange,
			Map<String, Object> restrictions) {
		Finder f = createCacheableFinder("select count(*) from CmsGuestbook bean where bean.site.id=:siteId");
		f.setParam("siteId", restrictions.get(SITEID));
		if (timeRange != null) {
			if(timeRange.getBegin()!=null){
				f.append(" and bean.createTime >= :begin");
				f.setParam("begin", timeRange.getBegin());
			}
			if(timeRange.getEnd()!=null){
				f.append(" and bean.createTime < :end");
				f.setParam("end", timeRange.getEnd());
			}
		}
		Boolean isReplyed = (Boolean) restrictions.get(ISREPLYED);
		if (isReplyed != null) {
			if (isReplyed) {
				f.append(" and bean.replayTime is not null");
			} else {
				f.append(" and bean.replayTime is null");
			}
		}
		return (Long) find(f).iterator().next();
	}
	
	public List<Object[]> statisticGuestbookByTarget(Integer target,Integer siteId,
			Boolean isReplyed,Date timeBegin,Date timeEnd){
		String hql = "";
		if(target==STATISTIC_BY_DAY){
			hql="select count(bean.id),HOUR(bean.createTime) from CmsGuestbook bean where 1=1 ";
		}else if(target==STATISTIC_BY_MONTH){
			hql="select count(bean.id),DAY(bean.createTime) from CmsGuestbook bean  where 1=1  ";
		}else if(target==STATISTIC_BY_YEAR){
			hql="select count(bean.id),MONTH(bean.createTime) from CmsGuestbook bean  where 1=1 ";
		}else if(target==STATISTIC_BY_YEARS){
			hql="select count(bean.id),YEAR(bean.createTime) from CmsGuestbook bean  where 1=1 ";
		}
		Finder f = Finder.create(hql);
		f.setCacheable(true);
		if(timeBegin!=null){
			f.append(" and bean.createTime>=:timeBegin").setParam("timeBegin", timeBegin);
		}
		if(timeEnd!=null){
			f.append(" and bean.createTime<=:timeEnd").setParam("timeEnd", timeEnd);
		}
		if(siteId!=null){
			f.append(" and bean.site.id=:siteId").setParam("siteId", siteId);
		}
		if (isReplyed != null) {
			if (isReplyed) {
				f.append(" and bean.replayTime is not null");
			} else {
				f.append(" and bean.replayTime is null");
			}
		}
		if(target==STATISTIC_BY_DAY){
			f.append(" group by HOUR(bean.createTime) order by HOUR(bean.createTime) asc");
		}else if(target==STATISTIC_BY_MONTH){
			f.append(" group by DAY(bean.createTime) order by DAY(bean.createTime) asc");
		}else if(target==STATISTIC_BY_YEAR){
			f.append(" group by MONTH(bean.createTime) order by MONTH(bean.createTime) asc");
		}else if(target==STATISTIC_BY_YEARS){
			f.append(" group by YEAR(bean.createTime) order by YEAR(bean.createTime) asc");
		}
		return find(f);
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> basicInfoList(String statisticType,Integer departId,String sddscpUsertype){
		String cndSql = "";
		if ("DZJG".equals(statisticType))  {
			cndSql = " and bean.sddscp_jobstatus='公务员' and bean.sddscp_isvalid = '1' ";
		}else if ("SYDW".equals(statisticType)) {
 			cndSql = " and bean.sddscp_jobstatus='事业编制人员' and bean.sddscp_isvalid = '1' ";
		}else if ("GQ".equals(statisticType)) {
			cndSql = " and bean.sddscp_jobstatus='工勤' and bean.sddscp_isvalid = '1' ";
		}else if ("LTX".equals(statisticType)) {
			cndSql = " and bean.sddscp_jobstatus='离退休党员' and bean.sddscp_isvalid = '1' ";
		}else if ("FZSZB".equals(statisticType)) {
			cndSql = " and bean.sddscp_jobstatus='非正式在编人员' and bean.sddscp_isvalid = '1' ";
		}else if ("ALL".equals(statisticType)){
			//查询所有，不做任何操作
			cndSql = " and bean.sddscp_isvalid = '1' ";
		}
		
		if (departId!=null && departId>1) {
			//--------由于以下sql能实现查询指定机构下得所有支部，所以弃用本递归查询方法
			/*List<Integer> listIds = new ArrayList<Integer>();
			List<Integer> ids = cmsDepartmentMng.findDpetNameByLoginDept(2805, listIds);*/
			if(statisticType!=null && statisticType.equals("YX")){
				cndSql += " and bean.sddscp_isperororg='1' and bean.sddscp_isinjob<>'2' and bean.user_id in(" +
						"select risends_userid from risen_discussion where  risends_userid in( select u.user_id from jc_user u where " +
						"u.sddscp_jgdwname in (select dp.depart_name from jc_department dp start with dp.depart_id="+departId+" connect by prior dp.depart_id=dp.parent_id) or "+
						"u.sddscp_dzzname in (select dp.depart_name from jc_department dp start with dp.depart_id="+departId+" connect by prior dp.depart_id=dp.parent_id) or "+
						"u.sddscp_zbname in (select dp.depart_name from jc_department dp start with dp.depart_id="+departId+" connect by prior dp.depart_id=dp.parent_id) or "+
						"u.depart_id in (select dp.depart_id from jc_department dp start with dp.depart_id="+departId+" connect by prior dp.depart_id=dp.parent_id)) and  risends_score=1)";
			}else if(statisticType!=null && statisticType.equals("BHG")){
				cndSql += " and bean.sddscp_isperororg='1' and bean.sddscp_isinjob<>'2' and bean.user_id in(" +
						"select risends_userid from risen_discussion where  risends_userid in( select u.user_id from jc_user u where " +
						"u.sddscp_jgdwname in (select dp.depart_name from jc_department dp start with dp.depart_id="+departId+" connect by prior dp.depart_id=dp.parent_id) or "+
						"u.sddscp_dzzname in (select dp.depart_name from jc_department dp start with dp.depart_id="+departId+" connect by prior dp.depart_id=dp.parent_id) or "+
						"u.sddscp_zbname in (select dp.depart_name from jc_department dp start with dp.depart_id="+departId+" connect by prior dp.depart_id=dp.parent_id) or "+
						"u.depart_id in (select dp.depart_id from jc_department dp start with dp.depart_id="+departId+" connect by prior dp.depart_id=dp.parent_id)) and  risends_score=2)";
			}else{
				cndSql += " and bean.sddscp_isperororg='1' and bean.sddscp_isinjob<>'2' and bean.user_id in(" +
						"select u.user_id from jc_user u where " +
						"u.sddscp_jgdwname in (select dp.depart_name from jc_department dp start with dp.depart_id="+departId+" connect by prior dp.depart_id=dp.parent_id) or "+
						"u.sddscp_dzzname in (select dp.depart_name from jc_department dp start with dp.depart_id="+departId+" connect by prior dp.depart_id=dp.parent_id) or "+
						"u.sddscp_zbname in (select dp.depart_name from jc_department dp start with dp.depart_id="+departId+" connect by prior dp.depart_id=dp.parent_id) or "+
						"u.depart_id in (select dp.depart_id from jc_department dp start with dp.depart_id="+departId+" connect by prior dp.depart_id=dp.parent_id))";
			}
		}else{
			cndSql += " and bean.sddscp_isperororg='1'  ";
			if("LTX".equals(statisticType)){
				cndSql += " and bean.sddscp_isinjob='0'";
			}else{
				cndSql += " and bean.sddscp_isinjob<>'2' and bean.sddscp_isvalid = '1'";
				if(statisticType!=null && statisticType.equals("BHG")){
					cndSql += " and bean.user_id in (select risends_userid from risen_discussion where risends_score=2)";
				}else if(statisticType!=null && statisticType.equals("YX")){
					cndSql += " and bean.user_id in (select risends_userid from risen_discussion where risends_score=1)";
				}
			}
		}
		 
		String sql = "select (select count(bean.user_id) from jc_user bean where bean.sddscp_politicaltype='2' and bean.sddscp_changestype='1' "+cndSql+") as sddscpUsertype,"+
				" (select count(bean.user_id) from jc_user bean where bean.sddscp_sex!='3'"+cndSql+") as sddscpSexAll,"+
				" (select count(bean.user_id) from jc_user bean where bean.sddscp_sex='1'"+cndSql+") as sddscpSexB,"+
				" (select count(bean.user_id) from jc_user bean where bean.sddscp_sex='2'"+cndSql+") as sddscpSexG,"+
				" (select count(bean.user_id) from jc_user bean where (bean.sddscp_national not like '汉%')"+cndSql+") as sddscpNational,"+
				" (select count(bean.user_id) from jc_user bean where floor(MONTHS_BETWEEN(to_date(to_char(sysdate, 'yyyy-MM-dd'),'yyyy-MM-dd'),bean.sddscp_birthday) / 12)<=30"+cndSql+") as age1,"+
				" (select count(bean.user_id) from jc_user bean where floor(MONTHS_BETWEEN(to_date(to_char(sysdate, 'yyyy-MM-dd'),'yyyy-MM-dd'),bean.sddscp_birthday) / 12)<=35 and floor(MONTHS_BETWEEN(to_date(to_char(sysdate, 'yyyy-MM-dd'),'yyyy-MM-dd'),bean.sddscp_birthday) / 12)>=31"+cndSql+") as age2,"+
				" (select count(bean.user_id) from jc_user bean where floor(MONTHS_BETWEEN(to_date(to_char(sysdate, 'yyyy-MM-dd'),'yyyy-MM-dd'),bean.sddscp_birthday) / 12)<=40 and floor(MONTHS_BETWEEN(to_date(to_char(sysdate, 'yyyy-MM-dd'),'yyyy-MM-dd'),bean.sddscp_birthday) / 12)>=36"+cndSql+") as age3,"+
				" (select count(bean.user_id) from jc_user bean where floor(MONTHS_BETWEEN(to_date(to_char(sysdate, 'yyyy-MM-dd'),'yyyy-MM-dd'),bean.sddscp_birthday) / 12)<=45 and floor(MONTHS_BETWEEN(to_date(to_char(sysdate, 'yyyy-MM-dd'),'yyyy-MM-dd'),bean.sddscp_birthday) / 12)>=41"+cndSql+") as age4,"+
				" (select count(bean.user_id) from jc_user bean where floor(MONTHS_BETWEEN(to_date(to_char(sysdate, 'yyyy-MM-dd'),'yyyy-MM-dd'),bean.sddscp_birthday) / 12)<=50 and floor(MONTHS_BETWEEN(to_date(to_char(sysdate, 'yyyy-MM-dd'),'yyyy-MM-dd'),bean.sddscp_birthday) / 12)>=46"+cndSql+") as age5,"+
				" (select count(bean.user_id) from jc_user bean where floor(MONTHS_BETWEEN(to_date(to_char(sysdate, 'yyyy-MM-dd'),'yyyy-MM-dd'),bean.sddscp_birthday) / 12)<=55 and floor(MONTHS_BETWEEN(to_date(to_char(sysdate, 'yyyy-MM-dd'),'yyyy-MM-dd'),bean.sddscp_birthday) / 12)>=51"+cndSql+") as age6,"+
				" (select count(bean.user_id) from jc_user bean where floor(MONTHS_BETWEEN(to_date(to_char(sysdate, 'yyyy-MM-dd'),'yyyy-MM-dd'),bean.sddscp_birthday) / 12)<=60 and floor(MONTHS_BETWEEN(to_date(to_char(sysdate, 'yyyy-MM-dd'),'yyyy-MM-dd'),bean.sddscp_birthday) / 12)>=56"+cndSql+") as age7,"+
				" (select count(bean.user_id) from jc_user bean where floor(MONTHS_BETWEEN(to_date(to_char(sysdate, 'yyyy-MM-dd'),'yyyy-MM-dd'),bean.sddscp_birthday) / 12)<=65 and floor(MONTHS_BETWEEN(to_date(to_char(sysdate, 'yyyy-MM-dd'),'yyyy-MM-dd'),bean.sddscp_birthday) / 12)>=61"+cndSql+") as age8,"+
				" (select count(bean.user_id) from jc_user bean where floor(MONTHS_BETWEEN(to_date(to_char(sysdate, 'yyyy-MM-dd'),'yyyy-MM-dd'),bean.sddscp_birthday) / 12)<=70 and floor(MONTHS_BETWEEN(to_date(to_char(sysdate, 'yyyy-MM-dd'),'yyyy-MM-dd'),bean.sddscp_birthday) / 12)>=66"+cndSql+") as age9,"+
				" (select count(bean.user_id) from jc_user bean where floor(MONTHS_BETWEEN(to_date(to_char(sysdate, 'yyyy-MM-dd'),'yyyy-MM-dd'),bean.sddscp_birthday) / 12)>=71"+cndSql+") as age10"+
				" from dual";
		 List list = this.getSession().createSQLQuery(sql)
				 .addScalar("sddscpUsertype", StandardBasicTypes.INTEGER)
				 .addScalar("sddscpSexAll", StandardBasicTypes.INTEGER)
				 .addScalar("sddscpSexB", StandardBasicTypes.INTEGER)
				 .addScalar("sddscpSexG", StandardBasicTypes.INTEGER)
				 .addScalar("sddscpNational", StandardBasicTypes.INTEGER)
				 .addScalar("age1", StandardBasicTypes.INTEGER)
				 .addScalar("age2", StandardBasicTypes.INTEGER)
				 .addScalar("age3", StandardBasicTypes.INTEGER)
				 .addScalar("age4", StandardBasicTypes.INTEGER)
				 .addScalar("age5", StandardBasicTypes.INTEGER)
				 .addScalar("age6", StandardBasicTypes.INTEGER)
				 .addScalar("age7", StandardBasicTypes.INTEGER)
				 .addScalar("age8", StandardBasicTypes.INTEGER)
				 .addScalar("age9", StandardBasicTypes.INTEGER)
				 .addScalar("age10", StandardBasicTypes.INTEGER).list();	
		//f.append(" order by decode(bean.sddscpJobstatus,'公务员',1,'事业编制人员',2,'工勤',3,'离退休人员',4)");
		return list;
	}
	
	public List<Object[]> basicInfoListCount(String statisticType,Integer departId,String sddscpUsertype){
		String cndSql = "";
		if ("DZJG".equals(statisticType))  {
			cndSql = " bean.sddscp_jobstatus='公务员'";
		}else if ("SYDW".equals(statisticType)) {
 			cndSql = " bean.sddscp_jobstatus='事业编制人员'";
		}else if ("GQ".equals(statisticType)) {
			cndSql = " bean.sddscp_jobstatus='工勤'";
		}else if ("LTX".equals(statisticType)) {
			cndSql = " bean.sddscp_jobstatus='离退休党员'";
		}else if ("ALL".equals(statisticType)){
			//查询所有，不做任何操作
		}
		
		if (departId!=null && departId>-1) {
			cndSql += " and bean.sddscp_isperororg='1' and bean.sddscp_isinjob='1' and bean.user_id in(" +
					"select u.user_id from jc_user u where " +
					"u.sddscp_jgdwname in (select dp.depart_name from jc_department dp start with dp.depart_id="+departId+" connect by prior dp.depart_id=dp.parent_id) or "+
					"u.sddscp_dzzname in (select dp.depart_name from jc_department dp start with dp.depart_id="+departId+" connect by prior dp.depart_id=dp.parent_id) or "+
					"u.sddscp_zbname in (select dp.depart_name from jc_department dp start with dp.depart_id="+departId+" connect by prior dp.depart_id=dp.parent_id))";
		}else{
			cndSql += " and bean.sddscp_isperororg='1' and bean.sddscp_isinjob='1'";
			if("LTX".equals(statisticType)){
				cndSql += " and bean.sddscp_isinjob='0'";
			}else{
				cndSql += " and bean.sddscp_isinjob<>'2' and bean.sddscp_isvalid='1'";
			}
		}
		
		String sql = "select count(bean.user_id) as countAll from jc_user bean where "+cndSql;
		List list = this.getSession().createSQLQuery(sql).addScalar("countAll", StandardBasicTypes.INTEGER).list();
		return list;
	}
	
	public List<Object[]> partyTimeList(String statisticType,Integer departId,String sddscpUsertype){
		String cndSql = "";
		if ("DZJG".equals(statisticType))  {
			cndSql = " and bean.sddscp_jobstatus='公务员' and bean.sddscp_isvalid='1' ";
		}else if ("SYDW".equals(statisticType)) {
 			cndSql = " and bean.sddscp_jobstatus='事业编制人员' and bean.sddscp_isvalid='1' ";
		}else if ("GQ".equals(statisticType)) {
			cndSql = " and bean.sddscp_jobstatus='工勤' and bean.sddscp_isvalid='1' ";
		}else if ("LTX".equals(statisticType)) {
			cndSql = " and bean.sddscp_jobstatus='离退休党员' and bean.sddscp_isvalid='1' ";
		}else if("FZSZB".equals(statisticType)){
			cndSql = " and bean.sddscp_jobstatus='非正式在编人员' and bean.sddscp_isvalid='1' ";

		}else if ("ALL".equals(statisticType)){
			//查询所有，不做任何操作
			cndSql = " and bean.sddscp_isvalid='1' ";
		}
		
		if (departId!=null && departId>-1) {
			cndSql += " and bean.sddscp_isperororg='1' and bean.sddscp_isinjob='1' and bean.user_id in(" +
					"select u.user_id from jc_user u where " +
					"u.sddscp_jgdwname in (select dp.depart_name from jc_department dp start with dp.depart_id="+departId+" connect by prior dp.depart_id=dp.parent_id) or "+
					"u.sddscp_dzzname in (select dp.depart_name from jc_department dp start with dp.depart_id="+departId+" connect by prior dp.depart_id=dp.parent_id) or "+
					"u.sddscp_zbname in (select dp.depart_name from jc_department dp start with dp.depart_id="+departId+" connect by prior dp.depart_id=dp.parent_id) or "+
					"u.depart_id in (select dp.depart_id from jc_department dp start with dp.depart_id="+departId+" connect by prior dp.depart_id=dp.parent_id))";
		}else{
			cndSql += " and bean.sddscp_isperororg='1' ";
			if("LTX".equals(statisticType)){
				cndSql += " and bean.sddscp_isinjob='0' ";
			}else{
				cndSql += " and bean.sddscp_isinjob<>'2' and bean.sddscp_isvalid='1'";
			}
		}
		String sql = "select (select count(bean.user_id) from jc_user bean where 1=1 "+cndSql+" and to_char(bean.sddscp_joinpartydate,'yyyy-mm-dd')<='1937-07-06' ) as joinpartydate1,"+
				" (select count(bean.user_id) from jc_user bean where 1=1 "+cndSql+" and to_char(bean.sddscp_joinpartydate,'yyyy-mm-dd')>='1937-07-07' and to_char(bean.sddscp_joinpartydate,'yyyy-mm-dd')<='1945-09-02') as joinpartydate2,"+
				" (select count(bean.user_id) from jc_user bean where 1=1 "+cndSql+" and to_char(bean.sddscp_joinpartydate,'yyyy-mm-dd')>='1945-09-03' and to_char(bean.sddscp_joinpartydate,'yyyy-mm-dd')<='1949-09-30') as joinpartydate3,"+
				" (select count(bean.user_id) from jc_user bean where 1=1 "+cndSql+" and to_char(bean.sddscp_joinpartydate,'yyyy-mm-dd')>='1949-10-01' and to_char(bean.sddscp_joinpartydate,'yyyy-mm-dd')<='1966-04-30') as joinpartydate4,"+
				" (select count(bean.user_id) from jc_user bean where 1=1 "+cndSql+" and to_char(bean.sddscp_joinpartydate,'yyyy-mm-dd')>='1966-05-01' and to_char(bean.sddscp_joinpartydate,'yyyy-mm-dd')<='1976-10-31') as joinpartydate5,"+
				" (select count(bean.user_id) from jc_user bean where 1=1 "+cndSql+" and to_char(bean.sddscp_joinpartydate,'yyyy-mm-dd')>='1976-11-01' and to_char(bean.sddscp_joinpartydate,'yyyy-mm-dd')<='1978-12-31') as joinpartydate6,"+
				" (select count(bean.user_id) from jc_user bean where 1=1 "+cndSql+" and to_char(bean.sddscp_joinpartydate,'yyyy-mm-dd')>='1979-01-01' and to_char(bean.sddscp_joinpartydate,'yyyy-mm-dd')<='2002-10-31') as joinpartydate7,"+
				" (select count(bean.user_id) from jc_user bean where 1=1 "+cndSql+" and to_char(bean.sddscp_joinpartydate,'yyyy-mm-dd')>='2002-11-01' and to_char(bean.sddscp_joinpartydate,'yyyy-mm-dd')<='2012-10-31') as joinpartydate8,"+
				" (select count(bean.user_id) from jc_user bean where 1=1 "+cndSql+" and to_char(bean.sddscp_joinpartydate,'yyyy-mm-dd')>='2012-11-01') as joinpartydate9 from dual ";
		
		List list = getSession().createSQLQuery(sql)
				.addScalar("joinpartydate1", StandardBasicTypes.INTEGER)
				.addScalar("joinpartydate2", StandardBasicTypes.INTEGER)
				.addScalar("joinpartydate3", StandardBasicTypes.INTEGER)
				.addScalar("joinpartydate4", StandardBasicTypes.INTEGER)
				.addScalar("joinpartydate5", StandardBasicTypes.INTEGER)
				.addScalar("joinpartydate6", StandardBasicTypes.INTEGER)
				.addScalar("joinpartydate7", StandardBasicTypes.INTEGER)
				.addScalar("joinpartydate8", StandardBasicTypes.INTEGER)
				.addScalar("joinpartydate9", StandardBasicTypes.INTEGER).list();
		return list;
	}
	
	public List<Object[]> partyTimeListCount(String statisticType,Integer departId,String sddscpUsertype){
		String cndSql = "";
		if ("DZJG".equals(statisticType))  {
			cndSql = " and bean.sddscp_jobstatus='公务员'";
		}else if ("SYDW".equals(statisticType)) {
 			cndSql = " and bean.sddscp_jobstatus='事业编制人员'";
		}else if ("GQ".equals(statisticType)) {
			cndSql = " and bean.sddscp_jobstatus='工勤'";
		}else if ("LTX".equals(statisticType)) {
			cndSql = " and bean.sddscp_jobstatus='离退休党员'";
		}else if ("ALL".equals(statisticType)){
			//查询所有，不做任何操作
		}
		
		if (departId!=null && departId>-1) {
			cndSql += " and bean.sddscp_isperororg='1' and bean.sddscp_isinjob='1' and  bean.user_id in(" +
					"select u.user_id from jc_user u where " +
					"u.sddscp_jgdwname in (select dp.depart_name from jc_department dp start with dp.depart_id="+departId+" connect by prior dp.depart_id=dp.parent_id) or "+
					"u.sddscp_dzzname in (select dp.depart_name from jc_department dp start with dp.depart_id="+departId+" connect by prior dp.depart_id=dp.parent_id) or "+
					"u.sddscp_zbname in (select dp.depart_name from jc_department dp start with dp.depart_id="+departId+" connect by prior dp.depart_id=dp.parent_id))";
		}else{
			cndSql += " and bean.sddscp_isperororg='1' ";
			if("LTX".equals(statisticType)){
				cndSql += " and bean.sddscp_isinjob='0'";
			}else{
				cndSql += " and bean.sddscp_isinjob<>'2' and bean.sddscp_isvalid='1'";
			}
		}
		
		String sql = "select count(bean.user_id) as countAll from jc_user bean where "+cndSql;
		List list = this.getSession().createSQLQuery(sql).addScalar("countAll", StandardBasicTypes.INTEGER).list();
		return list;
	}
	
	public List<Object[]> educationAnalysisList(String statisticType,Integer departId,String sddscpUsertype){
		String cndSql = "";
		if ("DZJG".equals(statisticType))  {
			cndSql = " and bean.sddscp_jobstatus='公务员' and bean.sddscp_isvalid='1' ";
		}else if ("SYDW".equals(statisticType)) {
 			cndSql = " and bean.sddscp_jobstatus='事业编制人员' and bean.sddscp_isvalid='1' ";
		}else if ("GQ".equals(statisticType)) {
			cndSql = " and bean.sddscp_jobstatus='工勤' and bean.sddscp_isvalid='1' ";
		}else if ("LTX".equals(statisticType)) {
			cndSql = " and bean.sddscp_jobstatus='离退休党员' and bean.sddscp_isvalid='1' ";
		}else if("FZSZB".equals(statisticType)){
			cndSql = " and bean.sddscp_jobstatus='非正式在编人员' and bean.sddscp_isvalid='1' ";

		}
		else if ("ALL".equals(statisticType)){
			//查询所有，不做任何操作
			cndSql = " and bean.sddscp_isvalid='1' ";
		}
		
		if (departId!=null && departId>-1) {
			cndSql += " and bean.sddscp_isperororg='1' and bean.sddscp_isinjob='1' and bean.user_id in(" +
					"select u.user_id from jc_user u where " +
					"u.sddscp_jgdwname in (select dp.depart_name from jc_department dp start with dp.depart_id="+departId+" connect by prior dp.depart_id=dp.parent_id) or "+
					"u.sddscp_dzzname in (select dp.depart_name from jc_department dp start with dp.depart_id="+departId+" connect by prior dp.depart_id=dp.parent_id) or "+
					"u.sddscp_zbname in (select dp.depart_name from jc_department dp start with dp.depart_id="+departId+" connect by prior dp.depart_id=dp.parent_id) or "+
					"u.depart_id in (select dp.depart_id from jc_department dp start with dp.depart_id="+departId+" connect by prior dp.depart_id=dp.parent_id))";
		}else{
			cndSql += " and bean.sddscp_isperororg='1' ";
			if("LTX".equals(statisticType)){
				cndSql += " and bean.sddscp_isinjob='0'";
			}else{
				cndSql += " and bean.sddscp_isinjob<>'2' and bean.sddscp_isvalid='1'";
			}
		}
		String sql = "select (select count(bean.user_id) from jc_user bean where 1=1 "+cndSql+" and bean.sddscp_education='1') as sddscpEducation1,"+
				" (select count(bean.user_id) from jc_user bean where 1=1 "+cndSql+" and bean.sddscp_education='2') as sddscpEducation2,"+
				" (select count(bean.user_id) from jc_user bean where 1=1 "+cndSql+" and bean.sddscp_education='3') as sddscpEducation3,"+
				" (select count(bean.user_id) from jc_user bean where 1=1 "+cndSql+" and bean.sddscp_education='4') as sddscpEducation4 from dual ";
		
		List list = getSession().createSQLQuery(sql)
				.addScalar("sddscpEducation1", StandardBasicTypes.INTEGER)
				.addScalar("sddscpEducation2", StandardBasicTypes.INTEGER)
				.addScalar("sddscpEducation3", StandardBasicTypes.INTEGER)
				.addScalar("sddscpEducation4", StandardBasicTypes.INTEGER).list();
		return list;
	}
	
	public List<Object[]> educationAnalysisListCount(String statisticType,Integer departId,String sddscpUsertype){
		String cndSql = "";
		if ("DZJG".equals(statisticType))  {
			cndSql = " bean.sddscp_jobstatus='公务员'";
		}else if ("SYDW".equals(statisticType)) {
 			cndSql = " bean.sddscp_jobstatus='事业编制人员'";
		}else if ("GQ".equals(statisticType)) {
			cndSql = " bean.sddscp_jobstatus='工勤'";
		}else if ("LTX".equals(statisticType)) {
			cndSql = " bean.sddscp_jobstatus='离退休党员'";
		}else if ("ALL".equals(statisticType)){
			//查询所有，不做任何操作
		}
		
		if (departId!=null && departId>-1) {
			cndSql += " and bean.sddscp_isperororg='1' and bean.sddscp_isinjob='1' and bean.user_id in(" +
					"select u.user_id from jc_user u where " +
					"u.sddscp_jgdwname in (select dp.depart_name from jc_department dp start with dp.depart_id="+departId+" connect by prior dp.depart_id=dp.parent_id) or "+
					"u.sddscp_dzzname in (select dp.depart_name from jc_department dp start with dp.depart_id="+departId+" connect by prior dp.depart_id=dp.parent_id) or "+
					"u.sddscp_zbname in (select dp.depart_name from jc_department dp start with dp.depart_id="+departId+" connect by prior dp.depart_id=dp.parent_id))";
		}else{
			cndSql += " and bean.sddscp_isperororg='1' ";
			if("LTX".equals(statisticType)){
				cndSql += " and bean.sddscp_isinjob='0'";
			}else{
				cndSql += " and bean.sddscp_isinjob='1' and bean.sddscp_isvalid='1'";
			}
		}
		
		String sql = "select count(bean.user_id) as countAll from jc_user bean where "+cndSql;
		List list = this.getSession().createSQLQuery(sql).addScalar("countAll", StandardBasicTypes.INTEGER).list();
		return list;
	}
	
	public List<Object[]> dutiesList(String statisticType, Integer departId) {
		String cndSql = "";
		if ("DZJG".equals(statisticType))  {
			cndSql = " and bean.sddscp_jobstatus='公务员' and bean.sddscp_isvalid='1'";
		}else if ("SYDW".equals(statisticType)) {
 			cndSql = " and bean.sddscp_jobstatus='事业编制人员' and bean.sddscp_isvalid='1' ";
		}else if ("GQ".equals(statisticType)) {
			cndSql = " and bean.sddscp_jobstatus='工勤' and bean.sddscp_isvalid='1' ";
		}else if ("LTX".equals(statisticType)) {
			cndSql = " and bean.sddscp_jobstatus='离退休党员' and bean.sddscp_isvalid='1'";
		}else if ("ALL".equals(statisticType)){
			//查询所有，不做任何操作
			cndSql = " and bean.sddscp_isvalid='1'";
		}
		if (departId!=null && departId>-1){
			cndSql += " and bean.sddscp_isperororg='1' and bean.sddscp_isinjob='1' and bean.user_id in(" +
					"select u.user_id from jc_user u where " +
					"u.sddscp_jgdwname in (select dp.depart_name from jc_department dp start with dp.depart_id="+departId+" connect by prior dp.depart_id=dp.parent_id) or "+
					"u.sddscp_dzzname in (select dp.depart_name from jc_department dp start with dp.depart_id="+departId+" connect by prior dp.depart_id=dp.parent_id) or "+
					"u.sddscp_zbname in (select dp.depart_name from jc_department dp start with dp.depart_id="+departId+" connect by prior dp.depart_id=dp.parent_id) or "+
					"u.depart_id in (select dp.depart_id from jc_department dp start with dp.depart_id="+departId+" connect by prior dp.depart_id=dp.parent_id))";
		}else{
			cndSql += " and bean.sddscp_isperororg='1' ";
			if("LTX".equals(statisticType)){
				cndSql += " and bean.sddscp_isinjob='0'";
			}else{
				cndSql += " and bean.sddscp_isinjob='1' and bean.sddscp_isvalid='1'";
			}
		}
		String sql = "select (select count(bean.user_id) from jc_user bean inner join jc_department dp on bean.sddscp_jgdw=dp.depart_id where dp.sddspo_orglevel=4 and dp.sddspo_orgtype='机关党委'"+cndSql+" and (bean.sddscp_jgdwjob='书记'  and  bean.sddscp_jgdw is not null)) as duties1 ,"+
				" (select count(bean.user_id) from jc_user bean inner join jc_department dp on bean.sddscp_jgdw=dp.depart_id where dp.sddspo_orglevel=3 and dp.sddspo_orgtype='机关党委'"+cndSql+" and (bean.sddscp_jgdwjob='书记'  and  bean.sddscp_jgdw is not null)) as duties2 ,"+
				" (select count(bean.user_id) from jc_user bean inner join jc_department dp on bean.sddscp_jgdw=dp.depart_id where dp.sddspo_orglevel=1 and dp.sddspo_orgtype in('机关党委')"+cndSql+" and (bean.sddscp_jgdwjob='书记'  and  bean.sddscp_jgdw is not null)) as duties3 ,"+
				" (select count(bean.user_id) from jc_user bean inner join jc_department dp on bean.sddscp_jgdw=dp.depart_id where dp.sddspo_orglevel=2 and dp.sddspo_orgtype='机关党委' "+cndSql+" and (bean.sddscp_jgdwjob='书记'  and  bean.sddscp_jgdw is not null)) as duties4 ,"+
				
				" (select count(bean.user_id) from jc_user bean inner join jc_department dp on bean.sddscp_jgdw =dp.depart_id where dp.sddspo_orglevel=4 and dp.sddspo_orgtype='机关党委'"+cndSql+" and (bean.sddscp_jgdwjob like '%专职副书记'  and  bean.sddscp_jgdw is not null)) as duties5 ,"+
				" (select count(bean.user_id) from jc_user bean inner join jc_department dp on bean.sddscp_jgdw =dp.depart_id where dp.sddspo_orglevel=3 and dp.sddspo_orgtype='机关党委'"+cndSql+" and (bean.sddscp_jgdwjob like '%专职副书记'  and  bean.sddscp_jgdw is not null)) as duties6 ,"+
				" (select count(bean.user_id) from jc_user bean inner join jc_department dp on bean.sddscp_jgdw =dp.depart_id where dp.sddspo_orglevel=1 and dp.sddspo_orgtype in('机关党委')"+cndSql+" and (bean.sddscp_jgdwjob like '%专职副书记'  and  bean.sddscp_jgdw is not null)) as duties7 ,"+
				" (select count(bean.user_id) from jc_user bean inner join jc_department dp on bean.sddscp_jgdw =dp.depart_id where dp.sddspo_orglevel=2 and dp.sddspo_orgtype in('机关党委')"+cndSql+" and (bean.sddscp_jgdwjob like '%专职副书记'  and  bean.sddscp_jgdw is not null)) as duties8 ,"+
				
				" (select count(bean.user_id) from jc_user bean inner join jc_department dp on bean.sddscp_jgdw=dp.depart_id where dp.sddspo_orglevel=4 and dp.sddspo_orgtype='机关党委'"+cndSql+" and (bean.sddscp_jgdwjob='副书记'  and  bean.sddscp_jgdw is not null)) as duties9 ,"+
				" (select count(bean.user_id) from jc_user bean inner join jc_department dp on bean.sddscp_jgdw=dp.depart_id where dp.sddspo_orglevel=3 and dp.sddspo_orgtype='机关党委'"+cndSql+" and (bean.sddscp_jgdwjob='副书记'  and  bean.sddscp_jgdw is not null)) as duties10 ,"+
				" (select count(bean.user_id) from jc_user bean inner join jc_department dp on bean.sddscp_jgdw=dp.depart_id where dp.sddspo_orglevel=1 and dp.sddspo_orgtype in('机关党委')"+cndSql+" and (bean.sddscp_jgdwjob='副书记'  and  bean.sddscp_jgdw is not null)) as duties11 ,"+
				" (select count(bean.user_id) from jc_user bean inner join jc_department dp on bean.sddscp_jgdw=dp.depart_id where dp.sddspo_orglevel=2 and dp.sddspo_orgtype='机关党委'"+cndSql+" and (bean.sddscp_jgdwjob='副书记'  and  bean.sddscp_jgdw is not null)) as duties12 ,"+
				
				" (select count(bean.user_id) from jc_user bean inner join jc_department dp on bean.SDDSCP_DZZ=dp.depart_id where dp.sddspo_orglevel=4 and dp.sddspo_orgtype='党总支'"+cndSql+" and (bean.SDDSCP_DZZJOB='书记'  and  bean.SDDSCP_DZZ is not null)) as duties13 ,"+
				" (select count(bean.user_id) from jc_user bean inner join jc_department dp on bean.SDDSCP_DZZ=dp.depart_id where dp.sddspo_orglevel=3 and dp.sddspo_orgtype='党总支'"+cndSql+" and (bean.SDDSCP_DZZJOB='书记'  and  bean.SDDSCP_DZZ is not null)) as duties14 ,"+
				" (select count(bean.user_id) from jc_user bean inner join jc_department dp on bean.SDDSCP_DZZ=dp.depart_id where dp.sddspo_orglevel=1 and dp.sddspo_orgtype in('党总支')"+cndSql+" and (bean.SDDSCP_DZZJOB='书记'  and  bean.SDDSCP_DZZ is not null)) as duties15 ,"+
				" (select count(bean.user_id) from jc_user bean inner join jc_department dp on bean.SDDSCP_DZZ=dp.depart_id where dp.sddspo_orglevel=2 and dp.sddspo_orgtype='党总支' "+cndSql+" and (bean.SDDSCP_DZZJOB='书记'  and  bean.SDDSCP_DZZ is not null)) as duties16 ,"+

				" (select count(bean.user_id) from jc_user bean inner join jc_department dp on bean.SDDSCP_DZZ=dp.depart_id where dp.sddspo_orglevel=4 and dp.sddspo_orgtype='党总支'"+cndSql+" and (bean.SDDSCP_DZZJOB like '%专职副书记'  and  bean.SDDSCP_DZZ is not null)) as duties17,"+
				" (select count(bean.user_id) from jc_user bean inner join jc_department dp on bean.SDDSCP_DZZ=dp.depart_id where dp.sddspo_orglevel=3 and dp.sddspo_orgtype='党总支'"+cndSql+" and (bean.SDDSCP_DZZJOB like '%专职副书记'  and  bean.SDDSCP_DZZ is not null)) as duties18 ,"+
				" (select count(bean.user_id) from jc_user bean inner join jc_department dp on bean.SDDSCP_DZZ=dp.depart_id where dp.sddspo_orglevel=1 and dp.sddspo_orgtype in('党总支')"+cndSql+" and (bean.SDDSCP_DZZJOB like '%专职副书记'  and  bean.SDDSCP_DZZ is not null)) as duties19 ,"+
				" (select count(bean.user_id) from jc_user bean inner join jc_department dp on bean.SDDSCP_DZZ=dp.depart_id where dp.sddspo_orglevel=2 and dp.sddspo_orgtype in('党总支')"+cndSql+" and (bean.SDDSCP_DZZJOB like '%专职副书记'  and  bean.SDDSCP_DZZ is not null)) as duties20 ,"+
				
				" (select count(bean.user_id) from jc_user bean inner join jc_department dp on bean.SDDSCP_DZZ=dp.depart_id where dp.sddspo_orglevel=4 and dp.sddspo_orgtype='党总支'"+cndSql+" and (bean.SDDSCP_DZZJOB='副书记'  and  bean.SDDSCP_DZZ is not null)) as duties21 ,"+
				" (select count(bean.user_id) from jc_user bean inner join jc_department dp on bean.SDDSCP_DZZ=dp.depart_id where dp.sddspo_orglevel=3 and dp.sddspo_orgtype='党总支'"+cndSql+" and (bean.SDDSCP_DZZJOB='副书记'  and  bean.SDDSCP_DZZ is not null)) as duties22 ,"+
				" (select count(bean.user_id) from jc_user bean inner join jc_department dp on bean.SDDSCP_DZZ=dp.depart_id where dp.sddspo_orglevel=1 and dp.sddspo_orgtype in('党总支')"+cndSql+" and (bean.SDDSCP_DZZJOB='副书记'  and  bean.SDDSCP_DZZ is not null)) as duties23 ,"+
				" (select count(bean.user_id) from jc_user bean inner join jc_department dp on bean.SDDSCP_DZZ=dp.depart_id where dp.sddspo_orglevel=2 and dp.sddspo_orgtype='党总支'"+cndSql+" and (bean.SDDSCP_DZZJOB='副书记'  and  bean.SDDSCP_DZZ is not null)) as duties24 ,"+

				" (select count(bean.user_id) from jc_user bean inner join jc_department dp on bean.SDDSCP_ZB=dp.depart_id where dp.sddspo_orglevel=4 and dp.sddspo_orgtype in('支部')"+cndSql+" and (bean.SDDSCP_ZBJOB='书记'  and  bean.SDDSCP_ZB is not null)) as duties25 ,"+
				" (select count(bean.user_id) from jc_user bean inner join jc_department dp on bean.SDDSCP_ZB=dp.depart_id where dp.sddspo_orglevel=3 and dp.sddspo_orgtype='支部'"+cndSql+" and (bean.SDDSCP_ZBJOB='书记'  and  bean.SDDSCP_ZB is not null)) as duties26 ,"+
				" (select count(bean.user_id) from jc_user bean inner join jc_department dp on bean.SDDSCP_ZB=dp.depart_id where dp.sddspo_orglevel=1 and dp.sddspo_orgtype in('支部')"+cndSql+" and (bean.SDDSCP_ZBJOB='书记'  and  bean.SDDSCP_ZB is not null)) as duties27 ,"+
				" (select count(bean.user_id) from jc_user bean inner join jc_department dp on bean.SDDSCP_ZB=dp.depart_id where dp.sddspo_orglevel=2 and dp.sddspo_orgtype='支部'"+cndSql+" and (bean.SDDSCP_ZBJOB='书记'  and  bean.SDDSCP_ZB is not null)) as duties28,"+
				
				" (select count(bean.user_id) from jc_user bean inner join jc_department dp on bean.SDDSCP_ZB=dp.depart_id where dp.sddspo_orglevel=4 and dp.sddspo_orgtype in('支部')"+cndSql+" and (bean.SDDSCP_ZBJOB='副书记'  and  bean.SDDSCP_ZB is not null)) as duties29 ,"+
				" (select count(bean.user_id) from jc_user bean inner join jc_department dp on bean.SDDSCP_ZB=dp.depart_id where dp.sddspo_orglevel=3 and dp.sddspo_orgtype='支部'"+cndSql+" and (bean.SDDSCP_ZBJOB='副书记'  and  bean.SDDSCP_ZB is not null)) as duties30 ,"+
				" (select count(bean.user_id) from jc_user bean inner join jc_department dp on bean.SDDSCP_ZB=dp.depart_id where dp.sddspo_orglevel=1 and dp.sddspo_orgtype in('支部')"+cndSql+" and (bean.SDDSCP_ZBJOB='副书记'  and  bean.SDDSCP_ZB is not null)) as duties31 ,"+
				" (select count(bean.user_id) from jc_user bean inner join jc_department dp on bean.SDDSCP_ZB=dp.depart_id where dp.sddspo_orglevel=2 and dp.sddspo_orgtype='支部'"+cndSql+" and (bean.SDDSCP_ZBJOB='副书记'  and  bean.SDDSCP_ZB is not null)) as duties32 ,"+
				
				" (select count(bean.user_id) from jc_user bean inner join jc_department dp on bean.depart_id=dp.depart_id where dp.sddspo_orglevel=4"+cndSql+" and (bean.sddscp_jgdwjob like '%其他委员' or bean.sddscp_dzzjob like '%其他委员' or sddscp_zbjob like '%其他委员')) as duties33 ,"+
				" (select count(bean.user_id) from jc_user bean inner join jc_department dp on bean.depart_id=dp.depart_id where dp.sddspo_orglevel=3"+cndSql+" and (bean.sddscp_jgdwjob like '%其他委员' or bean.sddscp_dzzjob like '%其他委员' or sddscp_zbjob like '%其他委员')) as duties34 ,"+
				" (select count(bean.user_id) from jc_user bean inner join jc_department dp on bean.depart_id=dp.depart_id where dp.sddspo_orglevel=1"+cndSql+" and (bean.sddscp_jgdwjob like '%其他委员' or bean.sddscp_dzzjob like '%其他委员' or sddscp_zbjob like '%其他委员')) as duties35 ,"+
				" (select count(bean.user_id) from jc_user bean inner join jc_department dp on bean.depart_id=dp.depart_id where dp.sddspo_orglevel=2"+cndSql+" and (bean.sddscp_jgdwjob like '%其他委员' or bean.sddscp_dzzjob like '%其他委员' or sddscp_zbjob like '%其他委员')) as duties36 , "+

				" (select count(bean.user_id) from jc_user bean inner join jc_department dp on bean.depart_id=dp.depart_id where dp.sddspo_orglevel=4"+cndSql+" and (bean.sddscp_jgdwjob like '%纪检委员' or bean.sddscp_dzzjob like '%纪检委员' or sddscp_zbjob like '%纪检委员')) as duties37 ,"+
				" (select count(bean.user_id) from jc_user bean inner join jc_department dp on bean.depart_id=dp.depart_id where dp.sddspo_orglevel=3"+cndSql+" and (bean.sddscp_jgdwjob like '%纪检委员' or bean.sddscp_dzzjob like '%纪检委员' or sddscp_zbjob like '%纪检委员')) as duties38 ,"+
				" (select count(bean.user_id) from jc_user bean inner join jc_department dp on bean.depart_id=dp.depart_id where dp.sddspo_orglevel=1"+cndSql+" and (bean.sddscp_jgdwjob like '%纪检委员' or bean.sddscp_dzzjob like '%纪检委员' or sddscp_zbjob like '%纪检委员')) as duties39 ,"+
				" (select count(bean.user_id) from jc_user bean inner join jc_department dp on bean.depart_id=dp.depart_id where dp.sddspo_orglevel=2"+cndSql+" and (bean.sddscp_jgdwjob like '%纪检委员' or bean.sddscp_dzzjob like '%纪检委员' or sddscp_zbjob like '%纪检委员')) as duties40 , "+

				" (select count(bean.user_id) from jc_user bean inner join jc_department dp on bean.sddscp_jgdw =dp.depart_id where dp.sddspo_orglevel=4 and dp.sddspo_orgtype='机关党委'"+cndSql+" and (bean.sddscp_jgdwjob='机关纪委书记'  and  bean.sddscp_jgdw is not null)) as duties41,"+
				" (select count(bean.user_id) from jc_user bean inner join jc_department dp on bean.sddscp_jgdw =dp.depart_id where dp.sddspo_orglevel=3 and dp.sddspo_orgtype='机关党委'"+cndSql+" and (bean.sddscp_jgdwjob='机关纪委书记'  and  bean.sddscp_jgdw is not null)) as duties42,"+
				" (select count(bean.user_id) from jc_user bean inner join jc_department dp on bean.sddscp_jgdw =dp.depart_id where dp.sddspo_orglevel=1 and dp.sddspo_orgtype in('机关党委')"+cndSql+" and (bean.sddscp_jgdwjob='机关纪委书记'  and  bean.sddscp_jgdw is not null)) as duties43,"+
				" (select count(bean.user_id) from jc_user bean inner join jc_department dp on bean.sddscp_jgdw =dp.depart_id where dp.sddspo_orglevel=2 and dp.sddspo_orgtype in('机关党委')"+cndSql+" and (bean.sddscp_jgdwjob='机关纪委书记'  and  bean.sddscp_jgdw is not null)) as duties44 from dual";

				
		List list = getSession().createSQLQuery(sql)
				.addScalar("duties1", StandardBasicTypes.INTEGER)
				.addScalar("duties2", StandardBasicTypes.INTEGER)
				.addScalar("duties3", StandardBasicTypes.INTEGER)
				.addScalar("duties4", StandardBasicTypes.INTEGER)
				.addScalar("duties5", StandardBasicTypes.INTEGER)
				.addScalar("duties6", StandardBasicTypes.INTEGER)
				.addScalar("duties7", StandardBasicTypes.INTEGER)
				.addScalar("duties8", StandardBasicTypes.INTEGER)
				.addScalar("duties9", StandardBasicTypes.INTEGER)
				.addScalar("duties10", StandardBasicTypes.INTEGER)
				.addScalar("duties11", StandardBasicTypes.INTEGER)
				.addScalar("duties12", StandardBasicTypes.INTEGER)
				.addScalar("duties13", StandardBasicTypes.INTEGER)
				.addScalar("duties14", StandardBasicTypes.INTEGER)
				.addScalar("duties15", StandardBasicTypes.INTEGER)
				.addScalar("duties16", StandardBasicTypes.INTEGER)
				.addScalar("duties17", StandardBasicTypes.INTEGER)
				.addScalar("duties18", StandardBasicTypes.INTEGER)
				.addScalar("duties19", StandardBasicTypes.INTEGER)
				.addScalar("duties20", StandardBasicTypes.INTEGER)
				.addScalar("duties21", StandardBasicTypes.INTEGER)
				.addScalar("duties22", StandardBasicTypes.INTEGER)
				.addScalar("duties23", StandardBasicTypes.INTEGER)
				.addScalar("duties24", StandardBasicTypes.INTEGER)
				.addScalar("duties25", StandardBasicTypes.INTEGER)
				.addScalar("duties26", StandardBasicTypes.INTEGER)
				.addScalar("duties27", StandardBasicTypes.INTEGER)
				.addScalar("duties28", StandardBasicTypes.INTEGER)
				.addScalar("duties29", StandardBasicTypes.INTEGER)
				.addScalar("duties30", StandardBasicTypes.INTEGER)
				.addScalar("duties31", StandardBasicTypes.INTEGER)
				.addScalar("duties32", StandardBasicTypes.INTEGER)
				.addScalar("duties33", StandardBasicTypes.INTEGER)
				.addScalar("duties34", StandardBasicTypes.INTEGER)
				.addScalar("duties35", StandardBasicTypes.INTEGER)
				.addScalar("duties36", StandardBasicTypes.INTEGER)
				.addScalar("duties37", StandardBasicTypes.INTEGER)
				.addScalar("duties38", StandardBasicTypes.INTEGER)
				.addScalar("duties39", StandardBasicTypes.INTEGER)
				.addScalar("duties40", StandardBasicTypes.INTEGER)
				.addScalar("duties41", StandardBasicTypes.INTEGER)
				.addScalar("duties42", StandardBasicTypes.INTEGER)
				.addScalar("duties43", StandardBasicTypes.INTEGER)
				.addScalar("duties44", StandardBasicTypes.INTEGER).list();
		return list;
	}
	
	private Finder createCacheableFinder(String hql) {
		Finder finder = Finder.create(hql);
		finder.setCacheable(true);
		return finder;
	}
	
	@Autowired
	private CmsDepartmentMng cmsDepartmentMng;

	@Override
	public Integer getOutOrStopPartyUserNum(Integer departId,
			String decreaseType) {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder("select count(bean.user_id) from jc_user bean where 1=1 ");
		if(!StringUtils.isBlank(decreaseType)){
			// 注意：出党这个概念，是党员自己主动退党的才叫 出党，如果是违犯的法律或纪律导致的被开除党籍，叫“开除党籍。”
			// ”出党“ 这个词在前台可以展示出来，但是里面永远不可能有数据，数据是0才对，因为没有哪名党员会主动 的退党。
			// 即出党 等于主动退党的，不是被开除的。
			if("出党".equals(decreaseType)){
				sb.append(" and (bean.sddscp_isinjob = '2' and bean.sddscp_Changes in('7'))");
			}
			
			// 停止党籍 可以理解为 是被开除了党籍  具体名称的命名还得根据年报表的为准。
			if("停止党籍".equals(decreaseType)){
				sb.append(" and (bean.sddscp_isinjob = '2' and bean.sddscp_Changes in('5'))");
			}
			if("死亡".equals(decreaseType)){
				sb.append(" and (bean.sddscp_isinjob = '2' and bean.sddscp_Changes='9')");
			}
		}
		if (departId!=null && departId>-1) {
			sb.append( " and bean.sddscp_isperororg='1' and bean.user_id in(" +
					"select u.user_id from jc_user u where " +
					"u.depart_id in (select dp.depart_id from jc_department dp start with dp.depart_id="+departId+" connect by prior dp.depart_id=dp.parent_id) or "+
					"u.sddscp_jgdwname in (select dp.depart_name from jc_department dp start with dp.depart_id="+departId+" connect by prior dp.depart_id=dp.parent_id) or "+
					"u.sddscp_dzzname in (select dp.depart_name from jc_department dp start with dp.depart_id="+departId+" connect by prior dp.depart_id=dp.parent_id) or "+
					"u.sddscp_zbname in (select dp.depart_name from jc_department dp start with dp.depart_id="+departId+" connect by prior dp.depart_id=dp.parent_id))");
		}
		SQLQuery query = getSession().createSQLQuery(sb.toString());
		Integer sum = new Integer(query.list().iterator().next().toString());
		return sum;
	}

	@Override
	public List<Object[]> getAllMembersNotLeave() {
		// TODO Auto-generated method stub
		String sql = "select ";
		String beforeLastYearSumSql = "(select count(*) from jc_user where to_char(register_time,'yyyy') >= (to_char(sysdate,'yyyy')-2) and to_char(register_time,'yyyy') < (to_char(sysdate,'yyyy')-1) and sddscp_isperororg = '1'and (sddscp_isinjob != '2' and sddscp_isvalid!=0)) as beforeLastYearSum,";
		String lastYearSumSql = "(select count(*) from jc_user where to_char(register_time,'yyyy') >= (to_char(sysdate,'yyyy')-2) and to_char(register_time,'yyyy') < (to_char(sysdate,'yyyy')) and sddscp_isperororg = '1'and (sddscp_isinjob != '2' and sddscp_isvalid!=0)) as lastYearSum ,";
		String nowYearSumSql = "(select count(*) from jc_user where to_char(register_time,'yyyy') >= (to_char(sysdate,'yyyy')-2) and to_char(register_time,'yyyy') < (to_char(sysdate,'yyyy')+1) and sddscp_isperororg = '1' and (sddscp_isinjob != '2' and sddscp_isvalid!=0)) as nowYearSum ,";
		String nextYearSumSql = "(select count(*) from jc_user where to_char(register_time,'yyyy') >= (to_char(sysdate,'yyyy')-2) and to_char(register_time,'yyyy') < (to_char(sysdate,'yyyy')+2) and sddscp_isperororg = '1' and (sddscp_isinjob != '2' and sddscp_isvalid!=0)) as nextYearSum ,";
		String afterNextYearSum = "(select count(*) from jc_user where to_char(register_time,'yyyy') >= (to_char(sysdate,'yyyy')-2) and to_char(register_time,'yyyy') < (to_char(sysdate,'yyyy')+3) and sddscp_isperororg = '1' and (sddscp_isinjob != '2' and sddscp_isvalid!=0)) as afterNextYearSum";
		String endSql = " from jc_user where rownum =1 ";
		String fullSql = sql.concat(beforeLastYearSumSql).concat(lastYearSumSql).concat(nowYearSumSql).concat(nextYearSumSql).concat(afterNextYearSum).concat(endSql);
		SQLQuery query = getSession().createSQLQuery(fullSql);
		return query.list();
	}

}