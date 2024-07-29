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

        FSM<Integer> mod3BinaryMachine = ModuloFSMFactory.makeModuloFSM(BASE, MODULO);
        if(mod3BinaryMachine != null){
            System.out.println("Set up machine. Processing cases...");
        }
        else{
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
