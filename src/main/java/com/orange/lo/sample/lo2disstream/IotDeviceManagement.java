/** 
* Copyright (c) Orange. All Rights Reserved.
* 
* This source code is licensed under the MIT license found in the 
* LICENSE file in the root directory of this source tree. 
*/

package com.orange.lo.sample.lo2disstream;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import com.orange.lo.sample.lo2disstream.flexible_engine.FlexibleEngineProperties;
import com.orange.lo.sample.lo2disstream.flexible_engine.DisClientCache;
import com.huaweicloud.dis.DIS;
import com.huaweicloud.dis.DISClientBuilder;
import com.orange.lo.sample.lo2disstream.flexible_engine.FEDeviceClient;
import com.orange.lo.sample.lo2disstream.lo.LoCommandSender;
import com.orange.lo.sample.lo2disstream.lo.LoDeviceProvider;



@EnableScheduling
@Component
public class IotDeviceManagement {

	private static Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private static final String CONNECTION_STRING_PATTERN = "HostName=%s;DeviceId=%s;SharedAccessKey=%s";
	
	private LoDeviceProvider loDeviceProvider;
	private MessageProducerSupport mqttInbound;
	private LoCommandSender loCommandSender;
	private FlexibleEngineProperties flexibleengineProperties;
	private DisClientCache disClientCache;
	
	public IotDeviceManagement(LoDeviceProvider loDeviceProvider, FlexibleEngineProperties flexibleengineProperties, LoCommandSender loCommandSender, DisClientCache disClientCache, MessageProducerSupport mqttInbound) {
		this.flexibleengineProperties = flexibleengineProperties;
		this.loCommandSender = loCommandSender;
		this.disClientCache = disClientCache;
		this.mqttInbound = mqttInbound;
		
	 }

	@Scheduled(fixedRateString = "${flexibleengine.synchronization-device-interval}")
	public void synchronizeDevices() throws InterruptedException {
        LOG.debug("Synchronizing devices... ");
        Set<String> loIds = loDeviceProvider.getDevices().stream().map(d -> d.getId()).collect(Collectors.toSet());
        ThreadPoolExecutor synchronizingExecutor =
				new ThreadPoolExecutor(flexibleengineProperties.getSynchronizationThreadPoolSize(), flexibleengineProperties.getSynchronizationThreadPoolSize(),
						10, TimeUnit.SECONDS,
						new ArrayBlockingQueue<>(loIds.size()));
        for (String deviceId : loIds) {
			synchronizingExecutor.execute(() -> {
				createDeviceClient(deviceId);
			});
		}
        int synchronizationTimeout = calculateSynchronizationTimeout(loIds.size(), flexibleengineProperties.getSynchronizationThreadPoolSize());
        synchronizingExecutor.shutdown();
        if (synchronizingExecutor.awaitTermination(synchronizationTimeout, TimeUnit.SECONDS)) {
        	//Set<String> iotIds = ioTDeviceProvider.getDevices().stream().map(d -> d.getId()).collect(Collectors.toSet());
        	//iotIds.removeAll(loIds);
        	//iotIds.forEach(id -> {
        	//	LOG.debug("remove from cache and iot device " + id);
        	//	disClientCache.remove(id);
        	//});
        }
        mqttInbound.start();
	}
	
	public FEDeviceClient createDeviceClient(String deviceId) {
		synchronized (deviceId.intern()) {
			// if no deviceId  in disClientCache
			if (disClientCache.get(deviceId) == null) {
				return createFEDeviceClient(deviceId);
			} else {
				return disClientCache.get(deviceId);
			}
		}
	}

	private FEDeviceClient createFEDeviceClient(String deviceId) {
		if (disClientCache.get(deviceId) == null ) {
	        	
				DIS disclientstandard = DISClientBuilder.standard()
	                .withEndpoint("https://dis.eu-west-0.prod-cloud-ocb.orange-business.com")
	                .withAk("WOYP9KUMUJT6BVSGZFRA")
	                .withSk("Wqt5TbUxemsJ58nMZYxGHacmuQnOyRhwCPaNofY4")
	                .withProjectId("cddffdf04cf441c8b94aac85dfdc2a69")
	                .withRegion("eu-west-0")
	                .build();
	        
				FEDeviceClient deviceClient = new FEDeviceClient(deviceId, "LoLo", flexibleengineProperties.getdisStreamName(),disclientstandard);
				disClientCache.add(deviceId, deviceClient);
				LOG.info("Device client created for {}", deviceId);
				return deviceClient;
		}
		return disClientCache.get(deviceId);
	}
	
	private int calculateSynchronizationTimeout(int devices, int threadPoolSize) {
		return (devices / threadPoolSize + 1) * 5; // max 5 seconds for 1 operation
	}
	
}