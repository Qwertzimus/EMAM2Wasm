var Module = {
    'print': function (text) {
        console.log('stdout: ' + text)
    },
    'printErr': function (text) {
        console.log('stderr: ' + text)
    },
    onRuntimeInitialized: function () {
        Module.init();
    }
};

function init() {
    Module.init();
}

function execute() {
    Module.execute();
}

function getOutNoRangeNoUnit() {
    return math.format(Module.getOutNoRangeNoUnit(), {notation: 'fixed'});
}

function setInNoRangeNoUnit(_inNoRangeNoUnit) {
    var value = math.eval(_inNoRangeNoUnit);

    if (value === undefined) {
        throw "inNoRangeNoUnit: Could not evaluate input";
    }

    //check dimension
    var dim = math.matrix([2, 3]);
    if (!math.deepEqual(value.size(), dim)) {
        throw "inNoRangeNoUnit: Input has dimension " + value.size() + " but expected " + dim;
    }

    var array = [];
    for (var i0 = 0; i0 < 2; i0++) {
        array[i0] = [];
        for (var i1 = 0; i1 < 3; i1++) {

            var e = value.get([i0, i1]);

            var e_num = e;
            array[i0][i1] = e_num;
        }
    }
    Module.setInNoRangeNoUnit(array);
}
