package finite.automaton.template;

import finite.automaton.state.State;
import finite.automaton.template.MachineTester;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class BasicMachinesTesterTests extends TestCase {

    char[] ALPHABET = {'1', '2', '3'};

    //////////////
    // run tests

    @Test
    public void testMachineRunOneState(){
        // simplest ready non-trivial machine: one state mapping to itself on one character c
        // starts and remains on state 1 iff string is regex \c*\
        // here c = '1' (testChar)
        char testChar = '1';
        String[] passingTestCases = {"", "1", "11", "111", "111111"};
        String[] failingTestCases = {"2", "3", "23", "312", "1221331"};

        // make test cases
        List<MachineTester.TestCase> testCases = new ArrayList<>();
        for(String s : passingTestCases){
            testCases.add(new MachineTester.TestCase(s, 1));
        }
        for(String s : failingTestCases){
            testCases.add(new MachineTester.TestCase(s, null));
        }

        // simplest ready non-trivial machine: one state mapping to itself on one character c
        // starts and remains on state 1 iff string is regex \c*\
        // here, output Integer(1) is success, null is failure

        State<Integer> passingState = new State<>("pass", true, 1);

        MachineTester.Transition[] transitions = {
                new MachineTester.Transition(passingState, testChar, passingState)
        };

        MachineTester tester = MachineTester.makeMachineTester(ALPHABET, List.of(passingState),
                transitions, testCases);

        try {
            assertTrue(tester.runTests());
        }
        catch(Exception e){
            fail("Some test case failed: " + e.getMessage());
        }
    }
}
