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

package org.drools.examples.templates;
/*
 * Copyright 2005 JBoss Inc
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
public class FeeScheduleRule {
    private ActivityType activityType;
    private ProductType productType;
    private FeeScheduleType type;
    private FeeType feeType;
    private String owningParty;
    private String entityBranch;
    private String comparator;
    private long compareAmount;
    private long amount;
    private String currency;
    private boolean logEvent;
    private long feeEventId;
    public FeeScheduleRule(long feeEventId,
                           ActivityType activityType,
                           ProductType productType,
                           FeeScheduleType type,
                           FeeType feeType,
                           String owningParty,
                           String entityBranch,
                           String comparator,
                           long compareAmount,
                           long amount,
                           String currency, 
                           boolean logEvent) {
        this.feeEventId = feeEventId;
        this.activityType = activityType;
        this.productType = productType;
        this.type = type;
        this.feeType = feeType;
        this.owningParty = owningParty;
        this.entityBranch = entityBranch;
        this.comparator = comparator;
        this.compareAmount = compareAmount;
        this.amount = amount;
        this.currency = currency;
        this.logEvent = logEvent;
    }
    public ActivityType getActivityType() {
        return activityType;
    }
    public ProductType getProductType() {
        return productType;
    }
    public FeeScheduleType getType() {
        return type;
    }
    public FeeType getFeeType() {
        return feeType;
    }
    public String getOwningParty() {
        return owningParty;
    }
    public String getEntityBranch() {
        return entityBranch;
    }
    public String getComparator() {
        return comparator;
    }
    public long getAmount() {
        return amount;
    }
    public String getCurrency() {
        return currency;
    }
    public long getCompareAmount() {
        return compareAmount;
    }
    public boolean isLogEvent() {
        return logEvent;
    }
    public long getFeeEventId() {
        return feeEventId;
    }
    
     
}
