package de.monticore.lang.monticar.emam2wasm.cpp;

import de.monticore.symboltable.Symbol;
import org.springframework.stereotype.Component;

@Component
public class CppMainNameProvider implements CppNameProvider {

  private static final String CPP_FILE_EXTENSION = "cpp";

  @Override
  public String getName(Symbol model) {
    return model.getName();
  }

  @Override
  public String getFileExtension() {
    return CPP_FILE_EXTENSION;
  }
}
