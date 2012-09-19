/*
 * Copyright 2012 JBoss by Red Hat.
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
package org.jbpm.task.api;

/**
 * The Task Statistics Service provides all 
 *  the methods for gathering Task Instance Statistics.
 *  The Task Statistics methods are provided separately from the 
 *  Task Query Services, because they can include more complex operations than
 *  simple queries, like aggregations, averages, sums, etc. 
 */
public interface TaskStatisticsService {
    public int getCompletedTaskByUserId(String userId);
    public int getPendingTaskByUserId(String userId);
    
}
