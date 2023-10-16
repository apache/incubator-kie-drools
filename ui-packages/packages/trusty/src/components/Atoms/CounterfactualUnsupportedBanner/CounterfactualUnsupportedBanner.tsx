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
import React from 'react';
import './CounterfactualUnsupportedBanner.scss';
import { Alert } from '@patternfly/react-core';

const CounterfactualUnsupportedBanner = () => {
  return (
    <Alert
      className="cf-unsupported-banner"
      variant="info"
      isInline={true}
      title="Counterfactuals is an experimental feature and doesn't currently support all types of models."
    />
  );
};

export default CounterfactualUnsupportedBanner;
