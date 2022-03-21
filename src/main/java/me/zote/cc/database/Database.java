package me.zote.cc.database;

import com.google.common.collect.Lists;
import me.zote.cc.ChatChannels;

import java.io.File;
import java.sql.*;
import java.util.List;

public class Database {

    private Connection connection;
    private final File dbFile;

    public Database(ChatChannels plugin) {
        dbFile = new File(plugin.getDataFolder(), "users.db");
    }

    public void createTableIfNeeded() {
        String query = "CREATE TABLE IF NOT EXISTS user_channels (uuid TEXT UNIQUE NOT NULL PRIMARY KEY," +
                "channel TEXT," +
                "blacklist TEXT)";
        update(query);
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                connection = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection connection() {
        try {
            if (connection != null && !connection.isClosed())
                return connection;

            Class.forName("org.sqlite.JDBC");
            return connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void testQuery(String query) {

    }

    public List<ParsedData> query(String query, Object... args) {
        Connection connection = connection();

        if (connection == null)
            return null;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            for (int i = 0; i < args.length; i++) {
                statement.setObject(i + 1, args[i]);
            }
            List<ParsedData> parsedDataList = Lists.newArrayList();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                ParsedData parsedData = new ParsedData();

                ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
                int numberOfColumns = resultSetMetaData.getColumnCount();

                for (int i = 1; i < numberOfColumns + 1; i++) {
                    String name = resultSetMetaData.getColumnLabel(i);
                    parsedData.put(name, resultSet.getObject(name));
                }
                parsedDataList.add(parsedData);
            }

            resultSet.close();
            return parsedDataList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int update(String query, Object... args) {
        Connection connection = connection();

        if (connection == null)
            return -1;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            for (int i = 0; i < args.length; i++)
                statement.setObject(i + 1, args[i]);
            return statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

}
