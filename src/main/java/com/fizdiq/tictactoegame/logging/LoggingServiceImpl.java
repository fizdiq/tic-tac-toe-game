package com.fizdiq.tictactoegame.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fizdiq.tictactoegame.constant.RestConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

@Slf4j
@Component
public class LoggingServiceImpl implements LoggingService {

    private static final List<String> HEADERS_TO_SKIP = Arrays.asList("authorization",
            "token",
            "security",
            "oauth",
            "auth");

    @Override
    public void logRequest(HttpServletRequest httpServletRequest, Object body) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        Map<String, String> parameters = buildParametersMap(httpServletRequest);

        MDC.clear();
        Map<String, String> headers = buildHeadersMap(httpServletRequest);
        if(generateIdHeader(httpServletRequest, RestConstants.HeaderName.REQUEST_ID))
            headers.put(RestConstants.HeaderName.REQUEST_ID, MDC.get(RestConstants.HeaderName.REQUEST_ID));
        if(generateIdHeader(httpServletRequest,RestConstants.HeaderName.CORRELATION_ID, body))
            headers.put(RestConstants.HeaderName.CORRELATION_ID, MDC.get(RestConstants.HeaderName.CORRELATION_ID));

        stringBuilder.append("REQUEST ");
        stringBuilder.append("requester=[").append(httpServletRequest.getRemoteAddr()).append("] ");
        stringBuilder.append("method=[").append(httpServletRequest.getMethod()).append("] ");
        stringBuilder.append("path=[").append(httpServletRequest.getRequestURI()).append("] ");
        stringBuilder.append("headers=[").append(buildHeadersMap(httpServletRequest)).append("] ");

        if (!parameters.isEmpty()) {
            stringBuilder.append("parameters=[").append(parameters).append("] ");
        }

        if (body != null) {
            stringBuilder.append("body=[").append(body).append("]");
        }

        log.info(stringBuilder.toString());
    }

    @Override
    public void logResponse(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object body) {
        StringBuilder stringBuilder = new StringBuilder();

        if (!httpServletRequest.getRequestURI().toLowerCase().contains("actuator")) {
            Map<String, String> headers = buildHeadersMap(httpServletResponse);
            if(!StringUtils.hasLength(httpServletResponse.getHeader(RestConstants.HeaderName.REQUEST_ID)))
                headers.put(RestConstants.HeaderName.REQUEST_ID, MDC.get(RestConstants.HeaderName.REQUEST_ID));
            if(!StringUtils.hasLength(httpServletResponse.getHeader(RestConstants.HeaderName.CORRELATION_ID)))
                headers.put(RestConstants.HeaderName.CORRELATION_ID, MDC.get(RestConstants.HeaderName.CORRELATION_ID));

            stringBuilder.append("RESPONSE ");
            stringBuilder.append("method=[").append(httpServletRequest.getMethod()).append("] ");
            stringBuilder.append("path=[").append(httpServletRequest.getRequestURI()).append("] ");
            stringBuilder.append("responseHeaders=[").append(buildHeadersMap(httpServletResponse)).append("] ");
            stringBuilder.append("responseBody=[").append(body).append("] ");

            log.info(stringBuilder.toString());
        }
    }

    private Map<String, String> buildParametersMap(HttpServletRequest httpServletRequest) {
        Map<String, String> resultMap = new HashMap<>();
        Enumeration<String> parameterNames = httpServletRequest.getParameterNames();

        while (parameterNames.hasMoreElements()) {
            String key = parameterNames.nextElement();
            String value = httpServletRequest.getParameter(key);
            resultMap.put(key, value);
        }

        return resultMap;
    }

    private Map<String, String> buildHeadersMap(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            String value = request.getHeader(key);
            if (HEADERS_TO_SKIP.stream().noneMatch(h -> h.toLowerCase().contains(key.toLowerCase())
                    || key.toLowerCase().contains(h.toLowerCase()))) {
                map.put(key, value);
            }
        }
        return map;
    }

    private boolean generateIdHeader(HttpServletRequest request, String key) {
        boolean result = false;
        String id = request.getHeader(key);
        if (!StringUtils.hasLength(id)) {
            id = UUID.randomUUID().toString();
            result = true;
        }
        MDC.put(key, id);
        return result;
    }

    private boolean generateIdHeader(HttpServletRequest request, String key, Object body) throws IOException {
        boolean result = false;
        String id = request.getHeader(key);
        if (!StringUtils.hasLength(id)) {
            id = UUID.randomUUID().toString();
            result = true;
        }
        if (body != null && key.equalsIgnoreCase(RestConstants.HeaderName.CORRELATION_ID)) {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> map = objectMapper.readValue((String) body, HashMap.class);
            String correlationId;
            correlationId = map.getOrDefault("requestId", id);
            MDC.put(RestConstants.HeaderName.CORRELATION_ID, correlationId);
            result = true;
        }
        return result;
    }

    private Map<String, String> buildHeadersMap(HttpServletResponse response) {
        Map<String, String> map = new HashMap<>();

        Collection<String> headerNames = response.getHeaderNames();
        for (String header : headerNames) {
            map.put(header, response.getHeader(header));
        }

        return map;
    }
}
