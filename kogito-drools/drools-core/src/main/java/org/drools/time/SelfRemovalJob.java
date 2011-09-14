package org.drools.time;

public class SelfRemovalJob implements Job {
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
