package com.in28minutes.rest.webservices.restfulwebservices.exception;

import com.in28minutes.rest.webservices.restfulwebservices.user.UserNotFoundException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import java.time.LocalDateTime;

/*
    In the last step, we played with error responses for a non-existing user. When we send a request to retrieve the details of a non-existing user,
    we saw that we got a 404 response which is good. And also there was a predefined structure which was made use of by spring boot.

    Having a proper error response structure is very important when it comes to enterprises. In your enterprise, you might not be following
    exactly the same structure as what is defined in here. You might have your own custom structure.
    Let's explore how to create a custom structure for your Exception Response. Let's define a bean to represent your custom exception structure.
*/

/*
    Let's say, as part of error details, I would want a timestamp when the error happened, an error message and I also want to store the details of the error.
    Let's say this is the standard exception structure I would want to define across my enterprise. Whenever an exception happens, we want to return it in this specific structure.

    So let's first look at how spring is implementing this exception handling. There is a common structure which is defined by spring.
    If I go over to class called ResponseEntityExceptionHandler, we can see that this is the standard class which handles all spring MVC raised exceptions, and it
    returns formatted error details. There are different methods which are present there, there are a wide variety of methods to react to different kinds of exceptions.

    And the fundamental exception handling method is handleException which is present in here - HandleException(Exception, WebRequest). It's final so can't be overridden.
    So this is the method where the standard structure is coming in from.

    So we would want to define our own method like this and provide our own implementation. We can extend ResponseEntityExceptionHandler class and create our own exception handling code.
    So I will create a new class named CustomizedResponseEntityExceptionHandler and extend ResponseEntityExceptionHandler to customize our own handleException method.
*/
@ControllerAdvice
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class) // @ExceptionHandler is used to define what kind of exceptions we want to handle. We'd want to handle all exceptions so Exception.class
    public final ResponseEntity<ErrorDetails> handleAllExceptions(Exception ex, WebRequest request) throws Exception {
        // What I want to do here is to create my own custom exception object.
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(),
                ex.getMessage(), request.getDescription(false));

        // And what we would now need to do is to return a response entity back. We want to send the error details back.
        // The status that we want to send back is Internal Server Error.
        return new ResponseEntity<ErrorDetails>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    /*
    Now we can't use the same name of this method as handleException as it's a final method in super class, so I'll say handleAllExceptions.
    So we are making use of our own custom exception structure, and we are returning it back as the response. Let's see if our code is working.

    So let's send a request to a non-existing user. You're getting a 404.
    However, you're getting it still in this same old structure because this class is not yet picked up by spring framework.

    Let's add @ControllerAdvice annotation. So it'll make this applicable to all the controllers i.e. all the controllers and RestControllers, which are defined in the specific project.
    If you hover over @ControllerAdvice annotation, it's a specialization of @Component for classes that declare @ExceptionHandler, @InitBinder or @ModelAttribute.

    You can also do something called InitBinding, and you can also define common Model Attributes to be shared across multiple controller classes by making use of ControllerAdvice
    Now let's go and fire the request again. And now you would see that your customized response structure is coming back. Right now you can see that the timestamp only has the date.
    If you want to actually have the time information as well, instead of LocalDate what we can do is we can make use of LocalDateTime. So we now switch from LocalDate to LocalDateTime.
    And now you can see that you have the complete timestamp in here with date and the timestamp as well. And we are defining everything in our own custom structure.
    */

    /*
    Now once we defined our own custom structure, what we are observing is the fact that for UserNotFoundException, we are returning a 500 back.
    However, for UserNotFoundException, I would want to return a bad request back. How can I do that? That's where I can customize this further.
    So for all exceptions, I would want to do this but if a UserNotFoundException happens, then I would want to return a different message back.
    */
    @ExceptionHandler(UserNotFoundException.class)
    public final ResponseEntity<ErrorDetails> handleUserNotFoundException(Exception ex, WebRequest request) throws Exception {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(),
                ex.getMessage(), request.getDescription(false));

        return new ResponseEntity<ErrorDetails>(errorDetails, HttpStatus.NOT_FOUND);
        // So instead of internal server error I want to return http status.not_found. This is the one which we want to return back.
        // Let's save this and refresh. So you can see now that we are getting a 404 in a customized structure that is defined by us.
    }

    /*
    However the response 404 i.e. bad request which we're getting on sending post request with empty name or Future birthdate is not telling me anything about what's wrong about the request.
    Whenever we build a rest api, we should always think about the consumer of the rest API. I have no idea what's wrong with my request. I'm just getting a 400 bad request.

    How can I improve this? Let's go back to our ResponseEntityExceptionHandler class and you can find a method with the name handleMethodArgumentNotValid.
    This is the one which handles the method argument not valid exception. It's a protected method, so it can be overridden in child class.

    So we override it and define our own method. In its method argument, we have MethodArgumentNotValidException, the headers, the status code and also
    the web request coming in. And what we want to do is to create our own custom response back.

    Over here, we cannot use ErrorDetails in ResponseEntity generics. You need to use the same signature as it was because we're overriding this method from
    ResponseEntityExceptionHandler placing @Override annotation. Now if I go ahead and execute the post request with invalid name or birthdate, I can see that
    there's a timestamp. There is a message and there are the details which are coming in and if I look at the message which is present there, it's a long message.

    If I copy & paste that error message into my IDE, I see a large error message and we see that there are two errors which are present in here which
    says it must be between 2 and a long value. And it also says, birthdate must be in the past.

    So you can see that the consumer can look at it and find out what's the problem with the request. But this is also a lot of information that is
    going back to the consumer. So we can customize the exact message which goes by providing message attribute to Size and Past annotation constraints.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(),
                //ex.getMessage(), request.getDescription(false));
                "Total Errors:" + ex.getErrorCount() + " First Error: " + ex.getFieldError().getDefaultMessage(), request.getDescription(false));
        // We can ex.getFieldErrors() to iterate over all the field errors and concatenate into one string to print in message of response.
        return new ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST); // So the response status should be bad request.

        /*
        Now, if you look at the exception ex which is written back in method argument, you would see that you'd be able to get a list of fields with errors -
        ex.getFieldErrors() or just first error with ex.getFieldError() and You can get the default message, and you can actually return it back.
        We can also return total errors count back. You can work out what would be the best thing to do for your specific consumer.

        But the important thing to know is that all the details are available as part of these errors in method argument ex.
        So you can pick up this error object and play with it to find out what works best for you.
        So we should always think from the perspective of consumers and that's what we've done improving our response message as well as our response status.
         */
    }

    /*
    Giving the right message back to your consumers is very, very important.
    And in the case of Rest API, the way you can send the details out to your consumers is through the response and the response status.

    Having the right error structure when something goes wrong and having the right response status is very, very important when it comes to rest API.
    In the last couple of steps we explored some of the important options that spring MVC provides for you to handle errors gracefully and return the right response structure and response status.
     */
}