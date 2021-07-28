package com.fsl.springbootrabbitmq.controller;

import com.fsl.springbootrabbitmq.producer.MsgProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author William 2021年7月16日15:37:29
 */
@Controller
public class RabbitController {

    @Autowired
    private MsgProducer msgProducer;

    /**
     * @author William 2021年7月16日15:37:29
     * 发送消息到队列A
     * @return
     */
    @ResponseBody
    @RequestMapping("/rabbitmq/sendMsg")
    public String sendMsg(){
        int msgNum = 5;
        for(int i=0;i<msgNum;i++) {
            msgProducer.sendMsg("这是发送的第"+i+"条消息");
        }
        return "success";
    }


    /**
     * 发送消息到队列B
     * @return
     */
    @ResponseBody
    @RequestMapping("/rabbitmq/sendMsgToQueueB")
    public String sendMsgToQueueB(){
        int msgNum = 100;
        for(int i=1;i<=msgNum;i++) {
            msgProducer.sendMsgToQueueB("这是发送的第"+i+"条消息");
        }
        return "success";
    }


    /**
     * 发送消息到队列B
     * @return
     */
    @ResponseBody
    @RequestMapping("/rabbitmq/sendMsgAll")
    public String sendMsgAll(){
        int msgNum = 10;
        for(int i=1;i<=msgNum;i++) {
            msgProducer.sendAll("这是发送的第"+i+"条消息");
        }
        return "success";
    }
}
