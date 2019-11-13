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

var PROBLEM_ID = 7;
var autoRefreshCount = 0;
var autoRefreshIntervalId = null;

function refreshTimeTable() {
    $.getJSON("/timeTable/" + PROBLEM_ID, function (timeTable) {
        var timeTableByRoom = $("#timeTableByRoom");
        timeTableByRoom.children().remove();
        var unassignedLessonsByRoom = $("#unassignedLessonsByRoom");
        unassignedLessonsByRoom.children().remove();

        var thead = $("<thead>").appendTo(timeTableByRoom);
        var headerRow = $("<tr>").appendTo(thead);
        headerRow.append($("<th>Timeslot</th>"));
        $.each(timeTable.roomList, function (index, room) {
            headerRow.append($("<th>" + room.name + "</th>"));
        });

        $.each(timeTable.timeslotList, function (index, timeslot) {
            var row = $("<tr>").appendTo(timeTableByRoom);
            row.append($("<th>" + timeslot.dayOfWeek + " " + timeslot.startTime + "-" + timeslot.endTime + "</th>"));
            $.each(timeTable.roomList, function (index, room) {
                row.append($("<td id=\"timeslot" + timeslot.id + "room" + room.id + "\"></td>"));
            });
        });

        $.each(timeTable.lessonList, function (index, lesson) {
            var lessonElement = $("<div class=\"card lesson\"><div class=\"card-body p-2\">"
                    + "<h5 class=\"card-title mb-1\">" + lesson.subject + "</h5>"
                    + "<p class=\"card-text text-muted ml-2 mb-1\">by " + lesson.teacher + "</p>"
                    + "<p class=\"card-text ml-2\">" + lesson.studentGroup + "</p>"
                    + "</div></div>");
            if (lesson.timeslot == null || lesson.room == null) {
                unassignedLessonsByRoom.append(lessonElement);
            } else {
                $("#timeslot" + lesson.timeslot.id + "room" + lesson.room.id).append(lessonElement);
            }
        });

    });
}

function addRoom() {
    var roomName = $("#roomName").val();
    $.post("/timeTable/" + PROBLEM_ID + "/addRoom", JSON.stringify({"name": roomName}), function () {
        refreshTimeTable();
    }).fail(function() {
        console.warn("Error on post to /timeTable/addRoom.")
    });
    $('#roomDialog').modal('toggle');
}

function solveTimeTable() {
    $.post("/timeTable/" + PROBLEM_ID + "/solve", function () {
        autoRefreshCount = 16;
        if (autoRefreshIntervalId == null) {
            autoRefreshIntervalId = setInterval(autoRefresh, 2000);
        }
    }).fail(function() {
        console.warn("Error on post to /timeTable/solve.")
    });
}

function autoRefresh() {
    refreshTimeTable();
    autoRefreshCount--;
    if (autoRefreshCount <= 0) {
        clearInterval(autoRefreshIntervalId);
        autoRefreshIntervalId = null;
    }
}

$(document).ready( function() {
    $.ajaxSetup({
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        }
    });
    $("#addRoomSubmitButton").click(function() {
        addRoom();
    });
    $("#refreshButton").click(function() {
        refreshTimeTable();
    });
    $("#solveButton").click(function() {
        solveTimeTable();
    });

    refreshTimeTable();
});
