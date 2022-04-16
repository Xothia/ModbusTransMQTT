package com.xothia;

import com.xothia.bean.modbusMaster.MbMaster;
import com.xothia.bean.modbusSlave.MbSlaveUpstreamPatten;
import com.xothia.util.Attribute;
import com.xothia.util.UpstreamFormat;
import com.xothia.util.Util;
import de.gandev.modjn.entity.exception.ConnectionException;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : xothia
 * @version : 1.0
 * @Project : ModbusTransMQTT
 * @Package : com.xothia
 * @ClassName : .java
 * @createTime : 2022/4/12 20:17
 * @Email : huaxia889900@126.com
 * @Description : Job的具体业务内容,每次被执行时会创建新实例。
 */
public class UpstreamJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //用于测试响应时间
        final long currentTimeMillis = System.currentTimeMillis();

        final JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        final MbMaster mbMaster = (MbMaster) jobDataMap.get("mbMaster");
        final MbSlaveUpstreamPatten patten = (MbSlaveUpstreamPatten) jobDataMap.get("patten");
        final Integer funCode = (Integer) jobDataMap.get("function");


        final Attribute[] attributes = patten.getAttributes();
        final UpstreamFormat format = new UpstreamFormat();

        //用于测试响应时间
        patten.timestamp = currentTimeMillis;

        int transactionId;
        for (Attribute attr:attributes) {
            try {
                //也许需要加锁
                //每个transactionId可以对应到一个attribute，因此可以定死transactionid，就不需要读写map了，如果慢的话可以改进这里。
                transactionId = mbMaster.requestAsync(funCode, attr.getAddress(), attr.getQuantity());
                mbMaster.putAttrMap(transactionId, attr.getAttrName());
                mbMaster.putUpsMap(transactionId, patten);

            } catch (ConnectionException e) {
                Util.LOGGER.fatal("UpstreamJob: "+e.getMessage());
                throw new RuntimeException("UpstreamJob: "+e.getMessage());
            }
        }
    }
}
