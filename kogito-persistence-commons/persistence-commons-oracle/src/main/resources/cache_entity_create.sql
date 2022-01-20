create table kogito_data_cache (
    key varchar(255) not null,
    name varchar(255) not null,
    json_value blob,
    CONSTRAINT kogito_data_cache_pk PRIMARY KEY(key),
    CONSTRAINT kogito_data_cache_json CHECK (json_value IS JSON)
);