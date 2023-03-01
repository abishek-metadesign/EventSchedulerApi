package uk.co.metadesignsolutions.javachallenge.dtos.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import uk.co.metadesignsolutions.javachallenge.enums.TimePeriod;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
public class EventRequest {

 private String title;
 @JsonFormat(pattern = "yyyy/MM/dd")
 @DateTimeFormat(pattern = "yyyy/MM/dd")
 @NotNull
 private LocalDate startDate;

 @JsonFormat(pattern = "yyyy/MM/dd")
 @DateTimeFormat(pattern = "yyyy/MM/dd")
 @NotNull
 private LocalDate endDate;
 @NotNull
 private TimePeriod timePeriod;

}
