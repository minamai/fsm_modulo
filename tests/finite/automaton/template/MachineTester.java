package finite.automaton.template;

import finite.automaton.FSM;
import finite.automaton.exceptions.IncompleteMachineException;
import finite.automaton.exceptions.InvalidCharacterException;
import finite.automaton.state.State;

import java.util.List;
import java.util.Objects;

/**
 * Class that allows the definition of test machines quickly and clearly.
 * Usage:
 *      - pass in list of states
 *      - pass in list of transitions
 *      - pass in test cases
 *      - passing cases lead to final states
 *      - failing cases lead to non-final states (or the null state, which is non-final)
 */
public class MachineTester {

    public static class TestCase{
        protected final String input;
        protected final Integer output;

        public TestCase(String input, Integer output){
            this.input = input;
            this.output = output;
        }
    }

    public static class Transition{
        protected final State<Integer> initial;
        protected final char letter;
        protected final State<Integer> ender; // final is a keyword

        public Transition(State<Integer> initial, char letter, State<Integer> ender){
            this.initial = initial;
            this.letter = letter;
            this.ender = ender;
        }
    }

    private final FSM<Integer> machine;

    private final List<TestCase> testCases;


    ////////////////////////
    // constructors

    private MachineTester(FSM<Integer> machine, List<TestCase> testCases){
        this.machine = machine;
        this.testCases = testCases;
    }

    public static MachineTester makeMachineTester(char[] alphabet,
                          List<State<Integer>> states, Transition[] transitions,
                          List<TestCase> testCases){
        FSM<Integer> machine = new FSM<>(alphabet);

        // if any error happens, just return null
        // part of the reason this is only for testing
        try {
            for (State<Integer> state : states) {
                machine.incorporateNewState(state);
            }
            for(Transition trans : transitions){
                machine.setTransition(trans.initial, trans.letter, trans.ender);
            }

            if(states.isEmpty()){
                return null;
            }
            else{
                // first state is assumed to be initial.
                machine.setInitState(states.get(0));
            }
        }
        catch(Exception e){
            return null;
        }
        return new MachineTester(machine, testCases);
    }


    ////////////////////////
    // testing

    // caller handles exceptions
    public boolean runTests() throws InvalidCharacterException, IncompleteMachineException {
        boolean allPass = true;
        for(TestCase testCase : testCases){
            State<Integer> endState = machine.runMachine(testCase.input);
            // for null-safe equality check
            // we are comparing state results here
            allPass = allPass && Objects.equals(testCase.output,
                    endState.getStateResult());
        }
        return allPass;
    }

}
