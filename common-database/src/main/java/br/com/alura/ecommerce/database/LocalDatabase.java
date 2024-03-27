package br.com.alura.ecommerce.database;

import java.sql.*;

public class LocalDatabase {
    private final Connection connection;

    public LocalDatabase(String name) throws SQLException {
        String url = "jdbc:sqlite:target/"+ name +".db";
        connection = DriverManager.getConnection(url);
    }

    // yes, this is way too generic
    public void createIfNotExists(String sql){
        try {
            connection.createStatement().execute(sql);
        } catch (SQLException ex) {
            // be careful, the sql could be wrong, be reallllllly careful
            ex.printStackTrace();
        }
    }

    public void update(String statement, String ... params) throws SQLException {
        getPreparedStatement(statement, params).execute();
    }

    private PreparedStatement getPreparedStatement(String statement, String[] params) throws SQLException {
        var prepareStatement = connection.prepareStatement(statement);
        for(int i = 0; i < params.length; i++){
            prepareStatement.setString(i + 1, params[i]);
        }
        return prepareStatement;
    }

    public ResultSet query(String query, String ... params) throws SQLException {
        return getPreparedStatement(query, params).executeQuery();
    }
}
