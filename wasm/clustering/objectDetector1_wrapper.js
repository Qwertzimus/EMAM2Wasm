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

  function getClusters() {
    return math.format(Module.getClusters(), {notation: 'fixed'})
  ;
  }

  function setImgFront(_imgFront) {
  var value = math.eval(_imgFront);
      var lower = 0/1;
      var upper = 255/1;

  if (value === undefined) {
    throw "imgFront: Could not evaluate input";
  }

    //check dimension
    var dim = math.matrix([2500, 3]);
    if (!math.deepEqual(math.size(value), dim)) {
      throw "imgFront: Input has dimension " + math.size(value) + " but expected " + dim;
    }

    var array = [];
  for (var i0 = 0; i0 < 2500; i0++) {
    array  [i0]
 = [];
  for (var i1 = 0; i1 < 3; i1++) {
      var e = value.get([i0,i1]);

        var e_num = e;
    //check range
  if (math.smaller(e_num, lower)) {
        throw "imgFront: Value " + e_num + " out of range";
      }
  if (math.larger(e_num, upper)) {
        throw "imgFront: Value " + e_num + " out of range";
      }
      array  [i0][i1]
 = e_num;
  }
  }
    Module.setImgFront(array);
}

