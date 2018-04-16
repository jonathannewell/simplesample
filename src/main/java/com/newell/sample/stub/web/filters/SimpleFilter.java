package com.newell.sample.stub.web.filters;

import com.newell.sample.stub.utils.HttpUtils;
import com.newell.sample.stub.utils.LoggingServletResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SimpleFilter implements Filter {

    private static Logger LOGGER = LoggerFactory.getLogger(SimpleFilter.class);

    private static final ThreadLocal<String> requestIds = ThreadLocal.withInitial(() -> {return "No Id";});

    @Value("${filter.log-request.enabled:true}")
    private boolean logRequest;

    @Value("${filter.log-request.detailed:false}")
    private boolean logRequestDetails;

    @Value("${filter.log-response.enabled:true}")
    private boolean logResponse;

    @Value("${filter.log-response.detailed:false}")
    private boolean logResponseDetails;

    @Value("${filter.log-request.headers:false}")
    private boolean headerDumpEnabled;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOGGER.debug("*****************   SIMPLE RESPONSE LOGGING FILTER INITIALIZING!!!  **********************");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String requestID = UUID.randomUUID().toString();
        requestIds.set(requestID);

        try {

            HttpServletRequest request = (HttpServletRequest) servletRequest;
            HttpServletResponse response = (HttpServletResponse) servletResponse;

            if (logRequest) {
                LOGGER.info(String.format("Received [%s] request from [%s] on [%s] for path [%s] ASSIGNED ID => %s",
                        request.getMethod(),
                        servletRequest.getRemoteAddr(),
                        request.getProtocol(),
                        request.getServletPath(),
                        requestID));
            }

            if (headerDumpEnabled)
                HttpUtils.dumpHttpHeaders(request, String.format("[%s] - ##### Http-Headers  #####", requestID));


            if (logRequestDetails) {
                LOGGER.info(String.format("[%s] - Request Details =>\nPROTOCOL: %s\n" +
                                "ENCODING: %s\nCONTENT-TYPE: %s\nSERVLET-PATH: %s\nCONTEXT-PATH: %s\n" +
                                "METHOD: %s\nAUTH: %s\nTRANSLATED-PATH: %s\nQUERY-STRING: %s\n" +
                                "DISPATCHER-TYPE: %s\n",
                        requestID,
                        request.getProtocol(),
                        request.getCharacterEncoding(),
                        request.getContentType(),
                        request.getServletPath(),
                        request.getContextPath(),
                        request.getMethod(),
                        request.getAuthType(),
                        request.getPathTranslated(),
                        request.getQueryString(),
                        request.getDispatcherType().name()));
            }


            HttpServletResponse wrappedResponse = logResponseDetails ? new LoggingServletResponseWrapper(response) : response;
            filterChain.doFilter(request, wrappedResponse);

            if (logResponse) {
                LOGGER.info(String.format("[%s] - Response Status => %s", requestID, wrappedResponse.getStatus()));
            }

            if (logResponseDetails) {
                LoggingServletResponseWrapper responseWrapper = (LoggingServletResponseWrapper) wrappedResponse;
                LOGGER.trace(String.format("[%s] - FULL RESPONSE =>\n%s", requestID, responseWrapper.getContent()));
                response.getOutputStream().write(responseWrapper.getContentAsBytes());
            }
        }
        finally {
            //Ensure we don't leak id's in our thread local and eat up heap!
            requestIds.remove();
        }
    }

    public static String getCurrentRequestId()
    {
        return requestIds.get();
    }

    @Override
    public void destroy() {
        LOGGER.debug("*****************   SIMPLE RESPONSE LOGGING FILTER BEING DESTROYED!!!  **********************");
    }

    public class CopyingPrintWriter extends PrintWriter {

        private StringBuilder copy = new StringBuilder();

        public CopyingPrintWriter(Writer writer) {
            super(writer);
        }

        @Override
        public void write(int c) {
            copy.append((char) c);
            super.write(c);
        }

        @Override
        public void write(char[] chars, int offset, int length) {
            copy.append(chars, offset, length);
            super.write(chars, offset, length);
        }

        @Override
        public void write(String string, int offset, int length) {
            copy.append(string, offset, length);
            super.write(string, offset, length);
        }

        public String getCopy() {
            return copy.toString();
        }

    }
}


