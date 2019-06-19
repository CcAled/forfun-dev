package com.zhou;

import com.zhou.cofig.ResourceConfig;
import com.zhou.enums.BGMOperatorTypeEnum;
import com.zhou.pojo.Bgm;
import com.zhou.service.BgmService;
import com.zhou.utils.JsonUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

@Component
public class ZKCuratorClient {

    //zk客户端
    private CuratorFramework client = null;
    final static Logger log = LoggerFactory.getLogger(ZKCuratorClient.class);

    @Autowired
    private BgmService bgmService;

    @Autowired
    private ResourceConfig resourceConfig;

//    public static final String ZOOKEEPER_SERVER="129.28.169.182:2181";


    public void init() throws Exception {
        if (client != null){
            return;
        }
        //创建重连策略
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 5);

        //创建客户端
        client = CuratorFrameworkFactory.builder()
                .connectString(resourceConfig.getZookeeperServer())
                .sessionTimeoutMs(10000)
                .retryPolicy(retryPolicy)
                .namespace("admin").build();

        //启动客户端
        client.start();

//        String testNodeData = new String(client.getData().forPath("/bgm/190617CSN6N8K5YW"));
//        log.info("。。。。。。。。。。。。。。。。测试的节点数据为:{}",testNodeData);

        addChildWatch("/bgm");

    }

    public void addChildWatch(String nodePath) throws Exception {

        final PathChildrenCache cache = new PathChildrenCache(client, nodePath, true);
        cache.start();
        cache.getListenable().addListener(new PathChildrenCacheListener() {

            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event)
                    throws Exception {

                if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)) {
                    log.info("监听到时间CHILD ADD");
                    //1.从数据库查询bgm对象，获取路径path

                    String path = event.getData().getPath();
                    String operatorTypeStr = new String(event.getData().getData());
                    Map<String,String> map = JsonUtils.jsonToPojo(operatorTypeStr,Map.class);
                    String operatorType = map.get("operType");
                    String songPath = map.get("path");

//                    String[] arr = path.split("/");
//                    String bgmId = arr[arr.length - 1];


//                    Bgm bgm = bgmService.queryBgmById(bgmId);
//                    if (bgm == null) {
//                        return;
//                    }
//
//                    //1.1bgm相对路径
//                    String songPath = bgm.getPath();

                    //2.定义保存到本地的bgm路径
//                    String filePath = "C:\\Users\\Chistopher\\Desktop\\forfun_host" + songPath;
                    String filePath = resourceConfig.getFileSpace()+ songPath;

                    //3.定义下载的路径（播放url）
                    String[] arrPath = songPath.split("\\\\");
                    String finalPath = "";
                    //3.1处理url的斜杠以及编码
                    for (int i = 0; i < arrPath.length; i++) {
                        if (StringUtils.isNotBlank(arrPath[i])) {
                            finalPath += "/";
                            finalPath += URLEncoder.encode(arrPath[i], "UTF-8");
                        }
                    }
//                    String bgmUrl = "http://localhost:8011/mvc" + finalPath;
                    String bgmUrl = resourceConfig.getBgmServer() + finalPath;

                    if (operatorType.equals(BGMOperatorTypeEnum.ADD.type)){
                        //下载url到springboot服务器
                        URL url = new URL(bgmUrl);
                        File file = new File(filePath);
                        FileUtils.copyURLToFile(url, file);
                        client.delete().forPath(path);
                    }else if (operatorType.equals(BGMOperatorTypeEnum.DELETE.type)){
                        File file = new File(filePath);
                        FileUtils.forceDelete(file);
                        client.delete().forPath(path);
                    }

                }
            }
        });

    }
}
