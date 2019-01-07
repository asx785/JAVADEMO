package lk;

import com.ioserver.bean.Struct_TagInfo;
import com.ioserver.bean.Struct_TagInfo_AddName;
import com.sun.jna.WString;
import lk.Impl.IOService;
import lk.Impl.IOServiceImpl;
import lk.flag.FlagEntity;
import lk.redis.RedisClient;
import lk.redis.RedisUtil;
import lk.redis.redisSubscribe.Publisher;
import lk.redis.redisSubscribe.SubThread;
import lk.thread.FuncIsConnectThread;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Date;
import java.text.DateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Auther: Lk
 * @Date: 2018/12/26 0026 15:24
 * @Description:启动类
 */
public class Main {


    private static IOService ioService;
    public  static  List<WString> vecAllTagName ;     //tag名列表
    private static RedisClient redisClient=null;           //redis方法
    private static Integer name_id;                         //命名后辍


    public Main() {
        redisClient=new RedisClient(RedisUtil.getJedis());//初始化调用redis池
        ioService=new IOServiceImpl();
        vecAllTagName = new LinkedList<>();
        start();
    }
    //入口
    public static void main(String[] args) {
       Main main = new Main();

    }

    private void start() {
        //连接opc
        FlagEntity.flagConnect = ioService.funcConnect("127.0.0.1", "12380");
        if (FlagEntity.flagConnect == 0) {
            System.out.println("连接success");
        } else {
            System.out.println("连接failed");
        }


        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();//线程池创建
        FuncIsConnectThread funcIsConnectThread=new FuncIsConnectThread(IOServiceImpl.client.getHandle());
        cachedThreadPool.execute(funcIsConnectThread);//连接状态线程回调


        if (FlagEntity.flagConnect == 0) {
            //vecAllTagName = ioService.funcSubscribeAllTags(); //提取Tag名称 并订阅全部
        }

        timer_subscribe(); //开始刷新

        //循环测试输入
        while (true){
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String line = null;
            try {
                System.out.println("1:同步读，2:异步读, 0订阅全部");
                line = reader.readLine();
                switch (line){
                    case "0":{
                        vecAllTagName = ioService.funcSubscribeAllTags();
                        break;
                    }
                    case "1":{
                        FlagEntity.flagSyncReadComplete=1;//同步读测试
                        break;
                    }
                    case "2":{
                        FlagEntity.flagAsyncReadComplete=1;//异步读测试
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    //刷新值获取数据
    public static void timer_subscribe() {
        Timer timer_subscribe = new Timer();
        name_id=0;
        timer_subscribe.schedule(new TimerTask() {
            // 变量刷新
            public void run() {
                //满足连接成功 且 满足订阅成功   订阅刷新
                if (FlagEntity.flagConnect == 0&&FlagEntity.flagSubscribeAll==0) {
                    System.out.println("~~~~~~~~~~~~~~订阅刷新~~~~~~~~~~~~~~~");
                    if (vecAllTagName.size() > 0) {
                        for (int i = 0; i < vecAllTagName.size(); i++) {
                            WString tagName = vecAllTagName.get(i);
                            //每次更新从中取数据把一轮的数据取出
                            Struct_TagInfo value = ioService.funcGetTagValue(tagName);
                            if (value != null) {
                                List row = new ArrayList();
                                //Map
                                Map<String,Object> map_row=new HashMap<String,Object>();

                                switch ((int) value.TagValue.ValueType) {
                                    case 1:
                                        row.add(value.TagValue.TagValue.bitVal);
                                        map_row.put("TagValue",value.TagValue.TagValue.bitVal);
                                        break;
                                    case 2:
                                        row.add(value.TagValue.TagValue.i1Val);
                                        map_row.put("TagValue",value.TagValue.TagValue.i1Val);
                                        break;
                                    case 3:
                                        row.add(value.TagValue.TagValue.i1Val);
                                        map_row.put("TagValue",value.TagValue.TagValue.i1Val);
                                        break;
                                    case 4:
                                        row.add(value.TagValue.TagValue.i2Val);
                                        map_row.put("TagValue",value.TagValue.TagValue.i2Val);
                                        break;
                                    case 5:
                                        row.add(value.TagValue.TagValue.i2Val);
                                        map_row.put("TagValue",value.TagValue.TagValue.i2Val);
                                        break;
                                    case 6:
                                        row.add(value.TagValue.TagValue.i4Val);
                                        map_row.put("TagValue",value.TagValue.TagValue.i4Val);
                                        break;
                                    case 7:
                                        row.add(value.TagValue.TagValue.i4Val);
                                        map_row.put("TagValue",value.TagValue.TagValue.i4Val);
                                        break;
                                    case 8:
                                        row.add(value.TagValue.TagValue.i8Val);
                                        map_row.put("TagValue",value.TagValue.TagValue.i8Val);
                                        break;
                                    case 9:
                                        row.add(value.TagValue.TagValue.r4Val);
                                        map_row.put("TagValue",value.TagValue.TagValue.r4Val);
                                        break;
                                    case 10:
                                        row.add(value.TagValue.TagValue.r8Val);
                                        map_row.put("TagValue",value.TagValue.TagValue.r8Val);
                                        break;
                                    case 11:
                                        row.add(value.TagValue.TagValue.wstrVal);
                                        map_row.put("TagValue",value.TagValue.TagValue.wstrVal);
                                        break;
                                    default:
                                        row.add("不支持类型");

                                        break;
                                }

                                Date TimeStamp = new Date(value.TimeStamp.Seconds.longValue() * 1000);
                                //tolocaleString过时方法替代

                                    DateFormat ddtf = DateFormat.getDateTimeInstance();
                                    {
                                        row.add(tagName);
                                        row.add("" + ddtf.format(TimeStamp));
                                        row.add(value.QualityStamp);
                                        row.add(value.TagID);
                                        System.out.println(row);
                                        //添加进Map
                                        map_row.put("TagName",tagName);
                                        map_row.put("Time",ddtf.format(TimeStamp));
                                        map_row.put("Quality",value.QualityStamp);
                                        map_row.put("TagId",value.TagID);
                                    }


                                //导入redis
                                redisClient.writeRedis(map_row,name_id);
                                //redisClient.readRedis(name_id);
                            }
                        }
                        System.out.println("---------------------------------------------");
                    }
                    name_id++;//命名后缀加1
                }

                //同步读刷新
                if (FlagEntity.flagConnect == 0&&FlagEntity.flagSyncReadComplete == 1) {
                    System.out.println("~~~~~~~~~~~~~~同步刷新~~~~~~~~~~~~~~~");
                    FlagEntity.flagSyncReadComplete = 0;
                    String strAllTagName ="Tag1,Tag2";//要查看的值
                    String[] tagNames = strAllTagName.split(",");//按逗号分开
                    Struct_TagInfo_AddName[] structTagValue = ioService.getSyncReadValue(tagNames);
                    for (int i = 0; i < structTagValue.length; i++) {
                        Struct_TagInfo_AddName value = structTagValue[i];

                        if (value != null) {
                            Vector row = new Vector();
                            //row.add(ioService.funcGetTagNameById((int)value.TagID));
                            row.add(tagNames[i]);
                            switch ((int)value.TagValue.ValueType) {
                                case 1:
                                    row.add(value.TagValue.TagValue.bitVal);
                                    break;
                                case 2:
                                    row.add(value.TagValue.TagValue.i1Val);
                                    break;
                                case 3:
                                    row.add(value.TagValue.TagValue.i1Val);
                                    break;
                                case 4:
                                    row.add(value.TagValue.TagValue.i2Val);
                                    break;
                                case 5:
                                    row.add(value.TagValue.TagValue.i2Val);
                                    break;
                                case 6:
                                    row.add(value.TagValue.TagValue.i4Val);
                                    break;
                                case 7:
                                    row.add(value.TagValue.TagValue.i4Val);
                                    break;
                                case 8:
                                    row.add(value.TagValue.TagValue.i8Val);
                                    break;
                                case 9:
                                    row.add(value.TagValue.TagValue.r4Val);
                                    break;
                                case 10:
                                    row.add(value.TagValue.TagValue.r8Val);
                                    break;
                                case 11:
                                    row.add(value.TagValue.TagValue.wstrVal);
                                    break;
                                default:
                                    row.add("不支持类型");
                                    break;
                            }

                            Date TimeStamp = new Date(value.TimeStamp.Seconds.longValue() * 1000);
                            //tolocaleString过时方法替代
                            DateFormat ddtf = DateFormat.getDateTimeInstance();
                            row.add(""+ddtf.format(TimeStamp));
                            row.add(value.QualityStamp);
                            row.add(value.TagID);
                            System.out.println(row);

                        }
                    }

                }

                //异步读刷新
                if (FlagEntity.flagConnect == 0&&FlagEntity.flagAsyncReadComplete == 1) {
                    System.out.println("~~~~~~~~~~~~~~异步刷新~~~~~~~~~~~~~~~");
                    FlagEntity.flagAsyncReadComplete = 0;
                    Vector<WString> WTagName=new Vector<>();
                    WTagName.add(new WString("Tag1"));
                    WTagName.add(new WString("Tag2"));
                    WTagName.add(new WString("Tag3"));
                    Struct_TagInfo[] structTagValue = ioService.getAsyncReadValue(WTagName);
                    for (int i = 0; i < structTagValue.length; i++) {
                        if (structTagValue[i] != null) {

                            Struct_TagInfo value = structTagValue[i];

                            if (value != null) {
                                Vector row = new Vector();
                                row.add(WTagName.get(i));

                                switch ((int)value.TagValue.ValueType) {
                                    case 1:
                                        row.add(value.TagValue.TagValue.bitVal);
                                        break;
                                    case 2:
                                        row.add(value.TagValue.TagValue.i1Val);
                                        break;
                                    case 3:
                                        row.add(value.TagValue.TagValue.i1Val);
                                        break;
                                    case 4:
                                        row.add(value.TagValue.TagValue.i2Val);
                                        break;
                                    case 5:
                                        row.add(value.TagValue.TagValue.i2Val);
                                        break;
                                    case 6:
                                        row.add(value.TagValue.TagValue.i4Val);
                                        break;
                                    case 7:
                                        row.add(value.TagValue.TagValue.i4Val);
                                        break;
                                    case 8:
                                        row.add(value.TagValue.TagValue.i8Val);
                                        break;
                                    case 9:
                                        row.add(value.TagValue.TagValue.r4Val);
                                        break;
                                    case 10:
                                        row.add(value.TagValue.TagValue.r8Val);
                                        break;
                                    case 11:
                                        row.add(value.TagValue.TagValue.wstrVal);
                                        break;
                                    default:
                                        row.add("不支持类型");
                                        break;
                                }

                                Date TimeStamp = new Date(value.TimeStamp.Seconds.longValue() * 1000);
                                //tolocaleString过时方法替代
                                DateFormat ddtf = DateFormat.getDateTimeInstance();
                                row.add(""+ddtf.format(TimeStamp));
                                row.add(value.QualityStamp);
                                row.add(value.TagID);
                                System.out.println(row);
                            }
                        }

                    }
                }


            }
        }, 1000, 1500);
    }


    //Redis订阅测试
    private void redisSubscribe(){
        System.out.println(String.format("redis pool is starting, redis ip %s, redis port %d", "127.0.0.1", 6379));
        SubThread subThread = new SubThread(RedisUtil.jedisPool);  //订阅者
        subThread.start();
        Publisher publisher = new Publisher(RedisUtil.jedisPool);   //发布者
        publisher.start();
    }

}
