package com.littlepay.app.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.littlepay.app.config.FareProperties;
import com.littlepay.app.model.Tap;
import com.littlepay.app.service.FareCalculatorService;

@Service
public class DefaultFareCalculatorImpl implements FareCalculatorService {

    private final Map<Set<String>, BigDecimal> fareRules;

    public DefaultFareCalculatorImpl(FareProperties properties) {
        this.fareRules = properties.getTransformedRules();
    }
    
    @Override
    public BigDecimal calculateFare(Tap onTap, Tap offTap) {
        return calculateFare(onTap.getStopId(), offTap.getStopId());
    }
    
    @Override
    public BigDecimal calculateFare(Tap tap) {
        return calculateFare(tap.getStopId(), null);
    }
    
    private BigDecimal calculateFare(String fromStop, String toStop) {
        if (fromStop == null)
            throw new IllegalArgumentException("Start stop cannot be null");

        if (toStop == null) {
            // INCOMPLETE trip
            return fareRules.entrySet().stream()
                    .filter(entry -> entry.getKey().contains(fromStop))
                    .map(Map.Entry::getValue)
                    .max(BigDecimal::compareTo)
                    .orElseThrow(() -> new IllegalArgumentException("No max fare defined for stop " + fromStop))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        if (fromStop.equals(toStop)) {
            // CANCELLED trip
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        // COMPLETED trip
        BigDecimal fare = fareRules.get(Set.of(fromStop, toStop));
        if (fare == null) {
            throw new IllegalArgumentException("No fare defined for route: " + fromStop + " <-> " + toStop);
        }

        return fare.setScale(2, RoundingMode.HALF_UP);
    }
}
