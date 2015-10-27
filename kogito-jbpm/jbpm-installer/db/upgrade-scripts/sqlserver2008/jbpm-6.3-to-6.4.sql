create table TaskVariableImpl (
    id bigint identity not null,
    modificationDate datetime2,
    name varchar(255),
    processId varchar(255),
    processInstanceId bigint,
    taskId bigint,
    type int,
    value varchar(255),
    primary key (id)
);
