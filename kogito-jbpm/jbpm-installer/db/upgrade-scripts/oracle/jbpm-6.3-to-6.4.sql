create table TaskVariableImpl (
    id number(19,0) not null,
    modificationDate timestamp,
    name varchar2(255 char),
    processId varchar2(255 char),
    processInstanceId number(19,0),
    taskId number(19,0),
    type number(10,0),
    value long,
    primary key (id)
);
create sequence TASK_VAR_ID_SEQ;

