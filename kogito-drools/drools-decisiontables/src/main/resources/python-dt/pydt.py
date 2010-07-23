#this is PyDT - Python Decision Tables
# (c) 2007 Michael Neale (michael@michaelneale.net)
# Use entirely at your own risk !
# 
#  Copyright 2005 JBoss Inc
#  
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#  
#       http://www.apache.org/licenses/LICENSE-2.0
#  
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.



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




# Load a XLS into a decision table structure for processing
def load_xls(file_name) :
	import xlrd
	book = xlrd.open_workbook(file_name)
	sh = book.sheet_by_index(0)	
        condition_headers, action_headers, data = [],[],[]
	for rx in range(sh.nrows):
		if rx == 0 :		
			divider = 0
			for cx in range(sh.ncols):
				cv = sh.cell_value(rowx=rx, colx=cx)				
				if cv == "" : 
					continue
				if cv == "*" or cv == 'actions:' :
					divider = cx
				else:
					if divider == 0 : #we are in conditions
						condition_headers.append([cx, cv])
					else: #we are in actions
						action_headers.append([cx, cv])
		else:	
			data_row = {}
			#print condition_headers
			for cx in range(sh.ncols):
				cv = sh.cell_value(rowx=rx, colx=cx)
				if cv != "":
					data_row[cx] = cv
			if len(data_row) > 0 :
				data.append(data_row)
	return {
		"condition_headers" : condition_headers,
		"action_headers" : action_headers,
		"data" : data
		}





