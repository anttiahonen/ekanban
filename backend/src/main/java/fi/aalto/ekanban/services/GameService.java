package fi.aalto.ekanban.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.aalto.ekanban.enums.GameDifficulty;
import fi.aalto.ekanban.exceptions.GameHasEndedException;
import fi.aalto.ekanban.exceptions.GameNotFoundException;
import fi.aalto.ekanban.models.Turn;
import fi.aalto.ekanban.models.db.games.*;
import fi.aalto.ekanban.repositories.GameRepository;

@Service
public class GameService {

    private GameInitService gameInitService;
    private PlayerService playerService;
    private GameOptionService gameOptionService;
    private GameRepository gameRepository;

    @Autowired
    public GameService(GameInitService gameInitService,
                       PlayerService playerService,
                       GameOptionService gameOptionService,
                       GameRepository gameRepository) {
        this.gameInitService = gameInitService;
        this.playerService = playerService;
        this.gameOptionService = gameOptionService;
        this.gameRepository = gameRepository;
    }

    @Transactional
    public Game startGame(String playerName, GameDifficulty gameDifficulty) {
        Game newGame = gameInitService.getInitializedGame(gameDifficulty, playerName);
        Game createdGame = gameRepository.save(newGame);
        return createdGame;
    }

    public Game throwError() {
        throw new RuntimeException("exception");
    }

    @Transactional
    public Game playTurn(String gameId, Turn turn) {
        Game game = gameRepository.findOne(gameId);
        if (game == null) {
            throw new GameNotFoundException();
        }
        else if (game.getHasEnded()) {
            throw new GameHasEndedException();
        }
        Game playedGame = playerService.playTurn(game, turn);
        gameRepository.save(playedGame);
        return playedGame;
    }

}
