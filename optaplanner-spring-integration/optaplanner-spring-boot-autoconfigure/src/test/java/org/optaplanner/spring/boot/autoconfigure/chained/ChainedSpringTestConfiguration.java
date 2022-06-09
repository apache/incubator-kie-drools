package org.optaplanner.spring.boot.autoconfigure.chained;

import org.optaplanner.spring.boot.autoconfigure.chained.constraints.TestdataChainedSpringConstraintProvider;
import org.optaplanner.spring.boot.autoconfigure.chained.domain.TestdataChainedSpringSolution;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EntityScan(basePackageClasses = { TestdataChainedSpringSolution.class, TestdataChainedSpringConstraintProvider.class })
@AutoConfigurationPackage
public class ChainedSpringTestConfiguration {

}
