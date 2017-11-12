import React, { PropTypes } from 'react';
import { connect } from 'react-redux';
import RaisedButton from 'material-ui/RaisedButton';
import { playTurn, setActiveDice, setActiveCard, assignResource, assignDie, removeAssignedDice } from '../../actions';
import Board from '../Board/Board';
import BacklogDeckArea from '../BacklogDeckArea/BacklogDeckArea';
import CFD from '../CFD/CFD';
import Message from '../Message/Message';
import constants from '../../constants';
import './Game.scss';

export class Game extends React.Component {
  static propTypes = {
    game: PropTypes.shape({
      playerName: PropTypes.string.isRequired,
      board: PropTypes.string.isRequired
    }).isRequired
  };

  constructor({ game }) {
    super();
    this.playerName = game.playerName.length > 16 ? game.playerName.substring(0,16)+"..." : game.playerName;
  };

  handleNextRoundClick() {
    let assignResourcesActions = [];
    if (this.props.selectedDice && this.props.selectedCard) {
      const diePhaseId = this.props.selectedDice.id;
      const diceIndex = this.props.selectedDice.diceIndex;
      const xMovement = this.props.selectedCard.cardPageX - this.props.selectedDice.diePageX;
      const yMovement = this.props.selectedCard.cardPageY - this.props.selectedDice.diePageY;
      const cardPhaseId = this.props.selectedCard.phaseId;
      const cardId = this.props.selectedCard.id;
      this.props.onCreateAssignedDie(diePhaseId, diceIndex, xMovement, yMovement);
      this.props.onResetSelectedDieAndCard();
      assignResourcesActions = this.props.assignedResources.concat([{
        cardId: cardId,
        cardPhaseId: cardPhaseId,
        diePhaseId: diePhaseId,
        dieIndex: diceIndex,
        points: 0
      }]);
    }
    this.props.onPlayTurn(this.props.game.id, this.props.wipLimits, assignResourcesActions);
  }

  handleResetDiceClick() {
    this.props.onResetSelectedDieAndCard();
    this.props.onResetAssignedDice();
  }

  get buttonStyles() {
    const baseStyle = {
      height: 50,
      marginTop: 10
    };
    const nextRoundMarginLeft = this.props.game.difficultyLevel === constants.GAME_DIFFICULTY_NORMAL ? "auto" : "2%";
    return {
      nextRound: {
        ...baseStyle,
        marginLeft: nextRoundMarginLeft
      },
      resetDice: {
        ...baseStyle,
        marginLeft: "auto"
      }
    }
  }

  get gameEndedMessage() {
    const linkStyle = {
      marginTop: 10,
      height: 40
    };
    let messageElements = [];
    messageElements.push(<div key={1}>{constants.GAME_ENDED_MESSAGE}</div>);
    messageElements.push(<RaisedButton key={2} label={constants.START_OVER_MESSAGE} primary={true} style={linkStyle} href="/" />);
    return messageElements;
  }

  render() {
    return (
      <div className="game">
        <div className="game-toolbar">
          <div className="game-details">
            <div className="game-info player-name">Player Name: {this.playerName}</div>
            <div className="game-info day">Current Day: {this.props.game.currentDay}</div>
          </div>
          { this.props.game.difficultyLevel !== constants.GAME_DIFFICULTY_NORMAL &&
            <RaisedButton
              label="Reset dice"
              disabled={this.props.gameHasEnded || !this.props.enableNextRound}
              style={this.buttonStyles.resetDice}
              onClick={() => {if (this.props.enableNextRound) this.handleResetDiceClick(); }}
            />
          }
          <RaisedButton
            label="Next Round"
            secondary={this.props.enableNextRound}
            disabled={this.props.gameHasEnded || !this.props.enableNextRound}
            style={this.buttonStyles.nextRound}
            onClick={() => {if (this.props.enableNextRound) this.handleNextRoundClick(); }}
          />
        </div>
        <BacklogDeckArea />
        <Board  />
        {this.props.gameHasEnded && <Message message={this.gameEndedMessage} />}
        <CFD  />
      </div>
    )
  }

}

const mapStateToProps = (state) => {
  return {
    game: state.game,
    wipLimits: state.wipLimits,
    assignedResources: state.assignResources,
    enableNextRound: state.nextRoundUIState.enableButtonPress,
    selectedDice: state.phaseDice,
    selectedCard: state.activeCard,
    gameHasEnded: state.game.hasEnded
  }
};

const mapDispatchToProps = (dispatch) => {
  return {
    onPlayTurn: (gameId, wipLimits, assignResourcesActions) => {
      dispatch(playTurn(gameId, wipLimits, assignResourcesActions))
    },
    onResetSelectedDieAndCard: () => {
      dispatch(setActiveDice(null));
      dispatch(setActiveCard(null));
    },
    onCreateAssignResource: (cardId, diePhaseId, dieIndex) => {
      dispatch(assignResource(cardId, diePhaseId, dieIndex))
    },
    onCreateAssignedDie: (phaseId, diceIndex, xMovement, yMovement) => {
      dispatch(assignDie(phaseId, diceIndex, xMovement, yMovement))
    },
    onResetAssignedDice: () => {
      dispatch(removeAssignedDice())
    }
  }
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Game);

