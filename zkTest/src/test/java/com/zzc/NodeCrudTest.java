package com.zzc;

import java.util.concurrent.CountDownLatch;

import junit.framework.TestCase;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.junit.Test;

public class NodeCrudTest extends TestCase{
	private static final String HOSTS = "127.0.0.1:2181,127.0.0.1:2182";
	
	public void testQueryNode(){
		try {
			final CountDownLatch countDownLatch = new CountDownLatch(1);
			
			NodeCrud nodeCrud = new NodeCrud(HOSTS);
			nodeCrud.queryNode("/testNode1", new Watcher(){
				@Override
				public void process(WatchedEvent event) {
					System.out.printf("zk回调,数据发生变化,eventType:%s eventState:%s\n", event.getType().toString(),event.getState().toString());
					countDownLatch.countDown();
				}
			});
			
			countDownLatch.await();
			nodeCrud.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testCreateNode(){
		try {
			NodeCrud nodeCrud = new NodeCrud(HOSTS);
			nodeCrud.createNode("/testNode1", "hello world");
			nodeCrud.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void testSetNode(){
		try{
			NodeCrud nodeCrud = new NodeCrud(HOSTS);
			nodeCrud.setNode("/testNode1", "hello world2", -1);
			Thread.sleep(Long.MAX_VALUE);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
