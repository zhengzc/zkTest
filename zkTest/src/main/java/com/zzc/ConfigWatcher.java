package com.zzc;

import java.io.IOException;
import java.util.Random;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooKeeper;

/**
 * 统一的配置服务
 * 
 * @author zhengzhichao
 * 
 */
public class ConfigWatcher implements Watcher {
	private ZooKeeper zk;
	private String nodePath;
	
	public ConfigWatcher(String hosts,String nodePath) throws InterruptedException, IOException{
		ConnectionWatcher connectionWatcher = new ConnectionWatcher(hosts);
		this.zk = connectionWatcher.getZk();
		this.nodePath = nodePath;
	}
	
	@Override
	public void process(WatchedEvent event) {
		System.out.printf("配置回调,eventType:%s eventState:%s\n", event.getType().toString(),event.getState().toString());
		if(event.getType() == EventType.NodeDataChanged){
			try {
				displayConfig();
			} catch (KeeperException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
			}
		}else{
			System.out.printf("配置发生变化eventType=%s\n",event.getType());
		}
	}

	/**
	 * 显示最新配置,并且重新设置观察者
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public void displayConfig() throws KeeperException, InterruptedException{
		byte[] data = this.zk.getData(nodePath, this, null);
		System.out.printf("最新的配置为:%s\n", new String(data));
	}
	
	/**
	 * 模拟更新配置
	 */
	public void updateConfig(){
		final Random random = new Random();
		Thread t = new Thread(){
			@Override
			public void run() {
				while(true){
					String num = random.nextInt(100)+"";
					System.out.printf("random num is %s\n", num);
					try {
						Thread.sleep(5000);
						zk.setData(nodePath, num.getBytes(), -1);
					} catch (KeeperException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		
		t.start();
	}
}
