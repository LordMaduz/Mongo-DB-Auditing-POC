package com.audit.mongo.handler;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.repository.config.AnnotationRepositoryConfigurationSource;
import org.springframework.data.util.Streamable;
import org.springframework.util.ClassUtils;

public class AuditableAnnotationRepositoryConfigurationSource extends AnnotationRepositoryConfigurationSource {

    private static final String BASE_PACKAGES = "basePackages";
    private static final String BASE_PACKAGE_CLASSES = "basePackageClasses";

    private final AnnotationMetadata configMetadata;
    private final AnnotationAttributes attributes;
    private final Environment environment;

    public AuditableAnnotationRepositoryConfigurationSource(AnnotationMetadata metadata,
        Class<? extends Annotation> annotation, ResourceLoader resourceLoader,
        Environment environment,
        BeanDefinitionRegistry registry,
        BeanNameGenerator generator) {

        super(metadata, annotation, resourceLoader, environment, registry, generator);
        Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes(annotation.getName());
        if (annotationAttributes == null) {
            throw new IllegalStateException(String.format("Unable to obtain annotation attributes for %s", annotation));
        }
        this.attributes = new AnnotationAttributes(annotationAttributes);
        this.configMetadata = metadata;
        this.environment = environment;
    }

    @Override
    public Streamable<String> getBasePackages() {

        String value = attributes.getStringArray("value")[0];
        String basePackages = attributes.getStringArray(BASE_PACKAGES)[0];
        Class<?>[] basePackageClasses = attributes.getClassArray(BASE_PACKAGE_CLASSES);

        if (StringUtils.isEmpty(value) && StringUtils.isEmpty(basePackages) && basePackageClasses.length == 0) {
            String className = configMetadata.getClassName();
            return Streamable.of(ClassUtils.getPackageName(className));
        }

        String[] packagesFromValue = parsePackages(value);
        String[] packagesFromBasePackages = parsePackages(basePackages);

        Set<String> packages = new HashSet<>();
        packages.addAll(Arrays.asList(packagesFromValue));
        packages.addAll(Arrays.asList(packagesFromBasePackages));

        for (Class<?> typeName : basePackageClasses) {
            packages.add(ClassUtils.getPackageName(typeName));
        }
        return Streamable.of(packages);
    }

    private String[] parsePackages(String input) {
        if (!input.trim().startsWith("$")) {
            if (StringUtils.isEmpty(input)) {
                return new String[]{};
            }
            return input.split(",");
        } else {
            input = input.trim();
            String packages = this.environment.getProperty(input.substring("${".length(), input.length() - "}".length()));
            assert packages != null;
            return packages.replaceAll(StringUtils.SPACE, StringUtils.EMPTY).split(",");
        }
    }
}
