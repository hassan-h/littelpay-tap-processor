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
    	System.out.println("Littlepay Coding Exercise Application Started.");
    	
    	System.out.println("Args received:");
        for (int i = 0; i < args.length; i++) {
            System.out.printf("args[%d] = %s%n", i, args[i]);
        }

        String inputFilePath = args.length > 0 ? args[0] : inputFile;
        String outputFilePath = args.length > 1 ? args[1] : outputFile;

        try {
            System.out.println("Reading taps from: " + inputFilePath);
            List<Tap> taps = tapReader.read(inputFilePath);
            System.out.println("Successfully read " + taps.size() + " taps.");

            System.out.println("Processing taps...");
            List<Trip> trips = tripService.generateTrips(taps);
            System.out.println("Generated " + trips.size() + " trips.");

            System.out.println("Writing trips to: " + outputFilePath);
            tripWriter.write(outputFilePath, trips);
            System.out.println("Trips written successfully to " + outputFilePath);

        } catch (IOException e) {
            System.err.println("File I/O error: " + e.getMessage());
            e.printStackTrace();
        } catch (CsvException e) {
            System.err.println("CSV parsing/writing error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}