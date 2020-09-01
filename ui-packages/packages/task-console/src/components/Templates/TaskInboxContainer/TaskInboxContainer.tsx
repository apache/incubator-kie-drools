/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React, { useEffect } from 'react';
import { Card, Grid, GridItem, PageSection } from '@patternfly/react-core';
import { ouiaPageTypeAndObjectId, OUIAProps } from '@kogito-apps/common';
import UserTaskPageHeader from '../../Molecules/UserTaskPageHeader/UserTaskPageHeader';
import TaskInbox from '../../Organisms/TaskInbox/TaskInbox';

const TaskInboxContainer: React.FC<OUIAProps> = ({ ouiaId, ouiaSafe }) => {
  useEffect(() => {
    return ouiaPageTypeAndObjectId('task-inbox');
  });

  return (
    <React.Fragment>
      <UserTaskPageHeader />
      <PageSection>
        <Grid hasGutter md={1}>
          <GridItem span={12}>
            <Card>
              <TaskInbox />
            </Card>
          </GridItem>
        </Grid>
      </PageSection>
    </React.Fragment>
  );
};

export default TaskInboxContainer;
