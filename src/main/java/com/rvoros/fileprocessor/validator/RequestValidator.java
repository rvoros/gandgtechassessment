package com.rvoros.fileprocessor.validator;

import com.rvoros.fileprocessor.entity.RequestIpInfo;
import com.rvoros.fileprocessor.exception.InvalidRequestIpException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class RequestValidator {
    public void validateIp(RequestIpInfo requestIpInfo) {
        // checking country
        if ("CN".equalsIgnoreCase(requestIpInfo.countryCode())
                || "ES".equalsIgnoreCase(requestIpInfo.countryCode())
                || "US".equalsIgnoreCase(requestIpInfo.countryCode()))
            throw new InvalidRequestIpException("Calls from your country are blocked!");

        // checking ISP provider
        if (Objects.nonNull(requestIpInfo.org())
         && (requestIpInfo.org().startsWith("AWS")
                || requestIpInfo.org().startsWith("Google Cloud")
                || requestIpInfo.org().startsWith("Microsoft Azure Cloud")))
            throw new InvalidRequestIpException("Calls from your ISP provider (Data Center) are blocked!");
    }
}
