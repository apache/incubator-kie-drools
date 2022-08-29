package org.optaplanner.examples.conferencescheduling.score;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;

import org.optaplanner.examples.common.score.AbstractConstraintProviderTest;
import org.optaplanner.examples.common.score.ConstraintProviderTest;
import org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration;
import org.optaplanner.examples.conferencescheduling.domain.ConferenceSolution;
import org.optaplanner.examples.conferencescheduling.domain.Room;
import org.optaplanner.examples.conferencescheduling.domain.Speaker;
import org.optaplanner.examples.conferencescheduling.domain.Talk;
import org.optaplanner.examples.conferencescheduling.domain.Timeslot;
import org.optaplanner.test.api.score.stream.ConstraintVerifier;

class ConferenceSchedulingConstraintProviderTest
        extends AbstractConstraintProviderTest<ConferenceSchedulingConstraintProvider, ConferenceSolution> {

    private static final LocalDateTime START = LocalDateTime.of(2000, 2, 1, 9, 0);

    private static final Timeslot MONDAY_9_TO_10 = new Timeslot(1)
            .withStartDateTime(START)
            .withEndDateTime(START.plusHours(1))
            .withTagSet(singleton("a"));
    private static final Timeslot MONDAY_10_TO_11 = new Timeslot(2)
            .withStartDateTime(MONDAY_9_TO_10.getEndDateTime())
            .withEndDateTime(MONDAY_9_TO_10.getEndDateTime().plusHours(1))
            .withTagSet(singleton("b"));
    private static final Timeslot MONDAY_11_TO_12 = new Timeslot(3)
            .withStartDateTime(MONDAY_10_TO_11.getEndDateTime())
            .withEndDateTime(MONDAY_10_TO_11.getEndDateTime().plusHours(1))
            .withTagSet(singleton("c"));
    private static final Timeslot TUESDAY_9_TO_10 = new Timeslot(4)
            .withStartDateTime(START.plusDays(1))
            .withEndDateTime(START.plusDays(1).plusHours(1))
            .withTagSet(singleton("c"));

    // ************************************************************************
    // Hard constraints
    // ************************************************************************

    @ConstraintProviderTest
    void roomUnavailableTimeslot(
            ConstraintVerifier<ConferenceSchedulingConstraintProvider, ConferenceSolution> constraintVerifier) {
        Room room1 = new Room(1)
                .withUnavailableTimeslotSet(singleton(MONDAY_9_TO_10));
        Room room2 = new Room(2)
                .withUnavailableTimeslotSet(singleton(MONDAY_10_TO_11));
        Talk talk1 = new Talk(1)
                .withRoom(room1)
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk2 = new Talk(2)
                .withRoom(room2)
                .withTimeslot(MONDAY_9_TO_10);

        constraintVerifier.verifyThat(ConferenceSchedulingConstraintProvider::roomUnavailableTimeslot)
                .given(talk1, talk2)
                .penalizesBy(MONDAY_9_TO_10.getDurationInMinutes()); // room1 is in an unavailable timeslot.
    }

    @ConstraintProviderTest
    void roomConflict(ConstraintVerifier<ConferenceSchedulingConstraintProvider, ConferenceSolution> constraintVerifier) {
        Room room = new Room(1)
                .withUnavailableTimeslotSet(singleton(MONDAY_9_TO_10));
        Talk talk1 = new Talk(1)
                .withRoom(room)
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk2 = new Talk(2)
                .withRoom(room)
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk3 = new Talk(3)
                .withRoom(room)
                .withTimeslot(MONDAY_10_TO_11);

        constraintVerifier.verifyThat(ConferenceSchedulingConstraintProvider::roomConflict)
                .given(talk1, talk2, talk3)
                .penalizesBy(MONDAY_9_TO_10.getDurationInMinutes()); // talk1 and talk2 are in conflict.
    }

    @ConstraintProviderTest
    void speakerUnavailableTimeslot(
            ConstraintVerifier<ConferenceSchedulingConstraintProvider, ConferenceSolution> constraintVerifier) {
        Room room = new Room(0);
        Speaker speaker1 = new Speaker(1)
                .withUnavailableTimeslotSet(singleton(MONDAY_9_TO_10));
        Speaker speaker2 = new Speaker(2)
                .withUnavailableTimeslotSet(singleton(MONDAY_10_TO_11));
        Talk talk1 = new Talk(1)
                .withRoom(room)
                .withSpeakerList(singletonList(speaker1))
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk2 = new Talk(2)
                .withRoom(room)
                .withSpeakerList(singletonList(speaker2))
                .withTimeslot(MONDAY_9_TO_10);

        constraintVerifier.verifyThat(ConferenceSchedulingConstraintProvider::speakerUnavailableTimeslot)
                .given(talk1, talk2, speaker1, speaker2)
                .penalizesBy(MONDAY_9_TO_10.getDurationInMinutes()); // speaker1 is in an unavailable timeslot.
    }

    @ConstraintProviderTest
    void speakerConflict(ConstraintVerifier<ConferenceSchedulingConstraintProvider, ConferenceSolution> constraintVerifier) {
        Room room = new Room(0);
        Speaker speaker = new Speaker(1);
        Talk talk1 = new Talk(1)
                .withRoom(room)
                .withSpeakerList(singletonList(speaker))
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk2 = new Talk(2)
                .withRoom(room)
                .withSpeakerList(singletonList(speaker))
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk3 = new Talk(3)
                .withRoom(room)
                .withSpeakerList(singletonList(speaker))
                .withTimeslot(MONDAY_10_TO_11);

        constraintVerifier.verifyThat(ConferenceSchedulingConstraintProvider::speakerConflict)
                .given(speaker, talk1, talk2, talk3)
                .penalizesBy(MONDAY_9_TO_10.getDurationInMinutes()); // talk1 and talk2 are in conflict.
    }

    @ConstraintProviderTest
    void talkPrerequisiteTalks(
            ConstraintVerifier<ConferenceSchedulingConstraintProvider, ConferenceSolution> constraintVerifier) {
        Room room = new Room(0);
        Talk talk1 = new Talk(1)
                .withRoom(room)
                .withPrerequisiteTalksCodesSet(emptySet())
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk2 = new Talk(2)
                .withRoom(room)
                .withPrerequisiteTalksCodesSet(singleton(talk1))
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk3 = new Talk(3)
                .withRoom(room)
                .withPrerequisiteTalksCodesSet(singleton(talk1))
                .withTimeslot(MONDAY_10_TO_11);

        constraintVerifier.verifyThat(ConferenceSchedulingConstraintProvider::talkPrerequisiteTalks)
                .given(talk1, talk2, talk3)
                .penalizesBy(MONDAY_9_TO_10.getDurationInMinutes() * 2); // talk2 is not after talk1.
    }

    @ConstraintProviderTest
    void talkMutuallyExclusiveTalksTags(
            ConstraintVerifier<ConferenceSchedulingConstraintProvider, ConferenceSolution> constraintVerifier) {
        Room room = new Room(0);
        Talk talk1 = new Talk(1)
                .withRoom(room)
                .withMutuallyExclusiveTalksTagSet(emptySet())
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk2 = new Talk(2)
                .withRoom(room)
                .withMutuallyExclusiveTalksTagSet(new HashSet<>(Arrays.asList("a", "b")))
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk3 = new Talk(3)
                .withRoom(room)
                .withMutuallyExclusiveTalksTagSet(new HashSet<>(Arrays.asList("a", "b", "c")))
                .withTimeslot(MONDAY_9_TO_10);

        constraintVerifier.verifyThat(ConferenceSchedulingConstraintProvider::talkMutuallyExclusiveTalksTags)
                .given(talk1, talk2, talk3)
                .penalizesBy(MONDAY_9_TO_10.getDurationInMinutes() * 2); // talk2 and talk3 excluded twice.
    }

    @ConstraintProviderTest
    void consecutiveTalksPause(
            ConstraintVerifier<ConferenceSchedulingConstraintProvider, ConferenceSolution> constraintVerifier) {
        Room room = new Room(0);
        Speaker speaker1 = new Speaker(1);
        Speaker speaker2 = new Speaker(2);
        Talk talk1 = new Talk(1)
                .withRoom(room)
                .withSpeakerList(singletonList(speaker1))
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk2 = new Talk(2)
                .withRoom(room)
                .withSpeakerList(singletonList(speaker1))
                .withTimeslot(MONDAY_10_TO_11);
        Talk talk3 = new Talk(3)
                .withRoom(room)
                .withSpeakerList(singletonList(speaker1))
                .withTimeslot(MONDAY_11_TO_12);
        Talk talk4 = new Talk(4)
                .withRoom(room)
                .withSpeakerList(singletonList(speaker2))
                .withTimeslot(MONDAY_9_TO_10);
        ConferenceConstraintConfiguration configuration = new ConferenceConstraintConfiguration(0);
        configuration.setMinimumConsecutiveTalksPauseInMinutes(10);

        constraintVerifier.verifyThat(ConferenceSchedulingConstraintProvider::consecutiveTalksPause)
                .given(configuration, talk1, talk2, talk3, talk4)
                .penalizesBy(MONDAY_9_TO_10.getDurationInMinutes() * 4); // talk1+talk2 , talk2+talk3.
    }

    @ConstraintProviderTest
    void crowdControl(ConstraintVerifier<ConferenceSchedulingConstraintProvider, ConferenceSolution> constraintVerifier) {
        Room room = new Room(0);
        Talk talk1 = new Talk(1)
                .withRoom(room)
                .withTimeslot(MONDAY_9_TO_10);
        talk1.setCrowdControlRisk(1);
        Talk talk2 = new Talk(2)
                .withRoom(room)
                .withTimeslot(MONDAY_9_TO_10);
        talk2.setCrowdControlRisk(1);
        Talk talk3 = new Talk(3)
                .withRoom(room)
                .withTimeslot(MONDAY_9_TO_10);
        talk3.setCrowdControlRisk(1);
        Talk talk4 = new Talk(4)
                .withRoom(room)
                .withTimeslot(MONDAY_10_TO_11);
        talk4.setCrowdControlRisk(1);
        Talk talk5 = new Talk(5)
                .withRoom(room)
                .withTimeslot(MONDAY_10_TO_11);
        talk5.setCrowdControlRisk(1);
        Talk noRiskTalk = new Talk(6)
                .withRoom(room)
                .withTimeslot(MONDAY_10_TO_11);

        constraintVerifier.verifyThat(ConferenceSchedulingConstraintProvider::crowdControl)
                .given(talk1, talk2, talk3, talk4, talk5, noRiskTalk)
                .penalizesBy(MONDAY_9_TO_10.getDurationInMinutes() * 3); // talk1, talk2, talk3.
    }

    @ConstraintProviderTest
    void speakerRequiredTimeslotTags(
            ConstraintVerifier<ConferenceSchedulingConstraintProvider, ConferenceSolution> constraintVerifier) {
        Room room = new Room(0);
        Speaker speaker1 = new Speaker(1)
                .withRequiredTimeslotTagSet(singleton("a"));
        Speaker speaker2 = new Speaker(1)
                .withRequiredTimeslotTagSet(singleton("x"));
        Talk talk1 = new Talk(1)
                .withRoom(room)
                .withSpeakerList(singletonList(speaker1))
                .withRequiredTimeslotTagSet(new HashSet<>(Arrays.asList("a", "b")))
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk2 = new Talk(2)
                .withRoom(room)
                .withSpeakerList(singletonList(speaker2))
                .withRequiredTimeslotTagSet(emptySet())
                .withTimeslot(MONDAY_10_TO_11);

        constraintVerifier.verifyThat(ConferenceSchedulingConstraintProvider::speakerRequiredTimeslotTags)
                .given(talk1, talk2)
                .penalizesBy(MONDAY_10_TO_11.getDurationInMinutes());
    }

    @ConstraintProviderTest
    void speakerProhibitedTimeslotTags(
            ConstraintVerifier<ConferenceSchedulingConstraintProvider, ConferenceSolution> constraintVerifier) {
        Room room = new Room(0);
        Speaker speaker1 = new Speaker(1)
                .withProhibitedTimeslotTagSet(singleton("a"));
        Speaker speaker2 = new Speaker(1)
                .withProhibitedTimeslotTagSet(singleton("x"));
        Talk talk1 = new Talk(1)
                .withRoom(room)
                .withSpeakerList(singletonList(speaker1))
                .withProhibitedTimeslotTagSet(new HashSet<>(Arrays.asList("a", "b")))
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk2 = new Talk(2)
                .withRoom(room)
                .withSpeakerList(singletonList(speaker2))
                .withProhibitedTimeslotTagSet(emptySet())
                .withTimeslot(MONDAY_10_TO_11);

        constraintVerifier.verifyThat(ConferenceSchedulingConstraintProvider::speakerProhibitedTimeslotTags)
                .given(talk1, talk2)
                .penalizesBy(MONDAY_9_TO_10.getDurationInMinutes());
    }

    @ConstraintProviderTest
    void talkRequiredTimeslotTags(
            ConstraintVerifier<ConferenceSchedulingConstraintProvider, ConferenceSolution> constraintVerifier) {
        Room room = new Room(0);
        Talk talk1 = new Talk(1)
                .withRoom(room)
                .withRequiredTimeslotTagSet(new HashSet<>(Arrays.asList("a", "b")))
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk2 = new Talk(2)
                .withRoom(room)
                .withRequiredTimeslotTagSet(emptySet())
                .withTimeslot(MONDAY_10_TO_11);

        constraintVerifier.verifyThat(ConferenceSchedulingConstraintProvider::talkRequiredTimeslotTags)
                .given(talk1, talk2)
                .penalizesBy(MONDAY_10_TO_11.getDurationInMinutes());
    }

    @ConstraintProviderTest
    void talkProhibitedTimeslotTags(
            ConstraintVerifier<ConferenceSchedulingConstraintProvider, ConferenceSolution> constraintVerifier) {
        Room room = new Room(0);
        Talk talk1 = new Talk(1)
                .withRoom(room)
                .withProhibitedTimeslotTagSet(new HashSet<>(Arrays.asList("a", "b")))
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk2 = new Talk(2)
                .withRoom(room)
                .withProhibitedTimeslotTagSet(emptySet())
                .withTimeslot(MONDAY_10_TO_11);

        constraintVerifier.verifyThat(ConferenceSchedulingConstraintProvider::talkProhibitedTimeslotTags)
                .given(talk1, talk2)
                .penalizesBy(MONDAY_9_TO_10.getDurationInMinutes());
    }

    @ConstraintProviderTest
    void speakerRequiredRoomTags(
            ConstraintVerifier<ConferenceSchedulingConstraintProvider, ConferenceSolution> constraintVerifier) {
        Room room = new Room(0)
                .withTagSet(singleton("a"));
        Speaker speaker1 = new Speaker(1)
                .withRequiredRoomTagSet(singleton("a"));
        Speaker speaker2 = new Speaker(1)
                .withRequiredRoomTagSet(singleton("x"));
        Talk talk1 = new Talk(1)
                .withRoom(room)
                .withSpeakerList(singletonList(speaker1))
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk2 = new Talk(2)
                .withRoom(room)
                .withSpeakerList(singletonList(speaker2))
                .withTimeslot(MONDAY_10_TO_11);

        constraintVerifier.verifyThat(ConferenceSchedulingConstraintProvider::speakerRequiredRoomTags)
                .given(talk1, talk2)
                .penalizesBy(MONDAY_10_TO_11.getDurationInMinutes());
    }

    @ConstraintProviderTest
    void speakerProhibitedRoomTags(
            ConstraintVerifier<ConferenceSchedulingConstraintProvider, ConferenceSolution> constraintVerifier) {
        Room room = new Room(0)
                .withTagSet(singleton("a"));
        Speaker speaker1 = new Speaker(1)
                .withProhibitedRoomTagSet(singleton("a"));
        Speaker speaker2 = new Speaker(1)
                .withProhibitedRoomTagSet(singleton("x"));
        Talk talk1 = new Talk(1)
                .withRoom(room)
                .withSpeakerList(singletonList(speaker1))
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk2 = new Talk(2)
                .withRoom(room)
                .withSpeakerList(singletonList(speaker2))
                .withTimeslot(MONDAY_10_TO_11);

        constraintVerifier.verifyThat(ConferenceSchedulingConstraintProvider::speakerProhibitedRoomTags)
                .given(talk1, talk2)
                .penalizesBy(MONDAY_9_TO_10.getDurationInMinutes());
    }

    @ConstraintProviderTest
    void talkRequiredRoomTags(
            ConstraintVerifier<ConferenceSchedulingConstraintProvider, ConferenceSolution> constraintVerifier) {
        Room room = new Room(0)
                .withTagSet(singleton("a"));
        Talk talk1 = new Talk(1)
                .withRoom(room)
                .withRequiredRoomTagSet(new HashSet<>(Arrays.asList("a", "b")))
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk2 = new Talk(2)
                .withRoom(room)
                .withRequiredRoomTagSet(emptySet())
                .withTimeslot(MONDAY_10_TO_11);

        constraintVerifier.verifyThat(ConferenceSchedulingConstraintProvider::talkRequiredRoomTags)
                .given(talk1, talk2)
                .penalizesBy(MONDAY_10_TO_11.getDurationInMinutes());
    }

    @ConstraintProviderTest
    void talkProhibitedRoomTags(
            ConstraintVerifier<ConferenceSchedulingConstraintProvider, ConferenceSolution> constraintVerifier) {
        Room room = new Room(0)
                .withTagSet(singleton("a"));
        Talk talk1 = new Talk(1)
                .withRoom(room)
                .withProhibitedRoomTagSet(new HashSet<>(Arrays.asList("a", "b")))
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk2 = new Talk(2)
                .withRoom(room)
                .withProhibitedRoomTagSet(emptySet())
                .withTimeslot(MONDAY_10_TO_11);

        constraintVerifier.verifyThat(ConferenceSchedulingConstraintProvider::talkProhibitedRoomTags)
                .given(talk1, talk2)
                .penalizesBy(MONDAY_9_TO_10.getDurationInMinutes());
    }

    // ************************************************************************
    // Medium constraints
    // ************************************************************************

    @ConstraintProviderTest
    void publishedTimeslot(ConstraintVerifier<ConferenceSchedulingConstraintProvider, ConferenceSolution> constraintVerifier) {
        Room room = new Room(0);
        Talk talk1 = new Talk(1)
                .withRoom(room)
                .withTimeslot(MONDAY_9_TO_10);
        talk1.setPublishedTimeslot(MONDAY_9_TO_10);
        Talk talk2 = new Talk(2)
                .withRoom(room)
                .withTimeslot(MONDAY_10_TO_11);
        talk2.setPublishedTimeslot(MONDAY_9_TO_10);

        constraintVerifier.verifyThat(ConferenceSchedulingConstraintProvider::publishedTimeslot)
                .given(talk1, talk2)
                .penalizesBy(1);
    }

    // ************************************************************************
    // Soft constraints
    // ************************************************************************

    @ConstraintProviderTest
    void publishedRoom(ConstraintVerifier<ConferenceSchedulingConstraintProvider, ConferenceSolution> constraintVerifier) {
        Room room1 = new Room(0);
        Room room2 = new Room(1);
        Talk talk1 = new Talk(1)
                .withRoom(room1)
                .withTimeslot(MONDAY_9_TO_10);
        talk1.setPublishedRoom(room1);
        Talk talk2 = new Talk(2)
                .withRoom(room1)
                .withTimeslot(MONDAY_10_TO_11);
        talk2.setPublishedRoom(room2);

        constraintVerifier.verifyThat(ConferenceSchedulingConstraintProvider::publishedRoom)
                .given(talk1, talk2)
                .penalizesBy(1);
    }

    @ConstraintProviderTest
    void themeTrackConflict(ConstraintVerifier<ConferenceSchedulingConstraintProvider, ConferenceSolution> constraintVerifier) {
        Room room = new Room(0);
        Talk talk1 = new Talk(1)
                .withRoom(room)
                .withThemeTrackTagSet(singleton("a"))
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk2 = new Talk(2)
                .withRoom(room)
                .withThemeTrackTagSet(singleton("a"))
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk3 = new Talk(3)
                .withRoom(room)
                .withThemeTrackTagSet(singleton("b"))
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk4 = new Talk(4)
                .withRoom(room)
                .withThemeTrackTagSet(singleton("a"))
                .withTimeslot(MONDAY_10_TO_11);

        constraintVerifier.verifyThat(ConferenceSchedulingConstraintProvider::themeTrackConflict)
                .given(talk1, talk2, talk3, talk4)
                .penalizesBy(60); // talk1 + talk2.
    }

    @ConstraintProviderTest
    void themeTrackRoomStability(
            ConstraintVerifier<ConferenceSchedulingConstraintProvider, ConferenceSolution> constraintVerifier) {
        Room room1 = new Room(0);
        Room room2 = new Room(1);
        Talk talk1 = new Talk(1)
                .withRoom(room1)
                .withThemeTrackTagSet(singleton("a"))
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk2 = new Talk(2)
                .withRoom(room2)
                .withThemeTrackTagSet(singleton("a"))
                .withTimeslot(MONDAY_10_TO_11);
        Talk talk3 = new Talk(3)
                .withRoom(room1)
                .withThemeTrackTagSet(singleton("b"))
                .withTimeslot(MONDAY_11_TO_12);
        Talk talk4 = new Talk(4)
                .withRoom(room2)
                .withThemeTrackTagSet(singleton("a"))
                .withTimeslot(TUESDAY_9_TO_10);

        constraintVerifier.verifyThat(ConferenceSchedulingConstraintProvider::themeTrackRoomStability)
                .given(talk1, talk2, talk3, talk4)
                .penalizesBy(120); // talk1 + talk2.
    }

    @ConstraintProviderTest
    void sectorConflict(ConstraintVerifier<ConferenceSchedulingConstraintProvider, ConferenceSolution> constraintVerifier) {
        Room room = new Room(0);
        Talk talk1 = new Talk(1)
                .withRoom(room)
                .withSectorTagSet(singleton("a"))
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk2 = new Talk(2)
                .withRoom(room)
                .withSectorTagSet(singleton("a"))
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk3 = new Talk(3)
                .withRoom(room)
                .withSectorTagSet(singleton("b"))
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk4 = new Talk(4)
                .withRoom(room)
                .withSectorTagSet(singleton("a"))
                .withTimeslot(MONDAY_10_TO_11);

        constraintVerifier.verifyThat(ConferenceSchedulingConstraintProvider::sectorConflict)
                .given(talk1, talk2, talk3, talk4)
                .penalizesBy(60); // talk1 + talk2.
    }

    @ConstraintProviderTest
    void audienceTypeDiversity(
            ConstraintVerifier<ConferenceSchedulingConstraintProvider, ConferenceSolution> constraintVerifier) {
        Room room = new Room(0);
        Talk talk1 = new Talk(1)
                .withRoom(room)
                .withAudienceTypeSet(singleton("a"))
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk2 = new Talk(2)
                .withRoom(room)
                .withAudienceTypeSet(singleton("a"))
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk3 = new Talk(3)
                .withRoom(room)
                .withAudienceTypeSet(singleton("b"))
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk4 = new Talk(4)
                .withRoom(room)
                .withAudienceTypeSet(singleton("a"))
                .withTimeslot(MONDAY_10_TO_11);

        constraintVerifier.verifyThat(ConferenceSchedulingConstraintProvider::audienceTypeDiversity)
                .given(talk1, talk2, talk3, talk4)
                .rewardsWith(60); // talk1 + talk2.
    }

    @ConstraintProviderTest
    void audienceTypeThemeTrackConflict(
            ConstraintVerifier<ConferenceSchedulingConstraintProvider, ConferenceSolution> constraintVerifier) {
        Room room = new Room(0);
        Talk talk1 = new Talk(1)
                .withRoom(room)
                .withAudienceTypeSet(singleton("a"))
                .withThemeTrackTagSet(singleton("b"))
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk2 = new Talk(2)
                .withRoom(room)
                .withAudienceTypeSet(singleton("a"))
                .withThemeTrackTagSet(singleton("a"))
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk3 = new Talk(3)
                .withRoom(room)
                .withAudienceTypeSet(singleton("b"))
                .withThemeTrackTagSet(singleton("a"))
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk4 = new Talk(4)
                .withRoom(room)
                .withAudienceTypeSet(singleton("a"))
                .withThemeTrackTagSet(singleton("a"))
                .withTimeslot(MONDAY_10_TO_11);
        Talk talk5 = new Talk(5)
                .withRoom(room)
                .withAudienceTypeSet(singleton("a"))
                .withThemeTrackTagSet(singleton("b"))
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk6 = new Talk(6)
                .withRoom(room)
                .withAudienceTypeSet(singleton("a"))
                .withThemeTrackTagSet(singleton("c"))
                .withTimeslot(MONDAY_9_TO_10);

        constraintVerifier.verifyThat(ConferenceSchedulingConstraintProvider::audienceTypeThemeTrackConflict)
                .given(talk1, talk2, talk3, talk4, talk5, talk6)
                .penalizesBy(60); // talk1 + talk2.
    }

    @ConstraintProviderTest
    void audienceLevelDiversity(
            ConstraintVerifier<ConferenceSchedulingConstraintProvider, ConferenceSolution> constraintVerifier) {
        Room room = new Room(0);
        Talk talk1 = new Talk(1)
                .withRoom(room)
                .withAudienceLevel(1)
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk2 = new Talk(2)
                .withRoom(room)
                .withAudienceLevel(1)
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk3 = new Talk(3)
                .withRoom(room)
                .withAudienceLevel(2)
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk4 = new Talk(4)
                .withRoom(room)
                .withAudienceLevel(1)
                .withTimeslot(MONDAY_10_TO_11);

        constraintVerifier.verifyThat(ConferenceSchedulingConstraintProvider::audienceLevelDiversity)
                .given(talk1, talk2, talk3, talk4)
                .rewardsWith(120); // talk1 + talk2 v. talk3.
    }

    @ConstraintProviderTest
    void contentAudienceLevelFlowViolation(
            ConstraintVerifier<ConferenceSchedulingConstraintProvider, ConferenceSolution> constraintVerifier) {
        Room room = new Room(0);
        Talk talk1 = new Talk(1)
                .withRoom(room)
                .withAudienceLevel(1)
                .withContentTagSet(singleton("a"))
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk2 = new Talk(2)
                .withRoom(room)
                .withAudienceLevel(2)
                .withContentTagSet(singleton("a"))
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk3 = new Talk(3)
                .withRoom(room)
                .withAudienceLevel(3)
                .withContentTagSet(singleton("b"))
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk4 = new Talk(4)
                .withRoom(room)
                .withAudienceLevel(1)
                .withContentTagSet(singleton("a"))
                .withTimeslot(MONDAY_10_TO_11);

        constraintVerifier.verifyThat(ConferenceSchedulingConstraintProvider::contentAudienceLevelFlowViolation)
                .given(talk1, talk2, talk3, talk4)
                .penalizesBy(240); // talk1 + talk2, talk2 + talk1, talk2 + talk4, talk4 + talk2.
    }

    @ConstraintProviderTest
    void contentConflict(ConstraintVerifier<ConferenceSchedulingConstraintProvider, ConferenceSolution> constraintVerifier) {
        Room room = new Room(0);
        Talk talk1 = new Talk(1)
                .withRoom(room)
                .withContentTagSet(singleton("a"))
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk2 = new Talk(2)
                .withRoom(room)
                .withContentTagSet(singleton("a"))
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk3 = new Talk(3)
                .withRoom(room)
                .withContentTagSet(singleton("b"))
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk4 = new Talk(4)
                .withRoom(room)
                .withContentTagSet(singleton("a"))
                .withTimeslot(MONDAY_10_TO_11);

        constraintVerifier.verifyThat(ConferenceSchedulingConstraintProvider::contentConflict)
                .given(talk1, talk2, talk3, talk4)
                .penalizesBy(60); // talk1 + talk2.
    }

    @ConstraintProviderTest
    void languageDiversity(ConstraintVerifier<ConferenceSchedulingConstraintProvider, ConferenceSolution> constraintVerifier) {
        Room room = new Room(0);
        Talk talk1 = new Talk(1)
                .withRoom(room)
                .withLanguage("a")
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk2 = new Talk(2)
                .withRoom(room)
                .withLanguage("a")
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk3 = new Talk(3)
                .withRoom(room)
                .withLanguage("b")
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk4 = new Talk(4)
                .withRoom(room)
                .withLanguage("a")
                .withTimeslot(MONDAY_10_TO_11);

        constraintVerifier.verifyThat(ConferenceSchedulingConstraintProvider::languageDiversity)
                .given(talk1, talk2, talk3, talk4)
                .rewardsWith(120); // talk1 + talk3.
    }

    @ConstraintProviderTest
    void sameDayTalks(ConstraintVerifier<ConferenceSchedulingConstraintProvider, ConferenceSolution> constraintVerifier) {
        Room room = new Room(0);
        Talk talk1 = new Talk(1)
                .withRoom(room)
                .withContentTagSet(singleton("a"))
                .withThemeTrackTagSet(singleton("a"))
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk2 = new Talk(3)
                .withRoom(room)
                .withContentTagSet(singleton("b"))
                .withThemeTrackTagSet(singleton("a"))
                .withTimeslot(TUESDAY_9_TO_10);
        Talk talk3 = new Talk(4)
                .withRoom(room)
                .withContentTagSet(singleton("a"))
                .withThemeTrackTagSet(singleton("a"))
                .withTimeslot(TUESDAY_9_TO_10);
        Talk talk4 = new Talk(5)
                .withRoom(room)
                .withContentTagSet(singleton("a"))
                .withThemeTrackTagSet(singleton("b"))
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk5 = new Talk(7)
                .withRoom(room)
                .withContentTagSet(singleton("b"))
                .withThemeTrackTagSet(singleton("b"))
                .withTimeslot(TUESDAY_9_TO_10);
        Talk talk6 = new Talk(8)
                .withRoom(room)
                .withContentTagSet(singleton("a"))
                .withThemeTrackTagSet(singleton("b"))
                .withTimeslot(TUESDAY_9_TO_10);

        constraintVerifier.verifyThat(ConferenceSchedulingConstraintProvider::sameDayTalks)
                .given(talk1, talk2, talk3, talk4, talk5, talk6)
                .penalizesBy(960);
    }

    @ConstraintProviderTest
    void popularTalks(ConstraintVerifier<ConferenceSchedulingConstraintProvider, ConferenceSolution> constraintVerifier) {
        Room smallerRoom = new Room(0)
                .withCapacity(10);
        Room biggerRoom = new Room(1)
                .withCapacity(20);
        Talk talk1 = new Talk(1)
                .withRoom(smallerRoom)
                .withFavoriteCount(2)
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk2 = new Talk(2)
                .withRoom(biggerRoom)
                .withFavoriteCount(2)
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk3 = new Talk(3)
                .withRoom(biggerRoom)
                .withFavoriteCount(1)
                .withTimeslot(MONDAY_9_TO_10);

        constraintVerifier.verifyThat(ConferenceSchedulingConstraintProvider::popularTalks)
                .given(talk1, talk2, talk3)
                .penalizesBy(120); // talk1 + talk3
    }

    @ConstraintProviderTest
    void speakerPreferredTimeslotTags(
            ConstraintVerifier<ConferenceSchedulingConstraintProvider, ConferenceSolution> constraintVerifier) {
        Room room = new Room(0);
        Speaker speaker1 = new Speaker(1)
                .withPreferredTimeslotTagSet(singleton("a"));
        Speaker speaker2 = new Speaker(1)
                .withPreferredTimeslotTagSet(singleton("x"));
        Talk talk1 = new Talk(1)
                .withRoom(room)
                .withSpeakerList(singletonList(speaker1))
                .withPreferredTimeslotTagSet(new HashSet<>(Arrays.asList("a", "b")))
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk2 = new Talk(2)
                .withRoom(room)
                .withSpeakerList(singletonList(speaker2))
                .withPreferredTimeslotTagSet(emptySet())
                .withTimeslot(MONDAY_10_TO_11);

        constraintVerifier.verifyThat(ConferenceSchedulingConstraintProvider::speakerPreferredTimeslotTags)
                .given(talk1, talk2)
                .penalizesBy(MONDAY_10_TO_11.getDurationInMinutes());
    }

    @ConstraintProviderTest
    void speakerUndesiredTimeslotTags(
            ConstraintVerifier<ConferenceSchedulingConstraintProvider, ConferenceSolution> constraintVerifier) {
        Room room = new Room(0);
        Speaker speaker1 = new Speaker(1)
                .withUndesiredTimeslotTagSet(singleton("a"));
        Speaker speaker2 = new Speaker(1)
                .withUndesiredTimeslotTagSet(singleton("x"));
        Talk talk1 = new Talk(1)
                .withRoom(room)
                .withSpeakerList(singletonList(speaker1))
                .withUndesiredTimeslotTagSet(new HashSet<>(Arrays.asList("a", "b")))
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk2 = new Talk(2)
                .withRoom(room)
                .withSpeakerList(singletonList(speaker2))
                .withUndesiredTimeslotTagSet(emptySet())
                .withTimeslot(MONDAY_10_TO_11);

        constraintVerifier.verifyThat(ConferenceSchedulingConstraintProvider::speakerUndesiredTimeslotTags)
                .given(talk1, talk2)
                .penalizesBy(MONDAY_9_TO_10.getDurationInMinutes());
    }

    @ConstraintProviderTest
    void talkPreferredTimeslotTags(
            ConstraintVerifier<ConferenceSchedulingConstraintProvider, ConferenceSolution> constraintVerifier) {
        Room room = new Room(0);
        Talk talk1 = new Talk(1)
                .withRoom(room)
                .withPreferredTimeslotTagSet(new HashSet<>(Arrays.asList("a", "b")))
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk2 = new Talk(2)
                .withRoom(room)
                .withPreferredTimeslotTagSet(emptySet())
                .withTimeslot(MONDAY_10_TO_11);

        constraintVerifier.verifyThat(ConferenceSchedulingConstraintProvider::talkPreferredTimeslotTags)
                .given(talk1, talk2)
                .penalizesBy(MONDAY_10_TO_11.getDurationInMinutes());
    }

    @ConstraintProviderTest
    void talkUndesiredTimeslotTags(
            ConstraintVerifier<ConferenceSchedulingConstraintProvider, ConferenceSolution> constraintVerifier) {
        Room room = new Room(0);
        Talk talk1 = new Talk(1)
                .withRoom(room)
                .withUndesiredTimeslotTagSet(new HashSet<>(Arrays.asList("a", "b")))
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk2 = new Talk(2)
                .withRoom(room)
                .withUndesiredTimeslotTagSet(emptySet())
                .withTimeslot(MONDAY_10_TO_11);

        constraintVerifier.verifyThat(ConferenceSchedulingConstraintProvider::talkUndesiredTimeslotTags)
                .given(talk1, talk2)
                .penalizesBy(MONDAY_9_TO_10.getDurationInMinutes());
    }

    @ConstraintProviderTest
    void speakerPreferredRoomTags(
            ConstraintVerifier<ConferenceSchedulingConstraintProvider, ConferenceSolution> constraintVerifier) {
        Room room = new Room(0)
                .withTagSet(singleton("a"));
        Speaker speaker1 = new Speaker(1)
                .withPreferredRoomTagSet(singleton("a"));
        Speaker speaker2 = new Speaker(1)
                .withPreferredRoomTagSet(singleton("x"));
        Talk talk1 = new Talk(1)
                .withRoom(room)
                .withSpeakerList(singletonList(speaker1))
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk2 = new Talk(2)
                .withRoom(room)
                .withSpeakerList(singletonList(speaker2))
                .withTimeslot(MONDAY_10_TO_11);

        constraintVerifier.verifyThat(ConferenceSchedulingConstraintProvider::speakerPreferredRoomTags)
                .given(talk1, talk2)
                .penalizesBy(MONDAY_10_TO_11.getDurationInMinutes());
    }

    @ConstraintProviderTest
    void speakerUndesiredRoomTags(
            ConstraintVerifier<ConferenceSchedulingConstraintProvider, ConferenceSolution> constraintVerifier) {
        Room room = new Room(0)
                .withTagSet(singleton("a"));
        Speaker speaker1 = new Speaker(1)
                .withUndesiredRoomTagSet(singleton("a"));
        Speaker speaker2 = new Speaker(1)
                .withUndesiredRoomTagSet(singleton("x"));
        Talk talk1 = new Talk(1)
                .withRoom(room)
                .withSpeakerList(singletonList(speaker1))
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk2 = new Talk(2)
                .withRoom(room)
                .withSpeakerList(singletonList(speaker2))
                .withTimeslot(MONDAY_10_TO_11);

        constraintVerifier.verifyThat(ConferenceSchedulingConstraintProvider::speakerUndesiredRoomTags)
                .given(talk1, talk2)
                .penalizesBy(MONDAY_9_TO_10.getDurationInMinutes());
    }

    @ConstraintProviderTest
    void talkPreferredRoomTags(
            ConstraintVerifier<ConferenceSchedulingConstraintProvider, ConferenceSolution> constraintVerifier) {
        Room room = new Room(0)
                .withTagSet(singleton("a"));
        Talk talk1 = new Talk(1)
                .withRoom(room)
                .withPreferredRoomTagSet(new HashSet<>(Arrays.asList("a", "b")))
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk2 = new Talk(2)
                .withRoom(room)
                .withPreferredRoomTagSet(emptySet())
                .withTimeslot(MONDAY_10_TO_11);

        constraintVerifier.verifyThat(ConferenceSchedulingConstraintProvider::talkPreferredRoomTags)
                .given(talk1, talk2)
                .penalizesBy(MONDAY_10_TO_11.getDurationInMinutes());
    }

    @ConstraintProviderTest
    void talkUndesiredRoomTags(
            ConstraintVerifier<ConferenceSchedulingConstraintProvider, ConferenceSolution> constraintVerifier) {
        Room room = new Room(0)
                .withTagSet(singleton("a"));
        Talk talk1 = new Talk(1)
                .withRoom(room)
                .withUndesiredRoomTagSet(new HashSet<>(Arrays.asList("a", "b")))
                .withTimeslot(MONDAY_9_TO_10);
        Talk talk2 = new Talk(2)
                .withRoom(room)
                .withUndesiredRoomTagSet(emptySet())
                .withTimeslot(MONDAY_10_TO_11);

        constraintVerifier.verifyThat(ConferenceSchedulingConstraintProvider::talkUndesiredRoomTags)
                .given(talk1, talk2)
                .penalizesBy(MONDAY_9_TO_10.getDurationInMinutes());
    }

    @Override
    protected ConstraintVerifier<ConferenceSchedulingConstraintProvider, ConferenceSolution> createConstraintVerifier() {
        return ConstraintVerifier.build(new ConferenceSchedulingConstraintProvider(), ConferenceSolution.class, Talk.class);
    }
}
