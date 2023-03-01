package uk.co.metadesignsolutions.javachallenge.exceptions;

import org.springframework.http.HttpStatus;

public class GenericException extends RuntimeException{

    private HttpStatus httpStatus=HttpStatus.BAD_REQUEST;

    public GenericException(String message) {
        super(message);
    }


    public GenericException(String cannotScheduleInWeekend, HttpStatus httpStatus) {
        super(cannotScheduleInWeekend);
        this.httpStatus = httpStatus;
    }

    public  HttpStatus getHttpStatus(){
        return httpStatus;
    }

}
