package com.in28minutes.rest.webservices.restfulwebservices.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

@Entity(name = "user_details")
// For JPA, Only simply adding Entity annotation along with @Id annotation for primary key will create table in in-memory h2-database.
// For adding data to the table, create sql file named data.sql in src/main/resources and write insert query there.
public class User {

    @Id
    @GeneratedValue
    private Integer id;

    /*
    What is serialization? Serialization is nothing but the process of converting an object to a stream. For example, we are returning an EntityModel back
    or we are returning a list of users back. Converting this to a JSON or to an XML is what is called serialization. The most popular JSON serialization framework in Java is Jackson.

    Until now, we saw that whatever is the structure of this user is exactly what's in the response. So our response JSON exactly matches whatever is the structure of this user.
    So you have an ID name and birthdate, and that is exactly the structure in the response as well - ID name, birth date.

    In certain situations, you might not want to return the exact structure as what you have in here.
    You might want to customize the rest API response that is being returned by Jackson Framework.
    Let's look at a few options to customize in this and the next steps. Let's start with customizing the filenames in a response. How can you customize the field names in a response?

    You can make use of the @JsonProperty annotation. JsonProperty is very, very useful to customize the attribute and element names in your response.
     */
    @Size(min=2, message = "Name should have atleast 2 characters") // Adding validation, name should have minimum of two characters.
    @JsonProperty("user_name") // For changing attribute name in JSON response from default to "user_name".
    private String name;

    @Past(message = "Birth Date should be in the past") // Ensuring my birthdate should always be in the past.
    @JsonProperty
    private LocalDate birthDate;
    /*
    If you expand javax.validation.constraints package, you can see that there are a wide variety of constraints which are present in jakarta.validation.constraints.

    AssertTrue - You can check if something is false
    AssertTrue - Check if something is true.
    DecimalMax, DecimalMin - You can set a decimal max or decimal min.
    Basically for a numeric field you can set the max in min.
    Digits - You can say, I would need to have this many digits.
    Email - You can say this specific field should be a valid email.
    Future - You can say a specific date should be in the future.
    FutureOrPresent - This is future or present.
    Past -
    PastOrPresent -
    Min, Max - You can also set min and max on integer values.
    Negative - You can also say something should be negative.
    NotBlank, NotEmpty - You can say that something should not be blank, should not be empty.
    Null, NotNull - Something should either be null or not null.
    Pattern - You can also define a regular expression.

    You can see that there are a lot of other validations which are present.

    The ones which you are making use of right now is this Size.class and Past.class
    So there are a wide variety of constraints that you can define on your attributes which are present in your bean.
    So now if we send invalid post request, then we're getting 400 status i.e. a bad request status.
     */

    @OneToMany(mappedBy = "user") // In mappedBy, we pass the variable name of User i.e. to which it is mapped.
    // @JsonIgnore // As we don't want posts to be part of the JSON responses for user bean.
    private List<Post> posts;

    public User(Integer id, String name, LocalDate birthDate) {
        super();
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
    }

    public User() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", name=" + name + ", birthDate=" + birthDate + "]";
    }

}
