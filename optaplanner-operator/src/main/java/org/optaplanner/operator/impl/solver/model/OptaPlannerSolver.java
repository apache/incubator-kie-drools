package org.optaplanner.operator.impl.solver.model;

import org.optaplanner.operator.impl.solver.model.messaging.MessageAddress;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Kind;
import io.fabric8.kubernetes.model.annotation.Version;

@Group("org.optaplanner.solver")
@Kind("Solver")
@Version("v1beta1")
public final class OptaPlannerSolver extends CustomResource<OptaPlannerSolverSpec, OptaPlannerSolverStatus>
        implements Namespaced {

    // TODO: Move all the following methods away if this class ever becomes an API.
    @JsonIgnore
    public String getNamespace() {
        return getMetadata().getNamespace();
    }

    @JsonIgnore
    public String getConfigMapName() {
        return getSolverName();
    }

    @JsonIgnore
    public String getDeploymentName() {
        return getSolverName();
    }

    @JsonIgnore
    public String getInputMessageAddressName() {
        return getMessageAddressName(MessageAddress.INPUT);
    }

    @JsonIgnore
    public String getOutputMessageAddressName() {
        return getMessageAddressName(MessageAddress.OUTPUT);
    }

    @JsonIgnore
    public String getMessageAddressName(MessageAddress messageAddress) {
        return String.format("%s-%s", getSolverName(), messageAddress.getName());
    }

    @JsonIgnore
    public String getTriggerAuthenticationName() {
        return getSolverName();
    }

    @JsonIgnore
    public String getScaledObjectName() {
        return getSolverName();
    }

    @JsonIgnore
    public String getScaledObjectTriggerName() {
        return getSolverName();
    }

    @JsonIgnore
    private String getSolverName() {
        return getMetadata().getName();
    }

}
