package uk.co.metadesignsolutions.javachallenge.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uk.co.metadesignsolutions.javachallenge.enums.TimePeriod;
import uk.co.metadesignsolutions.javachallenge.exceptions.GenericException;
import uk.co.metadesignsolutions.javachallenge.models.Event;
import uk.co.metadesignsolutions.javachallenge.repositories.EventRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {

    private EventRepository eventRepository;

    private final Long TOTAL_WORKING_MIN = 480l;

    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public Event save(Event event) {
        List<Event> events = eventRepository.findAllByScheduledDateGreaterThanEqualAndScheduledDateLessThanEqualOrderByStartTimeAsc(event.getStartDate(), event.getEndDate());
        LocalDate currentDate = event.getStartDate();
        LocalDate endDate = event.getEndDate();


        Map<LocalDate, List<Event>> eventMap = events.stream().collect(Collectors.groupingBy(Event::getScheduledDate));

        boolean scheduled = false;
        while (currentDate.isBefore(endDate) || currentDate.isEqual(endDate)) {
            int dayOfWeek = currentDate.get(ChronoField.DAY_OF_WEEK);
            if (dayOfWeek == 6 || dayOfWeek == 7) {
                throw new GenericException("cannot schedule in weekend", HttpStatus.UNPROCESSABLE_ENTITY);
            }

            List<Event> eventsForADay = eventMap.get(currentDate);
            if (eventsForADay == null) {
                eventsForADay = new ArrayList<>();
            }
            Long totalTime = 0l;
            for (Event eventForADay : eventsForADay) {
                LocalTime startTime = eventForADay.getStartTime();
                LocalTime endTime = eventForADay.getEndTime();
                totalTime += startTime.until(endTime, ChronoUnit.MINUTES);
            }

            TimePeriod timePeriod = event.getTimePeriod();
            long timePeriodInLong = convertTimePeriodToMinutes(timePeriod);

            if (!(totalTime + timePeriodInLong > TOTAL_WORKING_MIN)) {
                event.setScheduledDate(currentDate);
                LocalTime startTime = LocalTime.parse("09:00").plusMinutes(totalTime);
                event.setStartTime(startTime);
                event.setEndTime(startTime.plusMinutes(timePeriodInLong));
                scheduled = true;
                break;
            }
            currentDate = currentDate.plusDays(1);
        }
        if (!scheduled) {
            throw new GenericException("Start Date error",HttpStatus.BAD_REQUEST);
        }



        return eventRepository.save(event);
    }

    @Override
    public List<Event> list() {
        return eventRepository.findAll();
    }

    @Override
    public Event update(Event event) {
        Event eventInDb = eventRepository.findById(event.getId())
                .orElseThrow(() -> new GenericException("Not Found", HttpStatus.NOT_FOUND));

        LocalDate startDate = event.getStartDate();
        LocalDate endDate = event.getEndDate();

        LocalDate currDate = startDate;

        boolean scheduled = false;
        while (!currDate.isAfter(endDate)) {
            List<Event> eventsForADay = eventRepository.findAllByScheduledDateOrderByStartTimeAsc(currDate);
            if (currDate.isEqual(eventInDb.getScheduledDate())) {
                long totalTime = 0l;
                for (Event eventForADay : eventsForADay) {
                    if (!(Objects.equals(event.getId(), eventForADay.getId()))) {
                        LocalTime startTime = eventForADay.getStartTime();
                        LocalTime endTime = eventForADay.getEndTime();
                        totalTime += startTime.until(endTime, ChronoUnit.MINUTES);
                    }
                }
                TimePeriod timePeriod = event.getTimePeriod();
                long timePeriodInLong = convertTimePeriodToMinutes(timePeriod);


                List<Event> updateList = new ArrayList<>();
                LocalTime startTime = LocalTime.parse("09:00");

                if (!(totalTime + timePeriodInLong > TOTAL_WORKING_MIN)) {
                    for (Event currEvent : eventsForADay) {
                        currEvent.setStartTime(startTime);
                        if (Objects.equals(currEvent.getId(), event.getId())) {
                            TimePeriod etimePeriod = event.getTimePeriod();
                            eventInDb.setTimePeriod(etimePeriod);
                            long startTimeInLong = convertTimePeriodToMinutes(etimePeriod);
                            eventInDb.setStartTime(startTime);
                            startTime = startTime.plusMinutes(startTimeInLong);
                            eventInDb.setEndTime(startTime);
                        } else {
                            long currEventTimePeriod = convertTimePeriodToMinutes(currEvent.getTimePeriod());
                            startTime = startTime.plusMinutes(currEventTimePeriod);
                            currEvent.setEndTime(startTime);
                            updateList.add(currEvent);
                        }
                    }
                    scheduled = true;
                    eventRepository.saveAll(updateList);
                    break;

                }
            } else {
                long totalTime = 0l;
                for (Event eventForADay : eventsForADay) {
                    LocalTime startTime = eventForADay.getStartTime();
                    LocalTime endTime = eventForADay.getEndTime();
                    totalTime += startTime.until(endTime, ChronoUnit.MINUTES);
                }

                TimePeriod timePeriod = event.getTimePeriod();
                long timePeriodInLong = convertTimePeriodToMinutes(timePeriod);

                if (!(totalTime + timePeriodInLong > TOTAL_WORKING_MIN)) {
                    event.setScheduledDate(currDate);
                    LocalTime startTime = LocalTime.parse("09:00").plusMinutes(totalTime);
                    eventInDb.setStartTime(startTime);
                    eventInDb.setEndTime(startTime.plusMinutes(timePeriodInLong));
                    scheduled = true;
                    break;
                }
            }


            currDate = currDate.plusDays(1);
        }
        if (!scheduled) {
            throw new GenericException("Could not schedule", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        return eventRepository.save(eventInDb);
    }

    @Override
    public void delete(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new GenericException("Not Fount", HttpStatus.NOT_FOUND));

        List<Event> allByScheduledDate = eventRepository.findAllByScheduledDateOrderByStartTimeAsc(event.getScheduledDate());
        boolean changed = false;
        ListIterator<Event> eventListIterator = allByScheduledDate.listIterator();

        LocalTime startTime = null;

        while (eventListIterator.hasNext()) {
            Event listEvent = eventListIterator.next();
            if (Objects.equals(listEvent.getId(), event.getId())) {
                startTime = listEvent.getStartTime();
                eventRepository.delete(event);
                eventListIterator.remove();
                changed = true;
                continue;
            }
            if (changed) {
                listEvent.setStartTime(startTime);
                TimePeriod timePeriod = listEvent.getTimePeriod();
                LocalTime endTime = startTime.plusMinutes(convertTimePeriodToMinutes(timePeriod));
                listEvent.setEndTime(endTime);
                startTime = endTime;
            }else {
                startTime = listEvent.getEndTime();
            }
        }
        eventRepository.saveAll(allByScheduledDate);


    }


    public long convertTimePeriodToMinutes(TimePeriod timePeriod) {
        switch (timePeriod) {
            case FIFTEEN_MINUTES: {
                return 15;
            }
            case THIRTY_MINUTES: {
                return 30;
            }
            case ONE_HOUR: {
                return 60;
            }
            case TWO_HOUR: {
                return 120;
            }

            case THREE_HOUR: {
                return 180;
            }

            case FOUR_HOUR: {
                return 240;
            }
        }

        return 1;
    }


}
