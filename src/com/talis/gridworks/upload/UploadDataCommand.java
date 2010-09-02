package com.talis.gridworks.upload;

import java.io.IOException;
import java.net.URI;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gridworks.browsing.Engine;
import com.google.gridworks.browsing.FilteredRows;
import com.google.gridworks.browsing.RowVisitor;
import com.google.gridworks.commands.Command;
import com.google.gridworks.model.Project;
import com.google.gridworks.model.Row;
import com.google.gridworks.rdf.Node;
import com.google.gridworks.rdf.RdfSchema;
import com.google.gridworks.rdf.Util;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.talis.platform.client.ClientException;
import com.talis.platform.client.ServiceException;
import com.talis.platform.client.Store;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.json.JSONException;
import org.json.JSONWriter;

import com.talis.http.Response;

public class UploadDataCommand extends Command {

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	String username = request.getParameter("username");
    	String password = request.getParameter("password");
    	String storeuri = request.getParameter("store");
    	int status = 200;
    	String message = "";
    	
    	Store store = new Store( storeuri, 
    			new UsernamePasswordCredentials(username, password) );
    	
    	try {    		
    		Model model = populateModel(request);
    		System.out.println("model populated " + model.size() );
    		Response platformResponse = store.getMetabox().submitModel(model);
    		System.out.println("upload completed");
    		status = platformResponse.getStatus();
    		//message = new String( platformResponse.getEntityBodyAsByteArray() );
    		
    	} catch (ClientException ce) {    	    		
            status = ce.getServiceResponse().getStatus() ;
            message = new String( ce.getServiceResponse().getEntityBodyAsByteArray() );
    	} catch (ServiceException se) {
            status = se.getServiceResponse().getStatus() ;
            message = new String( se.getServiceResponse().getEntityBodyAsByteArray() );    		
    	} catch (Exception e) {
    		if (e instanceof ServletException) {
    			throw (ServletException)e;
    		} else {
    			throw new ServletException(e);
    		}
    	}
    	
    	try {
	        JSONWriter writer = new JSONWriter(response.getWriter());
	        writer.object();
	        writer.key("status");
	        writer.value( status );
	        writer.key("message");
	        writer.value( message );
	        writer.endObject();
    	} catch (JSONException e) {
    		throw new ServletException(e);
    	}
    }
    
    private Model populateModel(HttpServletRequest request) 
    	throws ServletException, Exception {
    	Project project = getProject(request);
    	Engine engine = getEngine(request, project);
    	
        RdfSchema schema = Util.getProjectSchema(project);
        Model model = ModelFactory.createDefaultModel();
        URI baseUri = schema.getBaseUri();
        Node root = schema.getRoot();
        
        Resource[] blanks = new Resource[schema.get_blanks().size()];
        for (int i = 0; i < blanks.length; i++) {
            blanks[i] = model.createResource();
        }
        
        RowVisitor visitor = new RdfRowVisitor(model, baseUri, root,blanks);
        FilteredRows filteredRows = engine.getAllFilteredRows();
        filteredRows.accept(project, visitor);
        return model;
    }
    
    protected static class RdfRowVisitor implements RowVisitor{
        Model model;
        URI base;
        Node root;
        Resource[] blanks;
        public RdfRowVisitor(Model m,URI base, Node root,Resource[] blanks){
            this.model = m;
            this.base = base;
            this.root = root;
            this.blanks = blanks;
        }
        public void end(Project project) {
            // do nothing
            
        }

        public void start(Project project) {
            // do nothing
            
        }

        public boolean visit(Project project, int rowIndex, Row row) {
            root.createNode(base, model, project, row, rowIndex,blanks);
            return false;
        }
    }
    
}
