package com.rvoros.fileprocessor.service;

import com.rvoros.fileprocessor.entity.ResponseItem;
import com.rvoros.fileprocessor.exception.InvalidFileException;
import com.rvoros.fileprocessor.validator.FileValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileProcessorServiceTest {
    @Mock
    private MultipartFile multipartFile;

    private String fileContent = """
            18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A Bike|6.2|12.1
            3ce2d17b-e66a-4c1e-bca3-40eb1c9222c7|2X2D24|Mike Smith|Likes Grape|Drives an SUV|35.0|95.5
        """;

    @Mock
    private FileValidator fileValidator;

    @InjectMocks
    private FileProcessorService fileProcessorService;


    @Test
    @DisplayName("When file empty validation fails then exceptions should be thrown")
    public void whenFileEmptyValidationFailsThenExceptionShouldBeThrown() {
        doThrow(new InvalidFileException("error")).when(fileValidator).validateEmpty(any());

        assertThrows(InvalidFileException.class, () -> fileProcessorService.process(multipartFile));
    }

    @Test
    @DisplayName("When file line validation fails then exceptions should be thrown")
    public void whenFileLineValidationFailsThenExceptionShouldBeThrown() throws IOException {
        InputStream is = new ByteArrayInputStream(fileContent.getBytes());
        when(multipartFile.getInputStream()).thenReturn(is);

        doThrow(new InvalidFileException("error")).when(fileValidator).validateLine(any());

        assertThrows(InvalidFileException.class, () -> fileProcessorService.process(multipartFile));
    }

    @Test
    @DisplayName("When reading the file fails then exceptions should be thrown")
    public void whenReadingTheFileFailsThenExceptionShouldBeThrown() throws IOException {
        InputStream is = new ByteArrayInputStream(fileContent.getBytes());
        when(multipartFile.getInputStream()).thenThrow(new IOException());

        assertThrows(InvalidFileException.class, () -> fileProcessorService.process(multipartFile));
    }

    @Test
    @DisplayName("When file is valid then the content for the output file should be returned")
    public void whenFileIsValidThenTheContentForTheOutputFileShouldBeReturned() throws IOException {
        InputStream is = new ByteArrayInputStream(fileContent.getBytes());
        when(multipartFile.getInputStream()).thenReturn(is);

        List<ResponseItem> result = fileProcessorService.process(multipartFile);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("John Smith", result.get(0).name());
        assertEquals("Rides A Bike", result.get(0).transport());
        assertEquals(12.1, result.get(0).topSpeed());
        assertEquals("Mike Smith", result.get(1).name());
        assertEquals("Drives an SUV", result.get(1).transport());
        assertEquals(95.5, result.get(1).topSpeed());
    }
}