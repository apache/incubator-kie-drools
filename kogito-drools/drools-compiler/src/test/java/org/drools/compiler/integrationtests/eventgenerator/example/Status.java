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

package org.drools.compiler.integrationtests.eventgenerator.example;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Status {

    // Status names
    public static final String[] STATUS_NAMES = {"Operational status"};

    public static final int OPERATIONAL = 0;

    ////////////////////////////////////////////////////////////////////

    private int id;
    private String resourceId;
    private List<String> valueList;
    private int currentValue;
    private long hasValueSince;

    /**
     * @param id The status name.
     * @param resourceId The id of the corresponding resource.
     */
    public Status(int id, String resourceId) {
        this.id = id;
        this.resourceId = resourceId;
        this.valueList = new ArrayList<String>();
        this.hasValueSince = 0;
    }

    /**
     * @param id The status name.
     * @param resourceId The id of the corresponding resource.
     * @param currentValue current value of the status.
     */
    public Status(int id, String resourceId, int currentValue) {
        this (id, resourceId);
        this.currentValue = currentValue;
    }

    /**
     * @param id The status name.
     * @param resourceId The id of the corresponding resource.
     * @param valueList The values this status can have.
     * @param currentValue current value of the status.
     */
    public Status(int id, String resourceId, String[] valueList, int currentValue) {
        this (id, resourceId, currentValue);
        this.addValues(valueList);
    }

    /**
     * @return the status id
     */
    public int getId() {
        return this.id;
    }

    /**
     * @param name the status id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the resourceId
     */
    public String getResourceId() {
        return resourceId;
    }

    /**
     * @param resourceId the resourceId to set
     */
    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    /**
     * @return the currentValue
     */
    public int getCurrentValue() {
        return currentValue;
    }

    /**
     * @param currentValue the currentValue to set
     */
    public void setCurrentValue(int currentValue, long sinceWhen) {
        this.currentValue = currentValue;
        this.hasValueSince = sinceWhen;
    }

    /**
     * @param oldValue the oldValue to set
     * @param newValue the newValue to set
     */
    public void switchCurrentValue(int oldValue, int newValue, long sinceWhen) {
        if (this.currentValue == oldValue)
            this.setCurrentValue(newValue, sinceWhen);
    }

    /**
     * @param oldValue the oldValue to set
     * @param newValue the newValue to set
     */
    public void switchCurrentValue(String oldValue, String newValue, long sinceWhen) {
        this.switchCurrentValue (Integer.parseInt(oldValue), Integer.parseInt(newValue), sinceWhen);
    }

    /**
     * @param currentValue the currentValue to set
     *//*
    public void setCurrentValue(int currentValue) {
        setCurrentValue (currentValue, (Calendar)Calendar.getInstance().clone());
    }*/

    /**
     * @return the valueList
     */
    public String[] getValueList() {
        return (String[]) valueList.toArray();
    }

    /**
     * @param valueList the valueList to set
     */
    public void addValues(String[] valueList) {
        this.valueList.addAll(Arrays.asList(valueList));
    }

    /**
     * @return the hasValueSince
     */
    public long getHasValueSince() {
        return hasValueSince;
    }

}
