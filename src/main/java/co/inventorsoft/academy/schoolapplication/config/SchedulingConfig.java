package co.inventorsoft.academy.schoolapplication.config;

import co.inventorsoft.academy.schoolapplication.service.ParentNotificationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
public class SchedulingConfig {

    private final ParentNotificationService parentNotificationService;

    @Value("${scheduling.daily-report-cron}")
    private String dailyReportCron;

    public SchedulingConfig(ParentNotificationService parentNotificationService) {
        this.parentNotificationService = parentNotificationService;
    }

    @Scheduled(cron = "#{@dailyReportCron}")
    public void scheduleDailyReportSending() {
        parentNotificationService.sendDailyGradesReport();
    }
}
