package com.orange.lo.sample.lo2disstream.flexible_engine;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huaweicloud.dis.DIS;
import com.huaweicloud.dis.iface.stream.request.DescribeStreamRequest;
import com.huaweicloud.dis.iface.stream.response.DescribeStreamResult;
import com.huaweicloud.dis.iface.stream.response.PartitionResult;

/**
 * Describe Stream Example
 */
public class DescribeStream
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DescribeStream.class);
    
    public static void main(String[] args)
    {
        DIS dic = DISUtil.getInstance();
        String streamName = DISUtil.getStreamName();
        
        DescribeStreamRequest describeStreamRequest = new DescribeStreamRequest();
        describeStreamRequest.setStreamName(streamName);
        List<PartitionResult> partitions = new ArrayList<>();
        DescribeStreamResult describeStreamResult;
        String startPartition = null;
        
        try
        {
            do
            {
                describeStreamRequest.setStartPartitionId(startPartition);
                describeStreamResult = dic.describeStream(describeStreamRequest);
                partitions.addAll(describeStreamResult.getPartitions());
                startPartition = partitions.get(partitions.size() - 1).getPartitionId();
            } while (describeStreamResult.getHasMorePartitions());
            
            LOGGER.info("Success to describe stream {}", streamName);
            for (PartitionResult partition : partitions)
            {
                LOGGER.info("PartitionId='{}', Status='{}', SequenceNumberRange='{}'",
                    partition.getPartitionId(),
                    partition.getStatus(),
                    partition.getSequenceNumberRange());
            }
        }
        catch (Exception e)
        {
            LOGGER.error("Failed to describe stream {}", streamName, e);
        }
    }
}
