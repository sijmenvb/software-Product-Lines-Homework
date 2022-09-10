package DAL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

import models.Message;
import models.User;

public class Messages {
	/**
     * Create a new user table in the database
     *
     */
    public static void createNewMessageTableIfNotExist(Connection connection) {
        // SQL statement for creating a new table
        String query = "CREATE TABLE IF NOT EXISTS messages (\n"
                + "	id integer PRIMARY KEY AUTOINCREMENT,\n"
                + "	message_text text NOT NULL,\n"
                + "	sender text NOT NULL,\n"
                + "	color text NOT NULL,\n"
                + ");";
        try (Statement stmt = connection.createStatement()) {
            System.out.println(stmt.executeUpdate(query));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    /**
     * Get all the messages from all the senders.
     * Code: https://www.sqlitetutorial.net/sqlite-java/select/
     */
    public static LinkedList<Message> selectAll(){
        String sql = "SELECT id, message_text, sender, colot FROM messages";
        
        LinkedList<Message> messages = new LinkedList<Message>();
        
        Connect c = new Connect();
        try (Connection conn = c.connect();) {
        	createNewMessageTableIfNotExist(conn);
        	try (Statement stmt = conn.createStatement();
        			ResultSet rs = stmt.executeQuery(sql)){
        		// loop through the result set
        		while (rs.next()) {
        			Message message = new Message(rs.getInt("id"), rs.getString("message_text"), rs.getString("sender"), rs.getString("color"));
        			messages.add(message);
        		}
        	} catch (SQLException e) {
        		System.out.println(e.getMessage());
        	}
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        
        return messages;
    }
    
    /**
     * Get all the messages from one sender.
     * Code: https://www.sqlitetutorial.net/sqlite-java/select/
     */
    public static LinkedList<Message> selectBySender(String sender){
    	String sql = "SELECT id, message_text, sender, color "
                + "FROM messages WHERE sender = ?";

        LinkedList<Message> messages = new LinkedList<Message>();
        Connect c = new Connect();
        try (Connection conn = c.connect();) {
        	createNewMessageTableIfNotExist(conn);
        	try (PreparedStatement pstmt  = conn.prepareStatement(sql)){
                   
                   // set the sender
                   pstmt.setString(1,sender);
                   
                   ResultSet rs  = pstmt.executeQuery();
                   
                   // loop through the result set
                   while (rs.next()) {
                	   Message message = new Message(rs.getInt("id"), rs.getString("message_text"), rs.getString("sender"), rs.getString("color"));
                	   messages.add(message);
                   }
        	} catch (SQLException e) {
        		System.out.println(e.getMessage());
        	}
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        
        return messages;
    }
    
    /**
     * Insert new message to the messages table.
     * Code: https://www.sqlitetutorial.net/sqlite-java/insert/
     */
    public static int insert(String text, String sender, String color){
        String sql = "INSERT INTO messages(message_text,sender,color) VALUES(?,?,?)";
        
        Connect c = new Connect();
        try (Connection conn = c.connect();) {
        	createNewMessageTableIfNotExist(conn);
        	try (PreparedStatement pstmt = conn.prepareStatement(sql)){
        		pstmt.setString(1, text);
                pstmt.setString(2, sender);
                pstmt.setString(3, color);
                int id = pstmt.executeUpdate();
                return id;
        	} catch (SQLException e) {
        		System.out.println(e.getMessage());
        	}
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        
        
        return 0;
    }

}
