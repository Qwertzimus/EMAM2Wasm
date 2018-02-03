package de.monticore.lang.monticar.emscripten;

import static de.monticore.lang.monticar.contract.Precondition.requiresNotNull;
import static de.monticore.lang.monticar.contract.Precondition.requiresNotNullNoNulls;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class helps building emscripten compile commands. Parameters are typed as much as possible.
 * However, they are by no means complete. <p>
 * At a minimum, it is required to call {@link #setEmscripten(String)} and {@link #setFile(Path)}.
 */
public class EmscriptenCommandBuilder implements CommandBuilder {

  private String emscripten;
  private Path file;
  private final List<Path> includes = new ArrayList<>();
  private final List<Option> options = new ArrayList<>();
  private Optimization optimizationLevel;
  private boolean bind;
  private String std;
  private String outputFile;

  /**
   * Sets the command calling emscripten if invoked from a terminal on the current operating
   * system.
   *
   * @param emscripten emscripten command
   */
  public EmscriptenCommandBuilder setEmscripten(String emscripten) {
    this.emscripten = emscripten;
    return this;
  }

  /**
   * Sets the main C++ file that is to be compiled by emscripten.
   *
   * @param file main C++ file
   */
  public EmscriptenCommandBuilder setFile(Path file) {
    this.file = file;
    return this;
  }

  /**
   * Tells emscripten to include the given path during compilation. The actual command will look
   * like {@code -I"path"}.
   *
   * @param include directory with additional sources to be included during compilation
   * @return this builder
   */
  public EmscriptenCommandBuilder include(Path include) {
    includes.add(include);
    return this;
  }

  /**
   * Adds an {@link Option} to the command. The actual command will look like {@code -s option=1}.
   *
   * @param option compile option
   * @return this builder
   */
  public EmscriptenCommandBuilder addOption(Option option) {
    options.add(option);
    return this;
  }

  /**
   * Sets the optimization level. By default, no optimization is performed.
   *
   * @param level optimization level
   * @return this builder
   * @see Optimization
   */
  public EmscriptenCommandBuilder setOptimization(Optimization level) {
    this.optimizationLevel = level;
    return this;
  }

  /**
   * Activates embind by adding {@code --bind} switch.
   *
   * @param bind if {@code true}, activates embind
   * @return this builder
   * @see <a href="https://kripken.github.io/emscripten-site/docs/porting/connecting_cpp_and_javascript/embind.html">https://kripken.github.io/emscripten-site/docs/porting/connecting_cpp_and_javascript/embind.html</a>
   */
  public EmscriptenCommandBuilder setBind(boolean bind) {
    this.bind = bind;
    return this;
  }

  /**
   * Specifies which compiler to use, e.g. {@code c++11}. The actual command will look like
   * {@code -std="std"}.
   *
   * @param std C/C++ compiler to be used for compilation
   * @return this builder
   */
  public EmscriptenCommandBuilder setStd(String std) {
    this.std = std;
    return this;
  }

  /**
   * Specifies the name of the output file. This <b>cannot</b> be a path, it has to be a filename
   * with a filename extension. E.g. "module.js".
   * Therefore, the shell has to be opened at the designated target output path.
   *
   * @param outputFile output filename
   * @return this builder
   */
  public EmscriptenCommandBuilder setOutput(String outputFile) {
    this.outputFile = outputFile;
    return this;
  }

  /**
   * Returns a command list with the following order:
   * <ol>
   * <li>emscripten binary</li>
   * <li>C++ file</li>
   * <li>Output file name, if present</li>
   * <li>Additional folders to include for compilation, if present</li>
   * <li>Options, if present</li>
   * <li>Optimization level, if present</li>
   * <li>Bind option, if present</li>
   * <li>std option, if present</li>
   * </ol>
   *
   * @return command list
   */
  @Override
  public List<String> toList() {
    checkParameters();
    List<String> list = new ArrayList<>();
    list.add(emscripten);
    list.add(file.toString());
    if (outputFile != null) {
      list.add("-o");
      list.add(outputFile);
    }
    includes.stream().map(path -> "-I\"" + path.toString() + "\"").forEach(list::add);
    options.stream().map(Option::toString).forEach(s -> {
      list.add("-s");
      list.add(s);
    });
    if (optimizationLevel != null) {
      list.add(optimizationLevel.toString());
    }
    if (bind) {
      list.add("--bind");
    }
    if (std != null) {
      list.add("-std=" + std);
    }
    return list;
  }

  private void checkParameters() {
    requiresNotNull(emscripten);
    requiresNotNull(file);
    requiresNotNullNoNulls(includes);
    requiresNotNullNoNulls(options);
  }

  @Override
  public String toString() {
    return toList().stream().collect(Collectors.joining(" "));
  }
}
