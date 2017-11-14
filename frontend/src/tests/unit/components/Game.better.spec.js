import React from 'react'
import { setupProps, render } from './GameUtils'

describe('Game', () => {
    let props
    beforeEach(() => {
        props = setupProps()
    })

    it('handleNextRoundClick should create assigned die when there exists selected card and die', () => {
        props.selectedDice = true
        props.selectedCard = true
        const enzymeWrapper = render(props)
        
        enzymeWrapper.instance().handleNextRoundClick()
        
        expect(props.onCreateAssignedDie.mock.calls.length).toBe(1)
    })
    
    it('handleNextRoundClick should reset selected card and dice when there exists selected card and die', () => {
        props.selectedDice = true
        props.selectedCard = true
        const enzymeWrapper = render(props)
        
        enzymeWrapper.instance().handleNextRoundClick()
        
        expect(props.onResetSelectedDieAndCard.mock.calls.length).toBe(1)
    })
    
    it('handleNextRoundClick should call next turn action when there exists selected card and die', () => {
        props.selectedDice = true
        props.selectedCard = true
        const enzymeWrapper = render(props)
        
        enzymeWrapper.instance().handleNextRoundClick()
        
        expect(props.onPlayTurn.mock.calls.length).toBe(1)
    })

    it('handleNextRoundClick should not create assigned die when no selected card and die', () => {
        const enzymeWrapper = render(props)
        
        enzymeWrapper.instance().handleNextRoundClick()

        expect(props.onCreateAssignedDie.mock.calls.length).toBe(0)
    })

    it('handleNextRoundClick should not reset selected card and dice when no selected card and die', () => {
        const enzymeWrapper = render(props)
        
        enzymeWrapper.instance().handleNextRoundClick()

        expect(props.onResetSelectedDieAndCard.mock.calls.length).toBe(0)
    })

    it('handleNextRoundClick should call next turn action when no selected card and die', () => {
        const enzymeWrapper = render(props)
        
        enzymeWrapper.instance().handleNextRoundClick()

        expect(props.onPlayTurn.mock.calls.length).toBe(1)
    })

})

