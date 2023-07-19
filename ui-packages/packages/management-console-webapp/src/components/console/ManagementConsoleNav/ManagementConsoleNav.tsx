/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import React from 'react';
import {
  Nav,
  NavItem,
  NavList
} from '@patternfly/react-core/dist/js/components/Nav';
import { Link } from 'react-router-dom';
import { ouiaAttribute } from '@kogito-apps/ouia-tools';

interface IOwnProps {
  pathname: string;
}

const ManagementConsoleNav: React.FC<IOwnProps> = ({ pathname }) => {
  return (
    <Nav aria-label="Nav" theme="dark" ouiaId="navigation-list">
      <NavList>
        <NavItem
          key={'process-instances-nav'}
          isActive={pathname === '/ProcessInstances'}
          ouiaId="process-instances"
        >
          <Link
            to="/ProcessInstances"
            {...ouiaAttribute('data-ouia-navigation-name', 'process-instances')}
          >
            Process Instances
          </Link>
        </NavItem>
        <NavItem
          key={'jobs-management-nav'}
          isActive={pathname === '/JobsManagement'}
          ouiaId="jobs-management"
        >
          <Link
            to="/JobsManagement"
            {...ouiaAttribute('data-ouia-navigation-name', 'jobs-management')}
          >
            Jobs Management
          </Link>
        </NavItem>
      </NavList>
    </Nav>
  );
};

export default ManagementConsoleNav;
