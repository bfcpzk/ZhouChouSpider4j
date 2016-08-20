package service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.Comment;
import model.Support;
import model.Update;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Created by zhaokangpan on 16/8/18.
 */
public class JsonService{

    //工具类
    private static JsonParser jsonparer = new JsonParser();// 初始化解析json格式的对象
    private static HttpClient client = new DefaultHttpClient();//httpclient
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    //私有属性
    private String update_url;
    private String comment_url;
    private String support_url;
    private String deal_id;

    public JsonService(){
        super();
    }

    public JsonService(String pid, String updateSize, String commentSize, String supportSize){
        this.update_url = "http://www.zhongchou.com/deal-march_list?id=" + pid + "&offset=0&page_size=" + updateSize;
        this.comment_url = "http://www.zhongchou.com/deal-topic_list?id=" + pid + "&offset=0&page_size=" + commentSize;
        this.support_url = "http://www.zhongchou.com/deal-support_list?id=" + pid + "&offset=0&page_size=" + supportSize;
        this.deal_id = pid;
    }

    public String getUpdate_url() {
        return update_url;
    }

    public void setUpdate_url(String update_url) {
        this.update_url = update_url;
    }

    public String getSupport_url() {
        return support_url;
    }

    public void setSupport_url(String support_url) {
        this.support_url = support_url;
    }

    public String getComment_url() {
        return comment_url;
    }

    public void setComment_url(String comment_url) {
        this.comment_url = comment_url;
    }

    public static String StringFilter(String str) throws PatternSyntaxException {
        // 只允许字母和数字
        // String regEx = "[^a-zA-Z0-9]";
        // 清除掉所有特殊字符
        String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    public List<Support> parseSupport() throws IOException{
        List<Support> sulist = new ArrayList<Support>();
        HttpGet get = new HttpGet(this.getSupport_url());
        HttpResponse res = client.execute(get);
        HttpEntity entity = res.getEntity();
        String responseContent = EntityUtils.toString(entity, "UTF-8");
        //System.out.println(responseContent);
        JsonObject supportlist = jsonparer.parse(responseContent).getAsJsonObject();
        if(supportlist.has("data")){
            JsonObject su_jo = supportlist.get("data").getAsJsonObject();
            if(su_jo.has("support_list")){
                JsonArray su_ja = su_jo.get("support_list").getAsJsonArray();
                try{
                    for(int i = 0 ; i < su_ja.size() ; i++){
                        JsonObject su_jo_temp = su_ja.get(i).getAsJsonObject();
                        Support su = new Support();
                        su.setUser_id(su_jo_temp.get("user_id").getAsString());
                        su.setUser_name(su_jo_temp.get("user_name").getAsString());
                        su.setDeal_price(su_jo_temp.get("deal_price").getAsString());
                        su.setReturn_type(su_jo_temp.get("return_type").getAsInt());
                        su.setDeal_num(su_jo_temp.get("deal_num").getAsString());
                        su.setPay_time(String.valueOf(sdf.parse(su_jo_temp.get("pay_time").getAsString()).getTime()));
                        su.setDeal_id(this.deal_id);
                        sulist.add(su);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
        return sulist;
    }

    public List<Comment> parseComment() throws IOException{
        List<Comment> colist = new ArrayList<Comment>();
        HttpGet get = new HttpGet(this.getComment_url());
        HttpResponse res = client.execute(get);
        HttpEntity entity = res.getEntity();
        String responseContent = EntityUtils.toString(entity, "UTF-8");
        //System.out.println(responseContent);
        JsonObject commentlist = jsonparer.parse(responseContent).getAsJsonObject();
        if(commentlist.has("data")){
            JsonObject co_jo = commentlist.get("data").getAsJsonObject();
            if(co_jo.has("topic_list")){
                JsonArray co_ja = co_jo.get("topic_list").getAsJsonArray();
                try{
                    for(int i = 0 ; i < co_ja.size() ; i++){
                        JsonObject co_jo_temp = co_ja.get(i).getAsJsonObject();
                        Comment co = new Comment();
                        co.setLog_id(co_jo_temp.get("log_id").getAsString());
                        co.setLog_info(StringFilter(co_jo_temp.get("log_info").getAsString().replaceAll("\\[|\\]","").replaceAll("'","")));
                        co.setDeal_id(co_jo_temp.get("deal_id").getAsString());
                        co.setUser_id(co_jo_temp.get("user_id").getAsString());
                        co.setUser_name(co_jo_temp.get("user_name").getAsString());
                        co.setIs_self(co_jo_temp.get("is_self").getAsInt());
                        co.setCreate_time(String.valueOf(sdf.parse(co_jo_temp.get("create_time").getAsString()).getTime()));
                        colist.add(co);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
        return colist;
    }

    public List<Update> parseUpdate() throws IOException{
        List<Update> uplist = new ArrayList<Update>();
        HttpGet get = new HttpGet(this.getUpdate_url());
        HttpResponse res = client.execute(get);
        HttpEntity entity = res.getEntity();
        String responseContent = EntityUtils.toString(entity, "UTF-8");
        //System.out.println(responseContent);
        JsonObject updatelist = jsonparer.parse(responseContent).getAsJsonObject();
        if(updatelist.has("data")){
            JsonObject up_jo = updatelist.get("data").getAsJsonObject();
            if(up_jo.has("march_list")){
                JsonArray up_ja = up_jo.get("march_list").getAsJsonArray();
                try{
                    for(int i = 0 ; i < up_ja.size() ; i++){
                        JsonObject up_jo_temp = up_ja.get(i).getAsJsonObject();
                        Update up = new Update();
                        up.setId(up_jo_temp.get("id").getAsString());
                        up.setLog_info(StringFilter(up_jo_temp.get("log_info").getAsString().replaceAll("\\[|\\]","").replaceAll("'","")));
                        up.setCreate_time(String.valueOf(sdf.parse(up_jo_temp.get("create_time").getAsString()).getTime()));
                        up.setIs_self(up_jo_temp.get("is_self").getAsInt());
                        up.setUser_id(up_jo_temp.get("user_id").getAsString());
                        up.setUser_name(up_jo_temp.get("user_name").getAsString());
                        up.setDeal_id(this.deal_id);
                        uplist.add(up);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return uplist;
    }
}
