package com.littlepay.app.model;

import java.time.LocalDateTime;

import com.littlepay.app.enums.TapType;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tap {
    @CsvBindByName(column = "ID")
    private int id;

    @CsvDate(value = "dd-MM-yyyy HH:mm:ss")
    @CsvBindByName(column = "DateTimeUTC")
    private LocalDateTime dateTimeUTC;

    @CsvBindByName(column = "TapType")
    private TapType tapType;

    @CsvBindByName(column = "StopId")
    private String stopId;

    @CsvBindByName(column = "CompanyId")
    private String companyId;

    @CsvBindByName(column = "BusID")
    private String busID;

    @CsvBindByName(column = "PAN")
    private String pan;
}