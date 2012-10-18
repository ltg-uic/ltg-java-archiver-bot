/**
 * 
 */
package ltg.commons;

import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
	// Logger
	private Logger log = LoggerFactory.getLogger(getClass()); 
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
			System.out.println("Print use");
		}
		aa.initializeAgent();
		aa.startAgent();
	}
	
	
	private boolean parseCLIArgs(String[] args) {
		
		// If on dbHost!!!
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
			log.error("Impossible to connect to MongoDB, terminating");
			Thread.currentThread().interrupt();
		}
	}
	
	
	private void startAgent() {
		while (!Thread.currentThread().isInterrupted()) {
			storeEvent(xmpp.nextEvent());
		}
		xmpp.disconnect();
	}


	private void storeEvent(Event e) {
		BasicDBObject doc = new BasicDBObject();
		doc.put("origin", e.origin);
		// If it's json insert the payload as json otherwise, just record the payload
		// Pay attention to special characters
		// maybe log too???
		mongo.insert(doc);
	}


}
