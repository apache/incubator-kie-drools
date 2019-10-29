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
import org.optaplanner.examples.conferencescheduling.domain.Talk;

import static org.optaplanner.core.api.score.stream.Joiners.equal;
import static org.optaplanner.core.api.score.stream.Joiners.greaterThan;
import static org.optaplanner.core.api.score.stream.Joiners.lessThan;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.AUDIENCE_LEVEL_DIVERSITY;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.LANGUAGE_DIVERSITY;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.POPULAR_TALKS;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.PUBLISHED_ROOM;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.PUBLISHED_TIMESLOT;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.ROOM_CONFLICT;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.ROOM_UNAVAILABLE_TIMESLOT;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.SAME_DAY_TALKS;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.SPEAKER_PREFERRED_ROOM_TAGS;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.SPEAKER_PREFERRED_TIMESLOT_TAGS;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.SPEAKER_PROHIBITED_ROOM_TAGS;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.SPEAKER_PROHIBITED_TIMESLOT_TAGS;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.SPEAKER_REQUIRED_ROOM_TAGS;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.SPEAKER_REQUIRED_TIMESLOT_TAGS;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.SPEAKER_UNAVAILABLE_TIMESLOT;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.SPEAKER_UNDESIRED_ROOM_TAGS;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.SPEAKER_UNDESIRED_TIMESLOT_TAGS;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.TALK_PREFERRED_ROOM_TAGS;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.TALK_PREFERRED_TIMESLOT_TAGS;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.TALK_PROHIBITED_ROOM_TAGS;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.TALK_PROHIBITED_TIMESLOT_TAGS;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.TALK_REQUIRED_ROOM_TAGS;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.TALK_REQUIRED_TIMESLOT_TAGS;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.TALK_UNDESIRED_ROOM_TAGS;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.TALK_UNDESIRED_TIMESLOT_TAGS;

public class ConferenceSchedulingConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory factory) {
        return new Constraint[]{
                // TODO  Many of these constraints don't run yet

                // Hard constraints
                roomUnavailableTimeslot(factory),
                roomConflict(factory),
                speakerUnavailableTimeslot(factory),
                speakerConflict(factory),
                talkPrerequisiteTalks(factory),
                talkMutuallyExclusiveTalksTags(factory),
                consecutiveTalksPause(factory), // TODO Implement it
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

    private Constraint roomUnavailableTimeslot(ConstraintFactory factory) {
        return factory.from(Talk.class)
                .filter(Talk::hasUnavailableRoom)
                .penalizeConfigurable(ROOM_UNAVAILABLE_TIMESLOT,
                        Talk::getDurationInMinutes);
    }

    private Constraint roomConflict(ConstraintFactory factory) {
        return factory.fromUniquePair(Talk.class, equal(Talk::getRoom))
                // TODO Support joiner for time overlap
                .filter(Talk::overlapsTime)
                .penalizeConfigurable(ROOM_CONFLICT,
                        Talk::overlappingDurationInMinutes);
    }

    private Constraint speakerUnavailableTimeslot(ConstraintFactory factory) {
        return factory.from(Talk.class)
                .filter(Talk::hasAnyUnavailableSpeaker)
                .penalizeConfigurable(SPEAKER_UNAVAILABLE_TIMESLOT,
                        Talk::getDurationInMinutes);
    }

    private Constraint speakerConflict(ConstraintFactory factory) {
        throw new UnsupportedOperationException("Not yet implemented due to missing support for advanced joiners.");
//        return factory.fromUniquePair(Talk.class, intersecting(Talk::getSpeakerList))
//                // TODO Support joiner for time overlap
//                .filter(Talk::overlapsTime)
//                .penalizeConfigurable(SPEAKER_CONFLICT,
//                        Talk::overlappingDurationInMinutes);
    }

    private Constraint talkPrerequisiteTalks(ConstraintFactory factory) {
        throw new UnsupportedOperationException("Not yet implemented due to missing support for advanced joiners.");
//        return factory.from(Talk.class)
//                .join(Talk.class,
//                        containing(Talk::getPrerequisiteTalkSet, Function.identity()),
//                        lessThan(talk1 -> talk1.getTimeslot().getStartDateTime(),
//                                talk2 -> talk2.getTimeslot().getEndDateTime()))
//                .penalizeConfigurable(TALK_PREREQUISITE_TALKS,
//                        Talk::combinedDurationInMinutes);
    }

    private Constraint talkMutuallyExclusiveTalksTags(ConstraintFactory factory) {
        throw new UnsupportedOperationException("Not yet implemented due to missing support for advanced joiners.");
//        return factory.fromUniquePair(Talk.class, intersecting(Talk::getMutuallyExclusiveTalksTagSet))
//                // TODO Support joiner for time overlap
//                .filter(Talk::overlapsTime)
//                .penalizeConfigurable(TALK_MUTUALLY_EXCLUSIVE_TALKS_TAGS,
//                        (talk1, talk2) -> talk1.overlappingMutuallyExclusiveTalksTagCount(talk2)
//                        * talk1.overlappingDurationInMinutes(talk2));
    }

    private Constraint consecutiveTalksPause(ConstraintFactory factory) {
        throw new UnsupportedOperationException("Not yet implemented due to missing support for advanced joiners.");
//        return factory.fromUniquePair((Talk.class, intersecting(Talk::getSpeakerList))
//                .filter((talk1, talk2) -> !talk1.getTimeslot().pauseExists(talk2.getTimeslot(), $minimumPause))
//                .penalizeConfigurable(CONSECUTIVE_TALKS_PAUSE,
//                        Talk::combinedDurationInMinutes);
    }

    private Constraint crowdControl(ConstraintFactory factory) {
        throw new UnsupportedOperationException("Not yet implemented due to missing support for bi-joins.");
//        return factory.from(Talk.class)
//                .filter(talk -> talk.getCrowdControlRisk() > 0)
//                // No fromUniquePair() because we want both [A, B] and [B, A].
//                .join(Talk.class, equal(Talk::getTimeslot))
//                .filter((talk1, talk2) -> talk1 != talk2)
//                .groupBy((talk1, talk2) -> talk1, countBi())
//                .filter((talk, count) -> count != 1)
//                .penalizeConfigurable(CROWD_CONTROL,
//                        (talk, count) -> talk.getDurationInMinutes());
    }

    private Constraint speakerRequiredTimeslotTags(ConstraintFactory factory) {
        return factory.from(Talk.class)
                .filter(talk -> talk.missingSpeakerRequiredTimeslotTagCount() > 0)
                .penalizeConfigurable(SPEAKER_REQUIRED_TIMESLOT_TAGS,
                        talk -> talk.missingSpeakerRequiredTimeslotTagCount() * talk.getDurationInMinutes());
    }

    private Constraint speakerProhibitedTimeslotTags(ConstraintFactory factory) {
        return factory.from(Talk.class)
                .filter(talk -> talk.prevailingSpeakerProhibitedTimeslotTagCount() > 0)
                .penalizeConfigurable(SPEAKER_PROHIBITED_TIMESLOT_TAGS,
                        talk -> talk.prevailingSpeakerProhibitedTimeslotTagCount() * talk.getDurationInMinutes());
    }

    private Constraint talkRequiredTimeslotTags(ConstraintFactory factory) {
        return factory.from(Talk.class)
                .filter(talk -> talk.missingRequiredTimeslotTagCount() > 0)
                .penalizeConfigurable(TALK_REQUIRED_TIMESLOT_TAGS,
                        talk -> talk.missingRequiredTimeslotTagCount() * talk.getDurationInMinutes());
    }

    private Constraint talkProhibitedTimeslotTags(ConstraintFactory factory) {
        return factory.from(Talk.class)
                .filter(talk -> talk.prevailingProhibitedTimeslotTagCount() > 0)
                .penalizeConfigurable(TALK_PROHIBITED_TIMESLOT_TAGS,
                        talk -> talk.prevailingProhibitedTimeslotTagCount() * talk.getDurationInMinutes());
    }

    private Constraint speakerRequiredRoomTags(ConstraintFactory factory) {
        return factory.from(Talk.class)
                .filter(talk -> talk.missingSpeakerRequiredRoomTagCount() > 0)
                .penalizeConfigurable(SPEAKER_REQUIRED_ROOM_TAGS,
                        talk -> talk.missingSpeakerRequiredRoomTagCount() * talk.getDurationInMinutes());
    }

    private Constraint speakerProhibitedRoomTags(ConstraintFactory factory) {
        return factory.from(Talk.class)
                .filter(talk -> talk.prevailingSpeakerProhibitedRoomTagCount() > 0)
                .penalizeConfigurable(SPEAKER_PROHIBITED_ROOM_TAGS,
                        talk -> talk.prevailingSpeakerProhibitedRoomTagCount() * talk.getDurationInMinutes());
    }

    private Constraint talkRequiredRoomTags(ConstraintFactory factory) {
        return factory.from(Talk.class)
                .filter(talk -> talk.missingRequiredRoomTagCount() > 0)
                .penalizeConfigurable(TALK_REQUIRED_ROOM_TAGS,
                        talk -> talk.missingRequiredRoomTagCount() * talk.getDurationInMinutes());
    }

    private Constraint talkProhibitedRoomTags(ConstraintFactory factory) {
        return factory.from(Talk.class)
                .filter(talk -> talk.prevailingProhibitedRoomTagCount() > 0)
                .penalizeConfigurable(TALK_PROHIBITED_ROOM_TAGS,
                        talk -> talk.prevailingProhibitedRoomTagCount() * talk.getDurationInMinutes());
    }

    // ************************************************************************
    // Medium constraints
    // ************************************************************************

    private Constraint publishedTimeslot(ConstraintFactory factory) {
        return factory.from(Talk.class)
                .filter(talk -> talk.getPublishedTimeslot() != null
                        && talk.getTimeslot() != talk.getPublishedTimeslot())
                .penalizeConfigurable(PUBLISHED_TIMESLOT);
    }

    // ************************************************************************
    // Soft constraints
    // ************************************************************************

    private Constraint publishedRoom(ConstraintFactory factory) {
        return factory.from(Talk.class)
                .filter(talk -> talk.getPublishedRoom() != null && talk.getRoom() != talk.getPublishedRoom())
                .penalizeConfigurable(PUBLISHED_ROOM);
    }

    private Constraint themeTrackConflict(ConstraintFactory factory) {
        throw new UnsupportedOperationException("Not yet implemented due to missing support for advanced joiners.");
//        return factory.fromUniquePair(Talk.class, intersecting(Talk::getThemeTrackTagSet))
//                // TODO Support joiner for time overlap
//                .filter(Talk::overlapsTime)
//                .penalizeConfigurable(THEME_TRACK_CONFLICT,
//                        (talk1, talk2) -> talk1.overlappingThemeTrackCount(talk2)
//                        * talk1.overlappingDurationInMinutes(talk2));
    }

    private Constraint themeTrackRoomStability(ConstraintFactory factory) {
        throw new UnsupportedOperationException("Not yet implemented due to missing support for advanced joiners.");
//        return factory.fromUniquePair(Talk.class,
//                equal(talk -> talk.getTimeslot().getStartDateTime().toLocalDate()),
//                intersecting(Talk::getThemeTrackTagSet))
//                // TODO Support joiner for time overlap
//                .filter(Talk::overlapsTime)
//                .filter((talk1, talk2) -> talk1.getRoom() != talk2.getRoom())
//                .penalizeConfigurable(THEME_TRACK_ROOM_STABILITY,
//                        (talk1, talk2) -> talk1.overlappingThemeTrackCount(talk2)
//                        * talk1.combinedDurationInMinutes(talk2));
    }

    private Constraint sectorConflict(ConstraintFactory factory) {
        throw new UnsupportedOperationException("Not yet implemented due to missing support for advanced joiners.");
//        return factory.fromUniquePair(Talk.class, intersecting(Talk::getSectorTagSet))
//                // TODO Support joiner for time overlap
//                .filter(Talk::overlapsTime)
//                .penalizeConfigurable(SECTOR_CONFLICT,
//                        (talk1, talk2) -> talk1.overlappingSectorCount(talk2)
//                        * talk1.overlappingDurationInMinutes(talk2));
    }

    private Constraint audienceTypeDiversity(ConstraintFactory factory) {
        throw new UnsupportedOperationException("Not yet implemented due to missing support for advanced joiners.");
//        return factory.fromUniquePair(Talk.class,
//                // Timeslot.overlaps() is deliberately not used
//                equal(Talk::getTimeslot),
//                intersecting(Talk::getAudienceTypeSet))
//                .rewardConfigurable(AUDIENCE_TYPE_DIVERSITY,
//                        (talk1, talk2) -> talk1.overlappingAudienceTypeCount(talk2)
//                        * talk1.getTimeslot().getDurationInMinutes());
    }

    private Constraint audienceTypeThemeTrackConflict(ConstraintFactory factory) {
        throw new UnsupportedOperationException("Not yet implemented due to missing support for advanced joiners.");
//        return factory.fromUniquePair(Talk.class,
//                intersecting(Talk::getThemeTrackTagSet),
//                intersecting(Talk::getAudienceTypeSet))
//                // TODO Support joiner for time overlap
//                .filter(Talk::overlapsTime)
//                .penalizeConfigurable(AUDIENCE_TYPE_THEME_TRACK_CONFLICT,
//                        (talk1, talk2) -> talk1.overlappingThemeTrackCount(talk2)
//                        * talk1.overlappingAudienceTypeCount(talk2)
//                        * talk1.overlappingDurationInMinutes(talk2));
    }

    private Constraint audienceLevelDiversity(ConstraintFactory factory) {
        return factory.fromUniquePair(Talk.class,
                // Timeslot.overlaps() is deliberately not used
                equal(Talk::getTimeslot))
                .filter((talk1, talk2) -> talk1.getAudienceLevel() != talk2.getAudienceLevel())
                .rewardConfigurable(AUDIENCE_LEVEL_DIVERSITY,
                        (talk1, talk2) -> talk1.getTimeslot().getDurationInMinutes());
    }

    private Constraint contentAudienceLevelFlowViolation(ConstraintFactory factory) {
        throw new UnsupportedOperationException();
//        return factory.from(Talk.class)
//                // fromUniquePair() not wanted due to lessThan(Talk::getAudienceLevel)
//                .join(Talk.class,
//                        intersecting(Talk::getContentTagSet),
//                        lessThan(Talk::getAudienceLevel),
//                        greaterThan(talk1 -> talk1.getTimeslot().getEndDateTime(),
//                                talk2 -> talk2.getTimeslot().getStartDateTime()))
//                .penalizeConfigurable(CONTENT_AUDIENCE_LEVEL_FLOW_VIOLATION,
//                        (talk1, talk2) -> talk1.overlappingContentCount(talk2)
//                        * talk1.combinedDurationInMinutes(talk2));
    }

    private Constraint contentConflict(ConstraintFactory factory) {
        throw new UnsupportedOperationException("Not yet implemented due to missing support for advanced joiners.");
//        return factory.fromUniquePair(Talk.class, intersecting(Talk::getContentTagSet))
//                // TODO Support joiner for time overlap
//                .filter(Talk::overlapsTime)
//                .penalizeConfigurable(CONTENT_CONFLICT,
//                        (talk1, talk2) -> talk1.overlappingContentCount(talk2)
//                        * talk1.overlappingDurationInMinutes(talk2));
    }

    private Constraint languageDiversity(ConstraintFactory factory) {
        return factory.fromUniquePair(Talk.class,
                // Timeslot.overlaps() is deliberately not used
                equal(Talk::getTimeslot))
                .filter((talk1, talk2) -> !talk1.getLanguage().equals(talk2.getLanguage()))
                .rewardConfigurable(LANGUAGE_DIVERSITY,
                        (talk1, talk2) -> talk1.getTimeslot().getDurationInMinutes());
    }

    private Constraint sameDayTalks(ConstraintFactory factory) {
        return factory.fromUniquePair(Talk.class)
                .filter((talk1, talk2) ->
                        (talk1.overlappingContentCount(talk2) > 0 || talk1.overlappingThemeTrackCount(talk2) > 0)
                        && !talk1.getTimeslot().getDate().equals(talk2.getTimeslot().getDate()))
                .penalizeConfigurable(SAME_DAY_TALKS,
                        (talk1, talk2) -> talk1.overlappingContentCount(talk2)
                        * talk1.overlappingThemeTrackCount(talk2)
                        * talk1.overlappingDurationInMinutes(talk2));
    }

    private Constraint popularTalks(ConstraintFactory factory) {
        return factory.from(Talk.class)
                // fromUniquePair() not wanted due to lessThan(Talk::getFavoriteCount)
                .join(Talk.class,
                        lessThan(Talk::getFavoriteCount),
                        greaterThan(talk -> talk.getRoom().getCapacity()))
                .penalizeConfigurable(POPULAR_TALKS,
                        Talk::combinedDurationInMinutes);
    }

    private Constraint speakerPreferredTimeslotTags(ConstraintFactory factory) {
        return factory.from(Talk.class)
                .filter(talk -> talk.missingSpeakerPreferredTimeslotTagCount() > 0)
                .penalizeConfigurable(SPEAKER_PREFERRED_TIMESLOT_TAGS,
                        talk -> talk.missingSpeakerPreferredTimeslotTagCount() * talk.getDurationInMinutes());
    }

    private Constraint speakerUndesiredTimeslotTags(ConstraintFactory factory) {
        return factory.from(Talk.class)
                .filter(talk -> talk.prevailingSpeakerUndesiredTimeslotTagCount() > 0)
                .penalizeConfigurable(SPEAKER_UNDESIRED_TIMESLOT_TAGS,
                        talk -> talk.prevailingSpeakerUndesiredTimeslotTagCount() * talk.getDurationInMinutes());
    }

    private Constraint talkPreferredTimeslotTags(ConstraintFactory factory) {
        return factory.from(Talk.class)
                .filter(talk -> talk.missingPreferredTimeslotTagCount() > 0)
                .penalizeConfigurable(TALK_PREFERRED_TIMESLOT_TAGS,
                        talk -> talk.missingPreferredTimeslotTagCount() * talk.getDurationInMinutes());
    }

    private Constraint talkUndesiredTimeslotTags(ConstraintFactory factory) {
        return factory.from(Talk.class)
                .filter(talk -> talk.prevailingUndesiredTimeslotTagCount() > 0)
                .penalizeConfigurable(TALK_UNDESIRED_TIMESLOT_TAGS,
                        talk -> talk.prevailingUndesiredTimeslotTagCount() * talk.getDurationInMinutes());
    }

    private Constraint speakerPreferredRoomTags(ConstraintFactory factory) {
        return factory.from(Talk.class)
                .filter(talk -> talk.missingSpeakerPreferredRoomTagCount() > 0)
                .penalizeConfigurable(SPEAKER_PREFERRED_ROOM_TAGS,
                        talk -> talk.missingSpeakerPreferredRoomTagCount() * talk.getDurationInMinutes());
    }

    private Constraint speakerUndesiredRoomTags(ConstraintFactory factory) {
        return factory.from(Talk.class)
                .filter(talk -> talk.prevailingSpeakerUndesiredRoomTagCount() > 0)
                .penalizeConfigurable(SPEAKER_UNDESIRED_ROOM_TAGS,
                        talk -> talk.prevailingSpeakerUndesiredRoomTagCount() * talk.getDurationInMinutes());
    }

    private Constraint talkPreferredRoomTags(ConstraintFactory factory) {
        return factory.from(Talk.class)
                .filter(talk -> talk.missingPreferredRoomTagCount() > 0)
                .penalizeConfigurable(TALK_PREFERRED_ROOM_TAGS,
                        talk -> talk.missingPreferredRoomTagCount() * talk.getDurationInMinutes());
    }

    private Constraint talkUndesiredRoomTags(ConstraintFactory factory) {
        return factory.from(Talk.class)
                .filter(talk -> talk.prevailingUndesiredRoomTagCount() > 0)
                .penalizeConfigurable(TALK_UNDESIRED_ROOM_TAGS,
                        talk -> talk.prevailingUndesiredRoomTagCount() * talk.getDurationInMinutes());
    }

}
