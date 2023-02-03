package org.optaplanner.quarkus.gizmo;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.optaplanner.core.impl.domain.common.accessor.gizmo.GizmoMemberAccessorImplementor;

import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class OptaPlannerGizmoClassLoaderReset {

    void onStart(@Observes StartupEvent ev) {
        GizmoMemberAccessorImplementor.resetClassLoaderCache();
    }

}
