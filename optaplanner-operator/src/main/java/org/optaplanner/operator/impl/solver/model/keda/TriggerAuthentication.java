package org.optaplanner.operator.impl.solver.model.keda;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Kind;
import io.fabric8.kubernetes.model.annotation.Version;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Group(KedaConstants.GROUP)
@Version(KedaConstants.API_VERSION)
@Kind(TriggerAuthentication.KIND)
public final class TriggerAuthentication
        extends CustomResource<TriggerAuthenticationSpec, Void> implements Namespaced {

    public static final String KIND = "TriggerAuthentication";
}
