package com.fsl.springbootrabbitmq.consumer;


import com.fsl.springbootrabbitmq.config.RabbitConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author William 2021年7月16日15:37:29
 * 准备了三个类MsgReceiverB_A，MsgReceiverB_B，MsgReceiverB_C，来消费队列B当中的消息，消费的顺序是负载均衡的
 * 消费的顺序是无序的，也就是不保证先进来的消息先被消费
 */
@Component
@RabbitListener(queues = RabbitConfig.QUEUE_B)
public class MsgReceiverB_A {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RabbitHandler
    public void process(String content) {
        logger.info("处理器A接收处理队列B当中的消息： {}" , content);
    }

}
