package coursework.appointments.resource;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import coursework.aws.util.DateUtils;

import coursework.appointments.model.Appointment;
import coursework.appointments.model.AppointmentDB;

@Path("appointment")
public class AppointmentResources {
	private static final String USER_ID = "userId";
	@Context private HttpServletRequest request;
	
	@Path("/all")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Appointment> getAllAppointments(@QueryParam(USER_ID) String userId, 
			@QueryParam("startDate") String startDate,
			@QueryParam("endDate") String endDate) {	
		
		if (userId.isEmpty() || userId==null){
			throw new WebApplicationException(Response.status(400)
					.entity("Sorry, the userId is null or empty. Please enter a userId.")
					.build());
		} else {
			request.getSession(true).setAttribute(USER_ID, userId);
		}
		
		Date verifiedStartDate = null;
		Date verifiedEndDate = null;
		
		try {
			verifiedStartDate = DateUtils.stringToDate(startDate);
			verifiedEndDate = DateUtils.stringToDate(endDate);
		} catch (Exception e) {
			throw new WebApplicationException(Response.status(400)
					.entity(e.getMessage())
					.build());
		}

		try {
			List<Appointment> appointment = AppointmentDB.getAllAppointments(userId, verifiedStartDate, verifiedEndDate);
			return appointment;
		} catch (Exception e) {
			throw new WebApplicationException(Response.status(500)
					.entity(e.getMessage())
					.build());
		}
		
	}
	
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Appointment createAppointment(@FormParam("title") String title,
			@FormParam("date") String date,
			@FormParam("hour") int hours,
			@FormParam("minutes") int minutes,
			@FormParam("owner") String owner,
			@FormParam("durationHour") int durationHour,
			@FormParam("durationMin") int durationMin,
			@FormParam("description") String description) {
		String sessionUserId = (String)request.getSession(true).getAttribute(USER_ID);
		if (sessionUserId == null) {
			throw new WebApplicationException(Response.status(400)
					.entity("Sorry you need to provide the user id before creating any appointments")
					.build());
		}
		
		if(hours < 0 || hours >23 || minutes < 0 || minutes > 59) {
			throw new WebApplicationException(Response.status(400)
					.entity("The time entered is not valid.")
					.build());
		}
		
		if(durationHour < 0 || durationMin < 0 || durationMin > 59) {
			throw new WebApplicationException(Response.status(400)
					.entity("The duration is not valid.")
					.build());
		}
		int durationInMinutes = durationHour*60 + durationMin;
		Appointment appointment;
		
		try {
			Date dateTime = DateUtils.concatenateDateAndTime(date, hours, minutes, 0);
			appointment = new Appointment(sessionUserId, title, dateTime, owner, durationInMinutes, description);
			AppointmentDB.createAppointment(appointment);
		} catch (Exception e) {
			throw new WebApplicationException(Response.status(400)
					.entity(e.getMessage())
					.build());
		}
		
		return appointment;
	}
}
