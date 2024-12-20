package com.rvoros.fileprocessor.validator;

import com.rvoros.fileprocessor.entity.RequestIpInfo;
import com.rvoros.fileprocessor.exception.InvalidFileException;
import com.rvoros.fileprocessor.exception.InvalidRequestIpException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class RequestValidatorTest {
    private RequestValidator requestValidator = new RequestValidator();

    @DisplayName("When country is blocked then exception should be thrown")
    @ParameterizedTest
    @ValueSource(strings = { "CN", "ES", "US" })
    void whenCountryIsBlockedThenExceptionShouldBeThrown(String countryCode) {
        RequestIpInfo request = new RequestIpInfo("", "", countryCode, "", "");
        assertThrows(InvalidRequestIpException.class, () -> requestValidator.validateIp(request), "Calls from your country are blocked!");
    }

    @Test
    @DisplayName("When country is not blocked then exception should not be thrown")
    void whenCountryIsNotBlockedThenExceptionShouldNotBeThrown() {
        RequestIpInfo request = new RequestIpInfo("", "", "GB", "", "");
        assertDoesNotThrow(() -> requestValidator.validateIp(request));
    }

    @DisplayName("When ISP provider is blocked then exception should be thrown")
    @ParameterizedTest
    @ValueSource(strings = { "AWS", "Google Cloud", "Microsoft Azure Cloud" })
    void whenISPProviderIsBlockedThenExceptionShouldBeThrown(String org) {
        RequestIpInfo request = new RequestIpInfo("", "", "", "", org);
        assertThrows(InvalidRequestIpException.class, () -> requestValidator.validateIp(request), "Calls from your ISP provider (Data Center) are blocked!");
    }

    @Test
    @DisplayName("When ISP provider is not blocked then exception should not be thrown")
    void whenISPProviderIsNotBlockedThenExceptionShouldNotBeThrown() {
        RequestIpInfo request = new RequestIpInfo("", "", "", "", "Any Non Blocked");
        assertDoesNotThrow(() -> requestValidator.validateIp(request));
    }
}