/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.conferencescheduling.optional.score;

import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration;
import org.optaplanner.examples.conferencescheduling.domain.Talk;

import static org.optaplanner.core.api.score.stream.common.ConstraintCollectors.*;
import static org.optaplanner.core.api.score.stream.common.Joiners.*;

public class ConferenceSchedulingConstraintProvider implements ConstraintProvider {

    @Override
    public void defineConstraints(ConstraintFactory constraintFactory) {
        // Hard constraints
        roomUnavailableTimeslot(constraintFactory);
        roomConflict(constraintFactory);
        speakerUnavailableTimeslot(constraintFactory);
        speakerConflict(constraintFactory);
        talkPrerequisiteTalks(constraintFactory);
        talkPrerequisiteTalks(constraintFactory);
//        consecutiveTalksPause(constraintFactory); // TODO
        crowdControl(constraintFactory);

        speakerRequiredTimeslotTags(constraintFactory);
        speakerProhibitedTimeslotTags(constraintFactory);
        talkRequiredTimeslotTags(constraintFactory);
        talkProhibitedTimeslotTags(constraintFactory);
        speakerRequiredRoomTags(constraintFactory);
        speakerProhibitedRoomTags(constraintFactory);
        talkRequiredRoomTags(constraintFactory);
        talkProhibitedRoomTags(constraintFactory);

        // Medium constraints
        publishedTimeslot(constraintFactory);

        // Soft constraints
        publishedRoom(constraintFactory);
        themeTrackConflict(constraintFactory);
        themeTrackRoomStability(constraintFactory);
        sectorConflict(constraintFactory);
        audienceTypeDiversity(constraintFactory);
        audienceTypeThemeTrackConflict(constraintFactory);
        audienceLevelDiversity(constraintFactory);
        contentAudienceLevelFlowViolation(constraintFactory);
        contentConflict(constraintFactory);
        languageDiversity(constraintFactory);
        sameDayTalks(constraintFactory);
        popularTalks(constraintFactory);

        speakerPreferredTimeslotTags(constraintFactory);
        speakerUndesiredTimeslotTags(constraintFactory);
        talkPreferredTimeslotTags(constraintFactory);
        talkUndesiredTimeslotTags(constraintFactory);
        speakerPreferredRoomTags(constraintFactory);
        speakerUndesiredRoomTags(constraintFactory);
        talkPreferredRoomTags(constraintFactory);
        talkUndesiredRoomTags(constraintFactory);
    }

    // ************************************************************************
    // Hard constraints
    // ************************************************************************

    protected void roomUnavailableTimeslot(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraint(ConferenceConstraintConfiguration.ROOM_UNAVAILABLE_TIMESLOT);
        c.from(Talk.class)
                .filter(Talk::hasUnavailableRoom)
                .penalizeInt(Talk::getDurationInMinutes);
    }

    protected void roomConflict(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraint(ConferenceConstraintConfiguration.ROOM_CONFLICT);
        c.from(Talk.class)
                .joinOther(equalTo(Talk::getRoom))
                // TODO Support joiner for time overlap
                .filter(Talk::overlapsTime)
                .penalizeInt(Talk::overlappingDurationInMinutes);
    }

    protected void speakerUnavailableTimeslot(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraint(ConferenceConstraintConfiguration.SPEAKER_UNAVAILABLE_TIMESLOT);
        c.from(Talk.class)
                .filter(Talk::hasAnyUnavailableSpeaker)
                .penalizeInt(Talk::getDurationInMinutes);
    }

    protected void speakerConflict(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraint(ConferenceConstraintConfiguration.SPEAKER_CONFLICT);
        c.from(Talk.class)
                .joinOther(intersectingWith(Talk::getSpeakerList))
                // TODO Support joiner for time overlap
                .filter(Talk::overlapsTime)
                .penalizeInt(Talk::overlappingDurationInMinutes);
    }

    protected void talkPrerequisiteTalks(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraint(ConferenceConstraintConfiguration.TALK_PREREQUISITE_TALKS);
        c.from(Talk.class)
                .join(Talk.class,
                        contains(Talk::getPrerequisiteTalkSet, (Talk talk2) -> talk2),
                        lessThan(talk1 -> talk1.getTimeslot().getStartDateTime(),
                                talk2 -> talk2.getTimeslot().getEndDateTime()))
                .penalizeInt(Talk::combinedDurationInMinutes);
    }

    protected void talkMutuallyExclusiveTalksTags(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraint(ConferenceConstraintConfiguration.TALK_MUTUALLY_EXCLUSIVE_TALKS_TAGS);
        c.from(Talk.class)
                .joinOther(intersectingWith(Talk::getMutuallyExclusiveTalksTagSet))
                // TODO Support joiner for time overlap
                .filter(Talk::overlapsTime)
                .penalizeInt((talk1, talk2) -> talk1.overlappingMutuallyExclusiveTalksTagCount(talk2)
                        * talk1.overlappingDurationInMinutes(talk2));
    }

//    protected void consecutiveTalksPause(ConstraintFactory constraintFactory) {
//        Constraint c = constraintFactory.newConstraint(ConferenceConstraintConfiguration.CONSECUTIVE_TALKS_PAUSE);
//        c.from(Talk.class)
//                .joinOther(intersectingWith(Talk::getSpeakerList))
//                .filter((talk1, talk2) -> !talk1.getTimeslot().pauseExists(talk2.getTimeslot(), $minimumPause))
//                .penalizeInt(Talk::combinedDurationInMinutes);
//    }

    protected void crowdControl(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraint(ConferenceConstraintConfiguration.CROWD_CONTROL);
        c.from(Talk.class)
                .filter(talk -> talk.getCrowdControlRisk() > 0)
                // No joinOther() because we want both [A, B] and [B, A].
                .join(Talk.class, equalTo(Talk::getTimeslot))
                .filter((talk1, talk2) -> talk1 != talk2)
                .groupBy((talk1, talk2) -> talk1, countBi())
                .filter((talk, count) -> count != 1)
                .penalizeInt((talk, count) -> talk.getDurationInMinutes());
    }

    protected void speakerRequiredTimeslotTags(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraint(ConferenceConstraintConfiguration.SPEAKER_REQUIRED_TIMESLOT_TAGS);
        c.from(Talk.class)
                .filter(talk -> talk.missingSpeakerRequiredTimeslotTagCount() > 0)
                .penalizeInt(talk -> talk.missingSpeakerRequiredTimeslotTagCount() * talk.getDurationInMinutes());
    }

    protected void speakerProhibitedTimeslotTags(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraint(ConferenceConstraintConfiguration.SPEAKER_PROHIBITED_TIMESLOT_TAGS);
        c.from(Talk.class)
                .filter(talk -> talk.prevailingSpeakerProhibitedTimeslotTagCount() > 0)
                .penalizeInt(talk -> talk.prevailingSpeakerProhibitedTimeslotTagCount() * talk.getDurationInMinutes());
    }

    protected void talkRequiredTimeslotTags(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraint(ConferenceConstraintConfiguration.TALK_REQUIRED_TIMESLOT_TAGS);
        c.from(Talk.class)
                .filter(talk -> talk.missingRequiredTimeslotTagCount() > 0)
                .penalizeInt(talk -> talk.missingRequiredTimeslotTagCount() * talk.getDurationInMinutes());
    }

    protected void talkProhibitedTimeslotTags(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraint(ConferenceConstraintConfiguration.TALK_PROHIBITED_TIMESLOT_TAGS);
        c.from(Talk.class)
                .filter(talk -> talk.prevailingProhibitedTimeslotTagCount() > 0)
                .penalizeInt(talk -> talk.prevailingProhibitedTimeslotTagCount() * talk.getDurationInMinutes());
    }

    protected void speakerRequiredRoomTags(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraint(ConferenceConstraintConfiguration.SPEAKER_REQUIRED_ROOM_TAGS);
        c.from(Talk.class)
                .filter(talk -> talk.missingSpeakerRequiredRoomTagCount() > 0)
                .penalizeInt(talk -> talk.missingSpeakerRequiredRoomTagCount() * talk.getDurationInMinutes());
    }

    protected void speakerProhibitedRoomTags(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraint(ConferenceConstraintConfiguration.SPEAKER_PROHIBITED_ROOM_TAGS);
        c.from(Talk.class)
                .filter(talk -> talk.prevailingSpeakerProhibitedRoomTagCount() > 0)
                .penalizeInt(talk -> talk.prevailingSpeakerProhibitedRoomTagCount() * talk.getDurationInMinutes());
    }

    protected void talkRequiredRoomTags(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraint(ConferenceConstraintConfiguration.TALK_REQUIRED_ROOM_TAGS);
        c.from(Talk.class)
                .filter(talk -> talk.missingRequiredRoomTagCount() > 0)
                .penalizeInt(talk -> talk.missingRequiredRoomTagCount() * talk.getDurationInMinutes());
    }

    protected void talkProhibitedRoomTags(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraint(ConferenceConstraintConfiguration.TALK_PROHIBITED_ROOM_TAGS);
        c.from(Talk.class)
                .filter(talk -> talk.prevailingProhibitedRoomTagCount() > 0)
                .penalizeInt(talk -> talk.prevailingProhibitedRoomTagCount() * talk.getDurationInMinutes());
    }

    // ************************************************************************
    // Medium constraints
    // ************************************************************************

    protected void publishedTimeslot(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraint(ConferenceConstraintConfiguration.PUBLISHED_TIMESLOT);
        c.from(Talk.class)
                .filter(talk -> talk.getPublishedTimeslot() != null
                        && talk.getTimeslot() != talk.getPublishedTimeslot())
                .penalize();
    }

    // ************************************************************************
    // Soft constraints
    // ************************************************************************

    protected void publishedRoom(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraint(ConferenceConstraintConfiguration.PUBLISHED_ROOM);
        c.from(Talk.class)
                .filter(talk -> talk.getPublishedRoom() != null && talk.getRoom() != talk.getPublishedRoom())
                .penalize();
    }

    protected void themeTrackConflict(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraint(ConferenceConstraintConfiguration.THEME_TRACK_CONFLICT);
        c.from(Talk.class)
                .joinOther(intersectingWith(Talk::getThemeTrackTagSet))
                // TODO Support joiner for time overlap
                .filter(Talk::overlapsTime)
                .penalizeInt((talk1, talk2) -> talk1.overlappingThemeTrackCount(talk2)
                        * talk1.overlappingDurationInMinutes(talk2));
    }

    protected void themeTrackRoomStability(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraint(ConferenceConstraintConfiguration.THEME_TRACK_ROOM_STABILITY);
        c.from(Talk.class)
                .joinOther(equalTo(talk -> talk.getTimeslot().getStartDateTime().toLocalDate()),
                        intersectingWith(Talk::getThemeTrackTagSet))
                // TODO Support joiner for time overlap
                .filter(Talk::overlapsTime)
                .filter((talk1, talk2) -> talk1.getRoom() != talk2.getRoom())
                .penalizeInt((talk1, talk2) -> talk1.overlappingThemeTrackCount(talk2)
                        * talk1.combinedDurationInMinutes(talk2));
    }

    protected void sectorConflict(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraint(ConferenceConstraintConfiguration.SECTOR_CONFLICT);
        c.from(Talk.class)
                .joinOther(intersectingWith(Talk::getSectorTagSet))
                // TODO Support joiner for time overlap
                .filter(Talk::overlapsTime)
                .penalizeInt((talk1, talk2) -> talk1.overlappingSectorCount(talk2)
                        * talk1.overlappingDurationInMinutes(talk2));
    }

    protected void audienceTypeDiversity(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraint(ConferenceConstraintConfiguration.AUDIENCE_TYPE_DIVERSITY);
        c.from(Talk.class)
                // Timeslot.overlaps() is deliberately not used
                .joinOther(equalTo(Talk::getTimeslot),
                        intersectingWith(Talk::getAudienceTypeSet))
                .rewardInt((talk1, talk2) -> talk1.overlappingAudienceTypeCount(talk2)
                        * talk1.getTimeslot().getDurationInMinutes());
    }

    protected void audienceTypeThemeTrackConflict(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraint(ConferenceConstraintConfiguration.AUDIENCE_TYPE_THEME_TRACK_CONFLICT);
        c.from(Talk.class)
                .joinOther(intersectingWith(Talk::getThemeTrackTagSet),
                        intersectingWith(Talk::getAudienceTypeSet))
                // TODO Support joiner for time overlap
                .filter(Talk::overlapsTime)
                .penalizeInt((talk1, talk2) -> talk1.overlappingThemeTrackCount(talk2)
                        * talk1.overlappingAudienceTypeCount(talk2)
                        * talk1.overlappingDurationInMinutes(talk2));
    }

    protected void audienceLevelDiversity(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraint(ConferenceConstraintConfiguration.AUDIENCE_LEVEL_DIVERSITY);
        c.from(Talk.class)
                // Timeslot.overlaps() is deliberately not used
                .joinOther(equalTo(Talk::getTimeslot))
                .filter((talk1, talk2) -> talk1.getAudienceLevel() != talk2.getAudienceLevel())
                .rewardInt((talk1, talk2) -> talk1.getTimeslot().getDurationInMinutes());
    }

    protected void contentAudienceLevelFlowViolation(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraint(ConferenceConstraintConfiguration.CONTENT_AUDIENCE_LEVEL_FLOW_VIOLATION);
        c.from(Talk.class)
                // joinOther not needed due to lessThan(Talk::getAudienceLevel)
                .join(Talk.class,
                        intersectingWith(Talk::getContentTagSet),
                        lessThan(Talk::getAudienceLevel),
                        greaterThan(talk1 -> talk1.getTimeslot().getEndDateTime(),
                                talk2 -> talk2.getTimeslot().getStartDateTime()))
                .penalizeInt((talk1, talk2) -> talk1.overlappingContentCount(talk2)
                        * talk1.combinedDurationInMinutes(talk2));
    }

    protected void contentConflict(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraint(ConferenceConstraintConfiguration.CONTENT_CONFLICT);
        c.from(Talk.class)
                .joinOther(intersectingWith(Talk::getContentTagSet))
                // TODO Support joiner for time overlap
                .filter(Talk::overlapsTime)
                .penalizeInt((talk1, talk2) -> talk1.overlappingContentCount(talk2)
                        * talk1.overlappingDurationInMinutes(talk2));
    }

    protected void languageDiversity(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraint(ConferenceConstraintConfiguration.LANGUAGE_DIVERSITY);
        c.from(Talk.class)
                // Timeslot.overlaps() is deliberately not used
                .joinOther(equalTo(Talk::getTimeslot))
                .filter((talk1, talk2) -> !talk1.getLanguage().equals(talk2.getLanguage()))
                .rewardInt((talk1, talk2) -> talk1.getTimeslot().getDurationInMinutes());
    }

    protected void sameDayTalks(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraint(ConferenceConstraintConfiguration.SAME_DAY_TALKS);
        c.from(Talk.class)
                .joinOther()
                .filter((talk1, talk2) ->
                        (talk1.overlappingContentCount(talk2) > 0 || talk1.overlappingThemeTrackCount(talk2) > 0)
                        && !talk1.getTimeslot().getDate().equals(talk2.getTimeslot().getDate()))
                .penalizeInt((talk1, talk2) -> talk1.overlappingContentCount(talk2)
                        * talk1.overlappingThemeTrackCount(talk2)
                        * talk1.overlappingDurationInMinutes(talk2));
    }

    protected void popularTalks(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraint(ConferenceConstraintConfiguration.POPULAR_TALKS);
        c.from(Talk.class)
                // joinOther not needed due to lessThan(Talk::getFavoriteCount)
                .join(Talk.class,
                        lessThan(Talk::getFavoriteCount),
                        greaterThan(talk -> talk.getRoom().getCapacity()))
                .penalizeInt(Talk::combinedDurationInMinutes);
    }

    protected void speakerPreferredTimeslotTags(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraint(ConferenceConstraintConfiguration.SPEAKER_PREFERRED_TIMESLOT_TAGS);
        c.from(Talk.class)
                .filter(talk -> talk.missingSpeakerPreferredTimeslotTagCount() > 0)
                .penalizeInt(talk -> talk.missingSpeakerPreferredTimeslotTagCount() * talk.getDurationInMinutes());
    }

    protected void speakerUndesiredTimeslotTags(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraint(ConferenceConstraintConfiguration.SPEAKER_UNDESIRED_TIMESLOT_TAGS);
        c.from(Talk.class)
                .filter(talk -> talk.prevailingSpeakerUndesiredTimeslotTagCount() > 0)
                .penalizeInt(talk -> talk.prevailingSpeakerUndesiredTimeslotTagCount() * talk.getDurationInMinutes());
    }

    protected void talkPreferredTimeslotTags(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraint(ConferenceConstraintConfiguration.TALK_PREFERRED_TIMESLOT_TAGS);
        c.from(Talk.class)
                .filter(talk -> talk.missingPreferredTimeslotTagCount() > 0)
                .penalizeInt(talk -> talk.missingPreferredTimeslotTagCount() * talk.getDurationInMinutes());
    }

    protected void talkUndesiredTimeslotTags(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraint(ConferenceConstraintConfiguration.TALK_UNDESIRED_TIMESLOT_TAGS);
        c.from(Talk.class)
                .filter(talk -> talk.prevailingUndesiredTimeslotTagCount() > 0)
                .penalizeInt(talk -> talk.prevailingUndesiredTimeslotTagCount() * talk.getDurationInMinutes());
    }

    protected void speakerPreferredRoomTags(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraint(ConferenceConstraintConfiguration.SPEAKER_PREFERRED_ROOM_TAGS);
        c.from(Talk.class)
                .filter(talk -> talk.missingSpeakerPreferredRoomTagCount() > 0)
                .penalizeInt(talk -> talk.missingSpeakerPreferredRoomTagCount() * talk.getDurationInMinutes());
    }

    protected void speakerUndesiredRoomTags(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraint(ConferenceConstraintConfiguration.SPEAKER_UNDESIRED_ROOM_TAGS);
        c.from(Talk.class)
                .filter(talk -> talk.prevailingSpeakerUndesiredRoomTagCount() > 0)
                .penalizeInt(talk -> talk.prevailingSpeakerUndesiredRoomTagCount() * talk.getDurationInMinutes());
    }

    protected void talkPreferredRoomTags(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraint(ConferenceConstraintConfiguration.TALK_PREFERRED_ROOM_TAGS);
        c.from(Talk.class)
                .filter(talk -> talk.missingPreferredRoomTagCount() > 0)
                .penalizeInt(talk -> talk.missingPreferredRoomTagCount() * talk.getDurationInMinutes());
    }

    protected void talkUndesiredRoomTags(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraint(ConferenceConstraintConfiguration.TALK_UNDESIRED_ROOM_TAGS);
        c.from(Talk.class)
                .filter(talk -> talk.prevailingUndesiredRoomTagCount() > 0)
                .penalizeInt(talk -> talk.prevailingUndesiredRoomTagCount() * talk.getDurationInMinutes());
    }

}
