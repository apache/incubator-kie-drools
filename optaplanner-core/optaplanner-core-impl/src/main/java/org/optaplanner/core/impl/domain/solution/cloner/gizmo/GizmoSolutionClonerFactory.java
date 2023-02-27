package org.optaplanner.core.impl.domain.solution.cloner.gizmo;

import org.optaplanner.core.api.domain.common.DomainAccessType;
import org.optaplanner.core.api.domain.solution.cloner.SolutionCloner;
import org.optaplanner.core.impl.domain.common.accessor.gizmo.GizmoClassLoader;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;

public final class GizmoSolutionClonerFactory {
    /**
     * Returns the generated class name for a given solutionDescriptor.
     * (Here as accessing any method of GizmoMemberAccessorImplementor
     * will try to load Gizmo code)
     *
     * @param solutionDescriptor The solutionDescriptor to get the generated class name for
     * @return The generated class name for solutionDescriptor
     */
    public static String getGeneratedClassName(SolutionDescriptor<?> solutionDescriptor) {
        return solutionDescriptor.getSolutionClass().getName() + "$OptaPlanner$SolutionCloner";
    }

    public static <T> SolutionCloner<T> build(SolutionDescriptor<T> solutionDescriptor, GizmoClassLoader gizmoClassLoader) {
        try {
            // Check if Gizmo on the classpath by verifying we can access one of its classes
            Class.forName("io.quarkus.gizmo.ClassCreator", false, Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("When using the domainAccessType (" +
                    DomainAccessType.GIZMO +
                    ") the classpath or modulepath must contain io.quarkus.gizmo:gizmo.\n" +
                    "Maybe add a dependency to io.quarkus.gizmo:gizmo.");
        }
        return GizmoSolutionClonerImplementor.createClonerFor(solutionDescriptor, gizmoClassLoader);
    }

    // ************************************************************************
    // Private constructor
    // ************************************************************************

    private GizmoSolutionClonerFactory() {
    }
}
