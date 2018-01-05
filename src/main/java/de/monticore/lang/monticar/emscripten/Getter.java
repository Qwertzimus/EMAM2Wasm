package de.monticore.lang.monticar.emscripten;

public class Getter extends Method {

  private String variableName;

  public Getter(Datatype datatype, Type type, String methodName, String variableName) {
    super(datatype, type, methodName);
    this.variableName = variableName;
  }

  public String getVariableName() {
    return variableName;
  }

  public void setVariableName(String variableName) {
    this.variableName = variableName;
  }
}
