package com.xothia;

import com.xothia.bean.modbusSlave.MbSlave;
import com.xothia.bean.modbusSlave.MbSlaveGroup;
import com.xothia.bean.modbusSlave.MbSlaveUpstreamPatten;
import com.xothia.util.Util;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : xothia
 * @version : 1.0
 * @Project : ModbusTransMQTT
 * @Package : com.xothia
 * @ClassName : .java
 * @createTime : 2022/4/12 20:14
 * @Email : huaxia889900@126.com
 * @Description : 用于将Modbus设备的定时上报任务加入quartz。
 * 以及建立连接这种耗时操作。
 */
@Valid
@Component("modbusProxy")
public class ModbusProxy implements InitializingBean {
    public static final Log LOGGER = LogFactory.getLog(ModbusProxy.class); //日志
    @NotNull
    private ApplicationContext ctx;
    @NotNull
    private Scheduler scheduler;
    @NotNull
    private MbSlaveGroup mbSlaveGroup;

    @Autowired
    public ModbusProxy(ApplicationContext ctx, MbSlaveGroup mbSlaveGroup) throws SchedulerException {
        this.ctx = ctx;
        this.mbSlaveGroup = mbSlaveGroup;
        this.scheduler = StdSchedulerFactory.getDefaultScheduler();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LOGGER.info("Modbus代理服务初始化中。");

        //这里把所有定时任务加入quartz scheduler
        Util.valid(this);
        JobDetail jobDetail;
        Trigger trigger;
        SimpleScheduleBuilder builder;
        CronScheduleBuilder cronBuilder;
        MbSlaveUpstreamPatten[] pattens;

        final MbSlave[] slaveGroup = mbSlaveGroup.getSlaveGroup();
        for (MbSlave sla : slaveGroup) {
            pattens = sla.getUpstreamPatten();
            for (MbSlaveUpstreamPatten p:pattens) {
                //一个patten对应一个job
                //构建jobdetail
                jobDetail = JobBuilder.newJob(UpstreamJob.class).build();
                jobDetail.getJobDataMap().put("mbMaster", sla.getMbMasterManager());
                jobDetail.getJobDataMap().put("patten", p);

                //构建trigger
                if(!Util.isNullOrBlank(p.getCronExpr())){
                    cronBuilder = CronScheduleBuilder.cronSchedule(p.getCronExpr());
                    trigger = TriggerBuilder.newTrigger()
                            .withSchedule(cronBuilder)
                            .startNow()
                            .build();
                }
                else{
                    builder = SimpleScheduleBuilder.simpleSchedule()
                            .withIntervalInMilliseconds(p.getIntervalInMilliseconds())
                            .repeatForever();
                    trigger = TriggerBuilder.newTrigger()
                            .withSchedule(builder)
                            .startNow()
                            .build();
                }
                scheduler.scheduleJob(jobDetail, trigger);
            }
        }

        //再建立连接
        for (MbSlave sla : slaveGroup) {
            try {
                sla.getMbMasterManager().doConnect();
                sla.getMqttClientManager().doConnect();
            }catch (Exception e){
                LOGGER.fatal("客户端连接异常。"+e.getMessage());
                throw new RuntimeException("客户端连接异常。");
            }

        }
        LOGGER.info("Modbus代理服务初始化完成。");
    }

    //使服务运行起来
    public void run() throws SchedulerException {
        LOGGER.info("Modbus代理服务启动中。");
        this.scheduler.start();
        LOGGER.info("Modbus代理服务已启动。");

    }
}
