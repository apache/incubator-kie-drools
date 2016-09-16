
-- These indexes were added later to the 6.3 install DDL SQL and may possibly already be present on your system 

-- create index IDX_VInstLog_pInstId on VariableInstanceLog(processInstanceId);
-- go
-- create index IDX_VInstLog_varId on VariableInstanceLog(variableId);
-- go
-- create index IDX_VInstLog_pId on VariableInstanceLog(processId);
-- go

-- create index IDX_NInstLog_pInstId on NodeInstanceLog(processInstanceId);
-- go
-- create index IDX_NInstLog_nodeType on NodeInstanceLog(nodeType);
-- go
-- create index IDX_NInstLog_pId on NodeInstanceLog(processId);
-- go

