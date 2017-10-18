package fi.aalto.ekanban.services;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import fi.aalto.ekanban.enums.GameDifficulty;
import fi.aalto.ekanban.models.db.games.Game;
import fi.aalto.ekanban.repositories.GameRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class GameServiceIntegrationTest {

    @Autowired
    private GameService gameService;

    @Autowired
    private GameRepository gameRepository;

    private GameDifficulty gameDifficulty;
    private String playerName;

    @Before
    public void initGameValues() {
        gameDifficulty = GameDifficulty.NORMAL;
        playerName = "Player";
    }

    @Test
    public void testStartGameWithNormalDifficulty() {
        Game createdGame = gameService.startGame(playerName, gameDifficulty);

        assertThat(createdGame, is(notNullValue()));
        assertThat(createdGame.getPlayerName(), equalTo(playerName));
        assertThat(createdGame.getDifficultyLevel(), equalTo(gameDifficulty));
    }

    @Test
    public void testStartGamePersistsToDB() {
        Integer gameCountBeforeStartGame = gameRepository.findAll().size();

        gameService.startGame(playerName, gameDifficulty);

        Integer gameCountAfterStartGame = gameRepository.findAll().size();
        assertThat(gameCountAfterStartGame, is(greaterThan(gameCountBeforeStartGame)));
    }

}
