package uk.co.metadesignsolutions.javachallenge.dtos.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import uk.co.metadesignsolutions.javachallenge.enums.TimePeriod;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class EventResponse {

    private Long id;

    private String title;
    @JsonFormat(pattern = "yyyy/MM/dd")
    @DateTimeFormat(pattern = "yyyy/MM/dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy/MM/dd")
    @DateTimeFormat(pattern = "yyyy/MM/dd")
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private TimePeriod timePeriod;

    @JsonFormat(pattern = "yyyy/MM/dd")
    @DateTimeFormat(pattern = "yyyy/MM/dd")
    private LocalDate scheduledDate;



}
