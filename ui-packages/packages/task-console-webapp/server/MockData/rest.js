/**
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
module.exports = restData = {
  process: [
    {
      processId: 'travels',
      instances: [
        {
          processInstanceId: '9ae7ce3b-d49c-4f35-b843-8ac3d22fa427',
          tasks: [
            {
              referenceName: 'VisaApplication',
              taskId: '45a73767-5da3-49bf-9c40-d533c3e77ef3',
              complete: 'success',
              message: 'success'
            }
          ]
        },
        {
          processInstanceId: '9ae407dd-cdfa-4722-8a49-0a6d2e14550d',
          tasks: [
            {
              referenceName: 'ConfirmTravel',
              taskId: '047ec38d-5d57-4330-8c8d-9bd67b53a529',
              complete: 'failed',
              message: 'failed'
            }
          ]
        }
      ]
    }
  ]
};
