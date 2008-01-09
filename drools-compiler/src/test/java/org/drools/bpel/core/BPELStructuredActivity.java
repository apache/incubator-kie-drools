package org.drools.bpel.core;

import java.util.List;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public interface BPELStructuredActivity extends BPELActivity {
    
    void setActivities(List<BPELActivity> activities);

}
