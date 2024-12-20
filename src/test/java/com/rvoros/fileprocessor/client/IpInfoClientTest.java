package com.rvoros.fileprocessor.client;

import com.rvoros.fileprocessor.entity.RequestIpInfo;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IpInfoClientTest {
    @Mock
    private RestClient restClient;

    @InjectMocks
    private IpInfoClient client;

    @Test
    @DisplayName("When ip info client is called then it should return the response from the endpoint")
    public void whenIpInfoClientIsCalledThenItShouldReturnTheResponseFromTheEndpoint() {
        RequestIpInfo response = new RequestIpInfo("success", "Ireland", "IE", "Amazon.com", "AWS");

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRemoteAddr()).thenReturn("1.2.3.4");

        RestClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/json/1.2.3.4?fields=status,country,countryCode,isp,org")).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(RequestIpInfo.class)).thenReturn(response);

        RequestIpInfo ipInfo = client.getIpInfo(request);

        assertEquals(response, ipInfo);
    }
}