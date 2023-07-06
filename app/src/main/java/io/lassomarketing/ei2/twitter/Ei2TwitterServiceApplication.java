package io.lassomarketing.ei2.twitter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;

@ConfigurationPropertiesScan
@SpringBootApplication
@ComponentScan(basePackages = {
        "io.lassomarketing.ei2.common",
        "io.lassomarketing.ei2.config",
        "io.lassomarketing.ei2.twitter"
})
public class Ei2TwitterServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(Ei2TwitterServiceApplication.class, args);
    }
}
