/**
 * 
 */
package ltg.commons;

import java.net.UnknownHostException;

import org.jivesoftware.smack.packet.Message;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;

/**
 * @author tebemis
 *
 */
public class ArchiverAgent {
	
	private String agentUsername = null;
	private String agentPassword = null;
	private String chatRoom = null;
	private String dbName = null;
	private String dbHost = null;
	private static String DEFAULT_MONGO_HOST = "localhost";
	 
	// XMMP client
	private SimpleXMPPClient xmpp = null;
	// Mongo database collection
	private DBCollection mongo = null;


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ArchiverAgent aa = new ArchiverAgent();
		if (!aa.parseCLIArgs(args)) {
			System.out.println("java -jar archiverAgent.jar <XMPP_username> <XMPP_password> <chatRoom> <mongodb_name> [<mongodb_hostname>]");
			System.exit(0);
		}
		aa.initializeAgent();
		aa.startAgent();
	}
	
	
	private boolean parseCLIArgs(String[] args) {
		if (args.length < 4)
			return false;
		if (nullOrEmpty(args[0]) || nullOrEmpty(args[1]) || nullOrEmpty(args[2]) || nullOrEmpty(args[3]))
			return false;
		agentUsername = args[0];
		agentPassword = args[1];
		chatRoom = args[2];
		dbName = args[3];
		if (args.length > 4 && !nullOrEmpty(args[4]))
			dbHost = args[4];
		else 
			dbHost = DEFAULT_MONGO_HOST;
		return true;
	}


	private void initializeAgent() {
		xmpp = new SimpleXMPPClient(agentUsername, agentPassword, chatRoom);
		Mongo m = null;
		DB db = null;
		try {
			m = new Mongo(dbHost);
			db = m.getDB(dbName);
			mongo = db.getCollection("log");
		} catch (UnknownHostException e) {
			System.err.println("Impossible to connect to MongoDB, terminating");
			System.exit(1);
		}
		System.out.println("Connected to groupchat and MongoDB");
	}
	
	
	private void startAgent() {
		System.out.println("Listening and recording...");
		while (!Thread.currentThread().isInterrupted()) {
			storeEvent(xmpp.nextMessage());
		}
		xmpp.disconnect();
	}


	private void storeEvent(Message m) {
		BasicDBObject dbo = new BasicDBObject("message", m.getBody());
		mongo.insert(dbo);
	}

	
	public static boolean nullOrEmpty(String s) {
		return (s==null || s=="");
	}

}
