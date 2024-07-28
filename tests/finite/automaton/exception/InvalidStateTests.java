package finite.automaton.exception;

import finite.automaton.FSM;
import finite.automaton.exceptions.InvalidStateException;
import finite.automaton.state.State;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertThrows;

public class InvalidStateTests extends TestCase {

    private static final char[] ALPHABET = {'0', '1', '2'};
    private static final String[] NAMES = {"S0", "S1", "S2", "S3", "S4"};


    //////////////
    // per-test params and setup

    private static FSM<Integer> machine;
    private static State<Integer> nullState;
    private static State<Integer> inState;
    private static State<Integer> outState;

    // when you work, you will be used
    //@Before
    public static void setupDummyFSM(){
        machine = new FSM<>(ALPHABET);
        nullState = machine.getNullState();
        inState = machine.addNewState(NAMES[0]);
        outState = new State<>(NAMES[1]);
    }


    //////////////
    // setters & field constructors

    private void testInvalidStateStartingTransition(State<Integer> invalid){
        setupDummyFSM();

        // failure to get before setting:
        assertThrows("Attempting to transition from state not in FSM.",
                InvalidStateException.class,
                () -> machine.getTransition(invalid, ALPHABET[0]));

        // failure to set:
        assertThrows("Attempting to set transition from state not in FSM.",
                InvalidStateException.class,
                () -> machine.setTransition(invalid, ALPHABET[0], inState));

        // failure to get after setting:
        // can happen if user catches and insists on continuing
        assertThrows("Attempting to transition from state not in FSM.",
                InvalidStateException.class,
                () -> machine.getTransition(invalid, ALPHABET[0]));
    }

    private void testInvalidStateEndingTransition(State<Integer> invalid){
        setupDummyFSM();

        // failure to set:
        assertThrows("Attempting to set transition to state not in FSM.",
                InvalidStateException.class,
                () -> machine.setTransition(inState, ALPHABET[0], invalid));

        // failure to get after setting:
        // can happen if user catches and insists on continuing
        try {
            assertSame(nullState, machine.getTransition(inState, ALPHABET[0]));
        }
        catch(Exception e){
            fail("Failed to get null transitions from state: " + e.getMessage());
        }
    }


    //////////////
    // test cases

    @Test
    public void testSetInvalidStateAsInitial(){
        setupDummyFSM();
        assertThrows("Attempting to set state not in FSM to initial state.",
                InvalidStateException.class, () -> machine.setInitState(outState));
    }

    @Test
    public void testExternalStateStartingTransition(){
        testInvalidStateStartingTransition(outState);
    }

    @Test
    public void testExternalStateEndingTransition(){
        testInvalidStateEndingTransition(outState);
    }

    @Test
    public void testNullPointerStartingTransition(){
        testInvalidStateStartingTransition(null);
    }

    @Test
    public void testNullPointerEndingTransition(){
        testInvalidStateEndingTransition(null);
    }
}
