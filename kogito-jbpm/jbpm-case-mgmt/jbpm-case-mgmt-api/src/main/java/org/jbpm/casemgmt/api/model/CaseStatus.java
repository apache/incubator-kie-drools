/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.casemgmt.api.model;

import java.util.List;
import java.util.stream.Collectors;

public enum CaseStatus {
    OPEN(1, "open"),
    CLOSED(2, "closed"),
    CANCELLED(3, "cancelled");

    private final int id;
    private final String name;

    CaseStatus( int id, String name ) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }

    public String getName() {
        return name;
    }

    public static CaseStatus fromId( int id ) {
        switch ( id ) {
            case 1 : return OPEN;
            case 2 : return CLOSED;
            case 3 : return CANCELLED;
            default: return null;
        }
    }

    public static CaseStatus fromName( String name ) {
        if ("open".equalsIgnoreCase( name )) {
            return OPEN;
        } else if ("closed".equalsIgnoreCase( name )) {
            return CLOSED;
        } else if ("cancelled".equalsIgnoreCase( name )) {
            return CANCELLED;
        } else {
            return valueOf(name);
        }
    }

    public static List<CaseStatus> fromIdList( List<Integer> idList ) {
        return idList != null ? idList.stream().map(event -> fromId( event )).collect(Collectors.toList()) : null;
    }

    public static List<CaseStatus> fromNameList( List<String> nameList ) {
        return nameList != null ? nameList.stream().map(event -> fromName( event )).collect(Collectors.toList()) : null;
    }
}

