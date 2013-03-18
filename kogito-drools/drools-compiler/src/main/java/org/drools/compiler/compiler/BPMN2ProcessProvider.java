package org.drools.compiler.compiler;

import org.kie.api.Service;

public interface BPMN2ProcessProvider extends Service {

    void configurePackageBuilder(PackageBuilder packageBuilder);

}
