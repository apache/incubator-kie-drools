#this is PyDT - Python Decision Tables
# (c) 2007 Michael Neale (michael@michaelneale.net)  
# Use entirely at your own risk !
# Licenced under LGPL unless stated otherwise


fact = { "Age" : 42, "Risk" : "'HIGH'", "PolicyType" : "'COMPREHENSIVE'" }

table = {
    "condition_headers" : [ ["A" , "Age"], ["B", "Risk =="], ["C", "PolicyType =="]],
    "action_headers" : [ ["F","Premium"], ["G","Log"]],
    

    "data" : [ 
        {"row" : 2, "A" : "> 2", "B" : "'HIGH'", "C": "'COMPREHENSIVE'",  "F" : "245"},
        {"row" : 3, "A" : "< 25 ", "B" : "'LOW'", "F" : "390"}
        ]

}

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

    

"""
for row in table['data'] :
    #go through all the conditions, evaluating
    row_pass = True
    for condition in headers :
        col_index = condition[0]
        cell_value = row[col_index]
         
        predicate = str(condition[1]) + str(cell_value)
        
        if not eval(predicate) :
            #then failure due to negation
            row_pass = False
            break
        #if they all pass
        #then iterate through and apply the action (unless we finish on first match)
        #thats it !
    if row_pass :
        for action in table['action_headers'] :
            
            col_label = action[0]
            if (row.has_key(col_label)) :
                fact[action[1]] = row[col_label]
    



print str(fact)
"""


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

print "And the result is: " + str(fact)
