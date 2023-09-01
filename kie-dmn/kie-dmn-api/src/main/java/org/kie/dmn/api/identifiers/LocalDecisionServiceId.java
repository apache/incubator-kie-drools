package org.kie.dmn.api.identifiers;

import org.kie.efesto.common.api.identifiers.Id;
import org.kie.efesto.common.api.identifiers.LocalId;
import org.kie.efesto.common.api.identifiers.LocalUriId;

public class LocalDecisionServiceId extends LocalUriId implements LocalId {
    public static final String PREFIX = "services";

    private final Id decisionId;
    private final String serviceId;

    public LocalDecisionServiceId(Id decisionId, String serviceId) {
        super(decisionId.toLocalId().asLocalUri().append(PREFIX).append(serviceId));
        LocalId localDecisionId = decisionId.toLocalId();
        if (!localDecisionId.toLocalId().asLocalUri().startsWith(LocalDecisionId.PREFIX)) {
            throw new IllegalArgumentException("Not a valid decision path"); // fixme use typed exception
        }
        this.decisionId = decisionId;
        this.serviceId = serviceId;
    }

    public Id decisionId() {
        return decisionId;
    }

    public String serviceId() {
        return serviceId;
    }

    @Override
    public LocalId toLocalId() {
        return this;
    }

}
