/**
 * Copyright 2010 JBoss Inc
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

package org.jbpm.task;

import org.jbpm.task.service.IllegalTaskStateException;

public class SubTasksStrategyFactory  {

    public SubTasksStrategyFactory() {

    }
    
    public static SubTasksStrategy newStrategy(String type){
        if(type!= null && !type.equals("")){
            if(type.equals("OnParentAbortAllSubTasksEnd")){
                return new OnParentAbortAllSubTasksEndStrategy("OnParentAbortAllSubTasksEnd");
            }
             if(type.equals("OnAllSubTasksEndParentEnd")){
                return new OnAllSubTasksEndParentEndStrategy("OnAllSubTasksEndParentEnd");
            }
            throw new IllegalTaskStateException("Unknown " + SubTasksStrategy.class.getSimpleName() + " type: " + type);
        }

        return null;
    }
    

   

}
