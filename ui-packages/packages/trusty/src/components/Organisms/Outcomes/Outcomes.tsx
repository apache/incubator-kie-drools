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
import {
  Button,
  Card,
  CardBody,
  CardFooter,
  CardHeader,
  Gallery,
  GalleryItem,
  Label,
  Split,
  SplitItem,
  Title
} from '@patternfly/react-core';
import { v4 as uuid } from 'uuid';
import EvaluationStatus from '../../Atoms/EvaluationStatus/EvaluationStatus';
import FormattedValue from '../../Atoms/FormattedValue/FormattedValue';
import { LongArrowAltRightIcon } from '@patternfly/react-icons';
import { ItemObjectStructure, ItemObjectValue, Outcome } from '../../../types';
import './Outcomes.scss';
import {
  componentOuiaProps,
  OUIAProps
} from '@kogito-apps/ouia-tools/dist/utils/OuiaUtils';

type OutcomesProps =
  | {
      outcomes: Outcome[];
      listView: true;
      onExplanationClick: (outcomeId: string) => void;
    }
  | { outcomes: Outcome[]; listView?: false };

const Outcomes: React.FC<OutcomesProps & OUIAProps> = (
  props: OutcomesProps & OUIAProps
) => {
  const ouiaProps = componentOuiaProps(
    props.ouiaId,
    'outcomes',
    props.ouiaSafe
  );
  if (props.listView) {
    return (
      <section className="outcomes" {...ouiaProps}>
        {props.outcomes.length && (
          <Gallery className="outcome-cards" hasGutter>
            {props.outcomes.map((item) =>
              renderCard(item, props.onExplanationClick)
            )}
          </Gallery>
        )}
      </section>
    );
  }

  return (
    <section className="outcomes" {...ouiaProps}>
      {props.outcomes.map((item) => {
        if (
          item.outcomeResult !== null &&
          item.outcomeResult.kind === 'COLLECTION'
        ) {
          return (
            <Gallery className="outcome-cards" hasGutter key={uuid()}>
              {item.outcomeResult.value.map((value) => {
                return (
                  <GalleryItem key={uuid()}>
                    <LightCard className="outcome-cards__card" isHoverable>
                      <OutcomeSubList
                        key={item.outcomeName}
                        name={item.outcomeName}
                        value={value}
                      />
                    </LightCard>
                  </GalleryItem>
                );
              })}
            </Gallery>
          );
        }
        return (
          <LightCard key={uuid()}>
            {renderOutcome(item.outcomeName, item.outcomeResult, false, false)}
          </LightCard>
        );
      })}
    </section>
  );
};

export default Outcomes;

const renderCard = (
  outcome: Outcome,
  onExplanation: (outcomeId: string) => void
) => {
  if (outcome.evaluationStatus !== 'SUCCEEDED') {
    return (
      <GalleryItem key={uuid()}>
        <OutcomeCard outcome={outcome} onExplanation={onExplanation}>
          <span />
        </OutcomeCard>
      </GalleryItem>
    );
  }
  if (
    outcome.outcomeResult !== null &&
    outcome.outcomeResult.kind === 'COLLECTION'
  ) {
    return outcome.outcomeResult.value.map((value) => {
      return (
        <GalleryItem key={uuid()}>
          <OutcomeCard
            outcome={outcome}
            onExplanation={onExplanation}
            titleAsLabel
          >
            <OutcomeSubList
              key={outcome.outcomeName}
              name={outcome.outcomeName}
              value={value}
            />
          </OutcomeCard>
        </GalleryItem>
      );
    });
  }

  return (
    <GalleryItem key={uuid()}>
      <OutcomeCard outcome={outcome} onExplanation={onExplanation}>
        {renderOutcome(outcome.outcomeName, outcome.outcomeResult, true, true)}
      </OutcomeCard>
    </GalleryItem>
  );
};

type OutcomeCardProps = {
  children: React.ReactNode;
  outcome: Outcome;
  onExplanation: (outcomeId: string) => void;
  titleAsLabel?: boolean;
};

const OutcomeCard: React.FC<OutcomeCardProps> = ({
  children,
  outcome,
  onExplanation,
  titleAsLabel = false
}) => {
  return (
    /*
     * TODO: titleAsLabel is true when "components" is array.
     * Only way to recognize the right card is array index.
     */
    <Card
      className="outcome-cards__card outcome-cards__card--list-view"
      ouiaId={outcome.outcomeName}
      isHoverable
    >
      <CardHeader>
        {titleAsLabel ? (
          <Label
            className="outcome-cards__card__label"
            color="blue"
            {...componentOuiaProps('card-label', 'label', true)}
          >
            {outcome.outcomeName}
          </Label>
        ) : (
          <Title
            className="outcome-cards__card__title"
            headingLevel="h4"
            size="xl"
            {...componentOuiaProps('card-title', 'title', true)}
          >
            {outcome.outcomeName}
          </Title>
        )}
      </CardHeader>
      <CardBody>
        {outcome.evaluationStatus !== undefined &&
          outcome.evaluationStatus !== 'SUCCEEDED' && (
            <EvaluationStatus status={outcome.evaluationStatus} />
          )}
        {children}
      </CardBody>
      <CardFooter>
        {outcome.outcomeId && onExplanation && (
          <Button
            variant="link"
            isInline
            className="outcome-cards__card__explanation-link"
            onClick={() => {
              onExplanation(outcome.outcomeId);
            }}
            ouiaId="view-detail"
          >
            View details <LongArrowAltRightIcon />
          </Button>
        )}
      </CardFooter>
    </Card>
  );
};

const renderOutcome = (
  name: string,
  value: ItemObjectValue,
  compact = true,
  hidePropertyName = false
) => {
  const renderedItems: JSX.Element[] = [];

  if (value.kind === 'UNIT') {
    return (
      <OutcomeProperty
        key={name}
        name={name}
        value={value}
        hidePropertyName={hidePropertyName}
      />
    );
  }

  if (value.kind === 'STRUCTURE' || value.kind === 'COLLECTION') {
    if (value.kind === 'STRUCTURE') {
      renderedItems.push(
        <OutcomeComposed
          key={name}
          name={name}
          value={value}
          compact={compact}
        />
      );
    } else if (value.kind === 'COLLECTION') {
      value.value.forEach((item) => {
        renderedItems.push(<OutcomeSubList name={name} value={item} />);
      });
    }
  }

  return (
    <React.Fragment key={uuid()}>
      {renderedItems.map((item: JSX.Element) => item)}
    </React.Fragment>
  );
};

const OutcomeProperty = (props: {
  name: string;
  value: ItemObjectValue;
  hidePropertyName: boolean;
}) => {
  const { name, value, hidePropertyName } = props;
  const basicTypes = ['string', 'number', 'boolean'];
  const bigOutcome =
    hidePropertyName &&
    (basicTypes.includes(typeof value.value) || value.value === null);

  if (bigOutcome) {
    return (
      <div
        className="outcome__property__value--bigger"
        {...componentOuiaProps(name, 'simple-property-value', true)}
      >
        <FormattedValue value={value.value} />
      </div>
    );
  } else {
    return (
      <Split
        key={uuid()}
        className="outcome__property"
        {...componentOuiaProps(name, 'outcome-property', true)}
      >
        <SplitItem
          className="outcome__property__name"
          key="property-name"
          {...componentOuiaProps(name, 'property-name', true)}
        >
          {hidePropertyName ? 'Result' : name}:
        </SplitItem>
        <SplitItem
          className="outcome__property__value"
          key="property-value"
          {...componentOuiaProps(name, 'property-value', true)}
        >
          <FormattedValue value={value.value} />
        </SplitItem>
      </Split>
    );
  }
};

const OutcomeComposed = (props: {
  name: string;
  value: ItemObjectStructure;
  compact: boolean;
}) => {
  const { name, value, compact } = props;
  const renderItems: JSX.Element[] = [];

  Object.entries(value.value).forEach(([key, value]) => {
    renderItems.push(
      <div
        className="outcome-item"
        key={key}
        {...componentOuiaProps(name, 'outcome-subitem')}
      >
        {renderOutcome(key, value, compact)}
      </div>
    );
  });

  return (
    <>
      <div className="outcome__title outcome__title--struct" key={uuid()}>
        <span
          className="outcome__property__name"
          {...componentOuiaProps('subitem-title', 'title')}
        >
          {name}
        </span>
      </div>
      <div className="outcome outcome--struct" key={name}>
        {renderItems.map((item) => item)}
      </div>
    </>
  );
};

type OutcomeSubListProps = {
  name: string;
  value: ItemObjectValue;
};
const OutcomeSubList = (props: OutcomeSubListProps) => {
  const { name, value } = props;

  return (
    <>
      {value.kind === 'UNIT' && (
        <OutcomeProperty
          key={name}
          name={name}
          value={value}
          hidePropertyName={false}
        />
      )}
      {value.kind === 'STRUCTURE' &&
        Object.entries(value.value).map(([key, value]) => (
          <OutcomeProperty
            key={key}
            name={key}
            value={value}
            hidePropertyName={false}
          />
        ))}
    </>
  );
};

type LightCardProps = {
  children: React.ReactNode;
  className?: string;
  isHoverable?: boolean;
};

const LightCard: React.FC<LightCardProps & OUIAProps> = ({
  children,
  className,
  isHoverable,
  ouiaId,
  ouiaSafe
}) => {
  return (
    <Card
      className={className}
      isHoverable={isHoverable}
      {...componentOuiaProps(ouiaId, 'outcome-card', ouiaSafe)}
    >
      <CardBody>{children}</CardBody>
    </Card>
  );
};
