import React, { PropTypes } from 'react';
import { connect } from 'react-redux';
import Card from '../Card/Card';
import "./BacklogDeck.scss";

const BacklogDeck = ({ topMostBacklogCardId, backlogHasCards, topMostCardOrderNumber, cardCount }) => {
  return <div className="backlog">
      { backlogHasCards && <Card key={topMostBacklogCardId} id={topMostBacklogCardId} /> }
      { !backlogHasCards && <div className="empty">&nbsp;</div> }
      <span className="card-situation">{topMostCardOrderNumber}/{cardCount}</span>
    </div>
};

BacklogDeck.propTypes = {
  topMostBacklogCardId: PropTypes.string
};

const mapStateToProps = (state) => {
  const topMostCardId = state.backlogDeck.length > 0 ? state.backlogDeck[0] : null;
  const cardCount = Object.keys(state.cards).length;
  const topMostCardOrderNumber = topMostCardId != null ? state.cards[topMostCardId].orderNumber : cardCount;
  const backlogHasCards = state.backlogDeck.length > 0;
  return {
    topMostBacklogCardId: topMostCardId,
    backlogHasCards: backlogHasCards,
    topMostCardOrderNumber: topMostCardOrderNumber,
    cardCount: cardCount
  }
};

export default connect(
  mapStateToProps
)(BacklogDeck);
