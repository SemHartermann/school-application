package co.inventorsoft.academy.schoolapplication.util.lessongeneration;

import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Slf4j
@DisallowConcurrentExecution
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class LessonGenerationJob implements Job {
    @Autowired
    LessonGenerationService lessonGenerationService;

    @Override
    public void execute(JobExecutionContext context) {
        log.info("Job ** {} ** starting @ {}",
                context.getJobDetail().getKey().getName(),
                context.getFireTime());
        lessonGenerationService.generateNextWeekLessonsByModuleSchedule(
                ZonedDateTime.now(),
                ZonedDateTime.now().plusDays(4L));
        log.info("Job ** {} ** completed.  Next job scheduled @ {}",
                context.getJobDetail().getKey().getName(),
                context.getNextFireTime());
    }
}
