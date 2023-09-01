package org.kie.api.builder;

import org.kie.api.internal.utils.KieService;

public interface KieScannerFactoryService extends KieService {

    KieScanner newKieScanner();
}
