package org.optaplanner.core.impl.domain.solution.cloner.gizmo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.optaplanner.core.api.domain.common.DomainAccessType;
import org.optaplanner.core.api.domain.solution.cloner.SolutionCloner;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;

public final class GizmoSolutionClonerFactory {
    // GizmoSolutionCloner are stateless, and thus can be safely reused across multiple instances
    private static Map<String, SolutionCloner> solutionClonerMap = new ConcurrentHashMap<>();

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

    public static void useSolutionClonerMap(Map<String, SolutionCloner> theSolutionClonerMap) {
        solutionClonerMap = theSolutionClonerMap;
    }

    public static <T> SolutionCloner<T> build(SolutionDescriptor<T> solutionDescriptor) {
        String gizmoMemberAccessorClassName = getGeneratedClassName(solutionDescriptor);
        if (solutionClonerMap.containsKey(gizmoMemberAccessorClassName)) {
            return (SolutionCloner<T>) solutionClonerMap.get(gizmoMemberAccessorClassName);
        } else {
            try {
                // Check if Gizmo on the classpath by verifying we can access one of its classes
                Class.forName("io.quarkus.gizmo.ClassCreator", false,
                        Thread.currentThread().getContextClassLoader());
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("When using the domainAccessType (" +
                        DomainAccessType.GIZMO +
                        ") the classpath or modulepath must contain io.quarkus.gizmo:gizmo.\n" +
                        "Maybe add a dependency to io.quarkus.gizmo:gizmo.");
            }
            SolutionCloner<T> cloner = GizmoSolutionClonerImplementor.createClonerFor(solutionDescriptor);
            solutionClonerMap.put(gizmoMemberAccessorClassName, cloner);
            return cloner;
        }
    }

    // ************************************************************************
    // Private constructor
    // ************************************************************************

    private GizmoSolutionClonerFactory() {
    }
}
