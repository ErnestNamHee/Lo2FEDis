package com.orange.lo.sample.lo2disstream.flexible_engine;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huaweicloud.dis.DIS;
import com.huaweicloud.dis.iface.stream.request.ListStreamsRequest;
import com.huaweicloud.dis.iface.stream.response.ListStreamsResult;

/**
 * List Stream Example
 */
public class ListStreams
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ListStreams.class);
    
    public static void main(String[] args)
    {
        DIS dic = DISUtil.getInstance();
        
        ListStreamsRequest listStreamsRequest = new ListStreamsRequest();
        listStreamsRequest.setLimit(100);
        String startStreamName = null;
        ListStreamsResult listStreamsResult = null;
        List<String> streams = new ArrayList<>();
        
        try
        {
            do
            {
                listStreamsRequest.setExclusivetartStreamName(startStreamName);
                listStreamsResult = dic.listStreams(listStreamsRequest);
                streams.addAll(listStreamsResult.getStreamNames());
                if (streams.size() > 0)
                {
                    startStreamName = streams.get(streams.size() - 1);
                }
            } while (listStreamsResult.getHasMoreStreams());
            
            LOGGER.info("Success to list streams");
            for (int i = 1; i <= streams.size(); i++)
            {
                LOGGER.info("{}\t\t{}", i, streams.get(i - 1));
            }
        }
        catch (Exception e)
        {
            LOGGER.error("Failed to list streams", e);
        }
    }
}
