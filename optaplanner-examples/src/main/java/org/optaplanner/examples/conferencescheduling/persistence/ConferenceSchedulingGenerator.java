
package org.optaplanner.examples.conferencescheduling.persistence;

import java.io.File;
import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.app.LoggingMain;
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.examples.common.persistence.generator.StringDataGenerator;
import org.optaplanner.examples.common.util.Pair;
import org.optaplanner.examples.conferencescheduling.app.ConferenceSchedulingApp;
import org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration;
import org.optaplanner.examples.conferencescheduling.domain.ConferenceSolution;
import org.optaplanner.examples.conferencescheduling.domain.Room;
import org.optaplanner.examples.conferencescheduling.domain.Speaker;
import org.optaplanner.examples.conferencescheduling.domain.Talk;
import org.optaplanner.examples.conferencescheduling.domain.TalkType;
import org.optaplanner.examples.conferencescheduling.domain.Timeslot;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

public class ConferenceSchedulingGenerator extends LoggingMain {

    public static void main(String[] args) {
        ConferenceSchedulingGenerator generator = new ConferenceSchedulingGenerator();
        generator.writeConferenceSolution(1, 5);
        generator.writeConferenceSolution(2, 5);
        generator.writeConferenceSolution(2, 10);
        generator.writeConferenceSolution(3, 10);
        generator.writeConferenceSolution(3, 20);
    }

    private final StringDataGenerator conferenceNameGenerator = new StringDataGenerator()
            .addPart(true, 0,
                    "Javoxx",
                    "Red Bonnet Summit",
                    "JayFocus",
                    "YCon",
                    "JAQ")
            .addPart(false, 0,
                    "2021",
                    "2022",
                    "2023",
                    "2024",
                    "2025");

    private static final String LAB_TALK_TYPE = "Lab";
    private static final String BREAKOUT_TALK_TYPE = "Breakout";

    private final LocalDate timeslotFirstDay = LocalDate.of(2018, 10, 1);

    private final List<Pair<LocalTime, LocalTime>> timeslotOptions = Arrays.asList(
            //        Pair.of(LocalTime.of(8, 30), LocalTime.of(9, 30)), // General session
            Pair.of(LocalTime.of(10, 15), LocalTime.of(12, 15)), // Lab
            Pair.of(LocalTime.of(10, 15), LocalTime.of(11, 0)),
            Pair.of(LocalTime.of(11, 30), LocalTime.of(12, 15)),
            Pair.of(LocalTime.of(13, 0), LocalTime.of(15, 0)), // Lab
            //        Pair.of(LocalTime.of(13, 45), LocalTime.of(15, 0)), // General session
            Pair.of(LocalTime.of(15, 30), LocalTime.of(16, 15)),
            Pair.of(LocalTime.of(16, 30), LocalTime.of(17, 15)));

    private final List<Pair<String, Double>> roomTagProbabilityList = Arrays.asList(
            Pair.of("Large", 0.20),
            Pair.of("Recorded", 0.50));

    private final StringDataGenerator speakerNameGenerator = StringDataGenerator.buildFullNames();

    private final String[] contentTagOptions = {
            "OpenShift",
            "WildFly",
            "Spring",
            "Drools",
            "OptaPlanner",
            "jBPM",
            "Camel",
            "Jackson",
            "Docker",
            "Hibernate",
            "GWT",
            "Errai",
            "Angular",
            "Weld",
            "RestEasy",
            "Android",
            "Tensorflow",
            "VertX",
            "JUnit",
            "Keycloak"
    };
    private final StringDataGenerator talkTitleGenerator = new StringDataGenerator()
            .addPart(true, 0,
                    "Hands on",
                    "Advanced",
                    "Learn",
                    "Intro to",
                    "Discover",
                    "Mastering",
                    "Tuning",
                    "Building",
                    "Securing",
                    "Debug",
                    "Prepare for",
                    "Understand",
                    "Applying",
                    "Grok",
                    "Troubleshooting",
                    "Using",
                    "Deliver",
                    "Implement",
                    "Program",
                    "Hack")
            .addPart(true, 0,
                    "real-time",
                    "containerized",
                    "virtualized",
                    "serverless",
                    "AI-driven",
                    "machine learning",
                    "IOT-driven",
                    "deep learning",
                    "scalable",
                    "enterprise",
                    "streaming",
                    "mobile",
                    "modern",
                    "distributed",
                    "reliable",
                    "secure",
                    "stable",
                    "platform-independent",
                    "flexible",
                    "modularized")
            .addPart(true, 1,
                    contentTagOptions)
            .addPart(false, 3,
                    "in a nutshell",
                    "in practice",
                    "for dummies",
                    "in action",
                    "recipes",
                    "on the web",
                    "for decision makers",
                    "on the whiteboard",
                    "out of the box",
                    "for programmers",
                    "for managers",
                    "for QA engineers",
                    "in Java",
                    "in Scala",
                    "in Kotlin",
                    "in Lisp",
                    "in C++",
                    "in Assembly",
                    "with style",
                    "like a pro");

    private final List<String> themeTagOptions = Arrays.asList(
            "Artificial Intelligence",
            "Cloud",
            "Big Data",
            "Culture",
            "Middleware",
            "Mobile",
            "IoT",
            "Modern Web",
            "Security");

    private final List<String> sectorTagOptions = Arrays.asList(
            "Education",
            "Financial services",
            "Government",
            "Healthcare",
            "Telecommunications",
            "Transportation");
    private final List<String> audienceTypeOptions = Arrays.asList(
            "Programmers",
            "Business analysts",
            "Managers");

    private static final String TIMESLOT_AFTER_LUNCH_TAG = "After lunch";
    private static final List<String> mutuallyExclusiveTagList = List.of("Platinum Sponsor");

    protected final SolutionFileIO<ConferenceSolution> solutionFileIO;
    protected final File outputDir;

    private TalkType breakoutTalkType;
    private TalkType labTalkType;
    protected int labTalkCount;
    protected Random random;

    public ConferenceSchedulingGenerator() {
        solutionFileIO = new ConferenceSchedulingXlsxFileIO();
        outputDir = new File(CommonApp.determineDataDir(ConferenceSchedulingApp.DATA_DIR_NAME), "unsolved");
    }

    private void writeConferenceSolution(int dayListSize, int roomListSize) {
        int labTimeslotCount = (int) timeslotOptions.stream()
                .filter(pair -> Duration.between(pair.getKey(), pair.getValue()).toMinutes() >= 120).count();
        int labRoomCount = roomListSize / 5;
        labTalkCount = (dayListSize * labTimeslotCount) * labRoomCount;

        int timeslotListSize = dayListSize * timeslotOptions.size();
        int talkListSize = (dayListSize * (timeslotOptions.size() - labTimeslotCount)) * (roomListSize - labRoomCount)
                + labTalkCount;
        int speakerListSize = talkListSize * 2 / 3;

        String fileName = talkListSize + "talks-" + timeslotListSize + "timeslots-" + roomListSize + "rooms";
        File outputFile = new File(outputDir, fileName + "." + solutionFileIO.getOutputFileExtension());
        ConferenceSolution solution = createConferenceSolution(
                fileName, timeslotListSize, roomListSize, speakerListSize, talkListSize);
        solutionFileIO.write(solution, outputFile);
    }

    public ConferenceSolution createConferenceSolution(String fileName, int timeslotListSize, int roomListSize,
            int speakerListSize, int talkListSize) {
        random = new Random(37);
        ConferenceSolution solution = new ConferenceSolution(0L);
        solution.setConferenceName(conferenceNameGenerator.generateNextValue());
        ConferenceConstraintConfiguration constraintConfiguration = new ConferenceConstraintConfiguration(0L);
        constraintConfiguration.setMinimumConsecutiveTalksPauseInMinutes(15);
        solution.setConstraintConfiguration(constraintConfiguration);

        createTalkTypeList(solution);
        createTimeslotList(solution, timeslotListSize);
        createRoomList(solution, roomListSize);
        createSpeakerList(solution, speakerListSize);
        createTalkList(solution, talkListSize);

        BigInteger possibleSolutionSize = BigInteger.valueOf((long) timeslotListSize * roomListSize)
                .pow(talkListSize);
        logger.info("Conference {} has {} talks, {} timeslots and {} rooms with a search space of {}.",
                fileName,
                talkListSize,
                timeslotListSize,
                roomListSize,
                AbstractSolutionImporter.getFlooredPossibleSolutionSize(possibleSolutionSize));
        return solution;
    }

    private void createTalkTypeList(ConferenceSolution solution) {
        List<TalkType> talkTypeList = new ArrayList<>(2);
        breakoutTalkType = new TalkType(0L);
        breakoutTalkType.setName(BREAKOUT_TALK_TYPE);
        breakoutTalkType.setCompatibleTimeslotSet(new LinkedHashSet<>());
        breakoutTalkType.setCompatibleRoomSet(new LinkedHashSet<>());
        talkTypeList.add(breakoutTalkType);
        labTalkType = new TalkType(1L);
        labTalkType.setName(LAB_TALK_TYPE);
        labTalkType.setCompatibleTimeslotSet(new LinkedHashSet<>());
        labTalkType.setCompatibleRoomSet(new LinkedHashSet<>());
        talkTypeList.add(labTalkType);
        solution.setTalkTypeList(talkTypeList);
    }

    private void createTimeslotList(ConferenceSolution solution, int timeslotListSize) {
        List<Timeslot> timeslotList = new ArrayList<>(timeslotListSize);
        int timeslotOptionsIndex = 0;
        LocalDate day = timeslotFirstDay;
        for (int i = 0; i < timeslotListSize; i++) {
            Timeslot timeslot = new Timeslot(i);
            if (timeslotOptionsIndex >= timeslotOptions.size()) {
                timeslotOptionsIndex = 0;
                day = day.plusDays(1);
            }
            Pair<LocalTime, LocalTime> pair = timeslotOptions.get(timeslotOptionsIndex);
            timeslot.setStartDateTime(LocalDateTime.of(day, pair.getKey()));
            timeslot.setEndDateTime(LocalDateTime.of(day, pair.getValue()));
            TalkType talkType = timeslot.getDurationInMinutes() >= 120 ? labTalkType : breakoutTalkType;
            talkType.getCompatibleTimeslotSet().add(timeslot);
            timeslot.setTalkTypeSet(Collections.singleton(talkType));
            timeslotOptionsIndex++;
            Set<String> tagSet = new LinkedHashSet<>(2);
            if (timeslot.getStartDateTime().getHour() == 13) {
                tagSet.add(TIMESLOT_AFTER_LUNCH_TAG);
            }
            timeslot.setTagSet(tagSet);
            logger.trace("Created timeslot ({}) with tags ({}).",
                    timeslot, tagSet);
            timeslotList.add(timeslot);
        }
        solution.setTimeslotList(timeslotList);
    }

    private void createRoomList(ConferenceSolution solution, int roomListSize) {
        final int roomsPerFloor = 12;
        List<Room> roomList = new ArrayList<>(roomListSize);
        for (int i = 0; i < roomListSize; i++) {
            Room room = new Room(i);
            room.setName("R " + ((i / roomsPerFloor * 100) + (i % roomsPerFloor) + 1));
            room.setCapacity((1 + random.nextInt(100)) * 10);
            TalkType talkType;
            if (i % 5 == 4) {
                talkType = labTalkType;
            } else {
                talkType = breakoutTalkType;
            }
            talkType.getCompatibleRoomSet().add(room);
            room.setTalkTypeSet(Collections.singleton(talkType));
            room.setUnavailableTimeslotSet(new LinkedHashSet<>());
            Set<String> tagSet = new LinkedHashSet<>(roomTagProbabilityList.size());
            for (Pair<String, Double> roomTagProbability : roomTagProbabilityList) {
                if ((i == 0 || i == 4 || random.nextDouble() < roomTagProbability.getValue())
                        && roomTagProbability.getKey().equals("Recorded")) {
                    tagSet.add(roomTagProbability.getKey());
                }
            }
            if (room.getCapacity() >= 500) {
                tagSet.add("Large");
            }
            room.setTagSet(tagSet);
            logger.trace("Created room with name ({}) and tags ({}).",
                    room.getName(), tagSet);
            roomList.add(room);
        }
        solution.setRoomList(roomList);
    }

    private void createSpeakerList(ConferenceSolution solution, int speakerListSize) {
        List<Speaker> speakerList = new ArrayList<>(speakerListSize);
        speakerNameGenerator.predictMaximumSizeAndReset(speakerListSize);
        for (int i = 0; i < speakerListSize; i++) {
            Speaker speaker = new Speaker(i);
            speaker.setName(speakerNameGenerator.generateNextValue());
            Set<Timeslot> unavailableTimeslotSet;
            Set<String> preferredTimeslotTagSet = new LinkedHashSet<>();
            Set<String> undesiredTimeslotTagSet = new LinkedHashSet<>();
            List<Timeslot> timeslotList = solution.getTimeslotList();
            if (random.nextDouble() < 0.10) {
                double segmentRandom = random.nextDouble();
                if (segmentRandom < 0.10) {
                    // No mornings
                    unavailableTimeslotSet = timeslotList.stream()
                            .filter(timeslot -> timeslot.getStartDateTime().toLocalTime().isBefore(LocalTime.of(12, 0)))
                            .collect(Collectors.toCollection(LinkedHashSet::new));
                    preferredTimeslotTagSet.add(TIMESLOT_AFTER_LUNCH_TAG);
                } else if (segmentRandom < 0.20) {
                    // No afternoons
                    unavailableTimeslotSet = timeslotList.stream()
                            .filter(timeslot -> !timeslot.getStartDateTime().toLocalTime().isBefore(LocalTime.of(12, 0)))
                            .collect(Collectors.toCollection(LinkedHashSet::new));
                } else if (segmentRandom < 0.30) {
                    // Only 1 day available
                    LocalDate availableDate = timeslotList.get(random.nextInt(timeslotList.size())).getDate();
                    unavailableTimeslotSet = timeslotList.stream()
                            .filter(timeslot -> !timeslot.getDate().equals(availableDate))
                            .collect(Collectors.toCollection(LinkedHashSet::new));
                } else {
                    unavailableTimeslotSet = timeslotList.stream()
                            .filter(timeslot -> random.nextDouble() < 0.05)
                            .collect(Collectors.toCollection(LinkedHashSet::new));
                    undesiredTimeslotTagSet.add(TIMESLOT_AFTER_LUNCH_TAG);
                }
            } else {
                unavailableTimeslotSet = new LinkedHashSet<>(timeslotList.size());
            }
            speaker.setUnavailableTimeslotSet(unavailableTimeslotSet);
            speaker.setRequiredTimeslotTagSet(new LinkedHashSet<>()); // TODO: choose random required/prohibited tags that don't result in an infeasible solution
            speaker.setPreferredTimeslotTagSet(preferredTimeslotTagSet);
            speaker.setProhibitedTimeslotTagSet(new LinkedHashSet<>());
            speaker.setUndesiredTimeslotTagSet(undesiredTimeslotTagSet);
            Set<String> requiredRoomTagSet = new LinkedHashSet<>();
            Set<String> preferredRoomTagSet = new LinkedHashSet<>();
            Set<String> prohibitedRoomTagSet = new LinkedHashSet<>();
            Set<String> undesiredRoomTagSet = new LinkedHashSet<>();
            initializeRoomTagSets(requiredRoomTagSet, preferredRoomTagSet, prohibitedRoomTagSet, undesiredRoomTagSet);
            speaker.setRequiredRoomTagSet(requiredRoomTagSet);
            speaker.setPreferredRoomTagSet(preferredRoomTagSet);
            speaker.setProhibitedRoomTagSet(prohibitedRoomTagSet);
            speaker.setUndesiredRoomTagSet(undesiredRoomTagSet);
            logger.trace("Created speaker with name ({}).",
                    speaker.getName());
            speakerList.add(speaker);
        }
        solution.setSpeakerList(speakerList);
    }

    private void createTalkList(ConferenceSolution solution, int talkListSize) {
        List<Talk> talkList = new ArrayList<>(talkListSize);
        talkTitleGenerator.predictMaximumSizeAndReset(talkListSize);
        int speakerListIndex = 0;
        for (int i = 0; i < talkListSize; i++) {
            Talk talk = new Talk(i);
            talk.setCode(String.format("S%0" + ((String.valueOf(talkListSize).length()) + "d"), i));
            talk.setTitle(talkTitleGenerator.generateNextValue());
            double speakerRandomDouble = random.nextDouble();
            talk.setTalkType(i < labTalkCount ? labTalkType : breakoutTalkType);
            int speakerCount = (speakerRandomDouble < 0.01) ? 4
                    : (speakerRandomDouble < 0.03) ? 3 : (speakerRandomDouble < 0.40) ? 2 : 1;
            List<Speaker> speakerList = new ArrayList<>(speakerCount);
            for (int j = 0; j < speakerCount; j++) {
                speakerList.add(solution.getSpeakerList().get(speakerListIndex));
                speakerListIndex = (speakerListIndex + 1) % solution.getSpeakerList().size();
            }
            talk.setSpeakerList(speakerList);
            Set<String> themeTagSet = new LinkedHashSet<>();
            themeTagSet.add(themeTagOptions.get(random.nextInt(themeTagOptions.size())));
            if (random.nextDouble() < 0.20) {
                themeTagSet.add(themeTagOptions.get(random.nextInt(themeTagOptions.size())));
            }
            talk.setThemeTrackTagSet(themeTagSet);
            Set<String> sectorTagSet = new LinkedHashSet<>();
            if (random.nextDouble() < 0.20) {
                sectorTagSet.add(sectorTagOptions.get(random.nextInt(sectorTagOptions.size())));
            }
            talk.setAudienceTypeSet(Collections.singleton(audienceTypeOptions.get(random.nextInt(audienceTypeOptions.size()))));
            talk.setAudienceLevel(1 + random.nextInt(3));
            Set<String> contentTagSet = new LinkedHashSet<>();
            for (String contentTagOption : contentTagOptions) {
                if (talk.getTitle().contains(contentTagOption)) {
                    contentTagSet.add(contentTagOption);
                    if ((contentTagOption.equalsIgnoreCase("OpenShift") || contentTagOption.equalsIgnoreCase("Docker"))
                            && random.nextDouble() < 0.40) {
                        contentTagSet.add("Kubernetes");
                    }
                    break;
                }
            }
            talk.setContentTagSet(contentTagSet);
            talk.setSectorTagSet(sectorTagSet);
            talk.setLanguage("en");
            talk.setRequiredTimeslotTagSet(new LinkedHashSet<>());
            talk.setPreferredTimeslotTagSet(new LinkedHashSet<>());
            talk.setProhibitedTimeslotTagSet(new LinkedHashSet<>());
            talk.setUndesiredTimeslotTagSet(new LinkedHashSet<>());

            Set<String> requiredRoomTagSet = new LinkedHashSet<>();
            Set<String> preferredRoomTagSet = new LinkedHashSet<>();
            Set<String> prohibitedRoomTagSet = new LinkedHashSet<>();
            Set<String> undesiredRoomTagSet = new LinkedHashSet<>();
            initializeRoomTagSets(requiredRoomTagSet, preferredRoomTagSet, prohibitedRoomTagSet, undesiredRoomTagSet);
            talk.setRequiredRoomTagSet(requiredRoomTagSet);
            talk.setPreferredRoomTagSet(preferredRoomTagSet);
            talk.setProhibitedRoomTagSet(prohibitedRoomTagSet);
            talk.setUndesiredRoomTagSet(undesiredRoomTagSet);

            Set<String> mutuallyExclusiveTagSet = new LinkedHashSet<>();
            if (random.nextDouble() < 0.025) {
                mutuallyExclusiveTagSet.add(mutuallyExclusiveTagList.get(random.nextInt(mutuallyExclusiveTagList.size())));
            }
            talk.setMutuallyExclusiveTalksTagSet(mutuallyExclusiveTagSet);

            Set<Talk> prerequisiteTalkCodeSet = new LinkedHashSet<>();
            if (random.nextDouble() < 0.025) {
                prerequisiteTalkCodeSet.add(talkList.get(random.nextInt(talkList.size())));
            }
            talk.setPrerequisiteTalkSet(prerequisiteTalkCodeSet);

            talk.setFavoriteCount(random.nextInt(1000));
            if (random.nextDouble() < 0.02) {
                talk.setCrowdControlRisk(1);
                // Need an even number of talks with crowd control > 1 for a feasible solution
                Talk pairedTalk = talkList.get(random.nextInt(talkList.size()));
                while (pairedTalk.getCrowdControlRisk() != 0 || !pairedTalk.getTalkType().equals(talk.getTalkType())) {
                    pairedTalk = talkList.get(random.nextInt(talkList.size()));
                }
                pairedTalk.setCrowdControlRisk(1);
            }
            talk.setCrowdControlRisk(0); // Disabled for now: the unsolved schedules must have a feasible solution
            logger.trace("Created talk with code ({}), title ({}) and speakers ({}).",
                    talk.getCode(), talk.getTitle(), speakerList);
            talkList.add(talk);
        }

        Talk pinnedTalk = talkList.get(labTalkCount + random.nextInt(talkListSize - labTalkCount));
        pinnedTalk.setPinnedByUser(true);
        pinnedTalk.setTimeslot(solution.getTimeslotList().stream()
                .filter(timeslot -> timeslot.getTalkTypeSet().contains(breakoutTalkType)).findFirst().get());
        pinnedTalk.setRoom(solution.getRoomList().get(0));

        Talk publishedTalk = talkList.get(random.nextInt(labTalkCount));
        publishedTalk.setTimeslot(solution.getTimeslotList().stream()
                .filter(timeslot -> timeslot.getTalkTypeSet().contains(labTalkType)).findFirst().get());
        publishedTalk.setRoom(solution.getRoomList().stream()
                .filter(room -> room.getTalkTypeSet().contains(labTalkType)).findFirst().get()); // In this data set rooms has only one talkType
        publishedTalk.setPublishedTimeslot(publishedTalk.getTimeslot());
        publishedTalk.setPublishedRoom(publishedTalk.getRoom());

        solution.setTalkList(talkList);
    }

    private void initializeRoomTagSets(Set<String> requiredRoomTagSet, Set<String> preferredRoomTagSet,
            Set<String> prohibitedRoomTagSet, Set<String> undesiredRoomTagSet) {
        for (Pair<String, Double> roomTagProbability : roomTagProbabilityList) {
            double segmentRandom = random.nextDouble();
            if (segmentRandom < roomTagProbability.getValue() / 25.0) {
                requiredRoomTagSet.add(roomTagProbability.getKey());
            } else if (segmentRandom < roomTagProbability.getValue() / 20.0) {
                prohibitedRoomTagSet.add(roomTagProbability.getKey());
            } else if (segmentRandom < roomTagProbability.getValue() / 15.0) {
                preferredRoomTagSet.add(roomTagProbability.getKey());
            } else if (segmentRandom < roomTagProbability.getValue() / 10.0) {
                undesiredRoomTagSet.add(roomTagProbability.getKey());
            }
        }
    }
}
