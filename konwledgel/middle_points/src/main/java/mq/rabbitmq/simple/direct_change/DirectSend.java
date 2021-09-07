package mq.rabbitmq.simple.direct_change;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.concurrent.TimeoutException;

public class DirectSend {

    //队列名称
    private final static String QUEUE_NAME = "helloMQ";

    private final static String EXCHANGE_NAME = "test_exchange_direct";

    private final static String QUEUQ_EXCHANGE_NAME = "directMQ";

    public static void main(String[] argv) throws java.io.IOException, TimeoutException
    {
        /**
         * 创建连接连接到MabbitMQ
         */
        ConnectionFactory factory = new ConnectionFactory();
        //设置MabbitMQ所在主机ip或者主机名
        factory.setHost("114.67.77.75");
        factory.setPort(5672);
        factory.setUsername("aidong");
        factory.setPassword("aiDong202O");
        //创建一个连接
        Connection connection = factory.newConnection();
        //创建一个频道
        Channel channel = connection.createChannel();

        Channel channel1 = connection.createChannel();
        //指定一个队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        channel1.queueDeclare(QUEUQ_EXCHANGE_NAME, false, false, false, null);
        //发送的消息
        String message = "hello world!";

        String message1 = "hello world!------------";
        //往队列中发出一条消息

       /*
       层层调用后最终调用的是 public void basicPublish(String exchange, String routingKey, boolean mandatory, boolean immediate, BasicProperties props, byte[] body)
       routingKey：路由键，#匹配0个或多个单词，*匹配一个单词，在topic exchange做消息转发用

        mandatory：true：如果exchange根据自身类型和消息routeKey无法找到一个符合条件的queue，那么会调用basic.return方法将消息返还给生产者。false：出现上述情形broker会直接将消息扔掉

        immediate：true：如果exchange在将消息route到queue(s)时发现对应的queue上没有消费者，那么这条消息不会放入队列中。
        当与消息routeKey关联的所有queue(一个或多个)都没有消费者时，该消息会通过basic.return方法返还给生产者。

        BasicProperties ：需要注意的是BasicProperties.deliveryMode，0:不持久化 1：持久化
        这里指的是消息的持久化，配合channel(durable=true),queue(durable)可以实现，即使服务器宕机，消息仍然保留

        mandatory标志告诉服务器至少将该消息route到一个队列中，否则将消息返还给生产者；immediate标志告诉服务器如果该消息关联的queue上有消费者，则马上将消息投递给它，如果所有queue都没有消费者，直接把消息返还给生产者，不用将消息入队列等待消费者了。
        */
        channel.basicPublish("",QUEUE_NAME,false,false,null,message.getBytes());

        channel1.basicPublish("bind",QUEUQ_EXCHANGE_NAME,false,false,null,message1.getBytes());
        //channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
        System.out.println(" [x] Sent '" + message + "'");
        //关闭频道和连接
        connection.close();
    }
}
