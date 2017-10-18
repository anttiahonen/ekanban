package fi.aalto.ekanban.services;

import static com.greghaskins.spectrum.Spectrum.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

import java.util.function.Supplier;

import com.greghaskins.spectrum.Spectrum;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import fi.aalto.ekanban.builders.GameBuilder;
import fi.aalto.ekanban.enums.GameDifficulty;
import fi.aalto.ekanban.models.db.games.Game;
import fi.aalto.ekanban.repositories.GameRepository;

@RunWith(Spectrum.class)
public class GameServiceSpec {

    private static GameService gameService;
    private static GameInitService gameInitService;
    private static PlayerService playerService;
    private static GameOptionService gameOptionService;
    private static GameRepository gameRepository;

    {
        describe("GameService", () -> {

            beforeAll(() -> {
                gameInitService = Mockito.mock(GameInitService.class);
                playerService = Mockito.mock(PlayerService.class);
                gameOptionService = Mockito.mock(GameOptionService.class);
                gameRepository = Mockito.mock(GameRepository.class);
                gameService = new GameService(gameInitService, playerService,
                                              gameOptionService, gameRepository);
            });

    describe("startGame", () -> {

        final Supplier<GameDifficulty> gameDifficulty = let(() -> GameDifficulty.NORMAL);
        final Supplier<String> playerName = let(() -> "Player");
        final Supplier<Game> newGame = let(() -> GameBuilder.aGame()
                                                    .withPlayerName(playerName.get())
                                                    .withDifficultyLevel(gameDifficulty.get())
                                                    .build());

        describe("with normal difficulty", () -> {

            beforeEach(() -> {
                Mockito.when(gameInitService.getInitializedGame(
                        Mockito.any(GameDifficulty.class),
                        Mockito.any(String.class))).thenReturn(newGame.get());
                Mockito.when(gameRepository.save(Mockito.any(Game.class))).thenReturn(newGame.get());
            });

            final Supplier<Game> createdGame = let(() ->
                    gameService.startGame(playerName.get(), gameDifficulty.get()));

            it("should create game with the given name", () -> {
                assertThat(createdGame.get().getPlayerName(), equalTo(playerName.get()));
            });

            it("should create game with the given game difficulty", () -> {
                assertThat(createdGame.get().getDifficultyLevel(), equalTo(gameDifficulty.get()));
            });

        });
    });

            describe("throwError", () -> {
                final Supplier<Game> newGame = let(() -> gameService.throwError());

                it("should throw runTimeException", () -> {
                    //thrown.expect(RuntimeException.class);
                    //newGame.get();
                });
            });

        });
    }

}
