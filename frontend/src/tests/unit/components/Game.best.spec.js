import React from 'react'
import { setupProps, render } from './GameUtils'

describe('components', () => {
    describe('Game', () => {
        describe('.handleNextRoundClick()', () => {
            let props
            beforeEach(() => {
                props = setupProps()
            })
            context('with selected card and dice', () => {
                beforeEach(() => {
                    props.selectedDice = true
                    props.selectedCard = true
                    const renderedGame = render(props)
                    renderedGame.instance().handleNextRoundClick()
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
            context('with no selected card and dice', () => {
                beforeEach(() => {
                    const renderedGame = render(props)
                    renderedGame.instance().handleNextRoundClick()
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