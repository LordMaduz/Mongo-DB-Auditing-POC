package com.audit.mongo.controller;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.audit.mongo.model.School;
import com.audit.mongo.repo.SchoolRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/school")
@RequiredArgsConstructor
@Transactional
public class SchoolController {

    private final SchoolRepository schoolRepository;

    @PostMapping
    public School createUpdate(@RequestBody School school) {
        return schoolRepository.save(school);
    }

    @PostMapping("/all")
    public Iterable<School> createUpdateList(@RequestBody School school) {
        return schoolRepository.saveAll(List.of(school, school));
    }

    @GetMapping("/{id}")
    public School getById(@PathVariable String id) {
        School school = schoolRepository.findById(id)
            .get();
        return school;
    }

    @GetMapping
    public Iterable<School> getAll() {
        return schoolRepository.findAll();
    }

}
