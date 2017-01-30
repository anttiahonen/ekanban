package fi.aalto.ekanban.services;

import static com.greghaskins.spectrum.Spectrum.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

import java.util.function.Supplier;

import com.greghaskins.spectrum.Spectrum;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import fi.aalto.ekanban.builders.GameBuilder;
import fi.aalto.ekanban.enums.GameDifficulty;
import fi.aalto.ekanban.models.db.games.Game;
import fi.aalto.ekanban.repositories.GameRepository;

@RunWith(Spectrum.class)
public class GameServiceSpec {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

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
                gameService = new GameService(gameInitService, playerService, gameOptionService, gameRepository);
            });

            describe("startGame", () -> {

                final Supplier<GameDifficulty> gameDifficulty = let(() -> GameDifficulty.NORMAL);
                final Supplier<String> playerName = let(() -> "Player");

                describe("with normal difficulty", () -> {

                    beforeEach(() -> {
                        Game blankGame = GameBuilder.aGame().build();
                        Mockito.when(gameInitService.getInitializedGame(
                                Mockito.any(GameDifficulty.class), Mockito.any(String.class))).thenReturn(blankGame);
                        Mockito.when(gameRepository.save(Mockito.any(Game.class))).thenReturn(blankGame);
                    });

                    final Supplier<Game> newGame = let(() -> gameService.startGame(playerName.get(), gameDifficulty.get()));

                    it("should return new game", () -> {
                        assertThat(newGame.get(), is(notNullValue()));
                    });

                    it("should call other services", () -> {
                        Mockito.verify(gameInitService, Mockito.times(1))
                                .getInitializedGame(Mockito.any(GameDifficulty.class), Mockito.any(String.class));
                        Mockito.verify(gameRepository, Mockito.times(1)).save(Mockito.any(Game.class));
                    });

                });
            });

            describe("throwError", () -> {
                final Supplier<Game> newGame = let(() -> gameService.throwError());

                it("should throw runTimeException", () -> {
                    thrown.expect(RuntimeException.class);
                    newGame.get();
                });
            });

        });
    }

}
