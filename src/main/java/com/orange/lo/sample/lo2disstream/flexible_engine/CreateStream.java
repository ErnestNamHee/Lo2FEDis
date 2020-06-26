package com.orange.lo.sample.lo2disstream.flexible_engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huaweicloud.dis.DIS;
import com.huaweicloud.dis.iface.data.request.StreamType;
import com.huaweicloud.dis.iface.stream.request.CreateStreamRequest;
import com.huaweicloud.dis.util.DataTypeEnum;

/**
 * Create Stream Example
 */
public class CreateStream
{
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateStream.class);
    
    public static void main(String[] args)
    {
        DIS dic = DISUtil.getInstance();
        String streamName = DISUtil.getStreamName();
        
        CreateStreamRequest createStreamRequest = new CreateStreamRequest();
        createStreamRequest.setStreamName(streamName);
        // COMMON 普通通道; ADVANCED 高级通道
        createStreamRequest.setStreamType(StreamType.COMMON.name());
        createStreamRequest.setDataType(DataTypeEnum.BLOB.name());
        createStreamRequest.setPartitionCount(1);
        createStreamRequest.setDataDuration(24);
        try
        {
            dic.createStream(createStreamRequest);
            LOGGER.info("Success to create stream {}", streamName);
        }
        catch (Exception e)
        {
            LOGGER.error("Failed to create stream {}", streamName, e);
        }
    }
}
