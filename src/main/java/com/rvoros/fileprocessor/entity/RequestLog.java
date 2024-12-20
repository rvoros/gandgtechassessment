package com.rvoros.fileprocessor.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Entity
public class RequestLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String requestUri;

    private long requestTimestamp;

    private int responseCode;

    private String requestIpAddress;

    private String requestCountryCode;

    private String requestIpProvider;

    private long timeLapsedInMs;
}
