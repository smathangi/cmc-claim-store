package uk.gov.hmcts.cmc.scheduler.services;

import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.cmc.scheduler.exceptions.JobException;
import uk.gov.hmcts.cmc.scheduler.exceptions.JobNotFoundException;
import uk.gov.hmcts.cmc.scheduler.model.JobData;

import java.time.ZonedDateTime;
import java.util.Date;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

@Service
public class JobService {

    private final Scheduler scheduler;

    @Autowired
    public JobService(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public JobKey scheduleJob(JobData jobData, ZonedDateTime startDateTime) {
        try {
            scheduler.scheduleJob(
                newJob(jobData.getJobClass())
                    .withIdentity(jobData.getId(), jobData.getGroup())
                    .withDescription(jobData.getDescription())
                    .usingJobData(new JobDataMap(jobData.getData()))
                    .requestRecovery()
                    .build(),
                newTrigger()
                    .startAt(Date.from(startDateTime.toInstant()))
                    .withSchedule(
                        simpleSchedule()
                            .withMisfireHandlingInstructionNowWithExistingCount()
                    )
                    .build()
            );

            return JobKey.jobKey(jobData.getId(), jobData.getGroup());

        } catch (SchedulerException exc) {
            throw new JobException("Error while scheduling a job", exc);
        }
    }

    public void deleteJob(JobKey jobKey) {
        try {
            boolean jobFound = scheduler.deleteJob(jobKey);
            if (!jobFound) {
                throw new JobNotFoundException("No job found with key: " + jobKey);
            }
        } catch (SchedulerException exc) {
            throw new JobException("Error while deleting job with key " + jobKey, exc);
        }
    }

    public void deleteJob(String id, String group) {
        deleteJob(JobKey.jobKey(id, group));
    }
}