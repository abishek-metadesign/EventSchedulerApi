package uk.co.metadesignsolutions.javachallenge.services;

import uk.co.metadesignsolutions.javachallenge.models.Event;

import java.util.List;

public interface EventService {

    Event save(Event event);
    List<Event> list();
    Event update(Event event);

    void delete(Long id);
}
