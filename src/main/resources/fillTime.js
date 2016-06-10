function sleepFor( sleepDuration ){
    var now = new Date().getTime();
    while(new Date().getTime() < now + sleepDuration){ /* do nothing */ }
}

var activityName = "%activityName%";
var time = "%time%";
$("a:contains('"+activityName+"')").parents("tr.rwTask").next().find("td:not(.future, .description)").find("input").val(time).change();
sleepFor(2000);

