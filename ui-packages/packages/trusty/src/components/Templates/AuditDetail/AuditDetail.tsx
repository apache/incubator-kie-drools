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
import React, { ReactNode, useContext, useEffect, useState } from 'react';
import {
  Nav,
  NavItem,
  NavList,
  PageSection,
  PageSectionVariants,
  Stack,
  StackItem,
  Tooltip
} from '@patternfly/react-core';
import {
  Redirect,
  Route,
  Switch,
  useLocation,
  useParams,
  useRouteMatch
} from 'react-router-dom';
import { ExecutionRouteParams, RemoteDataStatus } from '../../../types';
import SkeletonFlexStripes from '../../Molecules/SkeletonFlexStripes/SkeletonFlexStripes';
import useExecutionInfo from './useExecutionInfo';
import ExecutionHeader from '../../Organisms/ExecutionHeader/ExecutionHeader';
import SkeletonStripe from '../../Atoms/SkeletonStripe/SkeletonStripe';
import SkeletonCards from '../../Molecules/SkeletonCards/SkeletonCards';
import ExecutionDetail from '../ExecutionDetail/ExecutionDetail';
import useDecisionOutcomes from './useDecisionOutcomes';
import OutcomeDetails from '../OutcomeDetails/OutcomeDetails';
import InputData from '../InputData/InputData';
import ModelLookup from '../ModelLookup/ModelLookup';
import Counterfactual from '../Counterfactual/Counterfactual';
import { OutlinedQuestionCircleIcon } from '@patternfly/react-icons';
import CounterfactualUnsupportedBanner from '../../Atoms/CounterfactualUnsupportedBanner/CounterfactualUnsupportedBanner';
import NotFound from '../NotFound/NotFound';
import TrustyLink from '../../Atoms/TrustyLink/TrustyLink';
import { TrustyContext } from '../TrustyApp/TrustyApp';
import './AuditDetail.scss';

const AuditDetail = () => {
  const { path, url } = useRouteMatch();
  const location = useLocation();
  const { executionId } = useParams<ExecutionRouteParams>();
  const execution = useExecutionInfo(executionId);
  const outcomes = useDecisionOutcomes(executionId);
  const { config } = useContext(TrustyContext);

  const [thirdLevelNav, setThirdLevelNav] = useState<
    { url: string; desc: string; icon?: ReactNode }[]
  >([]);

  useEffect(() => {
    if (outcomes.status === RemoteDataStatus.SUCCESS) {
      const newNav = [];
      if (outcomes.data.length === 1) {
        newNav.push({
          url: `/single-outcome`,
          desc: 'Outcome'
        });
      } else {
        newNav.push({ url: '/outcomes', desc: 'Outcomes' });
        newNav.push({ url: '/outcomes-details', desc: 'Outcomes details' });
      }
      newNav.push({ url: '/input-data', desc: 'Input data' });
      newNav.push({ url: '/model-lookup', desc: 'Model lookup' });
      if (config.counterfactualEnabled) {
        newNav.push({
          url: '/counterfactual-analysis',
          desc: 'Counterfactual analysis',
          icon: (
            <Tooltip
              position="top"
              content={
                <div>
                  Counterfactuals is an experimental feature and doesn&apos;t
                  currently support all types of models.
                </div>
              }
            >
              <OutlinedQuestionCircleIcon />
            </Tooltip>
          )
        });
      }
      setThirdLevelNav(newNav);
    }
  }, [outcomes, config.counterfactualEnabled]);

  return (
    <>
      <PageSection variant={PageSectionVariants.light}>
        <ExecutionHeader execution={execution} />
        {thirdLevelNav.length === 0 && (
          <div className="audit-detail__nav">
            <SkeletonFlexStripes
              stripesNumber={4}
              stripesHeight={'1.5em'}
              stripesWidth={'120px'}
              isPadded={false}
            />
          </div>
        )}
        {thirdLevelNav.length > 0 && (
          <>
            {config.counterfactualEnabled && (
              <CounterfactualUnsupportedBanner />
            )}
            <Nav
              className="audit-detail__nav"
              variant="tertiary"
              ouiaId="nav-audit-detail"
            >
              <NavList>
                {thirdLevelNav.map((item, index) => (
                  <NavItem
                    key={`sub-nav-${index}`}
                    isActive={location.pathname === url + item.url}
                    ouiaId={item.url.substr(1)}
                  >
                    <TrustyLink url={url + item.url}>
                      <>
                        {item.desc}
                        {item.icon && (
                          <span className={'audit-detail__nav__badge'}>
                            {item.icon}
                          </span>
                        )}
                      </>
                    </TrustyLink>
                  </NavItem>
                ))}
              </NavList>
            </Nav>
          </>
        )}
      </PageSection>

      <Switch>
        <Route path={`${path}/single-outcome`}>
          <OutcomeDetails outcomes={outcomes} />
        </Route>
        <Route path={`${path}/outcomes`}>
          <ExecutionDetail outcomes={outcomes} />
        </Route>
        <Route path={`${path}/outcomes-details`}>
          <OutcomeDetails outcomes={outcomes} />
        </Route>
        <Route path={`${path}/input-data`}>
          <InputData />
        </Route>
        <Route path={`${path}/model-lookup`}>
          <ModelLookup />
        </Route>
        {config.counterfactualEnabled && (
          <Route path={`${path}/counterfactual-analysis`}>
            <Counterfactual />
          </Route>
        )}
        <Route exact path={`${path}/`}>
          {execution.status === RemoteDataStatus.SUCCESS &&
            outcomes.status === RemoteDataStatus.SUCCESS && (
              <>
                {outcomes.data.length === 1 && (
                  <Redirect
                    exact
                    from={path}
                    to={`${location.pathname}/single-outcome?outcomeId=${outcomes.data[0].outcomeId}`}
                  />
                )}
                {outcomes.data.length > 1 && (
                  <Redirect
                    exact
                    from={path}
                    to={`${location.pathname}/outcomes`}
                  />
                )}
              </>
            )}
          {outcomes.status === RemoteDataStatus.LOADING && (
            <PageSection>
              <Stack hasGutter>
                <StackItem>
                  <SkeletonStripe
                    isInline={true}
                    customStyle={{ height: '1.5em' }}
                  />
                </StackItem>
                <StackItem>
                  <SkeletonCards quantity={2} />
                </StackItem>
              </Stack>
            </PageSection>
          )}
        </Route>
        <Route path="/not-found" component={NotFound} />
        <Redirect to="/not-found" />
      </Switch>
    </>
  );
};

export default AuditDetail;
