package com.fsl.springbootrabbitmq.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author 2019年5月10日11:08:16
 */
@Component
@ServerEndpoint("/websocket/{username}")
public class WebSocket {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    //创建ConcurrentLinkedQueue消息队列
    ConcurrentLinkedQueue queue = new ConcurrentLinkedQueue();
    /**
     * 在线人数
     */
    public static int onlineNumber = 0;
    /**
     * 以用户的姓名为key，WebSocket为对象保存起来
     */
    private static Map<String, WebSocket> clients = new ConcurrentHashMap<String, WebSocket>();
    /**
     * 会话
     */
    private Session session;
    /**
     * 用户名称
     */
    private String username;
    /**
     * 建立连接
     *
     * @param
     */

    public  WebSocket ()
    {


        System.out.println("hello constructor");
    }

    @OnOpen
    public void onOpen(@PathParam("username") String username, Session session)
    {
        onlineNumber++;
        logger.info("现在来连接的客户id："+session.getId()+"用户名："+username);

        this.username = username;
        this.session = session;
        logger.info("有新连接加入！ 当前在线人数" + onlineNumber);
        try {
            //messageType 1代表上线 2代表下线 3代表在线名单 4代表普通消息
            //先给所有人发送通知，说我上线了
            Map<String,Object> map1 = Maps.newHashMap();
            map1.put("messageType",1);
            map1.put("username",username);

            //将map1放入队列

            sendMessageAll(JSON.toJSONString(map1),username);

            //把自己的信息加入到map当中去
            clients.put(username, this);
            //给自己发一条消息：告诉自己现在都有谁在线
            Map<String,Object> map2 = Maps.newHashMap();
            map2.put("messageType",3);
            //移除掉自己
            Set<String> set = clients.keySet();
            map2.put("onlineUsers",set);
            logger.info("目前通道的session："+session.getOpenSessions());
            sendMessageTo(JSON.toJSONString(map2),username);
        }
        catch (Exception e){
            logger.info(username+"上线的时候通知所有人发生了错误");
        }



    }

    @OnError
    public void onError(Session session, Throwable error) {
        logger.info("服务端发生了错误"+error.getMessage());
        //error.printStackTrace();
    }
    /**
     * 连接关闭
     */
    @OnClose
    public void onClose()
    {
        onlineNumber--;
        //webSockets.remove(this);
        clients.remove(username);
        try {
            //messageType 1代表上线 2代表下线 3代表在线名单  4代表普通消息
            Map<String,Object> map1 = Maps.newHashMap();
            map1.put("messageType",2);
            map1.put("onlineUsers",clients.keySet());
            map1.put("username",username);
            sendMessageAll(JSON.toJSONString(map1),username);
        }
        catch (Exception e){
            logger.info(username+"下线的时候通知所有人发生了错误");
        }
        logger.info("有连接关闭！ 当前在线人数" + onlineNumber);
    }

    /**
     * 收到客户端的消息
     *
     * @param message 消息
     * @param session 会话
     */
    @OnMessage
    public void onMessage(String message, Session session)
    {
        try {
            logger.info("来自客户端消息：" + message+"客户端的id是："+session.getId());
            JSONObject jsonObject = JSON.parseObject(message);
            String textMessage = jsonObject.getString("message");
            String fromusername = jsonObject.getString("username");
            String tousername = jsonObject.getString("to");
            //如果不是发给所有，那么就发给某一个人
            //messageType 1代表上线 2代表下线 3代表在线名单  4代表普通消息
            Map<String,Object> map1 = Maps.newHashMap();
            map1.put("messageType",4);
            map1.put("textMessage",textMessage);
            map1.put("fromusername",fromusername);
            logger.info("当前的session通道："+session.getOpenSessions());
            if(tousername.equals("All")){
                map1.put("tousername","所有人");

                List<CompletableFuture<Boolean>> futures = new ArrayList<>();

                int count = 0 ;
                while (count<3){
//                    futures.add(CompletableFuture.supplyAsync(() -> queue.offer(map1)));
                    Random random = new Random();
                    int number=random.nextInt(400);
//                    System.out.println("随机数是"+number);
                    sendMessageAll(JSON.toJSONString(map1),fromusername);
//                    if (queue.isEmpty() == false){
//                        sendMessageAll(JSON.toJSONString(queue.poll()),fromusername);
//                    }
                    count++;
                    try {
                        TimeUnit.MILLISECONDS.sleep(number);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
            else{
                map1.put("tousername",tousername);
                int count = 0 ;
                List<CompletableFuture<Boolean>> futures = new ArrayList<>();
                while (count<10){
                    Random random = new Random();
                    int number=random.nextInt(200);

                    futures.add(CompletableFuture.supplyAsync(() -> queue.offer(map1)));

                    if (queue.isEmpty() == false){
                        sendMessageTo(JSON.toJSONString(queue.poll()), fromusername);
                    }
//                    sendMessageTo(JSON.toJSONString(map1), tousername);
                    count++;
//                    try {
//                        TimeUnit.MILLISECONDS.sleep(number);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                }
            }
        }
        catch (Exception e){
            logger.info("发生了错误了"+e);
        }

    }

    public void sendMessageTo(String message, String ToUserName) throws IOException {
        for (WebSocket item : clients.values()) {
            if (item.username.equals(ToUserName) ) {
                logger.info(item.username+"正在发送的消息为："+message);
//                synchronized(item.session) {
                    item.session.getBasicRemote().sendText(message);
//                }
                break;
            }
        }
    }

    /**
     *getBasicRemote() 和 getAsyncRemote() 的本质区别是同步和异步
     * getBasicRemote()是同步，当第一行的消息未发送成功，则第二行的信息会被阻塞，会抛出IllegalStateException异常
     * 但是某种情况下必须使用同步发送信息
     * */
    public void sendMessageAll(String message,String FromUserName) throws IOException {
        for (WebSocket item : clients.values()) {
//            System.out.println(item.username+""+message);
            logger.info(item.username+"正在发送的消息为："+message);
//            synchronized(item.session) {
                item.session.getBasicRemote().sendText(message);
//            }
        }
    }


    public static synchronized int getOnlineCount() {
        return onlineNumber;
    }

}
