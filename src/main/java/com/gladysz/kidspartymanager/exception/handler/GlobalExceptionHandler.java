package com.gladysz.kidspartymanager.exception.handler;

import com.gladysz.kidspartymanager.exception.animator.AnimatorDeleteException;
import com.gladysz.kidspartymanager.exception.animator.AnimatorInactiveException;
import com.gladysz.kidspartymanager.exception.animator.AnimatorNotFoundException;
import com.gladysz.kidspartymanager.exception.currencyrate.CurrencyRateNotFoundException;
import com.gladysz.kidspartymanager.exception.eventassessment.EventAlreadyAssessedException;
import com.gladysz.kidspartymanager.exception.eventassessment.EventAssessmentNotAllowedException;
import com.gladysz.kidspartymanager.exception.eventassessment.EventAssessmentNotFoundException;
import com.gladysz.kidspartymanager.exception.eventpackage.EventPackageDeleteException;
import com.gladysz.kidspartymanager.exception.eventpackage.EventPackageNotFoundException;
import com.gladysz.kidspartymanager.exception.externalapi.ExternalApiException;
import com.gladysz.kidspartymanager.exception.orderer.OrdererDeleteException;
import com.gladysz.kidspartymanager.exception.orderer.OrdererNotFoundException;
import com.gladysz.kidspartymanager.exception.reservation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            AnimatorInactiveException.class,
            EventAlreadyAssessedException.class,
            EventAssessmentNotAllowedException.class,
            ReservationChangeStatusException.class,
            ReservationFilteringException.class,
            ReservationTimeException.class,
            ReservationUpdateException.class
    })
    public ResponseEntity<String> handleBadRequestException(RuntimeException e) {

        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler({
            AnimatorDeleteException.class,
            EventPackageDeleteException.class,
            OrdererDeleteException.class,
            ReservationOverlapException.class,
    })
    public ResponseEntity<String> handleConflictException(RuntimeException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }


    @ExceptionHandler({
            AnimatorNotFoundException.class,
            CurrencyRateNotFoundException.class,
            EventAssessmentNotFoundException.class,
            EventPackageNotFoundException.class,
            OrdererNotFoundException.class,
            ReservationNotFoundException.class,
    })
    public ResponseEntity<String> handleNotFoundException(RuntimeException e) {

        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<String> handleExternalApiException(ExternalApiException e) {

        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_GATEWAY);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleInternalServerErrorException(Exception e) {
        return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
