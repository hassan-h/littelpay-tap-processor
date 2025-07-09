package com.littlepay.app.util;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

public class CsvWriter<T> {

    private final Class<T> type;

    public CsvWriter(Class<T> type) {
        this.type = type;
    }

    public void write(String filePath, List<T> data) throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
    	
    	Path path = Paths.get(filePath);
        Path parentDir = path.getParent();
        
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }
    	
        try (Writer writer = Files.newBufferedWriter(Paths.get(filePath));
        		CSVWriter csvWriter = new CSVWriter(writer)) {
        	csvWriter.writeNext(Constants.HEADERS, false);
        	ColumnPositionMappingStrategy<T> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(type); 
            StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(writer)
            		.withMappingStrategy(strategy)
                    .withApplyQuotesToAll(false)
                    .build();
            beanToCsv.write(data);
        }
    }
}