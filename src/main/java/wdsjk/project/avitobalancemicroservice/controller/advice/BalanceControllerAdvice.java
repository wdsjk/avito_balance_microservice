package wdsjk.project.avitobalancemicroservice.controller.advice;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.TransactionSystemException;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import wdsjk.project.avitobalancemicroservice.dto.exception.Reason;

@RestControllerAdvice
public class BalanceControllerAdvice {
    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<Reason> handleTransactionSystemException(TransactionSystemException e) {
        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(
                new Reason("You're trying to withdraw more than is in your account!")
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Reason> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(
                new Reason("Invalid data type!")
        );
    }

    // May produce NullPointerException! TODO: Need to think about this!
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Reason> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(
                new Reason(e.getDetailMessageArguments()[1].toString())
        );
    }
}
