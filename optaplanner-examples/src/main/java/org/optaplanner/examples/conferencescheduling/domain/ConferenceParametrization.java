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

    public static final String TALK_TYPE_OF_TIMESLOT = "Talk type of timeslot";
    public static final String ROOM_UNAVAILABLE_TIMESLOT = "Room unavailable timeslot";
    public static final String ROOM_CONFLICT = "Room conflict";
    public static final String SPEAKER_UNAVAILABLE_TIMESLOT = "Speaker unavailable timeslot";
    public static final String SPEAKER_CONFLICT = "Speaker conflict";
    public static final String SPEAKER_REQUIRED_TIMESLOT_TAG = "Speaker required timeslot tag";
    public static final String SPEAKER_PROHIBITED_TIMESLOT_TAG = "Speaker prohibited timeslot tag";
    public static final String TALK_REQUIRED_TIMESLOT_TAG = "Talk required timeslot tag";
    public static final String TALK_PROHIBITED_TIMESLOT_TAG = "Talk prohibited timeslot tag";
    public static final String SPEAKER_REQUIRED_ROOM_TAG = "Speaker required room tag";
    public static final String SPEAKER_PROHIBITED_ROOM_TAG = "Speaker prohibited room tag";
    public static final String TALK_REQUIRED_ROOM_TAG = "Talk required room tag";
    public static final String TALK_PROHIBITED_ROOM_TAG = "Talk prohibited room tag";

    public static final String THEME_TRACK_CONFLICT = "Theme track conflict";
    public static final String SECTOR_CONFLICT = "Sector conflict";
    public static final String CONTENT_AUDIENCE_LEVEL_FLOW_VIOLATION = "Content audience level flow violation";
    public static final String AUDIENCE_LEVEL_DIVERSITY = "Audience level diversity";
    public static final String LANGUAGE_DIVERSITY = "Language diversity";
    public static final String SPEAKER_PREFERRED_TIMESLOT_TAG = "Speaker preferred timeslot tag";
    public static final String SPEAKER_UNDESIRED_TIMESLOT_TAG = "Speaker undesired timeslot tag";
    public static final String TALK_PREFERRED_TIMESLOT_TAG = "Talk preferred timeslot tag";
    public static final String TALK_UNDESIRED_TIMESLOT_TAG = "Talk undesired timeslot tag";
    public static final String SPEAKER_PREFERRED_ROOM_TAG = "Speaker preferred room tag";
    public static final String SPEAKER_UNDESIRED_ROOM_TAG = "Speaker undesired room tag";
    public static final String TALK_PREFERRED_ROOM_TAG = "Talk preferred room tag";
    public static final String TALK_UNDESIRED_ROOM_TAG = "Talk undesired room tag";

    private int themeTrackConflict = 10;
    private int sectorConflict = 10;
    private int contentAudienceLevelFlowViolation = 10;
    private int audienceLevelDiversity = 0;
    private int languageDiversity = 10;
    private int speakerPreferredTimeslotTag = 10;
    private int speakerUndesiredTimeslotTag = 10;
    private int talkPreferredTimeslotTag = 10;
    private int talkUndesiredTimeslotTag = 10;
    private int speakerPreferredRoomTag = 10;
    private int speakerUndesiredRoomTag = 10;
    private int talkPreferredRoomTag = 10;
    private int talkUndesiredRoomTag = 10;

    public ConferenceParametrization() {
    }

    public ConferenceParametrization(long id) {
        super(id);
    }

    // ************************************************************************
    // Simple getters and setters
    // ************************************************************************

    public int getThemeTrackConflict() {
        return themeTrackConflict;
    }

    public void setThemeTrackConflict(int themeTrackConflict) {
        this.themeTrackConflict = themeTrackConflict;
    }

    public int getSectorConflict() {
        return sectorConflict;
    }

    public void setSectorConflict(int sectorConflict) {
        this.sectorConflict = sectorConflict;
    }

    public int getContentAudienceLevelFlowViolation() {
        return contentAudienceLevelFlowViolation;
    }

    public void setContentAudienceLevelFlowViolation(int contentAudienceLevelFlowViolation) {
        this.contentAudienceLevelFlowViolation = contentAudienceLevelFlowViolation;
    }

    public int getAudienceLevelDiversity() {
        return audienceLevelDiversity;
    }

    public void setAudienceLevelDiversity(int audienceLevelDiversity) {
        this.audienceLevelDiversity = audienceLevelDiversity;
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

    public int getSpeakerUndesiredTimeslotTag() {
        return speakerUndesiredTimeslotTag;
    }

    public void setSpeakerUndesiredTimeslotTag(int speakerUndesiredTimeslotTag) {
        this.speakerUndesiredTimeslotTag = speakerUndesiredTimeslotTag;
    }

    public int getTalkPreferredTimeslotTag() {
        return talkPreferredTimeslotTag;
    }

    public void setTalkPreferredTimeslotTag(int talkPreferredTimeslotTag) {
        this.talkPreferredTimeslotTag = talkPreferredTimeslotTag;
    }

    public int getTalkUndesiredTimeslotTag() {
        return talkUndesiredTimeslotTag;
    }

    public void setTalkUndesiredTimeslotTag(int talkUndesiredTimeslotTag) {
        this.talkUndesiredTimeslotTag = talkUndesiredTimeslotTag;
    }

    public int getSpeakerPreferredRoomTag() {
        return speakerPreferredRoomTag;
    }

    public void setSpeakerPreferredRoomTag(int speakerPreferredRoomTag) {
        this.speakerPreferredRoomTag = speakerPreferredRoomTag;
    }

    public int getSpeakerUndesiredRoomTag() {
        return speakerUndesiredRoomTag;
    }

    public void setSpeakerUndesiredRoomTag(int speakerUndesiredRoomTag) {
        this.speakerUndesiredRoomTag = speakerUndesiredRoomTag;
    }

    public int getTalkPreferredRoomTag() {
        return talkPreferredRoomTag;
    }

    public void setTalkPreferredRoomTag(int talkPreferredRoomTag) {
        this.talkPreferredRoomTag = talkPreferredRoomTag;
    }

    public int getTalkUndesiredRoomTag() {
        return talkUndesiredRoomTag;
    }

    public void setTalkUndesiredRoomTag(int talkUndesiredRoomTag) {
        this.talkUndesiredRoomTag = talkUndesiredRoomTag;
    }
}
