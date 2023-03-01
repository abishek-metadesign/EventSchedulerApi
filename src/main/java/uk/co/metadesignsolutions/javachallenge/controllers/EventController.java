package uk.co.metadesignsolutions.javachallenge.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.co.metadesignsolutions.javachallenge.dtos.request.EventRequest;
import uk.co.metadesignsolutions.javachallenge.dtos.response.EventResponse;
import uk.co.metadesignsolutions.javachallenge.exceptions.GenericException;
import uk.co.metadesignsolutions.javachallenge.mappers.EntityMapper;
import uk.co.metadesignsolutions.javachallenge.models.Event;
import uk.co.metadesignsolutions.javachallenge.services.EventService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class EventController {

    private final EventService eventService;

    private final EntityMapper entityMapper;

    public EventController(EventService eventService, EntityMapper entityMapper) {
        this.eventService = eventService;
        this.entityMapper = entityMapper;
    }

    @PostMapping("/schedule")
    public ResponseEntity<EventResponse> create(@RequestBody @Valid EventRequest eventRequest )  {
        Event event = entityMapper.map(eventRequest, Event.class);
        Event savedEvent = eventService.save(event);
        EventResponse eventResponse = entityMapper.map(savedEvent, EventResponse.class);
        return  new ResponseEntity<>(eventResponse,HttpStatus.CREATED);
    }

   @GetMapping("/schedule/all")
   public ResponseEntity<List<EventResponse>> list(){
        List<Event> list = eventService.list();
        List<EventResponse> events = list.stream()
                .map(e -> entityMapper.map(e, EventResponse.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(events);
   }

   @PutMapping("/schedule/{id}")
   public  ResponseEntity<EventResponse> update(@RequestBody @Valid EventRequest eventRequest, @PathVariable("id") Long id  ){
       Event event = entityMapper.map(eventRequest, Event.class);
       event.setId( id);
       Event updatedEvent = eventService.update(event);
       EventResponse eventResponse  = entityMapper.map(updatedEvent, EventResponse.class);
       return ResponseEntity.ok(eventResponse);
   }


   @DeleteMapping("/schedule/{id}")
   public  ResponseEntity<EventResponse> delete(@PathVariable("id") Long id ,@RequestHeader(value = "Authorization",required = false) String authorization ){
        if (authorization==null){
          throw  new GenericException("Authentication Error", HttpStatus.UNAUTHORIZED);
        }
        if (!authorization.equals("Bearer SkFabTZibXE1aE14ckpQUUxHc2dnQ2RzdlFRTTM2NFE2cGI4d3RQNjZmdEFITmdBQkE=")){
            throw  new GenericException("Authentication error ", HttpStatus.FORBIDDEN);
        }
        eventService.delete(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
   }


}
