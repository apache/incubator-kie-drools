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
