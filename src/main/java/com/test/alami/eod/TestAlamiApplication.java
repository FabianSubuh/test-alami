package com.test.alami.eod;

import com.test.alami.eod.batch.EodTransactionJob;
import com.test.alami.eod.config.AutoWiringSpringBeanJobFactory;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class TestAlamiApplication {

	public static void main(String[] args) {
		SpringApplication.run(TestAlamiApplication.class, args);
	}

}
