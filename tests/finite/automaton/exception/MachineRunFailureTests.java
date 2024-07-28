package finite.automaton.exception;

import finite.automaton.FSM;
import finite.automaton.exceptions.IncompleteMachineException;
import finite.automaton.exceptions.InvalidCharacterException;
import finite.automaton.state.State;
import junit.framework.TestCase;
import org.junit.Test;

import static org.junit.Assert.assertThrows;


public class MachineRunFailureTests extends TestCase {

    char[] ALPHABET = {'1', '2', '3'};
    String[] VALID_STRINGS = {"", "1", "2", "3", "12", "13", "32", "22", "3211213", "2212323"};
    String[] INVALID_STRINGS = {"5", "a", "73", "233326", "4$", "32.23", "23-11"};
    char EXTERNAL = '0';

    String[] NAMES = {"S0", "S1", "S2"};


    //////////////
    // private helpers

    private void fillInMachine(FSM<Integer> machine, boolean makeReady){
        for(String name : NAMES){
            machine.setNewState(name);
        }

        // fill in all transitions to ensure no null states happen
        for(int i = 0; i < ALPHABET.length; i++) {
            char c = ALPHABET[i];
            for(String name : NAMES) {
                try {
                    machine.setTransition(machine.getStateByName(name), c,
                            machine.getStateByName(NAMES[i % NAMES.length]));
                }
                catch(Exception e){
                    fail("Could not fully initialize machine internals: " + e.getMessage());
                }
            }
        }

        if(makeReady){
            try{
            machine.setInitState(machine.getStateByName(NAMES[0]));
            }
            catch(Exception e){
                fail("Could not set machine init state: " + e.getMessage());
            }
        }
    }


    //////////////
    // not ready tests

    @Test
    public void testBlankMachineNotReady(){
        FSM<Integer> machine = new FSM<>(ALPHABET);
        assertFalse(machine.isReady());

        // should always check if machine is ready first

        assertThrows("Make sure all required FSM fields are filled in.",
                IncompleteMachineException.class,
                () -> machine.runMachine(VALID_STRINGS[0]));

        assertThrows("Make sure all required FSM fields are filled in.",
                IncompleteMachineException.class,
                () -> machine.runMachine(INVALID_STRINGS[0]));
    }

    @Test
    public void testMachineWithStatesNotReady(){
        FSM<Integer> machine = new FSM<>(ALPHABET);
        fillInMachine(machine, false);
        assertFalse(machine.isReady()); // repeat for good luck

        // should always check if machine is ready first

        assertThrows("Make sure all required FSM fields are filled in.",
                IncompleteMachineException.class,
                () -> machine.runMachine(VALID_STRINGS[0]));

        assertThrows("Make sure all required FSM fields are filled in.",
                IncompleteMachineException.class,
                () -> machine.runMachine(INVALID_STRINGS[0]));
    }


    //////////////
    // strings tests

    @Test
    public void testBlankMachineFailsWithInvalidStrings(){
        FSM<Integer> machine = new FSM<>(ALPHABET);
        try {
            machine.setInitState(machine.getNullState());
        }
        catch(Exception e){
            fail("Could not ready blank machine");
        }

        assertTrue(machine.isReady()); // repeat for good luck

        // should always check if machine is ready first
        for(String s : INVALID_STRINGS){
            assertFalse(machine.isStringOfAlphabet(s));
            assertThrows("Character in data is not in alphabet.",
                    InvalidCharacterException.class,
                    () -> machine.runMachine(s));
        }
    }

    @Test
    public void testBlankMachinePassesWithValidStrings(){
        FSM<Integer> machine = new FSM<>(ALPHABET);
        try {
            machine.setInitState(machine.getNullState());
        }
        catch(Exception e){
            fail("Could not ready blank machine");
        }

        assertTrue(machine.isReady()); // repeat for good luck

        // should always check if machine is ready first
        for(String s : VALID_STRINGS){
            assertTrue(machine.isStringOfAlphabet(s));
            try{
                State<Integer> endState = machine.runMachine(s);
                // blank machine: always outputs null state
                assertSame(machine.getNullState(), endState);
                assertTrue(machine.containsState(endState));
            }
            catch(Exception e){
                fail("Could not run machine on valid text \"" + s + "\": " + e.getMessage());
            }
        }
    }

    @Test
    public void testFullMachineFailsWithInvalidStrings(){
        FSM<Integer> machine = new FSM<>(ALPHABET);
        fillInMachine(machine, true);

        assertTrue(machine.isReady()); // repeat for good luck

        // should always check if machine is ready first
        for(String s : INVALID_STRINGS){
            assertFalse(machine.isStringOfAlphabet(s));
            assertThrows("Character in data is not in alphabet.",
                    InvalidCharacterException.class,
                    () -> machine.runMachine(s));
        }
    }

    @Test
    public void testFullMachinePassesWithValidStrings(){
        FSM<Integer> machine = new FSM<>(ALPHABET);
        fillInMachine(machine, true);

        assertTrue(machine.isReady()); // repeat for good luck

        // should always check if machine is ready first
        for(String s : VALID_STRINGS){
            assertTrue(machine.isStringOfAlphabet(s));
            try{
                State<Integer> endState = machine.runMachine(s);
                assertNotNull(endState);
                assertTrue(machine.containsState(endState));
            }
            catch(Exception e){
                fail("Could not run machine on valid text \"" + s + "\": " + e.getMessage());
            }
        }
    }

}
