package de.monticore.lang.monticar;

import de.monticore.lang.monticar.emscripten.Emscripten;
import de.monticore.lang.monticar.emscripten.EmscriptenCommandBuilder;
import de.monticore.lang.monticar.emscripten.EmscriptenCommandBuilderFactory;
import de.monticore.lang.monticar.emscripten.Optimization;
import de.monticore.lang.monticar.emscripten.Option;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class EmscriptenCommandBuilderFactoryOnTravis extends EmscriptenCommandBuilderFactory {

  private final List<Path> libraries = new ArrayList<>();
  private Emscripten emscripten;
  private Path file;
  private List<Path> includes = new ArrayList<>();
  private List<Option> options = new ArrayList<>();
  private List<String> flags = new ArrayList<>();
  private Optimization optimizationLevel;
  private Boolean bind;
  private String outputFile;

  public EmscriptenCommandBuilderFactory setEmscripten(Emscripten emscripten) {
    this.emscripten = emscripten;
    return this;
  }

  public EmscriptenCommandBuilderFactory setFile(Path file) {
    this.file = file;
    return this;
  }

  public EmscriptenCommandBuilderFactory include(Path include) {
    includes.add(include);
    return this;
  }

  public EmscriptenCommandBuilderFactory addLibrary(Path library) {
    libraries.add(library);
    return this;
  }

  public EmscriptenCommandBuilderFactory addOption(Option option) {
    options.add(option);
    return this;
  }

  public EmscriptenCommandBuilderFactory addFlag(String flag) {
    flags.add(flag);
    return this;
  }

  public EmscriptenCommandBuilderFactory setOptimization(Optimization level) {
    this.optimizationLevel = level;
    return this;
  }

  public EmscriptenCommandBuilderFactory setBind(boolean bind) {
    this.bind = bind;
    return this;
  }

  public EmscriptenCommandBuilderFactory setOutput(String outputFile) {
    this.outputFile = outputFile;
    return this;
  }

  public EmscriptenCommandBuilder getBuilder() {
    return new EmscriptenCommandBuilderFactoryOnTravis.DefaultValueEmscriptenCommandBuilder(
        emscripten, file, includes, libraries, options, optimizationLevel, bind, outputFile);
  }

  private class DefaultValueEmscriptenCommandBuilder extends EmscriptenCommandBuilderOnTravis {

    private final boolean emscriptenDefault;
    private final boolean fileDefault;
    private final boolean optimizationDefault;
    private final boolean bindDefault;
    private final boolean outputDefault;

    DefaultValueEmscriptenCommandBuilder(
        Emscripten emscripten,
        Path file,
        List<Path> includes,
        List<Path> libraries,
        List<Option> options,
        Optimization optimizationLevel,
        Boolean bind,
        String outputFile) {
      emscriptenDefault = emscripten != null;
      fileDefault = file != null;
      optimizationDefault = optimizationLevel != null;
      bindDefault = bind != null;
      outputDefault = outputFile != null;

      if (emscriptenDefault) {
        super.setEmscripten(emscripten);
      }
      if (fileDefault) {
        super.setFile(file);
      }
      if (optimizationDefault) {
        super.setOptimization(optimizationLevel);
      }
      if (bindDefault) {
        super.setBind(bind);
      }
      if (outputDefault) {
        super.setOutput(outputFile);
      }

      if (includes != null) {
        includes.forEach(super::include);
      }
      if (libraries != null) {
        libraries.forEach(super::addLibrary);
      }
      if (options != null) {
        options.forEach(super::addOption);
      }
      if (flags != null) {
        flags.forEach(super::addFlag);
      }
    }

    @Override
    public EmscriptenCommandBuilder setEmscripten(Emscripten emscripten) {
      if (emscriptenDefault) {
        throw new UnsupportedOperationException("Default parameter cannot be changed.");
      }
      return super.setEmscripten(emscripten);
    }

    @Override
    public EmscriptenCommandBuilder setFile(Path file) {
      if (fileDefault) {
        throw new UnsupportedOperationException("Default parameter cannot be changed.");
      }
      return super.setFile(file);
    }

    @Override
    public EmscriptenCommandBuilder setOptimization(Optimization level) {
      if (optimizationDefault) {
        throw new UnsupportedOperationException("Default parameter cannot be changed.");
      }
      return super.setOptimization(level);
    }

    @Override
    public EmscriptenCommandBuilder setBind(boolean bind) {
      if (bindDefault) {
        throw new UnsupportedOperationException("Default parameter cannot be changed.");
      }
      return super.setBind(bind);
    }

    @Override
    public EmscriptenCommandBuilder setOutput(String outputFile) {
      if (outputDefault) {
        throw new UnsupportedOperationException("Default parameter cannot be changed.");
      }
      return super.setOutput(outputFile);
    }

  }
}
