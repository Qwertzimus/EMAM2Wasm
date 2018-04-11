package de.monticore.lang.monticar.generator;

import static de.monticore.lang.monticar.generator.GeneratorUtil.filterMultipleArrayPorts;
import static de.monticore.lang.monticar.generator.GeneratorUtil.getDimension;
import static de.monticore.lang.monticar.generator.GeneratorUtil.getGetterMethodName;
import static de.monticore.lang.monticar.generator.GeneratorUtil.getSetterMethodName;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.ExpandedComponentInstanceSymbol;
import de.monticore.lang.embeddedmontiarc.embeddedmontiarc._symboltable.PortSymbol;
import de.monticore.lang.monticar.contract.Precondition.PreconditionViolationException;
import de.monticore.lang.monticar.resolver.Resolver;
import de.monticore.lang.monticar.resolver.ResolverFactory;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class GeneratorUtilTest {

  private static final Path RESOLVING_BASE_DIR = Paths.get("src/test/resources/generatorutil");
  private static final String NO_ARRAYS_MODEL = "models.noArrays";
  private static final String MULTIPLE_ARRAYS_MODEL = "models.multipleArrays";

  private static final PortSymbol NULL_PORT = null;
  private static final String NULL_STRING = null;
  private static final String EMPTY_STRING = "";
  private static final String LOWERCASE_SINGLE_CHARACTER = "a";
  private static final String UPPERCASE_SINGLE_CHARACTER = "A";
  private static final String FIRST_LOWERCASE_STRING = "aSdF";
  private static final String FIRST_UPPERCASE_STRING = "ASdF";
  private static final String ARRAY_NAME = "array[1]";
  private static final String ARRAY_BASE_NAME = "array";
  private static final String NOT_ARRAY_NAME = "somePort";

  private static final String IN = "in";
  private static final String OUT = "out";

  private static final String DIMENSION_MODEL = "models.dimensions";
  private static final String SCALAR = "Scalar";
  private static final String ARRAY = "Array[1]";
  private static final String ROW_VECTOR = "RowVector";
  private static final String COLUMN_VECTOR = "ColumnVector";
  private static final String MATRIX = "Matrix";
  private static final String MATRIX_ARRAY = "MatrixArray[2]";

  private static String portName(String direction, String name) {
    return direction + name;
  }

  @Nested
  class FilterMultipleArrayPorts {

    @Nested
    class WhenNoArraysPresent {

      private ExpandedComponentInstanceSymbol noArraysModel;

      @BeforeEach
      void setUp() {
        ResolverFactory resolverFactory = new ResolverFactory(RESOLVING_BASE_DIR);
        Resolver resolver = resolverFactory.get();
        noArraysModel = resolver.getExpandedComponentInstanceSymbol(NO_ARRAYS_MODEL);
      }

      @Nested
      class ShouldNotFilterAnyPorts {

        @Test
        void givenInports() {
          assertThat(filterMultipleArrayPorts(noArraysModel.getIncomingPorts()).stream()
              .map(PortSymbol::getName).collect(Collectors.toSet()))
              .containsExactlyInAnyOrder("in1", "in2");
        }

        @Test
        void givenOutports() {
          assertThat(filterMultipleArrayPorts(noArraysModel.getOutgoingPorts()).stream()
              .map(PortSymbol::getName).collect(Collectors.toSet()))
              .containsExactlyInAnyOrder("out1");
        }

        @Test
        void givenAllPorts() {
          assertThat(filterMultipleArrayPorts(noArraysModel.getPorts()).stream()
              .map(PortSymbol::getName).collect(Collectors.toSet()))
              .containsExactlyInAnyOrder("in1", "in2", "out1");
        }
      }
    }

    @Nested
    class WhenMultipleArraysPresent {

      private ExpandedComponentInstanceSymbol multipleArraysModel;

      @BeforeEach
      void setUp() {
        ResolverFactory resolverFactory = new ResolverFactory(RESOLVING_BASE_DIR);
        Resolver resolver = resolverFactory.get();
        multipleArraysModel = resolver.getExpandedComponentInstanceSymbol(MULTIPLE_ARRAYS_MODEL);
      }

      @Nested
      class ShouldFilterDuplicateArrayPorts {

        @Test
        void givenInports() {
          assertThat(filterMultipleArrayPorts(multipleArraysModel.getIncomingPorts()).stream()
              .map(PortSymbol::getNameWithoutArrayBracketPart).collect(Collectors.toList()))
              .containsExactlyInAnyOrder("scalar", "in1", "in2", "in3");
        }

        @Test
        void givenOutports() {
          assertThat(filterMultipleArrayPorts(multipleArraysModel.getOutgoingPorts()).stream()
              .map(PortSymbol::getNameWithoutArrayBracketPart).collect(Collectors.toList()))
              .containsExactlyInAnyOrder("out1", "out2");
        }

        @Test
        void givenAllPorts() {
          assertThat(filterMultipleArrayPorts(multipleArraysModel.getPorts()).stream()
              .map(PortSymbol::getNameWithoutArrayBracketPart).collect(Collectors.toList()))
              .containsExactlyInAnyOrder("scalar", "in1", "in2", "in3", "out1", "out2");
        }
      }
    }
  }

  @Nested
  class GetGetterMethodName {

    @Nested
    class ShouldThrowException {

      @Test
      void whenPortIsNull() {
        assertThatExceptionOfType(PreconditionViolationException.class)
            .isThrownBy(() -> getGetterMethodName(NULL_PORT));
      }

      @Test
      void whenPortNameIsNull() {
        assertThatExceptionOfType(PreconditionViolationException.class)
            .isThrownBy(() -> getGetterMethodName(NULL_STRING));
      }

      @Test
      void whenPortNameIsEmpty() {
        assertThatExceptionOfType(PreconditionViolationException.class)
            .isThrownBy(() -> getGetterMethodName(EMPTY_STRING));
      }
    }

    @Nested
    class ShouldCapitalizeName {

      @Test
      void whenGivenLowercaseSingleCharacter() {
        assertThat(getGetterMethodName(LOWERCASE_SINGLE_CHARACTER)).isEqualTo("getA");
      }

      @Test
      void whenGivenUppercaseSingleCharacter() {
        assertThat(getGetterMethodName(UPPERCASE_SINGLE_CHARACTER)).isEqualTo("getA");
      }

      @Test
      void whenGivenFirstLowercaseString() {
        assertThat(getGetterMethodName(FIRST_LOWERCASE_STRING)).isEqualTo("getASdF");
      }

      @Test
      void whenGivenFirstUppercaseString() {
        assertThat(getGetterMethodName(FIRST_UPPERCASE_STRING)).isEqualTo("getASdF");
      }
    }

    @Nested
    class ShouldReturnNameWithoutArrayBrackets {

      private PortSymbol arrayPort;
      private PortSymbol notArrayPort;


      @BeforeEach
      void setUp() {
        arrayPort = mock(PortSymbol.class);
        when(arrayPort.getName()).thenReturn(ARRAY_NAME);
        when(arrayPort.getNameWithoutArrayBracketPart()).thenReturn(ARRAY_BASE_NAME);

        notArrayPort = mock(PortSymbol.class);
        when(notArrayPort.getName()).thenReturn(NOT_ARRAY_NAME);
        when(notArrayPort.getNameWithoutArrayBracketPart()).thenReturn(NOT_ARRAY_NAME);
      }

      @Test
      void whenNotAnArrayName() {
        assertThat(getGetterMethodName(notArrayPort)).isEqualTo("getSomePort");
      }

      @Test
      void whenArrayName() {
        assertThat(getGetterMethodName(arrayPort)).isEqualTo("getArray");
      }
    }
  }

  @Nested
  class GetSetterMethodName {

    @Nested
    class ShouldThrowException {

      @Test
      void whenPortIsNull() {
        assertThatExceptionOfType(PreconditionViolationException.class)
            .isThrownBy(() -> getSetterMethodName(NULL_PORT));
      }

      @Test
      void whenPortNameIsNull() {
        assertThatExceptionOfType(PreconditionViolationException.class)
            .isThrownBy(() -> getSetterMethodName(NULL_STRING));
      }

      @Test
      void whenPortNameIsEmpty() {
        assertThatExceptionOfType(PreconditionViolationException.class)
            .isThrownBy(() -> getSetterMethodName(EMPTY_STRING));
      }
    }

    @Nested
    class ShouldCapitalizeName {

      @Test
      void whenGivenLowercaseSingleCharacter() {
        assertThat(getSetterMethodName(LOWERCASE_SINGLE_CHARACTER)).isEqualTo("setA");
      }

      @Test
      void whenGivenUppercaseSingleCharacter() {
        assertThat(getSetterMethodName(UPPERCASE_SINGLE_CHARACTER)).isEqualTo("setA");
      }

      @Test
      void whenGivenFirstLowercaseString() {
        assertThat(getSetterMethodName(FIRST_LOWERCASE_STRING)).isEqualTo("setASdF");
      }

      @Test
      void whenGivenFirstUppercaseString() {
        assertThat(getSetterMethodName(FIRST_UPPERCASE_STRING)).isEqualTo("setASdF");
      }
    }

    @Nested
    class ShouldReturnNameWithoutArrayBrackets {

      private PortSymbol arrayPort;
      private PortSymbol notArrayPort;


      @BeforeEach
      void setUp() {
        arrayPort = mock(PortSymbol.class);
        when(arrayPort.getName()).thenReturn(ARRAY_NAME);
        when(arrayPort.getNameWithoutArrayBracketPart()).thenReturn(ARRAY_BASE_NAME);

        notArrayPort = mock(PortSymbol.class);
        when(notArrayPort.getName()).thenReturn(NOT_ARRAY_NAME);
        when(notArrayPort.getNameWithoutArrayBracketPart()).thenReturn(NOT_ARRAY_NAME);
      }

      @Test
      void whenNotAnArrayName() {
        assertThat(getSetterMethodName(notArrayPort)).isEqualTo("setSomePort");
      }

      @Test
      void whenArrayName() {
        assertThat(getSetterMethodName(arrayPort)).isEqualTo("setArray");
      }
    }
  }

  @Nested
  class GetDimension {

    private ExpandedComponentInstanceSymbol dimensionModel;

    @BeforeEach
    void setUp() {
      ResolverFactory resolverFactory = new ResolverFactory(RESOLVING_BASE_DIR);
      Resolver resolver = resolverFactory.get();
      dimensionModel = resolver.getExpandedComponentInstanceSymbol(DIMENSION_MODEL);
    }

    @Nested
    class ShouldReturnEmptyArray {

      @Nested
      class GivenIncomingPort {

        @Test
        void whenScalar() {
          Collection<PortSymbol> ports = dimensionModel.getPorts();
          PortSymbol port = dimensionModel.getIncomingPort(portName(IN, SCALAR)).get();

          assertThat(getDimension(ports, port)).isEmpty();
        }
      }

      @Nested
      class GivenOutgoingPort {

        @Test
        void whenScalar() {
          Collection<PortSymbol> ports = dimensionModel.getPorts();
          PortSymbol port = dimensionModel.getOutgoingPort(portName(OUT, SCALAR)).get();

          assertThat(getDimension(ports, port)).isEmpty();
        }
      }
    }

    @Nested
    class ShouldReturnDimensionArray {

      @Nested
      class GivenIncomingPort {

        @Test
        void whenArray() {
          Collection<PortSymbol> ports = dimensionModel.getPorts();
          PortSymbol port = dimensionModel.getIncomingPort(portName(IN, ARRAY)).get();

          assertThat(getDimension(ports, port)).containsExactly("3");
        }

        @Test
        void whenRowVector() {
          Collection<PortSymbol> ports = dimensionModel.getPorts();
          PortSymbol port = dimensionModel.getIncomingPort(portName(IN, ROW_VECTOR)).get();

          assertThat(getDimension(ports, port)).containsExactly("1", "3");
        }

        @Test
        void whenColumnVector() {
          Collection<PortSymbol> ports = dimensionModel.getPorts();
          PortSymbol port = dimensionModel.getIncomingPort(portName(IN, COLUMN_VECTOR)).get();

          assertThat(getDimension(ports, port)).containsExactly("4", "1");
        }

        @Test
        void whenMatrix() {
          Collection<PortSymbol> ports = dimensionModel.getPorts();
          PortSymbol port = dimensionModel.getIncomingPort(portName(IN, MATRIX)).get();

          assertThat(getDimension(ports, port)).containsExactly("2", "3");
        }

        @Test
        void whenMatrixArray() {
          Collection<PortSymbol> ports = dimensionModel.getPorts();
          PortSymbol port = dimensionModel.getIncomingPort(portName(IN, MATRIX_ARRAY)).get();

          assertThat(getDimension(ports, port)).containsExactly("2", "3", "4");
        }
      }

      @Nested
      class GivenOutgoingPort {

        @Test
        void whenArray() {
          Collection<PortSymbol> ports = dimensionModel.getPorts();
          PortSymbol port = dimensionModel.getOutgoingPort(portName(OUT, ARRAY)).get();

          assertThat(getDimension(ports, port)).containsExactly("3");
        }

        @Test
        void whenRowVector() {
          Collection<PortSymbol> ports = dimensionModel.getPorts();
          PortSymbol port = dimensionModel.getOutgoingPort(portName(OUT, ROW_VECTOR)).get();

          assertThat(getDimension(ports, port)).containsExactly("1", "3");
        }

        @Test
        void whenColumnVector() {
          Collection<PortSymbol> ports = dimensionModel.getPorts();
          PortSymbol port = dimensionModel.getOutgoingPort(portName(OUT, COLUMN_VECTOR)).get();

          assertThat(getDimension(ports, port)).containsExactly("4", "1");
        }

        @Test
        void whenMatrix() {
          Collection<PortSymbol> ports = dimensionModel.getPorts();
          PortSymbol port = dimensionModel.getOutgoingPort(portName(OUT, MATRIX)).get();

          assertThat(getDimension(ports, port)).containsExactly("2", "3");
        }

        @Test
        void whenMatrixArray() {
          Collection<PortSymbol> ports = dimensionModel.getPorts();
          PortSymbol port = dimensionModel.getOutgoingPort(portName(OUT, MATRIX_ARRAY)).get();

          assertThat(getDimension(ports, port)).containsExactly("2", "3", "4");
        }
      }
    }
  }

}