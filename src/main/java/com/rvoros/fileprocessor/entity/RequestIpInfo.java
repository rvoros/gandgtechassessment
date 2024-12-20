package com.rvoros.fileprocessor.entity;

public record RequestIpInfo(
        String status,
        String country,
        String countryCode,
        String isp,
        String org
) {
}
