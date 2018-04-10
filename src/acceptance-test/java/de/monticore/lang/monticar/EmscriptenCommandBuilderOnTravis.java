package de.monticore.lang.monticar;

import static de.monticore.lang.monticar.contract.Precondition.requiresNotNull;

import de.monticore.lang.monticar.emscripten.Emscripten;
import de.monticore.lang.monticar.emscripten.EmscriptenCommandBuilder;
import de.monticore.lang.monticar.emscripten.Optimization;
import de.monticore.lang.monticar.emscripten.Option;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EmscriptenCommandBuilderOnTravis extends EmscriptenCommandBuilder {

  private final List<Path> includes = new ArrayList<>();
  private final List<Path> libraries = new ArrayList<>();
  private final List<Option> options = new ArrayList<>();
  private final List<String> flags = new ArrayList<>();
  private Path referenceDir;
  private Emscripten emscripten;
  private Path file;
  private Optimization optimizationLevel;
  private boolean bind;
  private String outputFile;

  private static Path relativize(Path base, Path other) {
    return normalize(base).relativize(normalize(other)).normalize();
  }

  private static Path normalize(Path path) {
    return path.toAbsolutePath().normalize();
  }

  public EmscriptenCommandBuilder setReferenceOutputDir(Path outputDir) {
    this.referenceDir = requiresNotNull(outputDir);
    return this;
  }

  public EmscriptenCommandBuilder setEmscripten(Emscripten emscripten) {
    this.emscripten = requiresNotNull(emscripten);
    return this;
  }

  public EmscriptenCommandBuilder setFile(Path file) {
    this.file = requiresNotNull(file);
    return this;
  }

  public EmscriptenCommandBuilder include(Path include) {
    includes.add(requiresNotNull(include));
    return this;
  }

  public EmscriptenCommandBuilder addLibrary(Path library) {
    libraries.add(requiresNotNull(library));
    return this;
  }

  public EmscriptenCommandBuilder addOption(Option option) {
    options.add(requiresNotNull(option));
    return this;
  }

  public EmscriptenCommandBuilder addFlag(String flag) {
    flags.add(flag);
    return this;
  }

  public EmscriptenCommandBuilder setOptimization(Optimization level) {
    this.optimizationLevel = requiresNotNull(level);
    return this;
  }

  public EmscriptenCommandBuilder setBind(boolean bind) {
    this.bind = bind;
    return this;
  }

  public EmscriptenCommandBuilder setOutput(String outputFile) {
    this.outputFile = requiresNotNull(outputFile);
    return this;
  }

  @Override
  public List<String> toList() {
    checkParameters();

    List<String> options = new ArrayList<>();
    options.add(file());
    options.addAll(outputFile());
    options.addAll(includes());
    options.addAll(libraries());
    options.addAll(options());
    options.addAll(flags());
    if (optimizationLevel != null) {
      options.add(optimizationLevel.toString());
    }
    if (bind) {
      options.add("--bind");
    }
    String[] command = emscripten.getCommand(options.toArray(new String[]{}));

    return Arrays.asList(command);
  }

  @Override
  public String toString() {
    if (emscripten == null || file == null) {
      return "";
    }
    return toList().stream().collect(Collectors.joining(" "));
  }

  private String file() {
    return referenceDir != null ? relativize(referenceDir, file).toString()
        : file.toString();
  }

  private List<String> outputFile() {
    return Arrays.asList(outputFile != null ? new String[]{"-o", outputFile} : new String[]{});
  }

  private void checkParameters() {
    requiresNotNull(emscripten);
    requiresNotNull(file);
  }

  private List<String> includes() {
    return includes.stream()
        .map(path -> referenceDir != null ? relativize(referenceDir, path) : path)
        .map(path -> "-I\"" + path.toString() + "\"").collect(Collectors.toList());
  }

  private List<String> libraries() {
    return libraries.stream()
        .map(path -> referenceDir != null ? relativize(referenceDir, path) : path)
        .map(path -> "-L\"" + path.toString() + "\"").collect(Collectors.toList());
  }

  private List<String> options() {
    List<String> res = new ArrayList<>();
    options.stream().map(Option::toString).forEach(s -> {
      res.add("-s");
      res.add(s);
    });
    return res;
  }

  private List<String> flags() {
    return flags.stream().map(flag -> '-' + flag).collect(Collectors.toList());
  }

}
