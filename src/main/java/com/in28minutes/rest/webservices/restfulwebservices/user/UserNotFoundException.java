package com.in28minutes.rest.webservices.restfulwebservices.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/*
By default, we can see that it's extending superclass Exception but I want to extend RuntimeException. If you extend exception,
our exception will become a checked exception and I would want to avoid checked exceptions most of the time. So I would go with a RuntimeException.
 */
@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException
/*
 Let's again go to localhost:8080/users/101 - Now again, we are back to White Label Error Page and are returning a status of 500. It's saying UserNotFoundException: id:101
 With throwing normal custom exception without @ResponseStatus annotation, we're returning a status of 500. When something is not found, the best exception to return is 404.
 So we use an annotation called @ResponseStatus to make the UserNotFoundException result in a 404 status. The response status that we want to return here is NOT_FOUND.
 There are a number of responses like ACCEPTED, BAD_REQUEST, CREATED and a lot of others. Now when we say localhost:8080/users/101, we will get response status of 404 Not Found.

 Most of the time, you don't want all the exception trace to be present, so we comment devtools dependency in our pom.xml to suppress printing of entire long Exception stack trace.
*/
{
    public UserNotFoundException(String message) {
        super(message);
    }
}
/*
One of the important things that you need to remember is that when you run this application in production, you'd be running it as a jar file.
So you'll be creating a jar file from this specific application, and you'll be running it using something like java -jar and run the jar file.

And when you run a packaged application like that, DevTools is automatically disabled. So in production, even if you have spring-boot-devtools enabled in the pom.xml,
and if you build a file for this and if you run the jar file, in that kind of scenario you will not be able to see the trace.

Let's now try and actually go to Talend Api Tester and let's send a get request to /users/412, which is not existing. You can see that we are getting 404 back
and you can also see that we are getting a structured response back. You can see that we are getting an application/json response back.

So when we are firing the request from a browser, we are getting HTML response back. However, when we fire it from talend api tester, we get it in a proper structure.
You have a timestamp, status, error, the entire trace, message and the path. In this step we took the basic steps in implementing error handling for our rest API.

There are a lot of things that we can do to improve this further. Let's explore them starting the next step.
 */