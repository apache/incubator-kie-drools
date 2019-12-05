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

var autoRefreshCount = 0;
var autoRefreshIntervalId = null;

function refreshTimeTable() {
    $.getJSON("/timeTable", function (timeTable) {
        $("#score").text("Score: "+ (timeTable.score == null ? "?" : timeTable.score));

        var timeTableByRoom = $("#timeTableByRoom");
        timeTableByRoom.children().remove();
        var unassignedLessonsByRoom = $("#unassignedLessonsByRoom");
        unassignedLessonsByRoom.children().remove();

        var thead = $("<thead>").appendTo(timeTableByRoom);
        var headerRow = $("<tr>").appendTo(thead);
        headerRow.append($("<th>Timeslot</th>"));
        $.each(timeTable.roomList, function (index, room) {
            headerRow.append($("<th>"
                    + "<span>" + room.name + "</span>"
                    + "<button id=\"deleteRoomButton-" + room.id + "\" type=\"button\" class=\"ml-2 mb-1 btn btn-light btn-sm p-1\">"
                    + "<small class=\"fas fa-trash\"></small>"
                    + "</button>"
                    + "</th>"));
            $("#deleteRoomButton-" + room.id).click(function() {
                deleteRoom(room);
            });
        });

        $.each(timeTable.timeslotList, function (index, timeslot) {
            var row = $("<tr>").appendTo(timeTableByRoom);
            row.append($("<th class=\"align-middle\">"
                    + "<span>" + timeslot.dayOfWeek + " " + timeslot.startTime + "-" + timeslot.endTime + "</span>"
                    + "<button id=\"deleteTimeslotButton-" + timeslot.id + "\" type=\"button\" class=\"ml-2 mb-1 btn btn-light btn-sm p-1\">"
                    + "<small class=\"fas fa-trash\"></small>"
                    + "</button>"
                    + "</th>"));
            $("#deleteTimeslotButton-" + timeslot.id).click(function() {
                deleteTimeslot(timeslot);
            });
            $.each(timeTable.roomList, function (index, room) {
                row.append($("<td id=\"timeslot" + timeslot.id + "room" + room.id + "\"></td>"));
            });
        });

        $.each(timeTable.lessonList, function (index, lesson) {
            var lessonElement = $("<div class=\"card lesson\"><div class=\"card-body p-2\">"
                    + "<button id=\"deleteLessonButton-" + lesson.id + "\" type=\"button\" class=\"ml-2 btn btn-light btn-sm p-1 float-right\">"
                    + "<small class=\"fas fa-trash\"></small>"
                    + "</button>"
                    + "<h5 class=\"card-title mb-1\">" + lesson.subject + "</h5>"
                    + "<p class=\"card-text text-muted ml-2 mb-1\">by " + lesson.teacher + "</p>"
                    + "<small class=\"ml-2 mt-1 card-text text-muted align-bottom float-right\">" + lesson.id + "</small>"
                    + "<p class=\"card-text ml-2\">" + lesson.studentGroup + "</p>"
                    + "</div></div>");
            if (lesson.timeslot == null || lesson.room == null) {
                unassignedLessonsByRoom.append(lessonElement);
            } else {
                $("#timeslot" + lesson.timeslot.id + "room" + lesson.room.id).append(lessonElement);
            }
            $("#deleteLessonButton-" + lesson.id).click(function() {
                deleteLesson(lesson);
            });
        });

    });
}

function solve() {
    $.post("/timeTable/solve", function () {
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

function stopSolving() {
    $.post("/timeTable/stopSolving", function () {
        refreshTimeTable();
    }).fail(function() {
        console.warn("Error on post to /timeTable/stopSolving.")
    });
}

function addLesson() {
    $.post("/lessons", JSON.stringify({
        "subject": $("#lesson_subject").val(),
        "teacher": $("#lesson_teacher").val(),
        "studentGroup": $("#lesson_studentGroup").val()
    }), function () {
        refreshTimeTable();
    }).fail(function() {
        console.warn("Error on post to /lessons.")
    });
    $('#lessonDialog').modal('toggle');
}

function deleteLesson(lesson) {
    $.delete("/lessons/" + lesson.id, function () {
        refreshTimeTable();
    }).fail(function() {
        console.warn("Error on delete to /lessons/" + lesson.id +".")
    });
}

function addTimeslot() {
    $.post("/timeslots", JSON.stringify({
        "dayOfWeek": $("#timeslot_dayOfWeek").val(),
        "startTime": $("#timeslot_startTime").val(),
        "endTime": $("#timeslot_endTime").val()
    }), function () {
        refreshTimeTable();
    }).fail(function() {
        console.warn("Error on post to /timeslots.")
    });
    $('#timeslotDialog').modal('toggle');
}

function deleteTimeslot(timeslot) {
    $.delete("/timeslots/" + timeslot.id, function () {
        refreshTimeTable();
    }).fail(function() {
        console.warn("Error on delete to /timeslots/" + timeslot.id +".")
    });
}

function addRoom() {
    $.post("/rooms", JSON.stringify({
        "name": $("#room_name").val()
    }), function () {
        refreshTimeTable();
    }).fail(function() {
        console.warn("Error on post to /rooms.")
    });
    $('#roomDialog').modal('toggle');
}

function deleteRoom(room) {
    $.delete("/rooms/" + room.id, function () {
        refreshTimeTable();
    }).fail(function() {
        console.warn("Error on delete to /rooms/" + room.id +".")
    });
}

function deleteRoom(room) {
    $.delete("/rooms/" + room.id, function () {
        refreshTimeTable();
    }).fail(function() {
        console.warn("Error on delete to /rooms/" + room.id +".")
    });
}

$(document).ready( function() {
    $.ajaxSetup({
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        }
    });
    // Extend jQuery to support $.put() and $.delete()
    jQuery.each( [ "put", "delete" ], function( i, method ) {
        jQuery[method] = function (url, data, callback, type) {
            if (jQuery.isFunction(data)) {
                type = type || callback;
                callback = data;
                data = undefined;
            }
            return jQuery.ajax({
                url: url,
                type: method,
                dataType: type,
                data: data,
                success: callback
            });
        };
    });


    $("#refreshButton").click(function() {
        refreshTimeTable();
    });
    $("#solveButton").click(function() {
        solve();
    });
    $("#stopSolvingButton").click(function() {
        stopSolving();
    });
    $("#addLessonSubmitButton").click(function() {
        addLesson();
    });
    $("#addTimeslotSubmitButton").click(function() {
        addTimeslot();
    });
    $("#addRoomSubmitButton").click(function() {
        addRoom();
    });

    refreshTimeTable();
});
