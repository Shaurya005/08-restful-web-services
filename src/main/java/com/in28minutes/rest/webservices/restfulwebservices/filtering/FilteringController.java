package com.in28minutes.rest.webservices.restfulwebservices.filtering;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/*
Another thing that you might want to do in your response is to only return selected fields. This is called filtering. For example, you might have a password
defined in your bean, and you don't want to send passwords out in the response. There are two types of filtering, static filtering and dynamic filtering.
Suppose, we have a bean with three fields field1, field2, field3.

Let's say I would want to always filter out field2, field2 is like a password field and I don't want to send it as part of any rest API responses.
So irrespective of the rest API, I would want to always block or filter out field2. This is what is called static filtering.

Basically same filtering for a bean across different rest API. The alternative is dynamic filtering.
Let's consider an example with field1 which I want to send as part of a specific API. However, for a different API, I don't want to send it.
So you need to dynamically decide if it wants to send a field as part of a response or not. That is what is called dynamic filtering.
 */
@RestController
public class FilteringController
{
//    @GetMapping("/filtering")
//    public SomeBean filtering() {
//        return new SomeBean("value1","value2", "value3");
//    }

    @GetMapping("/filtering") //field2
    public MappingJacksonValue filtering()
    {
        /*
        There might be situations where you might want to return different attributes for the same bean in different rest API. That is what is called dynamic filtering.
        You want to customize the filtering for a bean for a specific rest API. For example, we have two rest API filtering-list and filtering.
        What we do is for filtering, we will have field1 and field3 in the response. However, for filtering-list we will have field2 and field3 in the response.

        So for this bean we will have different filtering logic in different rest API. Let's see how to implement that because we want to do different things
        in different rest API, we cannot define filtering on the bean itself. The logic to filter will now have to be defined in your rest API.

        So over here in this rest API method, we not only want to define the data, but also we want to define how to do the filtering and that's where a class
        called MappingJacksonValue can help us. If you have specific serialization instructions that you would want to pass to the converter, to the Jackson converter,
        in that kind of scenario, we can make use of MappingJacksonValue.
         */
        SomeBean someBean = new SomeBean("value1","value2", "value3");
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(someBean);

        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("field1","field3");
        FilterProvider filters = new SimpleFilterProvider().addFilter("SomeBeanFilter", filter );

        /*
        We have to define this filter on our bean. So in addition to writing the code in here.
        On the SomeBean you need to define @JsonFilter and give it a name, SomeBeanFilter. Name should match whatever we have in here.
         */

        mappingJacksonValue.setFilters(filters );

        /*
        So what we are doing in here is we have the bean. This bean contains the data which you would want to return back. What we are defining in here is the
        filtering logic because we would want to execute a filter. We are making use of mapping Jackson value and we are returning mapping Jackson value back.
        So the mapping Jackson value allows you to add serialization logic in addition to your data. To be able to add the serialization logic, we need to define filters.

        Here we are making use of a simple in property filter.We would want to only allow field1 and field3 in the response. That's what we are defining using the filter.
        And we are creating a filter provider defining this specific filter. Once we have the filter ready, we are setting the filter into mapping Jackson value.
        So we are retaining the bean as well as the serialization logic back. And we see that only field1 and field3 are coming in our response.
         */
        return mappingJacksonValue;
    }

    @GetMapping("/filtering-list") //field2, field3
    public MappingJacksonValue filteringList()
    {
        List<SomeBean> list = Arrays.asList(new SomeBean("value1","value2", "value3"), new SomeBean("value4","value5", "value6"));

        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(list);

        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("field2","field3");
        FilterProvider filters = new SimpleFilterProvider().addFilter("SomeBeanFilter", filter );

        mappingJacksonValue.setFilters(filters );

        return mappingJacksonValue;
    }
}