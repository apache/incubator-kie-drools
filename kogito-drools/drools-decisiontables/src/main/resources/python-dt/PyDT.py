#this is PyDT - Python Decision Tables
# (c) 2007 Michael Neale (michael@michaelneale.net)
# Use entirely at your own risk !
# Licenced under LGPL unless stated otherwise


test_fact = { "Age" : 42, "Risk" : "'HIGH'", "PolicyType" : "'COMPREHENSIVE'" }

test_table = {
    "condition_headers" : [ ["A" , "Age"], ["B", "Risk =="], ["C", "PolicyType =="]],
    "action_headers" : [ ["F","Premium"], ["G","Log"]],


    "data" : [
        {"row" : 2, "A" : "> 2", "B" : "'HIGH'", "C": "'COMPREHENSIVE'",  "F" : "245"},
        {"row" : 3, "A" : "< 25 ", "B" : "'LOW'", "F" : "390"}
        ]

}

#this is the actual "engine" if you can call it that.
def process_dt(fact, table) :
	def make_header(hdr) :
	    splut = hdr[1].split(' ')
	    if len(splut) > 1 :
	    #if hdr[1].contains(' ') :
	        #itms = hdr[1].split(' ')
	        return [hdr[0], fact[splut[0]] + ' ' + splut[1]]
	    else :
	        return [hdr[0], fact[hdr[1]]]
	#calc the headers
	headers = map(make_header, table['condition_headers'])
	#lets try a map based approach
	def eval_table(row) :
	    #go through all the conditions, evaluating
	    def check_condition(condition) :
	    #for condition in headers :
	        col_index = condition[0]
	        if not row.has_key(col_index) :
	            return False
	        cell_value = row[col_index]
	        predicate = str(condition[1]) + str(cell_value)
	        return not eval(predicate)
	    size = len(filter(check_condition,headers))
	    if size == 0 :
	        #for action in table['action_headers'] :
	        def apply_actions(action) :
	            col_label = action[0]
	            if (row.has_key(col_label)) :
	                fact[action[1]] = row[col_label]
	        map(apply_actions, table['action_headers'])
	map(eval_table, table['data'])

#and now some crude test code
process_dt(test_fact, test_table)
print "RESULT: " + str(test_fact)
if not test_fact.has_key("Premium") :
	print("ERROR: no premium was calculated")
premium = test_fact["Premium"]
if premium == '245' :
	print("PASSED")
else :
	print("FAILED: Premium was " + test_fact["Premium"])

