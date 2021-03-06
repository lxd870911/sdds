package com.jeecms.core.entity.base;

import java.io.Serializable;
import java.util.Date;


/**
 * This is an object that contains data related to the jc_department table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="jc_department"
 */

public abstract class BaseCmsDepartment  implements Serializable {

	public static String REF = "CmsDepartment";
	public static String PROP_NAME = "name";
	public static String PROP_SITE = "site";
	public static String PROP_ID = "id";
	public static String PROP_WEIGHTS = "weights";
	public static String PROP_PRIORITY = "priority";


	// constructors
	public BaseCmsDepartment () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseCmsDepartment (java.lang.Integer id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseCmsDepartment (
		java.lang.Integer id,
		java.lang.String name,
		java.lang.Integer priority,
		java.lang.Integer weights) {

		this.setId(id);
		this.setName(name);
		this.setPriority(priority);
		this.setWeights(weights);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.Integer id;

	// fields
	private java.lang.String name;
	private java.lang.Integer priority;
	private java.lang.Integer weights;
	/**组织机构代码**/
	private java.lang.String sddspoOrgcode;

	/**党组织类型**/
	private java.lang.String sddspoOrgtype;

	/**党组织属地关系**/
	private java.lang.String sddspoRelations;

	/**通讯地址**/
	private java.lang.String sddspoOrgaddress;

	/**联系电话**/
	private java.lang.String sddspoPhone;
	
	/**是否成为联合党组织 **/
	private java.lang.Integer sddspoIsLianHe;

	/**批转建立党组织日期**/
	private java.util.Date sddspoJoinpartydate;

	/**邮政编码**/
	private java.lang.String sddspoZipcode;

	/**上级党组织代码**/
	private java.lang.String sddspoSuperorgcode;

	/**上级党组织名称**/
	private java.lang.String sddspoSuperorgname;

	/**党组织负责人UUID**/
	private java.lang.Integer sddspoLeaderid;

	/**党组织负责人名字**/
	private java.lang.String sddspoLeadername;

	/**党组织成分**/
	private java.lang.String sddspoComposition;

	/**组织地理坐标**/
	private java.lang.String sddspoXyindex;

	/**党组织类型（1.农村党组织 2.机关党组织）**/
	private java.lang.String sddspoPotype;

	/**账号是否激活 0_没激活 1_激活**/
	private java.lang.String sddspoIsjh;

	/**创建日期**/
	private java.util.Date sddspoCdate;

	/**修改日期**/
	private java.util.Date sddspoUdate;
	
	/**组织星级**/
	private java.lang.String sddspoStarlevel;

	/**备注**/
	private java.lang.String sddspoRemark;
	
	/**书记**/
	private java.lang.String sddspoSecretary;
	
	/**换届日期**/
	private java.util.Date sddspoChangelasttime;
	
	private String sddspoLogincode;
	//SDDSPO_ORGLEVEL
	private Integer sddspoOrglevel;
	//书记身份证
	private String sddspoSecretaryidc;
	//书记id
	private Integer sddspoSecretaryid;
	//积分
	private com.risen.entity.RisenIntegral risenintegral;
	private Date sddspoChangecdate;
	private String sddspoIp;  //IP
	/**-------------------临时字段 begin--------------------**/
	private String tempContentname;
	private String tempQd;
	private String overplus;	//换届剩余天数
	private Integer groupid;
	private Integer meberCount;//部门下的人数统计
	private Double score;		//当月总分
	private String sddscpUsertype; //党员类型
	private String key;
	private String value;
	private String isplus;		//判断是否是正数，正数即为正常距离换届天数， 负数即为超过换届天数
	private Double pid;            //绩效总分
	private String channelPath;
	private String sddscpAssess;
	private String sddscpChanges;
	private String sddscpIsinjob;
	
	/**-------------------临时字段 end--------------------**/
	// many to one
	private com.jeecms.core.entity.CmsSite site;
	private com.jeecms.core.entity.CmsDepartment parent;

	// collections
	private java.util.Set<com.jeecms.core.entity.CmsDepartment> child;
	private java.util.Set<com.jeecms.cms.entity.main.Channel> channels;
	private java.util.Set<com.jeecms.cms.entity.main.Channel> controlChannels;
	private java.util.Set<com.jeecms.cms.entity.assist.CmsGuestbookCtg> guestBookCtgs;



	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     *  generator-class="identity"
     *  column="depart_id"
     */
	public java.lang.Integer getId () {
		return id;
	}

	/**
	 * Set the unique identifier of this class
	 * @param id the new ID
	 */
	public void setId (java.lang.Integer id) {
		this.id = id;
		this.hashCode = Integer.MIN_VALUE;
	}




	/**
	 * Return the value associated with the column: depart_name
	 */
	public java.lang.String getName () {
		return name;
	}

	/**
	 * Set the value related to the column: depart_name
	 * @param name the depart_name value
	 */
	public void setName (java.lang.String name) {
		this.name = name;
	}


	/**
	 * Return the value associated with the column: priority
	 */
	public java.lang.Integer getPriority () {
		return priority;
	}

	/**
	 * Set the value related to the column: priority
	 * @param priority the priority value
	 */
	public void setPriority (java.lang.Integer priority) {
		this.priority = priority;
	}


	/**
	 * Return the value associated with the column: weights
	 */
	public java.lang.Integer getWeights () {
		return weights;
	}

	/**
	 * Set the value related to the column: weights
	 * @param weights the weights value
	 */
	public void setWeights (java.lang.Integer weights) {
		this.weights = weights;
	}


	/**
	 * Return the value associated with the column: site_id
	 */
	public com.jeecms.core.entity.CmsSite getSite () {
		return site;
	}

	/**
	 * Set the value related to the column: site_id
	 * @param site the site_id value
	 */
	public void setSite (com.jeecms.core.entity.CmsSite site) {
		this.site = site;
	}

	public com.jeecms.core.entity.CmsDepartment getParent() {
		return parent;
	}

	public void setParent(com.jeecms.core.entity.CmsDepartment parent) {
		this.parent = parent;
	}
	

	public static String getREF() {
		return REF;
	}

	public static void setREF(String ref) {
		REF = ref;
	}

	public static String getPROP_NAME() {
		return PROP_NAME;
	}

	public static void setPROP_NAME(String prop_name) {
		PROP_NAME = prop_name;
	}

	public static String getPROP_SITE() {
		return PROP_SITE;
	}

	public static void setPROP_SITE(String prop_site) {
		PROP_SITE = prop_site;
	}

	public static String getPROP_ID() {
		return PROP_ID;
	}

	public static void setPROP_ID(String prop_id) {
		PROP_ID = prop_id;
	}

	public static String getPROP_WEIGHTS() {
		return PROP_WEIGHTS;
	}

	public static void setPROP_WEIGHTS(String prop_weights) {
		PROP_WEIGHTS = prop_weights;
	}

	public static String getPROP_PRIORITY() {
		return PROP_PRIORITY;
	}

	public static void setPROP_PRIORITY(String prop_priority) {
		PROP_PRIORITY = prop_priority;
	}

	public int getHashCode() {
		return hashCode;
	}

	public void setHashCode(int hashCode) {
		this.hashCode = hashCode;
	}
	/**========================手动添加字段==========================**/
	
	public java.lang.String getSddspoOrgcode() {
		return sddspoOrgcode;
	}

	public void setSddspoOrgcode(java.lang.String sddspoOrgcode) {
		this.sddspoOrgcode = sddspoOrgcode;
	}

	public java.lang.String getSddspoOrgtype() {
		return sddspoOrgtype;
	}

	public void setSddspoOrgtype(java.lang.String sddspoOrgtype) {
		this.sddspoOrgtype = sddspoOrgtype;
	}

	public java.lang.String getSddspoRelations() {
		return sddspoRelations;
	}

	public void setSddspoRelations(java.lang.String sddspoRelations) {
		this.sddspoRelations = sddspoRelations;
	}

	public java.lang.String getSddspoOrgaddress() {
		return sddspoOrgaddress;
	}

	public void setSddspoOrgaddress(java.lang.String sddspoOrgaddress) {
		this.sddspoOrgaddress = sddspoOrgaddress;
	}

	public java.lang.String getSddspoPhone() {
		return sddspoPhone;
	}

	public void setSddspoPhone(java.lang.String sddspoPhone) {
		this.sddspoPhone = sddspoPhone;
	}

	public java.util.Date getSddspoJoinpartydate() {
		return sddspoJoinpartydate;
	}

	public void setSddspoJoinpartydate(java.util.Date sddspoJoinpartydate) {
		this.sddspoJoinpartydate = sddspoJoinpartydate;
	}

	public java.lang.String getSddspoZipcode() {
		return sddspoZipcode;
	}

	public void setSddspoZipcode(java.lang.String sddspoZipcode) {
		this.sddspoZipcode = sddspoZipcode;
	}

	public java.lang.String getSddspoSuperorgcode() {
		return sddspoSuperorgcode;
	}

	public void setSddspoSuperorgcode(java.lang.String sddspoSuperorgcode) {
		this.sddspoSuperorgcode = sddspoSuperorgcode;
	}

	public java.lang.String getSddspoSuperorgname() {
		return sddspoSuperorgname;
	}

	public void setSddspoSuperorgname(java.lang.String sddspoSuperorgname) {
		this.sddspoSuperorgname = sddspoSuperorgname;
	}

	public java.lang.Integer getSddspoLeaderid() {
		return sddspoLeaderid;
	}

	public void setSddspoLeaderid(java.lang.Integer sddspoLeaderid) {
		this.sddspoLeaderid = sddspoLeaderid;
	}

	public java.lang.String getSddspoLeadername() {
		return sddspoLeadername;
	}

	public void setSddspoLeadername(java.lang.String sddspoLeadername) {
		this.sddspoLeadername = sddspoLeadername;
	}

	public java.lang.String getSddspoComposition() {
		return sddspoComposition;
	}

	public void setSddspoComposition(java.lang.String sddspoComposition) {
		this.sddspoComposition = sddspoComposition;
	}

	public java.lang.String getSddspoXyindex() {
		return sddspoXyindex;
	}

	public void setSddspoXyindex(java.lang.String sddspoXyindex) {
		this.sddspoXyindex = sddspoXyindex;
	}

	public java.lang.String getSddspoPotype() {
		return sddspoPotype;
	}

	public void setSddspoPotype(java.lang.String sddspoPotype) {
		this.sddspoPotype = sddspoPotype;
	}

	public java.lang.String getSddspoIsjh() {
		return sddspoIsjh;
	}

	public void setSddspoIsjh(java.lang.String sddspoIsjh) {
		this.sddspoIsjh = sddspoIsjh;
	}

	public java.util.Date getSddspoCdate() {
		return sddspoCdate;
	}

	public void setSddspoCdate(java.util.Date sddspoCdate) {
		this.sddspoCdate = sddspoCdate;
	}

	public java.util.Date getSddspoUdate() {
		return sddspoUdate;
	}

	public void setSddspoUdate(java.util.Date sddspoUdate) {
		this.sddspoUdate = sddspoUdate;
	}

	public java.lang.String getSddspoStarlevel() {
		return sddspoStarlevel;
	}

	public void setSddspoStarlevel(java.lang.String sddspoStarlevel) {
		this.sddspoStarlevel = sddspoStarlevel;
	}

	public java.lang.String getSddspoRemark() {
		return sddspoRemark;
	}

	public void setSddspoRemark(java.lang.String sddspoRemark) {
		this.sddspoRemark = sddspoRemark;
	}

	public java.lang.String getSddspoSecretary() {
		return sddspoSecretary;
	}

	public void setSddspoSecretary(java.lang.String sddspoSecretary) {
		this.sddspoSecretary = sddspoSecretary;
	}
	public java.util.Date getSddspoChangelasttime() {
		return sddspoChangelasttime;
	}

	public void setSddspoChangelasttime(java.util.Date sddspoChangelasttime) {
		this.sddspoChangelasttime = sddspoChangelasttime;
	}

	public String getSddspoLogincode() {
		return sddspoLogincode;
	}

	public void setSddspoLogincode(String sddspoLogincode) {
		this.sddspoLogincode = sddspoLogincode;
	}

	// 是否成为联合党组织
	public java.lang.Integer getSddspoIsLianHe() {
		return sddspoIsLianHe;
	}

	public void setSddspoIsLianHe(Integer sddspoIsLianHe) {
		this.sddspoIsLianHe = sddspoIsLianHe;
	}

	/**========================手动添加结束==========================**/
	public java.util.Set<com.jeecms.core.entity.CmsDepartment> getChild() {
		return child;
	}

	public void setChild(
			java.util.Set<com.jeecms.core.entity.CmsDepartment> child) {
		this.child = child;
	}
	/**
	 * Return the value associated with the column: channels
	 */
	public java.util.Set<com.jeecms.cms.entity.main.Channel> getChannels () {
		return channels;
	}

	/**
	 * Set the value related to the column: channels
	 * @param channels the channels value
	 */
	public void setChannels (java.util.Set<com.jeecms.cms.entity.main.Channel> channels) {
		this.channels = channels;
	}

	public java.util.Set<com.jeecms.cms.entity.main.Channel> getControlChannels() {
		return controlChannels;
	}

	public void setControlChannels(
			java.util.Set<com.jeecms.cms.entity.main.Channel> controlChannels) {
		this.controlChannels = controlChannels;
	}

	/**
	 * Return the value associated with the column: guestBookCtgs
	 */
	public java.util.Set<com.jeecms.cms.entity.assist.CmsGuestbookCtg> getGuestBookCtgs () {
		return guestBookCtgs;
	}

	/**
	 * Set the value related to the column: guestBookCtgs
	 * @param guestBookCtgs the guestBookCtgs value
	 */
	public void setGuestBookCtgs (java.util.Set<com.jeecms.cms.entity.assist.CmsGuestbookCtg> guestBookCtgs) {
		this.guestBookCtgs = guestBookCtgs;
	}



	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof com.jeecms.core.entity.CmsDepartment)) return false;
		else {
			com.jeecms.core.entity.CmsDepartment cmsDepartment = (com.jeecms.core.entity.CmsDepartment) obj;
			if (null == this.getId() || null == cmsDepartment.getId()) return false;
			else return (this.getId().equals(cmsDepartment.getId()));
		}
	}

	public int hashCode () {
		if (Integer.MIN_VALUE == this.hashCode) {
			if (null == this.getId()) return super.hashCode();
			else {
				String hashStr = this.getClass().getName() + ":" + this.getId().hashCode();
				this.hashCode = hashStr.hashCode();
			}
		}
		return this.hashCode;
	}


	public String toString () {
		return super.toString();
	}

	public String getOverplus() {
		return overplus;
	}

	public void setOverplus(String overplus) {
		this.overplus = overplus;
	}

	public Integer getGroupid() {
		return groupid;
	}

	public void setGroupid(Integer groupid) {
		this.groupid = groupid;
	}

	public Integer getMeberCount() {
		return meberCount;
	}

	public void setMeberCount(Integer meberCount) {
		this.meberCount = meberCount;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}
	
	public String getSddscpUsertype() {
		return sddscpUsertype;
	}

	public void setSddscpUsertype(String sddscpUsertype) {
		this.sddscpUsertype = sddscpUsertype;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Integer getSddspoOrglevel() {
		return sddspoOrglevel;
	}

	public void setSddspoOrglevel(Integer sddspoOrglevel) {
		this.sddspoOrglevel = sddspoOrglevel;
	}

	public String getIsplus() {
		return isplus;
	}

	public void setIsplus(String isplus) {
		this.isplus = isplus;
	}

	public Double getPid() {
		return pid;
	}

	public void setPid(Double pid) {
		this.pid = pid;
	}
	public String getSddspoSecretaryidc() {
		return sddspoSecretaryidc;
	}

	public void setSddspoSecretaryidc(String sddspoSecretaryidc) {
		this.sddspoSecretaryidc = sddspoSecretaryidc;
	}

	public Integer getSddspoSecretaryid() {
		return sddspoSecretaryid;
	}

	public void setSddspoSecretaryid(Integer sddspoSecretaryid) {
		this.sddspoSecretaryid = sddspoSecretaryid;
	}

	public String getChannelPath() {
		return channelPath;
	}

	public void setChannelPath(String channelPath) {
		this.channelPath = channelPath;
	}

	public com.risen.entity.RisenIntegral getRisenintegral() {
		return risenintegral;
	}

	public void setRisenintegral(com.risen.entity.RisenIntegral risenintegral) {
		this.risenintegral = risenintegral;
	}

	public Date getSddspoChangecdate() {
		return sddspoChangecdate;
	}

	public void setSddspoChangecdate(Date sddspoChangecdate) {
		this.sddspoChangecdate = sddspoChangecdate;
	}

	public String getSddspoIp() {
		return sddspoIp;
	}

	public void setSddspoIp(String sddspoIp) {
		this.sddspoIp = sddspoIp;
	}

	public String getTempContentname() {
		return tempContentname;
	}

	public void setTempContentname(String tempContentname) {
		this.tempContentname = tempContentname;
	}

	public String getTempQd() {
		return tempQd;
	}

	public void setTempQd(String tempQd) {
		this.tempQd = tempQd;
	}

	public String getSddscpAssess() {
		return sddscpAssess;
	}

	public void setSddscpAssess(String sddscpAssess) {
		this.sddscpAssess = sddscpAssess;
	}

	public String getSddscpChanges() {
		return sddscpChanges;
	}

	public void setSddscpChanges(String sddscpChanges) {
		this.sddscpChanges = sddscpChanges;
	}

	public String getSddscpIsinjob() {
		return sddscpIsinjob;
	}

	public void setSddscpIsinjob(String sddscpIsinjob) {
		this.sddscpIsinjob = sddscpIsinjob;
	}

	
}