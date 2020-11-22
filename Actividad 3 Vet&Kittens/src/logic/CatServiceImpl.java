package logic;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import data.database.connection.ConnectionProvider;
import data.model.cat.Cat;
import data.model.login.Login;
import gui.view.component.JFocusField;
import gui.view.component.JFocusPasswordField;
import logic.service.exception.AccessDeniedException;

public class CatServiceImpl implements iCatService {

	public static CatServiceImpl getCatService() {
		return new CatServiceImpl();
	}

	public List<Cat> queryCatTable(String id, String chipSerial, String name, String weight, String dob, String vaccineA)
			throws SQLException {

		String sqlQuery;
		Connection con = null;
		Statement stmt;
		ResultSet rs;

		Integer idCat;
		Long chipSerialCat;
		String nameCat;
		Double weightCat;
		Date dobCat;
		Date vaccineACat;

		List<Cat> catList = null;
		Cat catInstance;

		try {
			con = ConnectionProvider.getMariaConnection();

			sqlQuery = "SELECT * FROM Cat WHERE 1 = 1 ";
			if (id != null && !id.equals("")) { // para que no genere una query err�nea al dejar el JTextField en blanco
				sqlQuery += " AND ID = '" + id + "'";
			} else if (chipSerial != null && !chipSerial.equals("")) {
				sqlQuery += " AND ChipSerial = '" + chipSerial + "'";
			} else if (name != null && !name.equals("")) {
				sqlQuery += " AND NAME = '" + name + "'";
			} else if (weight != null && !weight.equals("")) {
				sqlQuery += " AND WEIGHT = '" + weight + "'";
			} else if (dob != null && !dob.equals("")) {
				sqlQuery += " AND DOB = '" + dob + "'";
			} else if (vaccineA != null && !vaccineA.equals("")) {
				sqlQuery += " AND VACCINEA = '" + vaccineA + "'";
			}

			System.out.println(sqlQuery);

			stmt = con.createStatement();
			rs = stmt.executeQuery(sqlQuery);

			while (rs.next()) {

				if (catList == null) {
					catList = new ArrayList<Cat>();
				}

				catInstance = new Cat();

				idCat = rs.getInt("ID");
				catInstance.setId(idCat);

				chipSerialCat = rs.getLong("ChipSerial");
				catInstance.setChipSerial(chipSerialCat);

				nameCat = rs.getString("Name");
				catInstance.setName(nameCat);

				weightCat = rs.getDouble("Weight");
				catInstance.setWeight(weightCat);

				dobCat = rs.getDate("DOB");
				catInstance.setDob(dobCat);

				vaccineACat = rs.getDate("VaccineA");
				catInstance.setVaccineA(vaccineACat);

				catList.add(catInstance);
			}

		} catch (SQLException e) {

			System.err.println("Database access error occursor or closed connection" + e.getMessage());
			e.printStackTrace();
		} finally {
			con.close();
		}

		return catList;
	}

	public Integer insertCat(Cat cat) throws SQLException {
		Integer rowInsertNumber = 0;
		Connection con = null;
		String sqlQuery = "";
		Statement stmt;

		sqlQuery = "INSERT INTO edusoft.cat " + "(ChipSerial, Name, Weight, DOB, VaccineA) " + "VALUES (" + cat.getChipSerial() + ", '"
				+ cat.getName() + "'" + ", " + cat.getWeight() + ",'" + cat.getDob() + "'" + ",'" + cat.getVaccineA() + "')";
		System.out.println(sqlQuery);
		try {

			con = ConnectionProvider.getMariaConnection();
			stmt = con.createStatement();
			rowInsertNumber = stmt.executeUpdate(sqlQuery);
			System.out.println(rowInsertNumber);
		} catch (Exception e) {
			System.err.println("Error al insertar datos en la base de datos");
			e.printStackTrace();
		} finally {
			con.close();
		}

		if (rowInsertNumber > 0) {
			System.out.println("gato insertado");
		}
		return rowInsertNumber;

	}

	@Override
	public void checkPassword(Login login) throws AccessDeniedException, SQLException {
		// TODO probar a rellenar la tabla login con varios usuarios y ver si esta
		Boolean loginCorrect = true;
		String sqlQuery;
		Connection con = null;
		Statement stmt;
		ResultSet rs;

		sqlQuery = "SELECT * FROM Login ";
		try {
			con = ConnectionProvider.getMariaConnection();
			stmt = con.createStatement();
			rs = stmt.executeQuery(sqlQuery);
			while (rs.next()) {
				if (!login.getUser().equalsIgnoreCase(rs.getString("UserName"))) { // may�sculas o min�sculas
					loginCorrect = false;
				}
				if (!login.getPassword().equalsIgnoreCase(rs.getString("Password"))) {
					loginCorrect = false;
				}

			}
			con.close();
		} catch (SQLException e) {
			throw new SQLException("DATA problem. Call customer support. Review config.properties file");
		}

		if (loginCorrect) {
			return;
		} else {
			throw new AccessDeniedException("Usuario y/o contrase�a incorrectos");
		}

	}

	@SuppressWarnings("deprecation")
	@Override
	public Login retrieveLogin(JFocusField user, JFocusPasswordField password) {
		return new Login(user.getText(), password.getText());
	}

	public void LoginDataBase() {

	}

}
