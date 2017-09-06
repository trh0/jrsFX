var jasperserverUrl = "${jrs}";
try {
	visualize({
		auth : {
			name : "${name}",
			password : "${pass}"
			${orga}
		}
	}, function(client) {
			var rep = client('#reportContainer').report({
			server: jasperserverUrl,
			resource: "${uri}",
			params : {
				"IS_IGNORE_PAGINATION" : [ true ]
			},
			error : handleError,
			success : handleSuccess
		});
			function handleSuccess() {
				console.log("Executed succesful!")
			}
			function handleError(err) {
				console.log(err.message);
			}
	});
} catch (error) {
	console.log(error);
}
