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

function getOutRangeUnit() {
    return math.format(Module.getOutRangeUnit(), {notation: 'fixed'}).concat(" m/s");
}

function setInRangeUnit(_inRangeUnit) {
    var value = math.eval(_inRangeUnit);
    var lower = math.eval("-10/3 m/s").toSI().toNumber();
    var upper = math.eval("10/1 km/h").toSI().toNumber();

    if (value === undefined) {
        throw "Could not evaluate input for _inRangeUnit";
    }

    //check unit
    var expectedUnit = math.eval("m/s");
    if (math.typeof(expectedUnit) !== math.typeof(value) || !expectedUnit.equalBase(value)) {
        throw "Expected unit m/s";
    }
    var value_num = value.toSI().toNumber();
    //check range
    if (math.smaller(value_num, lower)) {
        throw "Value " + value_num + " out of range";
    }
    if (math.larger(value_num, upper)) {
        throw "Value " + value_num + " out of range";
    }
    Module.setInRangeUnit(value_num);
}
