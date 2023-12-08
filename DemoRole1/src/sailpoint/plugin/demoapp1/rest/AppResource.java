package sailpoint.plugin.demoapp1.rest;

import java.util.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.RequestBody;
import sailpoint.api.SailPointContext;
import sailpoint.api.SailPointFactory;
import sailpoint.object.Bundle;
import sailpoint.object.*;
import sailpoint.object.Identity;
import sailpoint.rest.plugin.AllowAll;
import sailpoint.rest.plugin.BasePluginResource;
import sailpoint.rest.plugin.RequiredRight;

@RequiredRight(value = "")
@Path("app")
public class AppResource extends BasePluginResource {
	public static final Log log = LogFactory.getLog(AppResource.class);

	/* 
	 * Create role endpoint
	 * Param: Map<String, Object> body
	*/
	@POST
	@Path("createApp")
	@Produces(MediaType.APPLICATION_JSON)
	@AllowAll
	public String createRole(@RequestBody Map<String, Object> body) {
	
		SailPointContext context;
		Identity ownerIdentity;
		Application app;
		
		try{
			context = SailPointFactory.getCurrentContext();
			ownerIdentity = context.getObjectByName(Identity.class, (String) body.get("owner"));
			app = new Application();
			app.setName((String) body.get("name"));
			app.setDescription((String) body.get("desc"));
			String type1=(String) body.get("type");
			app.setType("JDBC");
			app.setOwner(ownerIdentity);
			String app1="sailpoint.connector."+type1;
			app.setConnector(app1);
			context.saveObject(app);
			context.commitTransaction();
			return "Application created successfully";
		}catch(Exception e){
			System.out.println(e);
			return "Application creation failed";
		}
	}


	@Override
	public String getPluginName() {
		return "demoapp1";
	}
}
