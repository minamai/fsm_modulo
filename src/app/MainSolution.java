package app;

import finite.automaton.FSM;
import finite.automaton.state.State;

import java.util.ArrayList;
import java.util.List;

public class MainSolution {
    final static int BASE = 2;
    final static int MODULO = 3;
    final static int UPPER_BOUND = 50;

    public static void main(String[] args){

        System.out.println("Testing module FSM for base " + 2 + " and modulo" + 3);
        System.out.println("Testing all numbers from 0 to " + UPPER_BOUND);
        System.out.println("Setting up machine...");

        // generate alphabet
        char[] alphabet = new char[BASE];
        for(int i = 0; i < BASE; i++){
            alphabet[i] = (char)('0' + i);
        }

        FSM<Integer> mod3BinaryMachine = new FSM<>(alphabet);

        // generate list of states
        List<State<Integer>> stateList = new ArrayList<>();

        for(int i = 0; i <MODULO; i++){
            stateList.add(mod3BinaryMachine.setNewFinalState("S" + i, i));
        }

        try {
            for (int mod = 0; mod < MODULO; mod++) {
                for (int digit = 0; digit < BASE; digit++) {
                    // idea: concatenating is adding digit to end
                    // in numbers that is: (mod | digit) -> mod * BASE + digit
                    mod3BinaryMachine.setTransition(stateList.get(mod), alphabet[digit],
                            stateList.get((mod * BASE + digit) % MODULO) );
                }
            }

            mod3BinaryMachine.setInitState(stateList.get(0));
            System.out.println("Set up machine. Processing cases...");
        }
        catch(Exception e){
            System.out.println("Caught exception. Aborting...");
            return;
        }

        try {
            List<Integer> failedCases = new ArrayList<>();

            // try every integer as a string up to upper bound
            for (int num = 0; num <= UPPER_BOUND; num++) {
                String numString = Integer.toUnsignedString(num, BASE);
                int expected = num % MODULO;

                State<Integer> output = mod3BinaryMachine.runMachine(numString);
                if (output == null || output.getStateResult() == null) {
                    System.out.println("\t- Case " + num + " with string \"" + numString + "\" outputted nothing");
                    failedCases.add(num);
                    break;
                }
                int value = output.getStateResult();
                if(value != expected){
                    failedCases.add(num);
                    System.out.println("\t- Case " + num + " outputted " + value);
                    break;
                }
            }

            System.out.println("Failed in " + failedCases.size() + " cases.");
        }
        catch(Exception e){
            System.out.println("Failed to process all test cases. Aborting...");
        }
    }
}
