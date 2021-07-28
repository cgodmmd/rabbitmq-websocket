package com.fsl.springbootrabbitmq;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringbootRabbitmqApplicationTests {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	/**
	 * 1. 两个线程分别打印26个英文字母的元音（a,e,i,o,u）和辅音（其他）,按字母顺序输出
	 * 2. 一条N个格子组成的直线道路，每次可以前进1格或2格；设计算法计算有多少种方式走到终点？
	 * 3. 实现一个能够生产不同类型手机（Android，iPhone）的工厂，考虑未来可能的扩展
	 */
	@Test
	public void contextLoads() {
		int n = 5;
		Integer step = getStep(n);
		System.out.println("step = "+ step);
	}


	public Integer getStep(Integer n){
		int result=1;//存放结果
		int b = n/3;
		while(b>0) {
			result *= 3;
			b--;
		}
		int k = n%3;
		return result + k;
	}


//	public void queue() {
//		ConcurrentLinkedQueue queue = new ConcurrentLinkedQueue();
//		for (int i = 0; i < 10; i++) {
//			//向队列尾部插入数值
//			queue.offer(i);
//		}
//		logger.info("当前队列是否为空？" + queue.isEmpty());
//		logger.info("从当前队列中取出数值：" + queue.poll());
//		logger.info("当前队列是否为空？" + queue.isEmpty());
//		logger.info("删除已存在元素：" + queue.remove(1));
//		logger.info("从当前队列中取出数值：" + queue.poll());
//		logger.info("当前队列的长度：" + queue.size());
//		//如果队列中包含指定元素，返回ture
//		logger.info("队列中是否包含3：" + queue.contains(3));
////		Object[] objects = queue.toArray();
//		logger.info("将队列以数组的形式返回：" + queue.toArray());
//
//		//以恰当的顺序进行迭代
//		Iterator<String> iterator = queue.iterator();
//		while (iterator.hasNext()){
//			System.out.println(iterator.next());
//		}
//	}

}
