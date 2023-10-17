package com.in28minutes.rest.webservices.restfulwebservices.filtering;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class TokenBucket {

    private Timestamp token;
    private Integer count;

}
