package org.kie.internal.io;

import org.kie.api.io.KieResources;

public interface ResourceFactoryService extends KieResources {
    ResourceChangeNotifier getResourceChangeNotifierService();

    ResourceChangeScanner getResourceChangeScannerService();

}
