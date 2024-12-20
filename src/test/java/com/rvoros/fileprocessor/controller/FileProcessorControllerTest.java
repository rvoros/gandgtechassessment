package com.rvoros.fileprocessor.controller;

import com.rvoros.fileprocessor.client.IpInfoClient;
import com.rvoros.fileprocessor.entity.RequestIpInfo;
import com.rvoros.fileprocessor.entity.ResponseItem;
import com.rvoros.fileprocessor.exception.InvalidFileException;
import com.rvoros.fileprocessor.exception.InvalidRequestIpException;
import com.rvoros.fileprocessor.service.FileProcessorService;
import com.rvoros.fileprocessor.service.RequestLogService;
import com.rvoros.fileprocessor.validator.RequestValidator;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FileProcessorController.class)
class FileProcessorControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IpInfoClient ipInfoClient;

    @MockitoBean
    private RequestValidator requestValidator;

    @MockitoBean
    private FileProcessorService fileProcessor;

    @MockitoBean
    private RequestLogService requestLogService;

    @Mock
    private RequestIpInfo requestIpInfo;

    private MockMultipartFile multipartFile = new MockMultipartFile("file", "input.txt", "text/plain", "test input".getBytes());

    private FileProcessorController controller;

    @Test
    @DisplayName("when file is uploaded then the endpoint should return the outcome file")
    void whenFileIsUploadedThenTheEndpointShouldReturnTheOutcomeFile() throws Exception {
        List<ResponseItem> fileProcessorResponse = List.of(new ResponseItem("name", "transport", 95.5));

        when(ipInfoClient.getIpInfo(any())).thenReturn(requestIpInfo);
        when(fileProcessor.process(multipartFile)).thenReturn(fileProcessorResponse);

        mockMvc.perform(multipart("/file-processor").file(multipartFile))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"OutcomeFile.json\""))
                .andExpect(content().json("[{'name':'name','transport':'transport','topSpeed':95.5}]"));

        verify(ipInfoClient, times(1)).getIpInfo(any(HttpServletRequest.class));
        verify(requestValidator, times(1)).validateIp(requestIpInfo);
        verify(fileProcessor, times(1)).process(multipartFile);
        verify(requestLogService, times(1)).saveRequestLog(any(HttpServletRequest.class), any(RequestIpInfo.class), anyLong(), anyLong(), anyInt());
    }

    @Test
    @DisplayName("when ip is blocked then 403 is returned")
    void whenIpIsBlockedThen403IsReturned() throws Exception {
        doThrow(new InvalidRequestIpException("Invalid request IP")).when(requestValidator).validateIp(any());

        mockMvc.perform(multipart("/file-processor").file(multipartFile))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Invalid request IP"));

        verify(ipInfoClient, times(1)).getIpInfo(any());
        verify(requestValidator, times(1)).validateIp(any());
        verify(fileProcessor, times(0)).process(any());
        verify(requestLogService, times(1)).saveRequestLog(any(), any(), anyLong(), anyLong(), anyInt());
    }

    @Test
    @DisplayName("when file is not valid then 403 is returned")
    void whenFileIsNotValidThen403IsReturned() throws Exception {
        doThrow(new InvalidFileException("Invalid file")).when(fileProcessor).process(any());

        mockMvc.perform(multipart("/file-processor").file(multipartFile))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Invalid file"));

        verify(ipInfoClient, times(1)).getIpInfo(any());
        verify(requestValidator, times(1)).validateIp(any());
        verify(fileProcessor, times(1)).process(any());
        verify(requestLogService, times(1)).saveRequestLog(any(), any(), anyLong(), anyLong(), anyInt());
    }
}