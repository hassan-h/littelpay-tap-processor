package com.littlepay.app.config;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "fares")
public class FareProperties {
    private Map<String, BigDecimal> rules;
    
    public Map<Set<String>, BigDecimal> getTransformedRules() {
        if (rules == null) return Collections.emptyMap();

        Map<Set<String>, BigDecimal> transformed = new HashMap<>();
        for (Map.Entry<String, BigDecimal> entry : rules.entrySet()) {
            String[] stops = entry.getKey().split("_");
            if (stops.length != 2) {
                throw new IllegalArgumentException("Invalid rule key format: " + entry.getKey());
            }
            transformed.put(Set.of(stops[0], stops[1]), entry.getValue());
        }
        return Collections.unmodifiableMap(transformed);
    }
}