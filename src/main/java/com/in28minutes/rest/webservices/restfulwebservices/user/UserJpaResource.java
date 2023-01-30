package com.in28minutes.rest.webservices.restfulwebservices.user;

import com.in28minutes.rest.webservices.restfulwebservices.jpa.PostRepository;
import com.in28minutes.rest.webservices.restfulwebservices.jpa.UserRepository;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

/*
What is ambiguous mapping?
If you look at UserResource and the UserJpaResource, we have methods mapped with the same URL and that's not good.

So what I'll change all of these to use /jpa before all urls present.
So we have mapped in a different URL and now you should see the application start up fine.
 */
@RestController
public class UserJpaResource {

    // private UserDaoService service; We'll do everything which we did using UserDaoService using UserRepository here for working with JPA entity.
    private UserRepository userRepository;

    private PostRepository postRepository;

    public UserJpaResource(UserRepository userRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    @GetMapping("/jpa/users")
    public List<User> retrieveAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/jpa/users/{id}")
    public EntityModel<User> retrieveUserWithId(@PathVariable int id) {
        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) // Here we can't use user==null as Optional variable user is not null always.
            throw new UserNotFoundException("id: " + id);

        // For adding a link to /users
        EntityModel<User> entityModel = EntityModel.of(user.get()); // To get User from Optional we use user.get()
        WebMvcLinkBuilder link = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).retrieveAllUsers());
        entityModel.add(link.withRel("all-users"));

        return entityModel;
    }

    @DeleteMapping("/jpa/users/{id}")
    public void deleteUser(@PathVariable int id) {
        userRepository.deleteById(id);
    }

    @PostMapping("/jpa/users")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        User savedUser = userRepository.save(user);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                                                .path("/{id}")
                                                .buildAndExpand(savedUser.getId())
                                                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PostMapping("/jpa/users/{id}/posts")
    public ResponseEntity<Object> createPostForUser(@PathVariable int id, @Valid @RequestBody Post post) {
        Optional<User> user = userRepository.findById(id);

        if(user.isEmpty())
            throw new UserNotFoundException("id:"+id);

        post.setUser(user.get());

        Post savedPost = postRepository.save(post);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedPost.getId())
                .toUri(); // This location can be seen in response headers under Location key.

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/jpa/users/{user_id}/posts/{post_id}")
    public Post retrievePostWithIdForUser(@PathVariable int user_id, @PathVariable int post_id) {
        Optional<Post> post = postRepository.findById(post_id);

        if(post.isEmpty())
            throw new UserNotFoundException("id: " + post_id);

        return post.get();
    }

    @GetMapping("/jpa/users/{id}/posts")
    public List<Post> retrievePostsForUser(@PathVariable int id) {
        Optional<User> user = userRepository.findById(id);

        if(user.isEmpty())
            throw new UserNotFoundException("id:"+id);

        return user.get().getPosts();
    }
}