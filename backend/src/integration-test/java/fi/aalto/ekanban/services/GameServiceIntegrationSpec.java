package fi.aalto.ekanban.services;

import static com.greghaskins.spectrum.Spectrum.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

import java.util.function.Supplier;

import com.greghaskins.spectrum.Spectrum;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestContextManager;

import fi.aalto.ekanban.enums.GameDifficulty;
import fi.aalto.ekanban.models.db.games.Game;
import fi.aalto.ekanban.repositories.GameRepository;


@RunWith(Spectrum.class)
@SpringBootTest
public class GameServiceIntegrationSpec {

    @Autowired
    private GameService gameService;

    @Autowired
    private GameRepository  gameRepository;

    {
        beforeAll(() -> {
            new TestContextManager(getClass()).prepareTestInstance(this);
            gameRepository.deleteAll();
        });

        afterEach(() -> {
            gameRepository.deleteAll();
        });

        describe("GameService", () -> {
            describe("startGame", () -> {

                final Supplier<GameDifficulty> gameDifficulty = let(() -> GameDifficulty.NORMAL);
                final Supplier<String> playerName = let(() -> "Player");

                describe("with normal difficulty", () -> {

                    final Supplier<Game> newGame = let(() -> gameService.startGame(playerName.get(), gameDifficulty.get()));

                    it("should return new game", () -> {
                        assertThat(newGame.get(), is(notNullValue()));
                    });

                });
            });
        });
    }

}
