package org.kie.scanner;

import org.drools.compiler.kie.builder.impl.InternalKieScanner;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class KieScannersRegistry {

    private static List<WeakReference<InternalKieScanner>> scanners = new ArrayList<>();

    static void register(InternalKieScanner scanner) {
        scanners.add(new WeakReference<>(scanner));
    }

    public static Collection<InternalKieScanner> getAllKieScanners() {
        List<InternalKieScanner> allScanners = new ArrayList<>();
        Iterator<WeakReference<InternalKieScanner>> i = scanners.iterator();
        while (i.hasNext()) {
            InternalKieScanner scanner = i.next().get();
            if (scanner == null) {
                i.remove();
            } else {
                allScanners.add(scanner);
            }
        }
        return allScanners;
    }
}
