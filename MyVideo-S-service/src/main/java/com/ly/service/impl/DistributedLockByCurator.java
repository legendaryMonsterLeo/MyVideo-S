package com.ly.service.impl;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ly.enums.BgmOperatorTypeEnum;
import com.ly.pojo.Bgm;
import com.ly.service.BgmService;
import com.ly.utils.JsonUtils;

import java.io.File;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

@Service
public class DistributedLockByCurator implements InitializingBean {

	private static final Logger logger = LoggerFactory.getLogger(DistributedLockByCurator.class);

	private final static String ROOT_PATH_LOCK = "bgm";
	private CountDownLatch countDownLatch = new CountDownLatch(1);

	@Autowired
	private CuratorFramework curatorFramework;

	@Autowired
	private BgmService bgmService;

	/**
	 * 获取分布式锁
	 */
	public void acquireDistributedLock(String path) {
		String keyPath = "/" + ROOT_PATH_LOCK + "/" + path;
		while (true) {
			try {
				curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL)
						.withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE).forPath(keyPath);
				logger.info("success to acquire lock for path:{}", keyPath);
				break;
			} catch (Exception e) {
				logger.info("failed to acquire lock for path:{}", keyPath);
				logger.info("while try again .......");
				try {
					if (countDownLatch.getCount() <= 0) {
						countDownLatch = new CountDownLatch(1);
					}
					countDownLatch.await();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	/**
	 * 释放分布式锁
	 */
	public boolean releaseDistributedLock(String path) {
		try {
			String keyPath = "/" + ROOT_PATH_LOCK + "/" + path;
			if (curatorFramework.checkExists().forPath(keyPath) != null) {
				curatorFramework.delete().forPath(keyPath);
			}
		} catch (Exception e) {
			logger.error("failed to release lock");
			return false;
		}
		return true;
	}

	/**
	 * 创建 watcher 事件
	 */
	private void addWatcher(String path) throws Exception {
		String keyPath;
		if (path.equals(ROOT_PATH_LOCK)) {
			keyPath = "/" + path;
		} else {
			keyPath = "/" + ROOT_PATH_LOCK + "/" + path;
		}
		final PathChildrenCache cache = new PathChildrenCache(curatorFramework, keyPath, false);
		cache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
		cache.getListenable().addListener((client, event) -> {
			if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_REMOVED)) {
				String oldPath = event.getData().getPath();
				logger.info("success to release lock for path:{}", oldPath);
				if (oldPath.contains(path)) {
					// 释放计数器，让当前的请求获取锁
					countDownLatch.countDown();
				}
			}
		});
	}

	// 调用监听字节点函数
	@Override
	public void afterPropertiesSet() {
		try {
			curatorFramework = curatorFramework.usingNamespace("admin");
			this.myWatcher("/bgm");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	/**
	 * @Description:增加或删除bgm,并向zkServer创建子节点，供小程序后端监听
	 */
	public void sendBgmOperator(String bgmId, String operType) {
		String path = "/" + ROOT_PATH_LOCK;
		try {
			curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT)
					.withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE).forPath(path + "/" + bgmId, operType.getBytes());
			logger.info("bgm节点变化:{}", bgmId + "---" + operType);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @Description:对节点进行监听
	 */
	public void myWatcher(String nodePath) throws Exception {
		final PathChildrenCache cache = new PathChildrenCache(curatorFramework, nodePath, true);
		cache.start();
		cache.getListenable().addListener(new PathChildrenCacheListener() {

			@Override
			public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
				if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)) {
					logger.info("监听到CHILE_ADDED");

					// 1.从数据库查询bgm对象，获取路径path
					String path = event.getData().getPath();
					String operatorMsg = new String(event.getData().getData());
					Map<String, String>map = JsonUtils.jsonToPojo(operatorMsg, Map.class);
					String operatorType = map.get("operType");
					String songPath = map.get("path");
					
//					String arr[] = path.split("/");
//					String bgmId = arr[arr.length - 1];
//					Bgm bgm = bgmService.queryBgmById(bgmId);
//					if (bgm == null) {
//						return;
//					}
//					String songPath = bgm.getPath();

					// 2.定义保存本地路径
					String filePath = "E:\\MyVideo_data" + songPath;

					// 3.定义下载的路径
					String arrPath[] = songPath.split("\\\\");
					String finalPath = "";
					for (int i = 0; i < arrPath.length; i++) {
						if (StringUtils.isNotBlank(arrPath[i])) {
							finalPath += "/";
							finalPath +=URLEncoder.encode(arrPath[i],"UTF-8");
						}
					}
					
					finalPath = finalPath.replaceAll("\\+", "%20");
					String bgmUrl = "http://192.168.0.108:8082"+finalPath;
					if(operatorType.equals(BgmOperatorTypeEnum.ADD.type)) {
						URL url = new URL(bgmUrl);
						File file = new File(filePath);
						FileUtils.copyURLToFile(url, file);
						curatorFramework.delete().forPath(path);
					}else if(operatorType.equals(BgmOperatorTypeEnum.DELETE.type)) {
						File file =new File(filePath);
						FileUtils.forceDelete(file);
						curatorFramework.delete().forPath(path);
					}
				}

			}
		});
	}
}
