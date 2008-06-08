package org.drools.time;

import java.io.Externalizable;

public interface Job {
	public void execute(JobContext ctx);
}
