package com.orange.lo.sample.lo2disstream.flexible_engine;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huaweicloud.dis.core.util.StringUtils;
import com.huaweicloud.dis.DIS;
import com.huaweicloud.dis.exception.DISClientException;
import com.huaweicloud.dis.iface.data.request.PutRecordsRequest;
import com.huaweicloud.dis.iface.data.request.PutRecordsRequestEntry;
import com.huaweicloud.dis.iface.data.response.PutRecordsResult;
import com.huaweicloud.dis.iface.data.response.PutRecordsResultEntry;

/**
 * Put records to DIS Example
 */
public class PutRecords
{
    private static final Logger LOGGER = LoggerFactory.getLogger(PutRecords.class);
    
    public static void main(String args[])
    {
        // 创建DIS客户端实例
        DIS dic = DISUtil.getInstance();
        
        // 配置流名称
        String streamName = DISUtil.getStreamName();
        
        // 配置上传的数据
        String message = "Hello world.";
        
        PutRecordsRequest putRecordsRequest = new PutRecordsRequest();
        putRecordsRequest.setStreamName(streamName);
        List<PutRecordsRequestEntry> putRecordsRequestEntryList = new ArrayList<>();
        // 上传10条
        for (int i = 0; i < 10; i++)
        {
            PutRecordsRequestEntry putRecordsRequestEntry = new PutRecordsRequestEntry();
            putRecordsRequestEntry.setData(ByteBuffer.wrap((message + i).getBytes()));
            // PartitionKey为随机值可使数据均匀分布到所有分区中
            putRecordsRequestEntry.setPartitionKey(String.valueOf(ThreadLocalRandom.current().nextInt(1000000)));
            putRecordsRequestEntryList.add(putRecordsRequestEntry);
        }
        putRecordsRequest.setRecords(putRecordsRequestEntryList);
        
        PutRecordsResult putRecordsResult = null;
        try
        {
            putRecordsResult = dic.putRecords(putRecordsRequest);
        }
        catch (DISClientException e)
        {
            LOGGER.error("Failed to get a normal response, please check params and retry. Error message [{}]",
                e.getMessage(),
                e);
        }
        catch (Exception e)
        {
            LOGGER.error(e.getMessage(), e);
        }
        
        if (putRecordsResult != null)
        {
            LOGGER.info("Put {} [{} successful / {} failed] records.",
                putRecordsResult.getRecords().size(),
                putRecordsResult.getRecords().size() - putRecordsResult.getFailedRecordCount().get(),
                putRecordsResult.getFailedRecordCount());
            
            for (int j = 0; j < putRecordsResult.getRecords().size(); j++)
            {
                PutRecordsResultEntry putRecordsRequestEntry = putRecordsResult.getRecords().get(j);
                if (!StringUtils.isNullOrEmpty(putRecordsRequestEntry.getErrorCode()))
                {
                    // 上传失败
                    LOGGER.error("[{}] put failed, errorCode [{}], errorMessage [{}]",
                        new String(putRecordsRequestEntryList.get(j).getData().array()),
                        putRecordsRequestEntry.getErrorCode(),
                        putRecordsRequestEntry.getErrorMessage());
                }
                else
                {
                    // 上传成功
                    LOGGER.info("[{}] put success, partitionId [{}], partitionKey [{}], sequenceNumber [{}]",
                        new String(putRecordsRequestEntryList.get(j).getData().array()),
                        putRecordsRequestEntry.getPartitionId(),
                        putRecordsRequestEntryList.get(j).getPartitionKey(),
                        putRecordsRequestEntry.getSequenceNumber());
                }
            }
        }
    }
}
