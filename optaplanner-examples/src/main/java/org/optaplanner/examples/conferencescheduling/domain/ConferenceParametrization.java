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
    public static final String TALK_TYPE_OF_ROOM = "Talk type of room";
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
    public static final String TALK_MUTUALLY_EXCLUSIVE_TALKS_TAG = "Talk mutually-exclusive-talks tag";
    public static final String TALK_PREREQUISITE_TALKS = "Talk prerequisite talks";

    public static final String THEME_TRACK_CONFLICT = "Theme track conflict";
    public static final String SECTOR_CONFLICT = "Sector conflict";
    public static final String AUDIENCE_TYPE_DIVERSITY = "Audience type diversity";
    public static final String AUDIENCE_TYPE_THEME_TRACK_CONFLICT = "Audience type theme track conflict";
    public static final String AUDIENCE_LEVEL_DIVERSITY = "Audience level diversity";
    public static final String AUDIENCE_LEVEL_FLOW_PER_CONTENT_VIOLATION = "Audience level flow per content violation";
    public static final String CONTENT_CONFLICT = "Content conflict";
    public static final String LANGUAGE_DIVERSITY = "Language diversity";
    public static final String SPEAKER_PREFERRED_TIMESLOT_TAG = "Speaker preferred timeslot tag";
    public static final String SPEAKER_UNDESIRED_TIMESLOT_TAG = "Speaker undesired timeslot tag";
    public static final String TALK_PREFERRED_TIMESLOT_TAG = "Talk preferred timeslot tag";
    public static final String TALK_UNDESIRED_TIMESLOT_TAG = "Talk undesired timeslot tag";
    public static final String SPEAKER_PREFERRED_ROOM_TAG = "Speaker preferred room tag";
    public static final String SPEAKER_UNDESIRED_ROOM_TAG = "Speaker undesired room tag";
    public static final String TALK_PREFERRED_ROOM_TAG = "Talk preferred room tag";
    public static final String TALK_UNDESIRED_ROOM_TAG = "Talk undesired room tag";
    public static final String SAME_DAY_TALKS = "Same day talks";

    private int talkTypeOfTimeslot = 10000;
    private int talkTypeOfRoom = 10000;
    private int roomUnavailableTimeslot = 10000;
    private int roomConflict = 10;
    private int speakerUnavailableTimeslot = 1;
    private int speakerConflict = 1;
    private int speakerRequiredTimeslotTag = 1;
    private int speakerProhibitedTimeslotTag = 1;
    private int talkRequiredTimeslotTag = 1;
    private int talkProhibitedTimeslotTag = 1;
    private int speakerRequiredRoomTag = 1;
    private int speakerProhibitedRoomTag = 1;
    private int talkRequiredRoomTag = 1;
    private int talkProhibitedRoomTag = 1;
    private int talkMutuallyExclusiveTalksTag = 1;
    private int talkPrerequisiteTalks = 1;

    private int themeTrackConflict = 10;
    private int sectorConflict = 10;
    private int audienceTypeDiversity = 1;
    private int audienceTypeThemeTrackConflict = 0;
    private int audienceLevelDiversity = 1;
    private int audienceLevelFlowPerContentViolation = 10;
    private int contentConflict = 100;
    private int languageDiversity = 10;
    private int speakerPreferredTimeslotTag = 20;
    private int speakerUndesiredTimeslotTag = 20;
    private int talkPreferredTimeslotTag = 20;
    private int talkUndesiredTimeslotTag = 20;
    private int speakerPreferredRoomTag = 20;
    private int speakerUndesiredRoomTag = 20;
    private int talkPreferredRoomTag = 20;
    private int talkUndesiredRoomTag = 20;
    private int sameDayTalks = 10;

    public ConferenceParametrization() {
    }

    public ConferenceParametrization(long id) {
        super(id);
    }

    // ************************************************************************
    // Simple getters and setters
    // ************************************************************************

    public int getTalkTypeOfTimeslot() {
        return talkTypeOfTimeslot;
    }

    public int getTalkTypeOfRoom() {
        return talkTypeOfRoom;
    }

    public void setTalkTypeOfRoom(int talkTypeOfRoom) {
        this.talkTypeOfRoom = talkTypeOfRoom;
    }

    public void setTalkTypeOfTimeslot(int talkTypeOfTimeslot) {
        this.talkTypeOfTimeslot = talkTypeOfTimeslot;
    }

    public int getRoomUnavailableTimeslot() {
        return roomUnavailableTimeslot;
    }

    public void setRoomUnavailableTimeslot(int roomUnavailableTimeslot) {
        this.roomUnavailableTimeslot = roomUnavailableTimeslot;
    }

    public int getRoomConflict() {
        return roomConflict;
    }

    public void setRoomConflict(int roomConflict) {
        this.roomConflict = roomConflict;
    }

    public int getSpeakerUnavailableTimeslot() {
        return speakerUnavailableTimeslot;
    }

    public void setSpeakerUnavailableTimeslot(int speakerUnavailableTimeslot) {
        this.speakerUnavailableTimeslot = speakerUnavailableTimeslot;
    }

    public int getSpeakerConflict() {
        return speakerConflict;
    }

    public void setSpeakerConflict(int speakerConflict) {
        this.speakerConflict = speakerConflict;
    }

    public int getSpeakerRequiredTimeslotTag() {
        return speakerRequiredTimeslotTag;
    }

    public void setSpeakerRequiredTimeslotTag(int speakerRequiredTimeslotTag) {
        this.speakerRequiredTimeslotTag = speakerRequiredTimeslotTag;
    }

    public int getSpeakerProhibitedTimeslotTag() {
        return speakerProhibitedTimeslotTag;
    }

    public void setSpeakerProhibitedTimeslotTag(int speakerProhibitedTimeslotTag) {
        this.speakerProhibitedTimeslotTag = speakerProhibitedTimeslotTag;
    }

    public int getTalkRequiredTimeslotTag() {
        return talkRequiredTimeslotTag;
    }

    public void setTalkRequiredTimeslotTag(int talkRequiredTimeslotTag) {
        this.talkRequiredTimeslotTag = talkRequiredTimeslotTag;
    }

    public int getTalkProhibitedTimeslotTag() {
        return talkProhibitedTimeslotTag;
    }

    public void setTalkProhibitedTimeslotTag(int talkProhibitedTimeslotTag) {
        this.talkProhibitedTimeslotTag = talkProhibitedTimeslotTag;
    }

    public int getSpeakerRequiredRoomTag() {
        return speakerRequiredRoomTag;
    }

    public void setSpeakerRequiredRoomTag(int speakerRequiredRoomTag) {
        this.speakerRequiredRoomTag = speakerRequiredRoomTag;
    }

    public int getSpeakerProhibitedRoomTag() {
        return speakerProhibitedRoomTag;
    }

    public void setSpeakerProhibitedRoomTag(int speakerProhibitedRoomTag) {
        this.speakerProhibitedRoomTag = speakerProhibitedRoomTag;
    }

    public int getTalkRequiredRoomTag() {
        return talkRequiredRoomTag;
    }

    public void setTalkRequiredRoomTag(int talkRequiredRoomTag) {
        this.talkRequiredRoomTag = talkRequiredRoomTag;
    }

    public int getTalkProhibitedRoomTag() {
        return talkProhibitedRoomTag;
    }

    public void setTalkProhibitedRoomTag(int talkProhibitedRoomTag) {
        this.talkProhibitedRoomTag = talkProhibitedRoomTag;
    }

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

    public int getAudienceTypeDiversity() {
        return audienceTypeDiversity;
    }

    public void setAudienceTypeDiversity(int audienceTypeDiversity) {
        this.audienceTypeDiversity = audienceTypeDiversity;
    }

    public int getAudienceTypeThemeTrackConflict() {
        return audienceTypeThemeTrackConflict;
    }

    public void setAudienceTypeThemeTrackConflict(int audienceTypeThemeTrackConflict) {
        this.audienceTypeThemeTrackConflict = audienceTypeThemeTrackConflict;
    }

    public int getAudienceLevelDiversity() {
        return audienceLevelDiversity;
    }

    public void setAudienceLevelDiversity(int audienceLevelDiversity) {
        this.audienceLevelDiversity = audienceLevelDiversity;
    }

    public int getAudienceLevelFlowPerContentViolation() {
        return audienceLevelFlowPerContentViolation;
    }

    public void setAudienceLevelFlowPerContentViolation(int audienceLevelFlowPerContentViolation) {
        this.audienceLevelFlowPerContentViolation = audienceLevelFlowPerContentViolation;
    }

    public int getContentConflict() {
        return contentConflict;
    }

    public void setContentConflict(int contentConflict) {
        this.contentConflict = contentConflict;
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

    public int getTalkMutuallyExclusiveTalksTag() {
        return talkMutuallyExclusiveTalksTag;
    }

    public void setTalkMutuallyExclusiveTalksTag(int talkMutuallyExclusiveTalksTag) {
        this.talkMutuallyExclusiveTalksTag = talkMutuallyExclusiveTalksTag;
    }

    public int getTalkPrerequisiteTalks() {
        return talkPrerequisiteTalks;
    }

    public void setTalkPrerequisiteTalks(int talkPrerequisiteTalks) {
        this.talkPrerequisiteTalks = talkPrerequisiteTalks;
    }

    public int getSameDayTalks() {
        return sameDayTalks;
    }

    public void setSameDayTalks(int sameDayTalks) {
        this.sameDayTalks = sameDayTalks;
    }
}
