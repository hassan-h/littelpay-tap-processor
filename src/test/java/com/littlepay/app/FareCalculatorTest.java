package com.littlepay.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.littlepay.app.model.Tap;
import com.littlepay.app.service.FareCalculatorService;

@SpringBootTest
class FareCalculatorTest {

	@Autowired
    private FareCalculatorService calculator;


    @Test
    void testCompletedTripCharge_ValidPairs() {
        assertEquals(new BigDecimal("3.25"), calculator.calculateFare(
        		Tap.builder().stopId("Stop1").build(), 
        		Tap.builder().stopId("Stop2").build()));
        
        assertEquals(new BigDecimal("3.25"), calculator.calculateFare(
        		Tap.builder().stopId("Stop2").build(), 
        		Tap.builder().stopId("Stop1").build()));

        assertEquals(new BigDecimal("5.50"), calculator.calculateFare(
        		Tap.builder().stopId("Stop2").build(), 
        		Tap.builder().stopId("Stop3").build()));
        
        assertEquals(new BigDecimal("5.50"), calculator.calculateFare(
        		Tap.builder().stopId("Stop3").build(), 
        		Tap.builder().stopId("Stop2").build()));

        assertEquals(new BigDecimal("7.30"), calculator.calculateFare(
        		Tap.builder().stopId("Stop1").build(), 
        		Tap.builder().stopId("Stop3").build()));
        
        assertEquals(new BigDecimal("7.30"), calculator.calculateFare(
        		Tap.builder().stopId("Stop3").build(), 
        		Tap.builder().stopId("Stop1").build()));
    }

    @Test
    void testCompletedTripCharge_InvalidStops_Throws() {
        assertThrows(IllegalArgumentException.class,
            () -> calculator.calculateFare(
            		Tap.builder().stopId("Stop1").build(), 
            		Tap.builder().stopId("StopX").build()));
    }

    @Test
    void testMaxChargeForIncompleteTrip() {
        assertEquals(new BigDecimal("7.30"), calculator.calculateFare(
        		Tap.builder().stopId("Stop1").build()));
        
        assertEquals(new BigDecimal("5.50"), calculator.calculateFare(
        		Tap.builder().stopId("Stop2").build()));
        
        assertEquals(new BigDecimal("7.30"), calculator.calculateFare(
        		Tap.builder().stopId("Stop3").build()));
    }

    @Test
    void testMaxChargeForIncompleteTrip_InvalidStop_Throws() {
        assertThrows(IllegalArgumentException.class,
            () -> calculator.calculateFare(
            		Tap.builder().stopId("StopX").build()));
    }

    @Test
    void testCancelledTripChargeIsZero() {
        assertEquals(new BigDecimal("0.00"), calculator.calculateFare(
        		Tap.builder().stopId("Stop1").build(), 
        		Tap.builder().stopId("Stop1").build()));
    }
}
