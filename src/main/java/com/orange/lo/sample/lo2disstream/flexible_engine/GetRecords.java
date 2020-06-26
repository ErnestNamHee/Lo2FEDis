package com.orange.lo.sample.lo2disstream.flexible_engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huaweicloud.dis.DIS;
import com.huaweicloud.dis.exception.DISClientException;
import com.huaweicloud.dis.iface.data.request.GetPartitionCursorRequest;
import com.huaweicloud.dis.iface.data.request.GetRecordsRequest;
import com.huaweicloud.dis.iface.data.response.GetPartitionCursorResult;
import com.huaweicloud.dis.iface.data.response.GetRecordsResult;
import com.huaweicloud.dis.iface.data.response.Record;
import com.huaweicloud.dis.util.PartitionCursorTypeEnum;

/**
 * Get records from DIS Example
 */
public class GetRecords
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GetRecords.class);
    
    public static void main(String args[])
    {
        // 创建DIS客户端实例
        DIS dic = DISUtil.getInstance();
        
        // 配置流名称
        String streamName = DISUtil.getStreamName();
        
        // 配置数据下载分区ID
        String partitionId = "0";
        
        // 配置下载数据序列号
        String startingSequenceNumber = "0";
        
        // 配置下载数据方式
        // AT_SEQUENCE_NUMBER 从指定的sequenceNumber开始获取，需要设置StartingSequenceNumber
        // AFTER_SEQUENCE_NUMBER 从指定的sequenceNumber之后开始获取，需要设置StartingSequenceNumber
        // TRIM_HORIZON 从最旧的记录开始获取
        // LATEST 从最新的记录开始获取
        // AT_TIMESTAMP 从指定的时间戳(13位)开始获取，需要设置Timestamp
        String cursorType = PartitionCursorTypeEnum.AT_SEQUENCE_NUMBER.name();
        
        try
        {
            // 获取数据游标
            GetPartitionCursorRequest request = new GetPartitionCursorRequest();
            request.setStreamName(streamName);
            request.setPartitionId(partitionId);
            request.setStartingSequenceNumber(startingSequenceNumber);
            request.setCursorType(cursorType);
            GetPartitionCursorResult response = dic.getPartitionCursor(request);
            String cursor = response.getPartitionCursor();
            
            LOGGER.info("Get stream {}[partitionId={}] cursor success : {}", streamName, partitionId, cursor);
            
            GetRecordsRequest recordsRequest = new GetRecordsRequest();
            GetRecordsResult recordResponse = null;
            while (true)
            {
                recordsRequest.setPartitionCursor(cursor);
                recordResponse = dic.getRecords(recordsRequest);
                // 下一批数据游标
                cursor = recordResponse.getNextPartitionCursor();
                
                for (Record record : recordResponse.getRecords())
                {
                    LOGGER.info("Get record [{}], partitionKey [{}], sequenceNumber [{}].",
                        new String(record.getData().array()),
                        record.getPartitionKey(),
                        record.getSequenceNumber());
                }
            }
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
    }
}
