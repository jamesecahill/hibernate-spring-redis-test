package test;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
@Slf4j
public class TestController {
    @Autowired
    private TestEntityService service;

    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_USER')")
    public String getHelloWorld() {
        return String.format("hello %s", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    @PreAuthorize("#oauth2.hasScope('read')")
    @RequestMapping(method = RequestMethod.GET, value = "/entities")
    public String getEntityCount() {
        return String.valueOf(service.getEntityCount());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/entities/{id}")
    public TestEntity getTestEntity(@PathVariable("id") int id) {
        log.debug("getting entity...");
        return service.getTestEntity(id);
    }
}
