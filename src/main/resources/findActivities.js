var activities = $(".rwTask").find("a");
var list = [];

activities.each(function (key, value) {
    list.push($(value).text());
});

return JSON.stringify(list);

