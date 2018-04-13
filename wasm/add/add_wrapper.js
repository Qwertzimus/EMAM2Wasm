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

  function getSum() {
    return math.format(Module.getSum(), {notation: 'fixed'})
  ;
  }

  function setIn1(_in1) {
  var value = math.eval(_in1);

  if (value === undefined) {
    throw "in1: Could not evaluate input";
  }

  //check type
  if (math.typeof(value) !== "number") {
    throw "in1: Expected type number";
  }
      var value_num = value;
    Module.setIn1(value_num);
}
  function setIn2(_in2) {
  var value = math.eval(_in2);

  if (value === undefined) {
    throw "in2: Could not evaluate input";
  }

  //check type
  if (math.typeof(value) !== "number") {
    throw "in2: Expected type number";
  }
      var value_num = value;
    Module.setIn2(value_num);
}

