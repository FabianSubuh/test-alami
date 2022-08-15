package com.test.alami.eod.batch;

import com.test.alami.eod.dto.TransactionDto;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class EodTransactionHandler {

    @Autowired
    private Environment environment;

    private List<TransactionDto> listTrx = null;

    private static final String THREAD1="ThreadNo1";
    private static final String THREAD2a="ThreadNo2a";
    private static final String THREAD2b="ThreadNo2b";
    private static final String THREAD3="ThreadNo3";

    private static EodTransactionHandler instance = null;
    private boolean running = false;

    public static EodTransactionHandler getInstance(){
        if (instance == null){
            instance =  new EodTransactionHandler();
        }
        return instance;
    }

    public static void main(String[] args) {
        try {
            EodTransactionHandler.getInstance().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void execute() throws Exception {
        if(running)
            return;
        running = true;

        File dir = null;
        log.info("START  ......");
        try {
            String path ="/home/bian/Documents/test-alami/src/main/resources/in/";
            dir = new File(String.valueOf(path));
            System.out.println(path);
            if (!dir.isDirectory()) {
                throw new Exception("Fatal error PATHis not directory! " + dir.getAbsolutePath());
            }
            File[] files = dir.listFiles();
            for (File file : files) {
                if (file.getName().endsWith(".csv")) {
                    long time = System.currentTimeMillis();

                    listTrx = new ArrayList<>();
                    listTrx = getDataTrx(file);
                    log.info("get "+ listTrx.size());
                    listTrx = doProcessStep(THREAD1,listTrx);
                    log.info("s1 "+ listTrx.size());
                    listTrx = doProcessStep(THREAD2a,listTrx);
                    log.info("s2a "+ listTrx.size());
                    listTrx = doProcessStep(THREAD2b,listTrx);
                    log.info("s2b "+ listTrx.size());
                    listTrx = doProcessStep(THREAD3,listTrx);
                    log.info("s3 "+ listTrx.size());
                    writeFile();
                    log.info("parsing file " + file.getAbsolutePath() + " take ~ " + (System.currentTimeMillis() - time) + " ms");
                }
            }

        } catch(Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            running = false;
        }

    }

    private List<TransactionDto> doProcessStep(String step,List<TransactionDto> listTrx){
        List<TransactionDto> listStepTrx = new ArrayList<>();
        try {
            ExecutorService executor = Executors.newFixedThreadPool(8);
            for (final TransactionDto trx : listTrx) {
                executor.submit(new Thread(step) {

                    @Override
                    public void run() {
                        long threadId = Thread.currentThread().getId();
                        switch (step) {
                            case THREAD1 :
                                trx.setAvgBalance((trx.getBalance()+trx.getPrevBalance())/2);
                                trx.setThread1No(THREAD1+"-"+threadId);
                                break;
                            case THREAD2a :
                                if (trx.getBalance() >= 100 && trx.getBalance() <= 150 ){
                                    trx.setFreeTransfer(trx.getFreeTransfer()+5);
                                }
                                trx.setThread2aNo(THREAD2a+"-"+threadId);
                                break;
                            case THREAD2b :
                                if (trx.getBalance() > 150){
                                    trx.setFreeTransfer(trx.getFreeTransfer()+25);
                                }
                                trx.setThread2bNo(THREAD2b+"-"+threadId);
                                break;
                            case THREAD3 :
                                if (trx.getId() >100){
                                    trx.setAvgBalance((trx.getBalance()+trx.getPrevBalance())/2);
                                }
                                trx.setThread3No(THREAD3+"-"+threadId);
                                break;
                        }

                    }
                });

                listStepTrx.add(trx);
            }
            executor.shutdown();
            try {
                executor.awaitTermination(60, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.error(String.valueOf(e));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return listStepTrx;
    }

    private List<TransactionDto> getDataTrx(File file) throws Exception{
        listTrx = new ArrayList<>();
        final String originalFileName = file.getName();
        if (!file.getName().equalsIgnoreCase("Before Eod.csv")) {
            return null;
        }

        FileInputStream in = null;
        BufferedReader br = null;
        try {
            in = new FileInputStream(file);
            br = new BufferedReader(new InputStreamReader(in));
            br.readLine();// skip first line
            String s= null;

            while ((s = br.readLine()) != null){
                String[] data = s.split(";");
                TransactionDto trx = new TransactionDto();
                trx.setId(Integer.valueOf(data[0]));
                trx.setName(data[1]);
                trx.setAge(Integer.valueOf(data[2]));
                trx.setBalance(Integer.valueOf(data[3]));
                trx.setPrevBalance(Integer.valueOf(data[4]));
                trx.setAvgBalance(Integer.valueOf(data[5]));
                trx.setFreeTransfer(Integer.valueOf(data[6]));

                listTrx.add(trx);
            }

        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (in != null)
                in.close();
            if (br != null)
                br.close();
        }

        return listTrx;
    }

    private void writeFile() throws Exception {
        File fout = new File( "/home/bian/Documents/test-alami/src/main/resources/out/After Eod.csv");
        if (!fout.exists())
            fout.createNewFile();

        OutputStream out = null;
        StringBuffer sbOut = new StringBuffer();
        try {
            out = new FileOutputStream(fout);
            for (TransactionDto trx: listTrx) {
                sbOut.append(trx.toString());
                sbOut.append("\n");
            }

            if (!sbOut.toString().equals("")) {
                log.debug("writing  file...");
                out.write(sbOut.toString().getBytes());
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (out != null)
                out.close();
        }
    }

    private void waitAndTerminate(ExecutorService exec) {
        try {
            exec.shutdown();
            if (!exec.awaitTermination(60, TimeUnit.SECONDS)) {
                exec.shutdownNow();
            }
        } catch (Exception e) {
            exec.shutdownNow();
            log.error(e.getMessage(), e);
        }
    }
}
