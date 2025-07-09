package com.littlepay.app.service;

import java.util.List;

import com.littlepay.app.model.Tap;
import com.littlepay.app.model.Trip;

public interface TripService {
    public List<Trip> generateTrips(List<Tap> taps);
}