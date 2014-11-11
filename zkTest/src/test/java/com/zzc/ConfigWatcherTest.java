package com.zzc;

import java.io.IOException;

import org.apache.zookeeper.KeeperException;
import org.junit.Test;

import junit.framework.TestCase;

public class ConfigWatcherTest extends TestCase {
	private static final String HOSTS = "127.0.0.1:2181,127.0.0.1:2182";
	
	@Test
	public void testConfigWatcher(){
		try {
			ConfigWatcher configWatcher = new ConfigWatcher(HOSTS, "/testNode1");
			configWatcher.displayConfig();
			configWatcher.updateConfig();
			Thread.sleep(Long.MAX_VALUE);
		} catch (InterruptedException | IOException | KeeperException e) {
			e.printStackTrace();
		}
	}
}
