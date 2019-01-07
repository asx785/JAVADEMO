package lk.Impl;

import com.ioserver.bean.*;
import com.ioserver.dll.ClientDataBean;
import com.ioserver.dll.GlobalCilentBean;
import com.ioserver.dll.IOServerAPICilent;
import com.sun.jna.NativeLong;
import com.sun.jna.WString;
import lk.flag.FlagEntity;

import java.util.*;

/**
 * @Auther: Lk
 * @Date: 2018/12/26 0026 15:17
 * @Description:
 */
public class IOServiceImpl implements IOService {


    List<WString> vecAllTagName = new ArrayList<>();
    Map<NativeLong, WString> mapIdAndName = new HashMap<NativeLong, WString>();

    public static IOServerAPICilent client = new IOServerAPICilent();
    /*------------------------------------方法代码-----------------------------------------------*/
    /*------------------------------------方法代码-----------------------------------------------*/
    /*------------------------------------方法代码-----------------------------------------------*/


    // 连接ioserver 0表示成功，其他表示失败
    @Override
    public int funcConnect(String ip, String port) {
        if (!client.IOServerConnecton(ip, Integer.parseInt(port))) {
            client.IOServerDisConnect(client.getHandle());
            return -2; // 没有获取到句柄
        } else {
            int i = 0;
            while (i < 51) {
                if (i == 50) {
                    client.IOServerDisConnect(client.getHandle());
                    return -1;
                }
                if (funcIsConnect() == 0) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (client.RegisterReadCompleteCallbackFunc(client.getHandle()) != 0 // 注册异步读回调
                            || client.RegisterCollectValueCallbackFunc(client.getHandle()) != 0// 注册订阅回调(回调函数已经被封装，参数收集在databean中的对应map)
                            || funcStoreTagIdAndName() < 0) {
                        client.IOServerDisConnect(client.getHandle());
                        System.out.println("server failed！！！！！！！！！！！！！！！！！！");
                        return -1;
                    }
                    break;
                }
                i++;
            }

            //int tagId[]=new int[1];
            //tagId[0]=5001;
            //client.AsyncReadTagsValueByIDs(client.getHandle(), tagId, 1, 0);
            return 0;
        }
    }

    //返回获取的OPC列表tag的值  附带订阅
    @Override
    public List<WString> funcSubscribeAllTags() {
        if (funcIsConnect() != 0) {
            return null;
        }
        List<WString> vecSubscribeTagsName=new ArrayList<WString>();
        //channelProperties是io发过来的大类渠道 里面包含设备
        Struct_ChannelProperty[] channelProperties = client.BrowserChannels(client.getHandle(), new WString(""));


        for (int i_channel = 0; i_channel < channelProperties.length; i_channel++) {
            //deviceProperties设备属性
            Struct_DeviceProperty[] deviceProperties = client.BrowserDevices(client.getHandle(),
                    channelProperties[i_channel].ChannelName);


            for (int i_device = 0; i_device < deviceProperties.length; i_device++) {
                //tagProperties标签属性Tag
                Struct_TagProperty[] tagProperties = client.BrowserCollectTags(client.getHandle(),
                        deviceProperties[i_device].DeviceName);

                WString[] TagNames = new WString[tagProperties.length - 2];//排除不需要的两项数据
                int i_tagNames = 0;
                for (int i_tag = 0; i_tag < tagProperties.length; i_tag++) {
                    String wTagName = tagProperties[i_tag].TagName.toString();
                    if (wTagName.indexOf(new String("$")) == -1) {
                        TagNames[i_tagNames] = tagProperties[i_tag].TagName;
                        try {
                            vecSubscribeTagsName.add(tagProperties[i_tag].TagName);//标签Tag读取添加
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        i_tagNames++;
                    }
                }
                int[] TagIDs = new int[i_tagNames];
                for (int i = 0; i < i_tagNames; i++) {
                    TagIDs[i] = GlobalCilentBean.getInstance().getTagIDbyName(TagNames[i]);//TagID 例如：5001
                }
                //订阅变量  0：订阅成功，其他表示 注：不定阅就无法使用ClientDataBean的getTagValueByName等取值方法
               FlagEntity.flagSubscribeAll= client.SubscribeTagValuesChange(client.getHandle(), TagIDs, TagIDs.length);

            }
        }
        vecAllTagName=vecSubscribeTagsName;
        return vecSubscribeTagsName;
    }

    //根据tag获取其中的值
    @Override
    public Struct_TagInfo funcGetTagValue(WString TagName) {
        //ClientDataBean回调数据存储类
        /*
         * 回调数据存储就是存储客户端类执行某些操作后的数据：连接状态、工作状态、订阅变量值、
         * 异步读变量值、异步写结果
         * */
        //GlobalCilentBean全局客户端存储类
        /*
         *全局客户端存储是由单例存储多个客户端返回的数据，主要功能基本上是：增删改查功能
         */
        ClientDataBean dataBean = GlobalCilentBean.getInstance().getClientByHandle(client.getHandle());
        Struct_TagInfo structTagValue = dataBean.getTagValueByName(TagName);
        return structTagValue;
    }



    //同步读
    @Override
    public Struct_TagInfo_AddName[] getSyncReadValue(String[] strTagName) {
        if ((funcIsConnect() != 0) || (strTagName.length < 1)) {
            return null;
        }
        WString[] tagNames = new WString[strTagName.length];
        for (int i = 0; i < strTagName.length; i++) {
            tagNames[i] = new WString(strTagName[i]);
        }
        Struct_TagInfo_AddName[] structTagValue = client.SyncReadTagsValueReturnNames(client.getHandle(), tagNames,
                tagNames.length, 0);
        return structTagValue;
    }

    //异步读值
    @Override
    public Struct_TagInfo[] getAsyncReadValue(Vector<WString> vecAsyncReadTagsName) {
        WString  tagnames[]=new WString[vecAsyncReadTagsName.size()];
        int i=0;
        //tagnames为了获取异步的读值
        for( WString str:vecAsyncReadTagsName){
            tagnames[i]=str;
            i++;
        }

        if (vecAsyncReadTagsName.size() < 1) {
            return null;
        } else {
            //以变量名称异步读取变量值
            client.AsyncReadTagsValueByNames(client.getHandle(), tagnames, tagnames.length, 0);
        }

        //在此之前需要类似订阅一样
        Struct_TagInfo[] TagValueArray = new Struct_TagInfo[vecAsyncReadTagsName.size()];
        ClientDataBean dataBean = GlobalCilentBean.getInstance().getClientByHandle(client.getHandle());

        for (int i_tag = 0; i_tag < vecAsyncReadTagsName.size(); i_tag++) {
            for (int i_delay = 0; i_delay < 101; i_delay++) {
                if (i_delay == 100) {
                    TagValueArray[i_tag] = null;
                    break;
                }
                if (dataBean.getReadComTagValueByName(vecAsyncReadTagsName.get(i_tag)) != null) {
                    TagValueArray[i_tag] = dataBean.getReadComTagValueByName(vecAsyncReadTagsName.get(i_tag));
                    break;
                }

                try { // 如果数据没回来最多将阻塞1s，如果不喜欢这种阻塞延时，
                    Thread.sleep(10); // 请使用带接口的回调注册函数KSIOJAVAAPIRegisterReadCompleteCallbackFunc
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        return TagValueArray; // 存在null读取的时候忽略
    }

    //异步写
    @Override
    public int funcAsyncWrite(String tagName, String tagValue, int type) {
        if ((funcIsConnect() != 0) || (tagName == null) || (tagValue == null)) {
            return -1;
        }

        WString[] wsTagName = new WString[1];
        wsTagName[0] = new WString(tagName);
        if (type == 1) {
            List<Short> valuelist = new ArrayList<Short>();
            int intValue = Integer.parseInt(tagValue);
            valuelist.add((short) intValue);
            return client.AsyncWriteTagsValueByNames(client.getHandle(), valuelist, wsTagName);
        }
        if (type == 2) {
            List<Float> valuelist = new ArrayList<Float>();
            float intValue = Float.parseFloat(tagValue);
            valuelist.add((float) intValue);
            return client.AsyncWriteTagsValueByNames(client.getHandle(), valuelist, wsTagName);
        }
        if (type == 3) {
            List<String> valuelist = new ArrayList<String>();
            valuelist.add(tagValue);
            return client.AsyncWriteTagsValueByNames(client.getHandle(), valuelist, wsTagName);
        }
        return -2;
    }

    //同步写
    @Override
    public int funcSyncWrite(String tagName, String tagValue, int type) {
        if ((funcIsConnect() != 0) || (tagName == null) || (tagValue == null)) {
            return -1;
        }

        WString[] wsTagName = new WString[1];
        wsTagName[0] = new WString(tagName);
        if (type == 1) {
            List<Short> valuelist = new ArrayList<Short>();
            int intValue = Integer.parseInt(tagValue);
            valuelist.add((short) intValue);
            return client.SyncWriteTagsValueByNames(client.getHandle(), valuelist, wsTagName);
        }
        if (type == 2) {
            List<Float> valuelist = new ArrayList<Float>();
            float intValue = Float.parseFloat(tagValue);
            valuelist.add((float) intValue);
            return client.SyncWriteTagsValueByNames(client.getHandle(), valuelist, wsTagName);
        }
        if (type == 3) {
            List<String> valuelist = new ArrayList<String>();
            valuelist.add(tagValue);
            return client.SyncWriteTagsValueByNames(client.getHandle(), valuelist, wsTagName);
        }
        return -2;
    }

    //按id读TagName
    @Override
    public WString funcGetTagNameById(int id) {
        NativeLong tagId = new NativeLong(id);
        return mapIdAndName.get(tagId);
    }
    @Override
    public int funcAsyncRead(String[] strTagName) {
        if ((funcIsConnect() != 0) || (strTagName.length < 1)) {
            return -1;
        }

        WString[] TagNames = new WString[strTagName.length];
        for (int i = 0; i < TagNames.length; i++) {
            TagNames[i] = new WString(strTagName[i]);
        }
        //同步 按名字获取标签的值
        int result = client.AsyncReadTagsValueByNames(client.getHandle(), TagNames, TagNames.length, 0);
        return result;
    }



    //判断是否连接
    public int funcIsConnect() {
        if (client.IOServerIsConnected(client.getHandle()) == true) {
            if (client.getIOServerWorkStatus(client.getHandle()) == 0) {
                return 0;
            }
            else {
                return -1;	//IOServer没启动
            }
        }
        return -2;	//连接断开
    }

    public int funcStoreTagIdAndName() {

        Struct_TagProperty[] tagProperties = client.BrowserCollectTags(client.getHandle(), new WString(""));
        if (tagProperties.length == 0) {
            return -1;
        }
        for (int i = 0; i < tagProperties.length; i++) {

        }
        return tagProperties.length;
    }

    //注册连接状态变化回调接口
    public int funcRegisterConnectStatusChangedCallbackFunc(){
        short flag= -1;
        try {
            flag = client.RegisterConnectStatusChangedCallbackFunc(client.getHandle());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(flag!=0){
            return -1;
        }
        return 0;
    }

    //注册ioserver工作状态变化回调接口
    public int funcRegisterWorkStatusChangedCallbackFunc(){
        short flag= -1;
        try {
            flag = client. RegisterWorkStatusChangedCallbackFunc(client.getHandle());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(flag!=0){
            System.out.println("ioserver failed");
            return -1;
        }
        System.out.println("ioserver success");
        return 0;
    }


}
