package com;

import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class App {
	
	
	private static final String USERNAME = "Bij";
	private static final String PASSWORD = "Archos";
	private static final String NOM = "Nom_commune";
	
	public static void main(String[] args) {

		if (!args[0].isEmpty() && !args[1].isEmpty()) {
			double res = getDistanceEntreNom(args[0].toUpperCase(), args[1].toUpperCase());
			if (res == -1) {
				System.out.println("Une des ville demandé n'est pas enregistrée");
			} else {
				System.out.println("La distance entre ces deux communes est : " + res + "Km");
			}
			return;
		}
	}

	public static Connection connect() throws ClassNotFoundException {
		String url = "jdbc:mysql://localhost:3306/ville_france";
		Connection conn = null;
		try {
			conn = (Connection) DriverManager.getConnection(url, USERNAME, PASSWORD);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}

	public static void getVille() {
		String sql = "SELECT * from ville_france";
		try (Connection conn = connect(); PreparedStatement ps = (PreparedStatement) conn.prepareStatement(sql);) {

			ResultSet rs = ps.executeQuery();
			String nomVille = null;
			while (rs.next()) {
				nomVille = rs.getString(NOM);

				System.out.println("le nom de la ville est " + nomVille);

			}
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	public static String getVilleByCodePostal(String codePostal) {
		String sql = "SELECT * from ville_france where Code_postal=" + codePostal;
		String ret = new String();
		try (Connection conn = connect(); PreparedStatement ps = (PreparedStatement) conn.prepareStatement(sql);) {

			ResultSet rs = ps.executeQuery();
			String nomVille = null;
			while (rs.next()) {
				nomVille = rs.getString(NOM);
				ret = "Longitude=" + rs.getString("Longitude") + " | Latitude=" + rs.getString("Latitude");
			}
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("La ville n'est sans doute pas dans la base de donée");
		}
		return ret;
	}

	public static String getVilleByName(String name) {
		String sql = "SELECT * from ville_france where Nom_commune='" + name + "'";
		String ret = new String();
		try (Connection conn = connect(); PreparedStatement ps = (PreparedStatement) conn.prepareStatement(sql);) {

			ResultSet rs = ps.executeQuery();
			String nomVille = null;
			while (rs.next()) {
				nomVille = rs.getString(NOM);
				ret = "Longitude=" + rs.getString("Longitude") + " | Latitude=" + rs.getString("Latitude");
			}
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("La ville n'est sans doute pas dans la base de donée");
		}
		return ret;
	}

	private static double getDistanceEntreCodePostal(String codePostal1, String codePostal2) {
		String ville1 = getVilleByCodePostal(codePostal1);
		if (ville1 == null || ville1.isEmpty()) {
			return -1;
		}
		double latA = Double.parseDouble(ville1.substring(ville1.indexOf("=") + 1, ville1.indexOf("|") - 2));
		double lngA = Double.parseDouble(ville1.substring(ville1.lastIndexOf("=") + 1, ville1.length()));

		String ville2 = getVilleByCodePostal(codePostal2);
		if (ville2 == null || ville2.isEmpty()) {
			return -1;
		}
		double latB = Double.parseDouble(ville2.substring(ville2.indexOf("=") + 1, ville2.indexOf("|") - 2));
		double lngB = Double.parseDouble(ville2.substring(ville2.lastIndexOf("=") + 1, ville2.length()));

		return distance(latA, lngA, latB, lngB);
	}

	private static double getDistanceEntreNom(String nom1, String nom2) {
		String ville1 = getVilleByName(nom1);
		if (ville1 == null || ville1.isEmpty()) {
			return -1;
		}
		double latA = Double.parseDouble(ville1.substring(ville1.indexOf("=") + 1, ville1.indexOf("|") - 2));
		double lngA = Double.parseDouble(ville1.substring(ville1.lastIndexOf("=") + 1, ville1.length()));

		String ville2 = getVilleByName(nom2);
		if (ville2 == null || ville2.isEmpty()) {
			return -1;
		}
		double latB = Double.parseDouble(ville2.substring(ville2.indexOf("=") + 1, ville2.indexOf("|") - 2));
		double lngB = Double.parseDouble(ville2.substring(ville2.lastIndexOf("=") + 1, ville2.length()));

		return distance(latA, lngA, latB, lngB);
	}

	// Fonction de calcul de distance réupéré sur https://www.geodatasource.com/
	private static double distance(double lat1, double lon1, double lat2, double lon2) {
		if ((lat1 == lat2) && (lon1 == lon2)) {
			return 0;
		} else {
			double theta = lon1 - lon2;
			double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2))
					+ Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
			dist = Math.acos(dist);
			dist = Math.toDegrees(dist);
			dist = dist * 60 * 1.1515 * 1.609344;
			return (dist);
		}
	}

	public static void printVersion() {
		String version = System.getProperty("version");
		System.out.println(version);
	}

}
