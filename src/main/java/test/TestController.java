package test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("hello")
public class TestController {
    @Autowired
    private TestEntityService service;

    @RequestMapping(method = RequestMethod.GET)
    public String getHelloWorld() {
        return "hello world";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/entities")
    public String getEntityCount() {
        return String.valueOf(service.getEntityCount());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/entities/{id}", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public TestEntity getTestEntity(@PathVariable("id") int id) {
        return service.getTestEntity(id);
    }
}
