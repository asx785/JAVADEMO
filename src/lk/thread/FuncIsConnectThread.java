package lk.thread;

import com.ioserver.dll.IOServerAPICilent;
import lk.Impl.IOServiceImpl;
import lk.flag.FlagEntity;


/**
 * @Auther: Lk
 * @Date: 2018/12/28 0028 11:14
 * @Description: 回调监听ioservice的状态
 */
public class FuncIsConnectThread implements Runnable{
    public FuncIsConnectThread( int hander){
        //super("funcIsConnectThread");
        this.hander=hander;
    }

    private IOServiceImpl ioService=new IOServiceImpl();
    IOServerAPICilent client = new IOServerAPICilent();
    int hander;
    int reQuestConnectNumber;
    int startQuestConnectNumber;

    @Override
    public void run() {
        boolean flag=true;
        FlagEntity flagEntity;
        reQuestConnectNumber=1;
        startQuestConnectNumber=1;
        while (true){
            System.out.println("工作状态:"+client.getIOServerWorkStatus(hander));
            /*
            * getIOServerWorkStatus() 0：表示设备正常；1：表示系统控制挂起； -1:表示未连接
            */
            switch (client.getIOServerWorkStatus(hander)){
                case 1:{
                    if(flag){
                        System.out.println("-------------连接中断:数据不再更新-------------");
                        flag=false;//确保不重复提示
                        FlagEntity.flagConnect=-1;
                    }
                    System.out.println("-------------尝试重新连接Loading---"+ reQuestConnectNumber++ +"次--------");
                    try {
                        Thread.currentThread().sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case 0:{
                    if(!flag){
                        System.out.println("-------------重连成功:数据继续更新-------------");
                        flag=true;
                        FlagEntity.flagConnect=0;
                    }
                    try {
                        Thread.currentThread().sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case -1:{
                    System.out.println("-------------尝试开始连接，连接次数"+ startQuestConnectNumber++ +"次--------");
                    try {
                        Thread.currentThread().sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }

        }


    }
}
