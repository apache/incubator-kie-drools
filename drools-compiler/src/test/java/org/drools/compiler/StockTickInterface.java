/*
 * Copyright 2015 JBoss Inc
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

package org.drools.compiler;

import java.util.Date;

public interface StockTickInterface {

    public abstract String getCompany();

    public abstract void setCompany(String company);

    public abstract double getPrice();

    public abstract void setPrice(double price);

    public abstract long getSeq();

    public abstract void setSeq(long seq);

    public abstract long getTime();

    public abstract void setTime(long time);

    /**
     * @return the duration
     */
    public abstract long getDuration();

    /**
     * @param duration the duration to set
     */
    public abstract void setDuration(long duration);

    public abstract Date getDateTimestamp();

}
