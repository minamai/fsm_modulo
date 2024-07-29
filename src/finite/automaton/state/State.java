package finite.automaton.state;

/**
 * A state of a Finite State Machine (FSM), containing parameters for name, value
 * and whether or not it is a valid final state. Note that the name is a constant.
 *
 * @param <E> The type of the result should the state be the ending final state.
 */
public class State<E> {

    /**
     * Field used to give the state an identifying name.
     */
    private final String name;

    /**
     * Field for denoting whether the state is a valid final state. Defaults to false.
     */
    private boolean finality = false;

    /**
     * Field for the value of the node, which can be set even if the field is not final
     */
    private E value = null;


    ////////////////////////
    // constructors

    /**
     * Constructs a state with the provided parameters.
     * @param name The identifying name of the state
     * @param finality Whether the state is a final state for its FSM
     * @param value The resultant value of the state if it is final.
     *              It can be set even for non-final states.
     */
    public State(String name, boolean finality, E value){
        this.name = name;
        this.finality = finality;
        this.value = value;
    }

    /**
     * Constructs a state with a null value.
     * @param name The identifying name of the state.
     * @param finality Whether the state is a final state for its FSM.
     */
    public State(String name, boolean finality){
        this.name = name;
        this.finality = finality;
    }

    /**
     * Constructs a non-final state with a null value.
     * @param name The identifying name of the state.
     */
    public State(String name){
        this.name = name;
    }


    //////////////
    // getters

    /**
     * Returns the name of the state.
     * @return The name of the state.
     */
    public String getName(){
        return name;
    }


    /**
     * Returns whether the state is final.
     * @return Whether or not the state is final.
     */
    public boolean isFinal(){
        return finality;
    }

    /**
     * Returns the value of the state.
     * @return The value of the state.
     */
    public E getValue(){
        return value;
    }

    /**
     * Returns the resultant value of the state. This is <code>null</code> if this is not final.
     * @return The value of the state, or <code>null</code> if this state is not final.
     */
    public E getStateResult(){
        return finality ? value : null;
    }


    //////////////
    // setters

    /**
     * Sets whether the state is final.
     * @param finality Whether the state should be final.
     */
    public void setFinality(boolean finality){
        this.finality = finality;
    }

    /**
     * Sets a new value for the state.
     * @param newValue The new value of the state.
     */
    public void setValue(E newValue){
        this.value = newValue;
    }

    /**
     * Makes the state final and sets a new value for the state. This new value then becomes
     * the new result of the state (fetched by <code>getResult()</code>
     *
     * @param result The new value of the state.
     */
    public void setResult(E result){
        // TODO: if result is null, should this still be final?
        finality = true;
        value = result;
    }

}
