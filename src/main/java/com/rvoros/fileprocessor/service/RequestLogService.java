package com.rvoros.fileprocessor.service;

import com.rvoros.fileprocessor.entity.RequestIpInfo;
import com.rvoros.fileprocessor.entity.RequestLog;
import com.rvoros.fileprocessor.repository.RequestLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RequestLogService {
    @Autowired
    private RequestLogRepository requestLogRepository;

    public void saveRequestLog(HttpServletRequest request,
                               RequestIpInfo ipInfo,
                               long requestStartTimeInMs,
                               long requestEndTimeInMs,
                               int responseCode) {

        final var requestLog = new RequestLog(
                null,
                request.getRequestURI(),
                requestStartTimeInMs,
                responseCode,
                request.getRemoteAddr(),
                ipInfo.countryCode(),
                ipInfo.isp() + " : " + ipInfo.org(),
                requestEndTimeInMs - requestStartTimeInMs
        );

        requestLogRepository.save(requestLog);
    }
}
