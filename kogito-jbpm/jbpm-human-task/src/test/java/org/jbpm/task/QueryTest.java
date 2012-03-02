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

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.task.query.DeadlineSummary;
import org.jbpm.task.service.MockEscalatedDeadlineHandler;
import org.jbpm.task.service.persistence.TaskPersistenceManager;

public class QueryTest extends BaseTest {

    public void testUnescalatedDeadlines() throws Exception {
        MockEscalatedDeadlineHandler handler = new MockEscalatedDeadlineHandler();
        taskService.setEscalatedDeadlineHandler( handler );       
        Map vars = new HashMap();
        vars.put( "users",
                  users );
        vars.put( "groups",
                  groups );


        //Reader reader;
        Reader reader = new InputStreamReader( getClass().getResourceAsStream( "QueryData_UnescalatedDeadlines.mvel" ) );
        List<Task> tasks = (List<Task>) eval( reader,
                                              vars );
        for ( Task task : tasks ) {
            taskSession.addTask( task, null );
        }
        long now = ((Date)vars.get( "now" )).getTime();
        
        // should be three, one is marked as escalated
        TaskPersistenceManager tpm = new TaskPersistenceManager(emf);
        List<DeadlineSummary> list = tpm.getUnescalatedDeadlines();
        
        assertEquals( 3,
                      list.size() );

        DeadlineSummary result = list.get( 0 );
        assertEquals( now + 20000,
                      result.getDate().getTime() );

        result = list.get( 1 );
        assertEquals( now + 22000 ,
                      result.getDate().getTime() );

        result = list.get( 2 );
        assertEquals( now + 24000,
                      result.getDate().getTime());    
    }

}
