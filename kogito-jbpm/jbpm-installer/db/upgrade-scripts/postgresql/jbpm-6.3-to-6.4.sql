create table TaskVariableImpl (
    id int8 not null,
    modificationDate timestamp,
    name varchar(255),
    processId varchar(255),
    processInstanceId int8,
    taskId int8,
    type int4,
    value varchar(5000),
    primary key (id)
);
create sequence TASK_VAR_ID_SEQ;

