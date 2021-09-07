package mq.rabbitmq.simple.fanout_exchange;

import com.rabbitmq.client.*;

import java.io.IOException;

public class SimpleRecv {

    // 队列名称
    private final static String QUEUE_NAME = "helloMQ";

    public static void main(String[] argv) throws Exception {

        // 打开连接和创建频道，与发送端一样
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("114.67.77.75");
        factory.setPort(5672);
        factory.setUsername("aidong");
        factory.setPassword("aiDong202O");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        //channel.exchangeDeclare("e")
        //声明队列，主要为了防止消息接收者先运行此程序，队列还不存在时创建队列。

        /*name:队列名字
        durable：是否持久化, 队列的声明默认是存放到内存中的，如果rabbitmq重启会丢失，如果想重启之后还存在就要使队列持久化，保存到Erlang自带的Mnesia数据库中，
        当rabbitmq重启之后会读取该数据库

        autoDelete：队列中的数据消费完成后是否自动删除队列，当最后一个消费者断开连接之后队列是否自动被删除，可以通过RabbitMQ Management，
        查看某个队列的消费者数量，当consumers = 0时队列就会自动删除

        exclusive：是否排外的，有两个作用，一：当连接关闭时connection.close()该队列是否会自动删除；
        二：该队列是否是私有的private，如果不是排外的，可以使用两个消费者都访问同一个队列，没有任何问题，如果是排外的，会对当前队列加锁，其他通道channel是不能访问的，
        如果强制访问会报异常：
        com.rabbitmq.client.ShutdownSignalException: channel error; protocol method:
        #method<channel.close>(reply-code=405, reply-text=RESOURCE_LOCKED - cannot obtain exclusive access to locked queue 'queue_name' in vhost '/',
        class-id=50, method-id=20)一般等于true的话用于一个队列只能有一个消费者来消费的场景

        noWait:是否等待服务器返回
        args：相关参数，目前一般为nil*/
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        //创建消费者
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" [x] Received '" + message + "'");

            }

            @Override
            public void handleConsumeOk(String consumerTag) {
                super.handleConsumeOk(consumerTag);
                System.out.println("handleConsumeOk"+consumerTag);
            }

            @Override
            public void handleCancelOk(String consumerTag) {
                super.handleCancelOk(consumerTag);
                System.out.println("handleCancelOk"+consumerTag);
            }

            @Override
            public void handleCancel(String consumerTag) throws IOException {
                super.handleCancel(consumerTag);
                System.out.println("handleCancel"+consumerTag);
            }

            @Override
            public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
                super.handleShutdownSignal(consumerTag, sig);
                System.out.println("handleShutdownSignal"+consumerTag);
            }

            @Override
            public void handleRecoverOk(String consumerTag) {
                super.handleRecoverOk(consumerTag);
                System.out.println("handleRecoverOk"+consumerTag);
            }

            @Override
            public Channel getChannel() {

                System.out.println(super.getChannel().getChannelNumber());
                return super.getChannel();
            }

            @Override
            public String getConsumerTag() {
                return super.getConsumerTag();
            }
        };
        channel.basicConsume(QUEUE_NAME, true, consumer);
    }
}
