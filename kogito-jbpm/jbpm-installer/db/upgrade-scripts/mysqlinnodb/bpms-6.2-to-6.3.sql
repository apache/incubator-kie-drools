create table TaskVariableImpl (
    id bigint not null auto_increment,
    modificationDate datetime,
    name varchar(255),
    processId varchar(255),
    processInstanceId bigint,
    taskId bigint,
    type integer,
    value varchar(5000),
    primary key (id)
) ENGINE=InnoDB;
