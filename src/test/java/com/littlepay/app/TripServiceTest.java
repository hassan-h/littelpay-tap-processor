package com.littlepay.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

	private Tap tap(int id, String stop, TapType type, String pan, String bus, String company, LocalDateTime time) {
        return Tap.builder()
                .id(id)
                .stopId(stop)
                .tapType(type)
                .pan(pan)
                .busID(bus)
                .companyId(company)
                .dateTimeUTC(time)
                .build();
    }
	
    @Test
    void testGenerateTrips_AllScenarios() {
    	
    	Tap on1 = tap(1, "Stop1", TapType.ON, "PAN1", "Bus37", "Company1", LocalDateTime.of(2023, 1, 22, 13, 0, 0));
    	Tap off1 = tap(2, "Stop2", TapType.OFF, "PAN1", "Bus37", "Company1", LocalDateTime.of(2023, 1, 22, 13, 5, 0));

    	Tap on2 = tap(3, "Stop1", TapType.ON, "PAN2", "Bus37", "Company1", LocalDateTime.of(2023, 1, 23, 8, 0, 0));
    	Tap off2 = tap(4, "Stop1", TapType.OFF, "PAN2", "Bus37", "Company1", LocalDateTime.of(2023, 1, 23, 8, 2, 0));

    	Tap on3 = tap(5, "Stop3", TapType.ON, "PAN3", "Bus36", "Company1", LocalDateTime.of(2023, 1, 24, 9, 30, 0));


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
    
    @Test
    void testGenerateTrips_NullAndEmptyInput() {
        assertTrue(tripService.generateTrips(null).isEmpty());
        assertTrue(tripService.generateTrips(List.of()).isEmpty());
    }

    @Test
    void testGenerateTrips_CompletedTrip() {
        Tap on = tap(1, "Stop1", TapType.ON, "PAN1", "Bus1", "Company1", LocalDateTime.now());
        Tap off = tap(2, "Stop2", TapType.OFF, "PAN1", "Bus1", "Company1", LocalDateTime.now().plusMinutes(5));
        Trip trip = tripService.generateTrips(new ArrayList<>(List.of(on, off))).get(0);
        assertEquals(TripStatus.COMPLETED, trip.getStatus());
        assertEquals("Stop1", trip.getFromStopId());
        assertEquals("Stop2", trip.getToStopId());
    }

    @Test
    void testGenerateTrips_CancelledTrip() {
        Tap on = tap(1, "Stop1", TapType.ON, "PAN1", "Bus1", "Company1", LocalDateTime.now());
        Tap off = tap(2, "Stop1", TapType.OFF, "PAN1", "Bus1", "Company1", LocalDateTime.now().plusMinutes(2));
        Trip trip = tripService.generateTrips(new ArrayList<>(List.of(on, off))).get(0);
        assertEquals(TripStatus.CANCELLED, trip.getStatus());
        assertEquals(BigDecimal.ZERO.setScale(2), trip.getChargeAmount());
    }

    @Test
    void testGenerateTrips_IncompleteTrip() {
        Tap on = tap(1, "Stop3", TapType.ON, "PAN3", "Bus3", "Company3", LocalDateTime.now());
        Trip trip = tripService.generateTrips(new ArrayList<>(List.of(on))).get(0);
        assertEquals(TripStatus.INCOMPLETE, trip.getStatus());
        assertEquals("Stop3", trip.getFromStopId());
        assertNull(trip.getToStopId());
    }

    @Test
    void testGenerateTrips_OrphanOff() {
        Tap off = tap(2, "Stop1", TapType.OFF, "PAN1", "Bus1", "Company1", LocalDateTime.now());
        List<Trip> trips = tripService.generateTrips(new ArrayList<>(List.of(off)));
        assertTrue(trips.isEmpty());
    }

    @Test
    void testGenerateTrips_DuplicateOn_BeforeOff() {
        Tap on1 = tap(1, "Stop1", TapType.ON, "PAN1", "Bus1", "Company1", LocalDateTime.now());
        Tap on2 = tap(2, "Stop2", TapType.ON, "PAN1", "Bus1", "Company1", LocalDateTime.now().plusMinutes(3));
        Tap off = tap(3, "Stop3", TapType.OFF, "PAN1", "Bus1", "Company1", LocalDateTime.now().plusMinutes(5));
        List<Trip> trips = tripService.generateTrips(new ArrayList<>(List.of(on1, on2, off)));
        assertEquals(2, trips.size());
        assertEquals(TripStatus.INCOMPLETE, trips.get(0).getStatus());
        assertEquals(TripStatus.COMPLETED, trips.get(1).getStatus());
    }

    @Test
    void testGenerateTrips_MixedTripScenarios() {
        Tap on1 = tap(1, "Stop1", TapType.ON, "PAN1", "Bus1", "Company1", LocalDateTime.of(2023, 1, 1, 10, 0));
        Tap off1 = tap(2, "Stop2", TapType.OFF, "PAN1", "Bus1", "Company1", LocalDateTime.of(2023, 1, 1, 10, 5));

        Tap on2 = tap(3, "Stop1", TapType.ON, "PAN2", "Bus2", "Company2", LocalDateTime.of(2023, 1, 1, 11, 0));
        Tap off2 = tap(4, "Stop1", TapType.OFF, "PAN2", "Bus2", "Company2", LocalDateTime.of(2023, 1, 1, 11, 2));

        Tap on3 = tap(5, "Stop3", TapType.ON, "PAN3", "Bus3", "Company3", LocalDateTime.of(2023, 1, 1, 12, 0));

        List<Trip> trips = tripService.generateTrips(new ArrayList<>(List.of(on1, off1, on2, off2, on3)));
        assertEquals(3, trips.size());
        assertEquals(TripStatus.COMPLETED, trips.get(0).getStatus());
        assertEquals(TripStatus.CANCELLED, trips.get(1).getStatus());
        assertEquals(TripStatus.INCOMPLETE, trips.get(2).getStatus());
    }
}
