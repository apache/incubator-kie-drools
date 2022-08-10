package org.optaplanner.core.impl.domain.lookup;

import org.junit.jupiter.api.BeforeEach;
import org.optaplanner.core.api.domain.common.DomainAccessType;
import org.optaplanner.core.api.domain.lookup.LookUpStrategyType;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessorFactory;
import org.optaplanner.core.impl.domain.policy.DescriptorPolicy;

abstract class AbstractLookupTest {

    private final LookUpStrategyType lookUpStrategyType;
    protected LookUpManager lookUpManager;

    protected AbstractLookupTest(LookUpStrategyType lookUpStrategyType) {
        this.lookUpStrategyType = lookUpStrategyType;
    }

    @BeforeEach
    void setUpLookUpManager() {
        lookUpManager = new LookUpManager(createLookupStrategyResolver(lookUpStrategyType));
    }

    protected LookUpStrategyResolver createLookupStrategyResolver(LookUpStrategyType lookUpStrategyType) {
        DescriptorPolicy descriptorPolicy = new DescriptorPolicy();
        descriptorPolicy.setMemberAccessorFactory(new MemberAccessorFactory());
        descriptorPolicy.setDomainAccessType(DomainAccessType.REFLECTION);
        return new LookUpStrategyResolver(descriptorPolicy, lookUpStrategyType);
    }
}
