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
    public static final String SPEAKER_REQUIRED_TIMESLOT_TAGS = "Speaker required timeslot tags";
    public static final String SPEAKER_PROHIBITED_TIMESLOT_TAGs = "Speaker prohibited timeslot tags";
    public static final String TALK_REQUIRED_TIMESLOT_TAGS = "Talk required timeslot tags";
    public static final String TALK_PROHIBITED_TIMESLOT_TAGS = "Talk prohibited timeslot tags";
    public static final String SPEAKER_REQUIRED_ROOM_TAGS = "Speaker required room tags";
    public static final String SPEAKER_PROHIBITED_ROOM_TAGS = "Speaker prohibited room tags";
    public static final String TALK_REQUIRED_ROOM_TAGS = "Talk required room tags";
    public static final String TALK_PROHIBITED_ROOM_TAGS = "Talk prohibited room tags";
    public static final String TALK_MUTUALLY_EXCLUSIVE_TALKS_TAGS = "Talk mutually-exclusive-talks tags";
    public static final String TALK_PREREQUISITE_TALKS = "Talk prerequisite talks";
    public static final String CONSECUTIVE_TALKS_PAUSE = "Consecutive talks pause";

    public static final String THEME_TRACK_CONFLICT = "Theme track conflict";
    public static final String SECTOR_CONFLICT = "Sector conflict";
    public static final String AUDIENCE_TYPE_DIVERSITY = "Audience type diversity";
    public static final String AUDIENCE_TYPE_THEME_TRACK_CONFLICT = "Audience type theme track conflict";
    public static final String AUDIENCE_LEVEL_DIVERSITY = "Audience level diversity";
    public static final String AUDIENCE_LEVEL_FLOW_PER_CONTENT_VIOLATION = "Audience level flow per content violation";
    public static final String CONTENT_CONFLICT = "Content conflict";
    public static final String LANGUAGE_DIVERSITY = "Language diversity";
    public static final String SPEAKER_PREFERRED_TIMESLOT_TAGS = "Speaker preferred timeslot tags";
    public static final String SPEAKER_UNDESIRED_TIMESLOT_TAGS = "Speaker undesired timeslot tags";
    public static final String TALK_PREFERRED_TIMESLOT_TAGS = "Talk preferred timeslot tags";
    public static final String TALK_UNDESIRED_TIMESLOT_TAGS = "Talk undesired timeslot tags";
    public static final String SPEAKER_PREFERRED_ROOM_TAGS = "Speaker preferred room tags";
    public static final String SPEAKER_UNDESIRED_ROOM_TAGS = "Speaker undesired room tags";
    public static final String TALK_PREFERRED_ROOM_TAGS = "Talk preferred room tags";
    public static final String TALK_UNDESIRED_ROOM_TAGS = "Talk undesired room tags";
    public static final String SAME_DAY_TALKS = "Same day talks";
    public static final String POPULAR_TALKS = "Popular talks";
    public static final String CROWD_CONTROL = "Crowd control";
    public static final String PUBLISHED_TIMESLOT = "Published timeslot";
    public static final String PUBLISHED_ROOM = "Published room";
    public static final String ROOM_STABILITY = "Room stability";

    private int talkTypeOfTimeslot = 10000;
    private int talkTypeOfRoom = 10000;
    private int roomUnavailableTimeslot = 10000;
    private int roomConflict = 10;
    private int speakerUnavailableTimeslot = 1;
    private int speakerConflict = 1;
    private int speakerRequiredTimeslotTags = 1;
    private int speakerProhibitedTimeslotTags = 1;
    private int talkRequiredTimeslotTags = 1;
    private int talkProhibitedTimeslotTags = 1;
    private int speakerRequiredRoomTags = 1;
    private int speakerProhibitedRoomTags = 1;
    private int talkRequiredRoomTags = 1;
    private int talkProhibitedRoomTags = 1;
    private int talkPrerequisiteTalks = 1;
    private int consecutiveTalksPause = 1;
    private int minimumConsecutiveTalksPauseInMinutes = 30;

    private int talkMutuallyExclusiveTalksTags = 1;
    private int publishedTimeslot = 10;

    private int themeTrackConflict = 10;
    private int sectorConflict = 10;
    private int audienceTypeDiversity = 1;
    private int audienceTypeThemeTrackConflict = 0;
    private int audienceLevelDiversity = 1;
    private int audienceLevelFlowPerContentViolation = 10;
    private int contentConflict = 100;
    private int languageDiversity = 10;
    private int speakerPreferredTimeslotTags = 20;
    private int speakerUndesiredTimeslotTags = 20;
    private int talkPreferredTimeslotTags = 20;
    private int talkUndesiredTimeslotTags = 20;
    private int speakerPreferredRoomTags = 20;
    private int speakerUndesiredRoomTags = 20;
    private int talkPreferredRoomTags = 20;
    private int talkUndesiredRoomTags = 20;
    private int sameDayTalks = 10;
    private int popularTalks = 10;
    private int crowdControl = 10;
    private int publishedRoom = 10;
    private int roomStability = 10;

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

    public int getSpeakerRequiredTimeslotTags() {
        return speakerRequiredTimeslotTags;
    }

    public void setSpeakerRequiredTimeslotTags(int speakerRequiredTimeslotTags) {
        this.speakerRequiredTimeslotTags = speakerRequiredTimeslotTags;
    }

    public int getSpeakerProhibitedTimeslotTags() {
        return speakerProhibitedTimeslotTags;
    }

    public void setSpeakerProhibitedTimeslotTags(int speakerProhibitedTimeslotTags) {
        this.speakerProhibitedTimeslotTags = speakerProhibitedTimeslotTags;
    }

    public int getTalkRequiredTimeslotTags() {
        return talkRequiredTimeslotTags;
    }

    public void setTalkRequiredTimeslotTags(int talkRequiredTimeslotTags) {
        this.talkRequiredTimeslotTags = talkRequiredTimeslotTags;
    }

    public int getTalkProhibitedTimeslotTags() {
        return talkProhibitedTimeslotTags;
    }

    public void setTalkProhibitedTimeslotTags(int talkProhibitedTimeslotTags) {
        this.talkProhibitedTimeslotTags = talkProhibitedTimeslotTags;
    }

    public int getSpeakerRequiredRoomTags() {
        return speakerRequiredRoomTags;
    }

    public void setSpeakerRequiredRoomTags(int speakerRequiredRoomTags) {
        this.speakerRequiredRoomTags = speakerRequiredRoomTags;
    }

    public int getSpeakerProhibitedRoomTags() {
        return speakerProhibitedRoomTags;
    }

    public void setSpeakerProhibitedRoomTags(int speakerProhibitedRoomTags) {
        this.speakerProhibitedRoomTags = speakerProhibitedRoomTags;
    }

    public int getTalkRequiredRoomTags() {
        return talkRequiredRoomTags;
    }

    public void setTalkRequiredRoomTags(int talkRequiredRoomTags) {
        this.talkRequiredRoomTags = talkRequiredRoomTags;
    }

    public int getTalkProhibitedRoomTags() {
        return talkProhibitedRoomTags;
    }

    public void setTalkProhibitedRoomTags(int talkProhibitedRoomTags) {
        this.talkProhibitedRoomTags = talkProhibitedRoomTags;
    }

    public int getTalkMutuallyExclusiveTalksTags() {
        return talkMutuallyExclusiveTalksTags;
    }

    public void setTalkMutuallyExclusiveTalksTags(int talkMutuallyExclusiveTalksTags) {
        this.talkMutuallyExclusiveTalksTags = talkMutuallyExclusiveTalksTags;
    }

    public int getPublishedTimeslot() {
        return publishedTimeslot;
    }

    public void setPublishedTimeslot(int publishedTimeslot) {
        this.publishedTimeslot = publishedTimeslot;
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

    public int getSpeakerPreferredTimeslotTags() {
        return speakerPreferredTimeslotTags;
    }

    public void setSpeakerPreferredTimeslotTags(int speakerPreferredTimeslotTags) {
        this.speakerPreferredTimeslotTags = speakerPreferredTimeslotTags;
    }

    public int getSpeakerUndesiredTimeslotTags() {
        return speakerUndesiredTimeslotTags;
    }

    public void setSpeakerUndesiredTimeslotTags(int speakerUndesiredTimeslotTags) {
        this.speakerUndesiredTimeslotTags = speakerUndesiredTimeslotTags;
    }

    public int getTalkPreferredTimeslotTags() {
        return talkPreferredTimeslotTags;
    }

    public void setTalkPreferredTimeslotTags(int talkPreferredTimeslotTags) {
        this.talkPreferredTimeslotTags = talkPreferredTimeslotTags;
    }

    public int getTalkUndesiredTimeslotTags() {
        return talkUndesiredTimeslotTags;
    }

    public void setTalkUndesiredTimeslotTags(int talkUndesiredTimeslotTags) {
        this.talkUndesiredTimeslotTags = talkUndesiredTimeslotTags;
    }

    public int getSpeakerPreferredRoomTags() {
        return speakerPreferredRoomTags;
    }

    public void setSpeakerPreferredRoomTags(int speakerPreferredRoomTags) {
        this.speakerPreferredRoomTags = speakerPreferredRoomTags;
    }

    public int getSpeakerUndesiredRoomTags() {
        return speakerUndesiredRoomTags;
    }

    public void setSpeakerUndesiredRoomTags(int speakerUndesiredRoomTags) {
        this.speakerUndesiredRoomTags = speakerUndesiredRoomTags;
    }

    public int getTalkPreferredRoomTags() {
        return talkPreferredRoomTags;
    }

    public void setTalkPreferredRoomTags(int talkPreferredRoomTags) {
        this.talkPreferredRoomTags = talkPreferredRoomTags;
    }

    public int getTalkUndesiredRoomTags() {
        return talkUndesiredRoomTags;
    }

    public void setTalkUndesiredRoomTags(int talkUndesiredRoomTags) {
        this.talkUndesiredRoomTags = talkUndesiredRoomTags;
    }

    public int getTalkPrerequisiteTalks() {
        return talkPrerequisiteTalks;
    }

    public void setTalkPrerequisiteTalks(int talkPrerequisiteTalks) {
        this.talkPrerequisiteTalks = talkPrerequisiteTalks;
    }

    public int getConsecutiveTalksPause() {
        return consecutiveTalksPause;
    }

    public void setConsecutiveTalksPause(int consecutiveTalksPause) {
        this.consecutiveTalksPause = consecutiveTalksPause;
    }

    public int getMinimumConsecutiveTalksPauseInMinutes() {
        return minimumConsecutiveTalksPauseInMinutes;
    }

    public void setMinimumConsecutiveTalksPauseInMinutes(int minimumConsecutiveTalksPauseInMinutes) {
        this.minimumConsecutiveTalksPauseInMinutes = minimumConsecutiveTalksPauseInMinutes;
    }

    public int getSameDayTalks() {
        return sameDayTalks;
    }

    public void setSameDayTalks(int sameDayTalks) {
        this.sameDayTalks = sameDayTalks;
    }

    public int getPopularTalks() {
        return popularTalks;
    }

    public void setPopularTalks(int popularTalks) {
        this.popularTalks = popularTalks;
    }

    public int getCrowdControl() {
        return crowdControl;
    }

    public void setCrowdControl(int crowdControl) {
        this.crowdControl = crowdControl;
    }

    public int getPublishedRoom() {
        return publishedRoom;
    }

    public void setPublishedRoom(int publishedRoom) {
        this.publishedRoom = publishedRoom;
    }

    public int getRoomStability() {
        return roomStability;
    }

    public void setRoomStability(int roomStability) {
        this.roomStability = roomStability;
    }
}