package jdd.service;

import jdd.solr.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    @RequestMapping(value="/person", method = RequestMethod.POST, consumes = "application/json")
    public String indexPerson(Person person) {

    }
}
