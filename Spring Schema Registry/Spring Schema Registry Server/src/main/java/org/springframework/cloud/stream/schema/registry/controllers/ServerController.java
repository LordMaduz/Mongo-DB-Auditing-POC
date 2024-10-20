package org.springframework.cloud.stream.schema.registry.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.cloud.stream.schema.registry.config.SchemaServerProperties;
import org.springframework.cloud.stream.schema.registry.model.MongoSchema;
import org.springframework.cloud.stream.schema.registry.model.Schema;
import org.springframework.cloud.stream.schema.registry.repository.MongoSchemaRepository;
import org.springframework.cloud.stream.schema.registry.service.SequenceGeneratorService;
import org.springframework.cloud.stream.schema.registry.support.InvalidSchemaException;
import org.springframework.cloud.stream.schema.registry.support.MongoSchemaValidator;
import org.springframework.cloud.stream.schema.registry.support.SchemaDeletionNotAllowedException;
import org.springframework.cloud.stream.schema.registry.support.SchemaNotFoundException;
import org.springframework.cloud.stream.schema.registry.support.UnsupportedFormatException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "${spring.cloud.stream.schema.server.path:}")
public class ServerController {

    private final MongoSchemaRepository repository;

    private final Map<String, MongoSchemaValidator> mongoSchemaValidators;

    private final SequenceGeneratorService sequenceGeneratorService;

    private final SchemaServerProperties schemaServerProperties;

    private static final ReentrantLock lock = new ReentrantLock();

    public ServerController(MongoSchemaRepository repository, Map<String, MongoSchemaValidator> mongoSchemaValidators,SequenceGeneratorService sequenceGeneratorService, SchemaServerProperties schemaServerProperties) {
        Assert.notNull(repository, "cannot be null");
        Assert.notNull(sequenceGeneratorService, "can not be null");
        Assert.notEmpty(mongoSchemaValidators, "cannot be empty");
        this.repository = repository;
        this.sequenceGeneratorService = sequenceGeneratorService;
        this.mongoSchemaValidators = mongoSchemaValidators;
        this.schemaServerProperties = schemaServerProperties;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/", consumes = "application/json", produces = "application/json")
    public ResponseEntity<MongoSchema> register(@RequestBody MongoSchema schema, UriComponentsBuilder builder) {
        try {
            lock.lock();
            MongoSchemaValidator validator = this.mongoSchemaValidators.get(schema.getFormat());

            if (validator == null) {
                throw new UnsupportedFormatException(String.format("Invalid format, supported types are: %s", StringUtils.collectionToCommaDelimitedString(this.mongoSchemaValidators.keySet())));
            }

            validator.validate(schema.getDefinition());

            MongoSchema result;
            List<MongoSchema> registeredEntities = this.repository.findBySubjectAndFormatOrderByVersion(schema.getSubject(), schema.getFormat());
            if (registeredEntities.isEmpty()) {
                schema.setId(sequenceGeneratorService.generateSequence(MongoSchema.SEQUENCE_NAME));
                schema.setVersion(1);
                result = this.repository.save(schema);
            } else {
                result = validator.match(registeredEntities, schema.getDefinition());
                if (result == null) {
                    schema.setVersion(registeredEntities.get(registeredEntities.size() - 1)
                        .getVersion() + 1);
                    result = this.repository.save(schema);
                }

            }

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.LOCATION, builder.path("/{subject}/{format}/v{version}")
                .buildAndExpand(result.getSubject(), result.getFormat(), result.getVersion())
                .toString());
            ResponseEntity<MongoSchema> response = new ResponseEntity<>(result, headers, HttpStatus.CREATED);

            return response;
        } finally {
            lock.unlock();
        }
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json", path = "/{subject}/{format}/v{version}")
    public ResponseEntity<MongoSchema> findOne(@PathVariable("subject") String subject, @PathVariable("format") String format,
        @PathVariable("version") Integer version) {
        MongoSchema schema = this.repository.findOneBySubjectAndFormatAndVersion(subject, format, version);
        if (schema == null) {
            throw new SchemaNotFoundException(String.format("Could not find Schema by subject: %s, format: %s, version %s", subject, format, version));
        }
        return new ResponseEntity<>(schema, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json", path = "/schemas/{id}")
    public ResponseEntity<MongoSchema> findOne(@PathVariable("id") Integer id) {
        Optional<MongoSchema> schema = this.repository.findById(Long.valueOf(id));
        if (schema.isEmpty()) {
            throw new SchemaNotFoundException(String.format("Could not find Schema by id: %s", id));
        }
        return new ResponseEntity<>(schema.get(), HttpStatus.OK);
    }

    /**
     * Find by {@link Schema#getSubject() subject} and {@link Schema#getFormat() format}.
     *
     * @param subject the {@link Schema#getSubject() subject}, must not be
     *                {@literal null}.
     * @param format  the {@link Schema#getFormat() format}, must not be {@literal null}.
     * @return An {@link HttpStatus#OK} response populated with the list of {@link Schema
     * Schemas}, in ascending order by {@link Schema#getVersion() version}, that matched
     * the supplied {@link Schema#getSubject() subject} and {@link Schema#getFormat()
     * format}.
     * @since 3.0.0
     */
    @GetMapping(produces = APPLICATION_JSON_VALUE, path = "/{subject}/{format}")
    @NonNull
    public ResponseEntity<List<MongoSchema>> findBySubjectAndFormat(@NonNull @PathVariable("subject") final String subject,
        @NonNull @PathVariable("format") final String format) {
        return findBySubjectAndFormatOrderByVersionAsc(subject, format);
    }

    @RequestMapping(value = "/{subject}/{format}/v{version}", method = RequestMethod.DELETE)
    public void delete(@PathVariable("subject") String subject, @PathVariable("format") String format, @PathVariable("version") Integer version) {
        if (this.schemaServerProperties.isAllowSchemaDeletion()) {
            MongoSchema schema = this.repository.findOneBySubjectAndFormatAndVersion(subject, format, version);
            if (schema == null) {
                throw new SchemaNotFoundException(String.format("Could not find Schema by subject: %s, format: %s, version %s", subject, format, version));
            }
            deleteSchema(schema);
        } else {
            throw new SchemaDeletionNotAllowedException(
                String.format("Not permitted deletion of Schema by " + "subject: %s, format: %s, version %s", subject, format, version));
        }
    }

    @RequestMapping(value = "/schemas/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable("id") Integer id) {
        if (this.schemaServerProperties.isAllowSchemaDeletion()) {
            Optional<MongoSchema> schema = this.repository.findById(Long.valueOf(id));
            if (schema.isEmpty()) {
                throw new SchemaNotFoundException(String.format("Could not find Schema by id: %s", id));
            }
            deleteSchema(schema.get());
        } else {
            throw new SchemaDeletionNotAllowedException(String.format("Not permitted deletion of Schema by id: %s", id));
        }
    }

    @RequestMapping(value = "/{subject}", method = RequestMethod.DELETE)
    public void delete(@PathVariable("subject") String subject) {
        if (this.schemaServerProperties.isAllowSchemaDeletion()) {
            for (MongoSchema schema : this.repository.findAll()) {
                if (schema.getSubject()
                    .equals(subject)) {
                    deleteSchema(schema);
                }
            }
        } else {
            throw new SchemaDeletionNotAllowedException(String.format("Not permitted deletion of Schema by " + "subject: %s", subject));
        }

    }

    @NonNull
    public final ResponseEntity<List<MongoSchema>> findBySubjectAndFormatOrderByVersionAsc(@NonNull final String subject, @NonNull final String format) {
        List<MongoSchema> schemas = this.repository.findBySubjectAndFormatOrderByVersion(subject, format);
        if (schemas.isEmpty()) {
            throw new SchemaNotFoundException(String.format("No schemas found for subject %s and format %s", subject, format));
        }
        return new ResponseEntity<>(schemas, HttpStatus.OK);
    }

    private void deleteSchema(MongoSchema schema) {
        if (schema == null) {
            throw new SchemaNotFoundException("Could not find Schema");
        }
        this.repository.delete(schema);
    }

    @ExceptionHandler(UnsupportedFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String onUnsupportedFormat(UnsupportedFormatException e) {
        return errorMessage("Format not supported", e);
    }

    @ExceptionHandler(InvalidSchemaException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String onInvalidSchema(InvalidSchemaException e) {
        return errorMessage("Invalid Schema", e);
    }

    @ExceptionHandler(SchemaNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public String schemaNotFound(SchemaNotFoundException ex) {
        return errorMessage("Schema not found", ex);
    }

    @ExceptionHandler(SchemaDeletionNotAllowedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ResponseBody
    public String schemaDeletionNotPermitted(SchemaDeletionNotAllowedException ex) {
        return errorMessage("Schema deletion is not permitted", ex);
    }

    private String errorMessage(String prefix, Throwable e) {
        return prefix + (StringUtils.hasText(e.getMessage()) ? ": " + e.getMessage() : "");
    }
}