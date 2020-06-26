package com.orange.lo.sample.lo2disstream.flexible_engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huaweicloud.dis.DIS;
import com.huaweicloud.dis.iface.stream.request.DeleteStreamRequest;

/**
 * Delete Stream Example
 */
public class DeleteStream
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteStream.class);
    
    public static void main(String[] args)
    {
        DIS dic = DISUtil.getInstance();
        String streamName = DISUtil.getStreamName();
        
        DeleteStreamRequest deleteStreamRequest = new DeleteStreamRequest();
        deleteStreamRequest.setStreamName(streamName);
        try
        {
            dic.deleteStream(deleteStreamRequest);
            LOGGER.info("Success to delete stream {}", streamName);
        }
        catch (Exception e)
        {
            LOGGER.error("Failed to delete stream {}", streamName, e);
        }
    }
}
