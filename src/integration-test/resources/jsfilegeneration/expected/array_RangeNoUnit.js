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

function setInRangeNoUnit(param) {
    var value = math.eval(param);

    if (value === undefined) {
        throw "Could not evaluate input for param";
    }

//check dimension
    var dim = math.matrix([1]);
    if (!math.deepEqual(value.size(), dim)) {
        throw "Input has dimension " + value.size() + " but expected " + dim;
    }

    var array = [];
    for (var i0 = 0; i0 < 1; i0++) {
        var e = value.get([i0]);

        //check unit
        //check range
        if (math.smaller(e, math.eval("-10/1"))) {
            throw "Value " + e + " out of range";
        }
        if (math.larger(e, math.eval("10/1"))) {
            throw "Value " + e + " out of range";
        }
        array[i0] = e.toSI().toNumber();
    }
    Module.setInRangeNoUnit(array);
}
