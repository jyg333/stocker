package com.stocker.backend.exceptionHandling;


import com.stocker.backend.model.dto.response.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    //400 Bad Request
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponseDto> handleBadRequestException(BadRequestException ex) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
//    //401
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponseDto> handleUnauthorizedException(UnauthorizedException ex) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
    // 402
    @ExceptionHandler(PaymentRequiredException.class)
    public ResponseEntity<ErrorResponseDto> handlePaymentRequiredException(PaymentRequiredException ex) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.PAYMENT_REQUIRED);
    }
//
    //403
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponseDto> handleForbiddenException(ForbiddenException ex) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    //404 NOT Found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleProductNotFoundException(ResourceNotFoundException ex) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    //405
    @ExceptionHandler(MethodNotAllowedException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodNotAllowedException(MethodNotAllowedException ex) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.METHOD_NOT_ALLOWED);
    }
    //406
    @ExceptionHandler(NotAcceptableException.class)
    public ResponseEntity<ErrorResponseDto> handleNotAcceptableException(NotAcceptableException ex) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_ACCEPTABLE);
    }
    //417
    @ExceptionHandler(ExpectationFailedException.class)
    public ResponseEntity<ErrorResponseDto> handleExpectationFailException(ExpectationFailedException ex){
        ErrorResponseDto errorResponse = new ErrorResponseDto(ex.getMessage());
        return new ResponseEntity<>(errorResponse,HttpStatus.EXPECTATION_FAILED);
    }

    //422
    @ExceptionHandler(UnprocessableEntityException.class)
    public ResponseEntity<ErrorResponseDto> handleUnprocessableEntityException(UnprocessableEntityException ex) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    //423
    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ErrorResponseDto> handleLockedException(LockedException ex) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.LOCKED);
    }

    //500
    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<ErrorResponseDto> handleInternalServerErrorException(InternalServerErrorException ex) {
        ErrorResponseDto errorResponse = new ErrorResponseDto( ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleOtherExceptions(Exception ex) {
        return new ResponseEntity<>("An error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
