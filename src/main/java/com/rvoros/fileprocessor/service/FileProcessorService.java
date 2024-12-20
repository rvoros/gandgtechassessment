package com.rvoros.fileprocessor.service;

import com.rvoros.fileprocessor.entity.ResponseItem;
import com.rvoros.fileprocessor.exception.InvalidFileException;
import com.rvoros.fileprocessor.validator.FileValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

@Service
public class FileProcessorService{
    @Autowired
    FileValidator fileValidator;

    public List<ResponseItem> process(MultipartFile file) {
        fileValidator.validateEmpty(file);

        List<ResponseItem> response = new ArrayList<>();
        List<String> fileValidationErrors = new ArrayList<>();

        Scanner scanner = null;
        try {
            InputStream is = file.getInputStream();
            scanner = new Scanner(is);
            int lineNumber = 1;

            while (scanner.hasNextLine()) {
                String nextLine = scanner.nextLine();
                String[] fields = nextLine.split("\\|");

                try {
                    fileValidator.validateLine(fields);
                    response.add(new ResponseItem(
                            fields[2],
                            fields[4],
                            Double.parseDouble(fields[6])
                    ));
                } catch (InvalidFileException exception) {
                    fileValidationErrors.add(String.format("Line %d: %s", lineNumber, exception.getMessage()));
                }

                lineNumber++;
            }

        } catch (IOException ioException) {
            throw new InvalidFileException("There was a problem reading the file. It might be corrupted.");
        } finally {
            if (Objects.nonNull(scanner)) {
                scanner.close();
            }
        }

        if (!fileValidationErrors.isEmpty()) {
            throw new InvalidFileException("Some lines in the file are invalid.\n" + fileValidationErrors.stream().collect(Collectors.joining(";\n")));
        }

        return response;
    }
}
