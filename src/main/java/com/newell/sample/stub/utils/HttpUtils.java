package com.newell.sample.stub.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.List;

public class HttpUtils {

    private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    public static void dumpHttpHeaders(HttpHeaders headers)
    {
        for(String key:headers.keySet())
        {
            logger.info("Header: " + key);

            List<String> hlist = headers.get(key);

            for(String header:hlist)
            {
                List<String> values = headers.getValuesAsList(key);

                for(String value: values) {
                    logger.info("\tValue: " + value);
                }
            }

        }
    }

    public static void dumpHttpHeaders(HttpServletRequest request, String message)
    {
        logger.info(message);

        Enumeration<String> headerNames = request.getHeaderNames();

        while(headerNames.hasMoreElements())
        {
            String name = headerNames.nextElement();
            logger.info("Header: " + name);

            Enumeration<String> headers = request.getHeaders(name);

            while(headers.hasMoreElements())
            {
                String header = headers.nextElement();
                logger.info("\tValue: " + header);
            }
        }
    }
}
