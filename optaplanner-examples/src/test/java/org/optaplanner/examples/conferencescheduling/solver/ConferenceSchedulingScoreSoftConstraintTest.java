package org.optaplanner.examples.conferencescheduling.solver;

import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.LANGUAGE_DIVERSITY;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.SECTOR_CONFLICT;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.SPEAKER_PREFERRED_ROOM_TAGS;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.SPEAKER_PREFERRED_TIMESLOT_TAGS;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.SPEAKER_UNDESIRED_ROOM_TAGS;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.SPEAKER_UNDESIRED_TIMESLOT_TAGS;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.TALK_PREFERRED_ROOM_TAGS;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.TALK_PREFERRED_TIMESLOT_TAGS;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.TALK_UNDESIRED_ROOM_TAGS;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.TALK_UNDESIRED_TIMESLOT_TAGS;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration.THEME_TRACK_CONFLICT;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.examples.conferencescheduling.app.ConferenceSchedulingApp;
import org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration;
import org.optaplanner.examples.conferencescheduling.domain.ConferenceSolution;
import org.optaplanner.examples.conferencescheduling.domain.Room;
import org.optaplanner.examples.conferencescheduling.domain.Speaker;
import org.optaplanner.examples.conferencescheduling.domain.Talk;
import org.optaplanner.examples.conferencescheduling.domain.TalkType;
import org.optaplanner.examples.conferencescheduling.domain.Timeslot;
import org.optaplanner.test.impl.score.buildin.hardmediumsoft.HardMediumSoftScoreVerifier;

// TODO https://issues.redhat.com/browse/PLANNER-1335
@Disabled("Temporarily disabled until ScoreVerifier.assertPenalty() exists to avoid unneeded refactor")
public class ConferenceSchedulingScoreSoftConstraintTest {

    private HardMediumSoftScoreVerifier<ConferenceSolution> scoreVerifier = new HardMediumSoftScoreVerifier<>(
            SolverFactory.createFromXmlResource(ConferenceSchedulingApp.SOLVER_CONFIG));

    @Test
    public void themeConflict() {
        ConferenceConstraintConfiguration constraintConfiguration = new ConferenceConstraintConfiguration(1L);
        TalkType talkType = new TalkType(0L, "type1");
        String theme1 = "theme1";
        String theme2 = "theme2";
        String theme3 = "theme3";
        String theme4 = "theme4";
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
                .withConstraintConfiguration(constraintConfiguration)
                .withTalkTypeList(Collections.singletonList(talkType))
                .withTalkList(Arrays.asList(talk1, talk2))
                .withTimeslotList(Arrays.asList(slot1, slot2, slot3))
                .withRoomList(Collections.emptyList())
                .withSpeakerList(Collections.emptyList());
        constraintConfiguration.setThemeTrackConflict(HardMediumSoftScore.ofSoft(1));
        scoreVerifier.assertSoftWeight(THEME_TRACK_CONFLICT, 0, solution);
        // talks with overlapping time slots without theme track conflict
        talk1.withTimeslot(slot1).withThemeTrackTagSet(new HashSet<>(Arrays.asList(theme1, theme2)));
        talk2.withTimeslot(slot2).withThemeTrackTagSet(new HashSet<>(Arrays.asList(theme3, theme4)));
        scoreVerifier.assertSoftWeight(THEME_TRACK_CONFLICT, 0, solution);
        // talks with overlapping time slots with 1 theme track conflict
        talk2.withThemeTrackTagSet(new HashSet<>(Arrays.asList(theme1, theme3, theme4)));
        scoreVerifier.assertSoftWeight(THEME_TRACK_CONFLICT, -1, solution);
        // talks with overlapping time slots with 2 theme track conflicts
        talk1.withTimeslot(slot1).withThemeTrackTagSet(new HashSet<>(Arrays.asList(theme1, theme2, theme3)));
        scoreVerifier.assertSoftWeight(THEME_TRACK_CONFLICT, -2, solution);
        // talks with overlapping time slots with 2 theme track conflicts and theme conflict weight 2
        constraintConfiguration.setThemeTrackConflict(HardMediumSoftScore.ofSoft(2));
        scoreVerifier.assertSoftWeight(THEME_TRACK_CONFLICT, -4, solution);
        // talks with non overlapping time slots and theme track conflicts
        talk2.setTimeslot(slot3);
        scoreVerifier.assertSoftWeight(THEME_TRACK_CONFLICT, 0, solution);
    }

    @Test
    public void sectorConflict() {
        ConferenceConstraintConfiguration constraintConfiguration = new ConferenceConstraintConfiguration(1L);
        TalkType talkType = new TalkType(0L, "type1");
        String sector1 = "sector1";
        String sector2 = "sector2";
        String sector3 = "sector3";
        String sector4 = "sector4";
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
                .withConstraintConfiguration(constraintConfiguration)
                .withTalkTypeList(Collections.singletonList(talkType))
                .withTalkList(Arrays.asList(talk1, talk2))
                .withTimeslotList(Arrays.asList(slot1, slot2, slot3))
                .withRoomList(Collections.emptyList())
                .withSpeakerList(Collections.emptyList());
        constraintConfiguration.setSectorConflict(HardMediumSoftScore.ofSoft(1));
        scoreVerifier.assertSoftWeight(SECTOR_CONFLICT, 0, solution);
        // talks with overlapping time slots without sector conflict
        constraintConfiguration.setSectorConflict(HardMediumSoftScore.ofSoft(1));
        talk1.withTimeslot(slot1).withSectorTagSet(new HashSet<>(Arrays.asList(sector1, sector2)));
        talk2.withTimeslot(slot2).withSectorTagSet(new HashSet<>(Arrays.asList(sector3, sector4)));
        scoreVerifier.assertSoftWeight(SECTOR_CONFLICT, 0, solution);
        // talks with overlapping time slots with 1 sector conflict
        talk2.withSectorTagSet(new HashSet<>(Arrays.asList(sector1, sector3, sector4)));
        scoreVerifier.assertSoftWeight(SECTOR_CONFLICT, -1, solution);
        // talks with overlapping time slots with 2 sector conflicts
        talk1.withTimeslot(slot1).withSectorTagSet(new HashSet<>(Arrays.asList(sector1, sector2, sector3)));
        scoreVerifier.assertSoftWeight(SECTOR_CONFLICT, -2, solution);
        // talks with overlapping time slots with 2 sector conflicts and sector conflict weight 2
        constraintConfiguration.setSectorConflict(HardMediumSoftScore.ofSoft(2));
        scoreVerifier.assertSoftWeight(SECTOR_CONFLICT, -4, solution);
        // talks with non overlapping time slots and sector conflicts
        talk2.setTimeslot(slot3);
        scoreVerifier.assertSoftWeight(SECTOR_CONFLICT, 0, solution);
    }

    @Test
    public void languageDiversity() {
        ConferenceConstraintConfiguration constraintConfiguration = new ConferenceConstraintConfiguration(1L);
        TalkType talkType = new TalkType(0L, "type1");
        String language1 = "language1";
        String language2 = "language2";
        LocalDateTime start1 = LocalDateTime.of(2018, 1, 1, 9, 0);
        LocalDateTime end1 = LocalDateTime.of(2018, 1, 1, 10, 0);
        LocalDateTime start2 = LocalDateTime.of(2018, 1, 1, 9, 30);
        LocalDateTime end2 = LocalDateTime.of(2018, 1, 1, 10, 30);
        Timeslot slot1 = new Timeslot(1L)
                .withTalkTypeSet(Collections.singleton(talkType))
                .withStartDateTime(start1)
                .withEndDateTime(end1);
        Timeslot slot2 = new Timeslot(2L)
                .withTalkTypeSet(Collections.singleton(talkType))
                .withStartDateTime(start2)
                .withEndDateTime(end2);
        Talk talk1 = createTalk(1L).withTalkType(talkType);
        Talk talk2 = createTalk(2L).withTalkType(talkType);
        ConferenceSolution solution = new ConferenceSolution(1L)
                .withConstraintConfiguration(constraintConfiguration)
                .withTalkTypeList(Collections.singletonList(talkType))
                .withTalkList(Arrays.asList(talk1, talk2))
                .withTimeslotList(Collections.emptyList())
                .withRoomList(Collections.emptyList())
                .withSpeakerList(Collections.emptyList());
        scoreVerifier.assertSoftWeight(LANGUAGE_DIVERSITY, 0, solution);
        // 2 talks with the same language and the same time slot
        constraintConfiguration.setLanguageDiversity(HardMediumSoftScore.ofSoft(1));
        talk1.withTimeslot(slot1).withLanguage(language1);
        talk2.withTimeslot(slot1).withLanguage(language1);
        scoreVerifier.assertSoftWeight(LANGUAGE_DIVERSITY, 0, solution);
        // 2 talks with the same time slot with different languages
        talk2.withLanguage(language2);
        scoreVerifier.assertSoftWeight(LANGUAGE_DIVERSITY, 1, solution);
        // 2 talks with the same time slot with different languages and language diversity weight = 2
        constraintConfiguration.setLanguageDiversity(HardMediumSoftScore.ofSoft(2));
        scoreVerifier.assertSoftWeight(LANGUAGE_DIVERSITY, 2, solution);
        // 2 talks with different time slots with different languages
        talk2.withTimeslot(slot2);
        scoreVerifier.assertSoftWeight(LANGUAGE_DIVERSITY, 0, solution);
        // 2 talks with different time slot with the same language
        talk2.withLanguage(language1);
        scoreVerifier.assertSoftWeight(LANGUAGE_DIVERSITY, 0, solution);
    }

    @Test
    public void speakerPreferredTimeslot() {
        ConferenceConstraintConfiguration constraintConfiguration = new ConferenceConstraintConfiguration(1L);
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
                .withConstraintConfiguration(constraintConfiguration)
                .withTalkTypeList(Collections.singletonList(talkType))
                .withTalkList(Arrays.asList(talk1))
                .withTimeslotList(Arrays.asList(slot1))
                .withRoomList(Collections.emptyList())
                .withSpeakerList(Arrays.asList(speaker1, speaker2));
        constraintConfiguration.setSpeakerPreferredTimeslotTags(HardMediumSoftScore.ofSoft(1));
        scoreVerifier.assertSoftWeight(SPEAKER_PREFERRED_TIMESLOT_TAGS, 0, solution);
        // talk with 1 speaker, speaker without preferred time slot tag
        slot1.setTagSet(Collections.emptySet());
        talk1.withSpeakerList(Arrays.asList(speaker1)).withTimeslot(slot1);
        scoreVerifier.assertSoftWeight(SPEAKER_PREFERRED_TIMESLOT_TAGS, 0, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertSoftWeight(SPEAKER_PREFERRED_TIMESLOT_TAGS, 0, solution);
        // talk with 1 speaker, speaker with preferred time slot tag, time slot without matching tag
        slot1.setTagSet(Collections.emptySet());
        speaker1.setPreferredTimeslotTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertSoftWeight(SPEAKER_PREFERRED_TIMESLOT_TAGS, -1, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag2, tag3)));
        scoreVerifier.assertSoftWeight(SPEAKER_PREFERRED_TIMESLOT_TAGS, -1, solution);
        // talk with 1 speaker, speaker with preferred time slot tag, time slot without matching tag, weight = 2
        constraintConfiguration.setSpeakerPreferredTimeslotTags(HardMediumSoftScore.ofSoft(2));
        scoreVerifier.assertSoftWeight(SPEAKER_PREFERRED_TIMESLOT_TAGS, -2, solution);
        // talk with 1 speaker, speaker with preferred time slot tag, time slot with matching tag
        constraintConfiguration.setSpeakerPreferredTimeslotTags(HardMediumSoftScore.ofSoft(1));
        speaker1.setPreferredTimeslotTagSet(new HashSet<>(Arrays.asList(tag1)));
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertSoftWeight(SPEAKER_PREFERRED_TIMESLOT_TAGS, 0, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreVerifier.assertSoftWeight(SPEAKER_PREFERRED_TIMESLOT_TAGS, 0, solution);
        // talk with 1 speaker, speaker with 2 preferred time slot tags
        speaker1.setPreferredTimeslotTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        slot1.setTagSet(Collections.emptySet());
        scoreVerifier.assertSoftWeight(SPEAKER_PREFERRED_TIMESLOT_TAGS, -2, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertSoftWeight(SPEAKER_PREFERRED_TIMESLOT_TAGS, -1, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreVerifier.assertSoftWeight(SPEAKER_PREFERRED_TIMESLOT_TAGS, 0, solution);
        //talk with 2 speakers
        talk1.withSpeakerList(Arrays.asList(speaker1, speaker2));
        slot1.setTagSet(Collections.emptySet());
        speaker1.setPreferredTimeslotTagSet(new HashSet<>(Arrays.asList(tag1)));
        speaker2.setPreferredTimeslotTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertSoftWeight(SPEAKER_PREFERRED_TIMESLOT_TAGS, -2, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertSoftWeight(SPEAKER_PREFERRED_TIMESLOT_TAGS, 0, solution);
        slot1.setTagSet(Collections.emptySet());
        speaker1.setPreferredTimeslotTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        speaker2.setPreferredTimeslotTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreVerifier.assertSoftWeight(SPEAKER_PREFERRED_TIMESLOT_TAGS, -4, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertSoftWeight(SPEAKER_PREFERRED_TIMESLOT_TAGS, -2, solution);
        slot1.setTagSet(Collections.emptySet());
        speaker1.setPreferredTimeslotTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        speaker2.setPreferredTimeslotTagSet(new HashSet<>(Arrays.asList(tag1, tag3)));
        scoreVerifier.assertSoftWeight(SPEAKER_PREFERRED_TIMESLOT_TAGS, -4, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertSoftWeight(SPEAKER_PREFERRED_TIMESLOT_TAGS, -2, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag2)));
        scoreVerifier.assertSoftWeight(SPEAKER_PREFERRED_TIMESLOT_TAGS, -3, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2, tag3)));
        scoreVerifier.assertSoftWeight(SPEAKER_PREFERRED_TIMESLOT_TAGS, 0, solution);
    }

    @Test
    public void talkPreferredTimeslotTag() {
        ConferenceConstraintConfiguration constraintConfiguration = new ConferenceConstraintConfiguration(1L);
        String tag1 = "tag1";
        String tag2 = "tag2";
        TalkType talkType = new TalkType(0L, "type1");
        Talk talk1 = createTalk(1L).withTalkType(talkType);
        LocalDateTime start1 = LocalDateTime.of(2018, 1, 1, 9, 0);
        LocalDateTime end1 = LocalDateTime.of(2018, 1, 1, 10, 0);
        Timeslot slot1 = new Timeslot(1L)
                .withTalkTypeSet(Collections.singleton(talkType))
                .withStartDateTime(start1)
                .withEndDateTime(end1);
        ConferenceSolution solution = new ConferenceSolution(1L)
                .withConstraintConfiguration(constraintConfiguration)
                .withTalkTypeList(Collections.singletonList(talkType))
                .withTalkList(Arrays.asList(talk1))
                .withTimeslotList(Arrays.asList(slot1))
                .withRoomList(Collections.emptyList())
                .withSpeakerList(Collections.emptyList());
        constraintConfiguration.setTalkPreferredTimeslotTags(HardMediumSoftScore.ofSoft(1));
        scoreVerifier.assertSoftWeight(TALK_PREFERRED_TIMESLOT_TAGS, 0, solution);
        // talk without preferred time slot tags
        slot1.setTagSet(Collections.emptySet());
        talk1.withTimeslot(slot1);
        scoreVerifier.assertSoftWeight(TALK_PREFERRED_TIMESLOT_TAGS, 0, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertSoftWeight(TALK_PREFERRED_TIMESLOT_TAGS, 0, solution);
        // talk with preferred time slot tag, time slot without matching tag
        talk1.withPreferredTimeslotTagSet(new HashSet<>(Arrays.asList(tag1)));
        slot1.setTagSet(Collections.emptySet());
        scoreVerifier.assertSoftWeight(TALK_PREFERRED_TIMESLOT_TAGS, -1, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag2)));
        scoreVerifier.assertSoftWeight(TALK_PREFERRED_TIMESLOT_TAGS, -1, solution);
        // talk with preferred time slot tag, time slot without matching tag, weight = 2
        constraintConfiguration.setTalkPreferredTimeslotTags(HardMediumSoftScore.ofSoft(2));
        scoreVerifier.assertSoftWeight(TALK_PREFERRED_TIMESLOT_TAGS, -2, solution);
        // talk with required time slot tag, time slot with matching tag
        constraintConfiguration.setTalkPreferredTimeslotTags(HardMediumSoftScore.ofSoft(1));
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertSoftWeight(TALK_PREFERRED_TIMESLOT_TAGS, 0, solution);
        // talk with 2 preferred time slot tags
        slot1.setTagSet(Collections.emptySet());
        talk1.withPreferredTimeslotTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreVerifier.assertSoftWeight(TALK_PREFERRED_TIMESLOT_TAGS, -2, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertSoftWeight(TALK_PREFERRED_TIMESLOT_TAGS, -1, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreVerifier.assertSoftWeight(TALK_PREFERRED_TIMESLOT_TAGS, 0, solution);
    }

    @Test
    public void speakerUndesiredTimeslot() {
        ConferenceConstraintConfiguration constraintConfiguration = new ConferenceConstraintConfiguration(1L);
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
                .withConstraintConfiguration(constraintConfiguration)
                .withTalkTypeList(Collections.singletonList(talkType))
                .withTalkList(Arrays.asList(talk1))
                .withTimeslotList(Arrays.asList(slot1))
                .withRoomList(Collections.emptyList())
                .withSpeakerList(Arrays.asList(speaker1, speaker2));
        constraintConfiguration.setSpeakerUndesiredTimeslotTags(HardMediumSoftScore.ofSoft(1));
        scoreVerifier.assertSoftWeight(SPEAKER_UNDESIRED_TIMESLOT_TAGS, 0, solution);
        // talk with 1 speaker, speaker without undesired time slot tag
        slot1.setTagSet(Collections.emptySet());
        talk1.withSpeakerList(Arrays.asList(speaker1)).withTimeslot(slot1);
        scoreVerifier.assertSoftWeight(SPEAKER_UNDESIRED_TIMESLOT_TAGS, 0, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertSoftWeight(SPEAKER_UNDESIRED_TIMESLOT_TAGS, 0, solution);
        // talk with 1 speaker, speaker with undesired time slot tag, time slot without matching tag
        slot1.setTagSet(Collections.emptySet());
        speaker1.setUndesiredTimeslotTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertSoftWeight(SPEAKER_UNDESIRED_TIMESLOT_TAGS, 0, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag2, tag3)));
        scoreVerifier.assertSoftWeight(SPEAKER_UNDESIRED_TIMESLOT_TAGS, 0, solution);
        // talk with 1 speaker, speaker with undesired time slot tag, time slot with matching tag
        speaker1.setUndesiredTimeslotTagSet(new HashSet<>(Arrays.asList(tag1)));
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertSoftWeight(SPEAKER_UNDESIRED_TIMESLOT_TAGS, -1, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreVerifier.assertSoftWeight(SPEAKER_UNDESIRED_TIMESLOT_TAGS, -1, solution);
        // talk with 1 speaker, speaker with undesired time slot tag, time slot with matching tag, weight = 2
        constraintConfiguration.setSpeakerUndesiredTimeslotTags(HardMediumSoftScore.ofSoft(2));
        scoreVerifier.assertSoftWeight(SPEAKER_UNDESIRED_TIMESLOT_TAGS, -2, solution);
        // talk with 1 speaker, speaker with 2 undesired time slot tags
        constraintConfiguration.setSpeakerUndesiredTimeslotTags(HardMediumSoftScore.ofSoft(1));
        speaker1.setUndesiredTimeslotTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        slot1.setTagSet(Collections.emptySet());
        scoreVerifier.assertSoftWeight(SPEAKER_UNDESIRED_TIMESLOT_TAGS, 0, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertSoftWeight(SPEAKER_UNDESIRED_TIMESLOT_TAGS, -1, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreVerifier.assertSoftWeight(SPEAKER_UNDESIRED_TIMESLOT_TAGS, -2, solution);
        // talk with 2 speakers
        talk1.withSpeakerList(Arrays.asList(speaker1, speaker2));
        slot1.setTagSet(Collections.emptySet());
        speaker1.setUndesiredTimeslotTagSet(new HashSet<>(Arrays.asList(tag1)));
        speaker2.setUndesiredTimeslotTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertSoftWeight(SPEAKER_UNDESIRED_TIMESLOT_TAGS, 0, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertSoftWeight(SPEAKER_UNDESIRED_TIMESLOT_TAGS, -2, solution);
        speaker1.setUndesiredTimeslotTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        speaker2.setUndesiredTimeslotTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreVerifier.assertSoftWeight(SPEAKER_UNDESIRED_TIMESLOT_TAGS, -2, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreVerifier.assertSoftWeight(SPEAKER_UNDESIRED_TIMESLOT_TAGS, -4, solution);
        speaker1.setUndesiredTimeslotTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        speaker2.setUndesiredTimeslotTagSet(new HashSet<>(Arrays.asList(tag1, tag3)));
        scoreVerifier.assertSoftWeight(SPEAKER_UNDESIRED_TIMESLOT_TAGS, -3, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2, tag3)));
        scoreVerifier.assertSoftWeight(SPEAKER_UNDESIRED_TIMESLOT_TAGS, -4, solution);
    }

    @Test
    public void talkUndesiredTimeslotTag() {
        ConferenceConstraintConfiguration constraintConfiguration = new ConferenceConstraintConfiguration(1L);
        String tag1 = "tag1";
        String tag2 = "tag2";
        TalkType talkType = new TalkType(0L, "type1");
        Talk talk1 = createTalk(1L).withTalkType(talkType);
        LocalDateTime start1 = LocalDateTime.of(2018, 1, 1, 9, 0);
        LocalDateTime end1 = LocalDateTime.of(2018, 1, 1, 10, 0);
        Timeslot slot1 = new Timeslot(1L)
                .withTalkTypeSet(Collections.singleton(talkType))
                .withStartDateTime(start1)
                .withEndDateTime(end1);
        ConferenceSolution solution = new ConferenceSolution(1L)
                .withConstraintConfiguration(constraintConfiguration)
                .withTalkTypeList(Collections.singletonList(talkType))
                .withTalkList(Arrays.asList(talk1))
                .withTimeslotList(Arrays.asList(slot1))
                .withRoomList(Collections.emptyList())
                .withSpeakerList(Collections.emptyList());
        constraintConfiguration.setTalkUndesiredTimeslotTags(HardMediumSoftScore.ofSoft(1));
        // talk without undesired time slot tags
        slot1.setTagSet(Collections.emptySet());
        talk1.withTimeslot(slot1);
        scoreVerifier.assertSoftWeight(TALK_UNDESIRED_TIMESLOT_TAGS, 0, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertSoftWeight("Talk required timeslot tag", 0, solution);
        // talk with undesired time slot tag, time slot without matching tag
        slot1.setTagSet(Collections.emptySet());
        talk1.withTimeslot(slot1).withUndesiredTimeslotTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertSoftWeight(TALK_UNDESIRED_TIMESLOT_TAGS, 0, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag2)));
        scoreVerifier.assertSoftWeight(TALK_UNDESIRED_TIMESLOT_TAGS, 0, solution);
        // talk with undesired time slot tag, time slot with matching tag
        talk1.withTimeslot(slot1).withUndesiredTimeslotTagSet(new HashSet<>(Arrays.asList(tag1)));
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertSoftWeight(TALK_UNDESIRED_TIMESLOT_TAGS, -1, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreVerifier.assertSoftWeight(TALK_UNDESIRED_TIMESLOT_TAGS, -1, solution);
        // talk with undesired time slot tag, time slot with matching tag, weight = 2
        constraintConfiguration.setTalkUndesiredTimeslotTags(HardMediumSoftScore.ofSoft(2));
        scoreVerifier.assertSoftWeight(TALK_UNDESIRED_TIMESLOT_TAGS, -2, solution);
        // talk with 2 undesired time slot tags
        constraintConfiguration.setTalkUndesiredTimeslotTags(HardMediumSoftScore.ofSoft(1));
        talk1.withTimeslot(slot1).withUndesiredTimeslotTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        slot1.setTagSet(Collections.emptySet());
        scoreVerifier.assertSoftWeight(TALK_UNDESIRED_TIMESLOT_TAGS, -0, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertSoftWeight(TALK_UNDESIRED_TIMESLOT_TAGS, -1, solution);
        slot1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreVerifier.assertSoftWeight(TALK_UNDESIRED_TIMESLOT_TAGS, -2, solution);
    }

    @Test
    public void speakerPreferredRoomTag() {
        ConferenceConstraintConfiguration constraintConfiguration = new ConferenceConstraintConfiguration(1L);
        String tag1 = "tag1";
        String tag2 = "tag2";
        TalkType talkType = new TalkType(0L, "type1");
        Room room1 = new Room(1L).withTalkTypeSet(Collections.singleton(talkType));
        Talk talk1 = createTalk(1L);
        Speaker speaker1 = new Speaker(1L)
                .withUnavailableTimeslotSet(Collections.emptySet())
                .withRequiredTimeslotTagSet(Collections.emptySet())
                .withPreferredTimeslotTagSet(Collections.emptySet())
                .withRequiredRoomTagSet(Collections.emptySet())
                .withPreferredRoomTagSet(Collections.emptySet())
                .withProhibitedRoomTagSet(Collections.emptySet())
                .withUndesiredRoomTagSet(Collections.emptySet());
        Speaker speaker2 = new Speaker(2L)
                .withUnavailableTimeslotSet(Collections.emptySet())
                .withRequiredTimeslotTagSet(Collections.emptySet())
                .withPreferredTimeslotTagSet(Collections.emptySet())
                .withRequiredRoomTagSet(Collections.emptySet())
                .withPreferredRoomTagSet(Collections.emptySet())
                .withProhibitedRoomTagSet(Collections.emptySet())
                .withUndesiredRoomTagSet(Collections.emptySet());
        ConferenceSolution solution = new ConferenceSolution(1L)
                .withConstraintConfiguration(constraintConfiguration)
                .withTalkTypeList(Collections.singletonList(talkType))
                .withTalkList(Arrays.asList(talk1))
                .withTimeslotList(Collections.emptyList())
                .withRoomList(Arrays.asList(room1))
                .withSpeakerList(Arrays.asList(speaker1, speaker2));
        constraintConfiguration.setSpeakerPreferredRoomTags(HardMediumSoftScore.ofSoft(1));
        scoreVerifier.assertSoftWeight(SPEAKER_PREFERRED_ROOM_TAGS, 0, solution);
        // talk with 1 speaker, speaker without preferred room tag
        room1.setTagSet(Collections.emptySet());
        talk1.withSpeakerList(Arrays.asList(speaker1)).withRoom(room1);
        scoreVerifier.assertSoftWeight(SPEAKER_PREFERRED_ROOM_TAGS, 0, solution);
        // talk with 1 speaker, speaker with preferred room tag, room without matching tag
        speaker1.withPreferredRoomTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertSoftWeight(SPEAKER_PREFERRED_ROOM_TAGS, -1, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag2)));
        scoreVerifier.assertSoftWeight(SPEAKER_PREFERRED_ROOM_TAGS, -1, solution);
        // talk with 1 speaker, speaker with preferred room tag, room without matching tag, weight = 2
        constraintConfiguration.setSpeakerPreferredRoomTags(HardMediumSoftScore.ofSoft(2));
        scoreVerifier.assertSoftWeight(SPEAKER_PREFERRED_ROOM_TAGS, -2, solution);
        // talk with 1 speaker, speaker with required room tag, room with matching tag
        constraintConfiguration.setSpeakerPreferredRoomTags(HardMediumSoftScore.ofSoft(1));
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertSoftWeight(SPEAKER_PREFERRED_ROOM_TAGS, 0, solution);
        // talk with 1 speaker, speaker with 2 required room tags
        speaker1.withPreferredRoomTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        room1.setTagSet(Collections.emptySet());
        scoreVerifier.assertSoftWeight(SPEAKER_PREFERRED_ROOM_TAGS, -2, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertSoftWeight(SPEAKER_PREFERRED_ROOM_TAGS, -1, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreVerifier.assertSoftWeight(SPEAKER_PREFERRED_ROOM_TAGS, 0, solution);
        // talk with 2 speakers
        talk1.withSpeakerList(Arrays.asList(speaker1, speaker2));
        speaker1.withPreferredRoomTagSet(new HashSet<>(Arrays.asList(tag1)));
        speaker2.withPreferredRoomTagSet(new HashSet<>(Arrays.asList(tag1)));
        room1.setTagSet(Collections.emptySet());
        scoreVerifier.assertSoftWeight(SPEAKER_PREFERRED_ROOM_TAGS, -2, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertSoftWeight(SPEAKER_PREFERRED_ROOM_TAGS, 0, solution);
        speaker2.withPreferredRoomTagSet(new HashSet<>(Arrays.asList(tag2)));
        room1.setTagSet(Collections.emptySet());
        scoreVerifier.assertSoftWeight(SPEAKER_PREFERRED_ROOM_TAGS, -2, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertSoftWeight(SPEAKER_PREFERRED_ROOM_TAGS, -1, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreVerifier.assertSoftWeight(SPEAKER_PREFERRED_ROOM_TAGS, 0, solution);
        speaker2.withPreferredRoomTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        room1.setTagSet(Collections.emptySet());
        scoreVerifier.assertSoftWeight(SPEAKER_PREFERRED_ROOM_TAGS, -3, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertSoftWeight(SPEAKER_PREFERRED_ROOM_TAGS, -1, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreVerifier.assertSoftWeight(SPEAKER_PREFERRED_ROOM_TAGS, 0, solution);
    }

    @Test
    public void talkPreferredRoomTag() {
        ConferenceConstraintConfiguration constraintConfiguration = new ConferenceConstraintConfiguration(1L);
        String tag1 = "tag1";
        String tag2 = "tag2";
        TalkType talkType = new TalkType(0L, "type1");
        Room room1 = new Room(1L).withTalkTypeSet(Collections.emptySet());
        Talk talk1 = createTalk(1L).withTalkType(talkType);
        ConferenceSolution solution = new ConferenceSolution(1L)
                .withConstraintConfiguration(constraintConfiguration)
                .withTalkTypeList(Collections.singletonList(talkType))
                .withTalkList(Arrays.asList(talk1))
                .withTimeslotList(Collections.emptyList())
                .withRoomList(Collections.emptyList())
                .withSpeakerList(Collections.emptyList());
        constraintConfiguration.setTalkPreferredRoomTags(HardMediumSoftScore.ofSoft(1));
        scoreVerifier.assertSoftWeight(TALK_PREFERRED_ROOM_TAGS, 0, solution);
        // talk without preferred room tags
        room1.setTagSet(Collections.emptySet());
        talk1.withRoom(room1);
        scoreVerifier.assertSoftWeight(TALK_PREFERRED_ROOM_TAGS, 0, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertSoftWeight(TALK_PREFERRED_ROOM_TAGS, 0, solution);
        // talk with preferred room tag, room without matching tag
        talk1.withPreferredRoomTagSet(new HashSet<>(Arrays.asList(tag1)));
        room1.setTagSet(Collections.emptySet());
        scoreVerifier.assertSoftWeight(TALK_PREFERRED_ROOM_TAGS, -1, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag2)));
        scoreVerifier.assertSoftWeight(TALK_PREFERRED_ROOM_TAGS, -1, solution);
        // talk with preferred room tag, room without matching tag, weight = 2
        constraintConfiguration.setTalkPreferredRoomTags(HardMediumSoftScore.ofSoft(2));
        scoreVerifier.assertSoftWeight(TALK_PREFERRED_ROOM_TAGS, -2, solution);
        // talk with preferred room tag, room with matching tag
        constraintConfiguration.setTalkPreferredRoomTags(HardMediumSoftScore.ofSoft(1));
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertSoftWeight(TALK_PREFERRED_ROOM_TAGS, 0, solution);
        // talk with two preferred room tags
        room1.setTagSet(Collections.emptySet());
        talk1.withPreferredRoomTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreVerifier.assertSoftWeight(TALK_PREFERRED_ROOM_TAGS, -2, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertSoftWeight(TALK_PREFERRED_ROOM_TAGS, -1, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreVerifier.assertSoftWeight(TALK_PREFERRED_ROOM_TAGS, 0, solution);
    }

    @Test
    public void speakerUndesiredRoomTag() {
        ConferenceConstraintConfiguration constraintConfiguration = new ConferenceConstraintConfiguration(1L);
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
                .withConstraintConfiguration(constraintConfiguration)
                .withTalkTypeList(Collections.singletonList(talkType))
                .withTalkList(Arrays.asList(talk1))
                .withTimeslotList(Collections.emptyList())
                .withRoomList(Arrays.asList(room1))
                .withSpeakerList(Arrays.asList(speaker1, speaker2));
        constraintConfiguration.setSpeakerUndesiredRoomTags(HardMediumSoftScore.ofSoft(1));
        scoreVerifier.assertSoftWeight(SPEAKER_UNDESIRED_ROOM_TAGS, 0, solution);
        // talk with 1 speaker without undesired room tags
        room1.setTagSet(Collections.emptySet());
        talk1.withSpeakerList(Arrays.asList(speaker1)).withRoom(room1);
        scoreVerifier.assertSoftWeight(SPEAKER_UNDESIRED_ROOM_TAGS, 0, solution);
        // talk with 1 speaker, speaker with undesired room tag, room without matching tag
        speaker1.withUndesiredRoomTagSet(new HashSet<>(Arrays.asList(tag1)));
        room1.setTagSet(Collections.emptySet());
        scoreVerifier.assertSoftWeight(SPEAKER_UNDESIRED_ROOM_TAGS, 0, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag3)));
        scoreVerifier.assertSoftWeight(SPEAKER_UNDESIRED_ROOM_TAGS, 0, solution);
        // talk with 1 speaker, speaker with undesired room tag, room with matching tag
        speaker1.withUndesiredRoomTagSet(new HashSet<>(Arrays.asList(tag1)));
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertSoftWeight(SPEAKER_UNDESIRED_ROOM_TAGS, -1, solution);
        // talk with 1 speaker, speaker with undesired room tag, room with matching tag, weight = 2
        constraintConfiguration.setSpeakerUndesiredRoomTags(HardMediumSoftScore.ofSoft(2));
        scoreVerifier.assertSoftWeight(SPEAKER_UNDESIRED_ROOM_TAGS, -2, solution);
        // talk with 1 speaker, speaker with 2 undesired room tags
        constraintConfiguration.setSpeakerUndesiredRoomTags(HardMediumSoftScore.ofSoft(1));
        speaker1.withUndesiredRoomTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        room1.setTagSet(Collections.emptySet());
        scoreVerifier.assertSoftWeight(SPEAKER_UNDESIRED_ROOM_TAGS, 0, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertSoftWeight(SPEAKER_UNDESIRED_ROOM_TAGS, -1, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreVerifier.assertSoftWeight(SPEAKER_UNDESIRED_ROOM_TAGS, -2, solution);
        // talk with 2 speakers
        talk1.withSpeakerList(Arrays.asList(speaker1, speaker2)).withRoom(room1);
        speaker1.withUndesiredRoomTagSet(new HashSet<>(Arrays.asList(tag1)));
        speaker2.withUndesiredRoomTagSet(new HashSet<>(Arrays.asList(tag1)));
        room1.setTagSet(Collections.emptySet());
        scoreVerifier.assertSoftWeight(SPEAKER_UNDESIRED_ROOM_TAGS, 0, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertSoftWeight(SPEAKER_UNDESIRED_ROOM_TAGS, -2, solution);
        speaker2.withUndesiredRoomTagSet(new HashSet<>(Arrays.asList(tag2)));
        room1.setTagSet(Collections.emptySet());
        scoreVerifier.assertSoftWeight(SPEAKER_UNDESIRED_ROOM_TAGS, 0, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertSoftWeight(SPEAKER_UNDESIRED_ROOM_TAGS, -1, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreVerifier.assertSoftWeight(SPEAKER_UNDESIRED_ROOM_TAGS, -2, solution);
        speaker2.withUndesiredRoomTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        room1.setTagSet(Collections.emptySet());
        scoreVerifier.assertSoftWeight(SPEAKER_UNDESIRED_ROOM_TAGS, 0, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertSoftWeight(SPEAKER_UNDESIRED_ROOM_TAGS, -2, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreVerifier.assertSoftWeight(SPEAKER_UNDESIRED_ROOM_TAGS, -3, solution);
    }

    @Test
    public void talkUndesiredRoomTag() {
        ConferenceConstraintConfiguration constraintConfiguration = new ConferenceConstraintConfiguration(1L);
        String tag1 = "tag1";
        String tag2 = "tag2";
        TalkType talkType = new TalkType(0L, "type1");
        Room room1 = new Room(1L).withTalkTypeSet(Collections.emptySet());
        Talk talk1 = createTalk(1L).withTalkType(talkType);
        ConferenceSolution solution = new ConferenceSolution(1L)
                .withConstraintConfiguration(constraintConfiguration)
                .withTalkTypeList(Collections.singletonList(talkType))
                .withTalkList(Arrays.asList(talk1))
                .withTimeslotList(Collections.emptyList())
                .withRoomList(Collections.emptyList())
                .withSpeakerList(Collections.emptyList());
        constraintConfiguration.setTalkUndesiredRoomTags(HardMediumSoftScore.ofSoft(1));
        // talk without undesired room tags
        room1.setTagSet(Collections.emptySet());
        talk1.withRoom(room1);
        scoreVerifier.assertSoftWeight(TALK_UNDESIRED_ROOM_TAGS, 0, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertSoftWeight(TALK_UNDESIRED_ROOM_TAGS, 0, solution);
        // talk with undesired room tag, room without matching tag
        talk1.withUndesiredRoomTagSet(new HashSet<>(Arrays.asList(tag1)));
        room1.setTagSet(Collections.emptySet());
        scoreVerifier.assertSoftWeight(TALK_UNDESIRED_ROOM_TAGS, 0, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag2)));
        scoreVerifier.assertSoftWeight(TALK_UNDESIRED_ROOM_TAGS, 0, solution);
        // talk with undesired room tag, room with matching tag
        talk1.withUndesiredRoomTagSet(new HashSet<>(Arrays.asList(tag1)));
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertSoftWeight(TALK_UNDESIRED_ROOM_TAGS, -1, solution);
        // talk with undesired room tag, room with matching tag, weight = 2
        constraintConfiguration.setTalkUndesiredRoomTags(HardMediumSoftScore.ofSoft(2));
        scoreVerifier.assertSoftWeight(TALK_UNDESIRED_ROOM_TAGS, -2, solution);
        // talk with 2 undesired room tags
        constraintConfiguration.setTalkUndesiredRoomTags(HardMediumSoftScore.ofSoft(1));
        room1.setTagSet(Collections.emptySet());
        talk1.withUndesiredRoomTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreVerifier.assertSoftWeight(TALK_UNDESIRED_ROOM_TAGS, 0, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1)));
        scoreVerifier.assertSoftWeight(TALK_UNDESIRED_ROOM_TAGS, -1, solution);
        room1.setTagSet(new HashSet<>(Arrays.asList(tag1, tag2)));
        scoreVerifier.assertSoftWeight(TALK_UNDESIRED_ROOM_TAGS, -2, solution);
    }

    private Talk createTalk(long id) {
        return new Talk(id)
                .withSpeakerList(Collections.emptyList())
                .withAudienceTypeSet(Collections.emptySet())
                .withPreferredTimeslotTagSet(Collections.emptySet())
                .withRequiredTimeslotTagSet(Collections.emptySet())
                .withProhibitedTimeslotTagSet(Collections.emptySet())
                .withUndesiredTimeslotTagSet(Collections.emptySet())
                .withPreferredRoomTagSet(Collections.emptySet())
                .withRequiredRoomTagSet(Collections.emptySet())
                .withProhibitedRoomTagSet(Collections.emptySet())
                .withUndesiredRoomTagSet(Collections.emptySet())
                .withThemeTrackTagSet(Collections.emptySet())
                .withSectorTagSet(Collections.emptySet())
                .withContentTagSet(Collections.emptySet())
                .withMutuallyExclusiveTalksTagSet(Collections.emptySet())
                .withPrerequisiteTalksCodesSet(Collections.emptySet());
    }
}
