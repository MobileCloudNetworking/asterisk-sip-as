package tesi.ApplicationServer;

import java.util.ListIterator;

import javax.sdp.SdpException;
import javax.sdp.SdpFactory;
import javax.sdp.SdpParseException;
import javax.sdp.SessionDescription;
import javax.sip.PeerUnavailableException;
import javax.sip.SipException;
import javax.sip.SipFactory;
import javax.sip.header.FromHeader;
import javax.sip.header.Header;
import javax.sip.header.HeaderFactory;
import javax.sip.header.RecordRouteHeader;
import javax.sip.header.RouteHeader;
import javax.sip.header.ToHeader;
import javax.sip.message.Message;
import javax.sip.message.Request;
import javax.sip.message.Response;
import gov.nist.javax.sdp.fields.*;
import java.util.*;
import javax.sdp.*;

public class SipUtils {

	/**
	 * Questo metodo serve ad eliminare il mio indirizzo dall'header Route per
	 * non entrare in loop con me stesso e per inserirlo in "Record-Route"
	 * affinché riesca a ricevere i successivi messaggi del dialogo
	 * 
	 * @param request
	 * @throws SipException
	 * @throws NullPointerException
	 * @throws Exception
	 */
	public static Request gestisciRouting(Request request) {
		System.out.println("\n\n ECCOMIIIIIIIIIII \n\n");
		HeaderFactory headerFactory = null;
		try {
			headerFactory = SipFactory.getInstance().createHeaderFactory();
		} catch (PeerUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		ListIterator it = request.getHeaders("Route");

		RouteHeader me = (RouteHeader) it.next();
		RecordRouteHeader recordRouteHeader = headerFactory
				.createRecordRouteHeader(me.getAddress());
		try {
			request.addFirst(recordRouteHeader);
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (it.hasNext()) {
			RouteHeader routeHeader = (RouteHeader) it.next();
			request.removeHeader("Route");
			request.addHeader(routeHeader);
		}
	
		return request;
	}
	
	
	
	/*public static Request gestisciRoute(Request request) {
		HeaderFactory headerFactory = null;
		try {
			headerFactory = SipFactory.getInstance().createHeaderFactory();
		} catch (PeerUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		ListIterator it = request.getHeaders("Route");
		request.removeHeader("Route");
		RouteHeader me = (RouteHeader) it.next();
		RecordRouteHeader recordRouteHeader = headerFactory
				.createRecordRouteHeader(me.getAddress());
		try {
			request.addFirst(recordRouteHeader);
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		RouteHeader route = null;
		while(it.hasNext())
		{
			route= (RouteHeader) it.next();
			request.addHeader(route);
		}
		return request;
	}*/
	
	
	/**
	 * Nei messaggi di risposta è necessario eliminare il proprio indirizzo
	 * dall'header "Via"ogni volta che si riceve, poiché ogni nodo propaga la
	 * risposta in base a tale header
	 * 
	 * @param response
	 */
	public static void removeViaHeader(Response response) {
		ListIterator listIterator = response.getHeaders("via");
		response.removeHeader("via");
		listIterator.next(); // scarto il primo perché punta al mio indirizzo
		while (listIterator.hasNext())
			try {
				response.addLast((Header) listIterator.next());
			} catch (NullPointerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SipException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	public static String estraiUsername(Message message) {
		boolean request = false;
		if (message instanceof Request)
			request = true;
		String fromURI = ((FromHeader) message.getHeader("From")).getAddress()
				.getURI().toString();
		String toURI = ((ToHeader) message.getHeader("To")).getAddress()
				.getURI().toString();
		return request ? fromURI : toURI;

	}

	public static SessionDescription getSessionDescription(Message message) {
		String sdpContent = null;
		Object sdpObjectContent = message.getContent();
		SdpFactory sdpFactory = SdpFactory.getInstance();
		if (sdpObjectContent instanceof String)
			sdpContent = (String) sdpObjectContent;
		else if (sdpObjectContent instanceof byte[]) {
			sdpContent = new String((byte[]) sdpObjectContent);
		}

		try {
			if (sdpContent != null)
				return sdpFactory.createSessionDescription(sdpContent);
			else
				return null;
		} catch (SdpParseException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
	
	
	public static SessionDescription makeSessionDescription(SessionDescription sd, String quality)
	{
	  try {
		Vector mediaDescriptions = sd.getMediaDescriptions(true);
		int audioPort = 0;
		String audioProtocol = "RTP/AVP";
		int nMedia = mediaDescriptions.size();
		SdpFactory sdpFactory = SdpFactory.getInstance();
		Vector audioFormats=null;
		Vector audioAttributes=null;
		int banda = 0;
		for (int i=0; i<nMedia; i++)
		{
			MediaDescription media = (MediaDescription) mediaDescriptions.elementAt(i);
			if(media.getMedia().getMediaType().equals("audio"))
			{
				audioPort = media.getMedia().getMediaPort();
				audioProtocol = media.getMedia().getProtocol();
				audioAttributes = media.getAttributes(true);
				audioFormats = media.getMedia().getMediaFormats(true);
				banda = media.getBandwidth("AS");
			}
		}
		
		String [] formats = new String[1];
		if(quality.equals("MAX"))
		{
			formats[0] = "0";
		}
		if(quality.equals("MIN"))
		{
			formats[0]= "3";
		}
		
		MediaDescription nuovaMD = sdpFactory.createMediaDescription("audio", audioPort, 1, audioProtocol, formats);
		nuovaMD.setBandwidth("AS", banda);
		
		
		AttributeField attributeField = new AttributeField();
		attributeField.setName("rtpmap");
		if(quality.equals("MAX"))
			attributeField.setValue("0 ULAW/rtp");
		if(quality.equals("MIN"))
			attributeField.setValue("3 GSM/8000");
		nuovaMD.addAttribute(attributeField);
		
		
		
		
	
			SessionDescription nuovaSdp = sdpFactory.createSessionDescription();
			nuovaSdp.setConnection(sd.getConnection());
			nuovaSdp.setOrigin(sd.getOrigin());
			Vector nuoveMediaDescriptions = new Vector();
			nuoveMediaDescriptions.add(nuovaMD);
			nuovaSdp.setMediaDescriptions(nuoveMediaDescriptions);
			return nuovaSdp;
		} catch (SdpException e) {
			System.out.println("ERRORE NEL CREARE LA NUOVA SESSION DESCRIPTION");
			e.printStackTrace();
		}
		
		return sd;
	}

}
