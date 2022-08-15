package com.test.alami.eod;


import com.test.alami.eod.batch.EodTransactionJob;
import com.test.alami.eod.config.AutoWiringSpringBeanJobFactory;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.core.jmx.JobDetailSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

@Configuration
@Slf4j
@ComponentScan
public class QrtzScheduler {


    @Autowired
    private ApplicationContext applicationContext;


    @Bean
    public SpringBeanJobFactory springBeanJobFactory() {
        AutoWiringSpringBeanJobFactory jobFactory = new AutoWiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

//    @Bean
//    public Scheduler scheduler(Trigger trigger, JobDetail job, SchedulerFactoryBean factory)
//            throws SchedulerException {
//        Scheduler scheduler = factory.getScheduler();
//        scheduler.scheduleJob(job, trigger);
//        scheduler.start();
//        return scheduler;
//    }
//
//    @Bean
//    public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
//        SchedulerFactoryBean factory = new SchedulerFactoryBean();
//        factory.setJobFactory(springBeanJobFactory());
//        factory.setQuartzProperties(quartzProperties());
//        return factory;
//    }

    @Bean
    public SchedulerFactoryBean scheduler(Trigger trigger, JobDetail job) {
        SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
        schedulerFactory.setConfigLocation(new ClassPathResource("quartz.properties"));

        schedulerFactory.setJobFactory(springBeanJobFactory());
        schedulerFactory.setJobDetails(job);
        schedulerFactory.setTriggers(trigger);
        return schedulerFactory;
    }

    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }

    @Bean
    public JobDetail jobDetail() {
        return JobBuilder.newJob().ofType(EodTransactionJob.class)
                .storeDurably()
                .withIdentity("Qrtz_Job_Detail")
                .withDescription("Invoke Sample Job service...")
                .build();
    }

//    @Bean
//    public JobDetailFactoryBean jobDetail() {
//        JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
//        jobDetailFactory.setJobClass(EodTransactionJob.class);
//        jobDetailFactory.setDescription("Invoke Sample Job service...");
//        jobDetailFactory.setDurability(true);
//        return jobDetailFactory;
//    }

//    @Bean
//    public Trigger trigger(JobDetail job) {
//        return TriggerBuilder.newTrigger().forJob(job)
//                .withIdentity("Qrtz_Trigger")
//                .withDescription("Sample trigger")
//                .withSchedule(simpleSchedule().repeatForever().withIntervalInHours(1))
//                .build();
//    }

    @Bean
    public SimpleTriggerFactoryBean trigger(JobDetail job) {
        SimpleTriggerFactoryBean trigger = new SimpleTriggerFactoryBean();
        trigger.setJobDetail(job);
        trigger.setRepeatInterval(10000);
        trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        return trigger;
    }
}
