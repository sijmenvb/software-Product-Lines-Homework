package DAL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

import models.Message;

public class Messages {
	/**
	 * Create a new user table in the database
	 *
	 */
	public static void createNewMessageTableIfNotExist(Connection connection) {
		// SQL statement for creating a new table
		String query = "CREATE TABLE IF NOT EXISTS messages (\n" + "	id integer PRIMARY KEY AUTOINCREMENT,\n"
				+ "	message_text text NOT NULL,\n" + "	token text NOT NULL,\n" + "	color text NOT NULL\n" + ");";
		try (Statement stmt = connection.createStatement()) {
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Get all the messages from all the senders. Code:
	 * https://www.sqlitetutorial.net/sqlite-java/select/
	 */
	public static LinkedList<Message> selectAll() {
		String sql = "SELECT id, message_text, token, color FROM messages";

		LinkedList<Message> messages = new LinkedList<Message>();

		Connect c = new Connect();
		try (Connection conn = c.connect();) {
			createNewMessageTableIfNotExist(conn);
			try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
				// loop through the result set
				while (rs.next()) {
					Message message = new Message(rs.getInt("id"), rs.getString("message_text"), rs.getString("token"),
							rs.getString("color"));
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
	 * Get all the messages from one sender. Code:
	 * https://www.sqlitetutorial.net/sqlite-java/select/
	 * 
	 * @param sender is the sender of the messages we want to retrieve
	 */
	public static LinkedList<Message> selectBySender(String sender) {
		String sql = "SELECT id, message_text, token, color " + "FROM messages WHERE sender = ?";

		LinkedList<Message> messages = new LinkedList<Message>();
		Connect c = new Connect();
		try (Connection conn = c.connect();) {
			createNewMessageTableIfNotExist(conn);
			try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

				// set the sender
				pstmt.setString(1, sender);

				ResultSet rs = pstmt.executeQuery();

				// loop through the result set
				while (rs.next()) {
					Message message = new Message(rs.getInt("id"), rs.getString("message_text"), rs.getString("token"),
							rs.getString("color"));
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
	 * Insert new message to the messages table. Code:
	 * https://www.sqlitetutorial.net/sqlite-java/insert/
	 * 
	 * @param text   is message text
	 * @param sender is message sender
	 * @param color  is message text color
	 */
	public static int insert(String text, String token, String color) {
		String sql = "INSERT INTO messages(message_text,token,color) VALUES(?,?,?)";

		Connect c = new Connect();
		try (Connection conn = c.connect();) {
			createNewMessageTableIfNotExist(conn);
			try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
				pstmt.setString(1, text);
				pstmt.setString(2, token);
				pstmt.setString(3, color);
				int res = pstmt.executeUpdate();
				return res;
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
		}

		return 0;
	}

}
