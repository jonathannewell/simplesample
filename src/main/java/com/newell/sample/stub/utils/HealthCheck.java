package com.newell.sample.stub.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class HealthCheck implements HealthIndicator {

    Logger logger = LoggerFactory.getLogger(HealthCheck.class);

    @Override
    public Health health() {
        int errorCode = check(); // perform some specific health check
        if (errorCode != 0) {
            return Health.down()
                    .withDetail("Error Code", errorCode).build();
        }
        return Health.up().build();
    }

    public int check() {
        try {
            RestTemplate template = new RestTemplate();
            ResponseEntity resopnse = template.postForEntity("http://httpbin.org/anything", "{ message: \"this is a test\" }", String.class);

            logger.info(String.format("HttpBin Request Status [%s]\nResponse [%s]", resopnse.getStatusCode(), resopnse.getBody()));

            return resopnse.getStatusCode().is2xxSuccessful() ? 0 : 1;
        }
        catch(Throwable t)
        {
            return 1;
        }
    }
}