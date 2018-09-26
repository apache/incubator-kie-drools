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
import java.io.StringReader;
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

import org.apache.commons.lang3.tuple.Pair;
import org.optaplanner.examples.conferencescheduling.domain.ConferenceParametrization;
import org.optaplanner.examples.conferencescheduling.domain.ConferenceSolution;
import org.optaplanner.examples.conferencescheduling.domain.Room;
import org.optaplanner.examples.conferencescheduling.domain.Speaker;
import org.optaplanner.examples.conferencescheduling.domain.Talk;
import org.optaplanner.examples.conferencescheduling.domain.TalkType;
import org.optaplanner.examples.conferencescheduling.domain.Timeslot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Import an instance of a Devoxx Conference from an API created from https://github.com/nicmarti/cfp-devoxx
 */
public class ConferenceSchedulingCfpDevoxxImporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConferenceSchedulingCfpDevoxxImporter.class);
    private static final String ZONE_ID = "Europe/Paris";
    private static final String[] SMALL_ROOMS_TYPE_NAMES = {"lab", "Hands-on Labs", "bof", "BOF (Bird of a Feather)", "ignite", "Ignite Sessions"};
    private static final String[] LARGE_ROOMS_TYPE_NAMES = {"tia", "Tools-in-Action", "uni", "University",
            "conf", "Conference", "Deep Dive", "key", "Keynote", "Quickie", "quick"};

    private static final String[] IGNORED_ROOM_IDS = {"ExhibitionHall"};
    private static final String[] IGNORED_SPEAKER_NAMES = {"Devoxx Partner"};

    private String conferenceBaseUrl;
    private Map<String, TalkType> talkTypeNameToTalkTypeMap;
    private Map<String, Room> roomIdToRoomMap;
    private Map<String, Speaker> speakerNameToSpeakerMap;
    private Map<String, Talk> talkCodeToTalkMap;
    private Set<String> talkUrlSet;
    private Set<String> trackIdSet;
    private Map<String, String> talkTypeIdToTalkTypeNameMap;

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
        ConferenceParametrization parametrization = new ConferenceParametrization();
        parametrization.setId(0L);
        solution.setParametrization(parametrization);

        importTalkTypeList();
        importTrackIdSet();
        importRoomList();
        importSpeakerList();
        importTalkList();
        importTimeslotList();

        for (TalkType talkType : solution.getTalkTypeList()) {
            LOGGER.info(talkType.getName()
                    + ": Timeslots Total is " + (timeslotTalkTypeToTotalMap.get(talkType.getName()) == null ? 0 : timeslotTalkTypeToTotalMap.get(talkType.getName()))
                    + ", Talks Total is " + (talkTalkTypeToTotalMap.get(talkType.getName()) == null ? 0 : talkTalkTypeToTotalMap.get(talkType.getName()))
            );
        }
        return solution;
    }

    private String getConferenceName() {
        LOGGER.debug("Sending a request to: " + conferenceBaseUrl);
        JsonObject conferenceObject = readJson(conferenceBaseUrl, JsonReader::readObject);
        return conferenceObject.getString("eventCode");
    }

    private void importTalkTypeList() {
        this.talkTypeNameToTalkTypeMap = new HashMap<>();
        this.talkTypeIdToTalkTypeNameMap = new HashMap<>();
        List<TalkType> talkTypeList = new ArrayList<>();

        String proposalTypeUrl = conferenceBaseUrl + "/proposalTypes";
        LOGGER.debug("Sending a request to: " + proposalTypeUrl);
        JsonObject rootObject = readJson(proposalTypeUrl, JsonReader::readObject);

        JsonArray talkTypeArray = rootObject.getJsonArray("proposalTypes");
        for (int i = 0; i < talkTypeArray.size(); i++) {
            JsonObject talkTypeObject = talkTypeArray.getJsonObject(i);
            String talkTypeName = talkTypeObject.getString("label");
            if (talkTypeNameToTalkTypeMap.keySet().contains(talkTypeName)) {
                LOGGER.warn("Duplicate talk type in " + proposalTypeUrl
                        + " at index " + i + ".");
                continue;
            }
            talkTypeIdToTalkTypeNameMap.put(talkTypeObject.getString("id"), talkTypeName);

            TalkType talkType = new TalkType((long) i, talkTypeName);
            talkType.setCompatibleRoomSet(new HashSet<>());
            talkType.setCompatibleTimeslotSet(new HashSet<>());

            talkTypeList.add(talkType);
            talkTypeNameToTalkTypeMap.put(talkTypeName, talkType);
        }

        solution.setTalkTypeList(talkTypeList);
    }

    private void importTrackIdSet() {
        this.trackIdSet = new HashSet<>();
        String tracksUrl = conferenceBaseUrl + "/tracks";
        LOGGER.debug("Sending a request to: " + tracksUrl);
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
        LOGGER.debug("Sending a request to: " + roomsUrl);
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

        roomList.sort(Comparator.comparing(Room::getName));
        solution.setRoomList(roomList);
    }

    private void importSpeakerList() {
        this.speakerNameToSpeakerMap = new HashMap<>();
        this.talkUrlSet = new HashSet<>();
        List<Speaker> speakerList = new ArrayList<>();

        String speakersUrl = conferenceBaseUrl + "/speakers";
        LOGGER.debug("Sending a request to: " + speakersUrl);
        JsonArray speakerArray = readJson(speakersUrl, JsonReader::readArray);

        for (int i = 0; i < speakerArray.size(); i++) {
            String speakerUrl = speakerArray.getJsonObject(i).getJsonArray("links").getJsonObject(0).getString("href");
            LOGGER.debug("Sending a request to: " + speakerUrl);
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

            JsonArray speakerTalksArray = speakerObject.getJsonArray("acceptedTalks");
            for (int j = 0; j < speakerTalksArray.size(); j++) {
                String talkUrl = speakerTalksArray.getJsonObject(j).getJsonArray("links").getJsonObject(0).getString("href");
                talkUrlSet.add(talkUrl);
            }
        }

        speakerList.sort(Comparator.comparing(Speaker::getName));
        solution.setSpeakerList(speakerList);
    }

    private void importTalkList() {
        this.talkCodeToTalkMap = new HashMap<>();
        solution.setTalkList(new ArrayList<>());

        for (String talkUrl : this.talkUrlSet) {
            LOGGER.debug("Sending a request to: " + talkUrl);
            JsonObject talkObject = readJson(talkUrl, JsonReader::readObject);

            createTalk(talkUrl, talkObject);
        }
    }

    private Talk createTalk(String talkUrl, JsonObject talkObject) {
        String code = talkObject.getString("id");
        String title = talkObject.getString("title");
        String talkTypeName = talkObject.getString("talkType");
        Set<String> themeTrackSet = new HashSet<>(Arrays.asList(talkObject.getString("trackId")));
        if (!trackIdSet.containsAll(themeTrackSet)) {
            throw new IllegalStateException("The talk (" + title + ") with id (" + code
                    + ") contains trackId + (" + trackIdSet + ") that doesn't exist in the trackIdSet.");
        }
        String languageg = talkObject.getString("lang");
        List<Speaker> speakerList = talkObject.getJsonArray("speakers").stream()
                .map(speakerJsonValue -> {
                    JsonReader jsonReader = Json.createReader(new StringReader(speakerJsonValue.toString()));
                    JsonObject speakerJsonObject = jsonReader.readObject();

                    String speakerName = speakerJsonObject.getString("name");
                    Speaker speaker = speakerNameToSpeakerMap.get(speakerName);
                    if (speaker == null) {
//                            throw new IllegalStateException("The talk (" + title + ") with id (" + code
//                                    + ") contains a speaker (" + speakerName + ", " + speakerJsonObject.getJsonObject("link").getString("href")
//                                    + ") that doesn't exist in speaker list.");

                        //TODO: Temporary workaround until the missing speakers issue is fixed, once fixed uncomment the throw block above and delete this
                        LOGGER.warn("The talk (" + code + ": " + title + ", " + talkUrl
                                + ") has a speaker (" + speakerName + ", " + speakerJsonObject.getJsonObject("link").getString("href")
                                + ") that doesn't exist in speaker list.");
                        String speakerUrl = speakerJsonObject.getJsonObject("link").getString("href");
                        LOGGER.debug("Sending a request to: " + speakerUrl);
                        JsonObject speakerObject = readJson(speakerUrl, JsonReader::readObject);

                        String speakerId = speakerObject.getString("uuid");

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
                            throw new IllegalStateException("Speaker (" + speakerName + ") with id (" + speakerId
                                    + ") already exists in the speaker list");
                        }
                        speakerNameToSpeakerMap.put(speakerName, speaker);
                        solution.getSpeakerList().add(speaker);
                    }
                    return speaker;
                })
                .collect(Collectors.toList());
        speakerList.removeAll(speakerList.stream().filter(
                speaker -> Arrays.asList(IGNORED_SPEAKER_NAMES).contains(speaker.getName())).collect(Collectors.toList()));

        return createTalk(code, title, talkTypeName, themeTrackSet, languageg, speakerList);
    }

    private Talk createTalk(String code, String title, String talkTypeName, Set<String> themeTrackSet,
                            String languageg, List<Speaker> speakerList) {
        Talk talk = new Talk((long) solution.getTalkList().size());
        talk.setCode(code);
        talk.setTitle(title);
        if (talkTypeNameToTalkTypeMap.get(talkTypeName) == null) {
            throw new IllegalStateException("The talk (" + title + ") with id (" + code
                    + ") has a talkType (" + talkTypeName + ") that doesn't exist in the talkType list.");
        }
        talk.setTalkType(talkTypeNameToTalkTypeMap.get(talkTypeName));
        talk.withThemeTrackTagSet(themeTrackSet)
                .withLanguage(languageg)
                .withSpeakerList(speakerList)
                .withAudienceLevel(0)
                .withAudienceTypeSet(new HashSet<>())
                .withContentTagSet(new HashSet<>())
                .withPreferredRoomTagSet(new HashSet<>())
                .withPreferredTimeslotTagSet(new HashSet<>())
                .withProhibitedRoomTagSet(new HashSet<>())
                .withProhibitedTimeslotTagSet(new HashSet<>())
                .withRequiredRoomTagSet(new HashSet<>())
                .withRequiredTimeslotTagSet(new HashSet<>())
                .withSectorTagSet(new HashSet<>())
                .withUndesiredRoomTagSet(new HashSet<>())
                .withUndesiredTimeslotTagSet(new HashSet<>())
                .withMutuallyExclusiveTalksTagSet(new HashSet<>())
                .withPrerequisiteTalksCodesSet(new HashSet<>());

        talkCodeToTalkMap.put(talk.getCode(), talk);
        solution.getTalkList().add(talk);
        talkTalkTypeToTotalMap.merge(talk.getTalkType().getName(), 1, Integer::sum);

        return talk;
    }

    private void importTimeslotList() {
        List<Timeslot> timeslotList = new ArrayList<>();
        Map<Timeslot, List<Room>> timeslotToAvailableRoomsMap = new HashMap<>();
        Map<Pair<LocalDateTime, LocalDateTime>, Timeslot> startAndEndTimeToTimeslotMap = new HashMap<>();

        Long timeSlotId = 0L;
        String schedulesUrl = conferenceBaseUrl + "/schedules/";
        LOGGER.debug("Sending a request to: " + schedulesUrl);
        JsonArray daysArray = readJson(schedulesUrl, JsonReader::readObject).getJsonArray("links");
        for (int i = 0; i < daysArray.size(); i++) {
            JsonObject dayObject = daysArray.getJsonObject(i);
            String dayUrl = dayObject.getString("href");

            LOGGER.debug("Sending a request to: " + dayUrl);
            JsonArray daySlotsArray = readJson(dayUrl, JsonReader::readObject).getJsonArray("slots");

            for (int j = 0; j < daySlotsArray.size(); j++) {
                JsonObject timeslotObject = daySlotsArray.getJsonObject(j);

                LocalDateTime startDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timeslotObject.getJsonNumber("fromTimeMillis").longValue()),
                        ZoneId.of(ZONE_ID));
                LocalDateTime endDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timeslotObject.getJsonNumber("toTimeMillis").longValue()),
                        ZoneId.of(ZONE_ID));

                Room room = roomIdToRoomMap.get(timeslotObject.getString("roomId"));
                if (room == null) {
                    throw new IllegalStateException("The timeslot (" + timeslotObject.getString("slotId") + ") has a roomId (" + timeslotObject.getString("roomId")
                            + ") that does not exist in the rooms list");
                }

                // Assuming slotId is of format: tia_room6_monday_12_.... take only "tia"
                // Specific for DevoxxBE, unknown in slotId matches a keynote slot
                talkTypeIdToTalkTypeNameMap.put("unknown", talkTypeIdToTalkTypeNameMap.get("key"));
                String talkTypeName = talkTypeIdToTalkTypeNameMap.get(timeslotObject.getString("slotId").split("_")[0]);
                TalkType timeslotTalkType = talkTypeNameToTalkTypeMap.get(talkTypeName);

                Timeslot timeslot;
                if (startAndEndTimeToTimeslotMap.keySet().contains(Pair.of(startDateTime, endDateTime))) {
                    timeslot = startAndEndTimeToTimeslotMap.get(Pair.of(startDateTime, endDateTime));
                    timeslotToAvailableRoomsMap.get(timeslot).add(room);
                    if (timeslotTalkType != null) {
                        timeslot.getTalkTypeSet().add(timeslotTalkType);
                    }
                } else {
                    timeslot = new Timeslot(timeSlotId++);
                    timeslot.withStartDateTime(startDateTime)
                            .withEndDateTime(endDateTime)
                            .withTalkTypeSet(timeslotTalkType == null ? new HashSet<>() : new HashSet<>(Arrays.asList(timeslotTalkType)));
                    timeslot.setTagSet(new HashSet<>());

                    timeslotList.add(timeslot);
                    timeslotToAvailableRoomsMap.put(timeslot, new ArrayList<>(Arrays.asList(room)));
                    startAndEndTimeToTimeslotMap.put(Pair.of(startDateTime, endDateTime), timeslot);
                }

                if (!timeslotObject.isNull("talk")) {
                    scheduleTalk(timeslotObject, room, timeslot);
                }

                for (TalkType talkType : timeslot.getTalkTypeSet()) {
                    talkType.getCompatibleTimeslotSet().add(timeslot);
                }
                timeslotTalkTypeToTotalMap.merge(talkTypeName, 1, Integer::sum);
            }
        }

        for (Room room : solution.getRoomList()) {
            room.setUnavailableTimeslotSet(timeslotList.stream()
                    .filter(timeslot -> !timeslotToAvailableRoomsMap.get(timeslot).contains(room))
                    .collect(Collectors.toSet()));
        }

        solution.setTimeslotList(timeslotList);
    }

    private Set<TalkType> getTalkTypeSetForCapacity(int capacity) {
        Set<TalkType> talkTypeSet = new HashSet<>();
        List<String> typeNames = new ArrayList<>();
        if (capacity < 100) {
            typeNames.addAll(Arrays.asList(SMALL_ROOMS_TYPE_NAMES));
        } else {
            typeNames.addAll(Arrays.asList(LARGE_ROOMS_TYPE_NAMES));
        }

        for (String talkTypeName : typeNames) {
            TalkType talkType = talkTypeNameToTalkTypeMap.get(talkTypeName);
            if (talkType != null) {
                talkTypeSet.add(talkType);
            }
        }

        return talkTypeSet;
    }

    private void scheduleTalk(JsonObject timeslotObject, Room room, Timeslot timeslot) {
        Talk talk = talkCodeToTalkMap.get(timeslotObject.getJsonObject("talk").getString("id"));
        if (talk == null) {
//            throw new IllegalStateException("The timeslot (" + timeslotObject.getString("slotId")
//                    + ") has a talk (" + timeslotObject.getJsonObject("talk").getString("id")
//                    + ") that does not exist in the talk list");
            //TODO: Temporary workaround until the missing speakers issue is fixed, once fixed uncomment the throw block above and delete this
            talk = createTalk("", timeslotObject.getJsonObject("talk"));
        }
        if (talk.isPinnedByUser()) {
            throw new IllegalStateException("The timeslot (" + timeslotObject.getString("slotId")
                    + ") has a talk (" + timeslotObject.getJsonObject("talk").getString("id")
                    + ") that is already pinned by user at another timeslot (" + talk.getTimeslot().toString() + ").");
        }
        talk.setRoom(room);
        talk.setTimeslot(timeslot);
        talk.setPinnedByUser(true);
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