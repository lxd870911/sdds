package com.jeecms.cms.action.admin.main;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;

import com.jeecms.cms.cache.DepartmentCache;
import com.jeecms.cms.entity.main.Channel;
import com.jeecms.cms.manager.assist.CmsWebserviceMng;
import com.jeecms.cms.manager.main.ChannelMng;
import com.jeecms.common.web.RequestUtils;
import com.jeecms.common.web.ResponseUtils;
import com.jeecms.core.entity.CmsDepartment;
import com.jeecms.core.entity.CmsUser;
import com.jeecms.core.manager.CmsDepartmentMng;
import com.jeecms.core.manager.CmsGroupMng;
import com.jeecms.core.manager.CmsLogMng;
import com.jeecms.core.manager.CmsRoleMng;
import com.jeecms.core.manager.CmsSiteMng;
import com.jeecms.core.manager.CmsUserMng;
import com.jeecms.core.security.CmsAuthorizingRealm;

public class CmsAdminAbstract {
	protected String channelsAddJson(Integer siteId,
			HttpServletRequest request, HttpServletResponse response,
			ModelMap model) {
		List<Channel> channelList = channelMng.getTopList(siteId, false);
		model.addAttribute("channelList", channelList);
		response.setHeader("Cache-Control", "no-cache");
		response.setContentType("text/json;charset=UTF-8");
		return "admin/channels_add";
	}

	protected String channelsEditJson(Integer userId, Integer siteId,
			HttpServletRequest request, HttpServletResponse response,
			ModelMap model) {
		List<Channel> channelList = channelMng.getTopList(siteId, false);
		CmsUser user = manager.findById(userId);
		model.addAttribute("channelList", channelList);
		model.addAttribute("channelIds", user.getChannelIds(siteId));
		response.setHeader("Cache-Control", "no-cache");
		response.setContentType("text/json;charset=UTF-8");
		return "admin/channels_edit";
	}
	
	
	public String departAdd(String root, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		boolean isRoot;
		// jquery treeview的根请求为root=source
		if (StringUtils.isBlank(root) || "source".equals(root)) {
			isRoot = true;
		} else {
			isRoot = false;
		}
		model.addAttribute("isRoot", isRoot);
		List<CmsDepartment> departList = new ArrayList<CmsDepartment>();
		if(isRoot){
			departList = cmsDepartmentMng.getList(null,false);
		}else{
			/**/if(root.indexOf(",")>=0 && root.indexOf("source")>-1){
				String [] roots = root.split(",");
				root = roots[0];
			}else{
				String [] roots = root.split(",");
				if (roots !=null && roots.length>1) {
					root = roots[1];
				}else{
					root = roots[0];
				}
				
			}
			if(root.equals("0")||root.equals("")){
				departList = cmsDepartmentMng.getList(null,false);
			}else{
				departList = cmsDepartmentMng.getList(Integer.parseInt(root),false);
			}
		}
		model.addAttribute("list", departList);
		model.addAttribute("departId",root);
		response.setHeader("Cache-Control", "no-cache");
		response.setContentType("text/json;charset=UTF-8");
		return "admin/depart_tree";
	}

	/**
	 * 添加组织选择树
	 * @param request
	 * @param response
	 */
	public String newDepartAdd(String root, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		boolean isRoot;
		// jquery treeview的根请求为root=source
		if (StringUtils.isBlank(root) || "source".equals(root)) {
			isRoot = true;
		} else {
			isRoot = false;
		}
		model.addAttribute("isRoot", isRoot);
		List<CmsDepartment> departList = new ArrayList<CmsDepartment>();
		if(isRoot){
			departList= cmsDepartmentMng.getList(null,false);
		}else{
			departList = cmsDepartmentMng.getList(Integer.parseInt(root),false);
		}
		model.addAttribute("list", departList);
		response.setHeader("Cache-Control", "no-cache");
		response.setContentType("text/json;charset=UTF-8");
		return "admin/depart_add_tree";
	}
	protected void checkUserJson(HttpServletRequest request,HttpServletResponse response) {
		String username=RequestUtils.getQueryParam(request,"username");
		String pass;
		if (StringUtils.isBlank(username)) {
			pass = "false";
		} else {
			pass = manager.usernameNotExist(username) ? "true" : "false";
		}
		ResponseUtils.renderJson(response, pass);
	}

	protected void checkEmailJson(String email, HttpServletResponse response) {
		String pass;
		if (StringUtils.isBlank(email)) {
			pass = "false";
		} else {
			pass = manager.emailNotExist(email) ? "true" : "false";
		}
		ResponseUtils.renderJson(response, pass);
	}

	@Autowired
	protected CmsSiteMng cmsSiteMng;
	@Autowired
	protected ChannelMng channelMng;
	@Autowired
	protected CmsRoleMng cmsRoleMng;
	@Autowired
	protected CmsGroupMng cmsGroupMng;
	@Autowired
	protected CmsLogMng cmsLogMng;
	@Autowired
	protected CmsUserMng manager;
	@Autowired
	protected CmsDepartmentMng cmsDepartmentMng;
	@Autowired
	protected CmsWebserviceMng cmsWebserviceMng;
	@Autowired
	protected CmsAuthorizingRealm authorizingRealm;
}