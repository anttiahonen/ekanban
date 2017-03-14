package fi.aalto.ekanban.controllers;

import static com.greghaskins.spectrum.Spectrum.*;
import static fi.aalto.ekanban.ApplicationConstants.GAME_PATH;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.net.HttpURLConnection;
import java.util.function.Supplier;

import com.greghaskins.spectrum.Spectrum;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.runner.RunWith;

import fi.aalto.ekanban.enums.GameDifficulty;
import fi.aalto.ekanban.repositories.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestContextManager;

@RunWith(Spectrum.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GameControllerIntegrationSpec {

    private static final String PLAYER_NAME = "playerName";
    private static final String DIFFICULTY_LEVEL_NAME = "difficultyLevel";

    @Autowired
    private GameRepository gameRepository;

    @Value("${local.server.port}")
    int port;

    {

        beforeAll(() -> {
            new TestContextManager(getClass()).prepareTestInstance(this);
            RestAssured.port = port;
            gameRepository.deleteAll();
        });

        afterAll(() -> {
            gameRepository.deleteAll();
        });

        describe("/games post", () -> {

            describe("when creating", () -> {

                final Supplier<String> playerName = let(() -> "Player");

                describe("with normal difficulty", () -> {

                    final Supplier<GameDifficulty> gameDifficulty = let(() -> GameDifficulty.NORMAL);

                    final Supplier<Response> response = let(() -> given()
                            .formParam(PLAYER_NAME, "Player")
                            .formParam(DIFFICULTY_LEVEL_NAME, GameDifficulty.NORMAL)
                            .when().post(GAME_PATH));

                    it("should return 200 ok", () -> {
                        response.get().then().statusCode(HttpURLConnection.HTTP_OK);
                    });

                    it("should insert game to db", () -> {
                        assertThat(gameRepository.findAll().size(), equalTo(1));
                    });

                    it("should set given playerName for game", () -> {
                        response.get().then().body(PLAYER_NAME, equalTo(playerName.get()));
                    });

                    it("should set given gameDifficulty for game", () -> {
                        response.get().then().body("difficultyLevel", equalTo(gameDifficulty.get().toString()));
                    });

                });
            });

        });
    }

}
