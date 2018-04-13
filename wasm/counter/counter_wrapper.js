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

  function getTotalVal() {
    return math.format(Module.getTotalVal(), {notation: 'fixed'})
  ;
  }

  function setVal(_val) {
  var value = math.eval(_val);

  if (value === undefined) {
    throw "val: Could not evaluate input";
  }

  //check type
  if (math.typeof(value) !== "number") {
    throw "val: Expected type number";
  }
      var value_num = value;
    Module.setVal(value_num);
}

