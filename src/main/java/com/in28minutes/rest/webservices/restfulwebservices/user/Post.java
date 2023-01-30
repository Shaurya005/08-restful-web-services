package com.in28minutes.rest.webservices.restfulwebservices.user;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
public class Post {

    @Id
    @GeneratedValue
    private Integer id;

    @Size(min = 10)
    private String description;

    /*
    The fetch attributing here decides whether the association should be lazily loaded or eagerly fetched.
    If you want to retrieve the details of the post and the user in the same query, you are asking for eager fetch.
    So if you're asking for eager fetch along with the post details, the user details also will be fetched and that's the default for many to one relationship.

    However, what I want to do is to use a lazy Fetch type.
    When we fetch the post, we don't really want to fetch the user details that are associated with the post.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore // As we don't want user to be part of the JSON responses for post bean.
    private User user;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Post{" + "id=" + id + ", description='" + description + '\'' + '}';
    }
}
