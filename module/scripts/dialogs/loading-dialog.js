function TalisPlatformLoadingDialog() {
    this._createDialog();	
}

TalisPlatformLoadingDialog.prototype._createDialog = function() {
    var self = this;    
//    alert(ModuleWirings["talisplatform-extension"] + "scripts/dialogs/loading-dialog.html")
    var dialog = $( DOM.loadHTML("talisplatform-extension", "scripts/dialogs/loading-dialog.html") );
        
    self._elmts = DOM.bind(dialog);
    
    self._elmts.cancelButton.click(function() { self._dismiss(); });
    self._elmts.loadButton.click( function() { self._upload(); } );
    
	self._elmts.previewPane.empty().html('<img src="images/large-spinner.gif" title="loading..."/>');
	
    schema = theProject.overlayModels.rdfSchema;
    
	$.post(
		    "/command/rdf-exporter-extension/preview-rdf?" + $.param({ project: theProject.id }),
	        { schema: JSON.stringify(schema), engine: JSON.stringify(ui.browsingEngine.getJSON()) },
	        function(data) {
	        	self._elmts.previewPane.empty();
	        	self._elmts.previewPane.text(data.v);
	        	self._elmts.loadButton.removeAttr("disabled").button("refresh");
		    },
		    "json"
		);
    
	self._level = DialogSystem.showDialog(dialog);
}

TalisPlatformLoadingDialog.prototype._dismiss = function() {
    DialogSystem.dismissUntil(this._level - 1);
};

TalisPlatformLoadingDialog.prototype._upload = function() {
	username = $("#platform-username").val();
	password = $("#platform-password").val();
	storeuri = $("#platform-store").val();
	
	alert(username);
	var self = this;
	
	$.post(
		    "/command/talisplatform-extension/platform-upload",
	        { 
		    	project: theProject.id,		    	
		    	engine: JSON.stringify(ui.browsingEngine.getJSON()),
		    	username: username,
		    	password: password,
		    	store: storeuri
		    },
	        function(data) {
	        	self._elmts.previewPane.empty();
	        	self._elmts.previewPane.text(data.v);
		    },
		    "json"
		);	
};