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

public class SlidingWindow {

    // Sliding window states names
    public static final String[] SW_STATES = {"Default", "Collecting", "Evaluating", "Advancing"};

    public static final int DEFAULT = 0;
    public static final int COLLECT = 1;
    public static final int EVALUATE = 2;
    public static final int ADVANCE = 3;

    private static int idCounter = 0;

    private int id, state;
    private boolean readyToEvaluate;
    private String parentId;
    private long windowStart, windowEnd, lastUpdate;
    private long windowLength, windowShift;
    //private List<Event> eventsInWindow;
    int numberEvents;
    /*private int defectiveFridgesCount, defectiveFreezersCount;
    private int workingFridgesCount, workingFreezersCount;*/

    public SlidingWindow(long wStart, String parentId, long windowLength, long windowShift) {
        this.id = idCounter++;
        this.readyToEvaluate = false;
        this.state = DEFAULT;
        this.parentId = parentId;
        this.windowStart = wStart;
        this.windowEnd = this.windowStart + windowLength;
        this.lastUpdate = wStart;
        this.windowLength = windowLength;
        this.windowShift = windowShift;
        //this.eventsInWindow = new ArrayList<Event>();
        this.numberEvents = 0;
        //System.out.println ("Sliding window "+this.id+" expires at "+Tools.formattedDate(this.windowEnd));
    }

    /**
     * @return the id
     */
    public String getId() {
        return "sw"+id;
    }

    public long getWindowStart() {
        return windowStart;
    }

    public void setWindowStart(long start) {
        this.windowStart = start;
    }

    public long getWindowEnd() {
        return windowEnd;
    }

    public void setWindowEnd(long end) {
        this.windowEnd = end;
    }

    /**
     * @return the windowLength
     */
    public long getWindowLength() {
        return windowLength;
    }

    /**
     * @param windowLength the windowLength to set
     */
    public void setWindowLength(int windowLength) {
        this.windowLength = windowLength;
    }

    /**
     * @return the shift
     */
    public long getWindowShift() {
        return windowShift;
    }

    /**
     * @param shift the shift to set
     */
    public void setShift(int windowShift) {
        this.windowShift = windowShift;
    }

    /**
     * @return the numberEvents
     */
    public int getNumberEvents() {
        return numberEvents;
    }

    /**
     * @param numberEvents the numberEvents to set
     */
    public void setNumberEvents(int numberEvents) {
        this.numberEvents = numberEvents;
    }

/*    *//**
     * @return the eventsInWindow
     *//*
    public int getNumberEventsInWindow() {
        return this.eventsInWindow.size();
    }

    *//**
     * @return the eventsInWindow
     *//*
    public List<Event> getEventsInWindow() {
        return eventsInWindow;
    }

    *//**
     * @param eventsInWindow the eventsInWindow to set
     *//*
    public void setEventsInWindow(List<Event> eventsInWindow) {
        this.eventsInWindow = eventsInWindow;
    }

    *//**
     * @param additionalEventsInWindow the additional events in window to add
     *//*
    public void addEventsInWindow(List<Event> additionalEventsInWindow) {
        this.eventsInWindow.addAll(additionalEventsInWindow);
    }

    *//**
     * @param expiredEventsInWindow the events in window to remove
     *//*
    public void removeEventsInWindow(List<Event> expiredEventsInWindow) {
        this.eventsInWindow.removeAll(expiredEventsInWindow);
    }*/

    /**
     * @return the parentId
     */
    public String getParentId() {
        return parentId;
    }

    /**
     * @param parentId the parentId to set
     */
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    /**
     * Advances the sliding window
     */
    public void advance() {
        this.windowStart += this.windowShift;
        this.windowEnd += this.windowShift;
        //System.out.println ("Sliding window "+this.getId()+" is reset to "+Tools.formattedInterval(this.windowStart, this.windowEnd));
    }

    /**
     * @return the lastUpdate
     */
    public long getLastUpdate() {
        return lastUpdate;
    }

    /**
     * @param lastUpdate the lastUpdate to set
     */
    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    /**
     * @return the state
     */
    public int getState() {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(int state) {
        this.state = state;
    }

    /**
     * @return the readyForAction
     */
    public boolean isReadyToEvaluate() {
        return readyToEvaluate;
    }

    /**
     * @param readyForAction the readyForAction to set
     */
    public void setReadyToEvaluate(boolean readyToEvaluate) {
        this.readyToEvaluate = readyToEvaluate;
    }

    /**
     * @return the workingFreezersCount
     *//*
    public int getWorkingFreezersCount() {
        return workingFreezersCount;
    }

    *//**
     * @param workingFreezersCount the workingFreezersCount to set
     *//*
    public void setWorkingFreezersCount(int workingFreezersCount) {
        this.workingFreezersCount = workingFreezersCount;
    }

    *//**
     * @return the workingFridgesCount
     *//*
    public int getWorkingFridgesCount() {
        return workingFridgesCount;
    }

    *//**
     * @param workingFridgesCount the workingFridgesCount to set
     *//*
    public void setWorkingFridgesCount(int workingFridgesCount) {
        this.workingFridgesCount = workingFridgesCount;
    }

    public int getDefectiveFridgesCount() {
        return defectiveFridgesCount;
    }

    public void setDefectiveFridgesCount(int defectiveFridgesCount) {
        this.defectiveFridgesCount = defectiveFridgesCount;
    }

    public int getDefectiveFreezersCount() {
        return defectiveFreezersCount;
    }

    public void setDefectiveFreezersCount(int defectiveFreezersCount) {
        this.defectiveFreezersCount = defectiveFreezersCount;
    }

    public int getWorkingDevicesCount() {
        return workingFridgesCount+workingFreezersCount;
    }

    public int getDefectiveDevicesCount() {
        return defectiveFridgesCount+defectiveFreezersCount;
    }

    public int getOverallFridgesCount() {
        return getWorkingFridgesCount()+getDefectiveFridgesCount();
    }

    public int getOverallFreezersCount() {
        return getWorkingFreezersCount()+getDefectiveFreezersCount();
    }

    public int getOverallDevicesCount() {
        return getWorkingDevicesCount()+getDefectiveDevicesCount();
    }

    public double getWorkingFridgesRatio() {
        if (getOverallFridgesCount() == 0)
            return 0;
        return (double)getWorkingFridgesCount()/getOverallFridgesCount();
    }

    public double getDefectiveFridgesRatio() {
        if (getOverallFridgesCount() == 0)
            return 0;
        return (double)getDefectiveFridgesCount()/getOverallFridgesCount();
    }

    public double getWorkingFreezersRatio() {
        if (getOverallFreezersCount() == 0)
            return 0;
        return (double)getWorkingFreezersCount()/getOverallFreezersCount();
    }

    public double getDefectiveFreezersRatio() {
        if (getOverallFreezersCount() == 0)
            return 0;
        return (double)getDefectiveFreezersCount()/getOverallFreezersCount();
    }

    public double getWorkingDevicesRatio() {
        if (getOverallDevicesCount() == 0)
            return 0;
        return (double)getWorkingDevicesCount()/getOverallDevicesCount();
    }

    public double getDefectiveDevicesRatio() {
        if (getOverallDevicesCount() == 0)
            return 0;
        return (double)getDefectiveDevicesCount()/getOverallDevicesCount();
    }*/

}
