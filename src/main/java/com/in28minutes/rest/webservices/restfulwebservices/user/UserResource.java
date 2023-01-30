package com.in28minutes.rest.webservices.restfulwebservices.user;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
/*
Building the rest API for this social media application. As we talked about earlier, the key resources are users and posts.

How do you perform actions against resources when you're using a rest API? That way you'd go for request methods. So when we file a request directly
from the browser, when we type in a URL in the browser, we are sending a get request and typically get request methods are used to retrieve details
for a specific resource. To get the details of users or details of a post, we make use of get request method. To create a new resource, I would go for
post request methods i.e. if I want to create a new user, or if I would want to create a new post then I can use the post request method.
If we want to update an existing resource. I have an existing user and I would want to update his details.

That's where I would make use of a put request method. I can make use of a patch when I would want to update some part of an existing resource,
let's say I would want to update only the birthdate for a specific user then we can make use of a patch request method. If you want to delete a specific user,
delete a specific post. In those kind of scenarios, you can go for delete request method.

So whenever we link rest API, we talk about resources, and we talk about actions that you can perform on the resources. To perform actions on the resources,
we make use of different request methods. To retrieve the details - get. To create a new resource - post. To update an existing resource - put.
To update part of an existing resource - patch and to delete a resource - delete method. So we understood the different request methods we can make use of.

Let's go ahead and design the rest API for our social media applications. In this step we are talking about the different resources and the request methods
that we want to make use of to create our rest API. One best practice you have already observed is the fact that we are making use of plurals in our resource URLs.
I have a preference for plurals because I feel these URL's are much easier to read and understand.
 */

// In this step, let's get started with building the rest API for our users
@RestController // As this would be a REST API
public class UserResource {

    private UserDaoService service;
    /*
    We want to get the details from the user service and the userDaoService is something which is managed by spring.
    We have the @Component on it. So we can auto wire it into the user resource.
     */

    public UserResource(UserDaoService service) {
        this.service = service;
    }

    @GetMapping("/users") //
    public List<User> retrieveAllUsers() {
        return service.findAll();
    } // It goes to the service and from the user service we are returning list of users back which is converted to JSON before showing on browser.

    // http://localhost:8080/users

    @GetMapping("/users/{id}")
    public EntityModel<User> retrieveUserWithId(@PathVariable int id) {
        User user = service.findOne(id);

        if (user == null)
            throw new UserNotFoundException("id: " + id);

        // For adding a link to /users
        EntityModel<User> entityModel = EntityModel.of(user);
        WebMvcLinkBuilder link = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).retrieveAllUsers());
        entityModel.add(link.withRel("all-users"));

        return entityModel;
    }

    // So if delete is successful, you'll get a 200 response pack.
    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable int id) {
        service.deleteById(id);
    }

    /*
    To create a user, we will need to do a post request to /users. And we would want to send all the details of users there.

    {
        "id" : 2,
        "name": "Eve",
        "birthDate": "1997-08-11"
    }

    This is the response back when we retrieve the details of a specific user. When you want to create a new user, you'd want to be able to send in
    similar details. ID is not needed because id is something which the application should create, but you should be able to send in the name and the
    birthDate and based on which you can actually create a user. So what we would want to be able to send in is something that has the same structure as
    above user in JSON. And the content typically is sent as part of the request body and the way we can accept that is by using @RequestBody annotation.

    It's an annotation that indicates that a method parameter should be bound to the body of the web request. In the web request which we sent in,
    we will send a body along with it, and in the body of the request we will have all the details of the user and that would be mapped to the user bean.
    And once we receive the user details here using @RequestBody annotation in method argument, we would want to be able to create the user.

    When we create a new user, we want to dynamically assign an ID to him. And therefore we'll do so by dynamically increasing a count that we have in our User class.
    It's a kind of temporary implementation. Later, we'll actually have a sequence which is created in the database, and we'll be mapping to it using JPA and hibernate.

    @PostMapping("/users")
    public void createUser(@RequestBody User user) {
        service.save(user);
    }

    We want to execute a request to this post method. How can we do that? The thing is, we cannot execute post requests directly from the browser.
    You need some kind of rest API client to be able to fire post requests. One of the simplest rest API clients is Talend API tester.
    Talend API Tester makes it easy to invoke, discover and test HTTP and rest APIs. We'll make use of talend API tester as the rest API client here.
    It was earlier called as Restlet client. It makes it really easy to interact with APIs.

    You can see that the talend api tested is automatically adding in a Content-Type header. Content Type is basically an indication of what is the type
    of the content of your body of the request. We are sending Json here and Json is represented in Content-Type by saying application/json.

    When we executed the post request, we got a response status of 200 back. If you look at the HTTP response status codes, 200 stands for success.
    When we are creating a resource, it's better to return a response status of created i.e. 201. Whenever you create a rest API, it's very important to return the correct response status.
    The right response status for us right now for the post request is 201. How can we make this method to return a response status of 201?
    Let's see how can we return a response status of created and how can we further enhance the response which we are giving back in here?
     */
    @PostMapping("/users")
    // we want to ensure that the user is valid, and so we would add an annotation called @Valid. It marks a property, method parameter or method return type for validation cascading.
    // Constraints defined on the object and its properties are validated when the property, method parameter or method return type is validated.
    // We added additional spring-boot-starter-validation dependency for @Valid annotation. So when you use @Valid annotation and whenever the binding happens, the validations which are defined on your object are automatically invoked.
    // And it would start validating our requests. But for that we should have validations present on our bean.
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) // As the type of the response entity is User, so we'll use <User> in place of <Object> in generics.
    {

    /*
    ResponseEntity - It's one of the classes which is present in org.springframework.http. In this class, there are different methods for different statuses.
    like accepted(), badRequest(), created(), noContent(), notFound(), etc. So there are a number of methods which are present here based on the response
    status you would want to return back. So here we want to return is a status of created. So what we want to do is to create a new builder with created status.
    I don't want to return a BodyBuilder. I would want to return the response entity back. So I'll use .build() method.

    One of the important things for you to remember is that we are making use of a static list so whenever the server restarts, the entire list gets reset and
    so you'll only have three users present. If you execute the request localhost:8080/users, you'll see that there are 3 users present.
    */
        User savedUser = service.save(user);

        // We want to create the location like this - /users/4 on executing post method and send it as part of our HTTP response
        // This is how we create a URI location using in-built utility methods in a specific class of spring - ServletUriComponentsBuilder class.
        URI location = ServletUriComponentsBuilder.fromCurrentRequest() // Getting URI of current request method
                                                .path("/{id}") // To the current request path, add path /id and replace the variable id present in here with the id of the created user.
                                                .buildAndExpand(savedUser.getId())
                                                .toUri(); // To convert to a URI
        // Instead of hard coding the entire URL in here, we'll make use of the current request URL. So the current request always comes to this URL - /users.
        // Now we would want to append /4 to this. Instead of directly appending /4, the way we would need to do that is by saying /users, we want to append
        // an id and we want to replace the value of id with the value of the id inside object user i.e. user.getId().
        // So what we'll do is we'll pick up the /users, and we will append /id and we will replace the id with user.getId().
        // That's what is done to initialise above variable location.

        return ResponseEntity.created(location).build(); // Returning the URI location of created user as part of our ResponseEntity.
        /*
        We can see that the response status is now 201 which was 200 provided by default from spring boot. So we are returning a created response status when we are executing a post method.
        That's good. Let's improve this further. Whenever we creat a Rest API, we should always think from the perspective of consumer of the rest API.
        Now the consumer of this rest API is trying to create a user. Once a user is created, we are returning a status back of 201.

        In addition, can we return back the URI of the user who is created? For example, that "/users/4" is the URI of the user which is created.
        If I return /user/4 as the uri, then the consumer can execute a request to that. So he can say localhost:8080/user/4.
        That's a URI which is being written back. He can check it and see the details of the new user in their. But how to implement it here?

        Typically, whenever you want to return a URL of a created resource, there is a specific HTTP header
        you need to make use of, That header is called location header and the created method in ResponseEntity actually accepts a URI location.
        So let's see how to build the location.

        You can see that we are getting a status of 201 and if you scroll down you also see a location header where localhost:8080/users comes because of path of the current request and have id of created user appended with it.
        */
    }
}


/*
When a resource is not found, status of 404 is thrown. That's something which is provided by default by Spring boot. Springboot autoconfigures a error page
and an error response. Similar to above, if there's an exception that happened in the server, the recommended response status is 500.

If there's a validation error, if you are sending certain details, and they don't pass the validation, the recommended response status is 400.
So it's very, very important that when you create a rest API, you return the right response status from it.

Few other important response statuses -

200 - Success
201 - Created, when you execute a post request and a new resource is created, that's when you would return a created response status.
204 - No Content, if let's say you are executing a put request to update the content. In that kind of scenario, it might be one of the options to return a status of no content.
401 - Unauthorized. A little later in the course, we'll be adding authentication and authorization for our rest API.
      And you'd see that when user does not pass in the right details along with the request, you get a 401 response status.
400 - Bad request. For example, a validation error.

We already talked about 404, which is Resource Not Found and 500 is a Server Error. So whenever we are building rest API, you want to make the best use of HTTP.
Make sure that your URLs are designed right. Make sure that you're making use of the right request methods. And the third important thing is to make sure
that you are returning the right response status from your rest API.
 */