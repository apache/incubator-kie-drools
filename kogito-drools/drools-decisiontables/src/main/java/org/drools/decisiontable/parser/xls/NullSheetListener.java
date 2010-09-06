/*
 * Copyright 2005 JBoss Inc
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

package org.drools.decisiontable.parser.xls;

import org.drools.template.parser.DataListener;

/**
 * @author <a href="mailto:shaun.addison@gmail.com"> Shaun Addison </a>
 * 
 * Null listner.
 */
public class NullSheetListener
    implements
    DataListener {

    public void startSheet(final String name) {
    }

    public void finishSheet() {
    }

    public void newRow(final int rowNumber,
                       final int columns) {
    }

    public void newCell(final int row,
                        final int column,
                        final String value,
                        final int mergedColstart) {
    }

}
