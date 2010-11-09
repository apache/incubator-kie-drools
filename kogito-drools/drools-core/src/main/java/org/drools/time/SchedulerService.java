/*
* Copyright 2010 salaboy.
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
 * under the License.
 */
package org.drools.time;

/**
 *
 * @author salaboy
 */
public interface SchedulerService {

    /**
     * Schedule a job for later execution
     * 
     * @param job
     * @param ctx
     * @param trigger
     * 
     * @return
     */
    public JobHandle scheduleJob(Job job, JobContext ctx, Trigger trigger);

    /**
     * Remove the job identified by the given job handle from the 
     * scheduled queue
     * 
     * @param jobHandle the job identity handle
     * 
     * @return
     */
    public boolean removeJob(JobHandle jobHandle);
}
