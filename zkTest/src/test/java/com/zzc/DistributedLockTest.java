package com.zzc;

import java.io.IOException;

import org.apache.zookeeper.KeeperException;
import org.junit.Test;

import junit.framework.TestCase;

public class DistributedLockTest extends TestCase {
	private static final String HOSTS = "127.0.0.1:2181,127.0.0.1:2182";
	
	@Test
	public void testApplyLock(){
		try {
			//5个模拟测试
			final DistributedLock d1 = new DistributedLock(HOSTS, 1, 10000);
			final DistributedLock d2 = new DistributedLock(HOSTS, 2, 8000);
			final DistributedLock d3 = new DistributedLock(HOSTS, 3, 6000);
			final DistributedLock d4 = new DistributedLock(HOSTS, 4, 4000);
			final DistributedLock d5 = new DistributedLock(HOSTS, 5, 2000);
			
			Thread t1 = new Thread(){
				@Override
				public void run(){
					try {
						d1.applyLock();
					} catch (KeeperException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
			
			Thread t2 = new Thread(){
				@Override
				public void run(){
					try {
						d2.applyLock();
					} catch (KeeperException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
			
			Thread t3 = new Thread(){
				@Override
				public void run(){
					try {
						d3.applyLock();
					} catch (KeeperException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
			
			Thread t4 = new Thread(){
				@Override
				public void run(){
					try {
						d4.applyLock();
					} catch (KeeperException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
			
			Thread t5 = new Thread(){
				@Override
				public void run(){
					try {
						d5.applyLock();
					} catch (KeeperException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
			
			t1.start();
			t2.start();
			t3.start();
			t4.start();
			t5.start();
			
			Thread.sleep(Long.MAX_VALUE);
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
