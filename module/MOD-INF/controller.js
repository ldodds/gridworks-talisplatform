var html = "text/html";
var encoding = "UTF-8";

importPackage(com.talis.gridworks.upload);

/*
 * Function invoked to initialize the extension.
 */
function init() {

	/*
	 * Commands
	 */
    var GridworksServlet = Packages.com.google.gridworks.GridworksServlet;
    GridworksServlet.registerCommand(module, "platform-upload", new UploadDataCommand());

    /*
     *  Client-side Resources
     */    
    var ClientSideResourceManager = Packages.com.google.gridworks.ClientSideResourceManager;
    
    // Script files to inject into /project page
    ClientSideResourceManager.addPaths(
        "project/scripts",
        module,
        [         
            "scripts/dialogs/loading-dialog.js",
            "scripts/menu-items.js"            
        ]
    );
    
    // Style files to inject into /project page
    ClientSideResourceManager.addPaths(
        "project/styles",
        module,
        [
            "styles/talisplatform-extension.css"
        ]
    );
}
