package finite.automaton;

import finite.automaton.state.State;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.Collection;

import static finite.automaton.state.StateValueChecker.checkValuelessStateNotFinal;
import static org.junit.Assert.assertArrayEquals;

public class FSMUnitTests extends TestCase {

    private final char[] ALPHABET = {'0', '1', '2'};
    private final int[] VALUES = {1, 2, 3, 4, 5};


    @Test
    public void testEmptyMachine(){
        // check initial fields
        FSM<Integer> machine = new FSM<>(ALPHABET);
        assertArrayEquals(ALPHABET, machine.getAlphabet());

        Collection<State<Integer>> states = machine.getStates();
        assertEquals(states.size(), 1);
        assertNull(machine.getInitState());
    }

    @Test
    public void testNullStateInNullMachine(){
        FSM<Integer> machine = new FSM<>(ALPHABET);
        // from first test, null state should be unique state
        State<Integer> nullState = machine.getNullState();

        // alt ways to get null value
        assertSame(nullState, machine.getStateByName(null));
        assertSame(nullState, machine.getStates().get(0)); // null state is only state here

        // expected outputs of null state
        checkValuelessStateNotFinal(nullState, null);

        for (char c : ALPHABET) {
            assertSame(nullState, machine.getTransition(nullState, c));
        }
    }
}