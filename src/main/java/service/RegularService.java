package service;

import model.Project;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Created by zhaokangpan on 16/8/19.
 */
public class RegularService {

    //工具类
    private static HttpClient client = new DefaultHttpClient();//httpclient

    //正则模式
    //一级页面获取每个项目的URL
    private static String projects_url = "<a href=\"(.*?)\" class=\"siteCardICH3\"";
    //获取title
    private static String title = "<h3 class=\"jlxqTitle_h3\" id='move'>(.*?)</h3>";
    //获取项目发起人id
    private static String founder_id = "<span class='txt2'><a href=\"/home/id-(.*?)\">";
    //获取已筹款数
    private static String raised = "<p><span class=\"ftP\">¥(.*?)</span>";
    //项目关注数
    private static String concern_num = "<a class=\"is_right heart-shaped  deal_detail_like\" data-id=\".*?\" title=\"关注(.*?)\">";
    //目标金额
    private static String goal_money = "<span class=\"rightSpan\">目标筹资<b>¥(.*?)</b>";
    //gy
    private static String gy = "browse/id-\\d+\" target=\"_blank\" class=\"hoUdCLink\">(.*?)</a>";
    //address
    private static String addr = "class=\"site_ALink siteIlB_item\" target=\"_blank\">(.*?)</a>";
    //label
    private static String label = "search/lid-\\d+\" target=\"_blank\" class=\"hoUdCLink\">(.*?)</a>";
    //无私支持
    private static String nonprofit_num = "<h3 class=\"wszc_h3\">无私支持<b>(.*?) 人已支持</b></h3>";
    //支持等级
    private static String support_money = "<h3 class=\"zcje_h3\"><b>¥(.*?)</b>";
    //项目更新数
    private static String update_num = "data-scrollto=\"zxjzBox\">项目更新（<b>(.*?)</b>）</li>";
    //项目支持人数
    private static String support_num = "data-scrollto=\"zczOuterBox\">支持记录（<b>(.*?)</b>）</li>";
    //项目评论数
    private static String comment_num = "data-scrollto=\"plOuterBox\">评论（<b>(.*?)</b>）</li>";
    //文案信息
    private static String text = "<div class=\"newXmxqBox\" id=\"xmxqBox\">([\\s\\S]*?)<div class=\"xqMainRightBox\" id=\"right\">";


    public static String StringFilter(String str) throws PatternSyntaxException {
        // 只允许字母和数字
        // String regEx = "[^a-zA-Z0-9]";
        // 清除掉所有特殊字符
        String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }



    public List<String> parseProjectUrl(String page_url) throws IOException{
        List<String> result = new ArrayList<String>();
        HttpGet get = new HttpGet(page_url);
        HttpResponse res = client.execute(get);
        String responseContent = null; // 响应内容
        HttpEntity entity = res.getEntity();
        responseContent = EntityUtils.toString(entity, "UTF-8");

        Pattern pat_projects_url = Pattern.compile(projects_url);
        Matcher m_projects_url = pat_projects_url.matcher(responseContent);
        while(m_projects_url.find()){
            result.add(m_projects_url.group(1));
        }
        return result;
    }


    public Project parseProject(String project_url, String category) throws IOException{
        HttpGet get = new HttpGet(project_url);
        HttpResponse res = client.execute(get);
        String responseContent = null; // 响应内容
        HttpEntity entity = res.getEntity();
        responseContent = EntityUtils.toString(entity, "UTF-8");
        String temp = "";
        //编译规则
        Pattern pat_title = Pattern.compile(title);
        Pattern pat_founder_id = Pattern.compile(founder_id);
        Pattern pat_raised = Pattern.compile(raised);
        Pattern pat_concern_num = Pattern.compile(concern_num);
        Pattern pat_goal_money = Pattern.compile(goal_money);
        Pattern pat_gy = Pattern.compile(gy);
        Pattern pat_addr = Pattern.compile(addr);
        Pattern pat_label = Pattern.compile(label);
        Pattern pat_nonprofit_num = Pattern.compile(nonprofit_num);
        Pattern pat_support_money = Pattern.compile(support_money);
        Pattern pat_update_num = Pattern.compile(update_num);
        Pattern pat_support_num = Pattern.compile(support_num);
        Pattern pat_comment_num = Pattern.compile(comment_num);
        Pattern pat_text = Pattern.compile(text);
        //Matcher对象进行匹配
        Matcher m_title = pat_title.matcher(responseContent);
        Matcher m_founder_id = pat_founder_id.matcher(responseContent);
        Matcher m_raised = pat_raised.matcher(responseContent);
        Matcher m_concern_num = pat_concern_num.matcher(responseContent);
        Matcher m_goal_money = pat_goal_money.matcher(responseContent);
        Matcher m_gy = pat_gy.matcher(responseContent);
        Matcher m_addr = pat_addr.matcher(responseContent);
        Matcher m_label = pat_label.matcher(responseContent);
        Matcher m_nonprofit_num = pat_nonprofit_num.matcher(responseContent);
        Matcher m_support_money = pat_support_money.matcher(responseContent);
        Matcher m_update_num = pat_update_num.matcher(responseContent);
        Matcher m_support_num = pat_support_num.matcher(responseContent);
        Matcher m_comment_num = pat_comment_num.matcher(responseContent);
        Matcher m_text = pat_text.matcher(responseContent);

        //实例化Project类
        Project p = new Project();

        p.setPid(project_url.split("-")[2]);

        p.setPurl(project_url);

        p.setPcategory(category);

        if(m_title.find()){
            p.setPname(m_title.group(1));
        }

        if(m_founder_id.find()){
            p.setPfounder(m_founder_id.group(1));
        }

        if(m_raised.find()){
            p.setPraised(m_raised.group(1).replaceAll(",",""));
        }

        if(m_concern_num.find()){
            p.setPconcern_num(m_concern_num.group(1).replaceAll("\\(|\\)",""));
        }

        if(m_goal_money.find()){
            p.setPgoal_money(m_goal_money.group(1).replaceAll(",",""));
        }

        while(m_gy.find()){
            temp += m_gy.group(1) + "@";
        }
        p.setPgy(temp);

        temp = "";
        while(m_addr.find()){
            temp += m_addr.group(1) + "@";
        }
        p.setPaddr(temp);

        temp = "";
        while(m_label.find()){
            temp += m_label.group(1) + "@";
        }
        p.setPlabel(temp);

        temp = "";
        while (m_support_money.find()){
            temp += m_support_money.group(1).replaceAll(",","") + "@";
        }
        p.setPsupport_money(temp);

        if(m_nonprofit_num.find()){
            p.setPnonprofit_num(m_nonprofit_num.group(1).replaceAll(",",""));
        }

        if(m_support_num.find()){
            p.setPsupport_num(m_support_num.group(1).replaceAll(",",""));
        }

        if(m_comment_num.find()){
            p.setPcomment_num(m_comment_num.group(1).replaceAll(",",""));
        }

        if(m_update_num.find()){
            p.setPupdate_num(m_update_num.group(1).replaceAll(",",""));
        }

        p.setPpercent(1.0 * Double.parseDouble(p.getPraised())/Double.parseDouble(p.getPgoal_money()));

        p.setPraise_cate_num(p.getPsupport_money().split("@").length);

        while (m_text.find()) {
            p.setPtext(StringFilter(m_text.group(1).replaceAll("<[.[^<]]*>","").replaceAll("\n|\t|\r| ","").replaceAll("'","")));
        }

        return p;
    }
}
