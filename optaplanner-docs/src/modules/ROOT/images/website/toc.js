/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

$(document).ready(function() {
    $('ul.sectlevel1').wrap('<div id="tocTree"></div>');

    var $tocTree = $('#tocTree');
    $tocTree.jstree({
         "core" : {
             "themes" : {"variant" : "small", "icons" : false}
         },
         "plugins" : [ "search", "state", "wholerow" ]
    })
    .on("activate_node.jstree", function (e, data) { location.href = data.node.a_attr.href; });

    $tocTree.before('<input placeholder="Search TOC" id="tocSearch" type="text">');

    var $tocSearch = $('#tocSearch');
    var searchTimeout = false;
    $tocSearch.keyup(function () {
        if (searchTimeout) {
            clearTimeout(searchTimeout);
        }
        searchTimeout = setTimeout(function () {
            var v = $('#tocSearch').val();
            $('#tocTree').jstree(true).search(v);
        }, 250);
    });

    $tocSearch.after('<a href="#" id="tocTreeExpandAll" title="Expand All"><i class="fa fa-plus-square" aria-hidden="true"></i></a><a href="#" id="tocTreeCollapseAll" title="Collapse All"><i class="fa fa-minus-square" aria-hidden="true"></i></a>');
    $('#tocTreeExpandAll').click(function() { $('#tocTree').jstree('open_all'); });
    $('#tocTreeCollapseAll').click(function() { $('#tocTree').jstree('close_all'); });
});
