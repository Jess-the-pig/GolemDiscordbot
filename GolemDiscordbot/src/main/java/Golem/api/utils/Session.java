package Golem.api.utils;

public class Session<T> {

  public int step;
  public T entity;
  public String lastField;

  public Session(int step, T entity, String lastField) {
    this.step = step;
    this.entity = entity;
    this.lastField = lastField;
  }

  public Session() {}
}
