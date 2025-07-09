package com.littlepay.app.service.impl;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.littlepay.app.enums.TapType;
import com.littlepay.app.enums.TripStatus;
import com.littlepay.app.model.Tap;
import com.littlepay.app.model.Trip;
import com.littlepay.app.service.FareCalculatorService;
import com.littlepay.app.service.TripService;

@Service
public class TripServiceImpl implements TripService {
    private final FareCalculatorService fareCalculator;

    public TripServiceImpl(FareCalculatorService fareCalculator) {
        this.fareCalculator = Objects.requireNonNull(fareCalculator, "FareCalculator cannot be null");
    }

    @Override
    public List<Trip> generateTrips(List<Tap> taps) {
        List<Trip> trips = new ArrayList<>();
        // Map key = PAN|BusID|CompanyId -> Tap ON record
        Map<String, Tap> onTapRecords = new HashMap<>();

        if (taps == null || taps.isEmpty()) {
            return trips;
        }

        // 1. Sort all taps by timestamp
        taps.sort(Comparator.comparing(Tap::getDateTimeUTC));

        // 2. Process each tap in order
        for (Tap current : taps) {
            String key = current.getPan()
                       + "|" + current.getBusID()
                       + "|" + current.getCompanyId();

            if (current.getTapType() == TapType.ON) {
                // If there's already an ON for this key -> it’s incomplete
                if (onTapRecords.containsKey(key)) {
                    Tap previousOn = onTapRecords.remove(key);
                    trips.add(buildTrip(previousOn, null)); // Incomplete
                }
                // Store the new ON
                onTapRecords.put(key, current);

            } else { // OFF
                // If matching ON exists -> completed or cancelled
                if (onTapRecords.containsKey(key)) {
                    Tap onTap = onTapRecords.remove(key);
                    trips.add(buildTrip(onTap, current)); // Completed or Cancelled
                } else {
                    // Orphan OFF -> skip or log
                    System.out.println("Skipping unmatched OFF: " + current);
                }
            }
        }

        // 3. Any remaining ONs are incomplete
        for (Tap leftoverOn : onTapRecords.values()) {
            trips.add(buildTrip(leftoverOn, null));
        }

        return trips;
    }

    private Trip buildTrip(Tap onTap, Tap offTap) {
        TripStatus status;
        BigDecimal charge;
        LocalDateTime finished = null;
        Long durationSecs = null;
        String toStopId = null;

        if (offTap == null) {
            status = TripStatus.INCOMPLETE;
            charge = fareCalculator.calculateFare(onTap);
        } else {
            finished = offTap.getDateTimeUTC();
            durationSecs = Duration.between(onTap.getDateTimeUTC(), finished).getSeconds();
            toStopId = offTap.getStopId();
            status = onTap.getStopId().equals(toStopId) ? TripStatus.CANCELLED : TripStatus.COMPLETED;
            charge = fareCalculator.calculateFare(onTap, offTap);
        }

        return Trip.builder()
                .started(onTap.getDateTimeUTC())
                .finished(finished)
                .durationSecs(durationSecs)
                .fromStopId(onTap.getStopId())
                .toStopId(toStopId)
                .chargeAmount(charge)
                .companyId(onTap.getCompanyId())
                .busId(onTap.getBusID())
                .pan(onTap.getPan())
                .status(status)
                .build();
    }
}