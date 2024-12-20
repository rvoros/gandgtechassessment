package com.rvoros.fileprocessor.validator;

import com.rvoros.fileprocessor.exception.InvalidFileException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.UUID;

@Component
public class FileValidator {
    public void validateEmpty(MultipartFile file) {
        if (Objects.isNull(file) || file.isEmpty()) {
            throw new InvalidFileException("Please make sure the uploaded file is not empty.");
        }
    }

    public void validateLine(String[] fields) {
        if (fields.length < 7)
            throw new InvalidFileException("The number of fields should be 7.");

        try {
            UUID.fromString(fields[0]);
        } catch (IllegalArgumentException e) {
            throw new InvalidFileException("The value in the UUID field is not valid");
        }

        if (fields[2].isBlank())
            throw new InvalidFileException("The Name field should not be empty");

        if (fields[4].isBlank())
            throw new InvalidFileException("The Transport field should not be empty");

        if (fields[6].isBlank())
            throw new InvalidFileException("The Top Speed field should not be empty");

        try {
            Double.parseDouble(fields[6]);
        } catch (NumberFormatException ne) {
            throw new InvalidFileException("The Top Speed field should be a number");
        }
    }
}
