package fi.aalto.ekanban.services;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

import java.util.*;

import com.rits.cloning.Cloner;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import fi.aalto.ekanban.models.DrawFromBacklogAction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import fi.aalto.ekanban.builders.*;
import fi.aalto.ekanban.models.db.games.Card;
import fi.aalto.ekanban.models.db.games.Game;
import fi.aalto.ekanban.models.db.phases.Phase;
import fi.aalto.ekanban.utils.TestGameContainer;

@RunWith(HierarchicalContextRunner.class)
public class ActionExecutorServiceTest {

    private ActionExecutorService actionExecutorService;
    private TestGameContainer initialGameContainer;

    @Before
    public void init() {
        actionExecutorService = new ActionExecutorService();
        initialGameContainer = TestGameContainer.withNormalDifficultyMockGame();
    }
    public class drawFromBacklog {

        private TestGameContainer gameWithBacklogDrawnContainer;
        private Card cardToBeDrawnFromBacklogDeck;
        private List<Card> firstColumnCards;

        @Before
        public void initContext() {
            cardToBeDrawnFromBacklogDeck = initialGameContainer.getGame().getBoard().getBacklogDeck().get(0);
            Phase firstPhase = initialGameContainer.getGame().getBoard().getPhases().get(0);
            firstPhase.setWipLimit(2);
            firstColumnCards = initialGameContainer.getGame().getBoard()
                    .getPhases().get(0).getColumns().get(0).getCards();
        }

        private void drawCardToIndex(Integer index) {
            DrawFromBacklogAction backlogAction = DrawFromBacklogActionBuilder
                    .aDrawFromBacklogAction()
                    .withIndexToPlaceCardAt(index)
                    .build();
            Game gameWithBacklogDrawn = actionExecutorService.drawFromBacklog(
                    initialGameContainer.getGame(), Arrays.asList(backlogAction));
            gameWithBacklogDrawnContainer = TestGameContainer.withGame(gameWithBacklogDrawn);
            firstColumnCards = gameWithBacklogDrawnContainer.getGame().getBoard()
                    .getPhases().get(0).getColumns().get(0).getCards();
        }

        public class whenWipLimitIsNotFull {
            @Before
            public void setOneCardToFirstColumn() {
                firstColumnCards.add(CardBuilder.aCard().build());
            }

            @Test
            public void shouldSetDrawnCardToFirstColumn() {
                Integer countOfFirstColumnCardsBeforeBacklogDraw = firstColumnCards.size();
                drawCardToIndex(0);
                Integer countOfFirstColumnCardsAfterBacklogDraw = firstColumnCards.size();

                assertThat(countOfFirstColumnCardsAfterBacklogDraw,
                        equalTo(countOfFirstColumnCardsBeforeBacklogDraw + 1));
            }

            @Test
            public void shouldSetDrawnCardToFirstPosition() {
                drawCardToIndex(0);
                Card firstCardOnTheFirstColumn = firstColumnCards.get(0);

                assertThat(cardToBeDrawnFromBacklogDeck, equalTo(firstCardOnTheFirstColumn));
                assertThat(firstColumnCards.size(), equalTo(2));
            }

            @Test
            public void shouldSetDrawnCardBetweenFirstAndLastPosition() {
                initialGameContainer.getGame().getBoard().getPhases().get(0).setWipLimit(3);
                firstColumnCards.add(CardBuilder.aCard().build());
                drawCardToIndex(1);

                Card secondCardOnTheFirstColumn = firstColumnCards.get(1);
                assertThat(cardToBeDrawnFromBacklogDeck, equalTo(secondCardOnTheFirstColumn));
                assertThat(firstColumnCards.size(), equalTo(3));
            }

            @Test
            public void shouldSetDrawnCardToLastPosition() {
                Integer lastPosition = firstColumnCards.size();
                drawCardToIndex(lastPosition);

                Card lastCardOnTheFirstColumn = firstColumnCards.get(firstColumnCards.size() - 1);
                assertThat(cardToBeDrawnFromBacklogDeck, equalTo(lastCardOnTheFirstColumn));
                assertThat(firstColumnCards.size(), equalTo(2));
            }

            @Test
            public void shouldChangeBacklogDeckCardCountByOne() {
                Integer backlogDeckCardCountBeforeAction = initialGameContainer.getGame().getBoard()
                        .getBacklogDeck().size();
                drawCardToIndex(0);
                Integer backlogDeckCardCountAfterAction = gameWithBacklogDrawnContainer.getGame().getBoard()
                        .getBacklogDeck().size();

                assertThat(backlogDeckCardCountAfterAction, equalTo(backlogDeckCardCountBeforeAction - 1));
            }

            @Test
            public void shouldRemoveTheFirstCardFromBacklogDeck() {
                Card firstCardOnBacklogDeckBeforeAction = initialGameContainer.getGame().getBoard()
                        .getBacklogDeck().get(0);
                drawCardToIndex(0);
                Card firstCardOnBacklogDeckAfterAction = gameWithBacklogDrawnContainer.getGame().getBoard()
                        .getBacklogDeck().get(0);

                assertThat(firstCardOnBacklogDeckBeforeAction, not(equalTo(firstCardOnBacklogDeckAfterAction)));
            }

            @Test
            public void shouldSetDayStartedToCurrentDay() {
                drawCardToIndex(0);
                assertThat(cardToBeDrawnFromBacklogDeck.getDayStarted(),
                        equalTo(initialGameContainer.getGame().getCurrentDay()));
            }
        }

        public class whenWipLimitIsFull {

            @Before
            public void setFirstPhaseToFullWipLimit() {
                firstColumnCards.add(CardBuilder.aCard().build());
                firstColumnCards.add(CardBuilder.aCard().build());
            }

            @Test
            public void shouldNotSetCardToColumn() {
                assertShouldNotSedCardToColumnWhenDrawnTo(0);
            }

            @Test
            public void shouldNotChangeBacklogDeckCardCount() {
                assertShouldChangeBacklogDeckCardCountWhenDrawnTo(0);
            }

        }

        public class whenInvalidIndexIsGiven {
            @Test
            public void shouldNotSetCardToColumn() {
                assertShouldNotSedCardToColumnWhenDrawnTo(1);
            }

            @Test
            public void shouldNotChangeBacklogDeckCardCount() {
                assertShouldChangeBacklogDeckCardCountWhenDrawnTo(1);
            }
        }

        private void assertShouldChangeBacklogDeckCardCountWhenDrawnTo(Integer index) {
            Integer backlogDeckCardCountBeforeAction = initialGameContainer.getGame().getBoard()
                    .getBacklogDeck().size();
            drawCardToIndex(index);
            Integer backlogDeckCardCountAfterAction = gameWithBacklogDrawnContainer.getGame().getBoard()
                    .getBacklogDeck().size();

            assertThat(backlogDeckCardCountAfterAction, equalTo(backlogDeckCardCountBeforeAction));
        }

        private void assertShouldNotSedCardToColumnWhenDrawnTo(Integer index) {
            Cloner cloner = new Cloner();
            List<Card> cardsInFirstColumnBeforeAction = cloner.deepClone(firstColumnCards);
            Integer countOfCardsInFirstColumnBeforeBacklogDraw = firstColumnCards.size();
            drawCardToIndex(index);
            Integer countOfCardsInFirstColumnAfterBacklogDraw = firstColumnCards.size();

            assertThat(countOfCardsInFirstColumnAfterBacklogDraw,
                    equalTo(countOfCardsInFirstColumnBeforeBacklogDraw));
            for (Integer i = 0; i < firstColumnCards.size(); i++) {
                assertThat(cardsInFirstColumnBeforeAction.get(i), equalTo(firstColumnCards.get(i)));
            }
        }
    }
}