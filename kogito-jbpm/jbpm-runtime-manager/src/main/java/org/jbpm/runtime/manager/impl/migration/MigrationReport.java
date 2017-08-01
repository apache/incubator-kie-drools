/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.runtime.manager.impl.migration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents complete (might be unfinished in case of an error) 
 * process instance migration. It contains all migration entries 
 * that correspond to individual operations performed during migration.
 *
 */
public class MigrationReport implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(MigrationReport.class);
    private static final long serialVersionUID = -5992169359542031146L;

    private MigrationSpec migrationSpec;
    
    private boolean successful;
    
    private Date startDate;
    private Date endDate;
    
    private List<MigrationEntry> entries = new ArrayList<MigrationEntry>();

    public MigrationReport(MigrationSpec migrationSpec) {
        this.migrationSpec = migrationSpec;
        this.startDate = new Date();
    }
    
    public MigrationSpec getMigrationSpec() {
        return migrationSpec;
    }
    
    public void setMigrationSpec(MigrationSpec processData) {
        this.migrationSpec = processData;
    }
    
    public boolean isSuccessful() {
        return successful;
    }
    
    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }
    
    public Date getStartDate() {
        return startDate;
    }
    
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    
    public List<MigrationEntry> getEntries() {
        return entries;
    }
    
    public void setEntries(List<MigrationEntry> entries) {
        this.entries = entries;
    }
    
    public void addEntry(MigrationEntry.Type type, String message) throws MigrationException {
        this.entries.add(new MigrationEntry(type, message));
        switch (type) {
            case INFO:
                logger.debug(message);
                break;
            case WARN:
                logger.warn(message);
                break;
            case ERROR:
                logger.error(message);
                this.setSuccessful(false);
                setEndDate(new Date());
                throw new MigrationException(message, this);
            default:
                break;
        }
    }
}
