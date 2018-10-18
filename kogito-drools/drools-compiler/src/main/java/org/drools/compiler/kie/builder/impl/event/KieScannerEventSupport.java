/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.kie.builder.impl.event;

import java.util.Iterator;

import org.drools.core.event.AbstractEventSupport;
import org.kie.api.builder.Results;
import org.kie.api.builder.KieScanner.Status;
import org.kie.api.event.kiescanner.KieScannerEventListener;
import org.kie.api.event.kiescanner.KieScannerStatusChangeEvent;
import org.kie.api.event.kiescanner.KieScannerUpdateResultsEvent;

public class KieScannerEventSupport extends AbstractEventSupport<KieScannerEventListener> {
    
    public void fireKieScannerStatusChangeEventImpl(final Status status) {
        final Iterator<KieScannerEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            KieScannerStatusChangeEvent event = new KieScannerStatusChangeEventImpl(status);

            do {
                iter.next().onKieScannerStatusChangeEvent(event);
            } while (iter.hasNext());
        }
    }

    public void fireKieScannerUpdateResultsEventImpl(final Results results) {
        final Iterator<KieScannerEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            KieScannerUpdateResultsEvent event = new KieScannerUpdateResultsEventImpl(results);

            do {
                iter.next().onKieScannerUpdateResultsEvent(event);
            } while (iter.hasNext());
        }
    }
    
    
}
