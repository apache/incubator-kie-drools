/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.time;

import java.io.Serializable;

public class SelfRemovalJob implements Job, Serializable {

    private static final long serialVersionUID = 8876468420174364422L;
    private Job job;
    
    public SelfRemovalJob(Job job) {
        this.job = job;
    }

    public Job getJob() {
        return job;
    }

    public void execute(JobContext ctx) {
        job.execute(  ((SelfRemovalJobContext)ctx).getJobContext() );
        ((SelfRemovalJobContext)ctx).remove(); 
        
    }

}
