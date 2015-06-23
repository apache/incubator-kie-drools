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

package org.jbpm.query.jpa.builder.impl;

import org.kie.internal.query.data.QueryData;

public abstract class AbstractDeleteBuilderImpl<T> {

    protected QueryData queryData = new QueryData();
   
    public QueryData getQueryData() { 
        return queryData;
    }
    
    protected <P> void addRangeParameter( String listId, String name, P parameter, boolean start) { 
        if( parameter == null ) { 
            throw new IllegalArgumentException("A null " + name + " criteria is invalid." );
        }
        this.queryData.addRangeParameter(listId, parameter, start);
    }

    private <P> void addParameter( String listId, P... parameter ) { 
        this.queryData.addAppropriateParam(listId, parameter);
    }

    protected void addLongParameter( String listId, String name, long [] parameter) { 
        if( parameter == null ) { 
            throw new IllegalArgumentException("A null " + name + " criteria is invalid." );
        }
        Long [] wrapArr = new Long[parameter.length];
        for( int i = 0; i < parameter.length; ++i ) { 
            wrapArr[i] = parameter[i];
        }
        addParameter(listId, wrapArr);
    }
   
    protected void addIntParameter( String listId, String name, int [] parameter) { 
        if( parameter == null ) { 
            throw new IllegalArgumentException("A null " + name + " criteria is invalid." );
        }
        Integer [] wrapArr = new Integer[parameter.length];
        for( int i = 0; i < parameter.length; ++i ) { 
            wrapArr[i] = parameter[i];
        }
        addParameter(listId, wrapArr);
    }
    
    protected <P> void addObjectParameter(String listId, String name, P... parameter) { 
        if( parameter == null ) { 
            throw new IllegalArgumentException("A null " + name + " criteria is invalid." );
        }
        for( int i = 0; i < parameter.length; ++i ) { 
           if( parameter[i] == null ) { 
               throw new IllegalArgumentException("A null " + name + " criteria (argument " + i + ") is invalid." );
           }
        }
        addParameter(listId, parameter);
    }
}
