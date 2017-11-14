import { Game } from '../../../components/Game/Game'

export function setupProps() {
    const props = {
        onPlayTurn: jest.fn(),
        onResetSelectedDieAndCard: jest.fn(),
        onCreateAssignResource: jest.fn(),
        onCreateAssignedDie: jest.fn(),
        onResetAssignedDice: jest.fn(),
        assignedResources: [],
        enableNextRound: false,
        game: {
            difficultyLevel: "NORMAL",
            playerName: "player"
        },
        selectDice: false,
        selectedCard: false
    }
    return props
}

export function render(props) {
    const enzymeWrapper = shallow(<Game {...props} />)
    return enzymeWrapper
}