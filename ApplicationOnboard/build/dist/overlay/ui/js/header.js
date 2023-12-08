	// This is the JS file you use to decorate the DOM on the
	// pages that match your regular expression. For example:
var url = SailPoint.CONTEXT_PATH + '/plugins/pluginPage.jsf?pn=applicationonboard';
	jQuery(document).ready(function(){
		jQuery("div.home-widgets")
			.before(
					'<div role="presentation" style="padding:35px;width:225px;height:90px;border:0.1px none grey;text-align:center;font-size:15px;background-color:white;">'+
	        		'	<a href="'+ url + '"role="menuitem" class="menuitem" tabindex="0">'+
	        		'			ApplicationOnboard'+
	        		'	 </a>'+
	        		'</div><br>'  
			);
	});

