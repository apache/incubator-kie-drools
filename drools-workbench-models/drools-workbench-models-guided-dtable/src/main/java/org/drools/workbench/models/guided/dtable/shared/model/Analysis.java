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

    private List<String> list = new ArrayList<String>();

    public void addRowMessage( String htmlEntry ) {
        list.add( htmlEntry );
    }

    public String toHtmlString() {
        StringBuilder htmlBuilder = new StringBuilder( "<span>" );
        boolean first = true;
        for ( String htmlEntry : list ) {
            if ( first ) {
                first = false;
            } else {
                htmlBuilder.append( "<br/> " );
            }
            htmlBuilder.append( htmlEntry );
        }
        htmlBuilder.append( "</span>" );
        return htmlBuilder.toString();
    }

    public String firstRowToHtmlString() {
        if ( list.size() > 1 ) {
            return "<span>" + list.get( 0 ) + "...</span>";
        } else if ( list.size() == 1 ) {
            return "<span>" + list.get( 0 ) + "</span>";
        } else {
            return "<span></span>";
        }
    }

    public int getWarningsSize() {
        return getRowMessagesSize();
    }

    public int getRowMessagesSize() {
        return list.size();
    }

    public int compareTo( Analysis other ) {
        return Integer.valueOf( getWarningsSize() ).compareTo( Integer.valueOf( other.getWarningsSize() ) );
    }

}
