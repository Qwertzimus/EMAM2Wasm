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

function execute() {
    Module.execute();
}

function getOutRangeNoUnit() {
    return math.format(Module.getOutRangeNoUnit(), {notation: 'fixed'});
}

function setInRangeNoUnit(_inRangeNoUnit) {
    var value = math.eval(_inRangeNoUnit);

    if (value === undefined) {
        throw "Could not evaluate input for _inRangeNoUnit";
    }

    //check dimension
    var dim = math.matrix([4]);
    if (!math.deepEqual(value.size(), dim)) {
        throw "Input has dimension " + value.size() + " but expected " + dim;
    }

    var array = [];
    for (var i0 = 0; i0 < 4; i0++) {
        var e = value.get([i0]);

        var e_num = e;
        //check range
        if (math.smaller(e_num, -10 / 1)) {
            throw "Value " + e_num + " out of range";
        }
        if (math.larger(e_num, 10 / 1)) {
            throw "Value " + e_num + " out of range";
        }
        array[i0] = e_num;
    }
    Module.setInRangeNoUnit(array);
}
