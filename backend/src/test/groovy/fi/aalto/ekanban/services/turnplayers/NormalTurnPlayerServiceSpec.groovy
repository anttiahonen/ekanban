package fi.aalto.ekanban.services.turnplayers

import com.blogspot.toomuchcoding.spock.subjcollabs.Collaborator
import com.blogspot.toomuchcoding.spock.subjcollabs.Subject
import spock.lang.Shared
import spock.lang.Specification

import fi.aalto.ekanban.builders.AdjustWipLimitsActionBuilder
import fi.aalto.ekanban.builders.AssignResourcesActionBuilder
import fi.aalto.ekanban.builders.DrawFromBacklogActionBuilder
import fi.aalto.ekanban.builders.GameBuilder
import fi.aalto.ekanban.builders.MoveCardActionBuilder
import fi.aalto.ekanban.builders.TurnBuilder
import fi.aalto.ekanban.models.AssignResourcesAction
import fi.aalto.ekanban.models.DrawFromBacklogAction
import fi.aalto.ekanban.models.MoveCardAction
import fi.aalto.ekanban.services.ActionExecutorService
import fi.aalto.ekanban.services.ai.assignresources.AssignResourcesAIService
import fi.aalto.ekanban.services.ai.drawfrombacklog.DrawFromBacklogAIService
import fi.aalto.ekanban.services.ai.movecards.MoveCardsAIService

class NormalTurnPlayerServiceSpec extends Specification {

    //Note: this is not recommend way on injecting mocks, but used for example
    @Collaborator ActionExecutorService actionExecutorService = Mock()
    @Collaborator AssignResourcesAIService assignResourcesAIService = Mock()
    @Collaborator MoveCardsAIService moveCardsAIService = Mock()
    @Collaborator DrawFromBacklogAIService drawFromBacklogAIService = Mock()

    @Subject NormalTurnPlayerService normalTurnPlayerService

    @Shared List<AssignResourcesAction> assignResourcesActions
    @Shared List<MoveCardAction> moveCardActions
    @Shared List<DrawFromBacklogAction> drawFromBacklogActions

    def setup() {
        assignResourcesActions = Arrays.asList(AssignResourcesActionBuilder.anAssignResourcesAction().build(),
                AssignResourcesActionBuilder.anAssignResourcesAction().build())
        moveCardActions = Arrays.asList(MoveCardActionBuilder.aMoveCardAction().build(),
                MoveCardActionBuilder.aMoveCardAction().build())
        drawFromBacklogActions = Arrays.asList(DrawFromBacklogActionBuilder.aDrawFromBacklogAction().build(),
                DrawFromBacklogActionBuilder.aDrawFromBacklogAction().build())
    }

    def "NormalTurnPlayerService playTurn()"() {

        given: "a new game"
            def game = GameBuilder.aGame()
                .withNormalDifficultyMockDefaults("Player")
                .build()

        and: "a turn with wipLimitAdjustActions"
            def wipLimits = [analysis: 5, development: 10, test: 20]
            def turnWithAdjustWipLimits = TurnBuilder.aTurn()
                .withAdjustWipLimitsAction(AdjustWipLimitsActionBuilder.anAdjustWipLimitsAction().withPhaseWipLimits(wipLimits).build())
                .build()

        when: "playTurn is called for given game and turn"
            def gameWithTurnPlayed = normalTurnPlayerService.playTurn(game, turnWithAdjustWipLimits)

        then: "game wip limits should be adjusted"
            1 * actionExecutorService.adjustWipLimits(game, turnWithAdjustWipLimits.adjustWipLimitsAction) >> game

        and: "game should have resources assigned to it's cards"
            1 * assignResourcesAIService.getAssignResourcesActions(game) >> assignResourcesActions
            1 * actionExecutorService.assignResources(game, assignResourcesActions) >> game

        and: "game cards that are ready for next phase should be moved"
            1 * moveCardsAIService.getMoveCardsActions(game) >> moveCardActions
            1 * actionExecutorService.moveCards(game, moveCardActions) >> game

        and: "game should have new card drawn from backlog"
            1 * drawFromBacklogAIService.getDrawFromBacklogActions(game) >> drawFromBacklogActions
            1 * actionExecutorService.drawFromBacklog(game, drawFromBacklogActions) >> game


    }
    
}