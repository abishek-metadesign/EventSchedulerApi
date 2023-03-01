// /////////////////////////////////////////////////////////////////////////////
// PLEASE DO NOT RENAME OR REMOVE ANY OF THE CODE BELOW.
// YOU CAN ADD YOUR CODE TO THIS FILE TO EXTEND THE FEATURES TO USE THEM IN YOUR WORK.
// /////////////////////////////////////////////////////////////////////////////

package uk.co.metadesignsolutions.javachallenge.mappers;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("entityMapper")
public class EntityMapper {

    private final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

    private final MapperFacade mapper;

    public EntityMapper() {
        mapper =  mapperFactory.getMapperFacade();
    }

    public <S, T> List<T> mapAsList(final List<S> sourceElements, final Class<T> targetClass) {
        return mapper.mapAsList(sourceElements, targetClass);
    }

    public <S, T> T map(final S element, final Class<T> targetClass) {
        return mapper.map(element, targetClass);
    }
}
