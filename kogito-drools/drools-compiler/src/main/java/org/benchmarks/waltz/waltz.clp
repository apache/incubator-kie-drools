; Copyright -c- 1996 KnowledgeBased Systems Corporation (KBSC)
; Original code in CLIPS by Satoshi Nishiyama and the University of Texas at Austin 
; modified for use with Jess by James Owen
; Note that this is a benchmark program ONLY and is not warranted to do anything
; KBSC assumes no responsiblity for anyone using or mis-using this program

; Waltz 50 does nothing more than create a 3-D image from 2-D line points
; by creating junctions, edges from lines 
; Waltz 50, unlike the regular Waltz, does this 50 times

; If you find a mistake, please email jco@kbsc.com with details.  Thanks...
; 
 
(load-package org.benchmarks.waltz.WaltzFile)

; Notes:  
; Lines have the lable "line" followed by the 2 points defining the line.  
; Edges are like lines accept that they can be labeled permanently labelled and plotted.
; Junctions are defined by 4 points.  
; The basepoint is where the 3 (2) lines intersect.
; The points p1, p2, p3 are the other endpoints of the lines at this junction

; ==================
(deftemplate stage 
	(slot value))
; =================
(deftemplate line 
	(slot p1) 
	(slot p2))
	; ================
(deftemplate edge 
	(slot p1) 
	(slot p2 )
	(slot joined )
	(slot label )
	(slot plotted))
; =====================
(deftemplate junction 
	(slot p1 )
	(slot p2 )
	(slot p3 )
	(slot base_point )
	(slot type))
	

; ------------------------------------ 
; The first rule does nothing more than 
; check to see if the start flag is in WM
(defrule begin
	(initial-fact)
	=>)

; --------------------------------------------------- 
; If the duplicate flag is set, and there is still a line in WM, delete the line
; and add two edges. One edge runs from p1 to p2 and the other runs from p2 to
; p1.  We then plot the edge.
(defrule reverse_edges
	(stage (value duplicate))
	?f2 <- (line (p1 ?p1) (p2 ?p2))
	=>
	(printout t "Draw " ?p1 "-" ?p2 crlf)
	(assert (edge (p1 ?p1) (p2 ?p2) (joined false)))
    (assert (edge (p1 ?p2) (p2 ?p1) (joined false)))
	(retract ?f2))

; -------------------------------------------- 
; If the duplicate flag is set, and there are no more lines, then remove the
; duplicating flag and set the make junctions flag.
(defrule done_reversing
	(declare (salience -10))
	?f1 <- (stage (value duplicate))
        (not (line))
	=>
	(modify ?f1 (value detect_junctions))
	(printout t "reversing done" crlf)
 )
 ; ----------------------------------------
; If three edges meet at a point and none of them have already been joined in
; a junction, then make the corresponding type of junction and label the
; edges joined.  This production calls make-3_junction to determine
; what type of junction it is based on the angles inscribed by the
; intersecting edges
(defrule make-3_junction
	(declare (salience 10))
	(stage (value detect_junctions))
	?f2 <- (edge (p1 ?base_point) (p2 ?p1) (joined false))
	?f3 <- (edge (p1 ?base_point) (p2 ?p2&~?p1) (joined false))
	?f4 <- (edge (p1 ?base_point) (p2 ?p3&~?p1&~?p2) (joined false))
	=>
	(make_3_junction ?base_point ?p1 ?p2 ?p3)
	(modify ?f2 (joined true))
	(modify ?f3 (joined true))
	(modify ?f4 (joined true))
	(printout t "make 3 junction" crlf)
 )
; ---------------------------------
;If two, and only two, edges meet that have not already been joined, then
;the junction is an "L"
(defrule make_L
	(stage (value detect_junctions))
	?f2 <- (edge (p1 ?base_point) (p2 ?p2) (joined false))
	?f3 <- (edge (p1 ?base_point) (p2 ?p3&~?p2) (joined false))
	(not (edge (p1 ?base_point) (p2 ~?p2&~?p3)))
	=>
	(assert (junction
		(type L)
		(base_point ?base_point)
		(p1 ?p2)
		(p2 ?p3)))
	(modify ?f2 (joined true))
	(modify ?f3 (joined true))
	(printout t "make L" crlf) 
)	
; ---------------------------------
;If the detect junctions flag is set, and there are no more un_joined edges,
;set the find_initial_boundary flag
(defrule done_detecting
	(declare (salience -10))
	?f1 <- (stage (value detect_junctions))
	=>
	(modify ?f1 (value find_initial_boundary))
	(printout t "detecting done" crlf) 	
 )
; -----------------------------------
;If the initial boundary junction is an L, then we know it's labelling
(defrule initial_boundary_junction_L
	?f1 <- (stage (value find_initial_boundary))
    (junction (type L) (base_point ?base_point) (p1 ?p1) (p2 ?p2))
	?f3 <- (edge (p1 ?base_point) (p2 ?p1))
	?f4 <- (edge (p1 ?base_point) (p2 ?p2))
    (not (junction (base_point ?bp&:(>  ?bp ?base_point))))
	=>
    (modify ?f3 (label B))
	(modify ?f4 (label B))
	(modify ?f1 (value find_second_boundary))
	(printout t "initial boundary junction L" crlf) 	
)
	
 
; --------------------------------------
;Ditto for an arrow
(defrule initial_boundary_junction_arrow
	?f1 <- (stage (value find_initial_boundary))
	(junction (type arrow) (base_point ?bp) (p1 ?p1) (p2 ?p2) (p3 ?p3))
	?f3 <- (edge (p1 ?bp) (p2 ?p1))
	?f4 <- (edge (p1 ?bp) (p2 ?p2))
	?f5 <- (edge (p1 ?bp) (p2 ?p3))
	(not (junction (base_point ?b &:(> ?b ?bp))))
	=>
	(modify ?f3 (label B))
	(modify ?f4 (label +))
	(modify ?f5 (label B))
	(modify ?f1 (value find_second_boundary))
	(printout t "initial boundary junction arrow" crlf) 	
 )
; --------------------------------------
;If we have already found the first boundary point, then find the second
;boundary point, and label it.
 
(defrule second_boundary_junction_L
	?f1 <- (stage (value find_second_boundary))
    (junction (type L) (base_point ?base_point) (p1 ?p1) (p2 ?p2))
	?f3 <- (edge (p1 ?base_point) (p2 ?p1))
	?f4 <- (edge (p1 ?base_point) (p2 ?p2))
    (not (junction (base_point ?bp&:(< ?bp ?base_point))))
	=>
    (modify ?f3 (label B))
	(modify ?f4 (label B))
	(modify ?f1 (value labeling))
	(printout t "second boundary junction L" crlf) 		
 )
; --------------------------------------
(defrule second_boundary_junction_arrow
	?f1 <- (stage (value find_second_boundary))
	(junction (type arrow) (base_point ?bp) (p1 ?p1) (p2 ?p2) (p3 ?p3))
	?f3 <- (edge (p1 ?bp) (p2 ?p1))
	?f4 <- (edge (p1 ?bp) (p2 ?p2))
	?f5 <- (edge (p1 ?bp) (p2 ?p3))
	(not (junction (base_point ?b&:(< ?b ?bp))))
	=>
	(modify ?f3 (label B))
	(modify ?f4 (label +))
	(modify ?f5 (label B))
	(modify ?f1 (value labeling))
	(printout t "second boundary junction arrow" crlf) 			
 )
 
; --------------------------------------
;If we have an edge whose label we already know definitely, then
;label the corresponding edge in the other direction
(defrule match_edge
	(stage (value labeling))
	?f2 <- (edge (p1 ?p1) (p2 ?p2) (label ?label& + | - | B ))
	?f3 <- (edge (p1 ?p2) (p2 ?p1) (label nil))
	=>
	(modify ?f2 (plotted t))
	(modify ?f3 (label ?label) (plotted t))
	(printout t "match edge  - Plot: " ?label " " ?p1 " " ?p2 crlf)
) 
;The following productions propogate the possible labellings of the edges
;based on the labellings of edges incident on adjacent junctions.  Since
;from the initial boundary productions, we have determined the labellings of
;of atleast two junctions, this propogation will label all of the junctions
;with the possible labellings.  The search space is pruned due to filtering,
;i.e.(not only label a junction in the ways physically possible based on the
;labellings of adjacent junctions.
 
 
; --------------------------------------
(defrule label_L
	(stage (value labeling))
	(junction (type L) (base_point ?p1))
	(edge (p1 ?p1) (p2 ?p2) (label + | - ))
	?f4 <- (edge (p1 ?p1) (p2 ~?p2) (label nil))
	=>
	(modify ?f4 (label B))
	(printout t "Label L" crlf) 		
)	
 
 
; --------------------------------------
(defrule label_tee_A
	(declare (salience 5))
	(stage (value labeling))
	(junction (type tee) (base_point ?bp) (p1 ?p1) (p2 ?p2) (p3 ?p3))
	?f3 <- (edge (p1 ?bp) (p2 ?p1) (label nil))
	?f4 <- (edge (p1 ?bp) (p2 ?p3))
	=>
	(modify ?f3 (label B))
	(modify ?f4 (label B))
	(printout t "Label tee A" crlf) 		
)		
 
 
; --------------------------------------
(defrule label_tee_B
	(stage (value labeling))
	(junction (type tee) (base_point ?bp) (p1 ?p1) (p2 ?p2) (p3 ?p3))
	?f3 <- (edge (p1 ?bp) (p2 ?p1))
	?f4 <- (edge (p1 ?bp) (p2 ?p3) (label nil))
	=>
	(modify ?f3 (label B))
	(modify ?f4 (label B))
	(printout t "Label tee B" crlf) 			
 )
 
; --------------------------------------
(defrule label_fork-1
	(stage (value labeling))
	(junction (type fork) (base_point ?bp))
	(edge (p1 ?bp) (p2 ?p1) (label +))
	?f4 <- (edge (p1 ?bp) (p2 ?p2&~?p1) (label nil))
	?f5 <- (edge (p1 ?bp) (p2 ~?p2 &~?p1))
	=>
	(modify ?f4 (label +))
	(modify ?f5 (label +))
	(printout t "Label fork 1" crlf) 		
)		
 
 
; --------------------------------------
(defrule label_fork-2
	(stage (value labeling))
	(junction (type fork) (base_point ?bp))
	(edge (p1 ?bp) (p2 ?p1) (label B))
	(edge (p1 ?bp) (p2 ?p2&~?p1) (label - ))
	?f5 <- (edge (p1 ?bp) (p2 ~?p2&~?p1) (label nil))
	=>
	(modify ?f5 (label B))
	(printout t "Label fork 2" crlf) 			
 )
 
; --------------------------------------
(defrule label_fork-3
	(stage (value labeling))
	(junction (type fork) (base_point ?bp))
	(edge (p1 ?bp) (p2 ?p1) (label B))
	(edge (p1 ?bp) (p2 ?p2&~?p1) (label B))
	?f5 <- (edge (p1 ?bp) (p2 ~?p2&~?p1) (label nil))
	=>
	(modify ?f5 (label -))
	(printout t "Label fork 3" crlf)
)	
 
 
; --------------------------------------
(defrule label_fork-4
	(stage (value labeling))
	(junction (type fork) (base_point ?bp))
	(edge (p1 ?bp) (p2 ?p1) (label -))
	(edge (p1 ?bp) (p2 ?p2&~?p1) (label -))
	?f5 <- (edge (p1 ?bp) (p2 ~?p2&~?p1) (label nil))
	=>
	(modify ?f5 (label -))
	(printout t "Label fork 4" crlf)	
)	
 
 
 
; --------------------------------------
(defrule label_arrow-1A
	(declare (salience 5))
	(stage (value labeling))
	(junction (type arrow) (base_point ?bp) (p1 ?p1) (p2 ?p2) (p3 ?p3))
	(edge (p1 ?bp) (p2 ?p1) (label ?label & B | - ))
	?f4 <- (edge (p1 ?bp) (p2 ?p2) (label nil))
	?f5 <- (edge (p1 ?bp) (p2 ?p3))
	=>
	(modify ?f4 (label +))
	(modify ?f5 (label ?label))
	(printout t "Label arrow 1A" crlf)	
)	 
; --------------------------------------
(defrule label_arrow-1B
	(stage (value labeling))
	(junction (type arrow) (base_point ?bp) (p1 ?p1) (p2 ?p2) (p3 ?p3))
	(edge (p1 ?bp) (p2 ?p1) (label ?label & B | - ))
	?f4 <- (edge (p1 ?bp) (p2 ?p2))
	?f5 <- (edge (p1 ?bp) (p2 ?p3) (label nil))
	=>
	(modify ?f4 (label +))
	(modify ?f5 (label ?label))	
	(printout t "Label arrow 1B" crlf)	
)	 
 
; --------------------------------------
(defrule label_arrow-2A
	(declare (salience 5))
	(stage (value labeling))
	(junction (type arrow) (base_point ?bp) (p1 ?p1) (p2 ?p2) (p3 ?p3))
	(edge (p1 ?bp) (p2 ?p3) (label ?label & B | - ))
	?f4 <- (edge (p1 ?bp) (p2 ?p2) (label nil))
	?f5 <- (edge (p1 ?bp) (p2 ?p1))
	=>
	(modify ?f4 (label +))
	(modify ?f5 (label ?label))
	(printout t "Label arrow 2A" crlf)	
)		
 
; --------------------------------------
(defrule label_arrow-2B
	(stage (value labeling))
	(junction (type arrow) (base_point ?bp) (p1 ?p1) (p2 ?p2) (p3 ?p3))
	(edge (p1 ?bp) (p2 ?p3) (label ?label & B | - ))
	?f4 <- (edge (p1 ?bp) (p2 ?p2))
	?f5 <- (edge (p1 ?bp) (p2 ?p1) (label nil))
	=>
	(modify ?f4 (label +))
	(modify ?f5 (label ?label))
	(printout t "Label arrow 2B" crlf)		
 )
 
; --------------------------------------
(defrule label_arrow-3A
	(declare (salience 5))
	(stage (value labeling))
	(junction (type arrow) (base_point ?bp) (p1 ?p1) (p2 ?p2) (p3 ?p3))
	(edge (p1 ?bp) (p2 ?p1) (label +))
	?f4 <- (edge (p1 ?bp) (p2 ?p2) (label nil))
	?f5 <- (edge (p1 ?bp) (p2 ?p3))
	=>
	(modify ?f4 (label -))
	(modify ?f5 (label +))
	(printout t "Label arrow 3A" crlf)	
)		
 
; --------------------------------------
(defrule label_arrow-3B
	(stage (value labeling))
	(junction (type arrow) (base_point ?bp) (p1 ?p1) (p2 ?p2) (p3 ?p3))
	(edge (p1 ?bp) (p2 ?p1) (label +))
	?f4 <- (edge (p1 ?bp) (p2 ?p2))
	?f5 <- (edge (p1 ?bp) (p2 ?p3) (label nil))
	=>
	(modify ?f4 (label -))
	(modify ?f5 (label +))
	(printout t "Label arrow 3B" crlf)	
)		
 
 
; --------------------------------------
(defrule label_arrow-4A
	(declare (salience 5))
	(stage (value labeling))
	(junction (type arrow) (base_point ?bp) (p1 ?p1) (p2 ?p2) (p3 ?p3))
	(edge (p1 ?bp) (p2 ?p3) (label +))
	?f4 <- (edge (p1 ?bp) (p2 ?p2) (label nil))
	?f5 <- (edge (p1 ?bp) (p2 ?p1))
	=>
	(modify ?f4 (label -))
	(modify ?f5 (label +))
	(printout t "Label arrow 4A" crlf)	
)		
 
; --------------------------------------
(defrule label_arrow-4B
	(stage (value labeling))
	(junction (type arrow) (base_point ?bp) (p1 ?p1) (p2 ?p2) (p3 ?p3))
	(edge (p1 ?bp) (p2 ?p3) (label +))
	?f4 <- (edge (p1 ?bp) (p2 ?p2))
	?f5 <- (edge (p1 ?bp) (p2 ?p1) (label nil))
	=>
	(modify ?f4 (label -))
	(modify ?f5 (label +))
	(printout t "Label arrow 4B" crlf)
)		
 
 
; --------------------------------------
(defrule label_arrow-5A
	(declare (salience 5))
	(stage (value labeling))
	(junction (type arrow) (base_point ?bp) (p1 ?p1) (p2 ?p2) (p3 ?p3))
	(edge (p1 ?bp) (p2 ?p2) (label -))
	?f4 <- (edge (p1 ?bp) (p2 ?p1))
	?f5 <- (edge (p1 ?bp) (p2 ?p3) (label nil))
	=>
	(modify ?f4 (label +))
	(modify ?f5 (label +))
	(printout t "Label arrow 5A" crlf)
)		
 
 
; --------------------------------------
(defrule label_arrow-5B
	(stage (value labeling))
	(junction (type arrow) (base_point ?bp) (p1 ?p1) (p2 ?p2) (p3 ?p3))
	(edge (p1 ?bp) (p2 ?p2) (label -))
	?f4 <- (edge (p1 ?bp) (p2 ?p1) (label nil))
	?f5 <- (edge (p1 ?bp) (p2 ?p3))
	=>
	(modify ?f4 (label +))
	(modify ?f5 (label +))
	(printout t "Label arrow 5B" crlf)	
)	
 
 
; --------------------------------------
; Originally, someone with CLIPS or UT made the following comment
; I don't think that we (KBSC) quite agree with this since Jess uses
; an MEA conflict resolution strategy by default.  However, in the interest
; of historical continuity we have left the original comment with the rule.
; 2 Sept 2006 jco
; --------------------------------------
;The conflict resolution mechanism will onle execute a production if no
;productions that are more complicated are satisfied.  This production is
;simple, so all of the above dictionary productions will fire before this
;change of state production
(defrule done_labeling
	(declare (salience -10))
	?f1 <- (stage (value labeling))
	=>
	(modify ?f1 (value plot_remaining_edges))
	(printout t "done labelling"  crlf)		
)
 
; --------------------------------------
;At this point, some labellings may have not been plotted, so plot them
(defrule plot_remaining
	(stage (value plot_remaining_edges))
	?f2 <- (edge (plotted nil) (label ?label&~nil) (p1 ?p1) (p2 ?p2))
	=>
	(printout t "Plot " ?label " " ?p1 " " ?p2 crlf)
	(modify ?f2 (plotted t)))
 
; --------------------------------------
; Again - the original author, probably Danny, left this comment in the rule.
; While it is a total kludge, it seems to have worked quite well for the past
; 20 years or so.  :-)

; --------------------------------------
;If we have been un able to label an edge, assume that it is a boundary.
;This is a total Kludge, but what the hell. (if we assume only valid drawings
;will be given for labeling, this assumption generally is true!)
(defrule plot_boundaries
	(stage (value plot_remaining_edges))
	?f2 <- (edge (plotted nil) (label nil) (p1 ?p1) (p2 ?p2))
	=>
	(printout t "Plot " B " " ?p1 " " ?p2 crlf)
	(modify ?f2 (plotted t)))
 
; --------------------------------------
;If there is no more work to do, then we are done and flag it.
(defrule done_plotting
	(declare (salience -10))
	?f1 <- (stage (value plot_remaining_edges))
	=>
	(modify ?f1 (value done))
	(printout t "done plotting" crlf)			
)	
 
; --------------------------------------
; Again, this is historical comment and does NOT describe what this rules does
; which is, well, nothing of value.  We'll probably change it before the year
; is up.  :-)  
; 2 Sept 2006 jco
; --------------------------------------
;Prompt the user as to where he can see a trace of the OPS5
;execution
(defrule done
	(stage (value done))
	=>
	)
