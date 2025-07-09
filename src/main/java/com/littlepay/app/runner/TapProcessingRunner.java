package com.littlepay.app.runner;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.littlepay.app.model.Tap;
import com.littlepay.app.model.Trip;
import com.littlepay.app.service.TripService;
import com.littlepay.app.util.CsvReader;
import com.littlepay.app.util.CsvWriter;
import com.opencsv.exceptions.CsvException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TapProcessingRunner implements CommandLineRunner{
	
	@Value("${app.input-file}")
    private String inputFile;

    @Value("${app.output-file}")
    private String outputFile;
	
	private final CsvReader<Tap> tapReader;
    private final CsvWriter<Trip> tripWriter;
    private final TripService tripService;

    public TapProcessingRunner(CsvReader<Tap> tapReader,
                               CsvWriter<Trip> tripWriter,
                               TripService tripService) {
        this.tapReader = tapReader;
        this.tripWriter = tripWriter;
        this.tripService = tripService;
    }

    @Override
    public void run(String... args) {
    	log.info("Littlepay Coding Exercise Application Started.");
    	
    	log.info("Args received:");
        for (int i = 0; i < args.length; i++) {
        	log.info("args[{}] = {}", i, args[i]);
        }

        String inputFilePath = args.length > 0 ? args[0] : inputFile;
        String outputFilePath = args.length > 1 ? args[1] : outputFile;

        try {
        	log.info("Reading taps from: {}", inputFilePath);
            List<Tap> taps = tapReader.read(inputFilePath);
            log.info("Successfully read {} taps.", taps.size());

            log.info("Processing taps...");
            List<Trip> trips = tripService.generateTrips(taps);
            log.info("Generated {} trips.", trips.size());

            log.info("Writing trips to: {}", outputFilePath);
            tripWriter.write(outputFilePath, trips);
            log.info("Trips written successfully to {}", outputFilePath);

        } catch (IOException e) {
        	log.error("File I/O error: {}", e.getMessage());
            e.printStackTrace();
        } catch (CsvException e) {
        	log.error("CSV parsing/writing error: {}", e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
        	log.error("An unexpected error occurred: {}", e.getMessage());
            e.printStackTrace();
        }
    }
}