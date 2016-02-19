package tesi.ApplicationServer;

import java.util.ArrayList;
import java.util.List;

import javax.sip.header.FromHeader;
import javax.sip.header.ToHeader;
import javax.sip.message.Message;
import javax.sip.message.Request;
import javax.sip.message.Response;

public class ArchivioSessioniVcc {

	private List<SessioneVcc> lista = new ArrayList<SessioneVcc>();

	private static ArchivioSessioniVcc archivio = null;

	private ArchivioSessioniVcc() {

	}

	public static ArchivioSessioniVcc getInstance() {
		if (archivio == null)
			archivio = new ArchivioSessioniVcc();
		return archivio;
	}

	public void add(Request request) {
		String fromURI = ((FromHeader) request.getHeader("From")).getAddress()
				.getURI().toString();
		String toURI = ((ToHeader) request.getHeader("To")).getAddress()
				.getURI().toString();
		SessioneVcc sessioneVcc = new SessioneVcc(fromURI, toURI);
		sessioneVcc.setInvite(request);
		if (!lista.contains(sessioneVcc)) {
			sessioneVcc.attiva();
			lista.add(sessioneVcc);
		}

	}
	
	

	public SessioneVcc remove(Request request) {

		SessioneVcc sessioneVcc = get(request);
		lista.remove(sessioneVcc);
		return sessioneVcc;
	}

	public SessioneVcc get(Message message) {
		
		
		String fromURI = ((FromHeader) message.getHeader("From")).getAddress()
				.getURI().toString();
		String toURI = ((ToHeader) message.getHeader("To")).getAddress()
				.getURI().toString();
		// add and modify by me
		SessioneVcc session = new SessioneVcc(fromURI, toURI);
		if(message instanceof Request)
		{
			Request msgReq = (Request) message;
			if (msgReq.getMethod().compareTo(Request.BYE)==0)
			{
				session = new SessioneVcc(toURI, fromURI);
			}
			
		}
		
		// end modify by me
	
		
		int indice = lista.indexOf(session);
		if (indice == -1)
			return null;
		else
			return lista.get(indice);
	}

}
