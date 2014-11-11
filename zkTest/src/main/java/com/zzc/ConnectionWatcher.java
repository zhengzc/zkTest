package com.zzc;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.KeeperState;

/**
 * 创建zk连接辅助类
 * @author zhengzhichao
 *
 */
public class ConnectionWatcher implements Watcher {

	private static final int SESSION_TIMEOUT = 4000;
	protected ZooKeeper zk;
	
	private CountDownLatch countDownLatch = new CountDownLatch(1);
	
	public ConnectionWatcher(String hosts) throws IOException,InterruptedException{
		System.out.printf("开始创建zk连接,hosts:%s\n", hosts);
		zk = new ZooKeeper(hosts, SESSION_TIMEOUT, this);
		countDownLatch.await();
	}
	
	@Override
	public void process(WatchedEvent event) {
		System.out.printf("zk回调事件,eventType:%s eventState:%s\n", event.getType().toString(),event.getState().toString());
		if(event.getState() == KeeperState.SyncConnected){
			countDownLatch.countDown();
		}
	}
	
	public void close() throws InterruptedException{
		System.out.println("关闭连接");
		zk.close();
	}

	public ZooKeeper getZk() {
		return zk;
	}

}
