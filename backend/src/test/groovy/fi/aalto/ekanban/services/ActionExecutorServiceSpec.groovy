package fi.aalto.ekanban.services

import fi.aalto.ekanban.builders.CardBuilder
import fi.aalto.ekanban.builders.DrawFromBacklogActionBuilder
import fi.aalto.ekanban.models.DrawFromBacklogAction
import fi.aalto.ekanban.models.db.games.Game
import fi.aalto.ekanban.models.db.phases.Phase
import fi.aalto.ekanban.utils.TestGameContainer

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise
import spock.lang.Subject
import spock.lang.Unroll


@Stepwise
class ActionExecutorServiceSpec extends Specification {

    @Shared TestGameContainer initialGameContainer
    @Shared Phase firstPhase
    @Shared cardToBeDrawnFromBacklogDeck
    @Shared backlogDeckCardCountBeforeAction
    @Shared firstColumnCards
    @Shared countOfFirstColumnCardsBeforeBacklogDraw
    @Shared gameWithBacklogDrawnContainer
    @Shared countOfFirstColumnCardsAfterBacklogDraw
    @Shared givenPositionCardInFirstColumn

    @Subject
    ActionExecutorService actionExecutorService = new ActionExecutorService()

    def setup() {
        initialGameContainer = TestGameContainer.withNormalDifficultyMockGame()
        firstPhase = initialGameContainer.getAnalysisPhase()
    }

    @Unroll("Successful drawFromBacklog(), first phase with #numberOfCards card(s), wipLimit is #wipLimit and card is drawn to #position position")
    def "Successful ActionExecutorService drawFromBacklog()"() {

        given: "first phase with wip limit set to #wipLimit"
            setFirstPhaseWipLimit(wipLimit)

        and: "first phase holds #numberOfCards card(s)"
            generateCardsForFirstPhase(numberOfCards)

        when: "card is drawn from backlog to given index #index at first phase"
            drawCardToGivenIndexInFirstPhase(index)
            firstColumnCards = firstPhase.getColumns().get(0).getCards()
            countOfFirstColumnCardsAfterBacklogDraw = firstColumnCards.size()
            givenPositionCardInFirstColumn = firstColumnCards.get(index)

        then: "drawn card is set to first column"
            countOfFirstColumnCardsAfterBacklogDraw == countOfFirstColumnCardsBeforeBacklogDraw + 1

        and: "drawn card is set to given #position position"
            cardToBeDrawnFromBacklogDeck == givenPositionCardInFirstColumn

        and: "drawn card dayStarted is set to current day"
            givenPositionCardInFirstColumn.dayStarted == gameWithBacklogDrawnContainer.getGame().currentDay

        and: "backlogDeck card count has decreased by 1"
            def backlogDeckCardCountAfterAction = gameWithBacklogDrawnContainer.getGame().getBoard().getBacklogDeck().size()
            backlogDeckCardCountAfterAction == backlogDeckCardCountBeforeAction - 1

        and: "backlogDeck has its first card removed"
            def firstCardOnBacklogDeckAfterAction = gameWithBacklogDrawnContainer.getGame().getBoard().getBacklogDeck().get(0)
            firstCardOnBacklogDeckAfterAction != cardToBeDrawnFromBacklogDeck

        where:
            wipLimit | numberOfCards | index    | position
            2        | 1             | 0        | "first"
            3        | 2             | 1        | "second, between two existing cards"
            2        | 1             | 1        | "second, last"

    }

    @Unroll("Failing drawFromBacklog(), first phase with #numberOfCards card(s), wipLimit is #wipLimit and card is drawn to #position position")
    def "Failing ActionExecutorService drawFromBacklog() "() {

        given: "first phase with wip limit set to #wipLimit"
            setFirstPhaseWipLimit(wipLimit)

        and: "first phase holds #numberOfCards card(s)"
            generateCardsForFirstPhase(numberOfCards)

        when: "card is drawn from backlog to given index #index at first phase"
            drawCardToGivenIndexInFirstPhase(index)
            firstColumnCards = firstPhase.getColumns().get(0).getCards()
            countOfFirstColumnCardsAfterBacklogDraw = firstColumnCards.size()
            givenPositionCardInFirstColumn = firstColumnCards.get(index)

        then: "card should not have been set to first phase position #position"
            firstColumnCards.every{ it != cardToBeDrawnFromBacklogDeck }

        and: "card should remain in backlogDeck"
            backlogDeckCardCountBeforeAction == gameWithBacklogDrawnContainer.getGame().getBoard().getBacklogDeck().size()
            cardToBeDrawnFromBacklogDeck == gameWithBacklogDrawnContainer.getGame().getBoard().getBacklogDeck().get(0)

        where:
            wipLimit | numberOfCards | index    | position
            2        | 2             | 0        | "first (when wip limit full)"
            2        | 0             | 1        | "second (when no cards in phase)"

    }

    void setFirstPhaseWipLimit(Integer wipLimit) {
        firstPhase.setWipLimit(wipLimit)
        cardToBeDrawnFromBacklogDeck = initialGameContainer.getGame().getBoard().getBacklogDeck().get(0)
        backlogDeckCardCountBeforeAction = initialGameContainer.getGame().getBoard().getBacklogDeck().size()
    }

    void generateCardsForFirstPhase(Integer numberOfCards) {
        firstColumnCards = firstPhase.getColumns().get(0).getCards()
        (1..numberOfCards).each{
            firstColumnCards.add(CardBuilder.aCard().build())
        }
        countOfFirstColumnCardsBeforeBacklogDraw = firstColumnCards.size()
    }

    void drawCardToGivenIndexInFirstPhase(Integer index) {
        DrawFromBacklogAction backlogAction = DrawFromBacklogActionBuilder
                .aDrawFromBacklogAction()
                .withIndexToPlaceCardAt(index)
                .build()
        Game gameWithBacklogDrawn = actionExecutorService.drawFromBacklog(
                initialGameContainer.getGame(), Arrays.asList(backlogAction))
        gameWithBacklogDrawnContainer = TestGameContainer.withGame(gameWithBacklogDrawn)
    }


}
