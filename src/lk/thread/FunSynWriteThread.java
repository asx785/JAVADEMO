package lk.thread;

/**
 * @Auther: Lk
 * @Date: 2018/12/29 0029 15:52
 * @Description: 监听Redis对IOservice的同步写操作
 */
public class FunSynWriteThread implements Runnable {

    public FunSynWriteThread(){
        Thread.currentThread().setName("SynWriteThread");
    }

    @Override
    public void run() {
        while (true){

        }
    }


}
