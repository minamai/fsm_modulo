package finite.automaton.state;

public class State<E> {

    private final String name;

    // whether this state is a final state or not
    private boolean finality = false;

    // the value that gets returned should this be
    // can be initialized to null
    // can still be useful for debugging purposes
    private E value = null;


    ////////////////////////
    // constructors

    public State(String name, boolean finality, E value){
        this.name = name;
        this.finality = finality;
        this.value = value;
    }

    public State(String name, boolean finality){
        this.name = name;
        this.finality = finality;
    }

    public State(String name){
        this.name = name;
    }


    //////////////
    // getters

    public String getName(){
        return name;
    }

    public boolean isFinal(){
        return finality;
    }

    public E getValue(){
        return value;
    }

    // getter that considers finality for state value
    // returns null if not final
    public E getStateResult(){
        return finality ? value : null;
    }


    //////////////
    // setters

    public void setFinality(boolean finality){
        this.finality = finality;
    }

    public void setValue(E newValue){
        this.value = newValue;
    }

    // sets value to true
    // TODO: if result is null, should this still be final?
    public void setResult(E result){
        finality = true;
        value = result;
    }

}
