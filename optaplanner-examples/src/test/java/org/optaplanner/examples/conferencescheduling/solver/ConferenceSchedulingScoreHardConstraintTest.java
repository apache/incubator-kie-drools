package org.optaplanner.examples.conferencescheduling.solver;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import org.junit.Test;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.examples.conferencescheduling.app.ConferenceSchedulingApp;
import org.optaplanner.examples.conferencescheduling.domain.ConferenceParametrization;
import org.optaplanner.examples.conferencescheduling.domain.ConferenceSolution;
import org.optaplanner.examples.conferencescheduling.domain.Room;
import org.optaplanner.examples.conferencescheduling.domain.Speaker;
import org.optaplanner.examples.conferencescheduling.domain.Talk;
import org.optaplanner.examples.conferencescheduling.domain.TalkType;
import org.optaplanner.examples.conferencescheduling.domain.Timeslot;
import org.optaplanner.test.impl.score.buildin.hardmediumsoft.HardMediumSoftScoreVerifier;

import static org.optaplanner.examples.conferencescheduling.domain.ConferenceParametrization.*;

public class ConferenceSchedulingScoreHardConstraintTest {

    private HardMediumSoftScoreVerifier<ConferenceSolution> scoreVerifier = new HardMediumSoftScoreVerifier<>(
            SolverFactory.createFromXmlResource(ConferenceSchedulingApp.SOLVER_CONFIG));

    @Test
    public void talkTypeOfTimeSlot() {
        Talk talk1 = createTalk(1L);
        Talk talk2 = createTalk(2L);
        Timeslot slot1 = new Timeslot(1L)
                .withStartDateTime(LocalDateTime.of(2018, 1, 1, 9, 0))
                .withEndDateTime(LocalDateTime.of(2018, 1, 1, 10, 0));
        Timeslot slot2 = new Timeslot(2L)
                .withStartDateTime(LocalDateTime.of(2018, 1, 1, 9, 0))
                .withEndDateTime(LocalDateTime.of(2018, 1, 1, 10, 0));
        TalkType talkType1 = new TalkType(0L, "type1");
        TalkType talkType2 = new TalkType(1L, "type2");
        ConferenceSolution solution = new ConferenceSolution(1L)
                .withParametrization(new ConferenceParametrization(1L))
                .withTalkTypeList(Arrays.asList(talkType1, talkType2))
                .withTalkList(Arrays.asList(talk1, talk2))
                .withTimeslotList(Arrays.asList(slot1, slot2))
                .withRoomList(Collections.emptyList())
                .withSpeakerList(Collections.emptyList());
        scoreVerifier.assertHardWeight(TALK_TYPE_OF_TIMESLOT, 0, solution);
        // time slot with matching talk type
        talk1.withTalkType(talkType1).withTimeslot(slot1);
        slot1.setTalkTypeSet(Collections.singleton(talkType1));
        scoreVerifier.assertHardWeight(TALK_TYPE_OF_TIMESLOT, 0, solution);
        // time slot with non matching talk type
        talk2.withTalkType(talkType2).withTimeslot(slot2);
        slot2.setTalkTypeSet(Collections.singleton(talkType1));
        scoreVerifier.assertHardWeight(TALK_TYPE_OF_TIMESLOT, -10000, solution);
    }

    @Test
    public void talkHasUnavailableRoom() {
        TalkType talkType = new TalkType(0L, "type1");
        Talk talk1 = createTalk(1L).withTalkType(talkType);
        Talk talk2 = createTalk(2L).withTalkType(talkType);
        Timeslot slot1 = new Timeslot(1L).withTalkTypeSet(Collections.singleton(talkType));
        Timeslot slot2 = new Timeslot(2L).withTalkTypeSet(Collections.singleton(talkType));
        Room room1 = new Room(1L).withTalkTypeSet(Collections.emptySet());
        Room room2 = new Room(2L).withTalkTypeSet(Collections.emptySet());
        ConferenceSolution solution = new ConferenceSolution(1L)
                .withParametrization(new ConferenceParametrization(1L))
                .withTalkTypeList(Collections.singletonList(talkType))
                .withTalkList(Arrays.asList(talk1, talk2))
                .withTimeslotList(Arrays.asList(slot1, slot2))
                .withRoomList(Arrays.asList(room1, room2))
                .withSpeakerList(Collections.emptyList());
        scoreVerifier.assertHardWeight(ROOM_UNAVAILABLE_TIMESLOT, 0, solution);
        // talk with available room
        room1.setUnavailableTimeslotSet(Collections.emptySet());
        talk1.withTimeslot(slot1).withRoom(room1);
        scoreVerifier.assertHardWeight(ROOM_UNAVAILABLE_TIMESLOT, 0, solution);
        room1.setUnavailableTimeslotSet(new HashSet<>(Arrays.asList(slot2)));
        scoreVerifier.assertHardWeight(ROOM_UNAVAILABLE_TIMESLOT, 0, solution);
        // talk with room with unavailable time slot
        room1.setUnavailableTimeslotSet(new HashSet<>(Arrays.asList(slot1)));
        talk1.withTimeslot(slot1).withRoom(room1);
        scoreVerifier.assertHardWeight(ROOM_UNAVAILABLE_TIMESLOT, -10000, solution);
        room1.setUnavailableTimeslotSet(new HashSet<>(Arrays.asList(slot1, slot2)));
        talk1.withTimeslot(slot1).withRoom(room1);
        scoreVerifier.assertHardWeight(ROOM_UNAVAILABLE_TIMESLOT, -10000, solution);
    }

    @Test
    public void roomConflict() {
        TalkType talkType = new TalkType(0L, "type1");
        Talk talk1 = createTalk(1L).withTalkType(talkType);
        Talk talk2 = createTalk(2L).withTalkType(talkType);
        LocalDateTime start1 = LocalDateTime.of(2018, 1, 1, 9, 0);
        LocalDateTime end1 = LocalDateTime.of(2018, 1, 1, 10, 0);
        LocalDateTime start2 = LocalDateTime.of(2018, 1, 1, 9, 30);
        LocalDateTime end2 = LocalDateTime.of(2018, 1, 1, 10, 30);
        LocalDateTime start3 = LocalDateTime.of(2018, 1, 1, 10, 0);
        LocalDateTime end3 = LocalDateTime.of(2018, 1, 1, 11, 0);
        Timeslot slot1 = new Timeslot(1L)
                .withTalkTypeSet(Collections.singleton(talkType))
                .withStartDateTime(start1)
                .withEndDateTime(end1);
        Timeslot slot2 = new Timeslot(2L)
                .withTalkTypeSet(Collections.singleton(talkType))
                .withStartDateTime(start2)
                .withEndDateTime(end2);
        Timeslot slot3 = new Timeslot(3L)
                .withTalkTypeSet(Collections.singleton(talkType))
                .withStartDateTime(start3)
                .withEndDateTime(end3);
        Room room1 = new Room(1L)
                .withTalkTypeSet(Collections.singleton(talkType))
                .withUnavailableTimeslotSet(Collections.emptySet());
        Room room2 = new Room(2L)
                .withTalkTypeSet(Collections.singleton(talkType))
                .withUnavailableTimeslotSet(Collections.emptySet());
        ConferenceSolution solution = new ConferenceSolution(1L)
                .withParametrization(new ConferenceParametrization(1L))
                .withTalkTypeList(Collections.singletonList(talkType))
                .withTalkList(Arrays.asList(talk1, talk2))
                .withTimeslotList(Arrays.asList(slot1, slot2, slot3))
                .withRoomList(Arrays.asList(room1, room2))
                .withSpeakerList(Collections.emptyList());
        scoreVerifier.assertHardWeight(ROOM_CONFLICT, 0, solution);
        // talks in same room without overlapping time slots
        talk1.withRoom(room1).withTimeslot(slot1);
        talk2.withRoom(room1).withTimeslot(slot3);
        scoreVerifier.assertHardWeight(ROOM_CONFLICT, 0, solution);
        // talks in same room with overlapping time slots
        talk1.withRoom(room2).withTimeslot(slot1);
        talk2.withRoom(room2).withTimeslot(slot2);
        scoreVerifier.assertHardWeight(ROOM_CONFLICT, -10, solution);
        // talks in different room with overlapping time slots
        talk1.withRoom(room1).withTimeslot(slot1);
        talk2.withRoom(room2).withTimeslot(slot2);
        scoreVerifier.assertHardWeight(ROOM_CONFLICT, 0, solution);
    }

    @Test
    public void talkWithUnavailableSpeaker() {
        TalkType talkType = new TalkType(0L, "type1");
        Speaker speaker1 = new Speaker(1L)
                .withUnavailableTimeslotSet(Collections.emptySet())
                .withRequiredTimeslotTagSet(Collections.emptySet())
                .withPreferredTimeslotTagSet(Collections.emptySet())
                .withProhibitedTimeslotTagSet(Collections.emptySet())
                .withUndesiredTimeslotTagSet(Collections.emptySet());
        Speaker speaker2 = new Speaker(2L)
                .withUnavailableTimeslotSet(Collections.emptySet())
                .withRequiredTimeslotTagSet(Collections.emptySet())
                .withPreferredTimeslotTagSet(Collections.emptySet())
                .withProhibitedTimeslotTagSet(Collections.emptySet())
                .withUndesiredTimeslotTagSet(Collections.emptySet());
        Timeslot slot1 = new Timeslot(1L).withTalkTypeSet(Collections.singleton(talkType));
        Timeslot slot2 = new Timeslot(1L).withTalkTypeSet(Collections.singleton(talkType));
        Talk talk1 = createTalk(1L).withTalkType(talkType);

        ConferenceSolution solution = new ConferenceSolution(1L)
                .withParametrization(new ConferenceParametrization(1L))
                .withTalkTypeList(Collections.singletonList(talkType))
                .withTalkList(Arrays.asList(talk1))
                .withTimeslotList(Arrays.asList(slot1))
                .withRoomList(Collections.emptyList())
                .withSpeakerList(Arrays.asList(speaker1, speaker2));
        scoreVerifier.assertHardWeight(SPEAKER_UNAVAILABLE_TIMESLOT, 0, solution);
        // talk without unavailable speaker
        talk1.withSpeakerList(Arrays.asList(speaker1)).withTimeslot(slot1);
        speaker1.setUnavailableTimeslotSet(Collections.emptySet());
        scoreVerifier.assertHardWeight(SPEAKER_UNAVAILABLE_TIMESLOT, 0, solution);
        speaker1.setUnavailableTimeslotSet(new HashSet<>(Arrays.asList(slot2)));
        scoreVerifier.assertHardWeight(SPEAKER_UNAVAILABLE_TIMESLOT, 0, solution);
        // talk with 2 speakers
        talk1.withSpeakerList(Arrays.asList(speaker1, speaker2)).withTimeslot(slot1);
        scoreVerifier.assertHardWeight(SPEAKER_UNAVAILABLE_TIMESLOT, 0, solution);
        speaker2.setUnavailableTimeslotSet(new HashSet<>(Arrays.asList(slot2)));
        scoreVerifier.assertHardWeight(SPEAKER_UNAVAILABLE_TIMESLOT, 0, solution);
        // talk with 1 or more unavailable speakers
        talk1.withSpeakerList(Arrays.asList(speaker1)).withTimeslot(slot1);
        speaker1.setUnavailableTimeslotSet(new HashSet<>(Arrays.asList(slot1)));
        scoreVerifier.assertHardWeight(SPEAKER_UNAVAILABLE_TIMESLOT, -1, solution);
        speaker2.setUnavailableTimeslotSet(Collections.emptySet());
        talk1.withSpeakerList(Arrays.asList(speaker1, speaker2)).withTimeslot(slot1);
        scoreVerifier.assertHardWeight(SPEAKER_UNAVAILABLE_TIMESLOT, -1, solution);
        speaker2.setUnavailableTimeslotSet(new HashSet<>(Arrays.asList(slot2)));
        scoreVerifier.assertHardWeight(SPEAKER_UNAVAILABLE_TIMESLOT, -1, solution);
        speaker2.setUnavailableTimeslotSet(new HashSet<>(Arrays.asList(slot1, slot2)));
        scoreVerifier.assertHardWeight(SPEAKER_UNAVAILABLE_TIMESLOT, -1, solution);
    }

    @Test
    public void speakerWithConflictingTimeslots() {
        TalkType talkType = new TalkType(0L, "type1");
        Speaker speaker1 = new Speaker(1L)
                .withUnavailableTimeslotSet(Collections.emptySet())
                .withRequiredTimeslotTagSet(Collections.emptySet())
                .withPreferredTimeslotTagSet(Collections.emptySet())
                .withProhibitedTimeslotTagSet(Collections.emptySet())
                .withUndesiredTimeslotTagSet(Collections.emptySet());
        Talk talk1 = createTalk(1L).withTalkType(talkType);
        Talk talk2 = createTalk(2L).withTalkType(talkType);

        LocalDateTime start1 = LocalDateTime.of(2018, 1, 1, 9, 0);
        LocalDateTime end1 = LocalDateTime.of(2018, 1, 1, 10, 0);
        LocalDateTime start2 = LocalDateTime.of(2018, 1, 1, 9, 30);
        LocalDateTime end2 = LocalDateTime.of(2018, 1, 1, 10, 30);
        LocalDateTime start3 = LocalDateTime.of(2018, 1, 1, 10, 0);
        LocalDateTime end3 = LocalDateTime.of(2018, 1, 1, 11, 0);
        Timeslot slot1 = new Timeslot(1L)
                .withTalkTypeSet(Collections.singleton(talkType))
                .withStartDateTime(start1)
                .withEndDateTime(end1);
        Timeslot slot2 = new Timeslot(2L)
                .withTalkTypeSet(Collections.singleton(talkType))
                .withStartDateTime(start2)
                .withEndDateTime(end2);
        Timeslot slot3 = new Timeslot(3L)
                .withTalkTypeSet(Collections.singleton(talkType))
                .withStartDateTime(start3)
                .withEndDateTime(end3);
        ConferenceSolution solution = new ConferenceSolution(1L)
                .withParametrization(new ConferenceParametrization(1L))
                .withTalkTypeList(Collections.singletonList(talkType))
                .withTalkList(Arrays.asList(talk1, talk2))
                .withTimeslotList(Arrays.asList(slot1, slot2, slot3))
                .withRoomList(Collections.emptyList())
                .withSpeakerList(Arrays.asList(speaker1));
        scoreVerifier.assertHardWeight(SPEAKER_CONFLICT, 0, solution);
        // speaker has no conflicting time slots
        talk1.withSpeakerList(Arrays.asList(speaker1)).withTimeslot(slot1);
        talk2.withSpeakerList(Arrays.asList(speaker1)).withTimeslot(slot3);
        scoreVerifier.assertHardWeight(SPEAKER_CONFLICT, 0, solution);
        // speaker has no conflicting time slots
        talk2.withTimeslot(slot2);
        scoreVerifier.assertHardWeight(SPEAKER_CONFLICT, -1, solution);
    }

    @Test
    public void speakerRequiredTimeSlotTag() {
        String tag1 = "tag1";
        String tag2 = "tag2";
        String tag3 = "tag3";
        TalkType talkType = new TalkType(0L, "type1");
        Speaker speaker1 = new Speaker(1L)
                .withUnavailableTimeslotSet(Collections.emptySet())
                .withRequiredTimeslotTagSet(Collections.emptySet())
                .withPreferredTimeslotTagSet(Collections.emptySet())
                .withProhibitedTimeslotTagSet(Collections.emptySet())
                .withUndesiredTimeslotTagSet(Collections.emptySet());
        Speaker speaker2 = new Speaker(2L)
                .withUnavailableTimeslotSet(Collections.emptySet())
                .withRequiredTimeslotTagSet(Collections.emptySet())
                .withPreferredTimeslotTagSet(Collections.emptySet())
                .withProhibitedTimeslotTagSet(Collections.emptySet())
                .withUndesiredTimeslotTagSet(Collections.emptySet());
        Talk talk1 = createTalk(1L).withTalkType(talkType);

        LocalDateTime start1 = LocalDateTime.of(2018, 1, 1, 9, 0);
        LocalDateTime end1 = LocalDateTime.of(2018, 1, 1, 10, 0);
        Timeslot slot1 = new Timeslot(1L)
                .withTalkTypeSet(Collections.singleton(talkType))
                .withStartDateTime(start1)
                .withEndDateTime(end1);
        ConferenceSolution solution = new ConferenceSolution(1L)
                .withParametrization(new ConferenceParametrization(1L))
                .withTalkTypeList(Collections.singletonList(talkType))
                .withTalkList(Arrays.asList(talk1))
                .withTimeslotList(Arrays.asList(slot1))
                .withRoomList(Collections.emptyList())
                .withSpeakerList(Arrays.asList(speaker1, speaker2));
        // talk with 1 speaker, speaker without required time slot tag
        talk1.withSpeakerList(Arrays.asList(speaker1)).withTimeslot(slot1);
        slot1.setTagSet(Collections.emptySet());
        scoreVerifier.assertHardWeight(SPEAKER_REQUIRED_TIMESLOT_TAGS, 0, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertHardWeight(SPEAKER_REQUIRED_TIMESLOT_TAGS, 0, solution);
        // talk with 1 speaker, speaker with required time slot tag, time slot without matching tag
        slot1.setTagSet(Collections.emptySet());
        speaker1.setRequiredTimeslotTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertHardWeight(SPEAKER_REQUIRED_TIMESLOT_TAGS, -1, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag2, tag3)));
        scoreVerifier.assertHardWeight(SPEAKER_REQUIRED_TIMESLOT_TAGS, -1, solution);
        // talk with 1 speaker, speaker with required time slot tag, time slot with matching tag
        speaker1.setRequiredTimeslotTagSet(new HashSet<>(Arrays.asList(tag1)));
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertHardWeight(SPEAKER_REQUIRED_TIMESLOT_TAGS, 0, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreVerifier.assertHardWeight(SPEAKER_REQUIRED_TIMESLOT_TAGS, 0, solution);
        // talk with 1 speaker, speaker with 2 required time slot tags
        speaker1.setRequiredTimeslotTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        slot1.setTagSet(Collections.emptySet());
        scoreVerifier.assertHardWeight(SPEAKER_REQUIRED_TIMESLOT_TAGS, -2, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertHardWeight(SPEAKER_REQUIRED_TIMESLOT_TAGS, -1, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreVerifier.assertHardWeight(SPEAKER_REQUIRED_TIMESLOT_TAGS, 0, solution);
        // talk with 2 speakers, speakers with required time slot tag, time slot without matching tag
        talk1.withSpeakerList(Arrays.asList(speaker1, speaker2));
        slot1.setTagSet(Collections.emptySet());
        speaker1.setRequiredTimeslotTagSet(new HashSet<>(Arrays.asList(tag1)));
        speaker2.setRequiredTimeslotTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertHardWeight(SPEAKER_REQUIRED_TIMESLOT_TAGS, -2, solution);
        // talk with 2 speakers, speakers with 2 required time slot tags, time slot without matching tag
        slot1.setTagSet(Collections.emptySet());
        speaker1.setRequiredTimeslotTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        speaker2.setRequiredTimeslotTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreVerifier.assertHardWeight(SPEAKER_REQUIRED_TIMESLOT_TAGS, -4, solution);
        // talk with 2 speakers, speakers with different required time slot tags, time slot with partially matching tag
        slot1.setTagSet(Collections.emptySet());
        speaker1.setRequiredTimeslotTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        speaker2.setRequiredTimeslotTagSet(new HashSet<>(Arrays.asList(tag1, tag3)));
        scoreVerifier.assertHardWeight(SPEAKER_REQUIRED_TIMESLOT_TAGS, -4, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertHardWeight(SPEAKER_REQUIRED_TIMESLOT_TAGS, -2, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag2)));
        scoreVerifier.assertHardWeight(SPEAKER_REQUIRED_TIMESLOT_TAGS, -3, solution);
    }

    @Test
    public void speakerProhibitedTimeSlotTag() {
        String tag1 = "tag1";
        String tag2 = "tag2";
        String tag3 = "tag3";
        TalkType talkType = new TalkType(0L, "type1");
        Speaker speaker1 = new Speaker(1L)
                .withUnavailableTimeslotSet(Collections.emptySet())
                .withRequiredTimeslotTagSet(Collections.emptySet())
                .withPreferredTimeslotTagSet(Collections.emptySet())
                .withProhibitedTimeslotTagSet(Collections.emptySet())
                .withUndesiredTimeslotTagSet(Collections.emptySet());
        Speaker speaker2 = new Speaker(2L)
                .withUnavailableTimeslotSet(Collections.emptySet())
                .withRequiredTimeslotTagSet(Collections.emptySet())
                .withPreferredTimeslotTagSet(Collections.emptySet())
                .withProhibitedTimeslotTagSet(Collections.emptySet())
                .withUndesiredTimeslotTagSet(Collections.emptySet());
        Talk talk1 = createTalk(1L).withTalkType(talkType);

        LocalDateTime start1 = LocalDateTime.of(2018, 1, 1, 9, 0);
        LocalDateTime end1 = LocalDateTime.of(2018, 1, 1, 10, 0);
        Timeslot slot1 = new Timeslot(1L)
                .withTalkTypeSet(Collections.singleton(talkType))
                .withStartDateTime(start1)
                .withEndDateTime(end1);
        ConferenceSolution solution = new ConferenceSolution(1L)
                .withParametrization(new ConferenceParametrization(1L))
                .withTalkTypeList(Collections.singletonList(talkType))
                .withTalkList(Arrays.asList(talk1))
                .withTimeslotList(Arrays.asList(slot1))
                .withRoomList(Collections.emptyList())
                .withSpeakerList(Arrays.asList(speaker1, speaker2));
        // talk with 1 speaker, speaker without prohibited time slot tag
        talk1.withSpeakerList(Arrays.asList(speaker1)).withTimeslot(slot1);
        slot1.setTagSet(Collections.emptySet());
        scoreVerifier.assertHardWeight(SPEAKER_PROHIBITED_TIMESLOT_TAGs, 0, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertHardWeight(SPEAKER_PROHIBITED_TIMESLOT_TAGs, 0, solution);
        // talk with 1 speaker, speaker with prohibited time slot tag, time slot without matching tag
        speaker1.setProhibitedTimeslotTagSet(new HashSet<>(Arrays.asList(tag1)));
        slot1.setTagSet(Collections.emptySet());
        scoreVerifier.assertHardWeight(SPEAKER_PROHIBITED_TIMESLOT_TAGs, 0, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag2)));
        scoreVerifier.assertHardWeight(SPEAKER_PROHIBITED_TIMESLOT_TAGs, 0, solution);
        // talk with 1 speaker, speaker with prohibited time slot tag, time slot with matching tag
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertHardWeight(SPEAKER_PROHIBITED_TIMESLOT_TAGs, -1, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreVerifier.assertHardWeight(SPEAKER_PROHIBITED_TIMESLOT_TAGs, -1, solution);
        // talk with 1 speaker, speaker with 2 required time slot tags
        speaker1.setProhibitedTimeslotTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        slot1.setTagSet(Collections.emptySet());
        scoreVerifier.assertHardWeight(SPEAKER_PROHIBITED_TIMESLOT_TAGs, 0, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertHardWeight(SPEAKER_PROHIBITED_TIMESLOT_TAGs, -1, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreVerifier.assertHardWeight(SPEAKER_PROHIBITED_TIMESLOT_TAGs, -2, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2, tag3)));
        scoreVerifier.assertHardWeight(SPEAKER_PROHIBITED_TIMESLOT_TAGs, -2, solution);
        // talk with 2 speakers, speakers with prohibited time slot tag, time slot without matching tag
        talk1.withSpeakerList(Arrays.asList(speaker1, speaker2));
        slot1.setTagSet(Collections.emptySet());
        speaker1.setProhibitedTimeslotTagSet(new HashSet<>(Arrays.asList(tag1)));
        speaker2.setProhibitedTimeslotTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertHardWeight(SPEAKER_PROHIBITED_TIMESLOT_TAGs, 0, solution);
        // talk with 2 speakers, speakers with prohibited time slot tags, time slot with matching tags
        speaker1.setProhibitedTimeslotTagSet(new HashSet<>(Arrays.asList(tag1)));
        speaker2.setProhibitedTimeslotTagSet(new HashSet<>(Arrays.asList(tag1)));
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertHardWeight(SPEAKER_PROHIBITED_TIMESLOT_TAGs, -2, solution);
        speaker1.setProhibitedTimeslotTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreVerifier.assertHardWeight(SPEAKER_PROHIBITED_TIMESLOT_TAGs, -2, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreVerifier.assertHardWeight(SPEAKER_PROHIBITED_TIMESLOT_TAGs, -3, solution);
        speaker2.setProhibitedTimeslotTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreVerifier.assertHardWeight(SPEAKER_PROHIBITED_TIMESLOT_TAGs, -4, solution);
        speaker2.setProhibitedTimeslotTagSet(new HashSet<>(Arrays.asList(tag1, tag3)));
        scoreVerifier.assertHardWeight(SPEAKER_PROHIBITED_TIMESLOT_TAGs, -3, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2, tag3)));
        scoreVerifier.assertHardWeight(SPEAKER_PROHIBITED_TIMESLOT_TAGs, -4, solution);
    }

    @Test
    public void talkRequiredTimeslotTag() {
        String tag1 = "tag1";
        String tag2 = "tag2";
        TalkType talkType = new TalkType(0L, "type1");
        Talk talk1 = createTalk(1L);
        LocalDateTime start1 = LocalDateTime.of(2018, 1, 1, 9, 0);
        LocalDateTime end1 = LocalDateTime.of(2018, 1, 1, 10, 0);
        Timeslot slot1 = new Timeslot(1L)
                .withTalkTypeSet(Collections.singleton(talkType))
                .withStartDateTime(start1)
                .withEndDateTime(end1);
        ConferenceSolution solution = new ConferenceSolution(1L)
                .withParametrization(new ConferenceParametrization(1L))
                .withTalkTypeList(Collections.singletonList(talkType))
                .withTalkList(Arrays.asList(talk1))
                .withTimeslotList(Arrays.asList(slot1))
                .withRoomList(Collections.emptyList())
                .withSpeakerList(Collections.emptyList());
        // talk without required time slot tag
        slot1.setTagSet(Collections.emptySet());
        talk1.withTimeslot(slot1);
        scoreVerifier.assertHardWeight(TALK_REQUIRED_TIMESLOT_TAGS, 0, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertHardWeight(TALK_REQUIRED_TIMESLOT_TAGS, 0, solution);
        // talk with required time slot tag, time slot without matching tag
        talk1.withRequiredTimeslotTagSet(new HashSet<>(Arrays.asList(tag1)));
        slot1.setTagSet(Collections.emptySet());
        scoreVerifier.assertHardWeight(TALK_REQUIRED_TIMESLOT_TAGS, -1, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag2)));
        scoreVerifier.assertHardWeight(TALK_REQUIRED_TIMESLOT_TAGS, -1, solution);
        // talk with required time slot tag, time slot with matching tag
        talk1.withRequiredTimeslotTagSet(new HashSet<>(Arrays.asList(tag1)));
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertHardWeight(TALK_REQUIRED_TIMESLOT_TAGS, 0, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreVerifier.assertHardWeight(TALK_REQUIRED_TIMESLOT_TAGS, 0, solution);
        // talk with 2 required time slot tags
        slot1.setTagSet(Collections.emptySet());
        talk1.withRequiredTimeslotTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreVerifier.assertHardWeight(TALK_REQUIRED_TIMESLOT_TAGS, -2, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertHardWeight(TALK_REQUIRED_TIMESLOT_TAGS, -1, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreVerifier.assertHardWeight(TALK_REQUIRED_TIMESLOT_TAGS, 0, solution);
    }

    @Test
    public void talkProhibitedTimeslotTag() {
        String tag1 = "tag1";
        String tag2 = "tag2";
        TalkType talkType = new TalkType(0L, "type1");
        Talk talk1 = createTalk(1L);
        LocalDateTime start1 = LocalDateTime.of(2018, 1, 1, 9, 0);
        LocalDateTime end1 = LocalDateTime.of(2018, 1, 1, 10, 0);
        Timeslot slot1 = new Timeslot(1L)
                .withTalkTypeSet(Collections.singleton(talkType))
                .withStartDateTime(start1)
                .withEndDateTime(end1);
        ConferenceSolution solution = new ConferenceSolution(1L)
                .withParametrization(new ConferenceParametrization(1L))
                .withTalkTypeList(Collections.singletonList(talkType))
                .withTalkList(Arrays.asList(talk1))
                .withTimeslotList(Arrays.asList(slot1))
                .withRoomList(Collections.emptyList())
                .withSpeakerList(Collections.emptyList());
        // talk without prohibited time slot tags
        slot1.setTagSet(Collections.emptySet());
        talk1.withTimeslot(slot1);
        scoreVerifier.assertHardWeight(TALK_PROHIBITED_TIMESLOT_TAGS, 0, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertHardWeight(TALK_REQUIRED_TIMESLOT_TAGS, 0, solution);
        // talk with prohibited time slot tag, time slot without matching tag
        slot1.setTagSet(Collections.emptySet());
        talk1.withTimeslot(slot1).withProhibitedTimeslotTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertHardWeight(TALK_PROHIBITED_TIMESLOT_TAGS, 0, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag2)));
        scoreVerifier.assertHardWeight(TALK_PROHIBITED_TIMESLOT_TAGS, 0, solution);
        // talk with prohibited time slot tag, time slot with matching tag
        talk1.withTimeslot(slot1).withProhibitedTimeslotTagSet(new HashSet<>(Arrays.asList(tag1)));
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertHardWeight(TALK_PROHIBITED_TIMESLOT_TAGS, -1, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreVerifier.assertHardWeight(TALK_PROHIBITED_TIMESLOT_TAGS, -1, solution);
        // talk with 2 prohibited time slot tags
        talk1.withTimeslot(slot1).withProhibitedTimeslotTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        slot1.setTagSet(Collections.emptySet());
        scoreVerifier.assertHardWeight(TALK_PROHIBITED_TIMESLOT_TAGS, -0, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertHardWeight(TALK_PROHIBITED_TIMESLOT_TAGS, -1, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreVerifier.assertHardWeight(TALK_PROHIBITED_TIMESLOT_TAGS, -2, solution);
    }

    @Test
    public void speakerRequiredRoomTag() {
        String tag1 = "tag1";
        String tag2 = "tag2";
        String tag3 = "tag3";
        TalkType talkType = new TalkType(0L, "type1");
        Room room1 = new Room(1L).withTalkTypeSet(Collections.singleton(talkType));
        Talk talk1 = createTalk(1L);
        Speaker speaker1 = new Speaker(1L)
                .withUnavailableTimeslotSet(Collections.emptySet())
                .withRequiredTimeslotTagSet(Collections.emptySet())
                .withRequiredRoomTagSet(Collections.emptySet())
                .withPreferredTimeslotTagSet(Collections.emptySet())
                .withPreferredRoomTagSet(Collections.emptySet())
                .withProhibitedRoomTagSet(Collections.emptySet())
                .withUndesiredRoomTagSet(Collections.emptySet());
        Speaker speaker2 = new Speaker(2L)
                .withUnavailableTimeslotSet(Collections.emptySet())
                .withRequiredTimeslotTagSet(Collections.emptySet())
                .withRequiredRoomTagSet(Collections.emptySet())
                .withPreferredTimeslotTagSet(Collections.emptySet())
                .withPreferredRoomTagSet(Collections.emptySet())
                .withProhibitedRoomTagSet(Collections.emptySet())
                .withUndesiredRoomTagSet(Collections.emptySet());
        ConferenceSolution solution = new ConferenceSolution(1L)
                .withParametrization(new ConferenceParametrization(1L))
                .withTalkTypeList(Collections.singletonList(talkType))
                .withTalkList(Arrays.asList(talk1))
                .withTimeslotList(Collections.emptyList())
                .withRoomList(Arrays.asList(room1))
                .withSpeakerList(Arrays.asList(speaker1, speaker2));
        // talk with 1 speaker without required room tags
        room1.setTagSet(Collections.emptySet());
        talk1.withSpeakerList(Arrays.asList(speaker1)).withRoom(room1);
        scoreVerifier.assertHardWeight(SPEAKER_REQUIRED_ROOM_TAGS, 0, solution);
        // talk with 1 speaker, speaker with required room tag, room without matching tag
        speaker1.withRequiredRoomTagSet(new HashSet<>(Arrays.asList(tag1)));
        room1.setTagSet(Collections.emptySet());
        scoreVerifier.assertHardWeight(SPEAKER_REQUIRED_ROOM_TAGS, -1, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag3)));
        scoreVerifier.assertHardWeight(SPEAKER_REQUIRED_ROOM_TAGS, -1, solution);
        // talk with 1 speaker, speaker with required room tag, room with matching tag
        speaker1.withRequiredRoomTagSet(new HashSet<>(Arrays.asList(tag1)));
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertHardWeight(SPEAKER_REQUIRED_ROOM_TAGS, -0, solution);
        // talk with 1 speaker, speaker with 2 required room tags
        speaker1.withRequiredRoomTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        room1.setTagSet(Collections.emptySet());
        scoreVerifier.assertHardWeight(SPEAKER_REQUIRED_ROOM_TAGS, -2, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertHardWeight(SPEAKER_REQUIRED_ROOM_TAGS, -1, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreVerifier.assertHardWeight(SPEAKER_REQUIRED_ROOM_TAGS, 0, solution);
        // talk with 2 speakers
        talk1.withSpeakerList(Arrays.asList(speaker1, speaker2)).withRoom(room1);
        speaker1.withRequiredRoomTagSet(new HashSet<>(Arrays.asList(tag1)));
        speaker2.withRequiredRoomTagSet(new HashSet<>(Arrays.asList(tag1)));
        room1.setTagSet(Collections.emptySet());
        scoreVerifier.assertHardWeight(SPEAKER_REQUIRED_ROOM_TAGS, -2, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertHardWeight(SPEAKER_REQUIRED_ROOM_TAGS, 0, solution);
        speaker2.withRequiredRoomTagSet(new HashSet<>(Arrays.asList(tag2)));
        room1.setTagSet(Collections.emptySet());
        scoreVerifier.assertHardWeight(SPEAKER_REQUIRED_ROOM_TAGS, -2, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertHardWeight(SPEAKER_REQUIRED_ROOM_TAGS, -1, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreVerifier.assertHardWeight(SPEAKER_REQUIRED_ROOM_TAGS, 0, solution);
        speaker2.withRequiredRoomTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        room1.setTagSet(Collections.emptySet());
        scoreVerifier.assertHardWeight(SPEAKER_REQUIRED_ROOM_TAGS, -3, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertHardWeight(SPEAKER_REQUIRED_ROOM_TAGS, -1, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreVerifier.assertHardWeight(SPEAKER_REQUIRED_ROOM_TAGS, 0, solution);
    }

    @Test
    public void speakerProhibitedRoomTag() {
        String tag1 = "tag1";
        String tag2 = "tag2";
        String tag3 = "tag3";
        TalkType talkType = new TalkType(0L, "type1");
        Room room1 = new Room(1L).withTalkTypeSet(Collections.singleton(talkType));
        Talk talk1 = createTalk(1L);
        Speaker speaker1 = new Speaker(1L)
                .withUnavailableTimeslotSet(Collections.emptySet())
                .withRequiredTimeslotTagSet(Collections.emptySet())
                .withRequiredRoomTagSet(Collections.emptySet())
                .withPreferredTimeslotTagSet(Collections.emptySet())
                .withPreferredRoomTagSet(Collections.emptySet())
                .withProhibitedRoomTagSet(Collections.emptySet())
                .withUndesiredRoomTagSet(Collections.emptySet());
        Speaker speaker2 = new Speaker(2L)
                .withUnavailableTimeslotSet(Collections.emptySet())
                .withRequiredTimeslotTagSet(Collections.emptySet())
                .withRequiredRoomTagSet(Collections.emptySet())
                .withPreferredTimeslotTagSet(Collections.emptySet())
                .withPreferredRoomTagSet(Collections.emptySet())
                .withProhibitedRoomTagSet(Collections.emptySet())
                .withUndesiredRoomTagSet(Collections.emptySet());
        ConferenceSolution solution = new ConferenceSolution(1L)
                .withParametrization(new ConferenceParametrization(1L))
                .withTalkTypeList(Collections.singletonList(talkType))
                .withTalkList(Arrays.asList(talk1))
                .withTimeslotList(Collections.emptyList())
                .withRoomList(Arrays.asList(room1))
                .withSpeakerList(Arrays.asList(speaker1, speaker2));
        scoreVerifier.assertHardWeight(SPEAKER_PROHIBITED_ROOM_TAGS, 0, solution);
        // talk with 1 speaker without prohibited room tags
        room1.setTagSet(Collections.emptySet());
        talk1.withSpeakerList(Arrays.asList(speaker1)).withRoom(room1);
        scoreVerifier.assertHardWeight(SPEAKER_PROHIBITED_ROOM_TAGS, 0, solution);
        // talk with 1 speaker, speaker with prohibited room tag, room without matching tag
        speaker1.withProhibitedRoomTagSet(new HashSet<>(Arrays.asList(tag1)));
        room1.setTagSet(Collections.emptySet());
        scoreVerifier.assertHardWeight(SPEAKER_PROHIBITED_ROOM_TAGS, 0, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag3)));
        scoreVerifier.assertHardWeight(SPEAKER_PROHIBITED_ROOM_TAGS, 0, solution);
        // talk with 1 speaker, speaker with prohibited room tag, room with matching tag
        speaker1.withProhibitedRoomTagSet(new HashSet<>(Arrays.asList(tag1)));
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertHardWeight(SPEAKER_PROHIBITED_ROOM_TAGS, -1, solution);
        // talk with 1 speaker, speaker with 2 prohibited room tags
        speaker1.withProhibitedRoomTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        room1.setTagSet(Collections.emptySet());
        scoreVerifier.assertHardWeight(SPEAKER_PROHIBITED_ROOM_TAGS, 0, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertHardWeight(SPEAKER_PROHIBITED_ROOM_TAGS, -1, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreVerifier.assertHardWeight(SPEAKER_PROHIBITED_ROOM_TAGS, -2, solution);
        // talk with 2 speakers
        talk1.withSpeakerList(Arrays.asList(speaker1, speaker2)).withRoom(room1);
        speaker1.withProhibitedRoomTagSet(new HashSet<>(Arrays.asList(tag1)));
        speaker2.withProhibitedRoomTagSet(new HashSet<>(Arrays.asList(tag1)));
        room1.setTagSet(Collections.emptySet());
        scoreVerifier.assertHardWeight(SPEAKER_PROHIBITED_ROOM_TAGS, 0, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertHardWeight(SPEAKER_PROHIBITED_ROOM_TAGS, -2, solution);
        speaker2.withProhibitedRoomTagSet(new HashSet<>(Arrays.asList(tag2)));
        room1.setTagSet(Collections.emptySet());
        scoreVerifier.assertHardWeight(SPEAKER_PROHIBITED_ROOM_TAGS, 0, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertHardWeight(SPEAKER_PROHIBITED_ROOM_TAGS, -1, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreVerifier.assertHardWeight(SPEAKER_PROHIBITED_ROOM_TAGS, -2, solution);
        speaker2.withProhibitedRoomTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        room1.setTagSet(Collections.emptySet());
        scoreVerifier.assertHardWeight(SPEAKER_PROHIBITED_ROOM_TAGS, 0, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertHardWeight(SPEAKER_PROHIBITED_ROOM_TAGS, -2, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreVerifier.assertHardWeight(SPEAKER_PROHIBITED_ROOM_TAGS, -3, solution);
    }

    @Test
    public void talkRequiredRoomTag() {
        String tag1 = "tag1";
        String tag2 = "tag2";
        TalkType talkType = new TalkType(0L, "type1");
        Room room1 = new Room(1L).withTalkTypeSet(Collections.emptySet());
        Talk talk1 = createTalk(1L).withTalkType(talkType);
        ConferenceSolution solution = new ConferenceSolution(1L)
                .withParametrization(new ConferenceParametrization(1L))
                .withTalkTypeList(Collections.singletonList(talkType))
                .withTalkList(Arrays.asList(talk1))
                .withTimeslotList(Collections.emptyList())
                .withRoomList(Collections.emptyList())
                .withSpeakerList(Collections.emptyList());
        // talk without required room tags
        room1.setTagSet(Collections.emptySet());
        talk1.withRoom(room1);
        scoreVerifier.assertHardWeight(TALK_REQUIRED_ROOM_TAGS, 0, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertHardWeight(TALK_REQUIRED_ROOM_TAGS, 0, solution);
        // talk with required room tag, room without matching tag
        talk1.withRequiredRoomTagSet(new HashSet<>(Arrays.asList(tag1)));
        room1.setTagSet(Collections.emptySet());
        scoreVerifier.assertHardWeight(TALK_REQUIRED_ROOM_TAGS, -1, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag2)));
        scoreVerifier.assertHardWeight(TALK_REQUIRED_ROOM_TAGS, -1, solution);
        // talk with required room tag, room with matching tag
        talk1.withRequiredRoomTagSet(new HashSet<>(Arrays.asList(tag1)));
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertHardWeight(TALK_REQUIRED_ROOM_TAGS, 0, solution);
        // talk with 2 required room tags
        room1.setTagSet(Collections.emptySet());
        talk1.withRequiredRoomTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreVerifier.assertHardWeight(TALK_REQUIRED_ROOM_TAGS, -2, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertHardWeight(TALK_REQUIRED_ROOM_TAGS, -1, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreVerifier.assertHardWeight(TALK_REQUIRED_ROOM_TAGS, 0, solution);
    }

    @Test
    public void talkProhibitedRoomTag() {
        String tag1 = "tag1";
        String tag2 = "tag2";
        TalkType talkType = new TalkType(0L, "type1");
        Room room1 = new Room(1L).withTalkTypeSet(Collections.emptySet());
        Talk talk1 = createTalk(1L).withTalkType(talkType);
        ConferenceSolution solution = new ConferenceSolution(1L)
                .withParametrization(new ConferenceParametrization(1L))
                .withTalkTypeList(Collections.singletonList(talkType))
                .withTalkList(Arrays.asList(talk1))
                .withTimeslotList(Collections.emptyList())
                .withRoomList(Collections.emptyList())
                .withSpeakerList(Collections.emptyList());
        // talk without prohibited room tags
        room1.setTagSet(Collections.emptySet());
        talk1.withRoom(room1);
        scoreVerifier.assertHardWeight(TALK_PROHIBITED_ROOM_TAGS, 0, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertHardWeight(TALK_PROHIBITED_ROOM_TAGS, 0, solution);
        // talk with prohibited room tag, room without matching tag
        talk1.withProhibitedRoomTagSet(new HashSet<>(Arrays.asList(tag1)));
        room1.setTagSet(Collections.emptySet());
        scoreVerifier.assertHardWeight(TALK_PROHIBITED_ROOM_TAGS, 0, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag2)));
        scoreVerifier.assertHardWeight(TALK_PROHIBITED_ROOM_TAGS, 0, solution);
        // talk with prohibited room tag, room with matching tag
        talk1.withProhibitedRoomTagSet(new HashSet<>(Arrays.asList(tag1)));
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertHardWeight(TALK_PROHIBITED_ROOM_TAGS, -1, solution);
        // talk with 2 prohibited room tags
        room1.setTagSet(Collections.emptySet());
        talk1.withProhibitedRoomTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreVerifier.assertHardWeight(TALK_PROHIBITED_ROOM_TAGS, 0, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertHardWeight(TALK_PROHIBITED_ROOM_TAGS, -1, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreVerifier.assertHardWeight(TALK_PROHIBITED_ROOM_TAGS, -2, solution);
    }

    private Talk createTalk(long id) {
        return new Talk(id)
                .withSpeakerList(Collections.emptyList())
                .withThemeTrackTagSet(Collections.emptySet())
                .withSectorTagSet(Collections.emptySet())
                .withAudienceTypeSet(Collections.emptySet())
                .withAudienceTypeSet(Collections.emptySet())
                .withContentTagSet(Collections.emptySet())
                .withRequiredRoomTagSet(Collections.emptySet())
                .withPreferredRoomTagSet(Collections.emptySet())
                .withProhibitedRoomTagSet(Collections.emptySet())
                .withUndesiredRoomTagSet(Collections.emptySet())
                .withRequiredTimeslotTagSet(Collections.emptySet())
                .withPreferredTimeslotTagSet(Collections.emptySet())
                .withProhibitedTimeslotTagSet(Collections.emptySet())
                .withUndesiredTimeslotTagSet(Collections.emptySet())
                .withMutuallyExclusiveTalksTagSet(Collections.emptySet())
                .withPrerequisiteTalksCodesSet(Collections.emptySet());
    }
}
