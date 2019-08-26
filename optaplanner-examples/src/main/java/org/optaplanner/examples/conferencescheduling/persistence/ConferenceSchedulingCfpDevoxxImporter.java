/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.examples.conferencescheduling.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

import org.apache.commons.lang3.tuple.Pair;
import org.optaplanner.examples.conferencescheduling.domain.ConferenceConstraintConfiguration;
import org.optaplanner.examples.conferencescheduling.domain.ConferenceSolution;
import org.optaplanner.examples.conferencescheduling.domain.Room;
import org.optaplanner.examples.conferencescheduling.domain.Speaker;
import org.optaplanner.examples.conferencescheduling.domain.Talk;
import org.optaplanner.examples.conferencescheduling.domain.TalkType;
import org.optaplanner.examples.conferencescheduling.domain.Timeslot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Import an instance of a Devoxx conference from the REST API created with https://github.com/nicmarti/cfp-devoxx
 */
public class ConferenceSchedulingCfpDevoxxImporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConferenceSchedulingCfpDevoxxImporter.class);

    // TODO expose these properties in the "import CFP dialog" or better yet, enhance the cfp-devoxx REST api to expose them
    private static final String ZONE_ID = "Europe/Paris";
    private static final String[] SMALL_ROOMS_TYPE_NAMES = {"lab", "Hands-on Labs", "bof", "BOF (Bird of a Feather)", "ignite", "Ignite Sessions"};
    private static final String[] LARGE_ROOMS_TYPE_NAMES = {"tia", "Tools-in-Action", "uni", "University",
            "conf", "Conference", "Deep Dive", "key", "Keynote", "Quickie", "quick"};

    private static final String[] IGNORED_TALK_TYPES = {"ignite", "key"};
    private static final String[] IGNORED_ROOM_IDS = {"ExhibitionHall"};
    private static final String[] IGNORED_SPEAKER_NAMES = {"Devoxx Partner"};

    private String conferenceBaseUrl;
    private Map<String, TalkType> talkTypeIdToTalkTypeMap;
    private Map<String, Room> roomIdToRoomMap;
    private Map<String, Speaker> speakerNameToSpeakerMap;
    private Map<String, Talk> talkCodeToTalkMap;
    private Set<String> trackIdSet;

    private ConferenceSolution solution;
    private Map<String, Integer> timeslotTalkTypeToTotalMap = new HashMap<>();
    private Map<String, Integer> talkTalkTypeToTotalMap = new HashMap<>();

    public ConferenceSchedulingCfpDevoxxImporter(String conferenceBaseUrl) {
        this.conferenceBaseUrl = conferenceBaseUrl;
    }

    public ConferenceSolution importSolution() {
        solution = new ConferenceSolution();
        solution.setId(0L);
        solution.setConferenceName(getConferenceName());
        ConferenceConstraintConfiguration constraintConfiguration = new ConferenceConstraintConfiguration();
        constraintConfiguration.setId(0L);
        solution.setConstraintConfiguration(constraintConfiguration);

        importTalkTypeList();
        importTrackIdSet();
        importRoomList();
        importSpeakerList();
        importTalkList();
        importTimeslotList();

        for (TalkType talkType : solution.getTalkTypeList()) {
            LOGGER.info("{}: Timeslots Total is {}, Talks Total is {}.", talkType.getName(),
                    timeslotTalkTypeToTotalMap.get(talkType.getName()) == null ? 0 : timeslotTalkTypeToTotalMap.get(talkType.getName()),
                    talkTalkTypeToTotalMap.get(talkType.getName()) == null ? 0 : talkTalkTypeToTotalMap.get(talkType.getName()));
        }
        return solution;
    }

    private String getConferenceName() {
        LOGGER.debug("Sending a request to: {}", conferenceBaseUrl);
        JsonObject conferenceObject = readJson(conferenceBaseUrl, JsonReader::readObject);
        return conferenceObject.getString("eventCode");
    }

    private void importTalkTypeList() {
        this.talkTypeIdToTalkTypeMap = new HashMap<>();
        List<TalkType> talkTypeList = new ArrayList<>();

        String proposalTypeUrl = conferenceBaseUrl + "/proposalTypes";
        LOGGER.debug("Sending a request to: {}", proposalTypeUrl);
        JsonObject rootObject = readJson(proposalTypeUrl, JsonReader::readObject);

        JsonArray talkTypeArray = rootObject.getJsonArray("proposalTypes");
        for (int i = 0; i < talkTypeArray.size(); i++) {
            JsonObject talkTypeObject = talkTypeArray.getJsonObject(i);
            String talkTypeId = talkTypeObject.getString("id");
            if (talkTypeIdToTalkTypeMap.keySet().contains(talkTypeId)) {
                LOGGER.warn("Duplicate talk type in {} at index {}.", proposalTypeUrl, i);
                continue;
            }

            TalkType talkType = new TalkType((long) i, talkTypeId);
            talkType.setCompatibleRoomSet(new HashSet<>());
            talkType.setCompatibleTimeslotSet(new HashSet<>());

            talkTypeList.add(talkType);
            talkTypeIdToTalkTypeMap.put(talkTypeId, talkType);
        }

        solution.setTalkTypeList(talkTypeList);
    }

    private void importTrackIdSet() {
        this.trackIdSet = new HashSet<>();
        String tracksUrl = conferenceBaseUrl + "/tracks";
        LOGGER.debug("Sending a request to: {}", tracksUrl);
        JsonObject rootObject = readJson(tracksUrl, JsonReader::readObject);

        JsonArray tracksArray = rootObject.getJsonArray("tracks");
        for (int i = 0; i < tracksArray.size(); i++) {
            trackIdSet.add(tracksArray.getJsonObject(i).getString("id"));
        }
    }

    private void importRoomList() {
        this.roomIdToRoomMap = new HashMap<>();
        List<Room> roomList = new ArrayList<>();

        String roomsUrl = conferenceBaseUrl + "/rooms/";
        LOGGER.debug("Sending a request to: {}", roomsUrl);
        JsonObject rootObject = readJson(roomsUrl, JsonReader::readObject);

        JsonArray roomArray = rootObject.getJsonArray("rooms");
        for (int i = 0; i < roomArray.size(); i++) {
            JsonObject roomObject = roomArray.getJsonObject(i);
            String id = roomObject.getString("id");
            int capacity = roomObject.getInt("capacity");

            if (!Arrays.asList(IGNORED_ROOM_IDS).contains(id)) {
                Room room = new Room((long) i);
                room.setName(id);
                room.setCapacity(capacity);
                room.setTalkTypeSet(getTalkTypeSetForCapacity(capacity));
                for (TalkType talkType : room.getTalkTypeSet()) {
                    talkType.getCompatibleRoomSet().add(room);
                }
                room.setTagSet(new HashSet<>());
                room.setUnavailableTimeslotSet(new HashSet<>());
                roomList.add(room);
                roomIdToRoomMap.put(id, room);
            }
        }

        if (roomList.isEmpty()) {
            LOGGER.warn("There are no rooms. Log into the CFP webapp, open the tab configuration and add the rooms before importing it here.");
        }
        roomList.sort(Comparator.comparing(Room::getName));
        solution.setRoomList(roomList);
    }

    private void importSpeakerList() {
        this.speakerNameToSpeakerMap = new HashMap<>();
        List<Speaker> speakerList = new ArrayList<>();

        String speakersUrl = conferenceBaseUrl + "/speakers";
        LOGGER.debug("Sending a request to: {}", speakersUrl);
        JsonArray speakerArray = readJson(speakersUrl, JsonReader::readArray);

        for (int i = 0; i < speakerArray.size(); i++) {
            String speakerUrl = speakerArray.getJsonObject(i).getJsonArray("links").getJsonObject(0).getString("href");
            LOGGER.debug("Sending a request to: {}", speakerUrl);
            JsonObject speakerObject = readJson(speakerUrl, JsonReader::readObject);

            String speakerId = speakerObject.getString("uuid");
            String speakerName = speakerObject.getString("firstName") + " " + speakerObject.getString("lastName");

            if (Arrays.asList(IGNORED_SPEAKER_NAMES).contains(speakerName)) {
                continue;
            }
            Speaker speaker = new Speaker((long) i);
            speaker.setName(speakerName);
            speaker.withPreferredRoomTagSet(new HashSet<>())
                    .withPreferredTimeslotTagSet(new HashSet<>())
                    .withProhibitedRoomTagSet(new HashSet<>())
                    .withProhibitedTimeslotTagSet(new HashSet<>())
                    .withRequiredRoomTagSet(new HashSet<>())
                    .withRequiredTimeslotTagSet(new HashSet<>())
                    .withUnavailableTimeslotSet(new HashSet<>())
                    .withUndesiredRoomTagSet(new HashSet<>())
                    .withUndesiredTimeslotTagSet(new HashSet<>());
            speakerList.add(speaker);
            if (speakerNameToSpeakerMap.keySet().contains(speakerName)) {
                throw new IllegalStateException("Speaker (" + speakerName + ") with id (" + speakerId
                        + ") already exists in the speaker list");
            }
            speakerNameToSpeakerMap.put(speakerName, speaker);
        }

        speakerList.sort(Comparator.comparing(Speaker::getName));
        solution.setSpeakerList(speakerList);
    }

    private void importTalkList() {
        this.talkCodeToTalkMap = new HashMap<>();
        solution.setTalkList(new ArrayList<>());

        String talksUrl = conferenceBaseUrl + "/talks";
        LOGGER.debug("Sending a request to: {}", talksUrl);
        for (JsonValue talksValue : readJson(talksUrl, JsonReader::readObject).getJsonObject("talks").values()) {
            JsonArray talkArray = (JsonArray) talksValue;
            for (int i = 0; i < talkArray.size(); i++) {
                JsonObject talkObject = talkArray.getJsonObject(i);

                String code = talkObject.getString("id");
                String title = talkObject.getString("title");
                String talkTypeId = talkObject.getJsonObject("talkType").getString("id");
                Set<String> themeTrackSet = extractThemeTrackSet(talkObject, code, title);
                String language = talkObject.getString("lang");
                String audienceLevelAsString = talkObject.getString("audienceLevel").replaceAll("[^0-9]", "");
                int audienceLevel = Integer.parseInt(audienceLevelAsString.isEmpty() ? "1" : audienceLevelAsString);
                List<Speaker> speakerList = extractSpeakerList(talkObject, code, title);
                Set<String> contentTagSet = extractContentTagSet(talkObject);

                if (!Arrays.asList(IGNORED_TALK_TYPES).contains(talkTypeId)) {
                    createTalk(code, title, talkTypeId, themeTrackSet, language, speakerList, audienceLevel, contentTagSet);
                }
            }
        }
    }

    private Set<String> extractThemeTrackSet(JsonObject talkObject, String code, String title) {
        Set<String> themeTrackSet = new HashSet<>(Arrays.asList(talkObject.getJsonObject("track").getString("id")));
        if (!trackIdSet.containsAll(themeTrackSet)) {
            throw new IllegalStateException("The talk (" + title + ") with id (" + code
                    + ") contains trackId (" + themeTrackSet + ") that doesn't exist in the trackIdSet.");
        }
        return themeTrackSet;
    }

    private List<Speaker> extractSpeakerList(JsonObject talkObject, String code, String title) {
        List<Speaker> speakerList = new ArrayList<>();

        String mainSpeakerName = talkObject.getString("mainSpeaker");
        if (Arrays.asList(IGNORED_SPEAKER_NAMES).contains(mainSpeakerName)) {
            return speakerList;
        }

        speakerList.add(getSpeakerOrCreateOneIfNull(code, title, mainSpeakerName));
        if (talkObject.containsKey("secondarySpeaker")) {
            String secondarySpeakerName = talkObject.getString("secondarySpeaker");
            speakerList.add(getSpeakerOrCreateOneIfNull(code, title, secondarySpeakerName));
        }

        if (talkObject.containsKey("otherSpeakers")) {
            JsonArray otherSpeakersArray = talkObject.getJsonArray("otherSpeakers");
            for (JsonValue otherSpeakerName : otherSpeakersArray) {
                speakerList.add(getSpeakerOrCreateOneIfNull(code, title,
                        otherSpeakerName.toString().replaceAll("\"", "")));
            }
        }

        return speakerList;
    }

    private Speaker getSpeakerOrCreateOneIfNull(String code, String title, String speakerName) {
        Speaker speaker = speakerNameToSpeakerMap.get(speakerName);
        if (speaker == null) {
            LOGGER.warn("The talk ({}: {}) has a speaker ({}) that doesn't exist in speaker list.", code, title, speakerName);

            speaker = new Speaker((long) solution.getSpeakerList().size());
            speaker.setName(speakerName);
            speaker.withPreferredRoomTagSet(new HashSet<>())
                    .withPreferredTimeslotTagSet(new HashSet<>())
                    .withProhibitedRoomTagSet(new HashSet<>())
                    .withProhibitedTimeslotTagSet(new HashSet<>())
                    .withRequiredRoomTagSet(new HashSet<>())
                    .withRequiredTimeslotTagSet(new HashSet<>())
                    .withUnavailableTimeslotSet(new HashSet<>())
                    .withUndesiredRoomTagSet(new HashSet<>())
                    .withUndesiredTimeslotTagSet(new HashSet<>());
            if (speakerNameToSpeakerMap.keySet().contains(speakerName)) {
                throw new IllegalStateException("Speaker (" + speakerName + ") already exists in the speaker list");
            }
            speakerNameToSpeakerMap.put(speakerName, speaker);
            solution.getSpeakerList().add(speaker);
        }
        return speaker;
    }

    private Set<String> extractContentTagSet(JsonObject talkObject) {
        if (talkObject.containsKey("tags")) {
            return talkObject.getJsonArray("tags").stream()
                    .map(JsonObject.class::cast)
                    .filter(tagObject -> !tagObject.getString("value").isEmpty())
                    .map(tagObject -> tagObject.getString("value"))
                    .collect(Collectors.toSet());
        }
        return new HashSet<>();
    }

    private void createTalk(String code, String title, String talkTypeId, Set<String> themeTrackSet,
                            String languageg, List<Speaker> speakerList, int audienceLevel, Set<String> contentTagSet) {
        Talk talk = new Talk((long) solution.getTalkList().size());
        talk.setCode(code);
        talk.setTitle(title);
        if (talkTypeIdToTalkTypeMap.get(talkTypeId) == null) {
            throw new IllegalStateException("The talk (" + title + ") with id (" + code
                    + ") has a talkType (" + talkTypeId + ") that doesn't exist in the talkType list.");
        }
        talk.setTalkType(talkTypeIdToTalkTypeMap.get(talkTypeId));
        talk.withSpeakerList(speakerList)
                .withThemeTrackTagSet(themeTrackSet)
                .withSectorTagSet(new HashSet<>())
                .withLanguage(languageg)
                .withAudienceTypeSet(new HashSet<>())
                .withAudienceLevel(audienceLevel)
                .withContentTagSet(contentTagSet)
                .withRequiredTimeslotTagSet(new HashSet<>())
                .withPreferredTimeslotTagSet(new HashSet<>())
                .withProhibitedTimeslotTagSet(new HashSet<>())
                .withUndesiredTimeslotTagSet(new HashSet<>())
                .withRequiredRoomTagSet(new HashSet<>())
                .withPreferredRoomTagSet(new HashSet<>())
                .withProhibitedRoomTagSet(new HashSet<>())
                .withUndesiredRoomTagSet(new HashSet<>())
                .withMutuallyExclusiveTalksTagSet(new HashSet<>())
                .withPrerequisiteTalksCodesSet(new HashSet<>());

        talkCodeToTalkMap.put(talk.getCode(), talk);
        solution.getTalkList().add(talk);
        talkTalkTypeToTotalMap.merge(talk.getTalkType().getName(), 1, Integer::sum);
    }

    private void importTimeslotList() {
        List<Timeslot> timeslotList = new ArrayList<>();
        Map<Timeslot, List<Room>> timeslotToAvailableRoomsMap = new HashMap<>();
        Map<Pair<LocalDateTime, LocalDateTime>, Timeslot> startAndEndTimeToTimeslotMap = new HashMap<>();
        talkTypeIdToTalkTypeMap.put("unknown", talkTypeIdToTalkTypeMap.get("key"));

        Long timeslotId = 0L;
        String slotsUrl = conferenceBaseUrl + "/slots";
        JsonArray slotsArray = readJson(slotsUrl, JsonReader::readObject).getJsonArray("slots");

        for (int i = 0; i < slotsArray.size(); i++) {
            JsonObject timeslotObject = slotsArray.getJsonObject(i);
            if (Arrays.asList(IGNORED_ROOM_IDS).contains(timeslotObject.getString("room"))) {
                continue;
            }

            LocalDateTime startDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timeslotObject.getJsonNumber("from").longValue()),
                    ZoneId.of(ZONE_ID));
            LocalDateTime endDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timeslotObject.getJsonNumber("to").longValue()),
                    ZoneId.of(ZONE_ID));


            Room room = extractRoom(timeslotObject, "id", "room");

            // Assuming slotId is of format: tia_room6_monday_12_.... take only "tia"
            // Specific for DevoxxBE, unknown in slotId matches a keynote slot
            String talkTypeId = timeslotObject.getString("id").split("_")[0];
            if (Arrays.asList(IGNORED_TALK_TYPES).contains(talkTypeId)) {
                continue;
            }
            TalkType timeslotTalkType = talkTypeIdToTalkTypeMap.get(talkTypeId);

            Timeslot timeslot = startAndEndTimeToTimeslotMap.get(Pair.of(startDateTime, endDateTime));
            if (timeslot != null) {
                timeslotToAvailableRoomsMap.get(timeslot).add(room);
                if (timeslotTalkType != null) {
                    timeslot.getTalkTypeSet().add(timeslotTalkType);
                }
            } else {
                timeslot = new Timeslot(timeslotId++);
                timeslot.withStartDateTime(startDateTime)
                        .withEndDateTime(endDateTime)
                        .withTalkTypeSet(timeslotTalkType == null ? new HashSet<>() : new HashSet<>(Arrays.asList(timeslotTalkType)));
                timeslot.setTagSet(new HashSet<>());

                timeslotList.add(timeslot);
                timeslotToAvailableRoomsMap.put(timeslot, new ArrayList<>(Arrays.asList(room)));
                startAndEndTimeToTimeslotMap.put(Pair.of(startDateTime, endDateTime), timeslot);
            }

            for (TalkType talkType : timeslot.getTalkTypeSet()) {
                talkType.getCompatibleTimeslotSet().add(timeslot);
            }
            timeslotTalkTypeToTotalMap.merge(talkTypeId, 1, Integer::sum);
        }

        String schedulesUrl = conferenceBaseUrl + "/schedules/";
        LOGGER.debug("Sending a request to: {}", schedulesUrl);
        JsonArray daysArray = readJson(schedulesUrl, JsonReader::readObject).getJsonArray("links");
        for (int i = 0; i < daysArray.size(); i++) {
            JsonObject dayObject = daysArray.getJsonObject(i);
            String dayUrl = dayObject.getString("href");

            LOGGER.debug("Sending a request to: {}", dayUrl);
            JsonArray daySlotsArray = readJson(dayUrl, JsonReader::readObject).getJsonArray("slots");

            for (int j = 0; j < daySlotsArray.size(); j++) {
                JsonObject timeslotObject = daySlotsArray.getJsonObject(j);

                if (Arrays.asList(IGNORED_ROOM_IDS).contains(timeslotObject.getString("roomId"))
                        || Arrays.asList(IGNORED_TALK_TYPES).contains(timeslotObject.getString("slotId").split("_")[0])) {
                    continue;
                }

                LocalDateTime startDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timeslotObject.getJsonNumber("fromTimeMillis").longValue()),
                        ZoneId.of(ZONE_ID));
                LocalDateTime endDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timeslotObject.getJsonNumber("toTimeMillis").longValue()),
                        ZoneId.of(ZONE_ID));
                Timeslot timeslot = startAndEndTimeToTimeslotMap.get(Pair.of(startDateTime, endDateTime));
                if (timeslot == null) {
                    throw new IllegalStateException("Timeslot (" + timeslotObject.getString("slotId") + ") in + ("
                            + dayUrl + ") does not exist in /slots endpoint.");
                }
                Room room = extractRoom(timeslotObject, "slotId", "roomId");

                if (timeslotObject.containsKey("talk") && !timeslotObject.isNull("talk")) {
                    scheduleTalk(timeslotObject, room, timeslot);
                }
            }
        }

        if (timeslotList.isEmpty()) {
            LOGGER.warn("There are no timeslots. Log into the CFP webapp, open the tab configuration and add the timeslots before importing it here.");
        }
        for (Room room : solution.getRoomList()) {
            room.setUnavailableTimeslotSet(timeslotList.stream()
                    .filter(timeslot -> !timeslotToAvailableRoomsMap.get(timeslot).contains(room))
                    .collect(Collectors.toSet()));
        }

        timeslotList.sort(Comparator.comparing(timeslot -> timeslot.getStartDateTime()));
        solution.setTimeslotList(timeslotList);
    }

    private Room extractRoom(JsonObject timeslotObject, String slotId, String roomId) {
        Room room = roomIdToRoomMap.get(timeslotObject.getString(roomId));
        if (room == null) {
            throw new IllegalStateException("The timeslot (" + timeslotObject.getString(slotId) + ") has a roomId (" + timeslotObject.getString(roomId)
                    + ") that does not exist in the rooms list");
        }
        return room;
    }

    private Set<TalkType> getTalkTypeSetForCapacity(int capacity) {
        Set<TalkType> talkTypeSet = new HashSet<>();
        List<String> typeNames = new ArrayList<>();
        if (capacity < 100) {
            typeNames.addAll(
                    Arrays.asList(SMALL_ROOMS_TYPE_NAMES).stream()
                            .filter(typeName -> solution.getTalkTypeList().contains(talkTypeIdToTalkTypeMap.get(typeName)))
                            .collect(Collectors.toSet()));
        } else {
            typeNames.addAll(Arrays.asList(LARGE_ROOMS_TYPE_NAMES).stream()
                    .filter(typeName -> solution.getTalkTypeList().contains(talkTypeIdToTalkTypeMap.get(typeName)))
                    .collect(Collectors.toSet()));
        }

        for (String talkTypeName : typeNames) {
            TalkType talkType = talkTypeIdToTalkTypeMap.get(talkTypeName);
            if (talkType != null) {
                talkTypeSet.add(talkType);
            }
        }

        return talkTypeSet;
    }

    private void scheduleTalk(JsonObject timeslotObject, Room room, Timeslot timeslot) {
        Talk talk = talkCodeToTalkMap.get(timeslotObject.getJsonObject("talk").getString("id"));
        if (talk == null) {
            throw new IllegalStateException("The timeslot (" + timeslotObject.getString("slotId")
                    + ") has a talk (" + timeslotObject.getJsonObject("talk").getString("id")
                    + ") that does not exist in the talk list");
        }
        if (talk.isPinnedByUser()) {
            throw new IllegalStateException("The timeslot (" + timeslotObject.getString("slotId")
                    + ") has a talk (" + timeslotObject.getJsonObject("talk").getString("id")
                    + ") that is already pinned by user at another timeslot (" + talk.getTimeslot().toString() + ").");
        }
        talk.setRoom(room);
        talk.setTimeslot(timeslot);
    }

    private <R> R readJson(String url, Function<JsonReader, R> mapper) {
        try (InputStream inputStream = new ConnectionFollowRedirects(url).getInputStream()) {
            JsonReader jsonReader = Json.createReader(inputStream);
            return mapper.apply(jsonReader);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Import failed on URL (" + url + ").", e);
        }
    }
}
