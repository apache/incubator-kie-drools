/*
 * Copyright 2011 JBoss Inc
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

package org.drools.workbench.models.guided.dtable.shared.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Only used on the client side, not stored on the server side.
 */
public class Analysis implements Comparable<Analysis> {

    private List<String> impossibleMatchHtmlList = new ArrayList<String>();
    private List<String> multipleValuesForOneActionHtmlList = new ArrayList<String>();
    private List<String> conflictingMatchHtmlList = new ArrayList<String>();
    private List<String> duplicatedMatchHtmlList = new ArrayList<String>();

    public void addImpossibleMatch( String htmlEntry ) {
        impossibleMatchHtmlList.add( htmlEntry );
    }

    public void addMultipleValuesForOneAction( String htmlEntry ) {
        multipleValuesForOneActionHtmlList.add( htmlEntry );
    }

    public void addConflictingMatch( String htmlEntry ) {
        conflictingMatchHtmlList.add( htmlEntry );
    }

    public void addDuplicatedMatch( String htmlEntry ) {
        duplicatedMatchHtmlList.add( htmlEntry );
    }

    public String toHtmlString() {
        StringBuilder htmlBuilder = new StringBuilder( "<span>" );
        boolean first = true;
        for ( String htmlEntry : impossibleMatchHtmlList ) {
            if ( !first ) {
                htmlBuilder.append( ", " );
                first = false;
            }
            htmlBuilder.append( htmlEntry );
        }
        for ( String htmlEntry : multipleValuesForOneActionHtmlList ) {
            if ( !first ) {
                htmlBuilder.append( ", " );
                first = false;
            }
            htmlBuilder.append( htmlEntry );
        }
        for ( String htmlEntry : conflictingMatchHtmlList ) {
            if ( !first ) {
                htmlBuilder.append( ", " );
                first = false;
            }
            htmlBuilder.append( htmlEntry );
        }
        for ( String htmlEntry : duplicatedMatchHtmlList ) {
            if ( !first ) {
                htmlBuilder.append( ", " );
                first = false;
            }
            htmlBuilder.append( htmlEntry );
        }
        htmlBuilder.append( "</span>" );
        return htmlBuilder.toString();
    }

    public int getWarningsSize() {
        return getImpossibleMatchesSize() + getMultipleValuesForOneActionSize()
                + getConflictingMatchSize() + getDuplicatedMatchSize();
    }

    public int getImpossibleMatchesSize() {
        return impossibleMatchHtmlList.size();
    }

    public int getMultipleValuesForOneActionSize() {
        return multipleValuesForOneActionHtmlList.size();
    }

    public int getConflictingMatchSize() {
        return conflictingMatchHtmlList.size();
    }

    public int getDuplicatedMatchSize() {
        return duplicatedMatchHtmlList.size();
    }

    public int compareTo( Analysis other ) {
        return Integer.valueOf( getWarningsSize() ).compareTo( Integer.valueOf( other.getWarningsSize() ) );
    }

}
