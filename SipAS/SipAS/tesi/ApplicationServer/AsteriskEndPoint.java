package tesi.ApplicationServer;

public class AsteriskEndPoint {
	private String address;
	private int port;
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public AsteriskEndPoint(String address, int port) {
		super();
		this.address = address;
		this.port = port;
	}
	@Override
	public String toString() {
		return "AsteriskEndPoint [address=" + address + ", port=" + port + "]";
	}
	
	@Override
	public boolean equals(Object o)
	{
		AsteriskEndPoint aep = (AsteriskEndPoint)o;
		if(this.address.compareTo(aep.getAddress())==0 && this.port == aep.getPort())
			return true;
		else
			return false;
		
	}
}
