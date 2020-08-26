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
import {
  Outcome,
  ItemObject,
  isItemObjectArray,
  isItemObjectMultiArray
} from '../../../types';
import './Outcomes.scss';

type OutcomesProps =
  | {
      outcomes: Outcome[];
      listView: true;
      onExplanationClick: (outcomeId: string) => void;
    }
  | { outcomes: Outcome[]; listView?: false };

const Outcomes = (props: OutcomesProps) => {
  if (props.listView) {
    return (
      <section className="outcomes">
        {props.outcomes.length && (
          <Gallery className="outcome-cards" hasGutter>
            {props.outcomes.map(item =>
              renderCard(item, props.onExplanationClick)
            )}
          </Gallery>
        )}
      </section>
    );
  }

  return (
    <section className="outcomes">
      {props.outcomes.map(item => {
        if (
          item.outcomeResult !== null &&
          item.outcomeResult.components !== null &&
          isItemObjectMultiArray(item.outcomeResult.components)
        ) {
          return (
            <Gallery className="outcome-cards" hasGutter key={uuid()}>
              {item.outcomeResult.components.map(subList => {
                return (
                  <GalleryItem key={uuid()}>
                    <LightCard className="outcome-cards__card" isHoverable>
                      <OutcomeSubList subListItem={subList} />
                    </LightCard>
                  </GalleryItem>
                );
              })}
            </Gallery>
          );
        }
        return (
          <LightCard key={uuid()}>
            {renderOutcome(item.outcomeResult, item.outcomeName, false, false)}
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
    outcome.outcomeResult.components !== null &&
    isItemObjectMultiArray(outcome.outcomeResult.components)
  ) {
    return outcome.outcomeResult.components.map(item => (
      <GalleryItem key={uuid()}>
        <OutcomeCard
          outcome={outcome}
          onExplanation={onExplanation}
          titleAsLabel
        >
          <OutcomeSubList subListItem={item} />
        </OutcomeCard>
      </GalleryItem>
    ));
  }

  return (
    <GalleryItem key={uuid()}>
      <OutcomeCard outcome={outcome} onExplanation={onExplanation}>
        {renderOutcome(outcome.outcomeResult, outcome.outcomeName, true, true)}
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

const OutcomeCard = (props: OutcomeCardProps) => {
  const { children, outcome, onExplanation, titleAsLabel = false } = props;
  return (
    <Card
      className="outcome-cards__card outcome-cards__card--list-view"
      isHoverable
    >
      <CardHeader>
        {titleAsLabel ? (
          <Label className="outcome-cards__card__label" color="blue">
            {outcome.outcomeName}
          </Label>
        ) : (
          <Title
            className="outcome-cards__card__title"
            headingLevel="h4"
            size="xl"
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
          >
            View Details <LongArrowAltRightIcon />
          </Button>
        )}
      </CardFooter>
    </Card>
  );
};

const renderOutcome = (
  outcomeData: ItemObject,
  name: string,
  compact = true,
  hidePropertyName = false
) => {
  const renderItems: JSX.Element[] = [];

  if (outcomeData.value !== null) {
    return (
      <OutcomeProperty
        property={outcomeData}
        key={outcomeData.name}
        hidePropertyName={hidePropertyName}
      />
    );
  }
  if (outcomeData.components.length) {
    if (isItemObjectArray(outcomeData.components)) {
      renderItems.push(
        <OutcomeComposed
          outcome={outcomeData}
          key={outcomeData.name}
          compact={compact}
          name={name}
        />
      );
    } else if (isItemObjectMultiArray(outcomeData.components)) {
      outcomeData.components.forEach(item => {
        renderItems.push(<OutcomeSubList subListItem={item} />);
      });
    }
  }

  return (
    <React.Fragment key={uuid()}>
      {renderItems.map((item: JSX.Element) => item)}
    </React.Fragment>
  );
};

const OutcomeProperty = (props: {
  property: ItemObject;
  hidePropertyName: boolean;
}) => {
  const { property, hidePropertyName } = props;
  const basicTypes = ['string', 'number', 'boolean'];
  const bigOutcome =
    hidePropertyName && basicTypes.includes(typeof property.value);

  if (bigOutcome) {
    return (
      <div className="outcome__property__value--bigger">
        <FormattedValue value={property.value} />
      </div>
    );
  } else {
    return (
      <Split key={uuid()} className="outcome__property">
        <SplitItem className="outcome__property__name" key="property-name">
          {hidePropertyName ? 'Result' : property.name}:
        </SplitItem>
        <SplitItem className="outcome__property__value" key="property-value">
          <FormattedValue value={property.value} />
        </SplitItem>
      </Split>
    );
  }
};

const OutcomeComposed = (props: {
  outcome: ItemObject;
  compact: boolean;
  name: string;
}) => {
  const { outcome, compact, name } = props;
  const renderItems: JSX.Element[] = [];

  for (const subItem of outcome.components as ItemObject[]) {
    renderItems.push(
      <div className="outcome-item" key={subItem.name}>
        {renderOutcome(subItem, name, compact)}
      </div>
    );
  }
  return (
    <>
      <div className="outcome__title outcome__title--struct" key={uuid()}>
        <span className="outcome__property__name">{outcome.name}</span>
      </div>
      <div className="outcome outcome--struct" key={outcome.name}>
        {renderItems.map(item => item)}
      </div>
    </>
  );
};

type OutcomeSubListProps = {
  subListItem: ItemObject[];
};
const OutcomeSubList = (props: OutcomeSubListProps) => {
  const { subListItem } = props;

  return (
    <>
      {subListItem &&
        subListItem.map(item => (
          <Split key={uuid()} className="outcome__property">
            <SplitItem className="outcome__property__name" key="property-name">
              {item.name}:
            </SplitItem>
            <SplitItem
              className="outcome__property__value"
              key="property-value"
            >
              <FormattedValue value={item.value} />
            </SplitItem>
          </Split>
        ))}
    </>
  );
};

type LightCardProps = {
  children: React.ReactNode;
  className?: string;
  isHoverable?: boolean;
};

const LightCard = (props: LightCardProps) => {
  const { children, className = '', isHoverable = false } = props;
  return (
    <Card className={className} isHoverable={isHoverable}>
      <CardBody>{children}</CardBody>
    </Card>
  );
};
