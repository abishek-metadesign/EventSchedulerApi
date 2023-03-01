package uk.co.metadesignsolutions.javachallenge.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.metadesignsolutions.javachallenge.models.Event;

import java.time.LocalDate;
import java.util.List;

public interface EventRepository extends JpaRepository<Event,Long> {

     List<Event> findAllByScheduledDateGreaterThanEqualAndScheduledDateLessThanEqualOrderByStartTimeAsc(LocalDate startDate, LocalDate endDate);

     List<Event> findAllByScheduledDateOrderByStartTimeAsc(LocalDate localDate);

}
