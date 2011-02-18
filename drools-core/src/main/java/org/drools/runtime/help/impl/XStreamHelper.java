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

package org.drools.runtime.help.impl;

import org.drools.command.runtime.BatchExecutionCommandImpl;
import org.drools.command.runtime.GetGlobalCommand;
import org.drools.command.runtime.SetGlobalCommand;
import org.drools.command.runtime.process.AbortWorkItemCommand;
import org.drools.command.runtime.process.CompleteWorkItemCommand;
import org.drools.command.runtime.process.SignalEventCommand;
import org.drools.command.runtime.process.StartProcessCommand;
import org.drools.command.runtime.rule.FireAllRulesCommand;
import org.drools.command.runtime.rule.GetObjectCommand;
import org.drools.command.runtime.rule.GetObjectsCommand;
import org.drools.command.runtime.rule.InsertElementsCommand;
import org.drools.command.runtime.rule.InsertObjectCommand;
import org.drools.command.runtime.rule.ModifyCommand;
import org.drools.command.runtime.rule.QueryCommand;
import org.drools.command.runtime.rule.RetractCommand;
import org.drools.command.runtime.rule.ModifyCommand.SetterImpl;
import org.drools.common.DefaultFactHandle;
import org.drools.runtime.impl.ExecutionResultImpl;
import org.drools.runtime.rule.impl.FlatQueryResults;
import org.drools.runtime.rule.impl.NativeQueryResults;

import com.thoughtworks.xstream.XStream;

public class XStreamHelper {
    public static void setAliases(XStream xstream) {
        xstream.alias( "batch-execution",
                       BatchExecutionCommandImpl.class );
        xstream.alias( "insert",
                       InsertObjectCommand.class );
        xstream.alias( "modify",
                       ModifyCommand.class );
        xstream.alias( "setters",
                       SetterImpl.class );
        xstream.alias( "retract",
                       RetractCommand.class );
        xstream.alias( "insert-elements",
                       InsertElementsCommand.class );
        xstream.alias( "start-process",
                       StartProcessCommand.class );
        xstream.alias( "signal-event",
                       SignalEventCommand.class );
        xstream.alias( "complete-work-item",
                       CompleteWorkItemCommand.class );
        xstream.alias( "abort-work-item",
                       AbortWorkItemCommand.class );
        xstream.alias( "set-global",
                       SetGlobalCommand.class );
        xstream.alias( "get-global",
                       GetGlobalCommand.class );
        xstream.alias( "get-object",
                       GetObjectCommand.class );
        xstream.alias( "get-objects",
                       GetObjectsCommand.class );
        xstream.alias( "execution-results",
                       ExecutionResultImpl.class );
        xstream.alias( "fire-all-rules",
                       FireAllRulesCommand.class );
        xstream.alias( "query",
                       QueryCommand.class );
        xstream.alias( "query-results",
                       FlatQueryResults.class );
        xstream.alias( "fact-handle",
                       DefaultFactHandle.class );
    }
}
