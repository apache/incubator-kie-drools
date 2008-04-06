package org.drools.workflow.core.node;

import org.drools.process.core.timer.Timer;

public class TimerNode extends SequenceNode {

    private static final long serialVersionUID = 400L;
    
    private Timer timer;
    
    public void setTimer(Timer timer) {
        this.timer = timer;
    }
    
    public Timer getTimer() {
        return this.timer;
    }

}
