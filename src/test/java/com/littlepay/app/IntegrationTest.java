package com.littlepay.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.littlepay.app.model.Tap;
import com.littlepay.app.model.Trip;
import com.littlepay.app.service.TripService;
import com.littlepay.app.util.CsvReader;
import com.littlepay.app.util.CsvWriter;

@SpringBootTest
class IntegrationTest {
	
	@Autowired
	private TripService tripService;

    @Test
    void testEndToEndProducesExpectedTripsCsv() throws Exception {
    	Path path = Paths.get(Objects.requireNonNull(getClass().getResource("/taps.csv")).toURI());
        List<Tap> taps = new CsvReader<>(Tap.class)
                .read(path.toString());

        // Generate trips
        List<Trip> trips = tripService.generateTrips(taps);

        // Write to temporary output
        Path temp = Files.createTempFile("trips", ".csv");
        new CsvWriter<>(Trip.class).write(temp.toString(), trips);

        // Verify file exists and has header and same number of data rows as trips list
        List<String> lines = Files.readAllLines(temp);
        assertFalse(lines.isEmpty(), "CSV should not be empty");
        assertTrue(lines.get(0).contains("Started,Finished,DurationSecs"), "Header missing");
        assertEquals(trips.size() + 1, lines.size(), "Row count should match trips + header");

        // Optionally, spot-check the first data line format
        String firstData = lines.get(1);
        assertTrue(firstData.split(",")[0].matches("\\d{2}-\\d{2}-\\d{4}.*"),
                   "Date format incorrect in first data row");
    }
}
