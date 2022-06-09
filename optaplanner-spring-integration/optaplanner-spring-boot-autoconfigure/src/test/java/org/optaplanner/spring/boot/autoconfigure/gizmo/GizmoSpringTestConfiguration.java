package org.optaplanner.spring.boot.autoconfigure.gizmo;

import org.optaplanner.spring.boot.autoconfigure.gizmo.constraints.TestdataGizmoConstraintProvider;
import org.optaplanner.spring.boot.autoconfigure.gizmo.domain.TestdataGizmoSpringEntity;
import org.optaplanner.spring.boot.autoconfigure.gizmo.domain.TestdataGizmoSpringSolution;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EntityScan(basePackageClasses = { TestdataGizmoSpringEntity.class, TestdataGizmoSpringSolution.class,
        TestdataGizmoConstraintProvider.class })
public class GizmoSpringTestConfiguration {
}
