create table TaskVariableImpl (
    id numeric(19,0) identity not null,
    modificationDate datetime null,
    name varchar(255) null,
    processId varchar(255) null,
    processInstanceId numeric(19,0) null,
    taskId numeric(19,0) null,
    type int null,
    value varchar(255) null,
    primary key (id)
) lock datarows
