package org.optaplanner.examples.conferencescheduling.domain;

import java.util.List;
import java.util.Set;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.entity.PlanningPin;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.examples.common.domain.AbstractPersistable;

@PlanningEntity
public class Talk extends AbstractPersistable {

    private String code;
    private String title;
    private TalkType talkType;
    private List<Speaker> speakerList;
    private Set<String> themeTrackTagSet;
    private Set<String> sectorTagSet;
    private Set<String> audienceTypeSet;
    private int audienceLevel;
    private Set<String> contentTagSet;
    private String language;
    private Set<String> requiredTimeslotTagSet;
    private Set<String> preferredTimeslotTagSet;
    private Set<String> prohibitedTimeslotTagSet;
    private Set<String> undesiredTimeslotTagSet;
    private Set<String> requiredRoomTagSet;
    private Set<String> preferredRoomTagSet;
    private Set<String> prohibitedRoomTagSet;
    private Set<String> undesiredRoomTagSet;
    private Set<String> mutuallyExclusiveTalksTagSet;
    private Set<Talk> prerequisiteTalkSet;
    private int favoriteCount;
    private int crowdControlRisk;
    private Timeslot publishedTimeslot;
    private Room publishedRoom;

    @PlanningPin
    private boolean pinnedByUser = false;

    @PlanningVariable
    private Timeslot timeslot;

    @PlanningVariable
    private Room room;

    public Talk() {
    }

    public Talk(long id) {
        super(id);
    }

    public boolean hasSpeaker(Speaker speaker) {
        return speakerList.contains(speaker);
    }

    public int overlappingThemeTrackCount(Talk other) {
        return overlappingCount(themeTrackTagSet, other.themeTrackTagSet);
    }

    private static <Item_> int overlappingCount(Set<Item_> left, Set<Item_> right) {
        int leftSize = left.size();
        if (leftSize == 0) {
            return 0;
        }
        int rightSize = right.size();
        if (rightSize == 0) {
            return 0;
        }
        Set<Item_> smaller = leftSize < rightSize ? left : right;
        Set<Item_> other = smaller == left ? right : left;
        int overlappingCount = 0;
        for (Item_ item : smaller) { // Iterate over smaller set, lookup in the larger.
            if (other.contains(item)) {
                overlappingCount++;
            }
        }
        return overlappingCount;
    }

    public int overlappingSectorCount(Talk other) {
        return overlappingCount(sectorTagSet, other.sectorTagSet);
    }

    public int overlappingAudienceTypeCount(Talk other) {
        return overlappingCount(audienceTypeSet, other.audienceTypeSet);

    }

    public int overlappingContentCount(Talk other) {
        return overlappingCount(contentTagSet, other.contentTagSet);

    }

    public int missingRequiredTimeslotTagCount() {
        if (timeslot == null) {
            return 0;
        }
        return missingCount(requiredTimeslotTagSet, timeslot.getTagSet());

    }

    private static <Item_> int missingCount(Set<Item_> required, Set<Item_> available) {
        int requiredCount = required.size();
        if (requiredCount == 0) {
            return 0; // If no items are required, none can be missing.
        }
        int availableCount = available.size();
        if (availableCount == 0) {
            return requiredCount; // All the items are missing.
        }
        int missingCount = 0;
        for (Item_ item : required) {
            if (!available.contains(item)) {
                missingCount++;
            }
        }
        return missingCount;
    }

    public int missingPreferredTimeslotTagCount() {
        if (timeslot == null) {
            return 0;
        }
        return missingCount(preferredTimeslotTagSet, timeslot.getTagSet());
    }

    public int prevailingProhibitedTimeslotTagCount() {
        if (timeslot == null) {
            return 0;
        }
        return overlappingCount(prohibitedTimeslotTagSet, timeslot.getTagSet());
    }

    public int prevailingUndesiredTimeslotTagCount() {
        if (timeslot == null) {
            return 0;
        }
        return overlappingCount(undesiredTimeslotTagSet, timeslot.getTagSet());
    }

    public int missingRequiredRoomTagCount() {
        if (room == null) {
            return 0;
        }
        return missingCount(requiredRoomTagSet, room.getTagSet());

    }

    public int missingPreferredRoomTagCount() {
        if (room == null) {
            return 0;
        }
        return missingCount(preferredRoomTagSet, room.getTagSet());
    }

    public int prevailingProhibitedRoomTagCount() {
        if (room == null) {
            return 0;
        }
        return overlappingCount(prohibitedRoomTagSet, room.getTagSet());

    }

    public int prevailingUndesiredRoomTagCount() {
        if (room == null) {
            return 0;
        }
        return overlappingCount(undesiredRoomTagSet, room.getTagSet());
    }

    public int missingSpeakerRequiredTimeslotTagCount() {
        if (timeslot == null) {
            return 0;
        }
        int count = 0;
        for (Speaker speaker : speakerList) {
            count += missingCount(speaker.getRequiredTimeslotTagSet(), timeslot.getTagSet());
        }
        return count;
    }

    public int missingSpeakerPreferredTimeslotTagCount() {
        if (timeslot == null) {
            return 0;
        }
        int count = 0;
        for (Speaker speaker : speakerList) {
            count += missingCount(speaker.getPreferredTimeslotTagSet(), timeslot.getTagSet());
        }
        return count;
    }

    public int prevailingSpeakerProhibitedTimeslotTagCount() {
        if (timeslot == null) {
            return 0;
        }
        int count = 0;
        for (Speaker speaker : speakerList) {
            count += overlappingCount(speaker.getProhibitedTimeslotTagSet(), timeslot.getTagSet());
        }
        return count;
    }

    public int prevailingSpeakerUndesiredTimeslotTagCount() {
        if (timeslot == null) {
            return 0;
        }
        int count = 0;
        for (Speaker speaker : speakerList) {
            count += overlappingCount(speaker.getUndesiredTimeslotTagSet(), timeslot.getTagSet());
        }
        return count;
    }

    public int missingSpeakerRequiredRoomTagCount() {
        if (room == null) {
            return 0;
        }
        int count = 0;
        for (Speaker speaker : speakerList) {
            count += missingCount(speaker.getRequiredRoomTagSet(), room.getTagSet());
        }
        return count;
    }

    public int missingSpeakerPreferredRoomTagCount() {
        if (room == null) {
            return 0;
        }
        int count = 0;
        for (Speaker speaker : speakerList) {
            count += missingCount(speaker.getPreferredRoomTagSet(), room.getTagSet());
        }
        return count;
    }

    public int prevailingSpeakerProhibitedRoomTagCount() {
        if (room == null) {
            return 0;
        }
        int count = 0;
        for (Speaker speaker : speakerList) {
            count += overlappingCount(speaker.getProhibitedRoomTagSet(), room.getTagSet());
        }
        return count;
    }

    public int prevailingSpeakerUndesiredRoomTagCount() {
        if (room == null) {
            return 0;
        }
        int count = 0;
        for (Speaker speaker : speakerList) {
            count += overlappingCount(speaker.getUndesiredRoomTagSet(), room.getTagSet());
        }
        return count;
    }

    public boolean hasUnavailableRoom() {
        if (timeslot == null || room == null) {
            return false;
        }
        return room.getUnavailableTimeslotSet().contains(timeslot);
    }

    public int overlappingMutuallyExclusiveTalksTagCount(Talk other) {
        return overlappingCount(mutuallyExclusiveTalksTagSet, other.mutuallyExclusiveTalksTagSet);
    }

    public boolean hasMutualSpeaker(Talk other) {
        for (Speaker speaker : speakerList) {
            if (other.hasSpeaker(speaker)) {
                return true;
            }
        }
        return false;
    }

    public Integer getDurationInMinutes() {
        return timeslot == null ? null : timeslot.getDurationInMinutes();
    }

    public boolean overlapsTime(Talk other) {
        return timeslot != null && other.getTimeslot() != null && timeslot.overlapsTime(other.getTimeslot());
    }

    public int overlappingDurationInMinutes(Talk other) {
        if (timeslot == null) {
            return 0;
        }
        if (other.getTimeslot() == null) {
            return 0;
        }
        return timeslot.getOverlapInMinutes(other.getTimeslot());
    }

    public int combinedDurationInMinutes(Talk other) {
        if (timeslot == null) {
            return 0;
        }
        if (other.getTimeslot() == null) {
            return 0;
        }
        return timeslot.getDurationInMinutes() + other.getTimeslot().getDurationInMinutes();
    }

    @Override
    public String toString() {
        return code;
    }

    @ValueRangeProvider
    public Set<Timeslot> getTimeslotRange() {
        return talkType.getCompatibleTimeslotSet();
    }

    @ValueRangeProvider
    public Set<Room> getRoomRange() {
        return talkType.getCompatibleRoomSet();
    }

    // ************************************************************************
    // Simple getters and setters
    // ************************************************************************

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public TalkType getTalkType() {
        return talkType;
    }

    public void setTalkType(TalkType talkType) {
        this.talkType = talkType;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<Speaker> getSpeakerList() {
        return speakerList;
    }

    public Set<String> getThemeTrackTagSet() {
        return themeTrackTagSet;
    }

    public void setThemeTrackTagSet(Set<String> themeTrackTagSet) {
        this.themeTrackTagSet = themeTrackTagSet;
    }

    public Set<String> getSectorTagSet() {
        return sectorTagSet;
    }

    public void setSectorTagSet(Set<String> sectorTagSet) {
        this.sectorTagSet = sectorTagSet;
    }

    public Set<String> getAudienceTypeSet() {
        return audienceTypeSet;
    }

    public void setAudienceTypeSet(Set<String> audienceTypeSet) {
        this.audienceTypeSet = audienceTypeSet;
    }

    public int getAudienceLevel() {
        return audienceLevel;
    }

    public void setAudienceLevel(int audienceLevel) {
        this.audienceLevel = audienceLevel;
    }

    public Set<String> getContentTagSet() {
        return contentTagSet;
    }

    public void setContentTagSet(Set<String> contentTagSet) {
        this.contentTagSet = contentTagSet;
    }

    public String getLanguage() {
        return language;
    }

    public void setSpeakerList(List<Speaker> speakerList) {
        this.speakerList = speakerList;
    }

    public Set<String> getRequiredTimeslotTagSet() {
        return requiredTimeslotTagSet;
    }

    public void setRequiredTimeslotTagSet(Set<String> requiredTimeslotTagSet) {
        this.requiredTimeslotTagSet = requiredTimeslotTagSet;
    }

    public Set<String> getPreferredTimeslotTagSet() {
        return preferredTimeslotTagSet;
    }

    public void setPreferredTimeslotTagSet(Set<String> preferredTimeslotTagSet) {
        this.preferredTimeslotTagSet = preferredTimeslotTagSet;
    }

    public Set<String> getProhibitedTimeslotTagSet() {
        return prohibitedTimeslotTagSet;
    }

    public void setProhibitedTimeslotTagSet(Set<String> prohibitedTimeslotTagSet) {
        this.prohibitedTimeslotTagSet = prohibitedTimeslotTagSet;
    }

    public Set<String> getUndesiredTimeslotTagSet() {
        return undesiredTimeslotTagSet;
    }

    public void setUndesiredTimeslotTagSet(Set<String> undesiredTimeslotTagSet) {
        this.undesiredTimeslotTagSet = undesiredTimeslotTagSet;
    }

    public Set<String> getRequiredRoomTagSet() {
        return requiredRoomTagSet;
    }

    public void setRequiredRoomTagSet(Set<String> requiredRoomTagSet) {
        this.requiredRoomTagSet = requiredRoomTagSet;
    }

    public Set<String> getPreferredRoomTagSet() {
        return preferredRoomTagSet;
    }

    public void setPreferredRoomTagSet(Set<String> preferredRoomTagSet) {
        this.preferredRoomTagSet = preferredRoomTagSet;
    }

    public Set<String> getProhibitedRoomTagSet() {
        return prohibitedRoomTagSet;
    }

    public void setProhibitedRoomTagSet(Set<String> prohibitedRoomTagSet) {
        this.prohibitedRoomTagSet = prohibitedRoomTagSet;
    }

    public Set<String> getUndesiredRoomTagSet() {
        return undesiredRoomTagSet;
    }

    public void setUndesiredRoomTagSet(Set<String> undesiredRoomTagSet) {
        this.undesiredRoomTagSet = undesiredRoomTagSet;
    }

    public boolean isPinnedByUser() {
        return pinnedByUser;
    }

    public void setPinnedByUser(boolean pinnedByUser) {
        this.pinnedByUser = pinnedByUser;
    }

    public Timeslot getTimeslot() {
        return timeslot;
    }

    public void setTimeslot(Timeslot timeslot) {
        this.timeslot = timeslot;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Set<String> getMutuallyExclusiveTalksTagSet() {
        return mutuallyExclusiveTalksTagSet;
    }

    public void setMutuallyExclusiveTalksTagSet(Set<String> mutuallyExclusiveTalksTagSet) {
        this.mutuallyExclusiveTalksTagSet = mutuallyExclusiveTalksTagSet;
    }

    public Set<Talk> getPrerequisiteTalkSet() {
        return prerequisiteTalkSet;
    }

    public void setPrerequisiteTalkSet(Set<Talk> prerequisiteTalkSet) {
        this.prerequisiteTalkSet = prerequisiteTalkSet;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public int getCrowdControlRisk() {
        return crowdControlRisk;
    }

    public void setCrowdControlRisk(int crowdControlRisk) {
        this.crowdControlRisk = crowdControlRisk;
    }

    public Timeslot getPublishedTimeslot() {
        return publishedTimeslot;
    }

    public void setPublishedTimeslot(Timeslot publishedTimeslot) {
        this.publishedTimeslot = publishedTimeslot;
    }

    public Room getPublishedRoom() {
        return publishedRoom;
    }

    public void setPublishedRoom(Room publishedRoom) {
        this.publishedRoom = publishedRoom;
    }

    // ************************************************************************
    // With methods
    // ************************************************************************

    public Talk withTalkType(TalkType talkType) {
        this.talkType = talkType;
        return this;
    }

    public Talk withSpeakerList(List<Speaker> speakerList) {
        this.speakerList = speakerList;
        return this;
    }

    public Talk withThemeTrackTagSet(Set<String> themeTrackTagSet) {
        this.themeTrackTagSet = themeTrackTagSet;
        return this;
    }

    public Talk withSectorTagSet(Set<String> sectorTagSet) {
        this.sectorTagSet = sectorTagSet;
        return this;
    }

    public Talk withAudienceTypeSet(Set<String> audienceTypeSet) {
        this.audienceTypeSet = audienceTypeSet;
        return this;
    }

    public Talk withAudienceLevel(int audienceLevel) {
        this.audienceLevel = audienceLevel;
        return this;
    }

    public Talk withContentTagSet(Set<String> contentTagSet) {
        this.contentTagSet = contentTagSet;
        return this;
    }

    public Talk withLanguage(String language) {
        this.language = language;
        return this;
    }

    public Talk withRequiredRoomTagSet(Set<String> requiredRoomTagSet) {
        this.requiredRoomTagSet = requiredRoomTagSet;
        return this;
    }

    public Talk withPreferredRoomTagSet(Set<String> preferredRoomTagSet) {
        this.preferredRoomTagSet = preferredRoomTagSet;
        return this;
    }

    public Talk withProhibitedRoomTagSet(Set<String> prohibitedRoomTagSet) {
        this.prohibitedRoomTagSet = prohibitedRoomTagSet;
        return this;
    }

    public Talk withUndesiredRoomTagSet(Set<String> undesiredRoomTagSet) {
        this.undesiredRoomTagSet = undesiredRoomTagSet;
        return this;
    }

    public Talk withRequiredTimeslotTagSet(Set<String> requiredTimeslotTagSet) {
        this.requiredTimeslotTagSet = requiredTimeslotTagSet;
        return this;
    }

    public Talk withProhibitedTimeslotTagSet(Set<String> prohibitedTimeslotTagSet) {
        this.prohibitedTimeslotTagSet = prohibitedTimeslotTagSet;
        return this;
    }

    public Talk withPreferredTimeslotTagSet(Set<String> preferredTimslotTagSet) {
        this.preferredTimeslotTagSet = preferredTimslotTagSet;
        return this;
    }

    public Talk withUndesiredTimeslotTagSet(Set<String> undesiredTimeslotTagSet) {
        this.undesiredTimeslotTagSet = undesiredTimeslotTagSet;
        return this;
    }

    public Talk withMutuallyExclusiveTalksTagSet(Set<String> mutuallyExclusiveTalksTagSet) {
        this.mutuallyExclusiveTalksTagSet = mutuallyExclusiveTalksTagSet;
        return this;
    }

    public Talk withPrerequisiteTalksCodesSet(Set<Talk> prerequisiteTalksCodesSet) {
        this.prerequisiteTalkSet = prerequisiteTalksCodesSet;
        return this;
    }

    public Talk withFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
        return this;
    }

    public Talk withTimeslot(Timeslot timeslot) {
        this.timeslot = timeslot;
        return this;
    }

    public Talk withRoom(Room room) {
        this.room = room;
        return this;
    }

}
