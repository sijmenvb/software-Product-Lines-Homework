package DAL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

import models.User;

public class Users {
    /**
     * Create a new user table in the database
     * Code: https://www.sqlitetutorial.net/sqlite-java/create-table/
     */
    public static void createNewUserTableIfNotExist(Connection connection) {
        // SQL statement for creating a new table
        String query = "CREATE TABLE IF NOT EXISTS users (\n"
                + "	id integer PRIMARY KEY AUTOINCREMENT,\n"
                + "	username text NOT NULL,\n"
                + "	password text NOT NULL\n"
                + ");";
        
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    /**
     * Select all rows in the users table
     * Code: https://www.sqlitetutorial.net/sqlite-java/select/
     */
    public static LinkedList<User> selectAll(){
        String sql = "SELECT id, username, password FROM users";
        
        LinkedList<User> users = new LinkedList<User>();
        
        Connect c = new Connect();
        
        try (Connection conn = c.connect();) {
        	createNewUserTableIfNotExist(conn);
        	try (Statement stmt  = conn.createStatement();
        			ResultSet rs    = stmt.executeQuery(sql)){
        		// loop through the result set
        		while (rs.next()) {
        			User newUser = new User(rs.getInt("id"), rs.getString("username"), rs.getString("password"));
        			users.add(newUser);
        		}
        	} catch (SQLException e) {
        		System.out.println(e.getMessage());
        	}
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        
        
        return users;
    }
    
    /**
     * Select one user in the users table, which matches a username.
     * Code: https://www.sqlitetutorial.net/sqlite-java/select/
     */
    public static User selectByUsername(String username){
    	String sql = "SELECT id, username, password "
                + "FROM users WHERE username = ?";
    	
        Connect c = new Connect();
        try (Connection conn = c.connect();) {
        	createNewUserTableIfNotExist(conn);
        	try (PreparedStatement pstmt  = conn.prepareStatement(sql)){
                   
                   // set the username
                   pstmt.setString(1,username);
                   //
                   ResultSet rs  = pstmt.executeQuery();
                   
                   // loop through the result set
                   while (rs.next()) {
                	   return new User(rs.getInt("id"), rs.getString("username"), rs.getString("password"));
                   }
        	} catch (SQLException e) {
        		System.out.println(e.getMessage());
        	}
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        
        return null;
    }
    
    /**
     * Insert new user to the users table.
     * Code: https://www.sqlitetutorial.net/sqlite-java/insert/
     */
    public static int insert(String username, String password){
        String sql = "INSERT INTO users(username,password) VALUES(?,?)";
        
        User existingUser = selectByUsername(username);
        if(existingUser == null) {
        	Connect c = new Connect();
            try (Connection conn = c.connect();) {
            	createNewUserTableIfNotExist(conn);
            	try (PreparedStatement pstmt = conn.prepareStatement(sql)){
            		pstmt.setString(1, username);
                    pstmt.setString(2, password);
                    int id = pstmt.executeUpdate();
                    return id;
            	} catch (SQLException e) {
            		System.out.println(e.getMessage());
            	}
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
            
            return 0;
        }else {
        	return existingUser.getId();
        }
    }
}
