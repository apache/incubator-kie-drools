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

import org.optaplanner.core.api.domain.constraintweight.ConstraintConfiguration;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.examples.common.domain.AbstractPersistable;

@ConstraintConfiguration(constraintPackage = "org.optaplanner.examples.conferencescheduling.solver")
public class ConferenceConstraintConfiguration extends AbstractPersistable {

    public static final String ROOM_UNAVAILABLE_TIMESLOT = "Room unavailable timeslot";
    public static final String ROOM_CONFLICT = "Room conflict";
    public static final String SPEAKER_UNAVAILABLE_TIMESLOT = "Speaker unavailable timeslot";
    public static final String SPEAKER_CONFLICT = "Speaker conflict";
    public static final String TALK_PREREQUISITE_TALKS = "Talk prerequisite talks";
    public static final String TALK_MUTUALLY_EXCLUSIVE_TALKS_TAGS = "Talk mutually-exclusive-talks tags";
    public static final String CONSECUTIVE_TALKS_PAUSE = "Consecutive talks pause";
    public static final String CROWD_CONTROL = "Crowd control";

    public static final String SPEAKER_REQUIRED_TIMESLOT_TAGS = "Speaker required timeslot tags";
    public static final String SPEAKER_PROHIBITED_TIMESLOT_TAGS = "Speaker prohibited timeslot tags";
    public static final String TALK_REQUIRED_TIMESLOT_TAGS = "Talk required timeslot tags";
    public static final String TALK_PROHIBITED_TIMESLOT_TAGS = "Talk prohibited timeslot tags";
    public static final String SPEAKER_REQUIRED_ROOM_TAGS = "Speaker required room tags";
    public static final String SPEAKER_PROHIBITED_ROOM_TAGS = "Speaker prohibited room tags";
    public static final String TALK_REQUIRED_ROOM_TAGS = "Talk required room tags";
    public static final String TALK_PROHIBITED_ROOM_TAGS = "Talk prohibited room tags";

    public static final String PUBLISHED_TIMESLOT = "Published timeslot";

    public static final String PUBLISHED_ROOM = "Published room";
    public static final String THEME_TRACK_CONFLICT = "Theme track conflict";
    public static final String THEME_TRACK_ROOM_STABILITY = "Theme track room stability";
    public static final String SECTOR_CONFLICT = "Sector conflict";
    public static final String AUDIENCE_TYPE_DIVERSITY = "Audience type diversity";
    public static final String AUDIENCE_TYPE_THEME_TRACK_CONFLICT = "Audience type theme track conflict";
    public static final String AUDIENCE_LEVEL_DIVERSITY = "Audience level diversity";
    public static final String CONTENT_AUDIENCE_LEVEL_FLOW_VIOLATION = "Content audience level flow violation";
    public static final String CONTENT_CONFLICT = "Content conflict";
    public static final String LANGUAGE_DIVERSITY = "Language diversity";
    public static final String SAME_DAY_TALKS = "Same day talks";
    public static final String POPULAR_TALKS = "Popular talks";

    public static final String SPEAKER_PREFERRED_TIMESLOT_TAGS = "Speaker preferred timeslot tags";
    public static final String SPEAKER_UNDESIRED_TIMESLOT_TAGS = "Speaker undesired timeslot tags";
    public static final String TALK_PREFERRED_TIMESLOT_TAGS = "Talk preferred timeslot tags";
    public static final String TALK_UNDESIRED_TIMESLOT_TAGS = "Talk undesired timeslot tags";
    public static final String SPEAKER_PREFERRED_ROOM_TAGS = "Speaker preferred room tags";
    public static final String SPEAKER_UNDESIRED_ROOM_TAGS = "Speaker undesired room tags";
    public static final String TALK_PREFERRED_ROOM_TAGS = "Talk preferred room tags";
    public static final String TALK_UNDESIRED_ROOM_TAGS = "Talk undesired room tags";

    private int minimumConsecutiveTalksPauseInMinutes = 30;

    @ConstraintWeight(ROOM_UNAVAILABLE_TIMESLOT)
    private HardMediumSoftScore roomUnavailableTimeslot = HardMediumSoftScore.ofHard(100_000);
    @ConstraintWeight(ROOM_CONFLICT)
    private HardMediumSoftScore roomConflict = HardMediumSoftScore.ofHard(1_000);
    @ConstraintWeight(SPEAKER_UNAVAILABLE_TIMESLOT)
    private HardMediumSoftScore speakerUnavailableTimeslot = HardMediumSoftScore.ofHard(100);
    @ConstraintWeight(SPEAKER_CONFLICT)
    private HardMediumSoftScore speakerConflict = HardMediumSoftScore.ofHard(10);
    @ConstraintWeight(TALK_PREREQUISITE_TALKS)
    private HardMediumSoftScore talkPrerequisiteTalks = HardMediumSoftScore.ofHard(10);
    @ConstraintWeight(TALK_MUTUALLY_EXCLUSIVE_TALKS_TAGS)
    private HardMediumSoftScore talkMutuallyExclusiveTalksTags = HardMediumSoftScore.ofHard(1);
    @ConstraintWeight(CONSECUTIVE_TALKS_PAUSE)
    private HardMediumSoftScore consecutiveTalksPause = HardMediumSoftScore.ofHard(1);
    @ConstraintWeight(CROWD_CONTROL)
    private HardMediumSoftScore crowdControl = HardMediumSoftScore.ofHard(1);

    @ConstraintWeight(SPEAKER_REQUIRED_TIMESLOT_TAGS)
    private HardMediumSoftScore speakerRequiredTimeslotTags = HardMediumSoftScore.ofHard(1);
    @ConstraintWeight(SPEAKER_PROHIBITED_TIMESLOT_TAGS)
    private HardMediumSoftScore speakerProhibitedTimeslotTags = HardMediumSoftScore.ofHard(1);
    @ConstraintWeight(TALK_REQUIRED_TIMESLOT_TAGS)
    private HardMediumSoftScore talkRequiredTimeslotTags = HardMediumSoftScore.ofHard(1);
    @ConstraintWeight(TALK_PROHIBITED_TIMESLOT_TAGS)
    private HardMediumSoftScore talkProhibitedTimeslotTags = HardMediumSoftScore.ofHard(1);
    @ConstraintWeight(SPEAKER_REQUIRED_ROOM_TAGS)
    private HardMediumSoftScore speakerRequiredRoomTags = HardMediumSoftScore.ofHard(1);
    @ConstraintWeight(SPEAKER_PROHIBITED_ROOM_TAGS)
    private HardMediumSoftScore speakerProhibitedRoomTags = HardMediumSoftScore.ofHard(1);
    @ConstraintWeight(TALK_REQUIRED_ROOM_TAGS)
    private HardMediumSoftScore talkRequiredRoomTags = HardMediumSoftScore.ofHard(1);
    @ConstraintWeight(TALK_PROHIBITED_ROOM_TAGS)
    private HardMediumSoftScore talkProhibitedRoomTags = HardMediumSoftScore.ofHard(1);

    @ConstraintWeight(PUBLISHED_TIMESLOT)
    private HardMediumSoftScore publishedTimeslot = HardMediumSoftScore.ofMedium(1);

    @ConstraintWeight(PUBLISHED_ROOM)
    private HardMediumSoftScore publishedRoom = HardMediumSoftScore.ofSoft(10);
    @ConstraintWeight(THEME_TRACK_CONFLICT)
    private HardMediumSoftScore themeTrackConflict = HardMediumSoftScore.ofSoft(10);
    @ConstraintWeight(THEME_TRACK_ROOM_STABILITY)
    private HardMediumSoftScore themeTrackRoomStability = HardMediumSoftScore.ofSoft(10);
    @ConstraintWeight(SECTOR_CONFLICT)
    private HardMediumSoftScore sectorConflict = HardMediumSoftScore.ofSoft(10);
    @ConstraintWeight(AUDIENCE_TYPE_DIVERSITY)
    private HardMediumSoftScore audienceTypeDiversity = HardMediumSoftScore.ofSoft(1);
    @ConstraintWeight(AUDIENCE_TYPE_THEME_TRACK_CONFLICT)
    private HardMediumSoftScore audienceTypeThemeTrackConflict = HardMediumSoftScore.ofSoft(1);
    @ConstraintWeight(AUDIENCE_LEVEL_DIVERSITY)
    private HardMediumSoftScore audienceLevelDiversity = HardMediumSoftScore.ofSoft(1);
    @ConstraintWeight(CONTENT_AUDIENCE_LEVEL_FLOW_VIOLATION)
    private HardMediumSoftScore contentAudienceLevelFlowViolation = HardMediumSoftScore.ofSoft(10);
    @ConstraintWeight(CONTENT_CONFLICT)
    private HardMediumSoftScore contentConflict = HardMediumSoftScore.ofSoft(100);
    @ConstraintWeight(LANGUAGE_DIVERSITY)
    private HardMediumSoftScore languageDiversity = HardMediumSoftScore.ofSoft(10);
    @ConstraintWeight(SAME_DAY_TALKS)
    private HardMediumSoftScore sameDayTalks = HardMediumSoftScore.ofSoft(10);
    @ConstraintWeight(POPULAR_TALKS)
    private HardMediumSoftScore popularTalks = HardMediumSoftScore.ofSoft(10);

    @ConstraintWeight(SPEAKER_PREFERRED_TIMESLOT_TAGS)
    private HardMediumSoftScore speakerPreferredTimeslotTags = HardMediumSoftScore.ofSoft(20);
    @ConstraintWeight(SPEAKER_UNDESIRED_TIMESLOT_TAGS)
    private HardMediumSoftScore speakerUndesiredTimeslotTags = HardMediumSoftScore.ofSoft(20);
    @ConstraintWeight(TALK_PREFERRED_TIMESLOT_TAGS)
    private HardMediumSoftScore talkPreferredTimeslotTags = HardMediumSoftScore.ofSoft(20);
    @ConstraintWeight(TALK_UNDESIRED_TIMESLOT_TAGS)
    private HardMediumSoftScore talkUndesiredTimeslotTags = HardMediumSoftScore.ofSoft(20);
    @ConstraintWeight(SPEAKER_PREFERRED_ROOM_TAGS)
    private HardMediumSoftScore speakerPreferredRoomTags = HardMediumSoftScore.ofSoft(20);
    @ConstraintWeight(SPEAKER_UNDESIRED_ROOM_TAGS)
    private HardMediumSoftScore speakerUndesiredRoomTags = HardMediumSoftScore.ofSoft(20);
    @ConstraintWeight(TALK_PREFERRED_ROOM_TAGS)
    private HardMediumSoftScore talkPreferredRoomTags = HardMediumSoftScore.ofSoft(20);
    @ConstraintWeight(TALK_UNDESIRED_ROOM_TAGS)
    private HardMediumSoftScore talkUndesiredRoomTags = HardMediumSoftScore.ofSoft(20);

    public ConferenceConstraintConfiguration() {
    }

    public ConferenceConstraintConfiguration(long id) {
        super(id);
    }

    // ************************************************************************
    // Simple getters and setters
    // ************************************************************************

    public int getMinimumConsecutiveTalksPauseInMinutes() {
        return minimumConsecutiveTalksPauseInMinutes;
    }

    public void setMinimumConsecutiveTalksPauseInMinutes(int minimumConsecutiveTalksPauseInMinutes) {
        this.minimumConsecutiveTalksPauseInMinutes = minimumConsecutiveTalksPauseInMinutes;
    }

    public HardMediumSoftScore getRoomUnavailableTimeslot() {
        return roomUnavailableTimeslot;
    }

    public void setRoomUnavailableTimeslot(HardMediumSoftScore roomUnavailableTimeslot) {
        this.roomUnavailableTimeslot = roomUnavailableTimeslot;
    }

    public HardMediumSoftScore getRoomConflict() {
        return roomConflict;
    }

    public void setRoomConflict(HardMediumSoftScore roomConflict) {
        this.roomConflict = roomConflict;
    }

    public HardMediumSoftScore getSpeakerUnavailableTimeslot() {
        return speakerUnavailableTimeslot;
    }

    public void setSpeakerUnavailableTimeslot(HardMediumSoftScore speakerUnavailableTimeslot) {
        this.speakerUnavailableTimeslot = speakerUnavailableTimeslot;
    }

    public HardMediumSoftScore getSpeakerConflict() {
        return speakerConflict;
    }

    public void setSpeakerConflict(HardMediumSoftScore speakerConflict) {
        this.speakerConflict = speakerConflict;
    }

    public HardMediumSoftScore getTalkPrerequisiteTalks() {
        return talkPrerequisiteTalks;
    }

    public void setTalkPrerequisiteTalks(HardMediumSoftScore talkPrerequisiteTalks) {
        this.talkPrerequisiteTalks = talkPrerequisiteTalks;
    }

    public HardMediumSoftScore getTalkMutuallyExclusiveTalksTags() {
        return talkMutuallyExclusiveTalksTags;
    }

    public void setTalkMutuallyExclusiveTalksTags(HardMediumSoftScore talkMutuallyExclusiveTalksTags) {
        this.talkMutuallyExclusiveTalksTags = talkMutuallyExclusiveTalksTags;
    }

    public HardMediumSoftScore getConsecutiveTalksPause() {
        return consecutiveTalksPause;
    }

    public void setConsecutiveTalksPause(HardMediumSoftScore consecutiveTalksPause) {
        this.consecutiveTalksPause = consecutiveTalksPause;
    }

    public HardMediumSoftScore getCrowdControl() {
        return crowdControl;
    }

    public void setCrowdControl(HardMediumSoftScore crowdControl) {
        this.crowdControl = crowdControl;
    }

    public HardMediumSoftScore getSpeakerRequiredTimeslotTags() {
        return speakerRequiredTimeslotTags;
    }

    public void setSpeakerRequiredTimeslotTags(HardMediumSoftScore speakerRequiredTimeslotTags) {
        this.speakerRequiredTimeslotTags = speakerRequiredTimeslotTags;
    }

    public HardMediumSoftScore getSpeakerProhibitedTimeslotTags() {
        return speakerProhibitedTimeslotTags;
    }

    public void setSpeakerProhibitedTimeslotTags(HardMediumSoftScore speakerProhibitedTimeslotTags) {
        this.speakerProhibitedTimeslotTags = speakerProhibitedTimeslotTags;
    }

    public HardMediumSoftScore getTalkRequiredTimeslotTags() {
        return talkRequiredTimeslotTags;
    }

    public void setTalkRequiredTimeslotTags(HardMediumSoftScore talkRequiredTimeslotTags) {
        this.talkRequiredTimeslotTags = talkRequiredTimeslotTags;
    }

    public HardMediumSoftScore getTalkProhibitedTimeslotTags() {
        return talkProhibitedTimeslotTags;
    }

    public void setTalkProhibitedTimeslotTags(HardMediumSoftScore talkProhibitedTimeslotTags) {
        this.talkProhibitedTimeslotTags = talkProhibitedTimeslotTags;
    }

    public HardMediumSoftScore getSpeakerRequiredRoomTags() {
        return speakerRequiredRoomTags;
    }

    public void setSpeakerRequiredRoomTags(HardMediumSoftScore speakerRequiredRoomTags) {
        this.speakerRequiredRoomTags = speakerRequiredRoomTags;
    }

    public HardMediumSoftScore getSpeakerProhibitedRoomTags() {
        return speakerProhibitedRoomTags;
    }

    public void setSpeakerProhibitedRoomTags(HardMediumSoftScore speakerProhibitedRoomTags) {
        this.speakerProhibitedRoomTags = speakerProhibitedRoomTags;
    }

    public HardMediumSoftScore getTalkRequiredRoomTags() {
        return talkRequiredRoomTags;
    }

    public void setTalkRequiredRoomTags(HardMediumSoftScore talkRequiredRoomTags) {
        this.talkRequiredRoomTags = talkRequiredRoomTags;
    }

    public HardMediumSoftScore getTalkProhibitedRoomTags() {
        return talkProhibitedRoomTags;
    }

    public void setTalkProhibitedRoomTags(HardMediumSoftScore talkProhibitedRoomTags) {
        this.talkProhibitedRoomTags = talkProhibitedRoomTags;
    }

    public HardMediumSoftScore getPublishedTimeslot() {
        return publishedTimeslot;
    }

    public void setPublishedTimeslot(HardMediumSoftScore publishedTimeslot) {
        this.publishedTimeslot = publishedTimeslot;
    }

    public HardMediumSoftScore getPublishedRoom() {
        return publishedRoom;
    }

    public void setPublishedRoom(HardMediumSoftScore publishedRoom) {
        this.publishedRoom = publishedRoom;
    }

    public HardMediumSoftScore getThemeTrackConflict() {
        return themeTrackConflict;
    }

    public void setThemeTrackConflict(HardMediumSoftScore themeTrackConflict) {
        this.themeTrackConflict = themeTrackConflict;
    }

    public HardMediumSoftScore getThemeTrackRoomStability() {
        return themeTrackRoomStability;
    }

    public void setThemeTrackRoomStability(HardMediumSoftScore themeTrackRoomStability) {
        this.themeTrackRoomStability = themeTrackRoomStability;
    }

    public HardMediumSoftScore getSectorConflict() {
        return sectorConflict;
    }

    public void setSectorConflict(HardMediumSoftScore sectorConflict) {
        this.sectorConflict = sectorConflict;
    }

    public HardMediumSoftScore getAudienceTypeDiversity() {
        return audienceTypeDiversity;
    }

    public void setAudienceTypeDiversity(HardMediumSoftScore audienceTypeDiversity) {
        this.audienceTypeDiversity = audienceTypeDiversity;
    }

    public HardMediumSoftScore getAudienceTypeThemeTrackConflict() {
        return audienceTypeThemeTrackConflict;
    }

    public void setAudienceTypeThemeTrackConflict(HardMediumSoftScore audienceTypeThemeTrackConflict) {
        this.audienceTypeThemeTrackConflict = audienceTypeThemeTrackConflict;
    }

    public HardMediumSoftScore getAudienceLevelDiversity() {
        return audienceLevelDiversity;
    }

    public void setAudienceLevelDiversity(HardMediumSoftScore audienceLevelDiversity) {
        this.audienceLevelDiversity = audienceLevelDiversity;
    }

    public HardMediumSoftScore getContentAudienceLevelFlowViolation() {
        return contentAudienceLevelFlowViolation;
    }

    public void setContentAudienceLevelFlowViolation(HardMediumSoftScore contentAudienceLevelFlowViolation) {
        this.contentAudienceLevelFlowViolation = contentAudienceLevelFlowViolation;
    }

    public HardMediumSoftScore getContentConflict() {
        return contentConflict;
    }

    public void setContentConflict(HardMediumSoftScore contentConflict) {
        this.contentConflict = contentConflict;
    }

    public HardMediumSoftScore getLanguageDiversity() {
        return languageDiversity;
    }

    public void setLanguageDiversity(HardMediumSoftScore languageDiversity) {
        this.languageDiversity = languageDiversity;
    }

    public HardMediumSoftScore getSameDayTalks() {
        return sameDayTalks;
    }

    public void setSameDayTalks(HardMediumSoftScore sameDayTalks) {
        this.sameDayTalks = sameDayTalks;
    }

    public HardMediumSoftScore getPopularTalks() {
        return popularTalks;
    }

    public void setPopularTalks(HardMediumSoftScore popularTalks) {
        this.popularTalks = popularTalks;
    }

    public HardMediumSoftScore getSpeakerPreferredTimeslotTags() {
        return speakerPreferredTimeslotTags;
    }

    public void setSpeakerPreferredTimeslotTags(HardMediumSoftScore speakerPreferredTimeslotTags) {
        this.speakerPreferredTimeslotTags = speakerPreferredTimeslotTags;
    }

    public HardMediumSoftScore getSpeakerUndesiredTimeslotTags() {
        return speakerUndesiredTimeslotTags;
    }

    public void setSpeakerUndesiredTimeslotTags(HardMediumSoftScore speakerUndesiredTimeslotTags) {
        this.speakerUndesiredTimeslotTags = speakerUndesiredTimeslotTags;
    }

    public HardMediumSoftScore getTalkPreferredTimeslotTags() {
        return talkPreferredTimeslotTags;
    }

    public void setTalkPreferredTimeslotTags(HardMediumSoftScore talkPreferredTimeslotTags) {
        this.talkPreferredTimeslotTags = talkPreferredTimeslotTags;
    }

    public HardMediumSoftScore getTalkUndesiredTimeslotTags() {
        return talkUndesiredTimeslotTags;
    }

    public void setTalkUndesiredTimeslotTags(HardMediumSoftScore talkUndesiredTimeslotTags) {
        this.talkUndesiredTimeslotTags = talkUndesiredTimeslotTags;
    }

    public HardMediumSoftScore getSpeakerPreferredRoomTags() {
        return speakerPreferredRoomTags;
    }

    public void setSpeakerPreferredRoomTags(HardMediumSoftScore speakerPreferredRoomTags) {
        this.speakerPreferredRoomTags = speakerPreferredRoomTags;
    }

    public HardMediumSoftScore getSpeakerUndesiredRoomTags() {
        return speakerUndesiredRoomTags;
    }

    public void setSpeakerUndesiredRoomTags(HardMediumSoftScore speakerUndesiredRoomTags) {
        this.speakerUndesiredRoomTags = speakerUndesiredRoomTags;
    }

    public HardMediumSoftScore getTalkPreferredRoomTags() {
        return talkPreferredRoomTags;
    }

    public void setTalkPreferredRoomTags(HardMediumSoftScore talkPreferredRoomTags) {
        this.talkPreferredRoomTags = talkPreferredRoomTags;
    }

    public HardMediumSoftScore getTalkUndesiredRoomTags() {
        return talkUndesiredRoomTags;
    }

    public void setTalkUndesiredRoomTags(HardMediumSoftScore talkUndesiredRoomTags) {
        this.talkUndesiredRoomTags = talkUndesiredRoomTags;
    }
}
