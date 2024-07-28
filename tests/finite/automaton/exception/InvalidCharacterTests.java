package finite.automaton.exception;

import finite.automaton.FSM;
import finite.automaton.exceptions.InvalidCharacterException;
import finite.automaton.state.State;
import junit.framework.TestCase;
import org.junit.Test;

import static org.junit.Assert.assertThrows;

public class InvalidCharacterTests extends TestCase {
    // note '0' is not here
    char[] ALPHABET = {'1', '2', '3', '4', '5'};
    String[] NAMES = {"hello", "goodbye"};

    @Test
    public void testInvalidStateStartingTransition(){
        FSM<Integer> machine = new FSM<>(ALPHABET);
        State<Integer> state1 = machine.setNewState(NAMES[0]);
        State<Integer> state2 = machine.setNewState(NAMES[1]);
        char out = '0'; // character not in alphabet

        // failure to get before setting:
        assertThrows("Attempting to transition using character not in alphabet.",
                InvalidCharacterException.class,
                () -> machine.getTransition(state1, out));

        // failure to set:
        assertThrows("Attempting to set transition using character not in alphabet.",
                InvalidCharacterException.class,
                () -> machine.setTransition(state1, out, state2));

        // failure to get after setting:
        // can happen if user catches and insists on continuing
        assertThrows("Attempting to transition using character not in alphabet.",
                InvalidCharacterException.class,
                () -> machine.getTransition(state1, out));
    }
}
