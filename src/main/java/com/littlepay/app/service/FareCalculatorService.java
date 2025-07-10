package com.littlepay.app.service;

import java.math.BigDecimal;

import com.littlepay.app.model.Tap;

public interface FareCalculatorService {
    public BigDecimal calculateFare(Tap onTap, Tap offTap);
    public BigDecimal calculateFare(Tap onTap); 
}
