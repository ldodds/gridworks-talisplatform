// This file is added to the /project page
MenuBar.insertAfter(
	    [ "core/schemas", "core/load-info-freebase" ],
	    [
	        {
	            label: "Load into Talis Platform...",
	            click: function() {
	        	  TalisPlatformMenu.loadIntoStore();
	            }
	        },
	        {}
	    ]
	);


var TalisPlatformMenu = {};

TalisPlatformMenu.loadIntoStore = function(anything) { 
	
    if ( theProject.overlayModels.rdfSchema.rootNodes.length == 0 ) {
        alert(
            "You haven't done any RDF schema alignment yet!"
        );
    } else {
        new TalisPlatformLoadingDialog();
    }
	
}