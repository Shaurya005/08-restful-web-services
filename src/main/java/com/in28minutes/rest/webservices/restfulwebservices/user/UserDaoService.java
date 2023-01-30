package com.in28minutes.rest.webservices.restfulwebservices.user;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
/*
What we want to be able to do is to build a rest API around the User bean. We would want to be able to save the details of the user,
retrieve the details of the user, delete a specific user and do a lot of operations with the specific user.

And to be able to play with a database and to be able to perform operations with something which is stored in a database.
We would create a DAO object i.e. Data Access Object. So this class is our DAO service to manage these things.

What I want to do in this specific UserDaoService is to implement methods to be able to retrieve all users, to be able to save details of a specific user,
to be able to retrieve details of a specific user. So these are all the methods that I would want to implement in this specific UserDaoService.

And typically we would want to store all the user details in the database and we want to use JPA or Hibernate to talk to the database.
To keep things simple, we'll create a static list, so we'll have the UserDaoService talk to the static ArrayList, which you would create in here a little later.

We will learn a lot more about JPA and Hibernate and convert all the rest API that we create to talk to the database.
For now, we'll be creating a static ArrayList and will be using UserDaoService to talk to this static ArrayList.
 */
@Component // I would want spring to manage this so make this @Component.
public class UserDaoService {
    // JPA/Hibernate > Database
    // UserDaoService > Static List

    private static List<User> users = new ArrayList<>();

    private static int usersCount = 0;

    static {
        users.add(new User(++usersCount,"Adam", LocalDate.now().minusYears(30)));
        users.add(new User(++usersCount,"Eve",LocalDate.now().minusYears(25)));
        users.add(new User(++usersCount,"Jim",LocalDate.now().minusYears(20)));
    }

    public List<User> findAll() {
        return users;
    }

    public User save(User user) {
        user.setId(++usersCount);
        users.add(user);

        return user;
    }

    public User findOne(int id) {
        return users.stream()
                .filter(u -> u.getId().equals(id))
                //.findFirst().get();
                .findFirst().orElse(null);
    }

    public void deleteById(int id) {
        Predicate<User> predicate = user -> user.getId().equals(id);
        users.removeIf(predicate);
    }
/*
One of the best practices when it comes to rest API is to return the right response status.

So if I type in localhost:8080/users/1 with an existing user, this returns a proper response back. This would actually return a status of 200, which is perfect.

However, let's say I type in a non-existing user localhost:8080/users/101. This is actually returning a white label error page.
It's also returning an exception traceback back and this is not good. So what I'll do right now is go to findOne method. So where are we getting the exception from?
Optional.get() So get method is the one from which we are getting an error from.

So when we filter, and we don't have a findFirst, we don't have an element which matches. In that kind of scenario, The .get() method would throw an exception.
If you hover over the get method, you would see this documentation - "If a value is present, returns the value. Otherwise, it would throw the "NoSuchElementException".

And therefore a better approach would be to say orElse. If a value is present, it returns the value, otherwise it returns the other value which we define.
So I will say orElse and return null back. So from findOne method, we are now returning a null back. Does that improve it? Let's see what happens.

Now when we go to localhost:8080/users/101. We get nothing coming back. We get no response coming back. And if I do inspect and look at the network and
do a refresh, you are seeing that we are getting a 200 status back. It's saying successful with no data coming back and that's not good as well.
So we'll create and use our own Exception in case user is not found. And that's the reason what we want to do is, go to userDaoResource take service.findOne(id)
into a local variable user and if it's null then throw a new custom exception which we need to create. And we would pass in a message with if of the user.
 */
}