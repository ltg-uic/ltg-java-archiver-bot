/**
 * 
 */
package ltg.commons;

import java.net.UnknownHostException;

import ltg.commons.ltg_event_handler.LTGEvent;
import ltg.commons.ltg_event_handler.SingleChatLTGEventHandler;
import ltg.commons.ltg_event_handler.SingleChatLTGEventListener;

import org.jivesoftware.smack.XMPPException;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;
import com.mongodb.util.JSONParseException;

/**
 * @author tebemis
 *
 */
public class ArchiverBot {
	
	private String jid = null;
	private String password = null;
	private String host = null;
	private String chatRoom = null;
	private String chatService = null;
	private String dbName = null;
	private String dbHost = null;
	private static String DEFAULT_MONGO_HOST = "localhost";
	 
	// Event handler
	private SingleChatLTGEventHandler eh = null;
	// Mongo database collection
	private DBCollection mongo = null;


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ArchiverBot aa = new ArchiverBot();
		if (!aa.parseCLIArgs(args)) {
			System.out.println("-----\nUsage\n-----\njava -jar <archive.jar> " +
					"<XMPP_full_jid> <chatRoom> (e.g. test-room) <mongodb_name> [<mongodb_hostname>]\n"
					+ "Note: we assume the password of ALL bots is the same as the username\n");
			System.exit(0);
		}
		aa.initializeAndStartBot();
	}
	
	
	private boolean parseCLIArgs(String[] args) {
		if (args.length < 3)
			return false;
		if (nullOrEmpty(args[0]) || nullOrEmpty(args[1]) || nullOrEmpty(args[2]) )
			return false;
		String[] userAndHost = null;
		try {
			userAndHost = SimpleXMPPClient.splitUserAndHost(args[0]);
		} catch (XMPPException e) {
			return false;
		}
		jid = args[0];
		// Assuming username and password are the same
		password = userAndHost[0];
		host = userAndHost[1];
		chatRoom = args[1];
		chatService = "@" + "conference." + host;
		dbName = args[2];
		if (args.length > 3 && !nullOrEmpty(args[3]))
			dbHost = args[3];
		else 
			dbHost = DEFAULT_MONGO_HOST;
		return true;
	}


	private void initializeAndStartBot() {
		// Initialize event handler and DB
		eh = new SingleChatLTGEventHandler(jid, password, chatRoom + chatService);
		try {
			mongo = new MongoClient(dbHost).getDB(dbName).getCollection("log-" + chatRoom);
		} catch (UnknownHostException e) {
			System.err.println("Impossible to connect to MongoDB, terminating");
			System.exit(1);
		}
		System.out.println("Connected to groupchat and MongoDB");
		
		// Register event handler for all events
		eh.registerHandler(".*", new SingleChatLTGEventListener() {
			@Override
			public void processEvent(LTGEvent e) {
				storeJSONEvent(e);
			}
		});
	
		//Start bot
		eh.runSynchronously();
	}
	
	

	private void storeJSONEvent(LTGEvent event) {
		DBObject dbo = null;
		try {
			dbo = (DBObject) JSON.parse(LTGEvent.serializeEvent(event));
		} catch (JSONParseException e) {
			// Not JSON... skip!
			return;
		}
		if (dbo!=null)
			mongo.insert(dbo);
	}

	
	public static boolean nullOrEmpty(String s) {
		return (s==null || s=="");
	}

}
