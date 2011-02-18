import pydt

test_fact = { "Age" : 42, "Risk" : "'HIGH'", "PolicyType" : "'COMPREHENSIVE'" }

test_table = {
    "condition_headers" : [ ["A" , "Age"], ["B", "Risk =="], ["C", "PolicyType =="]],
    "action_headers" : [ ["F","Premium"], ["G","Log"]],


    "data" : [
        {"row" : 2, "A" : "> 2", "B" : "'HIGH'", "C": "'COMPREHENSIVE'",  "F" : "245"},
        {"row" : 3, "A" : "< 25 ", "B" : "'LOW'", "F" : "390"}
        ]

}


#and now some crude test code
pydt.process_dt(test_fact, test_table)
print "RESULT: " + str(test_fact)
if not test_fact.has_key("Premium") :
    print("ERROR: no premium was calculated")
if  test_fact["Premium"] == '245' :
    print("PASSED STEP 1")
else :
    print("FAILED STEP 1: Premium was " + test_fact["Premium"])

#some simple test
tbl = pydt.load_xls("Example.xls")
if tbl['condition_headers'][0][1] == "Age" :
    print "PASSED STEP 2"
else:
    print "FAILED STEP 2"

#now test it all, end to end
test_fact = { "Age" : 42, "Risk" : "'HIGH'", "PolicyType" : "'COMPREHENSIVE'" }
pydt.process_dt(test_fact, tbl)
if not test_fact.has_key("Premium") :
    print("ERROR: no premium was calculated")
premium = test_fact["Premium"]
if premium == 245 :
    print("PASSED STEP 3")
else :
    print("FAILED STEP 3: Premium was " + test_fact["Premium"])
