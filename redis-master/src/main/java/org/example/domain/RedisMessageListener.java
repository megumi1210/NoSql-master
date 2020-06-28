package org.example.domain;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;


/**
 * 订阅发布的消息监听器
 * @author chenj
 */
@SuppressWarnings("all")
public class RedisMessageListener implements MessageListener {


    private RedisTemplate redisTemplate;


    @Override
    public void onMessage(Message message, byte[] bytes) {
        //获取消息
        byte[] body = message.getBody();
        //使用值序列化器转换
        String  msgBody = (String) redisTemplate.getValueSerializer().deserialize(body);
        System.out.println(msgBody);
        // 获取 channel
        byte[] channel =  message.getChannel();
        //使用字符串序列化器转换
        String  channelStr = (String) redisTemplate.getStringSerializer().deserialize(channel);
        System.out.println(channelStr);
        //渠道名称转换
        String  byteStr = new String(bytes);
        System.out.println(byteStr);
    }


    public RedisTemplate getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
}
