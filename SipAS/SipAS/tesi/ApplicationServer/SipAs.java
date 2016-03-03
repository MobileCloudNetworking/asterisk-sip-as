package tesi.ApplicationServer;
 
import java.text.ParseException;
import java.util.ListIterator;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.sdp.Connection;
import javax.sdp.Media;
import javax.sdp.MediaDescription;
import javax.sdp.SdpFactory;
import javax.sdp.SessionDescription;
import javax.sip.DialogTerminatedEvent;
import javax.sip.IOExceptionEvent;
import javax.sip.InvalidArgumentException;
import javax.sip.ListeningPoint;
import javax.sip.PeerUnavailableException;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.ServerTransaction;
import javax.sip.SipException;
import javax.sip.SipFactory;
import javax.sip.SipListener;
import javax.sip.SipProvider;
import javax.sip.SipStack;
import javax.sip.TimeoutEvent;
import javax.sip.TransactionAlreadyExistsException;
import javax.sip.TransactionTerminatedEvent;
import javax.sip.TransactionUnavailableException;
import javax.sip.header.*;
import javax.sip.message.Message;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;
import javax.sip.header.ToHeader;
import javax.sip.header.FromHeader;

import gov.nist.javax.sip.address.AddressImpl;

import javax.sip.header.CallIdHeader;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.address.*;



public class SipAs implements SipListener {
	
	private static String qChiamante ="MAX";
	
	private static String qChiamato ="MAX";
	
	private static String qRiChiamante ="MIN";
	
	private static String qRiChiamato ="MAX";

	private MessageFactory messageFactory;

	private HeaderFactory headerFactory;
	
	private SipFactory sipFactory;
	
	private AddressFactory addressFactory;

	private SipStack sipStack;
	
	private SipProvider sipProvider;
	
	private static  String asteriskAddress = "192.168.56.4";
	
	private static  int asteriskPort = 5070;
	

	private static  String localAddress = "192.168.56.1";

	private static  int localPort = 5060;

	private static  String mediaServer = "192.168.56.4";

	private static final int primaPortaLibera = 33330;
	
	private  Hashtable table = null;
	
	private  Hashtable asteriskTable = null;
	
	private Hashtable ringingSemaforo = null;
	
	private Hashtable okSemaforo = null;
	
	//private Hashtable inviteOKTable = null;
	
	private Hashtable byeTable = null;
	
	
	//private Hashtable sessionProgressTable=null;
	
	


	// private static final boolean log=false;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		switch(args.length)
		{
			case 0:
			{
				
			}break;
			
			case 4:
			{
				localAddress = args[0];
				localPort = Integer.parseInt(args[1]);
				asteriskAddress = args[2];
				asteriskPort = Integer.parseInt(args[3]);
				mediaServer = asteriskAddress;
			}break;
			
			default:
			{
				System.out.println("\nError in launching SipAs...\n");
				System.out.println("SipAs can be launched with no arguments or four, localAddress localPort asteriskAddress asteriskPort...\n");
				System.exit(-1);
			}break;
		}
		// TODO Auto-generated method stub
		new SipAs().init();
	}

	private void init() {
		// TODO Auto-generated method stub
	
		table = new Hashtable();
		asteriskTable = new Hashtable();
		//inviteOKTable = new Hashtable();
		okSemaforo = new Hashtable();
		ringingSemaforo=new Hashtable();
		byeTable=new Hashtable();
		//sessionProgressTable=new Hashtable();
		
		
		sipFactory = SipFactory.getInstance();
		sipFactory.setPathName("gov.nist");
		Properties properties = new Properties();
		try{
		addressFactory = sipFactory.createAddressFactory();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		//properties.setProperty("javax.sip.OUTBOUND_PROXY", localAddress + ":" 
		//               + localPort + "/" + "udp");
		properties.setProperty("javax.sip.OUTBOUND_PROXY", asteriskAddress + ":"
				+ asteriskPort + "/" + "udp");
		properties.setProperty("javax.sip.STACK_NAME", "sipAs");
		
		// properties.setProperty("gov.nist.javax.sip.DEBUG_LOG",
		// "sipAsdebug.txt");
		// properties.setProperty("gov.nist.javax.sip.SERVER_LOG",
		// "sipAslog.txt");
		// properties.setProperty("gov.nist.javax.sip.TRACE_LEVEL", "DEBUG");
		// //TRACE + DEBUG
		try {
			sipStack = sipFactory.createSipStack(properties);
			
		} catch (PeerUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {

			messageFactory = sipFactory.createMessageFactory();
			headerFactory = sipFactory.createHeaderFactory();
			ListeningPoint lp = sipStack.createListeningPoint(localAddress,
					localPort, "udp");
		
			sipProvider = sipStack.createSipProvider(lp);
			
			sipProvider.setAutomaticDialogSupportEnabled(false);
			
			System.out.println("An UDP Provider is created on: "
					+ localAddress + ":" + localPort);
			sipProvider.addSipListener(this);
			
			SessioneVcc.firstPort = primaPortaLibera;
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}

	}
	

	public void processRequest(RequestEvent requestEvent) {
		// TODO Auto-generated method stub
		System.out.println("Processing a Request...");
		Request request = requestEvent.getRequest();
		System.out.println(request);
		CSeqHeader cSeq = (CSeqHeader) request.getHeader("CSeq");
	
		
		ServerTransaction tr = null;
		try {
			tr = sipProvider.getNewServerTransaction(request);
		} catch (TransactionAlreadyExistsException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (TransactionUnavailableException e1) {
			// TODO Auto-generated catch block

		}
		ViaHeader viaHeader = null;
		try {
			viaHeader = headerFactory.createViaHeader(localAddress, localPort,
					"UDP", tr.getBranchId());
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InvalidArgumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		request.addHeader(viaHeader);

		
		if(request.getMethod().equals("REGISTER"))
			processRegister(request);
	    
		if(request.getMethod().equals("OPTIONS"))
			processOptions(request);
		
	
			
		
        
		if (request.getMethod().equals("INVITE"))
		{
			UserAgentHeader agentHeader = (UserAgentHeader)request.getHeader("User-Agent");
			if(agentHeader!=null && agentHeader.getProduct().next().toString().equalsIgnoreCase("Asterisk"))
				processAsteriskInvite(request);
			else
			   processInvite(request);
			
		} 
		
		/*else if (request.getMethod().equals("PRACK"))
		{
			
			processPrack(request);
		}*/
		
		else if(request.getMethod().equals("ACK"))
		{
			UserAgentHeader userAgentHeader = (UserAgentHeader)request.getHeader("User-Agent");
			if(userAgentHeader!=null &&
					((userAgentHeader.getProduct().next().toString()).equalsIgnoreCase("Asterisk")))
			    {
			    }
			 else 
			 {
				 if(cSeq.getSeqNumber()==300)
					 request.removeHeader("Route");
				 else
					 processAck(request);
			 }
			
		}
	
		else if (request.getMethod().equals("BYE"))
		{
			
			UserAgentHeader userAgentHeader = (UserAgentHeader)request.getHeader("User-Agent");
			
			String userAgent= null;
			if(userAgentHeader != null)
			    userAgent = userAgentHeader.getProduct().next().toString();
			System.out.println("Processing a BYE request");
			if(userAgent!= null &&userAgent.contains("Asterisk")/*.equalsIgnoreCase("Asterisk")*/)
			{
				processAsteriskBye(request);
			}
			else
			{
				if(byeTable.containsKey(((FromHeader) request.getHeader("From")).getAddress().getDisplayName()))
						{
							
							request = SipUtils.gestisciRouting(request);
							try{
								sipProvider.sendRequest(request);
							}
							catch(Exception e)
							{
								//System.out.println("ERRORE NELL INOLTRARE IL BYE AL DESTINATARIO");
								e.printStackTrace();
							}
						}
				else
			        processBye(request);
			}
		}

	}

	public void processResponse(ResponseEvent responseEvent) {
		System.out.println("Processing a Response...");
		Response response = responseEvent.getResponse();
		System.out.println(response);

		CSeqHeader cSeq = (CSeqHeader) response.getHeader("CSeq");
		// Messaggio OK relativo ad un invito?
		
		if (response.getStatusCode() == Response.OK
				&& cSeq.getMethod().equals("INVITE"))
		{
			
			ServerHeader sh = (ServerHeader) response.getHeader("Server");
			ArchivioSessioniVcc archive = ArchivioSessioniVcc.getInstance();
			SessioneVcc session = archive.get(response);
			
			UserAgentHeader userAgentHeader = (UserAgentHeader)response.getHeader("User-Agent");
			if(sh!=null && 
					sh.toString().contains("Asterisk"))
			{
				
				if(cSeq.getSeqNumber()==300)
					processReInviteOK(response);
				if(cSeq.getSeqNumber()==200)
				{
						//ArchivioSessioniVcc archivio = ArchivioSessioniVcc.getInstance();
						SessioneVcc sessione = archive.get(response);
						try {
							Request asteriskAckRequest = (Request) sessione.getAckMessage().clone();
							SipURI asteriskUri = addressFactory.createSipURI(null, asteriskAddress);
							asteriskUri.setPort(asteriskPort);
							asteriskAckRequest.setRequestURI(asteriskUri);
							asteriskAckRequest.removeHeader("Route");
							asteriskAckRequest.removeHeader("CSeq");
							asteriskAckRequest.addHeader(response.getHeader("CSeq"));
							sipProvider.sendRequest(asteriskAckRequest);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (SipException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
				if(cSeq.getSeqNumber()!=300 && cSeq.getSeqNumber()!=200 )
				{
					processAsteriskOk(response);
				}
				    
			}
			
			//OK proveniente dal chiamato
			else
			{
				//OK a un re-Invite
				if(cSeq.getSeqNumber()==300)
				{
					processAckToCalle(response);
				}
				//OK a un INVITE
				else
				  processInviteOK(response);
			}
		}
			
	
		if(response.getStatusCode()==Response.OK
				&& cSeq.getMethod().equals("REGISTER"))
		{
			SipUtils.removeViaHeader(response);
			try {
				sipProvider.sendResponse(response);
			} catch (Exception e) {
				//System.out.println("ERRORE NELL'INOLTRARE L'OK ALLA REGISTRAZIONE");
				e.printStackTrace();
			}			
			
		}
		
		if(response.getStatusCode()==Response.OK && cSeq.getMethod().equals("BYE"))
		{
			UserAgentHeader userAgentHeader = (UserAgentHeader)response.getHeader("User-Agent");
			// add by me, checking server header
			ServerHeader server = (ServerHeader) response.getHeader("Server");
			if(userAgentHeader!=null || server!=null)
			{
			
				//String userAgent = userAgentHeader.getProduct().next().toString();
			    if(/*userAgent.contains("Asterisk") ||*/ server.toString().contains("Asterisk"))
			    {
			    	
			    	 processAsteriskByeOK(response);
			    }
			          
			    else
			    	{
			    		inoltraByeOKToAsterisk(response);
			    		try{
			    			SipUtils.removeViaHeader(response);
			    			sipProvider.sendResponse(response);
			    		}
			    		catch(Exception e)
			    		{
			    			//System.out.println("ERRORE NELL'INVIARE OK AL MITTENTE DEL BYE");
			    			e.printStackTrace();
			    		}
			    	}
			}
		    else
		    {
	
	    		inoltraByeOKToAsterisk(response);
	    		try{
	    			SipUtils.removeViaHeader(response);
	    			sipProvider.sendResponse(response);
	    		}
	    		catch(Exception e)
	    		{
	    			//System.out.println("ERRORE NELL'INVIARE OK AL MITTENTE DEL BYE");
	    			e.printStackTrace();
	    		}
	    	}
		    	
		}
		
		if(response.getStatusCode()== Response.RINGING)
		{
			processRinging(response);
		}
		
		
		if(response.getStatusCode()==Response.SESSION_PROGRESS)
		{
			
			SipUtils.removeViaHeader(response);
			try{
			ArchivioSessioniVcc archivio = ArchivioSessioniVcc.getInstance();
			SessioneVcc sessione = archivio.get(response);
			sessione.setSessionProgress(response);
			SessionDescription sd = SipUtils.getSessionDescription(response);
			response.removeContent();
			SessionDescription newSd = SipUtils.makeSessionDescription(sd, qChiamante);
			response.setContent(newSd, headerFactory.createContentTypeHeader("application", "sdp"));
			sipProvider.sendResponse(response);
			}
			catch(Exception e)
			{
				//System.out.println("ERRORE NELL INOLTRARE IL 183 PROVENIENTE DA ASTERISK");
				e.printStackTrace();
			}
			
		}
		
		if(response.getStatusCode() == Response.TRYING)
		{
			response.removeHeader("Via");
			System.out.println(" \nTRYING RECEIVED\n");
			
		}

	}
	
	

	private void processInvite(Request request) {
		
		System.out.println("\n\nPROCESSING A INVITE/REINVITE...\n");
			
		      try {
		    	  
		    	ArchivioSessioniVcc archivioSessioniVcc = ArchivioSessioniVcc.getInstance();
			    SessioneVcc sessione = archivioSessioniVcc.get(request);
			    System.out.println("\n\n\nSESSION:\n"+sessione);
			    
			    //Se il messaggio di INVITE o re-INVITE riattraversa il SipAS per la seconda volta
		    	if((sessione!=null) && (sessione.isEstablished()==false))
		    	{
		    		System.out.println("The Session EXISTS, but it is not ESTABLISHED");
		    		request = SipUtils.gestisciRouting(request);
					sipProvider.sendRequest(request);
		    	}
		    	
		
		    	//Se il messaggio è un re-INVITE che giunge al SipAS per la prima volta
				if ((sessione!=null) && (sessione.isEstablished()))
						processReInvite(request);
				
				
				//Se il messaggio di INVITE giunge al SipAS per la prima volta
				if(sessione==null)
				    {
						archivioSessioniVcc.add(request);
					    inoltraInviteToAsterisk(request);
					}	
			
			    } catch (SipException e)
			      		{
			    			System.out.println("ERROR PROCESSING INVITE");
			    	  		e.printStackTrace();
			      		}
			    
			    
			  	}	
			
	
	


	
	
	private void processAsteriskInvite(Request asteriskRequest)
	{
		System.out.println("PROCESSING A INVITE from ASTERISK");
		
		String from = ((FromHeader)asteriskRequest.getHeader("From")).getAddress().getDisplayName();
		Request request = (Request) table.get(from);
		ToHeader to= (ToHeader) request.getHeader("To");
		RouteHeader rh = headerFactory.createRouteHeader(to.getAddress());
		try {
			request.addFirst(rh);
		} catch (NullPointerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SipException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
//		
		
//		request = SipUtils.gestisciRouting(request);
//		request.removeHeader("Supported");
		SupportedHeader supported;
			try {
				
				
				supported = headerFactory.createSupportedHeader("replaces");
			
			request.addHeader(supported);
			SessionDescription asteriskSD = SipUtils.getSessionDescription(asteriskRequest);
			SessionDescription newSD = SipUtils.makeSessionDescription(asteriskSD, qChiamato);
			request.removeContent();
			request.setContent(newSD, headerFactory
					.createContentTypeHeader("application", "sdp"));
			ArchivioSessioniVcc archivio = ArchivioSessioniVcc.getInstance();
			SessioneVcc sessione = archivio.get(request);
			String tag = ((FromHeader) asteriskRequest.getHeader("From")).getTag();
			sessione.setAsteriskInvite(request);
			sessione.setTagFrom(tag);
			asteriskTable.put(from, asteriskRequest);
			System.out.println(request);
			System.out.println("\n     Sending the follow request       \n"+request);
			sipProvider.sendRequest(request);
			System.out.println("     SENT       ");
			
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (SipException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}
	
	

/*	private void processPrack(Request request) {
		System.out.println("\n\n\nPROCESSO UN PRACK...");
		try{
		Response okResponse = messageFactory.createResponse(200, request);
		SipUtils.removeViaHeader(okResponse);
		Response sessionProgress = (Response)sessionProgressTable.get(((FromHeader)request.getHeader("From")).getAddress().getDisplayName());
		Address contactAddress = ((ToHeader)request.getHeader("To")).getAddress();
		ContactHeader contact = headerFactory.createContactHeader(contactAddress);
		okResponse.addHeader(contact);
		RequireHeader require = headerFactory.createRequireHeader("precondition");
		ListIterator routes = request.getHeaders("Route");
		RecordRouteHeader recordRoute = null;
		while(routes.hasNext())
		{
			recordRoute = headerFactory.createRecordRouteHeader(((RouteHeader)routes.next()).getAddress());
			okResponse.addHeader(recordRoute);
		}
		Header network = request.getHeader("P-Access-Network-Info");
		okResponse.addHeader(network);
		okResponse.setContent(sessionProgress.getContent(), headerFactory.createContentTypeHeader("application", "sdp") );
		System.out.println("\n"+okResponse);
		sipProvider.sendResponse(okResponse);
		}
		catch(Exception e)
		{
			System.out.println("ERRORE NELLA GENERAZIONE DEL OK AL PRACK");
			e.printStackTrace();
		}

	}*/
	


	private void processInviteOK(Response response) {
		System.out.println("\n\nPROCESSING A INVITE OK...\n\n");
		// TODO Auto-generated method stub
	
		ArchivioSessioniVcc archivio = ArchivioSessioniVcc.getInstance();
		SessioneVcc sessione = archivio.get(response);
		Response asteriskResponse = sessione.getOkToAsterisk();
	
		String utente = ((FromHeader)response.getHeader("From")).getAddress().getDisplayName();
	
		
		//Il messaggio di OK giunge al SipAS del chiamato e viene inoltrato ad Asterisk
		if(asteriskResponse==null)
		{
			Request inviteRequest = (Request) asteriskTable.get(utente);
			try{
			asteriskResponse = messageFactory.createResponse(200, inviteRequest);
			asteriskResponse.setReasonPhrase("OK");
			SipUtils.removeViaHeader(asteriskResponse);
			SessionDescription sd = SipUtils.getSessionDescription(response);
			//add by RV trying to adjust sdp packets
			SessionDescription newSD = SipUtils.makeSessionDescription(sd, qChiamato);
			
//			asteriskResponse.setContent(newSD,
//					headerFactory.createContentTypeHeader("application","sdp"));
			asteriskResponse.setContent(sd, headerFactory.createContentTypeHeader("application","sdp"));
			//end: add by RV trying to adjust sdp packets
			System.out.println("SENDING THE 200 OK:\n"+asteriskResponse);
			sessione.setInviteOK(response);
			sessione.setOkToAsterisk(asteriskResponse);
			//add by RV trying to adjust sdp packets
			sipProvider.sendResponse(asteriskResponse);
//			sipProvider.sendResponse(response);
			//end: add by RV trying to adjust sdp packets
			}
			catch(Exception e)
			{
				System.out.println("ERROR SENDING OK MESSAGE TO ASTERISK");
				e.printStackTrace();
			}
		}

		//Il messaggio di OK giunge al SipAs del chiamante e viene inoltrato verso il chiamante
		else if (asteriskResponse!=null)
		{
			SipUtils.removeViaHeader(response);
			try{
			sipProvider.sendResponse(response);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
			
	
	}
	
	

	private void processBye(Request request) {
		// TODO Auto-generated method stub
		
		System.out.println("FORWARDING  BYE TO ASTERISK");
		try{
		ArchivioSessioniVcc archive = ArchivioSessioniVcc.getInstance();
		SessioneVcc session = archive.get(request);
		FromHeader from = (FromHeader) request.getHeader("From");
		ToHeader to = (ToHeader) request.getHeader("To"); 
		//		Address fromAddress = from.getAddress();

		Address fromAddress = from.getAddress();
		byeTable.put(fromAddress.getDisplayName(), request);
		//ToHeader to = (ToHeader) request.getHeader("To");
		String user = ((SipURI)to.getAddress().getURI()).getUser();
		SipURI contactUri = addressFactory.createSipURI(null, user+"@"+asteriskAddress);
		contactUri.setPort(asteriskPort);
		System.out.println(contactUri);
		CallIdHeader callId = (CallIdHeader) request.getHeader("Call-ID");
		CSeqHeader cSeq = (CSeqHeader) request.getHeader("CSeq");
		
		ListIterator viaHeaders = request.getHeaders("Via");
		ArrayList vias = new ArrayList();
		// add by me -- modifing the tag
	
		String tag1= from.getTag();
		System.out.println("Session : "+session.toString()+"\t tag: ");
		System.out.println(tag1+"\n");
		
		from.setTag(tag1);
		to.setTag(session.getTagTo());
		//end add by me
		while(viaHeaders.hasNext())
		{
			vias.add(viaHeaders.next());
		}
		MaxForwardsHeader maxF = (MaxForwardsHeader) request.getHeader("Max-Forwards");
		Request asteriskRequest = messageFactory.createRequest(contactUri,
				Request.BYE, callId, cSeq, from, to, vias, maxF);
		
		
//		Header assertedIdentity = request.getHeader("P-Asserted-Identity");
//		asteriskRequest.addHeader(assertedIdentity);
		Address toAddress = to.getAddress();
		AddressImpl astAddress = new AddressImpl();
		SipURI asteriskUri = addressFactory.createSipURI(null, asteriskAddress);
		asteriskUri.setPort(5070);
		astAddress.setAddess(asteriskUri);
		
		
		System.out.println("\n\nBYE TO ASTERISK:\n"+asteriskRequest);
		sipProvider.sendRequest(asteriskRequest);
		}
		
		catch(Exception e)
		{
			System.out.println("Error in sending BYE to ASTERISK");
			e.printStackTrace();
		}
		
	}
	
	

	private void processReInvite(Request request) {
		System.out.println("\n\nPROCESSING A REINVITE...\n");
		// TODO Auto-generated method stub
		
			Request reInvite=null;
			
			ArchivioSessioniVcc archivioSessioni = ArchivioSessioniVcc.getInstance();
			SessioneVcc sessione = archivioSessioni.get(request);
			Response okResponse = sessione.getInviteOK();
			Request oldInvite = sessione.getInvite();
			CSeqHeader cSeqHeader = null;
			
//			Viene creata una risposta di OK da mandare al client che ha richiesto un re-invite
			try {
				cSeqHeader = headerFactory.createCSeqHeader((long)300, Request.INVITE);
				CallIdHeader inviteCallId = (CallIdHeader) request.getHeader("Call-ID");
				request.removeHeader("Call-ID");
				CallIdHeader idHeader = headerFactory.createCallIdHeader(((CallIdHeader)oldInvite.getHeader("Call-ID")).getCallId());
				request.addHeader(idHeader);
				request.removeHeader("CSeq");
				cSeqHeader = headerFactory.createCSeqHeader((long)300, Request.INVITE);
				request.addHeader(cSeqHeader);
				reInvite = (Request) request.clone();
				RouteHeader me = (RouteHeader) request.getHeaders("Route").next();
				request.removeHeader("Route");
				request.addHeader(me);
				RecordRouteHeader recordMe = headerFactory.createRecordRouteHeader(me.getAddress());
				request.addFirst(recordMe);
				sessione.setInvite(request);
				sessione.setEstablished(false);
				
				okResponse.removeHeader("CSeq");
				okResponse.addHeader(cSeqHeader);
				okResponse.removeHeader("Call-ID");
				okResponse.addHeader(inviteCallId);
				ListIterator it = request.getHeaders("Via");
				ListIterator vias = okResponse.getHeaders("Via");
				while(vias.hasNext())
				{
				  okResponse.removeHeader("Via");
				  vias.next();
				}
				
				
				while(it.hasNext())
				{
					ViaHeader via = (ViaHeader) it.next();
					okResponse.addLast(via);
				}
			
				okResponse.addHeader(recordMe);
				SipUtils.removeViaHeader(okResponse);
				System.out.println("\n\n\nOK RESPONSE:\n"+okResponse);
				sipProvider.sendResponse(okResponse);
			} catch (Exception e) {
				System.out.println("ERROR in sending the RE-INVITE OK");
				e.printStackTrace();
			}
			
			
			//Viene mandato il re-INVITE ad Asterisk
			
			System.out.println("\n\nFORWARD  RE-INVITE TO ASTERISK");
			Request inviteToAsterisk = (Request) sessione.getInviteToAsterisk().clone();
			

			inviteToAsterisk.removeHeader("Route");
			
			try {
	
				SessionDescription sd = SipUtils.getSessionDescription(request);
				SessionDescription nuovaSd = SipUtils.makeSessionDescription(sd, qRiChiamante);
				inviteToAsterisk.removeContent();
				inviteToAsterisk.setContent(nuovaSd, headerFactory.createContentTypeHeader("application","sdp"));
				CSeqHeader cSeq = headerFactory.createCSeqHeader((long) 300, Request.INVITE);
				inviteToAsterisk.removeHeader("Cseq");
				inviteToAsterisk.addHeader(cSeq);
				sipProvider.sendRequest(inviteToAsterisk);
				
			} catch (Exception e) {
				System.out.println("ERROR sending RE-INVITE to ASTERISK");
				e.printStackTrace();
			}	
			
			

		
	}
	

	
	/*private void processRePrack(Request request) {
		System.out.println("Processo un reprack...");
		// TODO Auto-generated method stub
		try {
			Response sessionProgress = ArchivioSessioniVcc.getInstance().get(
					request).getSessionProgress();
			Response response = messageFactory.createResponse(Response.OK,
					request);
			ContentTypeHeader contentType = headerFactory
					.createContentTypeHeader("application", "sdp");
			response.setContent(sessionProgress.getContent(), contentType);
			response.addHeader(sessionProgress
					.getHeader("P-Access-Network-Info"));
			response.addHeader(sessionProgress.getHeader("Contact"));
			ListIterator requireHeaders = sessionProgress.getHeaders("Require");
			while (requireHeaders.hasNext())
				response.addHeader((Header) requireHeaders.next());
			response.removeHeader("To");
			response.addHeader(sessionProgress.getHeader("To"));

			ListIterator recordRoutes = request.getHeaders("Record-Route");
			while (recordRoutes.hasNext())
				response.addHeader((Header) recordRoutes.next());
			sipProvider.sendResponse(response);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}*/

	

	private void estraiSdpMedia(Message message) {
		System.out.println("Extracting SdpMedia...");
		SessionDescription sessionDescriptor = SipUtils
				.getSessionDescription(message);
		if (sessionDescriptor == null)
		{
			System.out.println("SessionDescriptor NULL");
			return;
		}
			
		try {

			String username = SipUtils.estraiUsername(message);
			String ip = sessionDescriptor.getOrigin().getAddress();
			Connection con = sessionDescriptor.getConnection();
			con.setAddress(mediaServer);
			sessionDescriptor.setConnection(con);
			Vector mediaDescriptions = sessionDescriptor
					.getMediaDescriptions(false);
			for (Object mediaDescription : mediaDescriptions) {
				Media media = ((MediaDescription) mediaDescription).getMedia();
				SessioneVcc sessione = ArchivioSessioniVcc.getInstance().get(
						message);
				if (media.getMediaType().equals("audio")) {
					sessione.setHost(username, ip);
					sessione.setRedirectHost(mediaServer);
					int port = sessione.setPort(username, media.getMediaPort());
					media.setMediaPort(port);
					mediaDescriptions.removeAllElements();
					MediaDescription mediaDesc = (MediaDescription) mediaDescription;
					mediaDesc.setMedia(media);
					mediaDescriptions.add(mediaDesc);
					sessionDescriptor.setMediaDescriptions(mediaDescriptions);

					message.setContent(sessionDescriptor,
							(ContentTypeHeader) headerFactory
									.createContentTypeHeader("application",
											"sdp"));

				}

			}
        System.out.println("SdpMedia Extracted");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public void processTimeout(TimeoutEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void processTransactionTerminated(TransactionTerminatedEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void processDialogTerminated(DialogTerminatedEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void processIOException(IOExceptionEvent arg0) {
		// TODO Auto-generated method stub

	}
	

	public void processRegister(Request request)
	{
		System.out.println("PROCESSING A REGISTER...");
		
			try{
				
				SipURI toAddress = addressFactory.createSipURI(null, asteriskAddress);
				toAddress.setPort(asteriskPort);
				CallIdHeader asd = (CallIdHeader) request.getHeader("Call-ID");
				request.setRequestURI(toAddress);
				ContactHeader contactHeader = headerFactory.createContactHeader(((ToHeader)request.getHeader("To")).getAddress());
				request.addHeader(contactHeader);
				sipProvider.sendRequest(request);
		
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
		
	  
		
	}
	
	
	
	
	public void processOptions(Request request)
	{
	 try{	
		Response response=messageFactory.createResponse(Response.OK, request);
		SipUtils.removeViaHeader(response);
		sipProvider.sendResponse(response);
		System.out.println("REPLY OK");
	 }
	 catch(ParseException e)
	 {
		 e.printStackTrace();
	 }
	 catch(SipException e)
	 {
		 e.printStackTrace();
	 }
	}

	
	
	
	
	private void inoltraInviteToAsterisk(Request request)
	{
		FromHeader from = (FromHeader) request.getHeader("From");
		Address fromAddress = from.getAddress();
		table.put(fromAddress.getDisplayName(), request);
		ringingSemaforo.put(fromAddress.getDisplayName(), 1);
		okSemaforo.put(fromAddress.getDisplayName(), 1);
		
		try{
			System.out.println("\n\nFORWARDIND THE INVITE TO ASTERISK");
			Request invite = (Request) request.clone();
			ToHeader to = (ToHeader) invite.getHeader("To");
			String user = ((SipURI)to.getAddress().getURI()).getUser();
			SipURI contactUri = addressFactory.createSipURI(null, user+"@"+asteriskAddress);
			contactUri.setPort(asteriskPort);
			invite.setRequestURI(contactUri);
			invite.removeHeader("Route");
			SessionDescription oldSd = SipUtils.getSessionDescription(invite);
			SessionDescription newSd = SipUtils.makeSessionDescription(oldSd, qChiamato);
			
			// add by me, trying to adjust sdp packets
//			invite.removeContent();
//			invite.setContent(newSd, headerFactory.createContentTypeHeader("application","sdp"));
			
			// end add by me, trying to adjust sdp packets
			ArchivioSessioniVcc archivio = ArchivioSessioniVcc.getInstance();
			SessioneVcc sessione = archivio.get(request);
			sessione.setInviteToAsterisk(invite);
			System.out.println("\n\nINVITE TO ASTERISK:\n"+invite);
			sipProvider.sendRequest(invite);
				
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	private void processRinging(Response response)
	{
		String utente = ((FromHeader)response.getHeader("From")).getAddress().getDisplayName();
		int semaforo = (Integer) ringingSemaforo.get(utente);
		
		//UserAgentHeader uah = (UserAgentHeader)response.getHeader("User-Agent");
		//System.out.println("\n USER AGENT -> "+uah.toString());
		ListIterator li = response.getHeaderNames();
		boolean presente = false;
		while(li.hasNext())
		{
			if(li.next().toString().contains("Server"))
				presente=true;
		}
		if(presente)//((UserAgentHeader)response.getHeader("User-Agent")).getProduct().next().toString().equals("Asterisk"))
		{
			
			System.out.println("\n\n ASTERISK'S RINGING\n");
			SipUtils.removeViaHeader(response);
			String tag = ((ToHeader)response.getHeader("To")).getTag();
			ArchivioSessioniVcc archivio = ArchivioSessioniVcc.getInstance();
			SessioneVcc sessione = archivio.get(response);
			sessione.setTagTo(tag);
			try{
			sipProvider.sendResponse(response);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return;
		}
		
		if(semaforo==1)
		{
			
			ArchivioSessioniVcc archivio = ArchivioSessioniVcc.getInstance();
			SessioneVcc sessione = archivio.get(response);
			sessione.setRingingMessage(response);
			ringingSemaforo.remove(utente);
			ringingSemaforo.put(utente, 0);
			try{
				
			sipProvider.sendResponse(response);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return;
		}
		else
		try{
			
		  if(asteriskTable.containsKey(utente))
		   {
			Request inviteRequest = (Request)asteriskTable.get(utente);
		
			
			
			Response ringing = messageFactory.createResponse(180, inviteRequest);
			ringing.setReasonPhrase("Ringing");
			System.out.println("RINGING TO ASTERISK:\n"+ringing);
			SipUtils.removeViaHeader(ringing);
			sipProvider.sendResponse(ringing);
		  }
		 
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	
	
	
	private void processAck(Request request)
	{
		
		ArchivioSessioniVcc archivio = ArchivioSessioniVcc.getInstance();
		SessioneVcc sessione = archivio.get(request);
		//sessione.setEstablished(true);
		
		try{
			// prova di route header, devo mandare l'ack sia ad Asterisk che al server
//		ListIterator it = request.getHeaders("Route");
//		request.removeHeader("Route");
//		it.next();
			//aggiunto da me
			ToHeader to = (ToHeader) request.getHeader("To");
			//fine aggiunto da me
		RouteHeader route=null;
//		while(it.hasNext())
//		{
			route = headerFactory.createRouteHeader(to.getAddress());//headerFactory.createRouteHeader(((RouteHeader)it.next()).getAddress());
			request.addHeader(route);
//		}
		
		FromHeader from = (FromHeader) request.getHeader("From");
		Address fromAddress = from.getAddress();
		table.remove(fromAddress.getDisplayName());
		if(sessione.getAckMessage()!=null)
		{
			sipProvider.sendRequest(request);
		}
		else
		{
			
		    sessione.setAckMessage(request);
		    //estrapolo il ToHeader e lo metto come Route
		    
			sessione.setEstablished(true);
		    RouteHeader rh = (RouteHeader) headerFactory.createRouteHeader(((ToHeader)request.getHeader("To")).getAddress());
		    request.addHeader(rh);
		    // fine
		    to.setTag(sessione.getTagTo());
		    request.setHeader(to);
			sipProvider.sendRequest(request);
			Request asteriskAckRequest = (Request) request.clone();
			SipURI asteriskUri = addressFactory.createSipURI(null, asteriskAddress);
			asteriskUri.setPort(asteriskPort);
			asteriskAckRequest.setRequestURI(asteriskUri);
			asteriskAckRequest.removeHeader("Route");
			sipProvider.sendRequest(asteriskAckRequest);
			// questo blocco manda un re-invite ad asterisk per la conferma del codec, non è necessario
//			Request invite = (Request) sessione.getInviteToAsterisk().clone();
//			SdpFactory sdpFactory = SdpFactory.getInstance();
//			SessionDescription sd = sdpFactory.createSessionDescription(invite.getContent().toString());
//			SessionDescription newSd = SipUtils.makeSessionDescription(sd, qChiamante);
//			invite.removeContent();
//			invite.setContent(newSd, headerFactory.createContentTypeHeader("application","sdp"));
//			invite.removeHeader("CSeq");
//			CSeqHeader cSeq = headerFactory.createCSeqHeader((long)200, Request.INVITE);
//			invite.addHeader(cSeq);
//			lastInvite=true;
//			System.out.println("\n\n\n Questo è l'INVITE che mando ad Asterisk da dentro l'else dell'ACK\n"+invite);
//			System.out.println("\n\n\n\t\t\t FINE INVITE \n");
//			sipProvider.sendRequest(invite);
		}
		
		
       	}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	private void processReInviteOK(Response response)
	{
		
		ArchivioSessioniVcc archivio = ArchivioSessioniVcc.getInstance();
		SessioneVcc sessione = archivio.get(response);
		
			Request ackRequest = (Request) sessione.getAckMessage().clone();
			try {
				SipURI asteriskUri = addressFactory.createSipURI(null, asteriskAddress);
				asteriskUri.setPort(asteriskPort);
				ackRequest.setRequestURI(asteriskUri);
				ackRequest.removeHeader("Route");
				
				CSeqHeader cSeq = headerFactory.createCSeqHeader((long)300, Request.ACK);
				ackRequest.removeHeader("CSeq");
				ackRequest.addHeader(cSeq);
				sipProvider.sendRequest(ackRequest);
				
			} catch (Exception e) {
				// TODO: handle exception
			}		
		
		
	}
	
	private void processAsteriskByeOK(Response response)
	{
		SipUtils.removeViaHeader(response);
		//the first ok from Asterisk must be ignored
//		try {
//			sipProvider.sendResponse(response);
//		} catch (SipException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}
	
	
	private void processAsteriskBye(Request request)
	{
		try{
		
		ToHeader to = (ToHeader) request.getHeader("To");
			
		String name = to.getName();
		Address displayName = to.getAddress();
		StringTokenizer st = new StringTokenizer(displayName.toString(), "@");
		String firstStep = st.nextToken(); // getting the first token <sip:name
		st = new StringTokenizer(firstStep,":");
		st.nextToken();
		String trueName = st.nextToken();
		displayName.setDisplayName(trueName);
		to.setAddress(displayName);
		request.setHeader(to);	
		//String from = ((FromHeader)request.getHeader("From")).getAddress().getDisplayName();
		String toName = ((ToHeader)request.getHeader("To")).getAddress().getDisplayName();
		Request byeRequest = (Request) byeTable.get(toName);
//		System.out.println("\n\n\nBYE REQUEST, FIRST\n"+byeRequest);
//		//byeRequest = SipUtils.gestisciRouting(byeRequest);
//		
//		System.out.println("\nBYE REQUEST, THEN\n"+byeRequest);
		
//		System.out.println("\n\n\n displayName: "+displayName+"\n FirstStep: "+firstStep+"\n trueName: "+trueName+"\n\n\n");
//		System.out.println("\n\n Stringhe prova nome\n\n nome: "+name+"\n displayName: "+displayName+"\n\n Fine Stringhe prova nome\n\n");
		String key = toName + "Asterisk";
//		System.out.println("\nLa CHIAVE: "+key);
		byeTable.put(key, request);
		// added by me
		RouteHeader rh = (RouteHeader) headerFactory.createRouteHeader(((ToHeader)byeRequest.getHeader("To")).getAddress());
		byeRequest.setHeader(rh);
		System.out.println("\nAdded route header... sending request\n"+byeRequest);
		//end added by me
		sipProvider.sendRequest(byeRequest);
		}
		catch(Exception e)
		{
			System.out.println("ERROR in BYE sent by ASTERISK");
			e.printStackTrace();
		}
	}
	
	
	private void inoltraByeOKToAsterisk(Response response)
	{
		try{
		System.out.println("FORWARDING THE OK OF THE BYE TO ASTERISK");
		String from = ((FromHeader)response.getHeader("From")).getAddress().getDisplayName();
		String key = from + "Asterisk";
		Request byeRequest = (Request) byeTable.get(key);
		System.out.println("BYE OLD REQUEST:\n"+byeRequest);
		Response okResponse = messageFactory.createResponse(200, byeRequest);
		SipUtils.removeViaHeader(okResponse);
		sipProvider.sendResponse(okResponse);
		}
		catch(Exception e)
		{
			System.out.println("ERROR in forwarding bye OK to ASTERISK");
			e.printStackTrace();
		}
	}
	
	
	private void processAckToAsterisk(Response response)
	{
		ArchivioSessioniVcc archivio = ArchivioSessioniVcc.getInstance();
		SessioneVcc sessione = archivio.get(response);
		sessione.setEstablished(true);
		sessione.setAsteriskOK(response);
		Request ackRequest = sessione.getAckMessage();
		Request asteriskAckRequest = (Request) ackRequest.clone();
		try{
		SipURI asteriskUri = addressFactory.createSipURI(null, asteriskAddress);
		asteriskUri.setPort(asteriskPort);
		asteriskAckRequest.setRequestURI(asteriskUri);
		
		//asteriskAckRequest.setRequestURI(((ContactHeader) response.getHeader("Contact")).getAddress().getURI());
		asteriskAckRequest.removeHeader("Route");
		
		sipProvider.sendRequest(asteriskAckRequest);
		sipProvider.sendRequest(ackRequest);
			
		} catch (Exception e) {
			// TODO: handle exception
		}		
	}
	
	
	private void inoltraReInviteAckToAsterisk(Request request)
	{
		
		System.out.println("\n\nFORWANDING ACK TO ASTERISK\n");
		ArchivioSessioniVcc archivio = ArchivioSessioniVcc.getInstance();
		SessioneVcc sessione = archivio.get(request);
		sessione.setEstablished(true);

		request.removeHeader("Route");
		
			try {
				SipURI asteriskUri = addressFactory.createSipURI(null, asteriskAddress);
				asteriskUri.setPort(asteriskPort);
				request.setRequestURI(asteriskUri);
				sipProvider.sendRequest(request);
			} catch (Exception e) {
				// TODO: handle exception
			}		
		
	}
	
	
	private void inoltraReInviteToAsterisk(Request ackRequest)
	{
		
		
	}
	
	
	private void processAckToCalle(Response response)
	{
		
		ArchivioSessioniVcc archivio = ArchivioSessioniVcc.getInstance();
		SessioneVcc sessione = archivio.get(response);
		
		System.out.println("\n\nSENDING THE 200 OK POST-INVITE");
		try
		{
		Response okResponse = sessione.getOkToAsterisk();
		okResponse.removeHeader("CSeq");
		okResponse.addHeader(response.getHeader("CSeq"));
		okResponse.setContent(response.getContent(), headerFactory.createContentTypeHeader("application","sdp"));
		sipProvider.sendResponse(okResponse);
		}
		catch(Exception e)
		{
			
		}
		
		System.out.println("SENDING ACK AFTER RE-INVITE REQUEST");
		try{
		Request oldAck = sessione.getAckMessage();
		Request ackRequest = (Request) oldAck.clone();
		ackRequest.removeHeader("CSeq");
		ackRequest.addHeader(response.getHeader("CSeq"));
		ListIterator it = ackRequest.getHeaders("Route");
		it.next();
		it.next();
		it.next();
		ackRequest.removeHeader("Route");
		while(it.hasNext())
		{
			ackRequest.addLast((RouteHeader)it.next());
		}
		sipProvider.sendRequest(ackRequest);
		}
		catch(Exception e)
		{
			System.out.println("ERROR sending ACK");
			e.printStackTrace();
		}
		
	}
	
	private void processAsteriskOk(Response response)
	{
		ArchivioSessioniVcc archivio = ArchivioSessioniVcc.getInstance();
		SessioneVcc sessione = archivio.get(response);
		if(sessione.isEstablished()==false)
		{
			System.out.println("PROCESSING OK FROM ASTERISK");
			try{
			ToHeader to = (ToHeader) response.getHeader("To");
			String tag = to.getTag();
		//	sessione.setAsteriskOK(asteriskOK);
			Response okResponse = (Response)sessione.getInviteOK().clone();
			SipUtils.removeViaHeader(okResponse);
			SessionDescription sd = SipUtils.getSessionDescription(okResponse);
			SessionDescription newSd = SipUtils.makeSessionDescription(sd, qChiamante);
			okResponse.removeContent();
			okResponse.setContent(newSd, headerFactory.createContentTypeHeader("application","sdp"));
			
			sipProvider.sendResponse(okResponse);
			}catch(Exception e)
			{
				System.out.println("CANNOT SEND OK TO CALLER");
				e.printStackTrace();
			}
		}
		
	}
	
		
}


