package progEx;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.table.DefaultTableModel;

import com.mysql.cj.jdbc.integration.c3p0.MysqlConnectionTester;

public class Connector {

	private final String url = "jdbc:mysql://localhost:3306/Bikes?useTimezone=true&serverTimezone=UTC";
	private final String user = "root";
	private final String password = "root";
	private Connection myConn;
	private Statement myStmt;
	private ResultSet myRs;

	/*
	 * Constructor connects to database
	 */
	public Connector() {

		try {

			// 1. get connection to database
			this.myConn = DriverManager.getConnection(this.url, this.user, this.password);

			// 2. create a Statement
			this.myStmt = this.myConn.createStatement();

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	/*
	 * method prints out database table
	 */
	public void printTable() {
		try {
			// 3. Execute SQL query
			this.myRs = this.myStmt.executeQuery("select * from bikeTable");

			// 4. Process the result set
			while (myRs.next()) {
				System.out.println(myRs.getInt("article_id") + ". " + myRs.getString("brand") + ", "
						+ myRs.getString("type") + ", " + myRs.getFloat("price"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*
	 * sets the values in the model for the Jtable
	 */
	public void setTable(DefaultTableModel model) {
		try {
			this.myRs = this.myStmt.executeQuery("select * from bikeTable");

			while (myRs.next()) {
				String articleId = myRs.getString("article_id"); // one could work with INT
				String brand = myRs.getString("brand");
				String type = myRs.getString("type");
				String price = myRs.getString("price"); // one could work with FLOAT

				model.addRow(new Object[] { articleId, brand, type, price });
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * method for writing into the database table
	 */
	public void add(int articleId, String brand, String type, String price) {
		// execute SQL query
		String query = "INSERT INTO `Bikes`.`bikeTable` " + "(`article_id`, `brand`, `type`, `price`) "
				+ "VALUES (?,?,?,?);";

		try {
			// better way to go are prepared statements
			PreparedStatement statement = this.myConn.prepareStatement(query);
			statement.setInt(1, articleId); // 1 for the first '?'
			statement.setString(2, brand); // 2 for the second '?' and so on..
			statement.setString(3, type);
			statement.setString(4, price);

			int affectedColumns = statement.executeUpdate();

			if (affectedColumns > 0) {
				System.out.println("insert done");
			}
		} catch (SQLException e) {

			e.printStackTrace();

		}

	}

	public void updateDatabase(int id,int initialId, String brand, String type, String price) {
		String query = "UPDATE `Bikes`.`bikeTable` " 
				+ "SET `brand` = ?, " 
				+ "`type` = ?, " 
				+ "`price` = ?,"
				+ "`article_id` = ?"
				+ " WHERE (`article_id` = ?);";

		try {
			// set a prepared statement
			PreparedStatement statement = this.myConn.prepareStatement(query);
			statement.setString(1, brand);
			statement.setString(2, type);
			statement.setString(3, price);
			statement.setInt(4, id);
			statement.setInt(5, initialId);

			int affectedColumns = statement.executeUpdate();

			if (affectedColumns > 0) {
				System.out.println("update done");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/*
	 * function for deleting a row from the database
	 * argument = the id of the object which one wants to delete
	 */
	public void deleleteFromDatabase(int id) {
		String query = "DELETE FROM `Bikes`.`bikeTable` " + "WHERE (`article_id` = ?);";

		try {
			PreparedStatement statement = this.myConn.prepareStatement(query);
			statement.setInt(1, id);
			int affectedRow = statement.executeUpdate();
			if (affectedRow > 0) {
				System.out.println("deleted");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
