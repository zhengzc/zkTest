package com.zzc;

import java.io.IOException;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

/**
 * 分布式锁实现,避免羊群效应
 * @author zhengzhichao
 *
 */
public class DistributedLock implements Watcher {
	/**
	 * 序号,模拟锁顺序用的
	 */
	private int num;
	/**
	 * 模拟操作耗时,凸显锁顺序
	 */
	private long sleepTime;
	
	/**
	 * zk上面的队列父节点
	 */
	private String distributedLockNodePath = "/distributedLock";
	/**
	 * 客户端注册的时候使用的名字
	 */
	private String nodeName;
	
	/**
	 * 当前锁
	 */
	private String myLock;
	/**
	 * 记录上一个节点路径
	 */
	private String preNode;
	/**
	 * 是否获取到锁
	 */
	Boolean getLock = true;
	
	private ZooKeeper zk;
	
	public DistributedLock(String hosts,int num,long sleepTime) throws IOException, InterruptedException{
		this.num = num;
		this.sleepTime = sleepTime;
		
		ConnectionWatcher connectionWatcher = new ConnectionWatcher(hosts);
		this.zk = connectionWatcher.getZk();
		this.nodeName = "client-"+this.zk.getSessionId()+"-";
	}
	
	/**
	 * 申请锁
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public void applyLock() throws KeeperException, InterruptedException{
		Thread.sleep(this.sleepTime);
		
		//添加一个节点
		this.myLock = this.zk.create(this.distributedLockNodePath+"/"+this.nodeName, null, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
		System.out.printf("%s 添加节点:%s\n", this.num,myLock);
		
		//获取子节点列表,并判断当前申请节点是否为最小节点
		List<String> children = this.zk.getChildren(this.distributedLockNodePath, false);
		
		//有序字节点列表
		SortedSet<String> sortedChildren = new TreeSet<>();
		for(String temp : children){
			sortedChildren.add(this.distributedLockNodePath+"/"+temp);
		}
		//小于当前节点的字节点
		SortedSet<String> lessThanChildren = sortedChildren.headSet(this.myLock);
		
		
		if(lessThanChildren.isEmpty()){//说明已经获取到当前锁
			this.doSomething();
		}else{//没有获取到当前锁
			//将当前锁的前一个节点设置观察者,防止羊群效应发生
			String preNode = lessThanChildren.last();
			System.out.printf("%s 前一个节点为%s\n",this.num,preNode);
			this.zk.exists(preNode, this);
		}
		/*for(int i = 0 ; i < sortedChildren.size() ; i++){
			System.out.printf("%s 当前节点为%s\n",this.num,temp);
			if(myLock.compareTo(sortedChildren) > 0){//如果不是最小的节点
				getLock = false;//则没有获取到锁
				
				//我们先默认getChildren返回的是按照升序排列的,则我们找到当前锁的前一个node,设置观察者,防止羊群效应发生
				int index = sortedChildren.lastIndexOf(temp);
				String preNode = sortedChildren.get(index);
				System.out.printf("%s 前一个节点为%s\n",preNode);
				this.zk.exists(preNode, this);
				
				break;
			}
			getLock = true;
		}
		
		if(getLock){//当前节点已经是最小的,已经获取到锁
			this.doSomething();
		}*/
	}
	
	@Override
	public void process(WatchedEvent event) {
		System.out.printf("%s zk回调事件,eventType:%s eventState:%s\n",this.num,event.getType().toString(),event.getState().toString());
		try {
			if(event.getType() == EventType.NodeDeleted){//删除节点才会触发获取锁逻辑
				/*//以防万一,我们再去所有字节点判断一下是否为最小节点
				List<String> childs = this.zk.getChildren(this.distributedLockNodePath, false);
				for(String temp : childs){
					System.out.printf("%s 当前节点为%s\n",temp);
					if(myLock.compareTo(temp) > 0){//如果不是最小的节点
						getLock = false;//则没有获取到锁
						break;
					}
				}
				getLock = true;*/
				
				List<String> children = this.zk.getChildren(this.distributedLockNodePath, false);
				//有序字节点列表
				SortedSet<String> sortedChildren = new TreeSet<>();
				for(String temp : children){
					sortedChildren.add(this.distributedLockNodePath+"/"+temp);
				}
				//小于当前节点的字节点
				SortedSet<String> lessThanChildren = sortedChildren.headSet(this.myLock);
				
				
				if(lessThanChildren.isEmpty()){//说明已经获取到当前锁
					this.doSomething();
				}else{//没有获取到当前锁
					//将当前锁的前一个节点设置观察者,防止羊群效应发生
					String preNode = lessThanChildren.last();
					System.out.printf("%s 前一个节点为%s\n",this.num,preNode);
					this.zk.exists(preNode, this);
				}
			}else{//没有获取到锁就继续观察
				this.zk.exists(preNode, this);
			}
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
	}
	
	/**
	 * 模拟执行一些代码
	 */
	public void doSomething(){
		System.out.printf("%s do Something.........sleep %s\n",this.num,this.sleepTime);
		try {
			Thread.sleep(this.sleepTime);
			this.zk.close();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
