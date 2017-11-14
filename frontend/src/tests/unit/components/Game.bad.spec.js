import { setupProps, render } from './GameUtils'

it('test handleNextRoundClick', () => {
    let props = setupProps()
    props.selectedDice = true
    props.selectedCard = true
    let enzymeWrapper = render(props)
    enzymeWrapper.instance().handleNextRoundClick()
    expect(props.onCreateAssignedDie.mock.calls.length).toBe(1)
    expect(props.onResetSelectedDieAndCard.mock.calls.length).toBe(1)
    expect(props.onPlayTurn.mock.calls.length).toBe(1)

    props = setupProps()
    enzymeWrapper = render(props)
    enzymeWrapper.instance().handleNextRoundClick()
    expect(props.onCreateAssignedDie.mock.calls.length).toBe(0)
    expect(props.onResetSelectedDieAndCard.mock.calls.length).toBe(0)
    expect(props.onPlayTurn.mock.calls.length).toBe(1)
})