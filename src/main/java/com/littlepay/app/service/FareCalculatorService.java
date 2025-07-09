package com.littlepay.app.service;

import java.math.BigDecimal;

import com.littlepay.app.model.Tap;

public interface FareCalculatorService {
    BigDecimal calculateFare(Tap onTap, Tap offTap);
    BigDecimal calculateFare(Tap onTap); 
}
