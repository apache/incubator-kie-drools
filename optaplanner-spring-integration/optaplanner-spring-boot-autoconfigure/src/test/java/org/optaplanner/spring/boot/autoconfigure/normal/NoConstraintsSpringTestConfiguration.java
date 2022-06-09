package org.optaplanner.spring.boot.autoconfigure.normal;

import org.optaplanner.spring.boot.autoconfigure.normal.domain.TestdataSpringEntity;
import org.optaplanner.spring.boot.autoconfigure.normal.domain.TestdataSpringSolution;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EntityScan(basePackageClasses = { TestdataSpringEntity.class, TestdataSpringSolution.class })
public class NoConstraintsSpringTestConfiguration {
}
