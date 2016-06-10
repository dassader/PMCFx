function checkInputValueIs(inputs, value) {
    for(var i = 0; i<inputs.length; i++) {
    console.log($(inputs[i]).val()+ " = "+value)
        if($(inputs[i]).val() != value) {
                return false;
        }
    }
    return true;
}

var activityName = "%activityName%";
var time = "%time%";
var inputs = $("a:contains('"+activityName+"')").parents("tr.rwTask").next().find("td:not(.future, .description)").find("input");
while(!checkInputValueIs(inputs, time)) {
    inputs.val(time).change();
}