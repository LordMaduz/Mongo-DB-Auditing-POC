package com.audit.mongo.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.audit.mongo.model.School;

@Repository
public interface SchoolRepository extends CrudRepository<School, String> {

}
