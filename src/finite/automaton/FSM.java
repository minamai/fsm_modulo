package finite.automaton;

import finite.automaton.state.State;

import java.util.*;
import java.util.stream.Collectors;

// E is the output type
public class FSM<E> {

    // these exact types may not survive

    // canonical set of states of this FSM
    // all states used must be here
    private final Map<String, State<E>> states;

    private final char[] alphabet;
    private int alphaLength; // for easy access

    private State<E> initState;

    // for now a 2-tier map
    // decisions decisions (how to handle unset values: null state or null value?)
    private final Map<State<E>, Map<Character, State<E>>> transitionTable;


    //////////////
    // semi-static
    // true static variables aren't possible due to the nature of generics

    // the state that will be used with null-transitions
    // will not be added to transition table for size reasons
    // just shortcut to a null result
    // TODO: decide between the following options:
    /*   * NullState class that prevents re-setting nullState components
             ^ allow nullState to be publicly accessible then?
         * make sure user only sees null
         * allow user to change null state + document how to do that
             ^ advanced feature
     */
    private final State<E> nullState = new State<>(null);


    ////////////////////////
    // private helpers

    // private because alphabet is fundamental
    // changes are not a good idea
    private char[] setAlphabet(char[] alphabet){
        this.alphaLength = alphabet.length;
        //// alt version for non-final alphabet:
        // this.alphabet = alphabet;
        Arrays.sort(alphabet);
        return alphabet;
    }

    private void incorporateNewState(State<E> state){
        // add to states list
        // TODO: handle if names are not unique
        states.put(state.getName(), state);

        // add to transition table
        Map<Character, State<E>> stateTransitions= new HashMap<>();
        for(int i = 0; i < alphaLength; i++){
            stateTransitions.put(alphabet[i], null);
        }
        transitionTable.put(state, stateTransitions);
    }


    ////////////////////////
    // constructors

    public FSM(char[] alphabet){
        // this is a way to set final values while doing a helper function
        // final values cannot be set outside a
        // credit: https://stackoverflow.com/questions/11758440/initialize-final-variable-within-constructor-in-another-method
        this.alphabet = setAlphabet(alphabet);

        states = new HashMap<>();
        states.put(null, nullState);

        transitionTable = new HashMap<>();
    }


    //////////////
    // getters

    // for now we'll be lazy with the type
    public List<State<E>> getStates() {
        return new ArrayList<>(states.values());
    }

    public char[] getAlphabet() {
        return alphabet;
    }

    public State<E> getInitState(){
        return initState;
    }

    public State<E> getNullState(){
        return nullState;
    }

    public State<E> getStateByName(String name){
        // can this be improved?
        return states.get(name);
    }

    public State<E> getTransition(State<E> state, char c){
        // check that both state and character are in FSM
        if(! (states.containsValue(state) || Arrays.binarySearch(alphabet, c) < 0)){
            // TODO: determine if exception is good here.
            return null;
        }
        // if null state, return null state
        if(state.equals(nullState)){
            return nullState;
        }
        return transitionTable.get(state).get(c);
    }


    //////////////
    // setters & field constructors

    public void setInitState(State<E> state){
        if(states.containsValue(state)) {
            initState = state;
        }
        // TODO: throw exception
    }

    public State<E> addNewState(String name){
        State<E> state = new State<>(name);
        incorporateNewState(state);
        return state;
    }

    public State<E> addNewState(String name, E value){
        State<E> state = new State<>(name);
        incorporateNewState(state);
        return state;
    }

    public State<E> addNewFinalState(String name, E value){
        State<E> state = new State<>(name, true, value);
        incorporateNewState(state);
        return state;
    }

    public void setTransition(State<E> current, char c, State<E> next){
        // check that both states and character are in FSM
        if(! (states.containsValue(current) || states.containsValue(next)
            || Arrays.binarySearch(alphabet, c) < 0)){
            // TODO: determine if exception is good here.
            return;
        }
        // if null state, return null state
        if(current.equals(nullState) || next.equals(nullState)){
            return; // TODO: throw exception
        }
        transitionTable.get(current).put(c, next);
    }
}
