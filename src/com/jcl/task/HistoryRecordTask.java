package com.jcl.task;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.mongodb.morphia.Datastore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.jcl.dao.FundsetMapper;
import com.jcl.dao.SubzhMapper;
import com.jcl.mongodb.MongoUtils;
import com.jcl.pojo.DealCj;
import com.jcl.pojo.EntrustOrder;
import com.jcl.pojo.FundMessage;
import com.jcl.pojo.Fundset;
import com.jcl.pojo.FundsetExample;
import com.jcl.pojo.FundsetExample.Criteria;
import com.jcl.pojo.Holder;
import com.jcl.pojo.Subzh;
import com.jcl.tradedao.HisOrderMapper;
import com.jcl.tradedao.HisTradeMapper;
import com.jcl.tradedao.HolderMapper;
import com.jcl.tradedao.SettleInfoMapper;
import com.jcl.util.Arith;
import com.jcl.util.DateUtil;
import com.jcl.util.StringUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;

@Component 
public class HistoryRecordTask {

	public static Logger log = Logger.getLogger(HistoryRecordTask.class);
	@Autowired
	private SettleInfoMapper funddao;
	@Autowired
	private HolderMapper holddao;
	@Autowired
	private HisTradeMapper dealdao;
	@Autowired
	private HisOrderMapper entrustdao;
	@Autowired
	private SubzhMapper subzhdao;
	
	@Autowired
	private FundsetMapper fundsetMapper;
	
	@Resource(name = "dsForStock")
	private Datastore dsForStock;
	private DB db;
	Map<String,Subzh> storeSubzhs=new HashMap<String,Subzh>();
	



	 @Scheduled(cron="00 05 17 * * ? ")   //下午16点执行操作 
	 public void aTask(){  
		 log.info("历史数据读取处理任务开始>........");
		 
		 try {
			 db = dsForStock.getDB();
			 /*if(db.collectionExists("history_cj")){
				 db.getCollection("history_cj").drop(); 
			 }
			 if(db.collectionExists("history_wt")){
				 db.getCollection("history_wt").drop(); 
			 }
			 if(db.collectionExists("history_fund")){
				 db.getCollection("history_fund").drop(); 
			 }
			 if(db.collectionExists("history_hold")){
				 db.getCollection("history_hold").drop(); 
			 }*/
			 DBCollection lscjCollection= !db.collectionExists("history_cj") ? db.createCollection("history_cj", new BasicDBObject()): db.getCollection("history_cj");
			 DBCollection lswtCollection= !db.collectionExists("history_wt") ? db.createCollection("history_wt", new BasicDBObject()): db.getCollection("history_wt");
			 DBCollection lsfundCo= !db.collectionExists("history_fund") ? db.createCollection("history_fund", new BasicDBObject()): db.getCollection("history_fund");
			 DBCollection lsholdCo= !db.collectionExists("history_hold") ? db.createCollection("history_hold", new BasicDBObject()): db.getCollection("history_hold");
			 Date nowdate=new Date();
			 DateFormat df2=new SimpleDateFormat("yyyyMMdd");
			 String day2=df2.format(nowdate);
			 Long isload=Long.parseLong(day2+"090000");
			 Long isstartcj=lscjCollection.count(new BasicDBObject());
			 Long isstartwt=lswtCollection.count(new BasicDBObject());
			 List<DealCj> cjlist=new ArrayList<DealCj>();
			 List<EntrustOrder> wtlist=new ArrayList<EntrustOrder>();
			 List<FundMessage> fundlist=new ArrayList<FundMessage>();
			 List<Holder> holdlist=new ArrayList<Holder>();
			 Fundset fundset=null;
			 DateFormat df=new SimpleDateFormat("yyyy-MM-dd");
			 Map<String,Object> condition=new HashMap<String,Object>();
			 if(isstartcj<1 && isstartwt<1){
				 log.info("走线路1");
			 }else{//将当天的记录查询，并加入到这里
				 Calendar cl = Calendar.getInstance();
				 cl.setTime(new Date());
				 //周5晚上交易,周一结算,周5晚上数据,周6的数据
				 int i = cl.get(Calendar.DAY_OF_WEEK);
				 if(i==2){
					 cl.add(Calendar.DATE, -3);
					 Calendar coo = Calendar.getInstance();
					 coo.setTime(new Date());
					 coo.add(Calendar.DATE, -2);
					 String cooday=df.format(coo.getTime());//昨天 年-月-日 2018-12-13
					 condition.put("cooday", cooday);
				 }else{
					 cl.add(Calendar.DATE, -1);
				 }
				 Date yesterday = cl.getTime();
				 String tradeday=df.format(nowdate);//今天 年-月-日 2018-12-14
				 String ystraday=df.format(yesterday);//昨天 年-月-日 2018-12-13
				 
				 Integer intiday=Integer.parseInt(day2);//今天 年月日  20181214
				 Integer preday=Integer.parseInt(df2.format(yesterday));//昨天 年月日 20181213
				 condition.put("intday", intiday);
				 condition.put("tradedate", tradeday);
				 condition.put("ystraday", ystraday);
				 condition.put("preday", preday);
				 //记录上次高度
				 FundsetExample fundsetExample=new FundsetExample();
				 List<Fundset> list = fundsetMapper.selectByExample(fundsetExample);
				 if(list !=null && list.size()>0){
					 fundset=list.get(0);
					 condition.put("cjheight", fundset.getFundtype());
					 condition.put("wtheihgt", fundset.getSetpro());
				 }
				 log.info("走线路2");
			 }
			 Long isstartcj2=lscjCollection.count(new BasicDBObject("trade_time",new BasicDBObject("$gt", isload)));
			 Long isstartwt2=lswtCollection.count(new BasicDBObject("order_time",new BasicDBObject("$gt", isload)));
			 if(isstartcj2>0 || isstartwt2>0){//已经脉今天的数据记录了，就不去查
				 return;
			 }
			 cjlist=dealdao.getDealCjList(condition);
			 //保存当前成交高度
			 wtlist=entrustdao.getEntrustRecordList(condition);
			 //保存当前委托高度
			 fundlist=funddao.getFundList(condition);
			 holdlist=holddao.getHoldList(condition);
			 log.info("今日委托数量："+wtlist.size()+"今日成交数量："+cjlist.size()+"今日资金信息数量："+fundlist.size()+"今日持仓数量："+holdlist.size());
			 int dealCjH=0;
			 if(cjlist!=null && cjlist.size()>0){
				 saveDealcj(cjlist,lscjCollection);
				 dealCjH = cjlist.get(cjlist.size() - 1).getId();
			 }
			 log.info("同步成交记录成功");
			 int entrustOrderH=0;
			 if(wtlist!=null && wtlist.size()>0){
				 saveEntrustRecord(wtlist,lswtCollection);
				 entrustOrderH = wtlist.get(wtlist.size() - 1).getId();
			 }
			 log.info("同步委托记录成功");
			 
			 if(fundlist!=null && fundlist.size()>0){
				 saveFundlist(fundlist,lsfundCo);
			 }
			 log.info("同步资金信息记录成功");
			 if(holdlist!=null && holdlist.size()>0){
				 saveHoldlist(holdlist,lsholdCo);
			 }
			 log.info("将历史数据同步到mongodb完成");
			 if(fundset !=null){
				 //更新高度
				 if(dealCjH>0){
					 fundset.setFundtype(dealCjH);
				 }
				 if(entrustOrderH>0){
					 fundset.setSetpro(entrustOrderH);
				 }
				 fundsetMapper.updateByPrimaryKey(fundset);
			 }else{
				 fundset=new Fundset();
				//更新高度
				 if(dealCjH>0){
					 fundset.setFundtype(dealCjH);
				 }
				 if(entrustOrderH>0){
					 fundset.setSetpro(entrustOrderH);
				 }
				 fundset.setCreatetime(new Date());
			 }
			 log.info("同步委托记录,成交记录高度成功");
			} catch (Exception e) {
				e.printStackTrace();
				
			}
	}
	 
	 
	private void saveDealcj(List<DealCj> cjlist,DBCollection dbCollection){
		try {
			DateFormat df=new SimpleDateFormat("yyyyMMddHHmmss");
			DateFormat df2=new SimpleDateFormat("yyyyMMdd");
			for(DealCj deal:cjlist){
				//处理一些数据然后放到mongodb库中
				String userid=deal.getSubzh();
				if(userid==null){
					break;
				}
				String jjr="";
				String qd="";
				String gly="";
				Map<String,Object> users=getParents(userid);
				if(users.containsKey("jjr")){
					jjr=(String)users.get("jjr");
				}
				if(users.containsKey("qd")){
					qd=(String)users.get("qd");
				}
				if(users.containsKey("gly")){
					gly=(String)users.get("gly");
				}
				String jjrName="";
				if(users.containsKey("jjrName")){
					jjrName=(String)users.get("jjrName");
				}
				String qdName="";
				if(users.containsKey("qdName")){
					qdName=(String)users.get("qdName");
				}
				String username="";
				if(users.containsKey("username")){
					username=(String)users.get("username");
				}
				String phone=(String)users.get("phone");
				BasicDBObject dbObj = new BasicDBObject();
				dbObj.put("orderNo", deal.getOrderNo());//成交编号
				dbObj.put("entrustNo", deal.getEntrustNo());//委托编号
				dbObj.put("subzh", userid);//当前用户
				dbObj.put("exchangeType", deal.getExchangeType());//市场
				dbObj.put("stockCode", deal.getStockCode());//
				dbObj.put("stockName", deal.getStockName());//
				dbObj.put("businessPrice", formateDouble(deal.getBusinessPrice()));//成交价格
				dbObj.put("businessVol", deal.getBusinessVol().intValue());//成交数量
				dbObj.put("hedgeType", deal.getHedgeType());//成交数量
				int bs=deal.getEntrustBs();
				dbObj.put("entrustBs", bs);//委托类型
				String cjtime="0";
				Date ddate=deal.getTradeDate();
				if(ddate!=null){
					String date=df2.format(ddate);
					String dtime=String.valueOf(deal.getBusinessTime());
					if(dtime.length()==5){
						dtime="0"+dtime;
					}else if(dtime.length()==4){
						dtime="00"+dtime;
					}else if(dtime.length()==3){
						dtime="000"+dtime;
					}else if(dtime.length()==2){
						dtime="0000"+dtime;
					}else if(dtime.length()==1){
						dtime="00000"+dtime;
					}
					cjtime=date+dtime;
				}else{
					log.error("成交记录中成交时间不能为空");
				}
				//
				dbObj.put("directionType", deal.getDirectionType());//
				dbObj.put("fee", formateDouble(deal.getFee()));//
				dbObj.put("occurBalance", formateDouble(deal.getOccurBalance()));//
				dbObj.put("mdealtime", Long.parseLong(cjtime));//
				dbObj.put("phone", phone);//
				
				dbObj.put("pt",(String)users.get("pt") );//所在平台
				dbObj.put("agentzh", (String)users.get("dls"));//所属代理商
				dbObj.put("agentname", (String)users.get("dlsname"));//代理名字
				dbObj.put("brokername", jjrName);//
				dbObj.put("channelname", qdName);
				dbObj.put("username",username);
				dbObj.put("broker", jjr);//
				dbObj.put("channel", qd);//
				dbObj.put("manage", gly);
				
				dbCollection.insert(dbObj);//增加一条java的废单记录
				
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	
	
	private void saveEntrustRecord(List<EntrustOrder> wtlist,DBCollection dbCollection){
		try {
			DateFormat df=new SimpleDateFormat("yyyyMMddHHmmss");
			for(EntrustOrder wt:wtlist){
				String userid=wt.getSubzh();
				String jjr="";
				String qd="";
				String gly="";
				Map<String,Object> users=getParents(userid);
				if(users.containsKey("jjr")){
					jjr=(String)users.get("jjr");
				}
				if(users.containsKey("qd")){
					qd=(String)users.get("qd");
				}
				if(users.containsKey("gly")){
					gly=(String)users.get("gly");
				}
				String jjrName="";
				if(users.containsKey("jjrName")){
					jjrName=(String)users.get("jjrName");
				}
				String qdName="";
				if(users.containsKey("qdName")){
					qdName=(String)users.get("qdName");
				}
				String phone=(String)users.get("phone");
				String username="";
				if(users.containsKey("username")){
					username=(String)users.get("username");
				}
				BasicDBObject dbObj = new BasicDBObject(10);
				dbObj.put("entrustNo",wt.getEno());//
				dbObj.put("subzh", userid);//
				dbObj.put("exchangeType", wt.getExchangeType());//
				dbObj.put("stockCode", wt.getStockCode());//
				dbObj.put("stockName", wt.getStockName());//
				Double entrustje=Arith.mul(wt.getEntrustPrice(), wt.getEntrustVol());
				dbObj.put("wtje", formateDouble(entrustje));
				dbObj.put("entrustPrice", formateDouble(wt.getEntrustPrice()));//
				dbObj.put("entrustVol", wt.getEntrustVol());//
				String wttime="0";
				String entrustDate=String.valueOf(wt.getEntrustDate());
				String entrustTime=String.valueOf(wt.getEntrustTime());
				if(entrustDate.length()>2&&entrustTime.length()>0){
					if(entrustTime.length()==5){
						entrustTime="0"+entrustTime;
					}else if(entrustTime.length()==4){
						entrustTime="00"+entrustTime;
					}else if(entrustTime.length()==3){
						entrustTime="000"+entrustTime;
					}else if(entrustTime.length()==2){
						entrustTime="0000"+entrustTime;
					}else if(entrustTime.length()==1){
						entrustTime="00000"+entrustTime;
					}
					wttime=entrustDate+entrustTime;
				}
				dbObj.put("mentrusttime", Long.parseLong(wttime));//委托时间
				wttime="";
				String dealDate=String.valueOf(wt.getBusinessDate());
				String dealTime=String.valueOf(wt.getBusinessTime());
				if(dealDate.length()>2&&dealTime.length()>0){
					if(dealTime.length()==5){
						dealTime="0"+dealTime;
					}else if(dealTime.length()==4){
						dealTime="00"+dealTime;
					}else if(dealTime.length()==3){
						dealTime="000"+dealTime;
					}else if(dealTime.length()==2){
						dealTime="0000"+dealTime;
					}else if(dealTime.length()==1){
						dealTime="00000"+dealTime;
					}
					wttime=dealDate+dealTime;
					DateFormat df2=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					wttime=df2.format(df.parse(wttime));
				}
				dbObj.put("dealtime",wttime);//成交时间
				
				dbObj.put("entrustBs", wt.getEntrustBs());//买卖方向
				dbObj.put("directionType", wt.getDirectionType());//开平
				dbObj.put("entrustStatus", wt.getEntrustStatus());//
				dbObj.put("businessPrice", formateDouble(wt.getBusinessPrice()));//
				dbObj.put("businessVol", wt.getBusinessVol());//
				dbObj.put("ordertype", wt.getOrdertype());//市价，限价
				String cjtime="0";
				if(wt.getBusinessDate()!=null && wt.getBusinessTime()!=null){
					String businessDate=wt.getBusinessDate()+"";
					String businessTime=wt.getBusinessTime()+"";
					if(businessDate.length()>2&&businessTime.length()>2){
						if(businessTime.length()==5){
							businessTime="0"+businessTime;
						}
						cjtime=businessDate+businessTime;
					}
				}
				dbObj.put("mdealtime", Long.parseLong(cjtime));//
				dbObj.put("phone", phone);//
				dbObj.put("pt",(String)users.get("pt") );//所在平台
				dbObj.put("agentzh", (String)users.get("dls"));//所属代理商
				dbObj.put("agentname", (String)users.get("dlsname"));//代理名字
				dbObj.put("brokername", jjrName);//
				dbObj.put("channelname", qdName);
				dbObj.put("username",username);
				dbObj.put("broker", jjr);//
				dbObj.put("channel", qd);//
				dbObj.put("manage", gly);
				
				dbCollection.insert(dbObj);//增加一条java的废单记录
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	
	private void saveFundlist(List<FundMessage> fundlist,DBCollection dbCollection){
		try {
			for(FundMessage fund:fundlist){
				String userid=fund.getSubzh();
				String jjr="";
				String qd="";
				String gly="";
				Map<String,Object> users=getParents(userid);
				if(users.containsKey("jjr")){
					jjr=(String)users.get("jjr");
				}
				if(users.containsKey("qd")){
					qd=(String)users.get("qd");
				}
				if(users.containsKey("gly")){
					gly=(String)users.get("gly");
				}
				String jjrName="";
				if(users.containsKey("jjrName")){
					jjrName=(String)users.get("jjrName");
				}
				String qdName="";
				if(users.containsKey("qdName")){
					qdName=(String)users.get("qdName");
				}
				String phone=(String)users.get("phone");
				String username="";
				if(users.containsKey("username")){
					username=(String)users.get("username");
				}
				BasicDBObject dbObj = new BasicDBObject(10);
				dbObj.put("fee", fund.getFee());//
				dbObj.put("settleDate",fund.getSettleDate());//
				dbObj.put("subzh", fund.getSubzh());//
				dbObj.put("preFundBalance", formateDouble(fund.getPreFundBalance()));//总资金
				
				dbObj.put("freezeBalance",formateDouble(fund.getFreezeBalance()));//占用保证金
				dbObj.put("totalFloatprofit", formateDouble(fund.getTotalFloatprofit()));//持仓盈亏
				dbObj.put("totalCloseprofit", formateDouble(fund.getTotalCloseprofit()));//平仓盈亏
				
				dbObj.put("marketBalance", formateDouble(fund.getMarketBalance()));//市值
				dbObj.put("incomingBalance", fund.getIncomingBalance());//
				dbObj.put("outcomingBalance", fund.getOutcomingBalance());//
				dbObj.put("occur_balance", fund.getOccur_balance());//
				if(fund.getPreFundBalance()!=null && fund.getPreFundBalance()!=0){
					double riskdegree=Arith.div(fund.getFreezeBalance(), fund.getPreFundBalance());
					dbObj.put("rate", formateDouble(riskdegree));
				}else{
					dbObj.put("rate", formateDouble(fund.getRate()));
				}
				dbObj.put("enableBalance", fund.getEnableBalance());
				dbObj.put("initLoan", fund.getInitLoan());//
				dbObj.put("initBalance", fund.getInitBalance());//directionType
				dbObj.put("noFetch", fund.getNoFetch());
				dbObj.put("username", username);//
				dbObj.put("phone", phone);//
				dbObj.put("pt",(String)users.get("pt") );//所在平台
				dbObj.put("agentzh", (String)users.get("dls"));//所属代理商
				dbObj.put("agentname", (String)users.get("dlsname"));//代理名字
				dbObj.put("brokername", jjrName);//
				dbObj.put("channelname", qdName);
				dbObj.put("broker", jjr);//
				dbObj.put("channel", qd);//
				dbObj.put("manage", gly);
				
				dbCollection.insert(dbObj);//增加一条java的废单记录
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	
	
	private void saveHoldlist(List<Holder> holderlist,DBCollection dbCollection){
		try {
			for(Holder hold:holderlist){
				String userid=hold.getSubzh();
				String jjr="";
				String qd="";
				String gly="";
				Map<String,Object> users=getParents(userid);
				if(users.containsKey("jjr")){
					jjr=(String)users.get("jjr");
				}
				if(users.containsKey("qd")){
					qd=(String)users.get("qd");
				}
				if(users.containsKey("gly")){
					gly=(String)users.get("gly");
				}
				String jjrName="";
				if(users.containsKey("jjrName")){
					jjrName=(String)users.get("jjrName");
				}
				String qdName="";
				if(users.containsKey("qdName")){
					qdName=(String)users.get("qdName");
				}
				String phone=(String)users.get("phone");
				String username="";
				if(users.containsKey("username")){
					username=(String)users.get("username");
				}
				BasicDBObject dbObj = new BasicDBObject(10);
				dbObj.put("subzh", userid);//
				dbObj.put("holderDate", hold.getHolderDate());//
				dbObj.put("exchangeType", hold.getExchangeType());//
				dbObj.put("entrustBs", hold.getEntrustBs());//持仓方向
				dbObj.put("stockName", hold.getStockName());//
				dbObj.put("stockCode", hold.getStockCode());
				dbObj.put("currentVol", hold.getCurrentVol());//
				dbObj.put("todayVol", hold.getTodayVol());//
				dbObj.put("zuocang", hold.getCurrentVol()-hold.getTodayVol());
				//持仓均价和持仓盈亏应该要格式化一下
				dbObj.put("keepCostPrice", formateDouble(hold.getKeepCostPrice()));//持仓均价
				dbObj.put("todayCostPrice", hold.getTodayCostPrice());
				dbObj.put("profit", formateDouble(hold.getProfit()));
				dbObj.put("username", username);//
				dbObj.put("phone", phone);//
				dbObj.put("pt",(String)users.get("pt") );//所在平台
				dbObj.put("agentzh", (String)users.get("dls"));//所属代理商
				dbObj.put("agentname", (String)users.get("dlsname"));//代理名字
				dbObj.put("brokername", jjrName);//
				dbObj.put("channelname", qdName);
				dbObj.put("broker", jjr);//
				dbObj.put("channel", qd);//
				dbObj.put("manage", gly);
				
				dbCollection.insert(dbObj);//增加一条java的废单记录
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	
	
	private Map<String,Object> getParents(String userid){
		Map<String,Object> p=new HashMap<String,Object>();
		try{
			p.put("user", userid);
			Subzh user=null;
			if(storeSubzhs.containsKey(userid)){//如果这个用户已经查过就从缓存map中取
				user=storeSubzhs.get(userid);
			}else{//如果这个用户之前没查过，就需要从库中查询，并将它放进缓存map中
				user=subzhdao.selectChannelBrokerBySubzh(userid);//当前用户
				storeSubzhs.put(userid, user);
			}
			if(user==null){
				p.put("phone", "13100635079");
				p.put("username", "13100635079");
				p.put("jjr","JJR000001");
				p.put("jjrName", "kjds顶嘴");
				p.put("dls", "---");
				p.put("dlsname", "---");
				p.put("qd","QD000");
				p.put("qdName", "kjlsl");
				p.put("pt", "---");
				p.put("gly","06371309");
			}else{
				p.put("phone", user.getPhone());
				p.put("username", user.getName());
				p.put("jjr",user.getAllocbroker());
				p.put("jjrName", user.getBrokername());
				p.put("dls", user.getAllocagent());
				p.put("dlsname", user.getAgentname());
				p.put("qd",user.getAllocchannel());
				p.put("qdName", user.getChannelname());
				p.put("pt", user.getAllocpt());
				p.put("gly",user.getManage());
			}
			
			
			
		}catch(Exception e){
			e.printStackTrace();
			log.error("查询用户的父级出错");
		}
		return p;
	}
	
	///格式化小数，
	private Double formateDouble(Double d){
		if(d==null){
			return 0.00;
		}
		String ss=String.valueOf(d);
		if(ss.indexOf(".")>-1){
			if(ss.length()-ss.indexOf(".")<=2){
				return d;
			}
		}
		BigDecimal b =new BigDecimal(String.valueOf(d));  
		double   f1   =   b.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();  
		return f1;
	}
	
	public static void main(String[] args) {
		double d=5.3;
		String ss=String.valueOf(d);
		if(ss.indexOf(".")>-1){
			if(ss.length()-ss.indexOf(".")<=2){
				System.out.println("1情况："+d);
			}
		}
		BigDecimal b =new BigDecimal(String.valueOf(d));  
		double   f1   =   b.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();  
		System.out.println("2情况："+f1);
	}
}