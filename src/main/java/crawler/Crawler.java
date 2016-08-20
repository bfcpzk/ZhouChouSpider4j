package crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import model.Comment;
import model.Project;
import model.Support;
import model.Update;
import service.JsonService;
import service.RegularService;
import util.Jdbc_Util;

public class Crawler {

    //工具类
    static Jdbc_Util db = new Jdbc_Util();

    //设置参数
    private static String page_url = "http://www.zhongchou.com/browse/id-28-p";
    private static int page_num = 55;
    private static String p_category = "公益";

	public static void main(String[] args) throws IOException, InterruptedException {

        String sql = "";
        RegularService rs = new RegularService();
        List<String> projects_url_list;
        for( int i = 1 ; i <= page_num ; i++){//第n页
            System.out.println("页数:" + i);
            projects_url_list = rs.parseProjectUrl(page_url + i);
            for( int j = 0 ; j < projects_url_list.size() ; j++){//每一个具体项目

                System.out.println("当前URL:" + projects_url_list.get(j));

                //项目特有信息
                Project p = rs.parseProject(projects_url_list.get(j), p_category);
                sql = "insert into `project` (`pid`, `purl`, `pname`, `pfounder`, `praised`, `pconcern_num`, `pgoal_money`, `pgy`, `paddr`, `plabel`, `pnonprofit_num`, `psupport_money`, `pupdate_num`, `pcomment_num`, `psupport_num`, `ptext`, `pcategory`, `ppercent`, `praise_cate_num`) values ('" + p.getPid() + "', '" + p.getPurl() + "', '" + p.getPname() + "', '" + p.getPfounder() + "', '" + p.getPraised() + "', '" + p.getPconcern_num() + "', '" + p.getPgoal_money() + "', '" + p.getPgy() + "', '" + p.getPaddr() + "', '" + p.getPlabel() + "', '" + p.getPnonprofit_num() + "', '" + p.getPsupport_money() + "', '" + p.getPupdate_num() + "', '" + p.getPcomment_num() + "', '" + p.getPsupport_num() + "', '" + p.getPtext() + "', '" + p.getPcategory() + "', '" + p.getPpercent() + "', '" + p.getPraise_cate_num() + "')";
                db.add(sql);
                Thread.sleep(6000);
                //用户反馈信息
                JsonService js = new JsonService(p.getPid(),p.getPupdate_num(),p.getPcomment_num(),p.getPsupport_num());
                List<Support> sulist = new ArrayList<Support>();
                if(!p.getPsupport_num().equals("0")){
                    sulist = js.parseSupport();
                }
                Thread.sleep(6000);
                List<Comment> colist = new ArrayList<Comment>();
                if(! p.getPcomment_num().equals("0")){
                    colist = js.parseComment();
                }
                Thread.sleep(6000);
                List<Update> uplist = new ArrayList<Update>();
                if(! p.getPupdate_num().equals("0")){
                    uplist = js.parseUpdate();
                }
                Thread.sleep(6000);
                System.out.println("支持:" + p.getPsupport_num());
                for(int k = 0 ; k < sulist.size() ; k++){
                    //System.out.println(sulist.get(k).getUser_id() + "," + sulist.get(k).getUser_name());
                    sql = "insert into `support` (`su_user_id`, `su_user_name`, `su_deal_id`, `su_deal_price`, `su_return_type`, `su_deal_num`, `su_pay_time`) values ('" + sulist.get(k).getUser_id() + "', '" + sulist.get(k).getUser_name() + "', '" + sulist.get(k).getDeal_id() + "', '" + sulist.get(k).getDeal_price() + "', '" + sulist.get(k).getReturn_type() + "', '" + sulist.get(k).getDeal_num() + "', '" + sulist.get(k).getPay_time() + "')";
                    db.add(sql);
                }

                System.out.println("评论:" + p.getPcomment_num());
                for(int k = 0 ; k < colist.size() ; k++){
                    //System.out.println(colist.get(k).getUser_id() + "," + colist.get(k).getLog_info());
                    sql = "insert into `comment` (`co_log_id`, `co_log_info`, `co_deal_id`, `co_user_id`, `co_user_name`, `co_create_time`, `co_is_self`) values ('" + colist.get(k).getLog_id() + "', '" + colist.get(k).getLog_info() + "', '" + colist.get(k).getDeal_id() + "', '" + colist.get(k).getUser_id() + "', '" + colist.get(k).getUser_name() + "', '" + colist.get(k).getCreate_time() + "', '" + colist.get(k).getIs_self() + "')";
                    db.add(sql);
                }

                System.out.println("更新:" + p.getPupdate_num());
                for(int k = 0 ; k < uplist.size() ; k++){
                    //System.out.println(uplist.get(k).getUser_id() + "," + uplist.get(k).getLog_info());
                    sql = "insert into `update` (`up_id`, `up_log_info`, `up_deal_id`, `up_user_id`, `up_user_name`, `up_create_time`, `up_is_self`) values ('" + uplist.get(k).getId() + "', '" + uplist.get(k).getLog_info() + "', '" + uplist.get(k).getDeal_id() + "', '" + uplist.get(k).getUser_id() + "', '" + uplist.get(k).getUser_name() + "', '" + uplist.get(k).getCreate_time() + "', '" + uplist.get(k).getIs_self() + "')";
                    db.add(sql);
                }
            }
        }
	}
}
