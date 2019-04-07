create table TaskVariableImpl (
    id number(19,0) not null,
    modificationDate timestamp,
    name varchar2(255 char),
    processId varchar2(255 char),
    processInstanceId number(19,0),
    taskId number(19,0),
    type number(10,0),
    value varchar2(4000 char),
    primary key (id)
);
create sequence TASK_VAR_ID_SEQ;

create table QueryDefinitionStore (
    id number(19,0) not null,
    qExpression clob,
    qName varchar2(255 char),
    qSource varchar2(255 char),
    qTarget varchar2(255 char),
    primary key (id)
);

alter table QueryDefinitionStore 
        add constraint UK_4ry5gt77jvq0orfttsoghta2j unique (qName);
        
create sequence QUERY_DEF_ID_SEQ;

create index IDX_VInstLog_pInstId on VariableInstanceLog(processInstanceId);
create index IDX_VInstLog_varId on VariableInstanceLog(variableId);
create index IDX_VInstLog_pId on VariableInstanceLog(processId);

create index IDX_NInstLog_pInstId on NodeInstanceLog(processInstanceId);
create index IDX_NInstLog_nodeType on NodeInstanceLog(nodeType);
create index IDX_NInstLog_pId on NodeInstanceLog(processId);
