/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.models.guided.dtable.shared.model;

public class DTColumnConfig52
        implements BaseColumn {

    private static final long serialVersionUID = 510l;

    // Legacy Default Values were String however since 5.4 they are stored in a DTCellValue52 object
    public String defaultValue;

    // For a default value ! Will still be in the array of course, just use this value if its empty
    private DTCellValue52 typedDefaultValue = null;

    // To hide the column (eg if it has a mandatory default)
    private boolean hideColumn = false;

    //Column width
    private int width = -1;

    // The header to be displayed.
    private String header;

    public DTCellValue52 getDefaultValue() {
        return typedDefaultValue;
    }

    public int getWidth() {
        return width;
    }

    public boolean isHideColumn() {
        return hideColumn;
    }

    public void setDefaultValue( DTCellValue52 defaultValue ) {
        this.typedDefaultValue = defaultValue;
    }

    public void setHideColumn( boolean hideColumn ) {
        this.hideColumn = hideColumn;
    }

    public void setWidth( int width ) {
        this.width = width;
    }

    public void setHeader( String header ) {
        this.header = header;
    }

    public String getHeader() {
        return header;
    }

}
