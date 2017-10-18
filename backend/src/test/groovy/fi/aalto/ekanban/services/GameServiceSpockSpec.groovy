package fi.aalto.ekanban.services

import spock.lang.Shared
import spock.lang.Specification

import fi.aalto.ekanban.builders.GameBuilder
import fi.aalto.ekanban.enums.GameDifficulty
import fi.aalto.ekanban.repositories.GameRepository
import spock.lang.Unroll


class GameServiceSpockSpec extends Specification {

    @Shared GameService gameService
    @Shared GameInitService gameInitService
    @Shared GameRepository gameRepository

    def setup() {
        gameInitService = Mock(GameInitService)
        gameRepository = Mock(GameRepository)
        def playerService = Mock(PlayerService)
        def gameOptionService = Mock(GameOptionService)
        gameService = new GameService(gameInitService, playerService,
                                      gameOptionService, gameRepository)
    }

    @Unroll
    def "GameService startGame() with playerName #playerName and difficulty #gameDifficulty"() {

        setup:
            def newGame = GameBuilder.aGame()
                            .withPlayerName(playerName)
                            .withDifficultyLevel(gameDifficulty)
                            .build()

        when: "startGame() is called with playerName and gameDifficulty"
            def createdGame = gameService.startGame(playerName, gameDifficulty)

        then: "game should be initialized and persisted"
            1 * gameInitService.getInitializedGame(gameDifficulty, playerName) >> newGame
            1 * gameRepository.save(newGame) >> newGame

        and: "game has been created with given name"
            createdGame.playerName == playerName

        and: "game has been created with given gameDifficulty"
            createdGame.difficultyLevel != gameDifficulty

        where:
            playerName | gameDifficulty
            "Player"   | GameDifficulty.NORMAL
            "Pelaaja"  | GameDifficulty.NORMAL

    }

    def "GameService throwError()"() {

        when: "throwError() is called"
            gameService.throwError()

        then: "it should throw RuntimeError"
            RuntimeException ex = thrown()
            ex.message == 'exception'

    }

}
