package mq.rabbitmq.simple;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * MqttClient demo service
 *
 * @author admin
 */
public class MyMqttService {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(MyMqttService.class);

    private MqttClient client;
    private String defaultTopic;

    /**
     * Builder模式构造实例
     */
    public static class Builder {
        private String host;
        private String userName;
        private String passWord;
        private String clientId;
        private String defaultTopic = "MyMqttTopic";
        private MqttCallback callback;
        private boolean cleanSession;

        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public Builder userName(String userName) {
            this.userName = userName;
            return this;
        }

        public Builder passWord(String passWord) {
            this.passWord = passWord;
            return this;
        }

        public Builder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder defaultTopic(String defaultTopic) {
            this.defaultTopic = defaultTopic;
            return this;
        }

        public Builder callback(MqttCallback callback) {
            this.callback = callback;
            return this;
        }

        public Builder cleanSession(boolean cleanSession) {
            this.cleanSession = cleanSession;
            return this;
        }

        public MyMqttService build() {
            return new MyMqttService(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private MyMqttService(Builder builder) {
        defaultTopic = builder.defaultTopic;
        final ExecutorService executorService = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), r -> {
            Thread t = new Thread(r);
            t.setName("MyMQTT线程");
            return t;
        });
        try {
            //id应该保持唯一性
            client = new MqttClient(builder.host, builder.clientId, new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(builder.cleanSession);
            options.setUserName(builder.userName);
            options.setPassword(builder.passWord.toCharArray());
            options.setConnectionTimeout(10);
            options.setKeepAliveInterval(20);
            if (builder.callback == null) {
                client.setCallback(new MqttCallbackExtended() {

                    @Override
                    public void connectComplete(boolean reconnect, String serveruri) {
                        // 客户端连接成功后就需要尽快订阅需要的 topic
                        logger.debug(builder.clientId + " connectComplete reconnect=" + reconnect + ", serveruri=" + serveruri);

                        // 参考阿里云mqtt文档  https://www.alibabacloud.com/help/zh/doc-detail/42420.htm?spm=a2c63.p38356.b99.12.87851d06uHImcQ
                        /*
                         * cleanSession=true：客户端再次上线时，将不再关心之前所有的订阅关系以及离线消息。
                         * cleanSession=false：客户端再次上线时，还需要处理之前的离线消息，而之前的订阅关系也会持续生效
                         * QoS0 代表最多分发一次
                         * QoS1 代表至少达到一次
                         *   ----------------------------------------------------------------------------------
                         *   |QoS级别   |	cleanSession=true	                |cleanSession=false             |
                         *   | QoS0	    |  无离线消息，在线消息只尝试推一次。	    |无离线消息，在线消息只尝试推一次。 |
                         *   | QoS1	    |  无离线消息，在线消息保证可达。	        |有离线消息，所有消息保证可达。     |
                         *  ----------------------------------------------------------------------------------
                         */
                        final String[] topicFilter = {"test/zyh/simple/"};
                        final int[] qos = {2};
                        executorService.submit(() -> subscribe(topicFilter, qos));
                    }

                    @Override
                    public void connectionLost(Throwable arg0) {
                        logger.debug(builder.clientId + " connectionLost " + arg0);
                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken arg0) {

                        //logger.debug(builder.clientId + " deliveryComplete " + arg0);
                    }

                    @Override
                    public void messageArrived(String arg0, MqttMessage arg1) {

                       // logger.debug(builder.clientId + " messageArrived: " + arg1.toString());
                    }
                });
            } else {
                client.setCallback(builder.callback);
            }
            client.connect(options);
        } catch (MqttException e) {
            logger.error("MyMqttService 初始化异常 ", e);
        }
    }

    /**
     * 发送消息，默认主题
     *
     * @param msg 消息
     */
    public void sendMessage(String msg) {
        sendMessage(defaultTopic, msg);
    }

    /**
     * 发送指定主题消息
     *
     * @param topic 主题
     * @param msg   消息
     */
    public void sendMessage(String topic, String msg) {
        try {
            MqttMessage message = new MqttMessage(msg.getBytes());
            message.setQos(2);
            message.setRetained(true);
            client.publish(topic, message);
            logger.info("发送消息成功 topic={},msg={}", topic, msg);
        } catch (MqttException e) {
            logger.error("发送主题消息异常 topic={} ,msg={}", topic, msg, e);
        }
    }

    /**
     * 订阅主题
     *
     * @param topicFilters 主题名称
     * @param qos          规则
     */
    public void subscribe(String[] topicFilters, int[] qos) {
        try {
            logger.info("开始订阅主题"+topicFilters[0]);
            client.subscribe(topicFilters, qos);
            logger.info("完成订阅主题"+topicFilters[0]);
            for (int i = 0; i < topicFilters.length; i++) {
                logger.info("subscribe success topicFilters={}, qos={}", topicFilters[i], qos[i]);
            }

        } catch (MqttException e) {
            logger.error("订阅主题", e);
        }
    }

    /**
     * 取消订阅某个主题
     *
     * @param topicFilters 主题名称
     */
    public void unsubscribe(String[] topicFilters) {
        try {
            client.unsubscribe(topicFilters);
        } catch (MqttException e) {
            logger.error("取消订阅某个主题", e);
        }
    }

    public void closeClient(boolean force) {
        try {
            client.close(force);
        } catch (MqttException e) {
            logger.error("closeClient异常", e);
        }
    }
}
