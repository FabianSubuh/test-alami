package com.test.alami.eod.batch;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EodTransactionJob implements Job {

    @Autowired
    EodTransactionHandler eodTransactionHandler;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        try {
            eodTransactionHandler.getInstance().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
