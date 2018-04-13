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

  function getEngine() {
    return math.format(Module.getEngine(), {notation: 'fixed'})
  ;
  }
  function getSteering() {
    return math.format(Module.getSteering(), {notation: 'fixed'})
  ;
  }
  function getBrakes() {
    return math.format(Module.getBrakes(), {notation: 'fixed'})
  ;
  }

  function setTimeIncrement(_timeIncrement) {
  var value = math.eval(_timeIncrement);
      var lower = math.eval("0/1 s").toSI().toNumber();
      var upper = math.eval("1/1 s").toSI().toNumber();

  if (value === undefined) {
    throw "timeIncrement: Could not evaluate input";
  }

  //check type
  if (math.typeof(value) !== "Unit") {
    throw "timeIncrement: Expected type Unit";
  }
    //check unit
    var expectedUnit = math.eval("s");
    if (math.typeof(expectedUnit) !== math.typeof(value) || !expectedUnit.equalBase(value)) {
      throw "timeIncrement: Expected unit s";
    }
      var value_num = value.toSI().toNumber();
    //check range
  if (math.smaller(value_num, lower)) {
        throw "timeIncrement: Value " + value_num + " out of range";
      }
  if (math.larger(value_num, upper)) {
        throw "timeIncrement: Value " + value_num + " out of range";
      }
    Module.setTimeIncrement(value_num);
}
  function setCurrentVelocity(_currentVelocity) {
  var value = math.eval(_currentVelocity);
      var lower = math.eval("0/1 m/s").toSI().toNumber();

  if (value === undefined) {
    throw "currentVelocity: Could not evaluate input";
  }

  //check type
  if (math.typeof(value) !== "Unit") {
    throw "currentVelocity: Expected type Unit";
  }
    //check unit
    var expectedUnit = math.eval("m/s");
    if (math.typeof(expectedUnit) !== math.typeof(value) || !expectedUnit.equalBase(value)) {
      throw "currentVelocity: Expected unit m/s";
    }
      var value_num = value.toSI().toNumber();
    //check range
  if (math.smaller(value_num, lower)) {
        throw "currentVelocity: Value " + value_num + " out of range";
      }
    Module.setCurrentVelocity(value_num);
}
  function setX(_x) {
  var value = math.eval(_x);

  if (value === undefined) {
    throw "x: Could not evaluate input";
  }

  //check type
  if (math.typeof(value) !== "Unit") {
    throw "x: Expected type Unit";
  }
    //check unit
    var expectedUnit = math.eval("m");
    if (math.typeof(expectedUnit) !== math.typeof(value) || !expectedUnit.equalBase(value)) {
      throw "x: Expected unit m";
    }
      var value_num = value.toSI().toNumber();
    Module.setX(value_num);
}
  function setY(_y) {
  var value = math.eval(_y);

  if (value === undefined) {
    throw "y: Could not evaluate input";
  }

  //check type
  if (math.typeof(value) !== "Unit") {
    throw "y: Expected type Unit";
  }
    //check unit
    var expectedUnit = math.eval("m");
    if (math.typeof(expectedUnit) !== math.typeof(value) || !expectedUnit.equalBase(value)) {
      throw "y: Expected unit m";
    }
      var value_num = value.toSI().toNumber();
    Module.setY(value_num);
}
  function setCompass(_compass) {
  var value = math.eval(_compass);

  if (value === undefined) {
    throw "compass: Could not evaluate input";
  }

  //check type
  if (math.typeof(value) !== "number") {
    throw "compass: Expected type number";
  }
      var value_num = value;
    Module.setCompass(value_num);
}
  function setCurrentEngine(_currentEngine) {
  var value = math.eval(_currentEngine);
      var lower = 0/1;
      var upper = 5/2;

  if (value === undefined) {
    throw "currentEngine: Could not evaluate input";
  }

  //check type
  if (math.typeof(value) !== "number") {
    throw "currentEngine: Expected type number";
  }
      var value_num = value;
    //check range
  if (math.smaller(value_num, lower)) {
        throw "currentEngine: Value " + value_num + " out of range";
      }
  if (math.larger(value_num, upper)) {
        throw "currentEngine: Value " + value_num + " out of range";
      }
    Module.setCurrentEngine(value_num);
}
  function setCurrentSteering(_currentSteering) {
  var value = math.eval(_currentSteering);
      var lower = -157/200;
      var upper = 157/200;

  if (value === undefined) {
    throw "currentSteering: Could not evaluate input";
  }

  //check type
  if (math.typeof(value) !== "number") {
    throw "currentSteering: Expected type number";
  }
      var value_num = value;
    //check range
  if (math.smaller(value_num, lower)) {
        throw "currentSteering: Value " + value_num + " out of range";
      }
  if (math.larger(value_num, upper)) {
        throw "currentSteering: Value " + value_num + " out of range";
      }
    Module.setCurrentSteering(value_num);
}
  function setCurrentBrakes(_currentBrakes) {
  var value = math.eval(_currentBrakes);
      var lower = 0/1;
      var upper = 3/1;

  if (value === undefined) {
    throw "currentBrakes: Could not evaluate input";
  }

  //check type
  if (math.typeof(value) !== "number") {
    throw "currentBrakes: Expected type number";
  }
      var value_num = value;
    //check range
  if (math.smaller(value_num, lower)) {
        throw "currentBrakes: Value " + value_num + " out of range";
      }
  if (math.larger(value_num, upper)) {
        throw "currentBrakes: Value " + value_num + " out of range";
      }
    Module.setCurrentBrakes(value_num);
}
  function setTrajectory_length(_trajectory_length) {
  var value = math.eval(_trajectory_length);
      var lower = 0/1;
      var upper = 100/1;

  if (value === undefined) {
    throw "trajectory_length: Could not evaluate input";
  }

  //check type
  if (math.typeof(value) !== "number") {
    throw "trajectory_length: Expected type number";
  }
      var value_num = value;
    //check range
  if (math.smaller(value_num, lower)) {
        throw "trajectory_length: Value " + value_num + " out of range";
      }
  if (math.larger(value_num, upper)) {
        throw "trajectory_length: Value " + value_num + " out of range";
      }
    Module.setTrajectory_length(value_num);
}
  function setTrajectory_x(_trajectory_x) {
  var value = math.eval(_trajectory_x);

  if (value === undefined) {
    throw "trajectory_x: Could not evaluate input";
  }

    //check dimension
    var dim = math.matrix([1, 100]);
    if (!math.deepEqual(math.size(value), dim)) {
      throw "trajectory_x: Input has dimension " + math.size(value) + " but expected " + dim;
    }

    var array = [];
  for (var i0 = 0; i0 < 1; i0++) {
    array  [i0]
 = [];
  for (var i1 = 0; i1 < 100; i1++) {
      var e = value.get([i0,i1]);

    //check unit
    var expectedUnit = math.eval("m");
    if (math.typeof(expectedUnit) !== math.typeof(e) || !expectedUnit.equalBase(e)) {
      throw "trajectory_x: Expected unit m";
    }
        var e_num = e.toSI().toNumber();
      array  [i0][i1]
 = e_num;
  }
  }
    Module.setTrajectory_x(array);
}
  function setTrajectory_y(_trajectory_y) {
  var value = math.eval(_trajectory_y);

  if (value === undefined) {
    throw "trajectory_y: Could not evaluate input";
  }

    //check dimension
    var dim = math.matrix([1, 100]);
    if (!math.deepEqual(math.size(value), dim)) {
      throw "trajectory_y: Input has dimension " + math.size(value) + " but expected " + dim;
    }

    var array = [];
  for (var i0 = 0; i0 < 1; i0++) {
    array  [i0]
 = [];
  for (var i1 = 0; i1 < 100; i1++) {
      var e = value.get([i0,i1]);

    //check unit
    var expectedUnit = math.eval("m");
    if (math.typeof(expectedUnit) !== math.typeof(e) || !expectedUnit.equalBase(e)) {
      throw "trajectory_y: Expected unit m";
    }
        var e_num = e.toSI().toNumber();
      array  [i0][i1]
 = e_num;
  }
  }
    Module.setTrajectory_y(array);
}

