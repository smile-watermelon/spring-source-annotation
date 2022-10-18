package com.guagua.springboot;

/**
 * @author guagua
 * @date 2022/10/18 12:46
 * @describe
 */
/*
 * Copyright 2012-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.util.function.Supplier;

import org.springframework.beans.BeanUtils;
//import org.springframework.boot.SpringApplication;
import com.guagua.springboot.WebApplicationType;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.support.SpringFactoriesLoader;

/**
 * Strategy interface for creating the {@link ConfigurableApplicationContext} used by a
 * {@link org.springframework.boot.SpringApplication}. Created contexts should be returned in their default form,
 * with the {@code SpringApplication} responsible for configuring and refreshing the
 * context.
 *
 * @author Andy Wilkinson
 * @author Phillip Webb
 * @since 2.4.0
 */
@FunctionalInterface
public interface ApplicationContextFactory {

    /**
     * A default {@link org.springframework.boot.ApplicationContextFactory} implementation that will create an
     * appropriate context for the {@link org.springframework.boot.WebApplicationType}.
     */
    ApplicationContextFactory DEFAULT = (webApplicationType) -> {
        try {
            for (org.springframework.boot.ApplicationContextFactory candidate : SpringFactoriesLoader
                    .loadFactories(org.springframework.boot.ApplicationContextFactory.class, org.springframework.boot.ApplicationContextFactory.class.getClassLoader())) {
                ConfigurableApplicationContext context = candidate.create(webApplicationType);
                if (context != null) {
                    return context;
                }
            }
            return new AnnotationConfigApplicationContext();
        }
        catch (Exception ex) {
            throw new IllegalStateException("Unable create a default ApplicationContext instance, "
                    + "you may need a custom ApplicationContextFactory", ex);
        }
    };

    /**
     * Creates the {@link ConfigurableApplicationContext application context} for a
     * {@link SpringApplication}, respecting the given {@code webApplicationType}.
     * @param webApplicationType the web application type
     * @return the newly created application context
     */
    ConfigurableApplicationContext create(WebApplicationType webApplicationType);

    /**
     * Creates an {@code ApplicationContextFactory} that will create contexts by
     * instantiating the given {@code contextClass} via its primary constructor.
     * @param contextClass the context class
     * @return the factory that will instantiate the context class
     * @see BeanUtils#instantiateClass(Class)
     */
    static org.springframework.boot.ApplicationContextFactory ofContextClass(Class<? extends ConfigurableApplicationContext> contextClass) {
        return of(() -> BeanUtils.instantiateClass(contextClass));
    }

    /**
     * Creates an {@code ApplicationContextFactory} that will create contexts by calling
     * the given {@link Supplier}.
     * @param supplier the context supplier, for example
     * {@code AnnotationConfigApplicationContext::new}
     * @return the factory that will instantiate the context class
     */
    static org.springframework.boot.ApplicationContextFactory of(Supplier<ConfigurableApplicationContext> supplier) {
        return (webApplicationType) -> supplier.get();
    }

}
