package sailpoint.plugin.applicationonboard.rest;

import java.util.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sailpoint.rest.plugin.BasePluginResource;
import sailpoint.rest.plugin.RequiredRight;

@RequiredRight(value = "OnboardApplicationRESTAllow")
@Path("OnboardApplication")
public class OnboardApplicationResource extends BasePluginResource {
	public static final Log log = LogFactory.getLog(OnboardApplicationResource.class);

	@POST
	@Path("OnboardApplication")
	@Produces(MediaType.APPLICATION_JSON)
	public Map postOnboardApplication() {
		log.debug("POST OnboardApplication");
		Map ret = null;
		return ret;
	}

	@Override
	public String getPluginName() {
		return "applicationonboard";
	}
}
