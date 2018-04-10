package de.monticore.lang.monticar;

import static org.assertj.core.api.Assertions.assertThat;

import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.ExpandedComponentInstanceSymbol;
import de.monticore.lang.monticar.adapter.CppGeneratorAdapter;
import de.monticore.lang.monticar.adapter.EmamCppCompiler;
import de.monticore.lang.monticar.adapter.GeneratorCppWrapper;
import de.monticore.lang.monticar.emam2wasm.EmamWasmSingleDirectoryCompiler;
import de.monticore.lang.monticar.emam2wasm.cpp.CppMainNameProvider;
import de.monticore.lang.monticar.emam2wasm.cpp.CppStep;
import de.monticore.lang.monticar.emam2wasm.wasm.WasmJsNameProvider;
import de.monticore.lang.monticar.emam2wasm.wasm.WasmStep;
import de.monticore.lang.monticar.emam2wasm.web.HtmlGeneratorFactory;
import de.monticore.lang.monticar.emam2wasm.web.HtmlInterfaceNameProvider;
import de.monticore.lang.monticar.emam2wasm.web.JsGeneratorFactory;
import de.monticore.lang.monticar.emam2wasm.web.JsWrapperNameProvider;
import de.monticore.lang.monticar.emam2wasm.web.WebStep;
import de.monticore.lang.monticar.emscripten.Emscripten;
import de.monticore.lang.monticar.emscripten.EmscriptenCommand;
import de.monticore.lang.monticar.emscripten.EmscriptenCommandBuilderFactory;
import de.monticore.lang.monticar.emscripten.Shell;
import de.monticore.lang.monticar.freemarker.TemplateFactory;
import de.monticore.lang.monticar.generator.cpp.GeneratorCPP;
import de.monticore.lang.monticar.junit.TemporaryDirectoryExtension;
import de.monticore.lang.monticar.resolver.Resolver;
import de.monticore.lang.monticar.resolver.SymTabCreator;
import de.monticore.lang.monticar.setup.AutoSetup;
import de.monticore.lang.monticar.setup.BasicConfiguration;
import de.monticore.lang.monticar.setup.Configuration;
import de.monticore.lang.monticar.setup.action.DownloadAction;
import de.monticore.lang.monticar.setup.action.ExtractionAction;
import de.monticore.lang.monticar.setup.action.SetupAction;
import de.monticore.lang.tagging._symboltable.TaggingResolver;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Template;
import java.io.IOException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.ExtendWith;

class Emam2WasmAT {

  private static final Path RESOURCE_DIR = Paths.get("src/acceptance-test/resources");

  private static final Path EMSDK_WIN = Paths.get("./emsdk-master/emsdk.bat");
  private static final String EMSCRIPTEN_URL = "https://github.com/juj/emsdk/archive/master.zip";
  private static final String EMSCRIPTEN = "emscripten";
  private static final String EMSCRIPTEN_ARCHIVE_NAME = "emscripten.zip";
  private static final String EMCC_WIN = "emcc.bat";
  private static final String BRANCH = "emsdk-master";

  private static final String ARMADILLO_URL = "https://github.com/conradsnicta/armadillo-code/archive/8.400.x.zip";

  private static final JsWrapperNameProvider WRAPPER_NAME_PROVIDER = new JsWrapperNameProvider();
  private static final HtmlInterfaceNameProvider INTERFACE_NAME_PROVIDER = new HtmlInterfaceNameProvider();

  private static final String DOCKER_INSTALL_COMMAND =
      "docker run -dit --name emscripten -v $(pwd):/src trzeci/emscripten:sdk-incoming-64bit bash";
  private static final String DOCKER_EMSCRIPTEN_CALL = "docker exec -i emscripten emcc";
  private static final Path RESOLVING_BASE_DIR = RESOURCE_DIR;
  private static final String MODEL_NAME = "models.add";

  private static final Path CPP_TEMPLATE_DIR = Paths.get("src/main/resources/ftl/cpp");
  private static final String CPP_TEMPLATE = "cpp.ftl";
  private static final Path JS_TEMPLATE_DIR = Paths.get("src/main/resources/ftl/js");
  private static final String JS_TEMPLATE = "js.ftl";
  private static final Path HTML_TEMPLATE_DIR = Paths.get("src/main/resources/ftl/html");
  private static final String HTML_TEMPLATE = "html.ftl";
  private static final String CPP_DIR_NAME = "cpp";
  private static final String WASM_DIR_NAME = "wasm";
  private static final String WEB_DIR_NAME = "web";
  private static final String ARMADILLO_ARCHIVE_NAME = "armadillo.zip";
  private static final String ARMADILLO = "armadillo";
  private static final String ARMADILLO_INCLUDE = "armadillo-code-8.400.x/include";
  private static final String ARMADILLO_H = "armadillo.h";
  private static final String CPP11 = "std=c++11";

  private TemplateFactory setUpTemplateFactory() throws IOException {
    FileTemplateLoader cppFtl = new FileTemplateLoader(CPP_TEMPLATE_DIR.toFile());
    FileTemplateLoader jsFtl = new FileTemplateLoader(JS_TEMPLATE_DIR.toFile());
    FileTemplateLoader htmlFtl = new FileTemplateLoader(HTML_TEMPLATE_DIR.toFile());
    MultiTemplateLoader mtl = new MultiTemplateLoader(new TemplateLoader[]{cppFtl, jsFtl, htmlFtl});
    return new TemplateFactory(mtl);
  }

  private EmscriptenCommandBuilderFactory commandBuilderFactory(
      Emscripten emscripten, Path armadillo) {
    EmscriptenCommandBuilderFactory commandBuilderFactory = new EmscriptenCommandBuilderFactory();
    commandBuilderFactory.setEmscripten(emscripten);
    commandBuilderFactory.include(armadillo);
    commandBuilderFactory.addFlag(CPP11);
    return commandBuilderFactory;
  }

  private void createDirectories(Path... dirs) throws IOException {
    for (Path dir : dirs) {
      Files.createDirectories(dir);
    }
  }

  @SafeVarargs
  private final <T> List<T> listof(T... elements) {
    return Arrays.asList(elements);
  }

  @Nested
  @EnabledOnOs({OS.LINUX, OS.MAC})
  class WhenOnOtherOS {

    private SetupAction action;
    private Configuration configuration;
    private TaggingResolver symtab;
    private Template cppTemplate;
    private Template jsTemplate;
    private Template htmlTemplate;
    private ExpandedComponentInstanceSymbol model;
    private Path armadillo;
    private Path cppDir;
    private Path wasmDir;
    private Path webDir;


    @BeforeEach
    void setUp() throws IOException {
      this.action = new SetupAction(commands());
      this.configuration = new BasicConfiguration(action);
      this.symtab = new SymTabCreator(RESOLVING_BASE_DIR).createSymTabAndTaggingResolver();
      this.model = new Resolver(symtab).getExpandedComponentInstanceSymbol(MODEL_NAME);

      TemplateFactory templateFactory = setUpTemplateFactory();
      this.cppTemplate = templateFactory.getTemplate(CPP_TEMPLATE);
      this.jsTemplate = templateFactory.getTemplate(JS_TEMPLATE);
      this.htmlTemplate = templateFactory.getTemplate(HTML_TEMPLATE);
    }

    void setUp(Path dir) throws IOException {
      this.cppDir = dir.resolve(CPP_DIR_NAME);
      this.wasmDir = dir.resolve(WASM_DIR_NAME);
      this.webDir = dir.resolve(WEB_DIR_NAME);
      createDirectories(cppDir, wasmDir, webDir);

      Path armadilloDownloadPath = dir.resolve(ARMADILLO_ARCHIVE_NAME);
      Path armadilloExtractionPath = dir.resolve(ARMADILLO);
      URL armadilloURL = new URL(ARMADILLO_URL);
      Configuration armadilloConfiguration = new BasicConfiguration(
          new DownloadAction(armadilloURL, armadilloDownloadPath),
          new ExtractionAction(armadilloDownloadPath, armadilloExtractionPath));

      AutoSetup autoSetup = new AutoSetup(listof(armadilloConfiguration));
      autoSetup.setup();

      this.armadillo = armadilloExtractionPath.resolve(ARMADILLO_INCLUDE)
          .toAbsolutePath().normalize();
      Files.copy(RESOURCE_DIR.resolve(ARMADILLO_H), armadillo.resolve(ARMADILLO_H));
    }

    @Test
    @ExtendWith(TemporaryDirectoryExtension.class)
    void shouldSetupEmscripten(Path dir) throws IOException {
      setUp(dir);

      AutoSetup autoSetup = new AutoSetup(listof(configuration));

      autoSetup.setup();

      CppStep<ExpandedComponentInstanceSymbol> cppStep = new CppStep<>(new EmamCppCompiler(
          new GeneratorCppWrapper(new GeneratorCPP(), symtab,
              RESOLVING_BASE_DIR), new CppGeneratorAdapter(cppTemplate)),
          cppDir, new CppMainNameProvider());

      WasmStep wasmStep = new WasmStep(
          commandBuilderFactory(emscripten(DOCKER_EMSCRIPTEN_CALL), armadillo),
          wasmDir, new WasmJsNameProvider());

      WebStep webStep = new WebStep(new JsGeneratorFactory(jsTemplate),
          new HtmlGeneratorFactory(htmlTemplate), webDir, WRAPPER_NAME_PROVIDER,
          INTERFACE_NAME_PROVIDER);

      EmamWasmSingleDirectoryCompiler compiler = new EmamWasmSingleDirectoryCompiler(cppStep,
          wasmStep, webStep);

      Path webFile = compiler.emam2wasm(model);

      //TODO: Execute generated files in Javascript
      assertThat(webFile).exists();
    }

    private List<String[]> commands() {
      Shell shell = Shell.BASH;
      List<String[]> commands = new ArrayList<>();
      commands.add(new String[]{
          shell.getShellCommand(),
          shell.getExecuteSwitch(),
          DOCKER_INSTALL_COMMAND});
      return commands;
    }

    private Emscripten emscripten(String emscriptenCommand) {
      return new EmscriptenCommand(Shell.BASH, emscriptenCommand);
    }
  }

  @Disabled
  @Nested
  @EnabledOnOs({OS.WINDOWS})
  class WhenWindowsOS {

    private TaggingResolver symtab;
    private Template cppTemplate;
    private Template jsTemplate;
    private Template htmlTemplate;
    private ExpandedComponentInstanceSymbol model;
    private String emcc;
    private Path armadillo;
    private Path cppDir;
    private Path wasmDir;
    private Path webDir;

    @BeforeEach
    void setUp() throws IOException {
      this.symtab = new SymTabCreator(RESOLVING_BASE_DIR).createSymTabAndTaggingResolver();
      this.model = new Resolver(symtab).getExpandedComponentInstanceSymbol(MODEL_NAME);

      TemplateFactory templateFactory = setUpTemplateFactory();
      this.cppTemplate = templateFactory.getTemplate(CPP_TEMPLATE);
      this.jsTemplate = templateFactory.getTemplate(JS_TEMPLATE);
      this.htmlTemplate = templateFactory.getTemplate(HTML_TEMPLATE);
    }

    void setUp(Path dir) throws Exception {
      setUpEmscriptenAndArmadillo(dir);
      setUpDirectories(dir);
    }

    private void setUpDirectories(Path dir) throws IOException {
      this.cppDir = dir.resolve(CPP_DIR_NAME);
      this.wasmDir = dir.resolve(WASM_DIR_NAME);
      this.webDir = dir.resolve(WEB_DIR_NAME);
      createDirectories(cppDir, wasmDir, webDir);
    }

    private void setUpEmscriptenAndArmadillo(Path dir) throws Exception {
      Path downloadPath = dir.resolve(EMSCRIPTEN_ARCHIVE_NAME);
      Path extractionPath = dir.resolve(EMSCRIPTEN);
      URL emscriptenUrl = new URL(EMSCRIPTEN_URL);
      String emsdkCommand = "\"" + extractionPath.resolve(EMSDK_WIN)
          .toAbsolutePath().normalize().toString() + "\"";
      String[] emscriptenUpdate = new String[]{emsdkCommand, "update"};
      String[] emscriptenInstall = new String[]{emsdkCommand, "install", "latest"};
      String[] emscriptenActivate = new String[]{emsdkCommand, "activate", "latest"};
      Configuration emscriptenConfiguration = emscriptenWindowsConfig(emscriptenUrl, downloadPath,
          extractionPath, emscriptenWindowsCommands(emscriptenUpdate, emscriptenInstall,
              emscriptenActivate));

      Path armadilloDownloadPath = dir.resolve(ARMADILLO_ARCHIVE_NAME);
      Path armadilloExtractionPath = dir.resolve(ARMADILLO);
      URL armadilloURL = new URL(ARMADILLO_URL);
      Configuration armadilloConfiguration = new BasicConfiguration(
          new DownloadAction(armadilloURL, armadilloDownloadPath),
          new ExtractionAction(armadilloDownloadPath, armadilloExtractionPath));

      AutoSetup autoSetup = new AutoSetup(listof(emscriptenConfiguration, armadilloConfiguration));
      autoSetup.setup();

      this.armadillo = armadilloExtractionPath.resolve(ARMADILLO_INCLUDE)
          .toAbsolutePath().normalize();
      Files.copy(RESOURCE_DIR.resolve(ARMADILLO_H), armadillo.resolve(ARMADILLO_H));

      Path binaryDir = findSubDirectory(extractionPath.resolve(BRANCH).resolve(EMSCRIPTEN));
      this.emcc = binaryDir.resolve(EMCC_WIN).toAbsolutePath().normalize().toString();
    }

    @Test
    @ExtendWith(TemporaryDirectoryExtension.class)
    void shouldSetupEmscripten(Path dir) throws Exception {
      setUp(dir);

      CppStep<ExpandedComponentInstanceSymbol> cppStep = new CppStep<>(new EmamCppCompiler(
          new GeneratorCppWrapper(new GeneratorCPP(), symtab,
              RESOLVING_BASE_DIR), new CppGeneratorAdapter(cppTemplate)),
          cppDir, new CppMainNameProvider());

      WasmStep wasmStep = new WasmStep(commandBuilderFactory(emscripten(emcc), armadillo),
          wasmDir, new WasmJsNameProvider());

      WebStep webStep = new WebStep(new JsGeneratorFactory(jsTemplate),
          new HtmlGeneratorFactory(htmlTemplate), webDir, WRAPPER_NAME_PROVIDER,
          INTERFACE_NAME_PROVIDER);

      EmamWasmSingleDirectoryCompiler compiler = new EmamWasmSingleDirectoryCompiler(cppStep,
          wasmStep, webStep);

      Path webFile = compiler.emam2wasm(model);

      //TODO: Execute generated files in Javascript
      assertThat(webFile).exists();
    }

    private BasicConfiguration emscriptenWindowsConfig(URL emscriptenUrl, Path downloadPath,
        Path extractionPath, List<String[]> emscriptenWindowsCommands) {
      return new BasicConfiguration(
          new DownloadAction(emscriptenUrl, downloadPath),
          new ExtractionAction(downloadPath, extractionPath),
          new SetupAction(extractionPath, emscriptenWindowsCommands));
    }

    private List<String[]> emscriptenWindowsCommands(String[] emscriptenUpdate,
        String[] emscriptenInstall, String[] emscriptenActivate) {
      List<String[]> commands = new ArrayList<>();
      commands.add(emscriptenUpdate);
      commands.add(emscriptenInstall);
      commands.add(emscriptenActivate);
      return commands;
    }

    private Emscripten emscripten(String emscriptenCommand) {
      return new EmscriptenCommand(Shell.CMD, emscriptenCommand);
    }

    private Path findSubDirectory(Path path) throws IOException {
      try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
        for (Path p : stream) {
          if (Files.isDirectory(p)) {
            return p;
          }
        }
      }
      return null;
    }
  }
}
