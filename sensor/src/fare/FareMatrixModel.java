package fare;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FareMatrixModel {
	private Connection conn = null;

	public FareMatrixModel() throws ClassNotFoundException, SQLException {
      Class.forName("org.sqlite.JDBC");
      conn = DriverManager.getConnection("jdbc:sqlite:fares.db");
	}
	
	public List<String> getAllLocations() throws SQLException {
		Statement statement = conn.createStatement();
	    String sql = "SELECT location_name FROM locations";
	    ResultSet result = statement.executeQuery(sql);
	    List<String> list = new ArrayList<String>();
	    while (result.next()) {
	    	String location = result.getString(1);
	    	list.add(location);
		}
	    statement.close();
	    return list;
	}
}
