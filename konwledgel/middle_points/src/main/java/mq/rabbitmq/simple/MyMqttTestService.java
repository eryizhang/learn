package mq.rabbitmq.simple;

import java.util.Random;

public class MyMqttTestService {

    public static void main(String[] args) throws InterruptedException {

        /*MymqttRecvService service1=MymqttRecvService.builder()
                .host("tcp://192.168.0.124:1883")
                .userName("guest")
                .passWord("guest")

                .clientId("myclientid_100012")
                .defaultTopic("$queue/device/event/#")
                .cleanSession(true).build();*/

        MyMqttService service = MyMqttService.builder()
                .host("tcp://114.67.77.75:5672")
                .userName("aidong")
                .passWord("aiDong202O")

                .clientId("myclientid_10001")

                .defaultTopic("test/zyh/simple/")
                .cleanSession(true).build();


            int ran=new Random().nextInt();
            Thread.sleep(1000L);
            //service.sendMessage("/aiot/smartlink/12345678/thing/event/0x107/post","这是java后端发送的消息"+ran);
    }
}
