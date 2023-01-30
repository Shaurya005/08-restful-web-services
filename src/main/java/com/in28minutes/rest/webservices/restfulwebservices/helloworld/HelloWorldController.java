package com.in28minutes.rest.webservices.restfulwebservices.helloworld;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
/*
If you had any problems running this, the first thing we need to check is the package of HelloWorldController. Make sure that it's under a sub package of the package where RestfulWebServicesApplication is present.
The 2nd thing you need to make sure is that you have the annotation @RestController on the class. The 3rd thing is to make sure that you have a @RequestMapping annotation with the right method and path.
 */
@RestController // Marking this controller as a REST Controller. We can expose a rest API from here.
//@RequestMapping("/my")
public class HelloWorldController
{
    private MessageSource messageSource;

    public HelloWorldController(MessageSource messageSource){
        this.messageSource = messageSource;
    }

    // @RequestMapping(method = RequestMethod.GET, path = "/hello-world") Better way to do RequestMapping with Get method is to use @GetMapping annotation.
    @GetMapping(path = "/hello-world") // There are methods like post mapping, put mapping and a lot of other methods which you can use.
    public String helloWorld() {
        return "Hello-World";
    }

    /*
    Above, we created a simple rest API to return a string back however typically all the rest API return JSON back.
    Let's see how to return a JSON back from our rest API.
     */
    @GetMapping(path = "/hello-world-bean")
    public HelloWorldBean helloWorldBean() {
        return new HelloWorldBean("Hello-World");
    }
    /*
    Above, we saw that everything was automatically happening for us. We just returned a bean back and this was automatically converted into a JSON response.
    What's the magic that is happening in the background? To be able to understand some magic with spring, we can enable debug logging in application.properties.
    The default level is info. You should not see a lot more information printed in the log. One of the important things to remember is the logs keep changing frequently,
    even with minor versions in spring so sometimes there are log changes. So all of us may not see the exact logs.
     */

    // Here the URL should be hello-world/path-variable/variable. We have a variable in here.
    // So this is hello world and path-variable are constants, and we'll have name as a variable. So that's why we are putting it in braces to indicate that this is a variable.
    @GetMapping(path = "/hello-world/path-variable/{name}")
    public HelloWorldBean helloWorldPathVariable(@PathVariable String name) {
        // To capture the value inside above name variable, use something called @PathVariable. Make sure that this "name" matches whatever is in above {}.
        //So what happens is when we type in hello-world/path/Ranga. This ranga over here would be mapped by spring MVC to the name in method argument.
        return new HelloWorldBean(String.format("Hello-World %s", name));
    } // So path parameters are nothing but the variable values in your URLs. And to be able to capture the values of path parameters, we make use of an annotation @PathVariable.

    @GetMapping(path = "/hello-world-internationalized") // There are methods like post mapping, put mapping and a lot of other methods which you can use.
    public String helloWorldInternationalized()
    {
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("good.morning.message", null, "Default Message", locale);
        return message;

        /*
        `en` - English (Good Morning)
        `nl` - English (Goedemorgen)
        `fr` - English (Bonjour)
        `de` - English (Guten Morgen)
         */
    }
}
/*
We configured logging.level.org.springframework to debug level in application.properties. So the first question - How are our requests getting handled?

Whenever we talk about Spring MVC, we are talking about a front controller pattern i.e. all our requests are first going to something called Dispatcher Servlet.
Earlier we exhibited a request to localhost:8080/hello-world-bean and we also executed requests to /hello-world, so irrespective of the URL we are making use of,
all requests when we talk about spring MVC are being handled by DISPATCHER Servlet. This is called the front controller pattern.

Basically, dispatcher servlet is the first thing that your request goes to, irrespective of the URL you are making use of because DISPATCHER Servlet is mapped to the root URL.

If you search the logs for mapping servlet or dispatcherServlet, you should see something like Mapping servlets: dispatcherServlet.
So we can see that the dispatcher servlet is being mapped to the root URL.

If you are not able to see the logs, then right click -> preferences and make sure that you do not have any limit on the console output.
So if you remove the limit on the console output and if you restart the application, you should see something like this.

If you search for dispatcher servlet, you should see the URL of root mapped to it. So all the requests first go to the dispatcher servlet.
And once dispatcher servlet gets the request, it would map the request to the right controller.
Right now we only have one controller, so dispatcher servlet will look at the URL and the dispatcher servlet would map it to the right controller method.

So if you are sending it to /hello-world then dispatcher servlet knows that the HelloWorld method is the right one for it.

If you are sending a request to the hello-world-bean URL, Dispatcher servlet knows that hello world bean method can handle that and
it will get the response from the HelloWorldBean and return it back.

Now you might be wondering who is configuring dispatcher Servlet. The DISPATCHER Servlet is configured because of something called auto-configuration.

Auto configuration is one of the most important Spring boot features based on the classes which are available in the class path,
Spring Boot would automatically detect the fact that we are building a web application, a rest API, and therefore it automatically configures a dispatcher servlet.

If you search for DispatcherServletAutoconfiguration in the log, you should see a bean which is being created.
So this has matched and therefore this would go ahead and configure a dispatcher servlet for us.

The next question is how does HelloWorldBean Object get converted to JSON? Earlier we executed this request localhost:8080/hello-world-bean.
So the request goes to dispatcher servlet, it would check what are the URLs that are available and it will be able to map and be able to detect that
the /hello-world-bean is mapped to helloWorldBean method so it would execute this method and over there we are returning a Java bean back.

How does it get converted to JSON?
There are two very, very important parts that are involved. Number one is a @ResponseBody. Next one is the configuration of JacksonHttpMessageConverters.

Let's start with response body.

Over here we are configuring the annotation @RestController. If you click it and go inside control, click or command click and go inside.

You'll be able to see that this has the annotation @ResponseBody. Basically what we are telling is that this bean should be returned as is.
And when we return the been as is there is message conversion which would happen. The default condition, which is set up by Springboard Auto Configuration,
is using JacksonHttpMessageConverters and this is also a result of autoconfiguration.

Let's search our autoconfiguration results. You should see something called JacksonHttpMessageConvertersConfiguration. This is automatically configured for rest API by spring boot.
So the HelloWorldBean is getting converted to JSON because of our @ResponseBody annotation and the fact that Jackson HTTP message converters are autoconfigured by spring boot.

Now who is configuring error mapping? What do I mean? Let's go in here and type in a wrong url instead of /hello-world-bean, type in hello-world-bean-one
then we get a Whitelabel Error Page. It says this application has no explicit mapping for /error and it's saying 404 is the status.

Now who's configuring this error page? The error page is also a result of autoconfiguration, so you'd see something called errorMvcAutoConfiguration.
If you search the logs, you would see something called ErrorMVCAutoConfiguration. And if you open it up. If you scroll down a little, you can see "Whitelabel Error Page" text there.

So this is where our Whitelabel Error Page text is coming in from. There is no error mapping, so this errorMvcAutoConfiguration class is made use of to
configure an error page automatically whenever an unknown URL is typed into the browser.

The last question we'll be talking about is how are all the jars available - Spring, Spring MVC, Jackson, Tomcat.
We saw that our application is running directly in Tomcat. We did not do anything to set up Tomcat at all. So how are these all available to us?
The answer to that is because of startup projects. When we created this project, we included spring web in our dependencies.
And Spring Web is what is called spring-boot-starter-web.

If you go over to our pom.xml. You'd see spring-boot-starter-web there. And if I look at the XML for this, you would see that there are a number of
dependencies which are defined there like spring-boot-starter, spring-boot-starter-json, spring-boot-starter-tomcat. This is what brings in JSON conversion with Jackson by default and Tomcat.
And this spring-web dependency which is present in here, is the one which brings in spring MVC.

So you can see that all the dependencies that we are making use of are coming in because of just one starter, spring-boot-starter-web.
So at a high level, the important thing to understand is that the startup projects bring in all the dependencies.

So Spring, Spring MVC, Jackson Framework, Tomcat, all of them are coming into your project because of startup projects and Springboot Auto Configuration
does rest of the magic. Springboard Auto Configuration looks at the class path, looks at all the jars and the classes that are available in the class path.

And based on what is available in the class path, it can autoconfigure a lot of things by default whenever we have spring-boot-startup-web in the configuration.
Tomcat web server would be autoconfigured, Dispatcher servlet would be autoconfigured. The bean to JSON conversion would be autoconfigured using Jackson and
an error page is also autoconfigured for us. So all the magic that is happening in the application right now is because of Spring Boot and
the two most important features of Spring Boot - startup projects and Autoconfiguration.
 */