create table TaskVariableImpl (
    id numeric(19,0) identity not null,
    modificationDate datetime,
    name varchar(255),
    processId varchar(255),
    processInstanceId numeric(19,0),
    taskId numeric(19,0),
    type int,
    value varchar(5000),
    primary key (id)
);
