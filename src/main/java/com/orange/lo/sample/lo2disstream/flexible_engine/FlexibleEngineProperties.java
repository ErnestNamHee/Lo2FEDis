/** 
* Copyright (c) Orange. All Rights Reserved.
* 
* This source code is licensed under the MIT license found in the 
* LICENSE file in the root directory of this source tree. 
*/

package com.orange.lo.sample.lo2disstream.flexible_engine;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "flexibleengine")
public class FlexibleEngineProperties {
	
	private String disEndPoint;
	private String disak;
	private String dissk;
	private String disProjectId;
	private String disRegion;
	private int synchronizationThreadPoolSize;
	private int messagingThreadPoolSize;
	private String disStreamName;

	public String getDisEndpoint() {
		return disEndPoint;
	}

	public void setDisEndpoint(String disEndPoint) {
		this.disEndPoint = disEndPoint;
	}

	public String getDisak() {
		return disak;
	}

	public void setDisak(String disak) {
		this.disak = disak;
	}
	
	
	public String getDisSK() {
		return dissk;
	}

	public void setDisSK(String dissk) {
		this.dissk = dissk;
	}
	
	public String getdisProjectId() {
		return disProjectId;
	}

	public void setdisProjectId(String disProjectId) {
		this.disProjectId = disProjectId;
	}
	
	public String getdisRegion() {
		return disRegion;
	}

	public void setdisRegion(String disRegion) {
		this.disRegion= disRegion;
	}
	
	public String getdisStreamName() {
		return disStreamName;
	}

	public void setdisStreamName(String getdisStreamName) {
		this.disStreamName= disStreamName;
	}
	
	public int getSynchronizationThreadPoolSize() {
		return synchronizationThreadPoolSize;
	}

	public void setSynchronizationThreadPoolSize(int synchronizationThreadPoolSize) {
		this.synchronizationThreadPoolSize = synchronizationThreadPoolSize;
	}
	
	public int getMessagingThreadPoolSize() {
		return messagingThreadPoolSize;
	}

	public void setMessagingThreadPoolSize(int messagingThreadPoolSize) {
		this.messagingThreadPoolSize = messagingThreadPoolSize;
	}
	

}