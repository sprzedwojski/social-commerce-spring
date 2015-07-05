package com.sp.socialcommerce.prop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;

/**
 * Created by szymon on 16.05.15.
 */
@Configuration
@PropertySource("classpath:properties/${envTarget:localhost}.properties")
//@PropertySource("classpath:properties/application.properties")
public class ApplicationProperties {

    @Autowired
    private Environment env;

    public String getProperty(String propName) {
        return env.getProperty(propName);
    }

//    @Bean
//    public static PropertySourcesPlaceholderConfigurer placeHolderConfigurer() {
//        return new PropertySourcesPlaceholderConfigurer();
//    }
}
