package com.orange.lo.sample.lo2disstream.flexible_engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huaweicloud.dis.DIS;
import com.huaweicloud.dis.iface.data.request.CommitCheckpointRequest;
import com.huaweicloud.dis.util.CheckpointTypeEnum;

/**
 * Commit Checkpoint Example
 */
public class CommitCheckpoint
{
    private static final Logger LOGGER = LoggerFactory.getLogger(CommitCheckpoint.class);
    
    public static void main(String[] args)
    {
        DIS dic = DISUtil.getInstance();
        String streamName = DISUtil.getStreamName();
        String appName = DISUtil.getAppName();
        
        CommitCheckpointRequest commitCheckpointRequest = new CommitCheckpointRequest();
        commitCheckpointRequest.setCheckpointType(CheckpointTypeEnum.LAST_READ.name());
        commitCheckpointRequest.setStreamName(streamName);
        commitCheckpointRequest.setAppName(appName);
        // 需要提交的sequenceNumber
        commitCheckpointRequest.setSequenceNumber("100");
        // 分区编号
        commitCheckpointRequest.setPartitionId("0");
        
        try
        {
            dic.commitCheckpoint(commitCheckpointRequest);
            LOGGER.info("Success to commitCheckpoint, {}", commitCheckpointRequest.toString());
        }
        catch (Exception e)
        {
            LOGGER.error("Failed to commitCheckpoint {}", commitCheckpointRequest.toString(), e);
        }
    }
}
