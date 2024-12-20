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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class FileProcessorController {
    @Autowired
    private IpInfoClient ipInfoClient;

    @Autowired
    private RequestValidator requestValidator;

    @Autowired
    private FileProcessorService fileProcessor;

    @Autowired
    private RequestLogService requestLogService;

    @PostMapping("/file-processor")
    public ResponseEntity<?> processFile(
            @RequestParam(name = "skipValidation", required = false) String skipValidation,
            @RequestParam("file") MultipartFile file, HttpServletRequest request) {
        final long requestStartTimeInMs = System.currentTimeMillis();

        final RequestIpInfo ipInfo = ipInfoClient.getIpInfo(request);

        ResponseEntity<?> responseEntity;
        try {
            requestValidator.validateIp(ipInfo);

            final List<ResponseItem> response = fileProcessor.process(file);

            responseEntity = ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                    // the line below will make sure the response is downloaded instead of rendered by the browser
                    .header("Content-Disposition", "attachment; filename=\"OutcomeFile.json\"")
                    .body(response);
        } catch (InvalidFileException | InvalidRequestIpException exc) {
            responseEntity = ResponseEntity.status(403).body(exc.getMessage());
        }

        final long requestEndTimeInMs = System.currentTimeMillis();

        requestLogService.saveRequestLog(
                request,
                ipInfo,
                requestStartTimeInMs,
                requestEndTimeInMs,
                responseEntity.getStatusCode().value()
        );

        return responseEntity;
    }
}
