jQuery(document).ready(function(){
	// This is the JS file you use to decorate the DOM on the
	// pages that match your regular expression. For example:
	var url = SailPoint.CONTEXT_PATH + '/plugins/pluginPage.jsf?pn=demoapp1';
	jQuery(document).ready(function(){
		jQuery("ul.navbar-right li:first")
			.before(
					'<li class="dropdown">' +
					'		<a href="' + url + '" tabindex="0" role="menuitem" title="App Plugin Example">' +
					'			<i app="presenation" class="fa fa-shield fa-lg example"></i>' +
					'		</a>' +
					'</li>'
			);
	});

	
});
