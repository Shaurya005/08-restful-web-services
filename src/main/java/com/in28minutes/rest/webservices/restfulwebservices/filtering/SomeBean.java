package com.in28minutes.rest.webservices.restfulwebservices.filtering;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/*
In addition to @JsonIgnore, we can also have a JsonIgnoreProperties defined on the bean. So this annotation is something you can specify on a class.
And over here, you can specify the property names. So let's say field1. I don't want to include in the response at all, so use @JsonIgnoreProperties("field1") there.
So you can also define at the class level instead of adding @JsonIgnore, you can also use @JsonIgnoreProperties and define a class level as well.

When we want to add in it field2 as well, then pass them like a array inside a curly braces. So we can either use JsonIgnore or add JsonIgnoreProperties to implement
static filtering which is same filtering for a bin across different rest API. Typically, I prefer using JsonIgnore instead of using JsonIgnoreProperties actually like
adding it on this specific field JsonIgnore because even if the field name changes at a later point in time, you don't really need to change it at any other place.
If you're making use of JsonIgnoreProperties and if I'm actually changing field to name then I would need to go in and remember to change this in here as well.
 */
@JsonFilter("SomeBeanFilter") // For applying explicit filter created in Rest APIs
//@JsonIgnoreProperties({"field1", "field2"})
//@JsonIgnoreProperties("field1")
public class SomeBean {
    private String field1;

    // @JsonIgnore // By adding it, I'm doing static filtering on field 2 that it should not come in response.
    private String field2;

    private String field3;

    public SomeBean(String field1, String field2, String field3) {
        super();
        this.field1 = field1;
        this.field2 = field2;
        this.field3 = field3;
    }

    public String getField1() {
        return field1;
    }

    public String getField2() {
        return field2;
    }

    public String getField3() {
        return field3;
    }

    @Override
    public String toString() {
        return "SomeBean [field1=" + field1 + ", field2=" + field2 + ", field3=" + field3 + "]";
    }
}