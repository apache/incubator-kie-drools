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
