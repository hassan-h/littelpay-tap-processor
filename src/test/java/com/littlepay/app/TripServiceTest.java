package com.littlepay.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.littlepay.app.enums.TapType;
import com.littlepay.app.enums.TripStatus;
import com.littlepay.app.model.Tap;
import com.littlepay.app.model.Trip;
import com.littlepay.app.service.TripService;

@SpringBootTest
class TripServiceTest {

	@Autowired
	private TripService tripService;

    @Test
    void testGenerateTrips_AllScenarios() {
    	Tap on1 = Tap.builder()
    	        .id(1)
    	        .dateTimeUTC(LocalDateTime.of(2023, 1, 22, 13, 0, 0))
    	        .tapType(TapType.ON)
    	        .stopId("Stop1")
    	        .companyId("Company1")
    	        .busID("Bus37")
    	        .pan("PAN1")
    	        .build();

    	Tap off1 = Tap.builder()
    	        .id(2)
    	        .dateTimeUTC(LocalDateTime.of(2023, 1, 22, 13, 5, 0))
    	        .tapType(TapType.OFF)
    	        .stopId("Stop2")
    	        .companyId("Company1")
    	        .busID("Bus37")
    	        .pan("PAN1")
    	        .build();

    	Tap on2 = Tap.builder()
    	        .id(3)
    	        .dateTimeUTC(LocalDateTime.of(2023, 1, 23, 8, 0, 0))
    	        .tapType(TapType.ON)
    	        .stopId("Stop1")
    	        .companyId("Company1")
    	        .busID("Bus37")
    	        .pan("PAN2")
    	        .build();

    	Tap off2 = Tap.builder()
    	        .id(4)
    	        .dateTimeUTC(LocalDateTime.of(2023, 1, 23, 8, 2, 0))
    	        .tapType(TapType.OFF)
    	        .stopId("Stop1")
    	        .companyId("Company1")
    	        .busID("Bus37")
    	        .pan("PAN2")
    	        .build();

    	Tap on3 = Tap.builder()
    	        .id(5)
    	        .dateTimeUTC(LocalDateTime.of(2023, 1, 24, 9, 30, 0))
    	        .tapType(TapType.ON)
    	        .stopId("Stop3")
    	        .companyId("Company1")
    	        .busID("Bus36")
    	        .pan("PAN3")
    	        .build();


        List<Tap> taps = new ArrayList<>(List.of(on1, off1, on2, off2, on3));
        List<Trip> trips = tripService.generateTrips(taps);

        assertEquals(3, trips.size());

        // Verify first trip: COMPLETED Stop1->Stop2
        Trip t1 = trips.get(0);
        assertEquals(TripStatus.COMPLETED, t1.getStatus());
        assertEquals("Stop1", t1.getFromStopId());
        assertEquals("Stop2", t1.getToStopId());
        assertEquals(new BigDecimal("3.25"), t1.getChargeAmount());

        // Verify second trip: CANCELLED Stop1->Stop1
        Trip t2 = trips.get(1);
        assertEquals(TripStatus.CANCELLED, t2.getStatus());
        assertEquals(BigDecimal.ZERO.setScale(2), t2.getChargeAmount());

        // Verify third trip: INCOMPLETE from Stop3
        Trip t3 = trips.get(2);
        assertEquals(TripStatus.INCOMPLETE, t3.getStatus());
        assertEquals(new BigDecimal("7.30"), t3.getChargeAmount());
        assertNull(t3.getFinished());
        assertNull(t3.getDurationSecs());
    }
}
