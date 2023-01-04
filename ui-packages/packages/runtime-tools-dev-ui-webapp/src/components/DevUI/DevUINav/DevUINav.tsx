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
import { Nav, NavItem, NavList } from '@patternfly/react-core';
import { Link } from 'react-router-dom';
import { ouiaAttribute } from '@kogito-apps/ouia-tools';
import { useDevUIAppContext } from '../../contexts/DevUIAppContext';

interface IOwnProps {
  pathname: string;
}

const DevUINav: React.FC<IOwnProps> = ({ pathname }) => {
  const { isProcessEnabled, isTracingEnabled, customLabels, availablePages } =
    useDevUIAppContext();

  return (
    <Nav aria-label="Nav" theme="dark">
      <NavList>
        {isProcessEnabled && (
          <>
            {(!availablePages || availablePages.includes('Processes')) && (
              <NavItem
                key={'processes-nav'}
                isActive={pathname === '/Processes'}
              >
                <Link
                  to="/Processes"
                  {...ouiaAttribute(
                    'data-ouia-navigation-name',
                    'processes-nav'
                  )}
                >
                  {customLabels.pluralProcessLabel}
                </Link>
              </NavItem>
            )}
            {(!availablePages || availablePages.includes('Jobs')) && (
              <NavItem
                key={'jobs-management-nav'}
                isActive={pathname === '/JobsManagement'}
              >
                <Link
                  to="/JobsManagement"
                  {...ouiaAttribute(
                    'data-ouia-navigation-name',
                    'jobs-management-nav'
                  )}
                >
                  Jobs
                </Link>
              </NavItem>
            )}
            {(!availablePages || availablePages.includes('Tasks')) && (
              <NavItem
                key={'task-inbox-nav'}
                isActive={pathname === '/TaskInbox'}
              >
                <Link
                  to="/TaskInbox"
                  {...ouiaAttribute(
                    'data-ouia-navigation-name',
                    'task-inbox-nav'
                  )}
                >
                  Tasks
                </Link>
              </NavItem>
            )}
            {(!availablePages || availablePages.includes('Forms')) && (
              <NavItem key={'forms-list-nav'} isActive={pathname === '/Forms'}>
                <Link
                  to="/Forms"
                  {...ouiaAttribute(
                    'data-ouia-navigation-name',
                    'forms-list-nav'
                  )}
                >
                  Forms
                </Link>
              </NavItem>
            )}
            {(!availablePages || availablePages.includes('Monitoring')) && (
              <NavItem
                key={'monitoring-nav'}
                isActive={pathname.startsWith('/Monitoring')}
              >
                <Link
                  to="/Monitoring"
                  {...ouiaAttribute(
                    'data-ouia-navigation-name',
                    'monitoring-nav'
                  )}
                >
                  Monitoring
                </Link>
              </NavItem>
            )}
            {(!availablePages ||
              availablePages.includes('CustomDashboard')) && (
              <NavItem
                key={'customDashboard-nav'}
                isActive={pathname.startsWith('/CustomDashboard')}
              >
                <Link
                  to="/CustomDashboard"
                  {...ouiaAttribute(
                    'data-ouia-navigation-name',
                    'customDashboard-nav'
                  )}
                >
                  Dashboards
                </Link>
              </NavItem>
            )}
          </>
        )}
        {isTracingEnabled && (
          <NavItem key={'audit-nav'} isActive={pathname.startsWith('/Audit')}>
            <Link
              to="/Audit"
              {...ouiaAttribute('data-ouia-navigation-name', 'audit-nav')}
            >
              Audit investigation
            </Link>
          </NavItem>
        )}
      </NavList>
    </Nav>
  );
};

export default DevUINav;
