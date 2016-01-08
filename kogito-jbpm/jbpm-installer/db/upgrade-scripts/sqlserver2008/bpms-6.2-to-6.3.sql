create table TaskVariableImpl (
    id bigint identity not null,
    modificationDate datetime2,
    name varchar(255),
    processId varchar(255),
    processInstanceId bigint,
    taskId bigint,
    type int,
    value varchar(5000),
    primary key (id)
);

create table QueryDefinitionStore (
    id bigint identity not null,
    qExpression varchar(MAX),
    qName varchar(255),
    qSource varchar(255),
    qTarget varchar(255),
    primary key (id)
);

alter table QueryDefinitionStore 
        add constraint UK_4ry5gt77jvq0orfttsoghta2j unique (qName);