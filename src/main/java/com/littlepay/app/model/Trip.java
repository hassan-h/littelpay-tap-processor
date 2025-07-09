package com.littlepay.app.model;

import com.littlepay.app.enums.TripStatus;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvDate;
import com.opencsv.bean.CsvNumber;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trip {
    @CsvDate(value = "dd-MM-yyyy HH:mm:ss")
    @CsvBindByName(column = "Started")
    @CsvBindByPosition(position = 0)
    private LocalDateTime started;

    @CsvDate(value = "dd-MM-yyyy HH:mm:ss")
    @CsvBindByName(column = "Finished")
    @CsvBindByPosition(position = 1)
    private LocalDateTime finished;

    @CsvBindByName(column = "DurationSecs")
    @CsvBindByPosition(position = 2)
    private Long durationSecs;

    @CsvBindByName(column = "FromStopId")
    @CsvBindByPosition(position = 3)
    private String fromStopId;

    @CsvBindByName(column = "ToStopId")
    @CsvBindByPosition(position = 4)
    private String toStopId;

    @CsvBindByName(column = "ChargeAmount")
    @CsvNumber(value = "$0.00")
    @CsvBindByPosition(position = 5)
    private BigDecimal chargeAmount;

    @CsvBindByName(column = "CompanyId")
    @CsvBindByPosition(position = 6)
    private String companyId;

    @CsvBindByName(column = "BusID")
    @CsvBindByPosition(position = 7)
    private String busId;

    @CsvBindByName(column = "PAN")
    @CsvBindByPosition(position = 8)
    private String pan;

    @CsvBindByName(column = "Status")
    @CsvBindByPosition(position = 9)
    private TripStatus status;
}