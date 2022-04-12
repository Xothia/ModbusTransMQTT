package com.xothia;

import org.quartz.Job;
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
        //待施工

    }
}
