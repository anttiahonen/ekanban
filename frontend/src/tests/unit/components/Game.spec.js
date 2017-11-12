import React from 'react'
import { mount } from 'enzyme'
import { Game } from '../../../components/Game/Game'

function setupProps() {
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

function render(props) {
    const enzymeWrapper = shallow(<Game {...props} />)
    return enzymeWrapper
}

describe('components', () => {
    describe('Game', () => {
        describe('.handleNextRoundClick()', () => {
            let props
            beforeEach(() => {
                props = setupProps()
            })
            describe('with selected card and dice', () => {
                beforeEach(() => {
                    props.selectedDice = true
                    props.selectedCard = true
                    const enzymeWrapper = render(props)
                    enzymeWrapper.instance().handleNextRoundClick()
                })
                it('should create new assigned die', () => {
                    expect(props.onCreateAssignedDie.mock.calls.length).toBe(1)
                })
                it('should reset selected card and die', () => {
                    expect(props.onResetSelectedDieAndCard.mock.calls.length).toBe(1)
                })
                it('should call next turn action', () => {
                    expect(props.onPlayTurn.mock.calls.length).toBe(1)
                })
            })
            describe('with no selected card and dice', () => {
                beforeEach(() => {
                    const enzymeWrapper = render(props)
                    enzymeWrapper.instance().handleNextRoundClick()
                })
                it('should not create a new assigned die', () => {
                    expect(props.onCreateAssignedDie.mock.calls.length).toBe(0)
                })
                it('should not reset selected card and die', () => {
                    expect(props.onResetSelectedDieAndCard.mock.calls.length).toBe(0)
                })
                it('should call next turn action', () => {
                    expect(props.onPlayTurn.mock.calls.length).toBe(1)
                })
            })
        })
    })
})