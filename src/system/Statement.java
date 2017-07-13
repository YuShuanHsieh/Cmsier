package system;

public class Statement<T> {

  private final boolean result;
  private final T value;
  
  private Statement(boolean result, T value){
    this.result = result;
    this.value = value;
  }
  
  public static <U> Statement<U> success(U value){
    return new Statement<U>(true, value);
  }
  
  public boolean getResult(){
    return this.result;
  }
  
  public T getValue(){
    return this.value;
  }
  
}
