package com.risen.action;

import static com.jeecms.common.page.SimplePage.cpn;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import antlr.StringUtils;

import com.jeecms.common.page.Pagination;
import com.jeecms.common.web.CookieUtils;
import com.jeecms.core.entity.CmsSite;
import com.jeecms.core.entity.CmsUser;
import com.jeecms.core.web.WebErrors;
import com.jeecms.core.web.util.CmsUtils;
import com.risen.entity.RisenQuota;
import com.risen.entity.RisenQuota;
import com.risen.entity.RisenScore;
import com.risen.service.IRisenQuotaService;
import com.risen.service.IRisenScoreService;

@Controller
public class RisenQuotaAct {
	private static final Logger log = LoggerFactory.getLogger(RisenQuotaAct.class);
	
	@RequiresPermissions("risenQuota:v_list")
	@RequestMapping("/risenQuota/v_list.do")
	public String list(Integer pageNo, HttpServletRequest request, ModelMap model,String risenType) {
		CmsUser user = CmsUtils.getUser(request);
		Integer parentId = user.getDepartment().getId();
		try{
			Pagination pagination = manager.getPage(cpn(pageNo), CookieUtils.getPageSize(request),parentId,risenType);
			model.addAttribute("pagination",pagination);
			model.addAttribute("pageNo",pagination.getPageNo());
			model.addAttribute("risenType", risenType);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return "quota/list";
	}
	
	@RequiresPermissions("risenQuota:v_add")
	@RequestMapping("/risenQuota/v_add.do")
	public String add(ModelMap model,HttpServletRequest request) {
		try{
			String risenType = request.getParameter("risenType");
			model.addAttribute("risenType", risenType);
		}catch(Exception e){
			e.printStackTrace();
		}
		return "quota/add";
	}
	
	@RequiresPermissions("risenQuota:v_edit")
	@RequestMapping("/risenQuota/v_edit.do")
	public String edit(Integer id, Integer pageNo, HttpServletRequest request, ModelMap model) {
		WebErrors errors = validateEdit(id, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		String risenType=request.getParameter("risenqtType");
		String risenAddType=request.getParameter("risenAddType");
		model.addAttribute("risenQuota", manager.findById(id));
		model.addAttribute("pageNo",pageNo);
		model.addAttribute("risenType",risenType);
		model.addAttribute("risenAddType",risenAddType);
		return "quota/edit";
	}
	
	@RequiresPermissions("risenQuota:o_save")
	@RequestMapping("/risenQuota/o_save.do")
	public String save(RisenQuota bean, HttpServletRequest request, ModelMap model) {
		WebErrors errors = validateSave(bean, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		String risenType = request.getParameter("risenType");
		bean.setRisenqtType(new Integer(risenType));
		CmsUser user = CmsUtils.getUser(request);
		Integer parentId = user.getDepartment().getId();
		bean.setQuotaDepartId(parentId);
		bean.setQuotaDepartName(user.getDepartment().getName());
		try{
			manager.save(bean);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		log.info("save RisenQuota id={}", bean.getId());
		return list(1, request, model,risenType);
	}
	
	@RequiresPermissions("risenQuota:o_update")
	@RequestMapping("/risenQuota/o_update.do")
	public String update(RisenQuota bean, Integer pageNo, HttpServletRequest request,
			ModelMap model) {
		WebErrors errors = validateSave(bean, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		String type = request.getParameter("risenType");
		try{
			bean = manager.update(bean);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		log.info("update RisenQuota id={}.", bean.getId());
		return list(pageNo, request, model,type);
	}
	
	@RequiresPermissions("risenQuota:exist")
	@RequestMapping("/risenQuota/exist.do")
	public void existQuota(HttpServletRequest request,
			HttpServletResponse response){
		String name = request.getParameter("quotaName");
		String risenType = request.getParameter("risenType");
		String risenAddType = request.getParameter("risenAddType");
		if(("undefined".equals(risenAddType)) || ("null".equals(risenAddType))){
			risenAddType = "";
		}
		CmsUser user = CmsUtils.getUser(request);
		Integer parentId = user.getDepartment().getId();
		boolean result = manager.existQuota(name, parentId,risenType,risenAddType);
		try {
			if(result){
				response.getWriter().write("exists");
			}else{
				response.getWriter().write("noexists");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@RequiresPermissions("risenQuota:o_delete")
	@RequestMapping("/risenQuota/o_delete.do")
	public String delete(Integer[] ids, Integer pageNo, HttpServletRequest request,
			ModelMap model) {
		WebErrors errors = validateDelete(ids, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		Integer risenType = null;
		for(Integer id : ids){
			Set<RisenScore> scores = manager.findById(id).getRisenScores();
			if(scores!=null && (scores.size()>0)){
				for (RisenScore risenScore : scores) {
					scoreManager.delete(risenScore.getId());
				}
			}
			risenType = manager.findById(id).getRisenqtType();
			
			manager.delete(id);
			log.info("delete RisenQuota id={}", id);
			
		}
		return list(pageNo, request, model,String.valueOf(risenType));
	}
	
	private WebErrors validateSave(RisenQuota bean, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		CmsSite site = CmsUtils.getSite(request);
//		bean.setSite(site);
		return errors;
	}
	
	private WebErrors validateEdit(Integer id, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		CmsSite site = CmsUtils.getSite(request);
		if (vldExist(id, site.getId(), errors)) {
			return errors;
		}
		return errors;
	}

	private WebErrors validateUpdate(Integer id, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		CmsSite site = CmsUtils.getSite(request);
		if (vldExist(id, site.getId(), errors)) {
			return errors;
		}
		return errors;
	}

	private WebErrors validateDelete(Integer[] ids, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		CmsSite site = CmsUtils.getSite(request);
		if (errors.ifEmpty(ids, "ids")) {
			return errors;
		}
		for (Integer id : ids) {
			vldExist(id, site.getId(), errors);
		}
		return errors;
	}

	private boolean vldExist(Integer id, Integer siteId, WebErrors errors) {
		 
		return false;
	}
	@Autowired
	private IRisenQuotaService manager;
	@Autowired
	private IRisenScoreService scoreManager;
}
