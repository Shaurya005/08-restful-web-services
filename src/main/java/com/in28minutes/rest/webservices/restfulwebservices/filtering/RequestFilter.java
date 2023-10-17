package com.in28minutes.rest.webservices.restfulwebservices.filtering;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class RequestFilter {

    private Timestamp modifiedSince;
    private Timestamp modifiedBefore;
    private boolean deletedOnly;
    private String merchantId;
    private Integer limit;
    private Integer offset = 0;
    private String tableName;
    private boolean beforeAndMostRecentEqual;
}
