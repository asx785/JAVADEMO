package lk.redis.redisSubscribe;

import redis.clients.jedis.JedisPubSub;

/**
 * @Auther: Lk
 * @Date: 2018/12/28 0028 09:13
 * @Description:
 */
public class Subscriber extends JedisPubSub {
    //订阅者名字
    private String subScriberName;

    public Subscriber(String name){subScriberName=name;}
    @Override
    public void onMessage(String channel, String message) {       //收到消息会调用
        System.out.println(String.format("你订阅的频道: %s 有新消息:%s", channel, message));
    }
    @Override
    public void onSubscribe(String channel, int subscribedChannels) {    //订阅了频道会调用
        System.out.println(String.format("成功订阅频道, 频道名: %s, 订阅频道数: %d",
                channel, subscribedChannels));
    }
    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {   //取消订阅 会调用
        System.out.println(String.format("unsubscribe redis channel, channel %s, subscribedChannels %d",
                channel, subscribedChannels));

    }
}
