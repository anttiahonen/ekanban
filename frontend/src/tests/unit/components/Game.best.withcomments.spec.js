import React from 'react'
import { setupProps, render } from './GameUtils'

//Well written specs should act as documentation for the code under test.
//The indented behavior of the object and its functions in different cases should be visible from test code, 
// without need to check the code to understand the test. Together with well written specs, the code should be understandable quickly
describe('components', () => {
    describe('Game', () => { // use describe block for the object under test
        describe('.handleNextRoundClick()', () => { //use describe block for having tests related to this method under one example group
            let props
            beforeEach(() => {
                props = setupProps()    // use beforeEach to setup unmodified props before each it-method. These props are shared between both separate
                                        // context-blocks, therefore props are created in a before-each at this level.
            })                          // Teardowns could be done with afterEach-blocks. 
                                        // Use beforeAll/afterAll-blocks, if the
                                        // test-object is unmodified during all the tests --> only need to instantiate once.

            context('with selected card and dice', () => { //context-block is an alias for describe-block
        
                                                           //describe-block is used to describe what object and method are being tested
                                                           //context-block is best used explaining the preconditions of the tests (context of test) 
                                                           // 
                                                           //context for jest needs to be installed separately, check instructions:
                                                           // https://www.npmjs.com/package/jest-plugin-context
                beforeEach(() => {
                    props.selectedDice = true              //these prop inits are shared between all 3 it-methods in this context 
                    props.selectedCard = true
                    const renderedGame = render(props)    //render-helper uses shallow-rendering (it renders only the Game-component, no subcomponents)
                    //creating of the context stops here //

                     renderedGame.instance().handleNextRoundClick()  //here we do the action of the tests
                })
                it('should create new assigned die', () => {
                    //each expectation is in its own it-method -> one-assertion per test method
                    expect(props.onCreateAssignedDie.mock.calls.length).toBe(1)
                })
                it('should reset selected card and die', () => {
                    //each it-method should describe the indented behavior of object's method under test
                    expect(props.onResetSelectedDieAndCard.mock.calls.length).toBe(1)
                })
                it('should call next turn action', () => {
                    //describe, context and it blocks are read from top-down
                    //this example: components -> Game -> .handleNextRoundClick() -> with selected card and dice -> should call next turn action
                    expect(props.onPlayTurn.mock.calls.length).toBe(1)
                })
            })
            context('with no selected card and dice', () => {   //here is another, separate-context, 
                                                                // where all the it-methods share the same test preconditions
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
        describe(".someOtherFunction()", () => {}) //another describe-block for some other function that could be tested
                                                   //use separate describe block per method
    })
})