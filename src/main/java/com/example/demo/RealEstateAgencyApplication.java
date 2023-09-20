package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;

@SpringBootApplication
//@EnableScheduling
public class RealEstateAgencyApplication {

    public static void main(String[] args) {
        SpringApplication.run(RealEstateAgencyApplication.class, args);
    }



    @ConfigurationProperties(prefix = "real-estate-datasource")
    @Bean
    public DataSource realEstateDatasource() {

        return DataSourceBuilder
                .create()
                .build();
    }


    @ConfigurationProperties(prefix = "agency-datasource")
    @Bean
    public DataSource agencyDatasource() {

        return DataSourceBuilder
                .create()
                .build();
    }
}


