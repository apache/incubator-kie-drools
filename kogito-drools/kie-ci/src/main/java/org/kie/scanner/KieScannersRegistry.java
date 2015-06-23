/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.scanner;

import org.drools.compiler.kie.builder.impl.InternalKieScanner;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class KieScannersRegistry {

    private static List<WeakReference<InternalKieScanner>> scanners = new ArrayList<WeakReference<InternalKieScanner>>();

    static void register(InternalKieScanner scanner) {
        scanners.add(new WeakReference<InternalKieScanner>(scanner));
    }

    public static Collection<InternalKieScanner> getAllKieScanners() {
        List<InternalKieScanner> allScanners = new ArrayList<InternalKieScanner>();
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
