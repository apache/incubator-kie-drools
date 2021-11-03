package org.optaplanner.quarkus.runtime.graal;

import org.graalvm.nativeimage.ImageSingletons;
import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.impl.RuntimeClassInitializationSupport;

import com.oracle.svm.core.annotate.AutomaticFeature;

@AutomaticFeature
public class OptaPlannerFeature implements Feature {
    @Override
    public void afterRegistration(AfterRegistrationAccess access) {
        final RuntimeClassInitializationSupport runtimeInit = ImageSingletons.lookup(RuntimeClassInitializationSupport.class);
        final String reason = "Quarkus run time init for OptaPlanner";
        // TODO: Remove after https://issues.redhat.com/browse/DROOLS-6643 is resolved.
        runtimeInit.initializeAtRunTime("org.drools.compiler.kproject.models.KieModuleMarshaller", reason);
        runtimeInit.initializeAtRunTime("org.drools.core.rule.KieModuleMetaInfo$Marshaller", reason);

        runtimeInit.initializeAtRunTime("org.drools.core.rule.JavaDialectRuntimeData", reason);
    }
}
