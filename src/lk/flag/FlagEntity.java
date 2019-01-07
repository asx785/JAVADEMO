package lk.flag;

/**
 * @Auther: Lk
 * @Date: 2018/12/28 0028 11:42
 * @Description:
 */
public class FlagEntity {
    /*标签*/
    public static int flagConnect = -1;//连接标签
    public static int flagSubscribeAll = -1;//更新显示数据标签

    public static int flagSyncReadComplete = -1;//异步读完成标签
    public static int flagAsyncReadComplete = -1;//同步读完成标签

    public static int chooseAsyncReadWrite = 0; //选中异步读写
    public static int chooseSyncReadWrite = 0; //选中同步读写

    public static int chooseSyncRdBtn = 0;// 1,short 2,float 3,string 数据类型选择
    public static int chosseAsyncRdBtn = 0;//写
}
