var jasperserverUrl = "${jrs}";
visualize({
	auth : {
		name : "${name}",
		password : "${pass}"
		${orga}
	}
}, function(client) {
	client.report({
		report : "${uri}",
		container : "reportContainer",
		params : {
			"IS_IGNORE_PAGINATION" : [ true ]
		},
		error : function(err) {
			document.getElementById("statusContainer").innerHTML = err.message;
		},
		success : function() {
			document.getElementById("statusContainer").innerHTML = "Reportexecution successful!";
		}
	})
});