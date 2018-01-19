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
import org.optaplanner.examples.conferencescheduling.domain.Timeslot;
import org.optaplanner.test.impl.score.buildin.hardsoft.HardSoftScoreVerifier;

public class ConferenceSchedulingScoreHardConstraintTest {

    private HardSoftScoreVerifier<ConferenceSolution> scoreverifier = new HardSoftScoreVerifier<>(
            SolverFactory.createFromXmlResource(ConferenceSchedulingApp.SOLVER_CONFIG));

    @Test
    public void talkTypeOfTimeSlot() {
        Talk talk1 = new Talk(1L)
                .withSpeakerList(Collections.emptyList())
                .withRequiredRoomTagSet(Collections.emptySet())
                .withRequiredTimeslotTagSet(Collections.emptySet())
                .withPreferredTimeslotTagSet(Collections.emptySet())
                .withProhibitedTimeslotTagSet(Collections.emptySet())
                .withUndesiredTimeslotTagSet(Collections.emptySet());
        Talk talk2 = new Talk(2L)
                .withSpeakerList(Collections.emptyList())
                .withRequiredRoomTagSet(Collections.emptySet())
                .withRequiredTimeslotTagSet(Collections.emptySet())
                .withPreferredTimeslotTagSet(Collections.emptySet())
                .withProhibitedTimeslotTagSet(Collections.emptySet())
                .withUndesiredTimeslotTagSet(Collections.emptySet());
        Timeslot slot1 = new Timeslot(1L);
        Timeslot slot2 = new Timeslot(2L);
        String talkType1 = "type1";
        String talkType2 = "type2";
        ConferenceSolution solution = new ConferenceSolution(1L)
                .withTalkList(Arrays.asList(talk1, talk2))
                .withTimeslotList(Arrays.asList(slot1, slot2))
                .withRoomList(Collections.emptyList())
                .withSpeakerList(Collections.emptyList());
        scoreverifier.assertHardWeight("Talk type of timeslot", 0, solution);
        // time slot with matching talk type
        talk1.withTalkType(talkType1).withTimeslot(slot1);
        slot1.setTalkType(talkType1);
        scoreverifier.assertHardWeight("Talk type of timeslot", 0, solution);
        // time slot with non matching talk type
        talk2.withTalkType(talkType2).withTimeslot(slot2);
        slot2.setTalkType(talkType1);
        scoreverifier.assertHardWeight("Talk type of timeslot", -100, solution);
    }

    @Test
    public void talkHasUnavailableRoom() {
        String talkType = "type1";
        Talk talk1 = new Talk(1L)
                .withTalkType(talkType)
                .withSpeakerList(Collections.emptyList())
                .withRequiredRoomTagSet(Collections.emptySet())
                .withPreferredRoomTagSet(Collections.emptySet())
                .withProhibitedRoomTagSet(Collections.emptySet())
                .withUndesiredRoomTagSet(Collections.emptySet())
                .withRequiredTimeslotTagSet(Collections.emptySet())
                .withPreferredTimeslotTagSet(Collections.emptySet())
                .withProhibitedTimeslotTagSet(Collections.emptySet())
                .withUndesiredTimeslotTagSet(Collections.emptySet());
        Talk talk2 = new Talk(2L)
                .withTalkType(talkType)
                .withSpeakerList(Collections.emptyList())
                .withRequiredRoomTagSet(Collections.emptySet())
                .withPreferredRoomTagSet(Collections.emptySet())
                .withProhibitedRoomTagSet(Collections.emptySet())
                .withUndesiredRoomTagSet(Collections.emptySet())
                .withRequiredTimeslotTagSet(Collections.emptySet())
                .withPreferredTimeslotTagSet(Collections.emptySet())
                .withProhibitedTimeslotTagSet(Collections.emptySet())
                .withUndesiredTimeslotTagSet(Collections.emptySet());
        Timeslot slot1 = new Timeslot(1L).withTalkType(talkType);
        Timeslot slot2 = new Timeslot(2L).withTalkType(talkType);
        Room room1 = new Room(1L);
        Room room2 = new Room(2L);
        ConferenceSolution solution = new ConferenceSolution(1L)
                .withTalkList(Arrays.asList(talk1, talk2))
                .withTimeslotList(Arrays.asList(slot1, slot2))
                .withRoomList(Arrays.asList(room1, room2))
                .withSpeakerList(Collections.emptyList());
        scoreverifier.assertHardWeight("Room unavailable timeslots", 0, solution);
        // talk with available room
        room1.setUnavailableTimeslotSet(Collections.emptySet());
        talk1.withTimeslot(slot1).withRoom(room1);
        scoreverifier.assertHardWeight("Room unavailable timeslots", 0, solution);
        room1.setUnavailableTimeslotSet(new HashSet<>(Arrays.asList(slot2)));
        scoreverifier.assertHardWeight("Room unavailable timeslots", 0, solution);
        // talk with room with unavailable time slot
        room1.setUnavailableTimeslotSet(new HashSet<>(Arrays.asList(slot1)));
        talk1.withTimeslot(slot1).withRoom(room1);
        scoreverifier.assertHardWeight("Room unavailable timeslots", -10, solution);
        room1.setUnavailableTimeslotSet(new HashSet<>(Arrays.asList(slot1, slot2)));
        talk1.withTimeslot(slot1).withRoom(room1);
        scoreverifier.assertHardWeight("Room unavailable timeslots", -10, solution);
    }

    @Test
    public void roomConflict() {
        String talkType = "type1";
        Talk talk1 = new Talk(1L)
                .withTalkType(talkType)
                .withSpeakerList(Collections.emptyList())
                .withRequiredRoomTagSet(Collections.emptySet())
                .withPreferredRoomTagSet(Collections.emptySet())
                .withProhibitedRoomTagSet(Collections.emptySet())
                .withUndesiredRoomTagSet(Collections.emptySet())
                .withRequiredTimeslotTagSet(Collections.emptySet())
                .withPreferredTimeslotTagSet(Collections.emptySet())
                .withProhibitedTimeslotTagSet(Collections.emptySet())
                .withUndesiredTimeslotTagSet(Collections.emptySet());
        Talk talk2 = new Talk(2L)
                .withTalkType(talkType)
                .withSpeakerList(Collections.emptyList())
                .withRequiredRoomTagSet(Collections.emptySet())
                .withPreferredRoomTagSet(Collections.emptySet())
                .withProhibitedRoomTagSet(Collections.emptySet())
                .withUndesiredRoomTagSet(Collections.emptySet())
                .withRequiredTimeslotTagSet(Collections.emptySet())
                .withPreferredTimeslotTagSet(Collections.emptySet())
                .withProhibitedTimeslotTagSet(Collections.emptySet())
                .withUndesiredTimeslotTagSet(Collections.emptySet());
        LocalDateTime start1 = LocalDateTime.of(2018, 1, 1, 9, 0);
        LocalDateTime end1 = LocalDateTime.of(2018, 1, 1, 10, 0);
        LocalDateTime start2 = LocalDateTime.of(2018, 1, 1, 9, 30);
        LocalDateTime end2 = LocalDateTime.of(2018, 1, 1, 10, 30);
        LocalDateTime start3 = LocalDateTime.of(2018, 1, 1, 10, 0);
        LocalDateTime end3 = LocalDateTime.of(2018, 1, 1, 11, 0);
        Timeslot slot1 = new Timeslot(1L)
                .withTalkType(talkType)
                .withStartDateTime(start1)
                .withEndDateTime(end1);
        Timeslot slot2 = new Timeslot(2L)
                .withTalkType(talkType)
                .withStartDateTime(start2)
                .withEndDateTime(end2);
        Timeslot slot3 = new Timeslot(3L)
                .withTalkType(talkType)
                .withStartDateTime(start3)
                .withEndDateTime(end3);
        Room room1 = new Room(1L).withUnavailableTimeslotSet(Collections.emptySet());
        Room room2 = new Room(2L).withUnavailableTimeslotSet(Collections.emptySet());
        ConferenceSolution solution = new ConferenceSolution(1L)
                .withTalkList(Arrays.asList(talk1, talk2))
                .withTimeslotList(Arrays.asList(slot1, slot2, slot3))
                .withRoomList(Arrays.asList(room1, room2))
                .withSpeakerList(Collections.emptyList());
        scoreverifier.assertHardWeight("Room conflict", 0, solution);
        // talks in same room without overlapping time slots
        talk1.withRoom(room1).withTimeslot(slot1);
        talk2.withRoom(room1).withTimeslot(slot3);
        scoreverifier.assertHardWeight("Room conflict", 0, solution);
        // talks in same room with overlapping time slots
        talk1.withRoom(room2).withTimeslot(slot1);
        talk2.withRoom(room2).withTimeslot(slot2);
        scoreverifier.assertHardWeight("Room conflict", -10, solution);
        // talks in different room with overlapping time slots
        talk1.withRoom(room1).withTimeslot(slot1);
        talk2.withRoom(room2).withTimeslot(slot2);
        scoreverifier.assertHardWeight("Room conflict", 0, solution);
    }

    @Test
    public void talkWithUnavailableSpeaker() {
        String talkType = "type1";
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
        Timeslot slot1 = new Timeslot(1L).withTalkType(talkType);
        Timeslot slot2 = new Timeslot(1L).withTalkType(talkType);
        Talk talk1 = new Talk(1L)
                .withSpeakerList(Collections.emptyList())
                .withTalkType(talkType)
                .withRequiredTimeslotTagSet(Collections.emptySet())
                .withPreferredTimeslotTagSet(Collections.emptySet())
                .withProhibitedTimeslotTagSet(Collections.emptySet())
                .withUndesiredTimeslotTagSet(Collections.emptySet());
        ConferenceSolution solution = new ConferenceSolution(1L)
                .withTalkList(Arrays.asList(talk1))
                .withTimeslotList(Arrays.asList(slot1))
                .withRoomList(Collections.emptyList())
                .withSpeakerList(Arrays.asList(speaker1, speaker2));
        scoreverifier.assertHardWeight("Speaker unavailable timeslots", 0, solution);
        // talk without unavailable speaker
        talk1.withSpeakerList(Arrays.asList(speaker1)).withTimeslot(slot1);
        speaker1.setUnavailableTimeslotSet(Collections.emptySet());
        scoreverifier.assertHardWeight("Speaker unavailable timeslots", 0, solution);
        speaker1.setUnavailableTimeslotSet(new HashSet<>(Arrays.asList(slot2)));
        scoreverifier.assertHardWeight("Speaker unavailable timeslots", 0, solution);
        // talk with 2 speakers
        talk1.withSpeakerList(Arrays.asList(speaker1, speaker2)).withTimeslot(slot1);
        scoreverifier.assertHardWeight("Speaker unavailable timeslots", 0, solution);
        speaker2.setUnavailableTimeslotSet(new HashSet<>(Arrays.asList(slot2)));
        scoreverifier.assertHardWeight("Speaker unavailable timeslots", 0, solution);
        // talk with 1 or more unavailable speakers
        talk1.withSpeakerList(Arrays.asList(speaker1)).withTimeslot(slot1);
        speaker1.setUnavailableTimeslotSet(new HashSet<>(Arrays.asList(slot1)));
        scoreverifier.assertHardWeight("Speaker unavailable timeslots", -1, solution);
        speaker2.setUnavailableTimeslotSet(Collections.emptySet());
        talk1.withSpeakerList(Arrays.asList(speaker1, speaker2)).withTimeslot(slot1);
        scoreverifier.assertHardWeight("Speaker unavailable timeslots", -1, solution);
        speaker2.setUnavailableTimeslotSet(new HashSet<>(Arrays.asList(slot2)));
        scoreverifier.assertHardWeight("Speaker unavailable timeslots", -1, solution);
        speaker2.setUnavailableTimeslotSet(new HashSet<>(Arrays.asList(slot1, slot2)));
        scoreverifier.assertHardWeight("Speaker unavailable timeslots", -1, solution);
    }

    @Test
    public void speakerWithConflictingTimeslots() {
        String talkType = "type1";
        Speaker speaker1 = new Speaker(1L)
                .withUnavailableTimeslotSet(Collections.emptySet())
                .withRequiredTimeslotTagSet(Collections.emptySet())
                .withPreferredTimeslotTagSet(Collections.emptySet())
                .withProhibitedTimeslotTagSet(Collections.emptySet())
                .withUndesiredTimeslotTagSet(Collections.emptySet());
        Talk talk1 = new Talk(1L)
                .withSpeakerList(Collections.emptyList())
                .withTalkType(talkType)
                .withRequiredTimeslotTagSet(Collections.emptySet())
                .withPreferredTimeslotTagSet(Collections.emptySet())
                .withProhibitedTimeslotTagSet(Collections.emptySet())
                .withUndesiredTimeslotTagSet(Collections.emptySet());
        Talk talk2 = new Talk(2L)
                .withSpeakerList(Collections.emptyList())
                .withTalkType(talkType)
                .withRequiredTimeslotTagSet(Collections.emptySet())
                .withPreferredTimeslotTagSet(Collections.emptySet())
                .withProhibitedTimeslotTagSet(Collections.emptySet())
                .withUndesiredTimeslotTagSet(Collections.emptySet());
        LocalDateTime start1 = LocalDateTime.of(2018, 1, 1, 9, 0);
        LocalDateTime end1 = LocalDateTime.of(2018, 1, 1, 10, 0);
        LocalDateTime start2 = LocalDateTime.of(2018, 1, 1, 9, 30);
        LocalDateTime end2 = LocalDateTime.of(2018, 1, 1, 10, 30);
        LocalDateTime start3 = LocalDateTime.of(2018, 1, 1, 10, 0);
        LocalDateTime end3 = LocalDateTime.of(2018, 1, 1, 11, 0);
        Timeslot slot1 = new Timeslot(1L)
                .withTalkType(talkType)
                .withStartDateTime(start1)
                .withEndDateTime(end1);
        Timeslot slot2 = new Timeslot(2L)
                .withTalkType(talkType)
                .withStartDateTime(start2)
                .withEndDateTime(end2);
        Timeslot slot3 = new Timeslot(3L)
                .withTalkType(talkType)
                .withStartDateTime(start3)
                .withEndDateTime(end3);
        ConferenceSolution solution = new ConferenceSolution(1L)
                .withTalkList(Arrays.asList(talk1,talk2))
                .withTimeslotList(Arrays.asList(slot1,slot2,slot3))
                .withRoomList(Collections.emptyList())
                .withSpeakerList(Arrays.asList(speaker1));
        scoreverifier.assertHardWeight("Speaker conflict", 0, solution);
        // speaker has no conflicting time slots
        talk1.withSpeakerList(Arrays.asList(speaker1)).withTimeslot(slot1);
        talk2.withSpeakerList(Arrays.asList(speaker1)).withTimeslot(slot3);
        scoreverifier.assertHardWeight("Speaker conflict", 0, solution);
        // speaker has no conflicting time slots
        talk2.withTimeslot(slot2);
        scoreverifier.assertHardWeight("Speaker conflict", -1, solution);
    }

    @Test
    public void speakerRequiredTimeSlotTag() {
        String tag1 = "tag1";
        String tag2 = "tag2";
        String tag3 = "tag3";
        String talkType = "type1";
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
        Talk talk1 = new Talk(1L)
                .withTalkType(talkType)
                .withSpeakerList(Collections.emptyList())
                .withPreferredTimeslotTagSet(Collections.emptySet())
                .withRequiredTimeslotTagSet(Collections.emptySet())
                .withProhibitedTimeslotTagSet(Collections.emptySet())
                .withUndesiredTimeslotTagSet(Collections.emptySet());
        LocalDateTime start1 = LocalDateTime.of(2018, 1, 1, 9, 0);
        LocalDateTime end1 = LocalDateTime.of(2018, 1, 1, 10, 0);
        Timeslot slot1 = new Timeslot(1L)
                .withTalkType(talkType)
                .withStartDateTime(start1)
                .withEndDateTime(end1);
        ConferenceSolution solution = new ConferenceSolution(1L)
                .withTalkList(Arrays.asList(talk1))
                .withTimeslotList(Arrays.asList(slot1))
                .withRoomList(Collections.emptyList())
                .withSpeakerList(Arrays.asList(speaker1, speaker2));
        // talk with 1 speaker, speaker without required time slot tag
        talk1.withSpeakerList(Arrays.asList(speaker1)).withTimeslot(slot1);
        slot1.setTagSet(Collections.emptySet());
        scoreverifier.assertHardWeight("Speaker required timeslot tag", 0, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreverifier.assertHardWeight("Speaker required timeslot tag", 0, solution);
        // talk with 1 speaker, speaker with required time slot tag, time slot without matching tag
        slot1.setTagSet(Collections.emptySet());
        speaker1.setRequiredTimeslotTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreverifier.assertHardWeight("Speaker required timeslot tag", -1, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag2, tag3)));
        scoreverifier.assertHardWeight("Speaker required timeslot tag", -1, solution);
        // talk with 1 speaker, speaker with required time slot tag, time slot with matching tag
        speaker1.setRequiredTimeslotTagSet(new HashSet<>(Arrays.asList(tag1)));
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreverifier.assertHardWeight("Speaker required timeslot tag", 0, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreverifier.assertHardWeight("Speaker required timeslot tag", 0, solution);
        // talk with 1 speaker, speaker with 2 required time slot tags
        speaker1.setRequiredTimeslotTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        slot1.setTagSet(Collections.emptySet());
        scoreverifier.assertHardWeight("Speaker required timeslot tag", -2, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreverifier.assertHardWeight("Speaker required timeslot tag", -1, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreverifier.assertHardWeight("Speaker required timeslot tag", 0, solution);
        // talk with 2 speakers, speakers with required time slot tag, time slot without matching tag
        talk1.withSpeakerList(Arrays.asList(speaker1, speaker2));
        slot1.setTagSet(Collections.emptySet());
        speaker1.setRequiredTimeslotTagSet(new HashSet<>(Arrays.asList(tag1)));
        speaker2.setRequiredTimeslotTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreverifier.assertHardWeight("Speaker required timeslot tag", -2, solution);
        // talk with 2 speakers, speakers with 2 required time slot tags, time slot without matching tag
        slot1.setTagSet(Collections.emptySet());
        speaker1.setRequiredTimeslotTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        speaker2.setRequiredTimeslotTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreverifier.assertHardWeight("Speaker required timeslot tag", -4, solution);
        // talk with 2 speakers, speakers with different required time slot tags, time slot with partially matching tag
        slot1.setTagSet(Collections.emptySet());
        speaker1.setRequiredTimeslotTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        speaker2.setRequiredTimeslotTagSet(new HashSet<>(Arrays.asList(tag1, tag3)));
        scoreverifier.assertHardWeight("Speaker required timeslot tag", -4, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreverifier.assertHardWeight("Speaker required timeslot tag", -2, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag2)));
        scoreverifier.assertHardWeight("Speaker required timeslot tag", -3, solution);
    }

    @Test
    public void speakerProhibitedTimeSlotTag() {
        String tag1 = "tag1";
        String tag2 = "tag2";
        String tag3 = "tag3";
        String talkType = "type1";
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
        Talk talk1 = new Talk(1L)
                .withTalkType(talkType)
                .withSpeakerList(Collections.emptyList())
                .withPreferredTimeslotTagSet(Collections.emptySet())
                .withRequiredTimeslotTagSet(Collections.emptySet())
                .withProhibitedTimeslotTagSet(Collections.emptySet())
                .withUndesiredTimeslotTagSet(Collections.emptySet());
        LocalDateTime start1 = LocalDateTime.of(2018, 1, 1, 9, 0);
        LocalDateTime end1 = LocalDateTime.of(2018, 1, 1, 10, 0);
        Timeslot slot1 = new Timeslot(1L)
                .withTalkType(talkType)
                .withStartDateTime(start1)
                .withEndDateTime(end1);
        ConferenceSolution solution = new ConferenceSolution(1L)
                .withTalkList(Arrays.asList(talk1))
                .withTimeslotList(Arrays.asList(slot1))
                .withRoomList(Collections.emptyList())
                .withSpeakerList(Arrays.asList(speaker1, speaker2));
        // talk with 1 speaker, speaker without prohibited time slot tag
        talk1.withSpeakerList(Arrays.asList(speaker1)).withTimeslot(slot1);
        slot1.setTagSet(Collections.emptySet());
        scoreverifier.assertHardWeight("Speaker prohibited timeslot tag", 0, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreverifier.assertHardWeight("Speaker prohibited timeslot tag", 0, solution);
        // talk with 1 speaker, speaker with prohibited time slot tag, time slot without matching tag
        speaker1.setProhibitedTimeslotTagSet(new HashSet<>(Arrays.asList(tag1)));
        slot1.setTagSet(Collections.emptySet());
        scoreverifier.assertHardWeight("Speaker prohibited timeslot tag", 0, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag2)));
        scoreverifier.assertHardWeight("Speaker prohibited timeslot tag", 0, solution);
        // talk with 1 speaker, speaker with prohibited time slot tag, time slot with matching tag
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreverifier.assertHardWeight("Speaker prohibited timeslot tag", -1, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreverifier.assertHardWeight("Speaker prohibited timeslot tag", -1, solution);
        // talk with 1 speaker, speaker with 2 required time slot tags
        speaker1.setProhibitedTimeslotTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        slot1.setTagSet(Collections.emptySet());
        scoreverifier.assertHardWeight("Speaker prohibited timeslot tag", 0, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreverifier.assertHardWeight("Speaker prohibited timeslot tag", -1, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreverifier.assertHardWeight("Speaker prohibited timeslot tag", -2, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2, tag3)));
        scoreverifier.assertHardWeight("Speaker prohibited timeslot tag", -2, solution);
        // talk with 2 speakers, speakers with prohibited time slot tag, time slot without matching tag
        talk1.withSpeakerList(Arrays.asList(speaker1, speaker2));
        slot1.setTagSet(Collections.emptySet());
        speaker1.setProhibitedTimeslotTagSet(new HashSet<>(Arrays.asList(tag1)));
        speaker2.setProhibitedTimeslotTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreverifier.assertHardWeight("Speaker prohibited timeslot tag", 0, solution);
        // talk with 2 speakers, speakers with prohibited time slot tags, time slot with matching tags
        speaker1.setProhibitedTimeslotTagSet(new HashSet<>(Arrays.asList(tag1)));
        speaker2.setProhibitedTimeslotTagSet(new HashSet<>(Arrays.asList(tag1)));
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreverifier.assertHardWeight("Speaker prohibited timeslot tag", -2, solution);
        speaker1.setProhibitedTimeslotTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreverifier.assertHardWeight("Speaker prohibited timeslot tag", -2, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreverifier.assertHardWeight("Speaker prohibited timeslot tag", -3, solution);
        speaker2.setProhibitedTimeslotTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreverifier.assertHardWeight("Speaker prohibited timeslot tag", -4, solution);
        speaker2.setProhibitedTimeslotTagSet(new HashSet<>(Arrays.asList(tag1, tag3)));
        scoreverifier.assertHardWeight("Speaker prohibited timeslot tag", -3, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2, tag3)));
        scoreverifier.assertHardWeight("Speaker prohibited timeslot tag", -4, solution);
    }

    @Test
    public void talkRequiredTimeslotTag() {
        String tag1 = "tag1";
        String tag2 = "tag2";
        String talkType = "type1";
        Talk talk1 = new Talk(1L)
                .withTalkType(talkType)
                .withSpeakerList(Collections.emptyList())
                .withPreferredTimeslotTagSet(Collections.emptySet())
                .withRequiredTimeslotTagSet(Collections.emptySet())
                .withProhibitedTimeslotTagSet(Collections.emptySet())
                .withUndesiredTimeslotTagSet(Collections.emptySet());
        LocalDateTime start1 = LocalDateTime.of(2018, 1, 1, 9, 0);
        LocalDateTime end1 = LocalDateTime.of(2018, 1, 1, 10, 0);
        Timeslot slot1 = new Timeslot(1L)
                .withTalkType(talkType)
                .withStartDateTime(start1)
                .withEndDateTime(end1);
        ConferenceSolution solution = new ConferenceSolution(1L)
                .withTalkList(Arrays.asList(talk1))
                .withTimeslotList(Arrays.asList(slot1))
                .withRoomList(Collections.emptyList())
                .withSpeakerList(Collections.emptyList());
        // talk without required time slot tag
        slot1.setTagSet(Collections.emptySet());
        talk1.withTimeslot(slot1);
        scoreverifier.assertHardWeight("Talk required timeslot tag", 0, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreverifier.assertHardWeight("Talk required timeslot tag", 0, solution);
        // talk with required time slot tag, time slot without matching tag
        talk1.withRequiredTimeslotTagSet(new HashSet<>(Arrays.asList(tag1)));
        slot1.setTagSet(Collections.emptySet());
        scoreverifier.assertHardWeight("Talk required timeslot tag", -1, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag2)));
        scoreverifier.assertHardWeight("Talk required timeslot tag", -1, solution);
        // talk with required time slot tag, time slot with matching tag
        talk1.withRequiredTimeslotTagSet(new HashSet<>(Arrays.asList(tag1)));
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreverifier.assertHardWeight("Talk required timeslot tag", 0, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreverifier.assertHardWeight("Talk required timeslot tag", 0, solution);
        // talk with 2 required time slot tags
        slot1.setTagSet(Collections.emptySet());
        talk1.withRequiredTimeslotTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreverifier.assertHardWeight("Talk required timeslot tag", -2, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreverifier.assertHardWeight("Talk required timeslot tag", -1, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreverifier.assertHardWeight("Talk required timeslot tag", 0, solution);
    }

    @Test
    public void talkProhibitedTimeslotTag() {
        String tag1 = "tag1";
        String tag2 = "tag2";
        String talkType = "type1";
        Talk talk1 = new Talk(1L)
                .withTalkType(talkType)
                .withSpeakerList(Collections.emptyList())
                .withPreferredTimeslotTagSet(Collections.emptySet())
                .withRequiredTimeslotTagSet(Collections.emptySet())
                .withProhibitedTimeslotTagSet(Collections.emptySet())
                .withUndesiredTimeslotTagSet(Collections.emptySet());
        LocalDateTime start1 = LocalDateTime.of(2018, 1, 1, 9, 0);
        LocalDateTime end1 = LocalDateTime.of(2018, 1, 1, 10, 0);
        Timeslot slot1 = new Timeslot(1L)
                .withTalkType(talkType)
                .withStartDateTime(start1)
                .withEndDateTime(end1);
        ConferenceSolution solution = new ConferenceSolution(1L)
                .withTalkList(Arrays.asList(talk1))
                .withTimeslotList(Arrays.asList(slot1))
                .withRoomList(Collections.emptyList())
                .withSpeakerList(Collections.emptyList());
        // talk without prohibited time slot tags
        slot1.setTagSet(Collections.emptySet());
        talk1.withTimeslot(slot1);
        scoreverifier.assertHardWeight("Talk prohibited timeslot tag", 0, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreverifier.assertHardWeight("Talk required timeslot tag", 0, solution);
        // talk with prohibited time slot tag, time slot without matching tag
        slot1.setTagSet(Collections.emptySet());
        talk1.withTimeslot(slot1).withProhibitedTimeslotTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreverifier.assertHardWeight("Talk prohibited timeslot tag", 0, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag2)));
        scoreverifier.assertHardWeight("Talk prohibited timeslot tag", 0, solution);
        // talk with prohibited time slot tag, time slot with matching tag
        talk1.withTimeslot(slot1).withProhibitedTimeslotTagSet(new HashSet<>(Arrays.asList(tag1)));
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreverifier.assertHardWeight("Talk prohibited timeslot tag", -1, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreverifier.assertHardWeight("Talk prohibited timeslot tag", -1, solution);
        // talk with 2 prohibited time slot tags
        talk1.withTimeslot(slot1).withProhibitedTimeslotTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        slot1.setTagSet(Collections.emptySet());
        scoreverifier.assertHardWeight("Talk prohibited timeslot tag", -0, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreverifier.assertHardWeight("Talk prohibited timeslot tag", -1, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreverifier.assertHardWeight("Talk prohibited timeslot tag", -2, solution);
    }

    @Test
    public void speakerRequiredRoomTag() {
        String tag1 = "tag1";
        String tag2 = "tag2";
        String tag3 = "tag3";
        Room room1 = new Room(1L);
        Talk talk1 = new Talk(1L)
                .withSpeakerList(Collections.emptyList())
                .withPreferredTimeslotTagSet(Collections.emptySet())
                .withRequiredTimeslotTagSet(Collections.emptySet())
                .withRequiredRoomTagSet(Collections.emptySet())
                .withPreferredRoomTagSet(Collections.emptySet())
                .withProhibitedRoomTagSet(Collections.emptySet())
                .withUndesiredRoomTagSet(Collections.emptySet());
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
                .withTalkList(Arrays.asList(talk1))
                .withTimeslotList(Collections.emptyList())
                .withRoomList(Arrays.asList(room1))
                .withSpeakerList(Arrays.asList(speaker1, speaker2));
        // talk with 1 speaker without required room tags
        room1.setTagSet(Collections.emptySet());
        talk1.withSpeakerList(Arrays.asList(speaker1)).withRoom(room1);
        scoreverifier.assertHardWeight("Speaker required room tag", 0, solution);
        // talk with 1 speaker, speaker with required room tag, room without matching tag
        speaker1.withRequiredRoomTagSet(new HashSet<>(Arrays.asList(tag1)));
        room1.setTagSet(Collections.emptySet());
        scoreverifier.assertHardWeight("Speaker required room tag", -1, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag3)));
        scoreverifier.assertHardWeight("Speaker required room tag", -1, solution);
        // talk with 1 speaker, speaker with required room tag, room with matching tag
        speaker1.withRequiredRoomTagSet(new HashSet<>(Arrays.asList(tag1)));
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreverifier.assertHardWeight("Speaker required room tag", -0, solution);
        // talk with 1 speaker, speaker with 2 required room tags
        speaker1.withRequiredRoomTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        room1.setTagSet(Collections.emptySet());
        scoreverifier.assertHardWeight("Speaker required room tag", -2, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreverifier.assertHardWeight("Speaker required room tag", -1, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreverifier.assertHardWeight("Speaker required room tag", 0, solution);
        // talk with 2 speakers
        talk1.withSpeakerList(Arrays.asList(speaker1, speaker2)).withRoom(room1);
        speaker1.withRequiredRoomTagSet(new HashSet<>(Arrays.asList(tag1)));
        speaker2.withRequiredRoomTagSet(new HashSet<>(Arrays.asList(tag1)));
        room1.setTagSet(Collections.emptySet());
        scoreverifier.assertHardWeight("Speaker required room tag", -2, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreverifier.assertHardWeight("Speaker required room tag", 0, solution);
        speaker2.withRequiredRoomTagSet(new HashSet<>(Arrays.asList(tag2)));
        room1.setTagSet(Collections.emptySet());
        scoreverifier.assertHardWeight("Speaker required room tag", -2, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreverifier.assertHardWeight("Speaker required room tag", -1, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreverifier.assertHardWeight("Speaker required room tag", 0, solution);
        speaker2.withRequiredRoomTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        room1.setTagSet(Collections.emptySet());
        scoreverifier.assertHardWeight("Speaker required room tag", -3, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreverifier.assertHardWeight("Speaker required room tag", -1, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreverifier.assertHardWeight("Speaker required room tag", 0, solution);
    }

    @Test
    public void speakerProhibitedRoomTag() {
        String tag1 = "tag1";
        String tag2 = "tag2";
        String tag3 = "tag3";
        Room room1 = new Room(1L);
        Talk talk1 = new Talk(1L)
                .withSpeakerList(Collections.emptyList())
                .withPreferredTimeslotTagSet(Collections.emptySet())
                .withRequiredTimeslotTagSet(Collections.emptySet())
                .withRequiredRoomTagSet(Collections.emptySet())
                .withPreferredRoomTagSet(Collections.emptySet())
                .withProhibitedRoomTagSet(Collections.emptySet())
                .withUndesiredRoomTagSet(Collections.emptySet());
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
                .withTalkList(Arrays.asList(talk1))
                .withTimeslotList(Collections.emptyList())
                .withRoomList(Arrays.asList(room1))
                .withSpeakerList(Arrays.asList(speaker1, speaker2));
        scoreverifier.assertHardWeight("Speaker prohibited room tag", 0, solution);
        // talk with 1 speaker without prohibited room tags
        room1.setTagSet(Collections.emptySet());
        talk1.withSpeakerList(Arrays.asList(speaker1)).withRoom(room1);
        scoreverifier.assertHardWeight("Speaker prohibited room tag", 0, solution);
        // talk with 1 speaker, speaker with prohibited room tag, room without matching tag
        speaker1.withProhibitedRoomTagSet(new HashSet<>(Arrays.asList(tag1)));
        room1.setTagSet(Collections.emptySet());
        scoreverifier.assertHardWeight("Speaker prohibited room tag", 0, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag3)));
        scoreverifier.assertHardWeight("Speaker prohibited room tag", 0, solution);
        // talk with 1 speaker, speaker with prohibited room tag, room with matching tag
        speaker1.withProhibitedRoomTagSet(new HashSet<>(Arrays.asList(tag1)));
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreverifier.assertHardWeight("Speaker prohibited room tag", -1, solution);
        // talk with 1 speaker, speaker with 2 prohibited room tags
        speaker1.withProhibitedRoomTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        room1.setTagSet(Collections.emptySet());
        scoreverifier.assertHardWeight("Speaker prohibited room tag", 0, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreverifier.assertHardWeight("Speaker prohibited room tag", -1, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreverifier.assertHardWeight("Speaker prohibited room tag", -2, solution);
        // talk with 2 speakers
        talk1.withSpeakerList(Arrays.asList(speaker1, speaker2)).withRoom(room1);
        speaker1.withProhibitedRoomTagSet(new HashSet<>(Arrays.asList(tag1)));
        speaker2.withProhibitedRoomTagSet(new HashSet<>(Arrays.asList(tag1)));
        room1.setTagSet(Collections.emptySet());
        scoreverifier.assertHardWeight("Speaker prohibited room tag", 0, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreverifier.assertHardWeight("Speaker prohibited room tag", -2, solution);
        speaker2.withProhibitedRoomTagSet(new HashSet<>(Arrays.asList(tag2)));
        room1.setTagSet(Collections.emptySet());
        scoreverifier.assertHardWeight("Speaker prohibited room tag", 0, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreverifier.assertHardWeight("Speaker prohibited room tag", -1, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreverifier.assertHardWeight("Speaker prohibited room tag", -2, solution);
        speaker2.withProhibitedRoomTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        room1.setTagSet(Collections.emptySet());
        scoreverifier.assertHardWeight("Speaker prohibited room tag", 0, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreverifier.assertHardWeight("Speaker prohibited room tag", -2, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreverifier.assertHardWeight("Speaker prohibited room tag", -3, solution);
    }

    @Test
    public void talkRequiredRoomTag() {
        String tag1 = "tag1";
        String tag2 = "tag2";
        String talkType = "type1";
        Room room1 = new Room(1L);
        Talk talk1 = new Talk(1L)
                .withTalkType(talkType)
                .withSpeakerList(Collections.emptyList())
                .withPreferredTimeslotTagSet(Collections.emptySet())
                .withRequiredTimeslotTagSet(Collections.emptySet())
                .withPreferredRoomTagSet(Collections.emptySet())
                .withRequiredRoomTagSet(Collections.emptySet())
                .withProhibitedRoomTagSet(Collections.emptySet())
                .withUndesiredRoomTagSet(Collections.emptySet());
        ConferenceSolution solution = new ConferenceSolution(1L)
                .withTalkList(Arrays.asList(talk1))
                .withTimeslotList(Collections.emptyList())
                .withRoomList(Collections.emptyList())
                .withSpeakerList(Collections.emptyList());
        // talk without required room tags
        room1.setTagSet(Collections.emptySet());
        talk1.withRoom(room1);
        scoreverifier.assertHardWeight("Talk required room tag", 0, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreverifier.assertHardWeight("Talk required room tag", 0, solution);
        // talk with required room tag, room without matching tag
        talk1.withRequiredRoomTagSet(new HashSet<>(Arrays.asList(tag1)));
        room1.setTagSet(Collections.emptySet());
        scoreverifier.assertHardWeight("Talk required room tag", -1, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag2)));
        scoreverifier.assertHardWeight("Talk required room tag", -1, solution);
        // talk with required room tag, room with matching tag
        talk1.withRequiredRoomTagSet(new HashSet<>(Arrays.asList(tag1)));
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreverifier.assertHardWeight("Talk required room tag", 0, solution);
        // talk with 2 required room tags
        room1.setTagSet(Collections.emptySet());
        talk1.withRequiredRoomTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreverifier.assertHardWeight("Talk required room tag", -2, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreverifier.assertHardWeight("Talk required room tag", -1, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreverifier.assertHardWeight("Talk required room tag", 0, solution);
    }

    @Test
    public void talkProhibitedRoomTag() {
        String tag1 = "tag1";
        String tag2 = "tag2";
        String talkType = "type1";
        Room room1 = new Room(1L);
        Talk talk1 = new Talk(1L)
                .withTalkType(talkType)
                .withSpeakerList(Collections.emptyList())
                .withPreferredTimeslotTagSet(Collections.emptySet())
                .withRequiredTimeslotTagSet(Collections.emptySet())
                .withPreferredRoomTagSet(Collections.emptySet())
                .withRequiredRoomTagSet(Collections.emptySet())
                .withProhibitedRoomTagSet(Collections.emptySet())
                .withUndesiredRoomTagSet(Collections.emptySet());
        ConferenceSolution solution = new ConferenceSolution(1L)
                .withTalkList(Arrays.asList(talk1))
                .withTimeslotList(Collections.emptyList())
                .withRoomList(Collections.emptyList())
                .withSpeakerList(Collections.emptyList());
        // talk without prohibited room tags
        room1.setTagSet(Collections.emptySet());
        talk1.withRoom(room1);
        scoreverifier.assertHardWeight("Talk prohibited room tag", 0, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreverifier.assertHardWeight("Talk prohibited room tag", 0, solution);
        // talk with prohibited room tag, room without matching tag
        talk1.withProhibitedRoomTagSet(new HashSet<>(Arrays.asList(tag1)));
        room1.setTagSet(Collections.emptySet());
        scoreverifier.assertHardWeight("Talk prohibited room tag", 0, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag2)));
        scoreverifier.assertHardWeight("Talk prohibited room tag", 0, solution);
        // talk with prohibited room tag, room with matching tag
        talk1.withProhibitedRoomTagSet(new HashSet<>(Arrays.asList(tag1)));
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreverifier.assertHardWeight("Talk prohibited room tag", -1, solution);
        // talk with 2 prohibited room tags
        room1.setTagSet(Collections.emptySet());
        talk1.withProhibitedRoomTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreverifier.assertHardWeight("Talk prohibited room tag", 0, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreverifier.assertHardWeight("Talk prohibited room tag", -1, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreverifier.assertHardWeight("Talk prohibited room tag", -2, solution);
    }

    @Test
    public void languageDiversity() {
        ConferenceParametrization parametrization = new ConferenceParametrization(1L);
        String talkType = "talktype";
        String language1 = "language1";
        String language2 = "language2";
        LocalDateTime start1 = LocalDateTime.of(2018, 1, 1, 9, 0);
        LocalDateTime end1 = LocalDateTime.of(2018, 1, 1, 10, 0);
        LocalDateTime start2 = LocalDateTime.of(2018, 1, 1, 9, 30);
        LocalDateTime end2 = LocalDateTime.of(2018, 1, 1, 10, 30);
        Timeslot slot1 = new Timeslot(1L)
                .withTalkType(talkType)
                .withStartDateTime(start1)
                .withEndDateTime(end1);
        Timeslot slot2 = new Timeslot(2L)
                .withTalkType(talkType)
                .withStartDateTime(start2)
                .withEndDateTime(end2);
        Talk talk1 = new Talk(1L)
                .withTalkType(talkType)
                .withSpeakerList(Collections.emptyList())
                .withPreferredTimeslotTagSet(Collections.emptySet())
                .withRequiredTimeslotTagSet(Collections.emptySet())
                .withProhibitedTimeslotTagSet(Collections.emptySet())
                .withUndesiredTimeslotTagSet(Collections.emptySet())
                .withPreferredRoomTagSet(Collections.emptySet())
                .withRequiredRoomTagSet(Collections.emptySet())
                .withThemeTagSet(Collections.emptySet())
                .withSectorTagSet(Collections.emptySet());
        Talk talk2 = new Talk(2L)
                .withTalkType(talkType)
                .withSpeakerList(Collections.emptyList())
                .withPreferredTimeslotTagSet(Collections.emptySet())
                .withRequiredTimeslotTagSet(Collections.emptySet())
                .withProhibitedTimeslotTagSet(Collections.emptySet())
                .withUndesiredTimeslotTagSet(Collections.emptySet())
                .withPreferredRoomTagSet(Collections.emptySet())
                .withRequiredRoomTagSet(Collections.emptySet())
                .withThemeTagSet(Collections.emptySet())
                .withSectorTagSet(Collections.emptySet());
        ConferenceSolution solution = new ConferenceSolution(1L)
                .withParametrization(parametrization)
                .withTalkList(Arrays.asList(talk1, talk2))
                .withTimeslotList(Collections.emptyList())
                .withRoomList(Collections.emptyList())
                .withSpeakerList(Collections.emptyList());
        scoreverifier.assertHardWeight("Language diversity", 0, solution);
        // 2 talks with the same language and the same time slot
        parametrization.setLanguageDiversity(1);
        talk1.withTimeslot(slot1).withLanguage(language1);
        talk2.withTimeslot(slot1).withLanguage(language1);
        scoreverifier.assertHardWeight("Language diversity", 0, solution);
        // 2 talks with the same time slot with different languages
        talk2.withLanguage(language2);
        scoreverifier.assertHardWeight("Language diversity", 1, solution);
        // 2 talks with the same time slot with different languages and language diversity weight = 2
        parametrization.setLanguageDiversity(2);
        scoreverifier.assertHardWeight("Language diversity", 2, solution);
        // 2 talks with different time slots with different languages
        talk2.withTimeslot(slot2);
        scoreverifier.assertHardWeight("Language diversity", 0, solution);
        // 2 talks with different time slot with the same language
        talk2.withLanguage(language1);
        scoreverifier.assertHardWeight("Language diversity", 0, solution);
    }
}