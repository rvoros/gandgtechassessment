package com.rvoros.fileprocessor.client;

import com.rvoros.fileprocessor.entity.RequestIpInfo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class IpInfoClient {
    @Autowired
    private RestClient restClient;

    public RequestIpInfo getIpInfo(HttpServletRequest request) {
        return restClient.get()
                .uri(String.format("/json/%s?fields=status,country,countryCode,isp,org", request.getRemoteAddr()))
                .retrieve()
                .body(RequestIpInfo.class);
    }
}
