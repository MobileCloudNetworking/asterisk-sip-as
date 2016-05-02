package tesi.ApplicationServer;
 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.text.ParseException;
import java.util.ListIterator;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.TooManyListenersException;
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
import javax.sip.ObjectInUseException;
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
import javax.sip.TransportNotSupportedException;
import javax.sip.header.*;
import javax.sip.message.Message;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;
import javax.sip.header.RouteHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.FromHeader;

import gov.nist.javax.sip.address.AddressImpl;
import gov.nist.javax.sip.address.SipUri;

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
	
	private static SipFactory sipFactory;
	
	private AddressFactory addressFactory;

	private SipStack sipStack;
	
	private SipStack sipStack2;
	
	private SipProvider sipProvider;
	
	private SipProvider sipProvider2;
	
	private static  String asteriskAddress = "192.168.56.4";
	
	private static  int asteriskPort = 5060;
	

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
	
	// Added for scaling
	
	private int callcounter = 0;
	
	private static ArrayList<AsteriskEndPoint> asteriskPool = new ArrayList<AsteriskEndPoint>();
	
	private static ArrayList<SipStack> asteriskSipStackPool = new ArrayList<SipStack>();
	
	private static ArrayList<SipProvider> asteriskSipproviders = new ArrayList<SipProvider>();
	
	private static String secondAsteriskAddress;
	
	private static int secondAsteriskPort;
	
	public static int portCounter=1;
	
	private static SipAs  sa = null;
	
	// end added for scaling


	// private static final boolean log=false;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		byte[] receivedData = new byte[1024];
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
			
			case 6:
			{
				localAddress = args[0];
				localPort = Integer.parseInt(args[1]);
				asteriskAddress = args[2];
				asteriskPort = Integer.parseInt(args[3]);
				mediaServer = asteriskAddress;
				secondAsteriskAddress = args[4];
				secondAsteriskPort =  Integer.parseInt(args[5]);
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
		
		try {
<<<<<<< HEAD
			DatagramSocket ds = null;
=======
			DatagramSocket ds = new DatagramSocket(5070);
>>>>>>> 79922d983b2abd261969ca8eb8b0ab0c8751b905
//			char[] buffer = new char[1];
//			InputStreamReader isr = new InputStreamReader(System.in);
//			BufferedReader br = new BufferedReader(isr);
			while(true)
			{
//				String car = br.readLine();
//				switch(car.charAt(0))
//				{
//					case 'a':
//					{
//						//add
//						asteriskPool.add(new AsteriskEndPoint(secondAsteriskAddress,secondAsteriskPort));
//						System.out.println("A new Asterisk VM instance has been added at "+secondAsteriskAddress+":"+secondAsteriskPort);
//						Properties properties2 = new Properties();
//						properties2.setProperty("javax.sip.OUTBOUND_PROXY", secondAsteriskAddress + ":"
//								+ secondAsteriskPort + "/" + "udp");
//						properties2.setProperty("javax.sip.STACK_NAME", "sipAs2");
//						SipStack newSipStack = sipFactory.createSipStack(properties2);
//						ListeningPoint lp2 = newSipStack.createListeningPoint(localAddress,
//								5070+portCounter, "udp");
//						portCounter++;
//						SipProvider newSipProvider = newSipStack.createSipProvider(lp2);
//						newSipProvider.setAutomaticDialogSupportEnabled(false);
//						newSipProvider.addSipListener(sa);
//						asteriskPool.add(new AsteriskEndPoint(secondAsteriskAddress,secondAsteriskPort));
//						asteriskSipStackPool.add(newSipStack);
//						asteriskSipproviders.add(newSipProvider);
//					}break;
//					
//					case 'r':
//					{
//						AsteriskEndPoint ap = new AsteriskEndPoint(secondAsteriskAddress,secondAsteriskPort);
//						int index = asteriskPool.indexOf(ap);
//						asteriskPool.remove(ap);
//						asteriskSipStackPool.remove(index);
//						asteriskSipproviders.remove(index);
//						System.out.println("An Asterisk VM instance has been removed at "+secondAsteriskAddress+":"+secondAsteriskPort);
//					}break;
//				}
<<<<<<< HEAD
				ds=new DatagramSocket(5070);
=======
>>>>>>> 79922d983b2abd261969ca8eb8b0ab0c8751b905
				DatagramPacket dp = new DatagramPacket(receivedData, receivedData.length);
				System.out.println("A Service UDP socket has been opened at "+localAddress+":5070");
				ds.receive(dp);
				String data = new String(dp.getData());
<<<<<<< HEAD
				System.out.println("Data Received ->  "+data);
=======
>>>>>>> 79922d983b2abd261969ca8eb8b0ab0c8751b905
				StringTokenizer st = new StringTokenizer(data,"@");
				String action =st.nextToken();
				String address = st.nextToken();
				int port = Integer.parseInt(st.nextToken().trim());
				if(action.compareTo("add")==0)
				{
					//add
<<<<<<< HEAD
					//asteriskPool.add(new AsteriskEndPoint(address,port));
					System.out.println("A new Asterisk VM instance has been added at "+address+":"+port);
=======
					asteriskPool.add(new AsteriskEndPoint(secondAsteriskAddress,secondAsteriskPort));
					System.out.println("A new Asterisk VM instance has been added at "+secondAsteriskAddress+":"+secondAsteriskPort);
>>>>>>> 79922d983b2abd261969ca8eb8b0ab0c8751b905
					Properties properties2 = new Properties();
					properties2.setProperty("javax.sip.OUTBOUND_PROXY", address + ":"
							+ port + "/" + "udp");
					properties2.setProperty("javax.sip.STACK_NAME", "sipAs2");
					SipStack newSipStack = sipFactory.createSipStack(properties2);
					ListeningPoint lp2 = newSipStack.createListeningPoint(localAddress,
							5070+portCounter, "udp");
					portCounter++;
					SipProvider newSipProvider = newSipStack.createSipProvider(lp2);
					newSipProvider.setAutomaticDialogSupportEnabled(false);
					newSipProvider.addSipListener(sa);
					asteriskPool.add(new AsteriskEndPoint(address,port));
					asteriskSipStackPool.add(newSipStack);
					asteriskSipproviders.add(newSipProvider);
<<<<<<< HEAD
					ds.close();
				}
				if(action.compareTo("rem")==0)
=======
					
				}
				else
>>>>>>> 79922d983b2abd261969ca8eb8b0ab0c8751b905
				{
					//remove
					AsteriskEndPoint ap = new AsteriskEndPoint(address,port);
					int index = asteriskPool.indexOf(ap);
					asteriskPool.remove(ap);
					asteriskSipStackPool.remove(index);
					asteriskSipproviders.remove(index);
					portCounter--;
					System.out.println("An Asterisk VM instance has been removed at "+address+":"+port);
<<<<<<< HEAD
					ds.close();
=======
>>>>>>> 79922d983b2abd261969ca8eb8b0ab0c8751b905
				}
			}
		} 
		catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PeerUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransportNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ObjectInUseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TooManyListenersException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	

	private void init() {
		// TODO Auto-generated method stub
		sa= this;
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
		//Properties properties2 = new Properties();
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
		
//		properties2.setProperty("javax.sip.OUTBOUND_PROXY", "192.168.56.5" + ":"
//				+ "5070" + "/" + "udp");
//		properties2.setProperty("javax.sip.STACK_NAME", "sipAs2");
		
		// properties.setProperty("gov.nist.javax.sip.DEBUG_LOG",
		// "sipAsdebug.txt");
		// properties.setProperty("gov.nist.javax.sip.SERVER_LOG",
		// "sipAslog.txt");
		// properties.setProperty("gov.nist.javax.sip.TRACE_LEVEL", "DEBUG");
		// //TRACE + DEBUG
		try {
			sipStack = sipFactory.createSipStack(properties);
			//sipStack2 = sipFactory.createSipStack(properties2);
			
		} catch (PeerUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {

			messageFactory = sipFactory.createMessageFactory();
			headerFactory = sipFactory.createHeaderFactory();
			ListeningPoint lp = sipStack.createListeningPoint(localAddress,
					localPort, "udp");
//			ListeningPoint lp2 = sipStack2.createListeningPoint(localAddress,
//					5071, "udp");
			
			sipProvider = sipStack.createSipProvider(lp);
			//sipProvider2 = sipStack2.createSipProvider(lp2);
			
			sipProvider.setAutomaticDialogSupportEnabled(false);
		//	sipProvider2.setAutomaticDialogSupportEnabled(false);
			
			System.out.println("An UDP Provider is created on: "
					+ localAddress + ":" + localPort);
			sipProvider.addSipListener(this);
		//	sipProvider2.addSipListener(this);
			
			SessioneVcc.firstPort = primaPortaLibera;
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}

		asteriskPool.add(new AsteriskEndPoint(asteriskAddress,asteriskPort));
<<<<<<< HEAD
		asteriskSipStackPool.add(sipStack);
=======
>>>>>>> 79922d983b2abd261969ca8eb8b0ab0c8751b905
		asteriskSipproviders.add(sipProvider);
		//asteriskSipproviders.add(sipProvider2);
	}
	

	public void processRequest(RequestEvent requestEvent) {
		// TODO Auto-generated method stub
		System.out.println("Processing a Request...");
		Request request = requestEvent.getRequest();
		System.out.println(request);
		CSeqHeader cSeq = (CSeqHeader) request.getHeader("CSeq");
	
		
		ServerTransaction tr = null;
		try {
			//could be moved a
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
							// modify for scaling
//							SipURI asteriskUri = addressFactory.createSipURI(null, asteriskAddress);
//							asteriskUri.setPort(asteriskPort);
							SipURI asteriskUri = addressFactory.createSipURI(null, sessione.getAsteriskAddress());
							asteriskUri.setPort(sessione.getAsteriskPort());
							// end modify for scaling
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
			// removing the sdp modified
//			SessionDescription sd = SipUtils.getSessionDescription(response);
//			response.removeContent();
//			SessionDescription newSd = SipUtils.makeSessionDescription(sd, qChiamante);
//			response.setContent(newSd, headerFactory.createContentTypeHeader("application", "sdp"));
			//end removing the sdp modified
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
				//if ((sessione!=null) && (sessione.isEstablished()))
						//processReInvite(request);
				
				
				//Se il messaggio di INVITE giunge al SipAS per la prima volta
				if(sessione==null)
				    {
<<<<<<< HEAD
					
						archivioSessioniVcc.add(request);
						//added for scaling
						int selectedAsterisk = callcounter % asteriskPool.size();
						System.out.println("\n\nSELECTED ASTERISK  -> "+selectedAsterisk+"\n\n");
						System.out.println("\n\nCALL CAOUNTER  -> "+callcounter+"\n\n");
						System.out.println("\n\nASTERISK POOLSIZE  -> "+asteriskPool.size()+"\n\n");
=======
						archivioSessioniVcc.add(request);
						//added for scaling
						int selectedAsterisk = callcounter % asteriskPool.size();
>>>>>>> 79922d983b2abd261969ca8eb8b0ab0c8751b905
						SessioneVcc session = archivioSessioniVcc.get(request);
						session.setAsteriskAddress(asteriskPool.get(selectedAsterisk).getAddress());
						session.setAsteriskPort(asteriskPool.get(selectedAsterisk).getPort());
						session.setProviderIndex(selectedAsterisk);
						callcounter++;
						//end added for scaling
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
			
			//request.addHeader(supported);
			// removing the sdp modified
//			SessionDescription asteriskSD = SipUtils.getSessionDescription(asteriskRequest);
//			SessionDescription newSD = SipUtils.makeSessionDescription(asteriskSD, qChiamato);
//			request.removeContent();
//			request.setContent(newSD, headerFactory
//					.createContentTypeHeader("application", "sdp"));
			//end removing the sdp modified
			ArchivioSessioniVcc archivio = ArchivioSessioniVcc.getInstance();
			SessioneVcc sessione = archivio.get(request);
			String tag = ((FromHeader) asteriskRequest.getHeader("From")).getTag();
			SipProvider tmpSP = asteriskSipproviders.get(sessione.getProviderIndex());
			sessione.setAsteriskInvite(request);
			sessione.setTagFrom(tag);
			asteriskTable.put(from, asteriskRequest);
			System.out.println(request);
			System.out.println("\n     Sending the follow request       \n"+request);
			tmpSP.sendRequest(request);
			//sipProvider.sendRequest(request);
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
		SipProvider sp = asteriskSipproviders.get(sessione.getProviderIndex());
		String utente = ((FromHeader)response.getHeader("From")).getAddress().getDisplayName();
	
		
		//Il messaggio di OK giunge al SipAS del chiamato e viene inoltrato ad Asterisk
		if(asteriskResponse==null)
		{
			Request inviteRequest = (Request) asteriskTable.get(utente);
			try{
			asteriskResponse = messageFactory.createResponse(200, inviteRequest);
			asteriskResponse.setReasonPhrase("OK");
			System.out.println("\n\nThis is the request picked \n\n"+inviteRequest);
			System.out.println("\n\nEnd This is the request picked \n\n"+inviteRequest);
			SipUtils.removeViaHeader(asteriskResponse);
			//SessionDescription sd = SipUtils.getSessionDescription(response);
			//add by RV trying to adjust sdp packets
			//SessionDescription newSD = SipUtils.makeSessionDescription(sd, qChiamato);
			
//			asteriskResponse.setContent(newSD,
//					headerFactory.createContentTypeHeader("application","sdp"));
			//asteriskResponse.setContent(sd, headerFactory.createContentTypeHeader("application","sdp"));
			//end: add by RV trying to adjust sdp packets
			System.out.println("SENDING THE 200 OK:\n"+asteriskResponse);
			// added for scaling
//						Address a = addressFactory.createAddress(sessione.getAsteriskAddress()+":"+sessione.getAsteriskPort());
//						RouteHeader rh = headerFactory.createRouteHeader(a);
//						asteriskResponse.addHeader(rh);
						// end added for scaling
			sessione.setInviteOK(response);
			sessione.setOkToAsterisk(asteriskResponse);
			// added for auto registration
			SipURI su = addressFactory.createSipURI(null, localAddress);
			su.setPort(localPort);
			ContactHeader ch = headerFactory.createContactHeader(addressFactory.createAddress(su));
			
			asteriskResponse.addHeader(ch);
			asteriskResponse.setContent(response.getContent(), headerFactory.createContentTypeHeader("application","sdp"));
			// end added for auto registration
			//add by RV trying to adjust sdp packets
			sp.sendResponse(asteriskResponse);
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
//		SipURI contactUri = addressFactory.createSipURI(null, user+"@"+asteriskAddress);
//		contactUri.setPort(asteriskPort);
		// added/modified for scaling
				SipProvider sp = asteriskSipproviders.get(session.getProviderIndex()); 
				SipURI contactUri = addressFactory.createSipURI(null, user+"@"+session.getAsteriskAddress());
				contactUri.setPort(session.getAsteriskPort());
				System.out.println(contactUri);
				// end added/modified for scaling
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
		//add/ modify for scaling
				SipURI asteriskUri = addressFactory.createSipURI(null, session.getAsteriskAddress());
				asteriskUri.setPort(session.getAsteriskPort());
//		SipURI asteriskUri = addressFactory.createSipURI(null, asteriskAddress);
//		asteriskUri.setPort(5070);
		astAddress.setAddess(asteriskUri);
		
		
		System.out.println("\n\nBYE TO ASTERISK:\n"+asteriskRequest);
		sp.sendRequest(asteriskRequest);
		}
		
		catch(Exception e)
		{
			System.out.println("Error in sending BYE to ASTERISK");
			e.printStackTrace();
		}
		
	}
	
	

//	private void processReInvite(Request request) {
//		System.out.println("\n\nPROCESSING A REINVITE...\n");
//		// TODO Auto-generated method stub
//		
//			Request reInvite=null;
//			
//			ArchivioSessioniVcc archivioSessioni = ArchivioSessioniVcc.getInstance();
//			SessioneVcc sessione = archivioSessioni.get(request);
//			Response okResponse = sessione.getInviteOK();
//			Request oldInvite = sessione.getInvite();
//			CSeqHeader cSeqHeader = null;
//			
////			Viene creata una risposta di OK da mandare al client che ha richiesto un re-invite
//			try {
//				cSeqHeader = headerFactory.createCSeqHeader((long)300, Request.INVITE);
//				CallIdHeader inviteCallId = (CallIdHeader) request.getHeader("Call-ID");
//				request.removeHeader("Call-ID");
//				CallIdHeader idHeader = headerFactory.createCallIdHeader(((CallIdHeader)oldInvite.getHeader("Call-ID")).getCallId());
//				request.addHeader(idHeader);
//				request.removeHeader("CSeq");
//				cSeqHeader = headerFactory.createCSeqHeader((long)300, Request.INVITE);
//				request.addHeader(cSeqHeader);
//				reInvite = (Request) request.clone();
//				RouteHeader me = (RouteHeader) request.getHeaders("Route").next();
//				request.removeHeader("Route");
//				request.addHeader(me);
//				RecordRouteHeader recordMe = headerFactory.createRecordRouteHeader(me.getAddress());
//				request.addFirst(recordMe);
//				sessione.setInvite(request);
//				sessione.setEstablished(false);
//				
//				okResponse.removeHeader("CSeq");
//				okResponse.addHeader(cSeqHeader);
//				okResponse.removeHeader("Call-ID");
//				okResponse.addHeader(inviteCallId);
//				ListIterator it = request.getHeaders("Via");
//				ListIterator vias = okResponse.getHeaders("Via");
//				while(vias.hasNext())
//				{
//				  okResponse.removeHeader("Via");
//				  vias.next();
//				}
//				
//				
//				while(it.hasNext())
//				{
//					ViaHeader via = (ViaHeader) it.next();
//					okResponse.addLast(via);
//				}
//			
//				okResponse.addHeader(recordMe);
//				SipUtils.removeViaHeader(okResponse);
//				System.out.println("\n\n\nOK RESPONSE:\n"+okResponse);
//				sipProvider.sendResponse(okResponse);
//			} catch (Exception e) {
//				System.out.println("ERROR in sending the RE-INVITE OK");
//				e.printStackTrace();
//			}
//			
//			
//			//Viene mandato il re-INVITE ad Asterisk
//			
//			System.out.println("\n\nFORWARD  RE-INVITE TO ASTERISK");
//			Request inviteToAsterisk = (Request) sessione.getInviteToAsterisk().clone();
//			
//
//			inviteToAsterisk.removeHeader("Route");
//			
//			try {
//	
//				SessionDescription sd = SipUtils.getSessionDescription(request);
//				SessionDescription nuovaSd = SipUtils.makeSessionDescription(sd, qRiChiamante);
//				inviteToAsterisk.removeContent();
//				inviteToAsterisk.setContent(nuovaSd, headerFactory.createContentTypeHeader("application","sdp"));
//				CSeqHeader cSeq = headerFactory.createCSeqHeader((long) 300, Request.INVITE);
//				inviteToAsterisk.removeHeader("Cseq");
//				inviteToAsterisk.addHeader(cSeq);
//				sipProvider.sendRequest(inviteToAsterisk);
//				
//			} catch (Exception e) {
//				System.out.println("ERROR sending RE-INVITE to ASTERISK");
//				e.printStackTrace();
//			}	
//			
//			
//
//		
//	}
	

	
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

	

//	private void estraiSdpMedia(Message message) {
//		System.out.println("Extracting SdpMedia...");
//		SessionDescription sessionDescriptor = SipUtils
//				.getSessionDescription(message);
//		if (sessionDescriptor == null)
//		{
//			System.out.println("SessionDescriptor NULL");
//			return;
//		}
//			
//		try {
//
//			String username = SipUtils.estraiUsername(message);
//			String ip = sessionDescriptor.getOrigin().getAddress();
//			Connection con = sessionDescriptor.getConnection();
//			con.setAddress(mediaServer);
//			sessionDescriptor.setConnection(con);
//			Vector mediaDescriptions = sessionDescriptor
//					.getMediaDescriptions(false);
//			for (Object mediaDescription : mediaDescriptions) {
//				Media media = ((MediaDescription) mediaDescription).getMedia();
//				SessioneVcc sessione = ArchivioSessioniVcc.getInstance().get(
//						message);
//				if (media.getMediaType().equals("audio")) {
//					sessione.setHost(username, ip);
//					sessione.setRedirectHost(mediaServer);
//					int port = sessione.setPort(username, media.getMediaPort());
//					media.setMediaPort(port);
//					mediaDescriptions.removeAllElements();
//					MediaDescription mediaDesc = (MediaDescription) mediaDescription;
//					mediaDesc.setMedia(media);
//					mediaDescriptions.add(mediaDesc);
//					sessionDescriptor.setMediaDescriptions(mediaDescriptions);
//
//					message.setContent(sessionDescriptor,
//							(ContentTypeHeader) headerFactory
//									.createContentTypeHeader("application",
//											"sdp"));
//
//				}
//
//			}
//        System.out.println("SdpMedia Extracted");
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//		}
//	}

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
		SipProvider tmpSipProvider;
		
		try{
			
			System.out.println("\n\nFORWARDIND THE INVITE TO ASTERISK");
			Request invite = (Request) request.clone();
			ToHeader to = (ToHeader) invite.getHeader("To");
			String user = ((SipURI)to.getAddress().getURI()).getUser();
//			SipURI contactUri = addressFactory.createSipURI(null, user+"@"+asteriskAddress);
//			contactUri.setPort(asteriskPort);
			// fixing to scale
//			SipURI contactUri = addressFactory.createSipURI(null, user+"@"+asteriskAddress);
//			contactUri.setPort(asteriskPort);
			// modified to select asterisk
			ArchivioSessioniVcc archivio = ArchivioSessioniVcc.getInstance();
			SessioneVcc sessione = archivio.get(request);
			tmpSipProvider = asteriskSipproviders.get(sessione.getProviderIndex());
			SipURI contactUri = addressFactory.createSipURI(null, user+"@"+sessione.getAsteriskAddress());
			contactUri.setPort(sessione.getAsteriskPort());
			// end
			invite.setRequestURI(contactUri);
			invite.removeHeader("Route");
			
			
//			SipURI su = addressFactory.createSipURI(null, "192.168.56.5"+":"+"5070");
//			RouteHeader rh = headerFactory.createRouteHeader(addressFactory.createAddress(su));
			//invite.addHeader(rh);
			// removing sdp modified
		//	SessionDescription oldSd = SipUtils.getSessionDescription(invite);
		//	SessionDescription newSd = SipUtils.makeSessionDescription(oldSd, qChiamato);
			
			// add by me, trying to adjust sdp packets
//			invite.removeContent();
//			invite.setContent(newSd, headerFactory.createContentTypeHeader("application","sdp"));
			
			// end add by me, trying to adjust sdp packets
			
			sessione.setInviteToAsterisk(invite);
			System.out.println("\n\nINVITE TO ASTERISK:\n"+invite);
			//sipProvider.sendRequest(invite);
			tmpSipProvider.sendRequest(invite);
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
				// added for scaling
				RouteHeader rh = headerFactory.createRouteHeader(addressFactory.createAddress(sessione.getAsteriskAddress()+":"+sessione.getAsteriskPort())); 
				
				response.addHeader(rh);
				// end added for scaling
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
			//added for scaling
			ArchivioSessioniVcc archivio = ArchivioSessioniVcc.getInstance();
			SessioneVcc sessione = archivio.get(response);
			SipProvider sp = asteriskSipproviders.get(sessione.getProviderIndex());
			//end added for scaling
			
			
			Response ringing = messageFactory.createResponse(180, inviteRequest);
			ringing.setReasonPhrase("Ringing");
			System.out.println("RINGING TO ASTERISK:\n"+ringing);
			SipUtils.removeViaHeader(ringing);
			//sipProvider.sendResponse(ringing);
			sp.sendResponse(ringing);
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
		SipProvider sp = asteriskSipproviders.get(sessione.getProviderIndex());
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
			//modify for scaling
			SipURI asteriskUri = addressFactory.createSipURI(null, sessione.getAsteriskAddress());
			asteriskUri.setPort(sessione.getAsteriskPort());
			//modify for scaling
			Request asteriskAckRequest = (Request) request.clone();
//			SipURI asteriskUri = addressFactory.createSipURI(null, asteriskAddress);
//			asteriskUri.setPort(asteriskPort);
			
			asteriskAckRequest.setRequestURI(asteriskUri);
			asteriskAckRequest.removeHeader("Route");
			sp.sendRequest(asteriskAckRequest);
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
		// added for scaling
				ArchivioSessioniVcc archivio = ArchivioSessioniVcc.getInstance();
				//SessioneVcc sessione = archivio.get(response);
				SessioneVcc sessione = archivio.get(response);
				SipProvider sp = asteriskSipproviders.get(sessione.getProviderIndex());
				System.out.println("\nQuesta è la sessione--> "+from+"  "+((ToHeader)response.getHeader("To")).getAddress().getDisplayName()+"\n");
				RouteHeader rh = headerFactory.createRouteHeader(addressFactory.createAddress(sessione.getAsteriskAddress()+":"+sessione.getAsteriskPort()));
				okResponse.setHeader(rh);
				// end added for scaling
				System.out.println("\nQuesta la response \n"+okResponse);
		sp.sendResponse(okResponse);
<<<<<<< HEAD
		System.err.println(archivio.getSessionsNumber());
		archivio.remove(response);
=======
>>>>>>> 79922d983b2abd261969ca8eb8b0ab0c8751b905
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
			//add/ modify for scaling
//			SipURI asteriskUri = addressFactory.createSipURI(null, asteriskAddress);
//			asteriskUri.setPort(asteriskPort);
				SipURI asteriskUri = addressFactory.createSipURI(null, sessione.getAsteriskAddress());
				asteriskUri.setPort(sessione.getAsteriskPort());
			asteriskAckRequest.setRequestURI(asteriskUri);
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
		//// removing the sdp modified
		//okResponse.setContent(response.getContent(), headerFactory.createContentTypeHeader("application","sdp"));
		// end removing the sdp modified
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
			// removing the sdp modified
//			SessionDescription sd = SipUtils.getSessionDescription(okResponse);
//			SessionDescription newSd = SipUtils.makeSessionDescription(sd, qChiamante);
//			okResponse.removeContent();
//			okResponse.setContent(newSd, headerFactory.createContentTypeHeader("application","sdp"));
			// end removing the sdp modified
			sipProvider.sendResponse(okResponse);
			}catch(Exception e)
			{
				System.out.println("CANNOT SEND OK TO CALLER");
				e.printStackTrace();
			}
		}
		
	}
	
		
}


