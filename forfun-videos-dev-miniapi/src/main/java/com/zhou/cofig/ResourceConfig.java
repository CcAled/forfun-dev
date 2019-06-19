package com.zhou.cofig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:resource-prod.properties")
public class ResourceConfig {

	@Value("${zookeeperServer}")
	private String zookeeperServer;
	@Value("${bgmServer}")
	private String bgmServer;
	@Value("${fileSpace}")
	private String fileSpace;

	public String getZookeeperServer() {
		return zookeeperServer;
	}
	public void setZookeeperServer(String zookeeperServer) {
		this.zookeeperServer = zookeeperServer;
	}
	public String getBgmServer() {
		return bgmServer;
	}
	public void setBgmServer(String bgmServer) {
		this.bgmServer = bgmServer;
	}
	public String getFileSpace() {
		return fileSpace;
	}
	public void setFileSpace(String fileSpace) {
		this.fileSpace = fileSpace;
	}
}
