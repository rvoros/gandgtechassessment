package com.rvoros.fileprocessor;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {
                "server.port=8091",
                "ipApiBaseUrl=http://localhost:8095"
        })
@WireMockTest(httpPort = 8095)
class FileProcessorRestApiTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void httpRequestTest() {
        String ipApiResponse = """
                {
                  "status": "success",
                  "country": "Australia",
                  "countryCode": "AU",
                  "isp": "Isp",
                  "org": "Org"
                }
                """;

        stubFor(any(urlMatching("/json/[\\.0-9]+\\?fields=status,country,countryCode,isp,org")).willReturn(okJson(ipApiResponse)));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new org.springframework.core.io.ClassPathResource("input.txt"));

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);

        String response = restTemplate.postForObject("http://localhost:" + port + "/file-processor", entity, String.class);

        String expected = "[{\"name\":\"John Smith\",\"transport\":\"Rides A Bike\",\"topSpeed\":12.1},{\"name\":\"Mike Smith\",\"transport\":\"Drives an SUV\",\"topSpeed\":95.5},{\"name\":\"Jenny Walters\",\"transport\":\"Rides A Scooter\",\"topSpeed\":15.3}]";

        assertEquals(expected, response);
    }
}