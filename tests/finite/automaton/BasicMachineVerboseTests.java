package finite.automaton;

import finite.automaton.state.State;
import junit.framework.TestCase;
import org.junit.Test;

public class BasicMachineVerboseTests extends TestCase {

    char[] ALPHABET = {'1', '2', '3'};
    String[] VALID_STRINGS = {"", "1", "2", "3", "12", "13", "32", "22", "3211213", "2212323"};
    char EXTERNAL = '0';

    String[] NAMES = {"S0", "S1", "S2"};

    //////////////
    // run tests

    @Test
    public void testMachineRunNullStateOnly(){
        FSM<Integer> machine = new FSM<>(ALPHABET);
        try {
            // null state can be used to make simplest ready machine
            // blank machine always ends at null state, which is valid behaviour
            machine.setInitState(machine.getNullState());
            assertTrue(machine.isReady());
        }
        catch(Exception e){
            fail("could not use null state as init state: " + e.getMessage());
        }

        for(String s : VALID_STRINGS) {
            try {
                State<Integer> endState = machine.runMachine(VALID_STRINGS[0]);
                assertSame(machine.getNullState(), endState);
            }
            catch(Exception e){
                fail("could not run machine on valid string: " + e.getMessage());
            }
        }
    }

    @Test
    public void testMachineRunOneState(){
        // simplest ready non-trivial machine: one state mapping to itself on one character c
        // starts and remains on state 1 iff string is regex \c*\
        // here c = '1' (testChar)
        char testChar = '1';
        String[] passingTestCases = {"", "1", "11", "111", "111111"};
        String[] failingTestCases = {"2", "3", "23", "312", "1221331"};

        FSM<Integer> machine = new FSM<>(ALPHABET);
        assertTrue(machine.isInAlphabet(testChar));

        // Check all strings are valid. They should be
        for(String passing : passingTestCases){
            assertTrue(machine.isStringOfAlphabet(passing));
        }
        for(String failing : failingTestCases){
            assertTrue(machine.isStringOfAlphabet(failing));
        }

        State<Integer> passingState = new State<>(NAMES[0], true, 0);

        // simplest ready non-trivial machine: one state mapping to itself on one character c
        // starts and remains on state 1 iff string is regex \c*\
        try{
            machine.incorporateNewState(passingState);
            machine.setInitState(passingState);
            machine.setTransition(passingState, ALPHABET[0], passingState);
            assertTrue(machine.isReady());
        }
        catch(Exception e){
            fail("could not incorporate passing state in valid machine: " + e.getMessage());
        }

        for(String s : passingTestCases) {
            try {
                State<Integer> endState = machine.runMachine(s);
                assertSame(passingState, endState);
            }
            catch(Exception e){
                fail("could not run machine on valid string: " + e.getMessage());
            }
        }

        for(String s : failingTestCases) {
            try {
                State<Integer> endState = machine.runMachine(s);
                assertSame(machine.getNullState(), endState);
            }
            catch(Exception e){
                fail("could not run machine on valid string: " + e.getMessage());
            }
        }
    }
}
