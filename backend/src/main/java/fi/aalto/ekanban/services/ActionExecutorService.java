package fi.aalto.ekanban.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import fi.aalto.ekanban.models.AdjustWipLimitsAction;
import fi.aalto.ekanban.models.AssignResourcesAction;
import fi.aalto.ekanban.models.DrawFromBacklogAction;
import fi.aalto.ekanban.models.MoveCardAction;
import fi.aalto.ekanban.models.db.games.Card;
import fi.aalto.ekanban.models.db.games.CardPhasePoint;
import fi.aalto.ekanban.models.db.games.Game;
import fi.aalto.ekanban.models.db.phases.Column;
import fi.aalto.ekanban.models.db.phases.Phase;

@Service
public class ActionExecutorService {

    private static final Logger logger = LoggerFactory.getLogger(PlayerService.class);

    public Game adjustWipLimits(Game game, AdjustWipLimitsAction adjustWipLimitsAction) {
        if (game != null && game.isValid() && adjustWipLimitsAction != null) {
            game.getBoard().getPhases()
                    .forEach(phase -> {
                        String key = phase.getId();
                        Integer newWipLimitForPhaseOrUseOldIfNotGiven =
                                adjustWipLimitsAction.getPhaseWipLimits().getOrDefault(key, phase.getWipLimit());
                        phase.setWipLimit(newWipLimitForPhaseOrUseOldIfNotGiven);
                    });
        }
        return game;
    }

    public Game assignResources(Game game, List<AssignResourcesAction> assignResourcesActions) {
        if (game != null && game.isValid() && assignResourcesActions != null) {
            assignResourcesActions.forEach(assignResourcesAction -> {
                if (!isValidAssignResourcesAction(assignResourcesAction, game)) {
                    return;
                }
                game.performAssignResourcesAction(assignResourcesAction);
            });
        }
        return game;
    }

    public Game moveCards(Game game, List<MoveCardAction> moveCardActions) {
        if (game != null && game.isValid() && moveCardActions != null) {
            moveCardActions.forEach(moveCardAction -> {
                if (!isValidMoveCardAction(moveCardAction, game)) {
                    return;
                }
                game.performMoveCardAction(moveCardAction);
            });
        }
        return game;
    }

    public Game drawFromBacklog(Game game, List<DrawFromBacklogAction> drawActions) {
        if (game != null && game.isValid() && drawActions != null) {
            Phase firstPhase = game.getBoard().getPhases().get(0);

            drawActions.forEach(drawAction -> {
                if (isValidDrawFromBacklogAction(firstPhase, drawAction)) {
                    List<Card> backlogDeck = game.getBoard().getBacklogDeck();
                    Card cardToDraw = backlogDeck.get(0);
                    Boolean cardSuccessfullyRemovedFromDeck = backlogDeck.remove(cardToDraw);
                    if (cardSuccessfullyRemovedFromDeck) {
                        Column columnWhereCardIsToBeSet = firstPhase.getColumns().get(0);
                        columnWhereCardIsToBeSet.getCards().add(drawAction.getIndexToPlaceCardAt(), cardToDraw);
                        cardToDraw.setDayStarted(game.getCurrentDay());
                    }
                }
            });
        }

        return game;
    }

    private static Boolean isValidDrawFromBacklogAction(Phase firstPhase, DrawFromBacklogAction drawAction) {
        return !firstPhase.isFullWip()
                && drawAction.getIndexToPlaceCardAt() >= 0
                && drawAction.getIndexToPlaceCardAt() <= firstPhase.getColumns().get(0).getCards().size();
    }

    private static boolean isValidAssignResourcesAction(AssignResourcesAction assignResourcesAction, Game game) {
        Card card = game.getCardWithId(assignResourcesAction.getCardId());
        CardPhasePoint cardPhasePoint = card.getCardPhasePointOfPhase(assignResourcesAction.getPhaseId());
        Integer pointsLeft = cardPhasePoint.getTotalPoints() - cardPhasePoint.getPointsDone();
        return assignResourcesAction.getPoints() <= pointsLeft
                && game.isCardInFirstColumnOfPhase(card, assignResourcesAction.getPhaseId());
    }

    private static boolean isValidMoveCardAction(MoveCardAction moveCardAction, Game game) {
        return game.isColumnNextAdjacent(moveCardAction.getFromColumnId(), moveCardAction.getToColumnId()) &&
                !game.doesMoveExceedWIP(moveCardAction) &&
                game.isCardInColumn(moveCardAction.getCardId(), moveCardAction.getFromColumnId());
    }

}
