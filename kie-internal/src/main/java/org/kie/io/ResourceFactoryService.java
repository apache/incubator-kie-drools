package org.kie.io;

public interface ResourceFactoryService extends KieResources {
    ResourceChangeNotifier getResourceChangeNotifierService();

    ResourceChangeScanner getResourceChangeScannerService();

}
