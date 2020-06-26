/** 
* Copyright (c) Orange. All Rights Reserved.
* 
* This source code is licensed under the MIT license found in the 
* LICENSE file in the root directory of this source tree. 
*/

package com.orange.lo.sample.lo2disstream.flexible_engine;

import java.lang.invoke.MethodHandles;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import com.orange.lo.sample.lo2disstream.Counters;
import com.orange.lo.sample.lo2disstream.IotDeviceManagement;
import com.huaweicloud.dis.DIS;
import com.huaweicloud.dis.DISClientBuilder;
import com.huaweicloud.dis.core.util.StringUtils;
import com.huaweicloud.dis.exception.DISClientException;
import com.huaweicloud.dis.iface.data.request.PutRecordsRequest;
import com.huaweicloud.dis.iface.data.request.PutRecordsRequestEntry;
import com.huaweicloud.dis.iface.data.response.PutRecordsResult;
import com.huaweicloud.dis.iface.data.response.PutRecordsResultEntry;
import com.orange.lo.sample.lo2disstream.flexible_engine.FEDeviceClient;

@Component
public class MessageSender {

	private static Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
		
	private DisClientCache disClientCache;
	private IotDeviceManagement iotDeviceManagement;
    private ThreadPoolExecutor sendingExecutor;
    private Counters counterProvider;
    
	@Autowired
	public MessageSender(DisClientCache disClientCache, FlexibleEngineProperties flexibleengineProperties, IotDeviceManagement iotDeviceManagement, Counters counterProvider) {
		this.disClientCache = disClientCache;
		this.iotDeviceManagement = iotDeviceManagement;
		this.counterProvider = counterProvider;
		BlockingQueue<Runnable> tasks = new LinkedBlockingQueue<>();
		sendingExecutor = new ThreadPoolExecutor(flexibleengineProperties.getMessagingThreadPoolSize(), flexibleengineProperties.getMessagingThreadPoolSize(), 10, TimeUnit.SECONDS, tasks);
	}
	
	public void send(Message<String> msg) {
	    try {
			String loClientId = getSourceDeviceId(msg.getPayload());
			FEDeviceClient deviceClient = disClientCache.get(loClientId);
			if (deviceClient != null)
				sendMessage(msg, deviceClient);
            else {
                sendingExecutor.submit(() -> {
                	FEDeviceClient createdDeviceClient = iotDeviceManagement.createDeviceClient(loClientId);
					sendMessage(msg, createdDeviceClient);
                });
            }
	    }
		catch (IllegalArgumentException e) {
			LOG.error("Error while sending message", e);
		} catch (JSONException e) {
			LOG.error("Cannot retrieve device id from message, message not sent {}", msg.getPayload());
		}
	}

	private void sendMessage(Message<String> msg, FEDeviceClient createdDisClient) {
		counterProvider.evtAttempt().increment();		
		
        PutRecordsRequest putRecordsRequest = new PutRecordsRequest();
        putRecordsRequest.setStreamName(createdDisClient.getDisStreamName());
        
        List<PutRecordsRequestEntry> putRecordsRequestEntryList = new ArrayList<PutRecordsRequestEntry>();
        
        ByteBuffer buffer = ByteBuffer.wrap(msg.toString().getBytes());
        
        for (int i = 0; i < 3; i++)
        {
            PutRecordsRequestEntry putRecordsRequestEntry = new PutRecordsRequestEntry();
            putRecordsRequestEntry.setData(buffer);
            putRecordsRequestEntry.setPartitionKey(String.valueOf(ThreadLocalRandom.current().nextInt(1000000)));
            putRecordsRequestEntryList.add(putRecordsRequestEntry);
        }
        putRecordsRequest.setRecords(putRecordsRequestEntryList);
        
        LOG.info("========== BEGIN PUT ============");
        
        PutRecordsResult putRecordsResult = null;
        try
        {
            putRecordsResult = createdDisClient.getDisclientstandard().putRecords(putRecordsRequest);
        }
        catch (DISClientException e)
        {
            LOG.error("Failed to get a normal response, please check params and retry. Error message [{}]",
                e.getMessage(),
                e);
        }
        catch (Exception e)
        {
            LOG.error(e.getMessage(), e);
        }
        
        if (putRecordsResult != null)
        {
            LOG.info("Put {} records[{} successful / {} failed].",
                putRecordsResult.getRecords().size(),
                putRecordsResult.getRecords().size() - putRecordsResult.getFailedRecordCount().get(),
                putRecordsResult.getFailedRecordCount());
            
            for (int j = 0; j < putRecordsResult.getRecords().size(); j++)
            {
                PutRecordsResultEntry putRecordsRequestEntry = putRecordsResult.getRecords().get(j);
                if (!StringUtils.isNullOrEmpty(putRecordsRequestEntry.getErrorCode()))
                {
                    // 上传失败
                    LOG.error("[{}] put failed, errorCode [{}], errorMessage [{}]",
                        new String(putRecordsRequestEntryList.get(j).getData().array()),
                        putRecordsRequestEntry.getErrorCode(),
                        putRecordsRequestEntry.getErrorMessage());
                }
                else
                {
                    // 上传成功
                    LOG.info("[{}] put success, partitionId [{}], partitionKey [{}], sequenceNumber [{}]",
                        new String(putRecordsRequestEntryList.get(j).getData().array()),
                        putRecordsRequestEntry.getPartitionId(),
                        putRecordsRequestEntryList.get(j).getPartitionKey(),
                        putRecordsRequestEntry.getSequenceNumber());
                }
            }
        }
        LOG.info("========== END PUT ============");
    }
		
	private String getSourceDeviceId(String msg) throws JSONException {
		return new JSONObject(msg).getJSONObject("metadata").getString("source");
	}


}