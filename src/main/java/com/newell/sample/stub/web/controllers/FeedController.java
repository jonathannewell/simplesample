package com.newell.sample.stub.web.controllers;

import com.newell.sample.stub.web.filters.SimpleFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Controller
public class FeedController {

    private static final Logger logger = LoggerFactory.getLogger(FeedController.class);

    private static final String AUDIT_KEY = "auditId";

    @RequestMapping(method = RequestMethod.POST, path = "/process/feed")
    public ResponseEntity processRequest(@RequestBody Map<String,Object> requestMap)
    {
        HttpStatus status = HttpStatus.OK;

        if(requestMap == null)
        {
            requestMap = new HashMap<>();
        }

        logger.info(String.format("%s - Received Request Map with [%s] items!", SimpleFilter.getCurrentRequestId(), requestMap.size()));

        if(requestMap.containsKey(AUDIT_KEY))
        {
            String action = (String)requestMap.get(AUDIT_KEY);

            logger.info(String.format("Request[%s] has auditId [%s]", SimpleFilter.getCurrentRequestId(), action));

            if(action.equalsIgnoreCase("fail")) {
                status = HttpStatus.INTERNAL_SERVER_ERROR;
            } else
            if(action.equalsIgnoreCase("bad-request")) {
                status = HttpStatus.BAD_REQUEST;
            } else
            if(action.equalsIgnoreCase("bad-gateway")) {
                status = HttpStatus.BAD_GATEWAY;
            } else
            if(action.equalsIgnoreCase("accept")) {
                status = HttpStatus.ACCEPTED;
            } else
            if(action.equalsIgnoreCase("conflict")) {
                status = HttpStatus.CONFLICT;
            }
        }

        return new ResponseEntity<>(status);
    }
}
