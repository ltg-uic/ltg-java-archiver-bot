/**
 * 
 */
package ltg.commons;

/**
 * @author tebemis
 *
 */
public class ArchiverAgent {
	
	private String agentUsername = null;
	private String agentPassword = null;
	private String chatRoom = null;
	private String dbName = null;
	
	// Create an XMMP client based on CLI parameters
	SimpleXMPPClient xmpp = null;


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ArchiverAgent aa = new ArchiverAgent();
		if (!aa.parseCLIArgs(args)) {
			System.out.println("Print use");
		}
		aa.initializeAgent();
		aa.startAgent();
	}
	
	
	private boolean parseCLIArgs(String[] args) {
		// TODO Auto-generated method stub
		return true;
	}


	private void initializeAgent() {
		xmpp = new SimpleXMPPClient("archiver", "archiver", "fg-game");
	}
	
	
	private void startAgent() {
		// TODO Auto-generated method stub
		
	}


}
