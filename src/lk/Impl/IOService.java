package lk.Impl;

import com.ioserver.bean.Struct_TagInfo;
import com.ioserver.bean.Struct_TagInfo_AddName;
import com.sun.jna.WString;

import java.util.List;
import java.util.Vector;

/**
 * @Auther: Lk
 * @Date: 2018/12/26 0026 15:17
 * @Description:
 */
public interface IOService {




    public int funcConnect(String ip, String port);

    //订阅全部并获取标签名
    public List<WString> funcSubscribeAllTags();

    //根据标签名获取值
    public Struct_TagInfo funcGetTagValue(WString TagName);

    //异步读TagsValue byTagName
    public int funcAsyncRead(String[] strTagName);
    //按id读TagName（搁置）
    public WString funcGetTagNameById(int id);


    //同步读值
    public Struct_TagInfo_AddName[] getSyncReadValue(String[] strTagName);

    //异步读值
    public Struct_TagInfo[] getAsyncReadValue(Vector<WString> vecAsyncReadTagsName);
        /**--------------------------------------------------------------*/
    //同步写数据
    public int funcSyncWrite(String tagName, String tagValue, int type);

    //异步写数据
    public int funcAsyncWrite(String tagName, String tagValue, int type);

}
