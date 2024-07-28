package finite.automaton;

import finite.automaton.exceptions.InvalidStateException;
import finite.automaton.state.State;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static finite.automaton.state.StateValueChecker.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThrows;

public class FSMUnitTests extends TestCase {

    private final char[] ALPHABET = {'0', '1', '2'};
    private final String[] NAMES = {"S0", "S1", "S2", "S3", "S4"};
    private final int[] VALUES = {1, 2, 3, 4, 5};


    //////////////
    // initial state tests
    @Test
    public void testEmptyMachine(){
        // check initial fields
        FSM<Integer> machine = new FSM<>(ALPHABET);
        assertArrayEquals(ALPHABET, machine.getAlphabet());

        Collection<State<Integer>> states = machine.getStates();
        assertEquals(states.size(), 1);
        assertNull(machine.getInitState());
        assertFalse(machine.isReady());

        // random search should fail
        assertNull(machine.getStateByName("random"));
    }

    @Test
    public void testNullStateInEmptyMachine(){
        FSM<Integer> machine = new FSM<>(ALPHABET);
        // from first test, null state should be unique state
        State<Integer> nullState = machine.getNullState();

        // alt ways to get null value
        assertSame(nullState, machine.getStateByName(null));
        assertSame(nullState, machine.getStates().get(0));
        // null state is only state here
        // see "testEmptyMachine" for validity of this statement

        // expected outputs of null state
        checkValuelessStateNotFinal(nullState, null);

        for (char c : ALPHABET) {
            try {
                assertSame(nullState, machine.getTransition(nullState, c));
            }
            catch(Exception e){
                fail("Could not get transition from null state: " + e.getMessage());
            }
        }
    }


    //////////////
    // alphabet tests

    public void testCharacterInAlphabet(){
        char[] alphabet1 = {'0', '1', '2', '3'};
        char[] alphabet2 = {'a', 'b', 'c', 'd'};
        FSM<Integer> machine = new FSM<>(alphabet1);

        // assert alphabets are fine
        for(char c : alphabet1){
            assertTrue(machine.isInAlphabet(c));
        }
        for(char c : alphabet2){
            assertFalse(machine.isInAlphabet(c));
        }
    }


    //////////////
    // state creation tests

    @Test
    public void testBlankStateCreation() {
        FSM<Integer> machine = new FSM<>(ALPHABET);

        State<Integer> state = machine.setNewState(NAMES[0]);
        checkValuelessStateNotFinal(state, NAMES[0]);

        assertNotSame(state, machine.getNullState());
        assertSame(state, machine.getStateByName(NAMES[0]));
    }

    @Test
    public void testValuedStateCreation() {
        FSM<Integer> machine = new FSM<>(ALPHABET);

        State<Integer> state = machine.setNewState(NAMES[0], VALUES[0]);
        checkWholeStateNotFinal(state, NAMES[0], VALUES[0]);

        assertNotSame(state, machine.getNullState());
        assertSame(state, machine.getStateByName(NAMES[0]));
    }

    @Test
    public void testFinalStateCreation() {
        FSM<Integer> machine = new FSM<>(ALPHABET);

        State<Integer> state = machine.setNewFinalState(NAMES[0], VALUES[0]);
        checkWholeStateFinal(state, NAMES[0], VALUES[0]);

        assertNotSame(state, machine.getNullState());
        assertSame(state, machine.getStateByName(NAMES[0]));
    }

    public void testMultipleStateCreation(){
        FSM<Integer> machine = new FSM<>(ALPHABET);

        ArrayList<State<Integer>> nonnullStates = new ArrayList<>();
        nonnullStates.add(machine.setNewState(NAMES[0]));
        nonnullStates.add(machine.setNewState(NAMES[1], VALUES[1]));
        nonnullStates.add(machine.setNewFinalState(NAMES[2], VALUES[2]));
        nonnullStates.add(machine.setNewFinalState(NAMES[3], VALUES[3]));

        for(int i = 0; i < nonnullStates.size(); i++) {
            State<Integer> state = nonnullStates.get(i);

            assertNotSame(state, machine.getNullState());
            assertSame(state, machine.getStateByName(NAMES[i]));

            // check generated states are not the same
            for(int j = 0; j < i; j++){
                assertNotSame(state, nonnullStates.get(j));
            }
        }

        // check correct number of states
        assertEquals(nonnullStates.size() + 1, machine.getStates().size());
    }

    public void testStateIncorporation(){
        FSM<Integer> machine = new FSM<>(ALPHABET);
        State<Integer> first = new State<>(NAMES[0], false, VALUES[0]);
        State<Integer> second = new State<>(NAMES[1], false, VALUES[1]);

        // incorporate states
        try {
            machine.incorporateNewState(first);
            machine.incorporateNewState(second);
        }
        catch(Exception e){
            fail("Could not incorporate both states: " + e.getMessage());
        }

        // test both in machine by fetching by name
        assertSame(first, machine.getStateByName(first.getName()));
        assertSame(second, machine.getStateByName(second.getName()));
        assertEquals(3, machine.getStates().size()); // check size for good luck

        //if both in machine, we should be able to make valid connections
        try{
            machine.setTransition(first, ALPHABET[0], second);
            assertSame(second, machine.getTransition(first, ALPHABET[0]));
        }
        catch(Exception e){
            fail("Could not use transition on externally incorporated states: " + e.getMessage());
        }
    }


    //////////////
    // other state tests

    @Test
    public void testStateContainmentCheck() {
        FSM<Integer> machine = new FSM<>(ALPHABET);

        // null state should always be contained
        State<Integer> nullState = machine.getNullState();
        assertTrue(machine.containsState(nullState));

        // new contained state
        State<Integer> inState = machine.setNewState(NAMES[0]);
        assertTrue(machine.containsState(inState));

        // new external state
        State<Integer> outState1 = new State<>(NAMES[1]);
        assertFalse(machine.containsState(outState1));

        // new external state, same name as internal state
        State<Integer> outState2 = new State<>(NAMES[0]);
        assertFalse(machine.containsState(outState2));
    }

    @Test
    public void testSetInitialState(){
        FSM<Integer> machine = new FSM<>(ALPHABET);
        assertNull(machine.getInitState());

        // make null state init
        try {
            machine.setInitState(machine.getNullState());
            assertNotNull(machine.getInitState());
            assertSame(machine.getNullState(), machine.getInitState());

            // make new state init
            State<Integer> state = machine.setNewFinalState(NAMES[0], VALUES[0]);
            machine.setInitState(state);
            assertNotSame(machine.getInitState(), machine.getNullState());
            assertSame(state, machine.getInitState());
        }
        catch(Exception e){
            fail("Could not test init state: " + e.getMessage());
        }
    }

    @Test
    public void testReplaceStateSameName(){
        FSM<Integer> machine = new FSM<>(ALPHABET);
        char c = ALPHABET[0];

        State<Integer> firstStateOld = machine.setNewState(NAMES[0]);
        State<Integer> secondState = machine.setNewState(NAMES[1]);
        try {
            // part 1: name1 + c -> name2
            machine.setTransition(firstStateOld, c, secondState);

            // verify current state of firstState & firstState + c
            assertSame(firstStateOld, machine.getStateByName(NAMES[0]));
            assertSame(secondState, machine.getTransition(firstStateOld, c));
        }
        catch(Exception e){
            fail("Could not setup/verify initial transition: " + e.getMessage());
        }

        // part 2: switch name1, ensure things work (see later comments)
        State<Integer> firstStateNew = machine.setNewState(NAMES[0]);
        // ensure no transitions can take place from old state
        // this ensures
        assertThrows("Attempting to set transition from state not in FSM.",
                InvalidStateException.class,
                () -> machine.setTransition(firstStateOld, c, secondState));
        try {
            assertSame(machine.getNullState(), machine.getTransition(firstStateNew, c));
        }
        catch(Exception e){
            fail("could not verify new first state has null transition: " + e.getMessage());
        }
    }


    //////////////
    // transition tests

    @Test
    public void testTransition(){
        FSM<Integer> machine = new FSM<>(ALPHABET);
        State<Integer> nullState = machine.getNullState();

        State<Integer> first = machine.setNewFinalState(NAMES[0], VALUES[0]);
        try {
            // test that all transitions begin as to null state
            for (char c : ALPHABET) {
                assertSame(nullState, machine.getTransition(first, c));
            }
        }
        catch(Exception e){
            fail("Failed to get null transitions from state: " + e.getMessage());
        }

        try {
            // add recursive mapping, check again
            machine.setTransition(first, ALPHABET[0], first);
            assertSame(first, machine.getTransition(first, ALPHABET[0]));
            for (int i = 1; i < ALPHABET.length; i++) {
                assertSame(nullState, machine.getTransition(first, ALPHABET[i]));
            }
        }
        catch(Exception e){
            fail("Could not get recursive transition: " + e.getMessage());
        }

        try {
            // add state and transition to it
            State<Integer> second = machine.setNewFinalState(NAMES[1], VALUES[1]);
            machine.setTransition(first, ALPHABET[1], second);

            assertSame(first, machine.getTransition(first, ALPHABET[0]));
            assertSame(second, machine.getTransition(first, ALPHABET[1]));
            assertSame(nullState, machine.getTransition(first, ALPHABET[2]));
        }
        catch(Exception e){
            fail("Could not get transition to new state: " + e.getMessage());
        }
    }

    @Test
    public void testReplaceTransition(){
        FSM<Integer> machine = new FSM<>(ALPHABET);
        State<Integer> nullState = machine.getNullState();

        State<Integer> first = machine.setNewFinalState(NAMES[0], VALUES[0]);
        State<Integer> second = machine.setNewFinalState(NAMES[1], VALUES[1]);
        State<Integer> third = machine.setNewFinalState(NAMES[2], VALUES[2]);
        char c = ALPHABET[0];

        try {
            // part 1: set first + c = second
            machine.setTransition(first, c, second);
            assertSame(second, machine.getTransition(first, c));
        }
        catch(Exception e){
            fail("First transition failed: " + e.getMessage());
        }

        try{
            // part 2: set first + c = third
            machine.setTransition(first, c, third);
            assertSame(third, machine.getTransition(first, c));
        }
        catch(Exception e){
            fail("Second transition failed: " + e.getMessage());
        }

        try {
            // part 3: set first + c = null
            // here, check all transitions from first go to null state
            machine.setTransition(first, c, nullState);
            for (char ch : ALPHABET) {
                assertSame(nullState, machine.getTransition(first, ch));
            }
        }
        catch(Exception e){
            fail("Final transition failed: " + e.getMessage());
        }
    }


    //////////////
    // functionality tests

    @Test
    public void testMachineReadinessNullStateOnly(){
        FSM<Integer> machine = new FSM<>(ALPHABET);
        assertFalse(machine.isReady());

        try {
            // null state can be used to make simplest ready machine
            // blank machine always ends at null state, which is valid behaviour
            machine.setInitState(machine.getNullState());
            assertTrue(machine.isReady());
        }
        catch(Exception e){
            fail("could not use null state as init state: " + e.getMessage());
        }
    }

    @Test
    public void testMachineReadinessOneState(){
        FSM<Integer> machine = new FSM<>(ALPHABET);
        assertFalse(machine.isReady()); // repeat for good luck

        // simplest ready non-trivial machine: one state mapping to itself on one character c
        // starts and remains on state 1 iff string is regex \c*\
        try{
            State<Integer> state = machine.setNewFinalState(NAMES[0], VALUES[0]);
            machine.setInitState(state);
            assertTrue(machine.isReady()); // machine should now be ready

            machine.setTransition(state, ALPHABET[0], state);
            assertTrue(machine.isReady());
        }
        catch(Exception e){
            fail("could not use new state in valid machine: " + e.getMessage());
        }
    }
}