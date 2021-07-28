package com.fsl.springbootrabbitmq.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author William 2021年7月16日15:37:29
 */
@Controller
public class ConcurrentLinkedQueueController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    //创建ConcurrentLinkedQueue消息队列
    ConcurrentLinkedQueue queue = new ConcurrentLinkedQueue();

    @ResponseBody
    @RequestMapping(value = "/queue/publisher",method = RequestMethod.GET)
    public Object queueTest() throws Exception{

        //创建CompletableFuture，异步的方式，通过回调的方式处理结果
        CompletableFuture<Long> future = CompletableFuture.supplyAsync(() -> {
            try {
                for (int i = 0; i < 20; i++) {
                    //向队列尾部插入数值
                    //插入值的方式使用CompletableFuture
                    queue.offer(i);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("run end ...");
            return System.currentTimeMillis();
        });
        future.get();
//        future.complete(100l);
//		Object[] objects = queue.toArray();
        return consumer();
    }
    @ResponseBody
    @RequestMapping(value = "/queue/consumer",method = RequestMethod.GET)
    public Object consumer(){
        logger.info("当前队列是否为空？" + queue.isEmpty());
        logger.info("从当前队列中取出数值：" + queue.poll());
        logger.info("当前队列是否为空？" + queue.isEmpty());
        logger.info("删除已存在元素：" + queue.remove(3));
        logger.info("从当前队列中取出数值：" + queue.poll());
        logger.info("当前队列的长度：" + queue.size());
        //如果队列中包含指定元素，返回ture
        logger.info("队列中是否包含3：" + queue.contains(3));

        Object[] objects = queue.toArray();
        return objects;
    }


    @ResponseBody
    @RequestMapping(value = "/queue/publisher2",method = RequestMethod.GET)
    public Object queueTest1() throws Exception{

        //创建CompletableFuture，异步的方式，通过回调的方式处理结果

        List<CompletableFuture<Boolean>> futures = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            int finalI = i;
            futures.add(CompletableFuture.supplyAsync(() -> queue.offer(finalI)));
        }
        Object[] objects = queue.toArray();

        return objects;
    }

    //循环从队列取值
    @ResponseBody
    @RequestMapping(value = "/queue/poll",method = RequestMethod.GET)
    public  Object clean() {
        logger.info("当前队列是否为空？" + queue.isEmpty());
        while (queue.isEmpty() == false) {
            logger.info("从当前队列中取出数值：" + queue.poll());
        }
        logger.info("当前队列是否为空？" + queue.isEmpty());
        Object[] objects = queue.toArray();
        return objects;
    }


}
