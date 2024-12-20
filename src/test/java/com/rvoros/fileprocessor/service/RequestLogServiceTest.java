package com.rvoros.fileprocessor.service;

import com.rvoros.fileprocessor.entity.RequestIpInfo;
import com.rvoros.fileprocessor.entity.RequestLog;
import com.rvoros.fileprocessor.repository.RequestLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestLogServiceTest {
    @Captor
    private ArgumentCaptor<RequestLog> requestLog;

    @Mock
    private RequestLogRepository requestLogRepository;

    @InjectMocks
    private RequestLogService requestLogService;

    @Test
    @DisplayName("When save request log called then it should be saved in the repository")
    public void whenSaveRequestLogCalledThenItShouldBeSaveInTheRepository() {
        long requestStartTimeInMs = 1;
        long requestEndTimeInMs = 5;
        int responseCode = 200;
        String requestUri = "/request/";
        String remoteAddress = "1.2.3.4";

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn(requestUri);
        when(request.getRemoteAddr()).thenReturn(remoteAddress);

        RequestIpInfo ipInfo = new RequestIpInfo("", "", "countryCode", "Isp", "Org");

        requestLogService.saveRequestLog(request, ipInfo, requestStartTimeInMs, requestEndTimeInMs, responseCode);

        verify(requestLogRepository, times(1)).save(requestLog.capture());

        assertNull(requestLog.getValue().getId());
        assertEquals(requestUri, requestLog.getValue().getRequestUri());
        assertEquals(requestStartTimeInMs, requestLog.getValue().getRequestTimestamp());
        assertEquals(responseCode, requestLog.getValue().getResponseCode());
        assertEquals(remoteAddress, requestLog.getValue().getRequestIpAddress());
        assertEquals(ipInfo.isp() + " : " + ipInfo.org(), requestLog.getValue().getRequestIpProvider());
        assertEquals(requestEndTimeInMs - requestStartTimeInMs, requestLog.getValue().getTimeLapsedInMs());
    }

}