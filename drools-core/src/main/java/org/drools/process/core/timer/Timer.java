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

package org.drools.process.core.timer;

import java.io.Serializable;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class Timer implements Serializable {

    private long id;
    private String delay;
    private String period;
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public String getDelay() {
        return delay;
    }
    
    public void setDelay(String delay) {
        this.delay = delay;
    }
    
    public String getPeriod() {
        return period;
    }
    
    public void setPeriod(String period) {
        this.period = period;
    }
    
    public String toString() {
    	String result =  "Timer";
    	if (delay != null || period != null) {
    		result += "[";
    		if (delay != null) {
    			result += "delay=" + delay;
    			if (period != null) {
    				result += ", ";
    			}
    		}
    		if (period != null) {
    			result += "period=" + period;
    		}
    		result += "]";
    	}
    	return result;
    }

}
