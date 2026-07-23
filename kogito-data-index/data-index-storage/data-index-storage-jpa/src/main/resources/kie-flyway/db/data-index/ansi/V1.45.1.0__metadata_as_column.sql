/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

ALTER TABLE definitions ADD COLUMN metadata varchar(max);

/* Migration for postgresql and sql server */
/*
UPDATE definitions SET metadata = v
FROM  (SELECT process_id, process_version, string_agg(name || ':' || meta_value, ',') as v FROM definitions_metadata group by process_id, process_version) as grouped_metadata
WHERE id = grouped_metadata.process_id and version = grouped_metadata.process_version;
*/


/* Migration for h2 and oracle*/
/*
UPDATE definitions SET metadata = v
FROM  (SELECT process_id, process_version, listagg(name || ':' || meta_value, ',') as v FROM definitions_metadata group by process_id, process_version) as grouped_metadata
WHERE id = grouped_metadata.process_id and version = grouped_metadata.process_version;
*/


/* Since migration is disable, do not delete not longer used table*/
/* DROP TABLE definitions_metadata;*/
