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

create table QueryDefinitionStore (
    id numeric(19,0) identity not null,
    qExpression text,
    qName varchar(255),
    qSource varchar(255),
    qTarget varchar(255),
    primary key (id)
);

alter table QueryDefinitionStore 
        add constraint UK_4ry5gt77jvq0orfttsoghta2j unique (qName);
        