package co.inventorsoft.academy.schoolapplication.config;

import co.inventorsoft.academy.schoolapplication.util.lessongeneration.AutowiringSpringBeanJobFactory;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ArrayUtils;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import javax.sql.DataSource;
import java.util.Date;
import java.util.Properties;

@Configuration
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuartzConfig {
    ApplicationContext applicationContext;

    DataSource dataSource;

    QuartzProperties quartzProperties;

    String cronTriggerForEverySaturday;

    @Autowired
    public QuartzConfig(ApplicationContext applicationContext, DataSource dataSource, QuartzProperties quartzProperties, @Value("${application.trigger.cron.lesson}") String cronTriggerForEverySaturday) {
        this.applicationContext = applicationContext;
        this.dataSource = dataSource;
        this.quartzProperties = quartzProperties;
        this.cronTriggerForEverySaturday = cronTriggerForEverySaturday;
    }

    @Bean
    public SpringBeanJobFactory springBeanJobFactory() {
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();

        jobFactory.setApplicationContext(applicationContext);

        return jobFactory;
    }

    @Bean
    public SchedulerFactoryBean scheduler(Trigger... triggers) {
        SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();

        schedulerFactory.setOverwriteExistingJobs(true);
        schedulerFactory.setDataSource(dataSource);
        schedulerFactory.setQuartzProperties(quartzProperties());
        schedulerFactory.setJobFactory(springBeanJobFactory());
        schedulerFactory.setWaitForJobsToCompleteOnShutdown(true);
        schedulerFactory.setAutoStartup(true);

        if (ArrayUtils.isNotEmpty(triggers)) {
            schedulerFactory.setTriggers(triggers);
        }

        return schedulerFactory;
    }

    @Bean(name = "lessonGeneration")
    public JobDetailFactoryBean jobLessonGeneration() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();

        factoryBean.setJobClass(co.inventorsoft.academy.schoolapplication.util.lessongeneration.LessonGenerationJob.class);
        factoryBean.setApplicationContext(applicationContext);
        factoryBean.setDurability(true);

        return factoryBean;
    }

    @Bean(name = "lessonGenerationTrigger")
    public CronTriggerFactoryBean triggerLessonGeneration(@Qualifier("lessonGeneration") JobDetail jobDetail) {
        Date date = new Date();

        CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();

        factoryBean.setJobDetail(jobDetail);
        factoryBean.setCronExpression(cronTriggerForEverySaturday);
        factoryBean.setStartTime(date);
        factoryBean.setStartDelay(0L);
        factoryBean.setName("Lesson Generation Trigger");
        factoryBean.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);

        return factoryBean;
    }

    public Properties quartzProperties() {

        Properties properties = new Properties();
        properties.putAll(quartzProperties.getProperties());
        return properties;
    }

}