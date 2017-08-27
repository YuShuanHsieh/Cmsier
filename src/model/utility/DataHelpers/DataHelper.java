package model.utility.DataHelpers;

public interface DataHelper<T> {
  
  public T read(String name);
  
  public Boolean delete(String name);
  
  public Boolean write(T setting);
}
