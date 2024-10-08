package com.ruchira.collections.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ruchira.collections.model.Person;

@Repository
public interface PersonRepository extends CrudRepository<Person, String> {

}
