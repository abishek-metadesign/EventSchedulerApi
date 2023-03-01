package uk.co.metadesignsolutions.javachallenge.exceptions;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse>  handleFieldException(MethodArgumentNotValidException ex){
        List<FieldError> fieldErrors = ex.getFieldErrors();
        ErrorResponse errorResponse = new ErrorResponse();
        for (FieldError fieldError: fieldErrors){
             String error = "invalid value for " +fieldError.getField() + " : " + fieldError.getRejectedValue();
             errorResponse.setMessage(error);
             return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
        }
        errorResponse.setMessage("Invalid request");
        return  new ResponseEntity<>(errorResponse,HttpStatus.UNPROCESSABLE_ENTITY);
    }


    @org.springframework.web.bind.annotation.ExceptionHandler(HttpMessageNotReadableException.class)
    public  ResponseEntity<ErrorResponse> handleJsonException(HttpMessageNotReadableException exception){
        Throwable cause = exception.getCause();
        if (cause instanceof  InvalidFormatException){
            String message = cause.getMessage();
            String pattern = "\\[\"(.*?)\"\\]";

            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(message);

            ErrorResponse errorResponse= new ErrorResponse();

            InvalidFormatException  invalidFormatException = (InvalidFormatException)cause;
            String simpleName = invalidFormatException.getTargetType().getSimpleName();
            String value ="";
            Object dataValue = invalidFormatException.getValue();
            if (dataValue instanceof String){
                value = (String) dataValue;
            }
            if (m.find()) {
                String result = m.group(1);
                errorResponse.setMessage("Invalid value for "+ result + " : "+ value);
            }
            return new ResponseEntity<>(errorResponse,HttpStatus.UNPROCESSABLE_ENTITY);
        }

        ErrorResponse errorResponse= new ErrorResponse();
        errorResponse.setMessage("Invalid Request , Json parsing exception");

        return new ResponseEntity<>(errorResponse,HttpStatus.UNPROCESSABLE_ENTITY);
    }


    @org.springframework.web.bind.annotation.ExceptionHandler(MissingRequestHeaderException.class)
    public  ResponseEntity<ErrorResponse> handleMissingHeader(MissingRequestHeaderException headerException){
        String headerName = headerException.getHeaderName();
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage("Invalid Request Missing header "+ headerName);
        return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(GenericException.class)
    public  ResponseEntity<ErrorResponse> handleGenericException(GenericException genericException){
        String genericExceptionMessage = genericException.getMessage();
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(genericExceptionMessage);
        return new ResponseEntity<>(errorResponse,genericException.getHttpStatus());
    }





}
