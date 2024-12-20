package com.rvoros.fileprocessor.validator;

import com.rvoros.fileprocessor.exception.InvalidFileException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileValidatorTest {
    @Mock
    private MultipartFile multipartFile;

    private String[] fields = "18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A Bike|6.2|12.1".split("\\|");

    private FileValidator fileValidator = new FileValidator();

    @Test
    @DisplayName("When file is null then exception should be thrown")
    public void whenFileIsNullThenExceptionShouldBeThrown() {
        assertThrows(InvalidFileException.class, () -> fileValidator.validateEmpty(null), "Please make sure the uploaded file is not empty.");
    }

    @Test
    @DisplayName("When file is empty then exception should be thrown")
    public void whenFileIsEmptyThenExceptionShouldBeThrown() {
        when(multipartFile.isEmpty()).thenReturn(true);

        assertThrows(InvalidFileException.class, () -> fileValidator.validateEmpty(multipartFile), "Please make sure the uploaded file is not empty.");
    }

    @Test
    @DisplayName("When file is not empty then exception should not be thrown")
    public void whenFileIsNotEmptyThenExceptionShouldNotBeThrown() {
        when(multipartFile.isEmpty()).thenReturn(false);

        assertDoesNotThrow(() -> fileValidator.validateEmpty(multipartFile));
    }

    @Test
    @DisplayName("When fields are valid then exception should not be thrown")
    void whenFieldsAreValidThenExceptionShouldNotBeThrown() {
        assertDoesNotThrow(() -> fileValidator.validateLine(fields));
    }

    @Test
    @DisplayName("When a field is missing from a line then exception should be thrown")
    void whenFieldIsMissingFromALineThenExceptionShouldBeThrown() {
        assertThrows(InvalidFileException.class, () -> fileValidator.validateLine(new String[] {"1", "2"}), "The number of fields should be 7.");
    }

    @DisplayName("When a field is not valid then exception should be thrown")
    @ParameterizedTest
    @CsvSource({
            "0, UUID_INVALID, The value in the UUID field is not valid",
            "2, '', The Name field should not be empty",
            "4, '', The Transport field should not be empty",
            "6, '', The Top Speed field should not be empty",
            "6, a, The Top Speed field should be a number"
    })
    void whenAFieldIsNotValidThenExceptionShouldNotBeThrown(int idx, String value, String message) {
        String[] requestParam = Arrays.copyOf(fields, fields.length);

        requestParam[idx] = value;

        assertThrows(InvalidFileException.class, () -> fileValidator.validateLine(requestParam), message);
    }
}