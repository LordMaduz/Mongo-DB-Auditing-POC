package com.audit.mongo.controller;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruchira.collections.model.Person;
import com.ruchira.collections.repo.PersonAuditTrailRepository;
import com.ruchira.collections.repo.PersonRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/person")
@RequiredArgsConstructor
@Transactional
public class PersonController {

    private final PersonRepository personRepository;
    private final PersonAuditTrailRepository auditTrailRepository;

    @PostMapping
    public Person createUpdate(@RequestBody Person person) {
        return personRepository.save(person);
    }

    @PostMapping("/all")
    public Iterable<Person> createUpdateList(@RequestBody Person person) {
        return personRepository.saveAll(List.of(person, person));
    }

    @GetMapping("/{id}")
    public Person getById(@PathVariable String id) {
        Person person = personRepository.findById(id)
            .get();
        return person;
    }

    @GetMapping
    public Iterable<Person> getAll() {
        return personRepository.findAll();
    }

}
