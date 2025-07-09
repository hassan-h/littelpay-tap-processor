package com.littlepay.app.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvException;

public class CsvReader<T> {

    private final Class<T> type;

    public CsvReader(Class<T> type) {
        this.type = type;
    }

    public List<T> read(String filePath) throws IOException, CsvException {
        try (Reader reader = Files.newBufferedReader(Paths.get(filePath))) {
        	return buildCsv(reader);
        } catch (IOException e) {
            InputStream is = getClass().getClassLoader().getResourceAsStream("taps.csv");
            if (is == null) {
                throw new FileNotFoundException("File not found at path or classpath ");
            }
            try (Reader reader = new InputStreamReader(is)) {
                return buildCsv(reader);
            }
        }
    }
    
    private List<T> buildCsv(Reader reader) throws CsvException {
        return new CsvToBeanBuilder<T>(reader)
                .withType(type)
                .withIgnoreLeadingWhiteSpace(true)
                .build()
                .parse();
    }
}