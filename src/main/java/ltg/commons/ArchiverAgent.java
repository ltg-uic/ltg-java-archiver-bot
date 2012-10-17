/**
 * 
 */
package ltg.commons;

/**
 * @author tebemis
 *
 */
public class ArchiverAgent {


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Parse command line arguments
		
		// Create an XMMP client based on CLI parameters
		SimpleXMPPClient xmpp = new SimpleXMPPClient("archiver", "archiver", "fg-game");
	}

}
