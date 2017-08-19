package model.utility.DataHelpers;

public interface DataHelperBase<T> {
  
  public T read(String name);
  
  public Boolean delete(String name);
  
  public Boolean write(T setting);
}
