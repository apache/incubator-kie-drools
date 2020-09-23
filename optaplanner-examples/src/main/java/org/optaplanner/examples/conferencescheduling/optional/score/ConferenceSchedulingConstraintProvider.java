/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import static org.optaplanner.core.api.score.stream.ConstraintCollectors.countBi;
import static org.optaplanner.core.api.score.stream.Joiners.equal;
import static org.optaplanner.core.api.score.stream.Joiners.filtering;
import static org.optaplanner.core.api.score.stream.Joiners.greaterThan;
import static org.optaplanner.core.api.score.stream.Joiners.lessThan;
import static org.optaplanner.core.api.score.stream.Joiners.overlapping;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.AUDIENCE_LEVEL_DIVERSITY;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.AUDIENCE_TYPE_DIVERSITY;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.AUDIENCE_TYPE_THEME_TRACK_CONFLICT;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.CONSECUTIVE_TALKS_PAUSE;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.CONTENT_AUDIENCE_LEVEL_FLOW_VIOLATION;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.CONTENT_CONFLICT;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.CROWD_CONTROL;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.LANGUAGE_DIVERSITY;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.POPULAR_TALKS;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.PUBLISHED_ROOM;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.PUBLISHED_TIMESLOT;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.ROOM_CONFLICT;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.ROOM_UNAVAILABLE_TIMESLOT;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.SAME_DAY_TALKS;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.SECTOR_CONFLICT;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.SPEAKER_CONFLICT;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.SPEAKER_PREFERRED_ROOM_TAGS;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.SPEAKER_PREFERRED_TIMESLOT_TAGS;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.SPEAKER_PROHIBITED_ROOM_TAGS;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.SPEAKER_PROHIBITED_TIMESLOT_TAGS;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.SPEAKER_REQUIRED_ROOM_TAGS;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.SPEAKER_REQUIRED_TIMESLOT_TAGS;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.SPEAKER_UNAVAILABLE_TIMESLOT;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.SPEAKER_UNDESIRED_ROOM_TAGS;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.SPEAKER_UNDESIRED_TIMESLOT_TAGS;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.TALK_MUTUALLY_EXCLUSIVE_TALKS_TAGS;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.TALK_PREFERRED_ROOM_TAGS;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.TALK_PREFERRED_TIMESLOT_TAGS;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.TALK_PREREQUISITE_TALKS;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.TALK_PROHIBITED_ROOM_TAGS;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.TALK_PROHIBITED_TIMESLOT_TAGS;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.TALK_REQUIRED_ROOM_TAGS;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.TALK_REQUIRED_TIMESLOT_TAGS;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.TALK_UNDESIRED_ROOM_TAGS;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.TALK_UNDESIRED_TIMESLOT_TAGS;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.THEME_TRACK_CONFLICT;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.THEME_TRACK_ROOM_STABILITY;

import java.util.Objects;

import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration;
import org.optaplanner.examples.conferencescheduling.domain.Speaker;
import org.optaplanner.examples.conferencescheduling.domain.Talk;

public final class ConferenceSchedulingConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory factory) {
        return new Constraint[] {
                // Hard constraints
                roomUnavailableTimeslot(factory),
                roomConflict(factory),
                speakerUnavailableTimeslot(factory),
                speakerConflict(factory),
                talkPrerequisiteTalks(factory),
                talkMutuallyExclusiveTalksTags(factory),
                consecutiveTalksPause(factory),
                crowdControl(factory),
                speakerRequiredTimeslotTags(factory),
                speakerProhibitedTimeslotTags(factory),
                talkRequiredTimeslotTags(factory),
                talkProhibitedTimeslotTags(factory),
                speakerRequiredRoomTags(factory),
                speakerProhibitedRoomTags(factory),
                talkRequiredRoomTags(factory),
                talkProhibitedRoomTags(factory),
                // Medium constraints
                publishedTimeslot(factory),
                // Soft constraints
                publishedRoom(factory),
                themeTrackConflict(factory),
                themeTrackRoomStability(factory),
                sectorConflict(factory),
                audienceTypeDiversity(factory),
                audienceTypeThemeTrackConflict(factory),
                audienceLevelDiversity(factory),
                contentAudienceLevelFlowViolation(factory),
                contentConflict(factory),
                languageDiversity(factory),
                sameDayTalks(factory),
                popularTalks(factory),
                speakerPreferredTimeslotTags(factory),
                speakerUndesiredTimeslotTags(factory),
                talkPreferredTimeslotTags(factory),
                talkUndesiredTimeslotTags(factory),
                speakerPreferredRoomTags(factory),
                speakerUndesiredRoomTags(factory),
                talkPreferredRoomTags(factory),
                talkUndesiredRoomTags(factory)
        };
    }

    // ************************************************************************
    // Hard constraints
    // ************************************************************************

    protected Constraint roomUnavailableTimeslot(ConstraintFactory factory) {
        return factory.from(Talk.class)
                .filter(Talk::hasUnavailableRoom)
                .penalizeConfigurable(ROOM_UNAVAILABLE_TIMESLOT, Talk::getDurationInMinutes);
    }

    protected Constraint roomConflict(ConstraintFactory factory) {
        return factory.fromUniquePair(Talk.class,
                equal(Talk::getRoom),
                overlapping(t -> t.getTimeslot().getStartDateTime(), t -> t.getTimeslot().getEndDateTime()))
                .penalizeConfigurable(ROOM_CONFLICT, Talk::overlappingDurationInMinutes);
    }

    protected Constraint speakerUnavailableTimeslot(ConstraintFactory factory) {
        return factory.from(Talk.class)
                .filter(Talk::hasAnyUnavailableSpeaker)
                .penalizeConfigurable(SPEAKER_UNAVAILABLE_TIMESLOT, Talk::getDurationInMinutes);
    }

    protected Constraint speakerConflict(ConstraintFactory factory) {
        return factory.fromUniquePair(Talk.class,
                overlapping(t -> t.getTimeslot().getStartDateTime(), t -> t.getTimeslot().getEndDateTime()))
                .ifExists(Speaker.class,
                        filtering((talk1, talk2, speaker) -> talk1.hasSpeaker(speaker) && talk2.hasSpeaker(speaker)))
                .penalizeConfigurable(SPEAKER_CONFLICT, Talk::overlappingDurationInMinutes);
    }

    protected Constraint talkPrerequisiteTalks(ConstraintFactory factory) {
        return factory.from(Talk.class)
                .join(Talk.class,
                        greaterThan(t -> t.getTimeslot().getEndDateTime(), t -> t.getTimeslot().getStartDateTime()),
                        filtering((talk1, talk2) -> talk2.getPrerequisiteTalkSet().contains(talk1)))
                .penalizeConfigurable(TALK_PREREQUISITE_TALKS, Talk::combinedDurationInMinutes);
    }

    protected Constraint talkMutuallyExclusiveTalksTags(ConstraintFactory factory) {
        return factory.fromUniquePair(Talk.class,
                overlapping(t -> t.getTimeslot().getStartDateTime(), t -> t.getTimeslot().getEndDateTime()),
                filtering((talk1, talk2) -> talk2.overlappingMutuallyExclusiveTalksTagCount(talk1) > 0))
                .penalizeConfigurable(TALK_MUTUALLY_EXCLUSIVE_TALKS_TAGS,
                        (talk1, talk2) -> talk1.overlappingMutuallyExclusiveTalksTagCount(talk2) *
                                talk1.overlappingDurationInMinutes(talk2));
    }

    protected Constraint consecutiveTalksPause(ConstraintFactory factory) {
        return factory.fromUniquePair(Talk.class,
                filtering((talk1, talk2) -> talk2.hasMutualSpeaker(talk1)))
                .ifExists(ConferenceConstraintConfiguration.class,
                        filtering((talk1, talk2, config) -> !talk1.getTimeslot().pauseExists(talk2.getTimeslot(),
                                config.getMinimumConsecutiveTalksPauseInMinutes())))
                .penalizeConfigurable(CONSECUTIVE_TALKS_PAUSE, Talk::combinedDurationInMinutes);
    }

    protected Constraint crowdControl(ConstraintFactory factory) {
        return factory.from(Talk.class)
                .filter(talk -> talk.getCrowdControlRisk() > 0)
                .join(Talk.class,
                        equal(Talk::getTimeslot))
                .filter((talk1, talk2) -> !Objects.equals(talk1, talk2) && talk2.getCrowdControlRisk() > 0)
                .groupBy((talk1, talk2) -> talk1, countBi())
                .filter((talk, count) -> count != 1)
                .penalizeConfigurable(CROWD_CONTROL, (talk, count) -> talk.getDurationInMinutes());
    }

    protected Constraint speakerRequiredTimeslotTags(ConstraintFactory factory) {
        return factory.from(Talk.class)
                .filter(talk -> talk.missingSpeakerRequiredTimeslotTagCount() > 0)
                .penalizeConfigurable(SPEAKER_REQUIRED_TIMESLOT_TAGS,
                        talk -> talk.missingSpeakerRequiredTimeslotTagCount() * talk.getDurationInMinutes());
    }

    protected Constraint speakerProhibitedTimeslotTags(ConstraintFactory factory) {
        return factory.from(Talk.class)
                .filter(talk -> talk.prevailingSpeakerProhibitedTimeslotTagCount() > 0)
                .penalizeConfigurable(SPEAKER_PROHIBITED_TIMESLOT_TAGS,
                        talk -> talk.prevailingSpeakerProhibitedTimeslotTagCount() * talk.getDurationInMinutes());
    }

    protected Constraint talkRequiredTimeslotTags(ConstraintFactory factory) {
        return factory.from(Talk.class)
                .filter(talk -> talk.missingRequiredTimeslotTagCount() > 0)
                .penalizeConfigurable(TALK_REQUIRED_TIMESLOT_TAGS,
                        talk -> talk.missingRequiredTimeslotTagCount() * talk.getDurationInMinutes());
    }

    protected Constraint talkProhibitedTimeslotTags(ConstraintFactory factory) {
        return factory.from(Talk.class)
                .filter(talk -> talk.prevailingProhibitedTimeslotTagCount() > 0)
                .penalizeConfigurable(TALK_PROHIBITED_TIMESLOT_TAGS,
                        talk -> talk.prevailingProhibitedTimeslotTagCount() * talk.getDurationInMinutes());
    }

    protected Constraint speakerRequiredRoomTags(ConstraintFactory factory) {
        return factory.from(Talk.class)
                .filter(talk -> talk.missingSpeakerRequiredRoomTagCount() > 0)
                .penalizeConfigurable(SPEAKER_REQUIRED_ROOM_TAGS,
                        talk -> talk.missingSpeakerRequiredRoomTagCount() * talk.getDurationInMinutes());
    }

    protected Constraint speakerProhibitedRoomTags(ConstraintFactory factory) {
        return factory.from(Talk.class)
                .filter(talk -> talk.prevailingSpeakerProhibitedRoomTagCount() > 0)
                .penalizeConfigurable(SPEAKER_PROHIBITED_ROOM_TAGS,
                        talk -> talk.prevailingSpeakerProhibitedRoomTagCount() * talk.getDurationInMinutes());
    }

    protected Constraint talkRequiredRoomTags(ConstraintFactory factory) {
        return factory.from(Talk.class)
                .filter(talk -> talk.missingRequiredRoomTagCount() > 0)
                .penalizeConfigurable(TALK_REQUIRED_ROOM_TAGS,
                        talk -> talk.missingRequiredRoomTagCount() * talk.getDurationInMinutes());
    }

    protected Constraint talkProhibitedRoomTags(ConstraintFactory factory) {
        return factory.from(Talk.class)
                .filter(talk -> talk.prevailingProhibitedRoomTagCount() > 0)
                .penalizeConfigurable(TALK_PROHIBITED_ROOM_TAGS,
                        talk -> talk.prevailingProhibitedRoomTagCount() * talk.getDurationInMinutes());
    }

    // ************************************************************************
    // Medium constraints
    // ************************************************************************

    protected Constraint publishedTimeslot(ConstraintFactory factory) {
        return factory.from(Talk.class)
                .filter(talk -> talk.getPublishedTimeslot() != null
                        && talk.getTimeslot() != talk.getPublishedTimeslot())
                .penalizeConfigurable(PUBLISHED_TIMESLOT);
    }

    // ************************************************************************
    // Soft constraints
    // ************************************************************************

    protected Constraint publishedRoom(ConstraintFactory factory) {
        return factory.from(Talk.class)
                .filter(talk -> talk.getPublishedRoom() != null && talk.getRoom() != talk.getPublishedRoom())
                .penalizeConfigurable(PUBLISHED_ROOM);
    }

    protected Constraint themeTrackConflict(ConstraintFactory factory) {
        return factory.fromUniquePair(Talk.class,
                overlapping(t -> t.getTimeslot().getStartDateTime(), t -> t.getTimeslot().getEndDateTime()),
                filtering((talk1, talk2) -> talk2.overlappingThemeTrackCount(talk1) > 0))
                .penalizeConfigurable(THEME_TRACK_CONFLICT,
                        (talk1, talk2) -> talk1.overlappingThemeTrackCount(talk2) *
                                talk1.overlappingDurationInMinutes(talk2));
    }

    protected Constraint themeTrackRoomStability(ConstraintFactory factory) {
        return factory.fromUniquePair(Talk.class,
                equal(talk -> talk.getTimeslot().getStartDateTime().toLocalDate()),
                filtering((talk1, talk2) -> talk2.overlappingThemeTrackCount(talk1) > 0))
                .filter((talk1, talk2) -> talk1.getRoom() != talk2.getRoom())
                .penalizeConfigurable(THEME_TRACK_ROOM_STABILITY,
                        (talk1, talk2) -> talk1.overlappingThemeTrackCount(talk2) *
                                talk1.combinedDurationInMinutes(talk2));
    }

    protected Constraint sectorConflict(ConstraintFactory factory) {
        return factory.fromUniquePair(Talk.class,
                overlapping(t -> t.getTimeslot().getStartDateTime(), t -> t.getTimeslot().getEndDateTime()),
                filtering((talk1, talk2) -> talk2.overlappingSectorCount(talk1) > 0))
                .penalizeConfigurable(SECTOR_CONFLICT,
                        (talk1, talk2) -> talk1.overlappingSectorCount(talk2)
                                * talk1.overlappingDurationInMinutes(talk2));
    }

    protected Constraint audienceTypeDiversity(ConstraintFactory factory) {
        return factory.fromUniquePair(Talk.class,
                equal(Talk::getTimeslot),
                filtering((talk1, talk2) -> talk2.overlappingAudienceTypeCount(talk1) > 0))
                .rewardConfigurable(AUDIENCE_TYPE_DIVERSITY,
                        (talk1, talk2) -> talk1.overlappingAudienceTypeCount(talk2)
                                * talk1.getTimeslot().getDurationInMinutes());
    }

    protected Constraint audienceTypeThemeTrackConflict(ConstraintFactory factory) {
        return factory.fromUniquePair(Talk.class,
                overlapping(t -> t.getTimeslot().getStartDateTime(), t -> t.getTimeslot().getEndDateTime()),
                filtering((talk1, talk2) -> talk2.overlappingThemeTrackCount(talk1) > 0),
                filtering((talk1, talk2) -> talk2.overlappingAudienceTypeCount(talk1) > 0))
                .penalizeConfigurable(AUDIENCE_TYPE_THEME_TRACK_CONFLICT,
                        (talk1, talk2) -> talk1.overlappingThemeTrackCount(talk2)
                                * talk1.overlappingAudienceTypeCount(talk2)
                                * talk1.overlappingDurationInMinutes(talk2));
    }

    protected Constraint audienceLevelDiversity(ConstraintFactory factory) {
        return factory.fromUniquePair(Talk.class,
                equal(Talk::getTimeslot))
                .filter((talk1, talk2) -> talk1.getAudienceLevel() != talk2.getAudienceLevel())
                .rewardConfigurable(AUDIENCE_LEVEL_DIVERSITY,
                        (talk1, talk2) -> talk1.getTimeslot().getDurationInMinutes());
    }

    protected Constraint contentAudienceLevelFlowViolation(ConstraintFactory factory) {
        return factory.from(Talk.class)
                .join(Talk.class,
                        lessThan(Talk::getAudienceLevel),
                        greaterThan(talk1 -> talk1.getTimeslot().getEndDateTime(),
                                talk2 -> talk2.getTimeslot().getStartDateTime()),
                        filtering((talk1, talk2) -> talk2.overlappingContentCount(talk1) > 0))
                .penalizeConfigurable(CONTENT_AUDIENCE_LEVEL_FLOW_VIOLATION,
                        (talk1, talk2) -> talk1.overlappingContentCount(talk2)
                                * talk1.combinedDurationInMinutes(talk2));
    }

    protected Constraint contentConflict(ConstraintFactory factory) {
        return factory.fromUniquePair(Talk.class,
                overlapping(t -> t.getTimeslot().getStartDateTime(), t -> t.getTimeslot().getEndDateTime()),
                filtering((talk1, talk2) -> talk2.overlappingContentCount(talk1) > 0))
                .penalizeConfigurable(CONTENT_CONFLICT,
                        (talk1, talk2) -> talk1.overlappingContentCount(talk2)
                                * talk1.overlappingDurationInMinutes(talk2));
    }

    protected Constraint languageDiversity(ConstraintFactory factory) {
        return factory.fromUniquePair(Talk.class,
                equal(Talk::getTimeslot))
                .filter((talk1, talk2) -> !talk1.getLanguage().equals(talk2.getLanguage()))
                .rewardConfigurable(LANGUAGE_DIVERSITY,
                        (talk1, talk2) -> talk1.getTimeslot().getDurationInMinutes());
    }

    protected Constraint sameDayTalks(ConstraintFactory factory) {
        return factory.fromUniquePair(Talk.class)
                .filter((talk1,
                        talk2) -> (talk1.overlappingContentCount(talk2) > 0 || talk1.overlappingThemeTrackCount(talk2) > 0)
                                && !talk1.getTimeslot().isOnSameDayAs(talk2.getTimeslot()))
                .penalizeConfigurable(SAME_DAY_TALKS,
                        (talk1, talk2) -> (talk2.overlappingThemeTrackCount(talk1) + talk2.overlappingContentCount(talk1))
                                * talk1.combinedDurationInMinutes(talk2));
    }

    protected Constraint popularTalks(ConstraintFactory factory) {
        return factory.from(Talk.class)
                .join(Talk.class,
                        lessThan(Talk::getFavoriteCount),
                        greaterThan(talk -> talk.getRoom().getCapacity()))
                .penalizeConfigurable(POPULAR_TALKS, Talk::combinedDurationInMinutes);
    }

    protected Constraint speakerPreferredTimeslotTags(ConstraintFactory factory) {
        return factory.from(Talk.class)
                .filter(talk -> talk.missingSpeakerPreferredTimeslotTagCount() > 0)
                .penalizeConfigurable(SPEAKER_PREFERRED_TIMESLOT_TAGS,
                        talk -> talk.missingSpeakerPreferredTimeslotTagCount() * talk.getDurationInMinutes());
    }

    protected Constraint speakerUndesiredTimeslotTags(ConstraintFactory factory) {
        return factory.from(Talk.class)
                .filter(talk -> talk.prevailingSpeakerUndesiredTimeslotTagCount() > 0)
                .penalizeConfigurable(SPEAKER_UNDESIRED_TIMESLOT_TAGS,
                        talk -> talk.prevailingSpeakerUndesiredTimeslotTagCount() * talk.getDurationInMinutes());
    }

    protected Constraint talkPreferredTimeslotTags(ConstraintFactory factory) {
        return factory.from(Talk.class)
                .filter(talk -> talk.missingPreferredTimeslotTagCount() > 0)
                .penalizeConfigurable(TALK_PREFERRED_TIMESLOT_TAGS,
                        talk -> talk.missingPreferredTimeslotTagCount() * talk.getDurationInMinutes());
    }

    protected Constraint talkUndesiredTimeslotTags(ConstraintFactory factory) {
        return factory.from(Talk.class)
                .filter(talk -> talk.prevailingUndesiredTimeslotTagCount() > 0)
                .penalizeConfigurable(TALK_UNDESIRED_TIMESLOT_TAGS,
                        talk -> talk.prevailingUndesiredTimeslotTagCount() * talk.getDurationInMinutes());
    }

    protected Constraint speakerPreferredRoomTags(ConstraintFactory factory) {
        return factory.from(Talk.class)
                .filter(talk -> talk.missingSpeakerPreferredRoomTagCount() > 0)
                .penalizeConfigurable(SPEAKER_PREFERRED_ROOM_TAGS,
                        talk -> talk.missingSpeakerPreferredRoomTagCount() * talk.getDurationInMinutes());
    }

    protected Constraint speakerUndesiredRoomTags(ConstraintFactory factory) {
        return factory.from(Talk.class)
                .filter(talk -> talk.prevailingSpeakerUndesiredRoomTagCount() > 0)
                .penalizeConfigurable(SPEAKER_UNDESIRED_ROOM_TAGS,
                        talk -> talk.prevailingSpeakerUndesiredRoomTagCount() * talk.getDurationInMinutes());
    }

    protected Constraint talkPreferredRoomTags(ConstraintFactory factory) {
        return factory.from(Talk.class)
                .filter(talk -> talk.missingPreferredRoomTagCount() > 0)
                .penalizeConfigurable(TALK_PREFERRED_ROOM_TAGS,
                        talk -> talk.missingPreferredRoomTagCount() * talk.getDurationInMinutes());
    }

    protected Constraint talkUndesiredRoomTags(ConstraintFactory factory) {
        return factory.from(Talk.class)
                .filter(talk -> talk.prevailingUndesiredRoomTagCount() > 0)
                .penalizeConfigurable(TALK_UNDESIRED_ROOM_TAGS,
                        talk -> talk.prevailingUndesiredRoomTagCount() * talk.getDurationInMinutes());
    }

}
