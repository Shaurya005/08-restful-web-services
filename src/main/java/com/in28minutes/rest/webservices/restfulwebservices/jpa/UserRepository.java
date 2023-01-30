package com.in28minutes.rest.webservices.restfulwebservices.jpa;

import com.in28minutes.rest.webservices.restfulwebservices.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

/*
We would want user resource to be talking to a database, and the way user resource can talk to the database is through user repository.
This would manage the user entity. And what is the type of the ID field? The type of the ID field is integer.
And now I want to make use of user repository in the user resource and talk to the database.
 */
public interface UserRepository extends JpaRepository<User, Integer> {

}
