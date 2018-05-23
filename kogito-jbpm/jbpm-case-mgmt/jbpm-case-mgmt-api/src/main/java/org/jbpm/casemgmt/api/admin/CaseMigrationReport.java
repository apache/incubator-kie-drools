/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.casemgmt.api.admin;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jbpm.services.api.admin.MigrationReport;

public class CaseMigrationReport {
    
    private boolean successful;
    
    private Date startDate;
    private Date endDate;
    private List<MigrationReport> reports = new ArrayList<>();
    
    
    public CaseMigrationReport() {
        this.successful = false;
        this.startDate = new Date();        
    }
    
    /**
     * Indicates if the migration was successful or not
     */
    public boolean isSuccessful() {
        return successful;
    }

    /**
     * Timestamp representing start time of the migration
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Timestamp representing end time of the migration
     */
    public Date getEndDate() {
        return endDate;
    }
    
    /**
     * Returns all individual reports that corresponds to single process instance 
     * migrated as part of case instance migration 
     */
    public List<MigrationReport> getReports() {
        return reports;
    }
    
    /**
     * Adds individual report to the list of reports
     * @param report - single process instance migration report
     */
    public void addReport(MigrationReport report) {
        this.reports.add(report);
    }
    
    /**
     * Completes the migration and calculates the status.
     */
    public void complete() {
        this.endDate = new Date();
        
        if (reports.stream().allMatch(report -> report.isSuccessful() == true)) {
            this.successful = true;
        }
    }

    @Override
    public String toString() {
        return "CaseMigrationReport [successful=" + successful + ", startDate=" + startDate + ", endDate=" + endDate + "]";
    }
}
