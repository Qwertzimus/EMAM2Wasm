var Module = {
  'print': function (text) { console.log('stdout: ' + text) },
  'printErr': function (text) { console.log('stderr: ' + text) },
  onRuntimeInitialized: function () { Module.init(); }
};

function init() {
  Module.init();
}

function execute() {
  Module.execute();
}

  function getOutScalar() {
    return math.format(Module.getOutScalar(), {notation: 'fixed'})
  .concat(" m/s");
  }

  function setInScalar(_inScalar) {
  var value = math.eval(_inScalar);
      var lower = math.eval("-10/3 m/s").toSI().toNumber();
      var upper = math.eval("10/1 km/h").toSI().toNumber();

  if (value === undefined) {
    throw "inScalar: Could not evaluate input";
  }

  //check type
  if (math.typeof(value) !== "Unit") {
    throw "inScalar: Expected type Unit";
  }
    //check unit
    var expectedUnit = math.eval("m/s");
    if (math.typeof(expectedUnit) !== math.typeof(value) || !expectedUnit.equalBase(value)) {
      throw "inScalar: Expected unit m/s";
    }
      var value_num = value.toSI().toNumber();
    //check range
  if (math.smaller(value_num, lower)) {
        throw "inScalar: Value " + value_num + " out of range";
      }
  if (math.larger(value_num, upper)) {
        throw "inScalar: Value " + value_num + " out of range";
      }
    Module.setInScalar(value_num);
}

