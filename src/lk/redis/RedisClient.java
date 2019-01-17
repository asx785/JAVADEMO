package lk.redis;

import lk.loginForm.method.ReadProperties;
import redis.clients.jedis.Jedis;

import java.util.*;

/**
 * @Auther: Lk
 * @Date: 2018/12/27 0027 14:10
 * @Description:
 */
public class RedisClient {
    static Jedis jedis;
    public RedisClient(Jedis jedis){
        //初始化
        //jedis=new Jedis("localhost",6379);
        //jedis.auth("asx785");

        this.jedis=jedis;
        jedis.select(2);

    }

    /**
     * 功能描述:将数据写入redis
     *
     * @param: [map]
     * @return: void
     * @auther: LK
     * @date: 2018/12/27 0027 15:42
     */
    public synchronized void writeRedis(Map<String,Object> map,int name_id){

        String TagName=null;
        String DeviceName=null;
        String name=null;
        Map<String,String> row=new HashMap<String,String>();
        //遍历map 并将Object对象转为String 以Hash存入Redis
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if(entry.getKey().equals("TagName")){
                //TagName=entry.getValue().toString()+"@";//保存TagName，方便命名存储 记录式存储
                TagName=entry.getValue().toString();//保存TagName，方便命名存储
            }
            if(entry.getKey().equals("DeviceName")){
                DeviceName=entry.getValue().toString();//保存DeviceName，方便命名存储
            }
            row.put(entry.getKey(),entry.getValue().toString());
        }
        name=DeviceName+"-"+TagName;
        try {
            //jedis.hmset(TagName+name_id,row);//有记录式存储
            jedis.hmset(name,row);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 功能描述: 读取redis中的数据
     *
     * @param: [name_id]
     * @return: java.lang.String
     * @auther: LK
     * @date: 2018/12/27 0027 16:03
     */
    public String readRedis(){

        try {

            if(jedis.hgetAll("W_%")!=null){

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
