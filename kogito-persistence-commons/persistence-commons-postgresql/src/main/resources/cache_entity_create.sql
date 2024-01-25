
create table if not exists kogito_data_cache (
                                   key varchar(255) not null,
                                   name varchar(255) not null,
                                   json_value jsonb,
                                   primary key (key, name)
);
