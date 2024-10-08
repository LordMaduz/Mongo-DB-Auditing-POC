package com.audit.mongo.handler;

import java.lang.annotation.Annotation;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.repository.config.RepositoryConfigurationDelegate;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;
import org.springframework.data.repository.config.RepositoryConfigurationUtils;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

public abstract class AuditingRepositoryBeanDefinitionRegistrarSupport implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {

    @NonNull
    private ResourceLoader resourceLoader;
    @NonNull
    private Environment environment;

    public AuditingRepositoryBeanDefinitionRegistrarSupport() {
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Deprecated
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        this.registerBeanDefinitions(metadata, registry, ConfigurationClassPostProcessor.IMPORT_BEAN_NAME_GENERATOR);
    }

    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry, BeanNameGenerator generator) {
        Assert.notNull(metadata, "AnnotationMetadata must not be null");
        Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
        Assert.notNull(this.resourceLoader, "ResourceLoader must not be null");
        if (metadata.getAnnotationAttributes(this.getAnnotation().getName()) != null) {
            AuditableAnnotationRepositoryConfigurationSource configurationSource = new AuditableAnnotationRepositoryConfigurationSource(metadata, this.getAnnotation(), this.resourceLoader, this.environment, registry, generator);
            RepositoryConfigurationExtension extension = this.getExtension();
            RepositoryConfigurationUtils.exposeRegistration(extension, registry, configurationSource);
            RepositoryConfigurationDelegate delegate = new RepositoryConfigurationDelegate(configurationSource, this.resourceLoader, this.environment);
            delegate.registerRepositoriesIn(registry, extension);
        }
    }

    protected abstract Class<? extends Annotation> getAnnotation();

    protected abstract RepositoryConfigurationExtension getExtension();
}
