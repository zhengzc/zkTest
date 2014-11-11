package com.zzc;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

/**
 * zk node相关操作
 * @author zhengzhichao
 *
 */
public class NodeCrud extends ConnectionWatcher {

	public NodeCrud(String hosts) throws IOException, InterruptedException {
		super(hosts);
	}

	/**
	 * 
	 * @param nodePath
	 * @param watcher
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public void queryNode(String nodePath,Watcher watcher) throws KeeperException, InterruptedException{
		byte[] value = this.zk.getData(nodePath, watcher , null);
		System.out.printf("获取到的值为:%s\n",value.toString());
	}
	
	/**
	 * 创建node
	 * @param nodePath 请使用标准的全路径写法
	 * @param nodeValue node值
	 * @throws InterruptedException ,KeeperException
	 */
	public void createNode(String nodePath,String nodeValue) throws KeeperException, InterruptedException{
		String createdPath = this.zk.create(nodePath, nodeValue.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		System.out.printf("创建Node为:%s\n", createdPath);
	}
	
	/**
	 * 
	 * @param nodePath
	 * @param nodeValue
	 * @param version
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public void setNode(String nodePath,String nodeValue,int version) throws KeeperException,InterruptedException{
		Stat stat = this.zk.setData(nodePath, nodeValue.getBytes(), version);
	}
}
