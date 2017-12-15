/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.conferencescheduling.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;

public class ConferenceParametrization extends AbstractPersistable {

    private int themeConflict = 10;
    private int sectorConflict = 10;
    private int languageDiversity = 10;
    private int speakerPreferredTimeslotTag = 10;
    private int talkPreferredTimeslotTag = 10;
    private int speakerPreferredRoomTag = 10;
    private int talkPreferredRoomTag = 10;

    // ************************************************************************
    // Simple getters and setters
    // ************************************************************************

    public int getThemeConflict() {
        return themeConflict;
    }

    public void setThemeConflict(int themeConflict) {
        this.themeConflict = themeConflict;
    }

    public int getSectorConflict() {
        return sectorConflict;
    }

    public void setSectorConflict(int sectorConflict) {
        this.sectorConflict = sectorConflict;
    }

    public int getLanguageDiversity() {
        return languageDiversity;
    }

    public void setLanguageDiversity(int languageDiversity) {
        this.languageDiversity = languageDiversity;
    }

    public int getSpeakerPreferredTimeslotTag() {
        return speakerPreferredTimeslotTag;
    }

    public void setSpeakerPreferredTimeslotTag(int speakerPreferredTimeslotTag) {
        this.speakerPreferredTimeslotTag = speakerPreferredTimeslotTag;
    }

    public int getTalkPreferredTimeslotTag() {
        return talkPreferredTimeslotTag;
    }

    public void setTalkPreferredTimeslotTag(int talkPreferredTimeslotTag) {
        this.talkPreferredTimeslotTag = talkPreferredTimeslotTag;
    }

    public int getSpeakerPreferredRoomTag() {
        return speakerPreferredRoomTag;
    }

    public void setSpeakerPreferredRoomTag(int speakerPreferredRoomTag) {
        this.speakerPreferredRoomTag = speakerPreferredRoomTag;
    }

    public int getTalkPreferredRoomTag() {
        return talkPreferredRoomTag;
    }

    public void setTalkPreferredRoomTag(int talkPreferredRoomTag) {
        this.talkPreferredRoomTag = talkPreferredRoomTag;
    }

}
