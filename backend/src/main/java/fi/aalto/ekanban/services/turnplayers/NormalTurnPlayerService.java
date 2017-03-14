package fi.aalto.ekanban.services.turnplayers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.aalto.ekanban.services.ai.assignresources.AssignResourcesAIService;
import fi.aalto.ekanban.services.ai.drawfrombacklog.DrawFromBacklogAIService;
import fi.aalto.ekanban.services.ai.movecards.MoveCardsAIService;
import fi.aalto.ekanban.services.ActionExecutorService;
import fi.aalto.ekanban.models.db.games.Game;
import fi.aalto.ekanban.models.AssignResourcesAction;
import fi.aalto.ekanban.models.DrawFromBacklogAction;
import fi.aalto.ekanban.models.MoveCardAction;
import fi.aalto.ekanban.models.Turn;

@Service
public class NormalTurnPlayerService implements TurnPlayer {

    @Autowired
    private AssignResourcesAIService assignResourcesAIService;

    @Autowired
    private MoveCardsAIService moveCardsAIService;

    @Autowired
    private DrawFromBacklogAIService drawFromBacklogAIService;

    @Autowired
    private ActionExecutorService actionExecutorService;


    public Game playTurn(Game game, Turn turn) {
        game = actionExecutorService.adjustWipLimits(game, turn.getAdjustWipLimitsAction());
        game = assignResourcesWithAI(game);
        game = moveCardsWithAI(game);
        game = drawFromBacklogWithAI(game);
        return game;
    }

    private Game assignResourcesWithAI(Game game) {
        List<AssignResourcesAction> assignResourcesActions = assignResourcesAIService.getAssignResourcesActions(game);
        return actionExecutorService.assignResources(game, assignResourcesActions);
    }

    private Game moveCardsWithAI(Game game) {
        List<MoveCardAction> moveCardActions = moveCardsAIService.getMoveCardsActions(game);
        return actionExecutorService.moveCards(game, moveCardActions);
    }

    private Game drawFromBacklogWithAI(Game game) {
        List<DrawFromBacklogAction> drawFromBacklogActions = drawFromBacklogAIService.getDrawFromBacklogActions(game);
        return actionExecutorService.drawFromBacklog(game, drawFromBacklogActions);
    }
}
