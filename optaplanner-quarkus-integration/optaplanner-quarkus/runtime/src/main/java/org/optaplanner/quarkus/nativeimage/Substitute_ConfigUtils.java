package org.optaplanner.quarkus.nativeimage;

import java.util.function.Supplier;

import javax.enterprise.inject.spi.CDI;

import org.optaplanner.quarkus.gizmo.OptaPlannerGizmoBeanFactory;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

@TargetClass(className = "org.optaplanner.core.config.util.ConfigUtils")
public final class Substitute_ConfigUtils {

    @Substitute
    public static <T> T newInstance(Supplier<String> ownerDescriptor, String propertyName, Class<T> clazz) {
        T out = CDI.current().getBeanManager().createInstance().select(OptaPlannerGizmoBeanFactory.class)
                .get().newInstance(clazz);
        if (out != null) {
            return out;
        } else {
            throw new IllegalArgumentException("Impossible state: could not find the " + ownerDescriptor.get() +
                    "'s " + propertyName + " (" + clazz.getName() + ") generated Gizmo supplier.");
        }
    }
}
