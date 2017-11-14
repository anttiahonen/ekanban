import renderAppWithState from "./RenderAppWithState";
import { normalize } from 'normalizr';
import Game from '../../components/Game/Game';
import game from "../resources/initialData";
import { gameSchema } from '../../reducers/index';
import { setGameData } from '../../actions/index.js';

describe('App rendering', () => {
  describe('when game has not yet been started', () => {
    it('renders start screen', () => {
      const [, wrapper] = renderAppWithState({ game: null });

      const startScreenTitle = <h1>eKanban</h1>;
      expect(wrapper.contains(startScreenTitle)).toEqual(true);
    });
  });
  describe('when game has been started', () => {
    it('renders game', () => {
      const entities = normalize(game, gameSchema);
      const gameData = setGameData(entities).payload;

      const [, wrapper] = renderAppWithState({
        game: gameData.game,
        board: gameData.board,
        phases: gameData.phases,
        columns: gameData.columns,
        cards: gameData.cards,
        cardPhasePoints: gameData.phasePoints,
        wipLimits: gameData.wipLimits,
        cfdData: gameData.cfdData,
        cfdConfig: gameData.cfdConfig,
        backlogDeck: gameData.board.backlogDeck
      });

      expect(wrapper.find(Game)).toHaveLength(1);
    });
  });

});


