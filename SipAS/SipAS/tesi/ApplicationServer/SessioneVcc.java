package tesi.ApplicationServer;

import javax.sip.message.Request;
import javax.sip.message.Response;
import javax.sip.header.*;

/**
 * @author Riccardo
 *
 */
public class SessioneVcc {

	public static int firstPort = -1;

	private static int nextId = 1;

	private int id;

	private String fromURI;

	private String toURI;

	private String fromHost = null;

	private String toHost = null;

	private int fromPort = -1;

	private int toPort = -1;

	private int redirectFromPort;

	private int redirectToPort;

	private String redirectHost;

	private Response sessionProgress;

	private Request invite;
	
	private Response ringingMessage;
	
	private Response inviteOK;
	
	private Response asteriskOK;
	
	private Response okToAsterisk;
	
	private Request inviteToAsterisk;
	
	private Request asteriskInvite;
	
	private Request ackMessage;
	
	private String tagTo;
	
	private String tagFrom;

	private boolean established = false;
	
	private String callID=null;
	
	private long cSeq = -1;
	

	public boolean isEstablished() {
		return established;
	}

	public void setEstablished(boolean established) {
		this.established = established;
	}

	public SessioneVcc(String fromURI, String toURI) {
		super();
		this.fromURI = fromURI;
		this.toURI = toURI;

	}

	public void attiva()
	{
		id=nextId++;
	}
	
	public String getFromHost() {
		return fromHost;
	}

	public int getFromPort() {
		return fromPort;
	}

	public String getFromURI() {
		return fromURI;
	}

	public String getToHost() {
		return toHost;
	}

	public void setHost(String who, String host) {
		if (fromURI.equals(who) || fromURI.equals("sip:" + who))
			fromHost = host;
		else
			toHost = host;
	}

	public int getToPort() {
		return toPort;
	}

	public int setPort(String who, int port) {

		if (fromURI.equals(who) || fromURI.equals("sip:" + who)) {
			if (fromPort == -1)
				redirectFromPort = SessioneVcc.firstPort++;
			fromPort = port;
			return redirectFromPort;
		} else {
			if (toPort == -1)
				redirectToPort = SessioneVcc.firstPort++;
			toPort = port;
			return redirectToPort;
		}
	}

	public String getToURI() {
		return toURI;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (obj instanceof SessioneVcc) {
			SessioneVcc session = (SessioneVcc) obj;
			return session.getFromURI().equals(fromURI)
					&& session.getToURI().equals(toURI);
		} else
			return false;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "\nFrom: " + fromURI + " " + fromHost + ":" + fromPort
				+ "\nRedirect-from: " + redirectHost + ":" + redirectFromPort
				+ "\nTo: " + toURI + " " + toHost + ":" + toPort
				+ "\nRedirect-to: " + redirectHost + ":" + redirectToPort;
	}

	public int getNewfromPort() {
		return redirectFromPort;
	}

	public int getNewtoPort() {
		return redirectToPort;
	}

	public String getRedirectHost() {
		return redirectHost;
	}
	
	

	public String getTagTo() {
		return tagTo;
	}

	public void setTagTo(String tagTo) {
		this.tagTo = tagTo;
	}

	public String getTagFrom() {
		return tagFrom;
	}

	public void setTagFrom(String tagFrom) {
		this.tagFrom = tagFrom;
	}

	public void setRedirectHost(String redirectHost) {
		this.redirectHost = redirectHost;
	}

	public int getRedirectFromPort() {
		return redirectFromPort;
	}

	public int getRedirectToPort() {
		return redirectToPort;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Response getSessionProgress() {
		return sessionProgress;
	}

	public void setSessionProgress(Response session_progress) {
		this.sessionProgress = session_progress;
	}

	public Request getInvite() {
		return invite;
	}

	public void setInvite(Request invite) {
		this.invite = invite;
	}

	public String getCallID() {
		String callID = ((CallIdHeader)invite.getHeader("Call-ID")).getCallId();
		return callID;
	}

	

	public long getCSeq() {
		long cSeq = ((CSeqHeader)invite.getHeader("CSeq")).getSeqNumber();
		return cSeq;
	}

	public Response getRingingMessage() {
		return ringingMessage;
	}

	public void setRingingMessage(Response ringingMessage) {
		this.ringingMessage = ringingMessage;
	}

	public Response getInviteOK() {
		return inviteOK;
	}

	public void setInviteOK(Response inviteOK) {
		this.inviteOK = inviteOK;
	}

	public Request getAckMessage() {
		return ackMessage;
	}

	public void setAckMessage(Request ackMessage) {
		this.ackMessage = ackMessage;
	}

	public Response getAsteriskOK() {
		return asteriskOK;
	}

	public void setAsteriskOK(Response asteriskOK) {
		this.asteriskOK = asteriskOK;
	}

	public Request getAsteriskInvite() {
		return asteriskInvite;
	}

	public void setAsteriskInvite(Request asteriskInvite) {
		this.asteriskInvite = asteriskInvite;
	}

	public Response getOkToAsterisk() {
		return okToAsterisk;
	}

	public void setOkToAsterisk(Response okToAsterisk) {
		this.okToAsterisk = okToAsterisk;
	}

	public Request getInviteToAsterisk() {
		return inviteToAsterisk;
	}

	public void setInviteToAsterisk(Request inviteToAsterisk) {
		this.inviteToAsterisk = inviteToAsterisk;
	}

	

}
