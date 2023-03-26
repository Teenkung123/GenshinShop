package org.teenkung.genshinshop.manager;

import org.teenkung.genshinshop.GenshinShop;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySQLManager {

    private Connection connection;

    public void Connect() throws SQLException {
        if (!isConnected()) {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://" + GenshinShop.getInstance().getConfig().getString("MySQL.Host") + ":" + GenshinShop.getInstance().getConfig().getString("MySQL.Port")
                            + "/" + GenshinShop.getInstance().getConfig().getString("MySQL.Database") + "?useSSL=false&autoReconnect=true",
                    GenshinShop.getInstance().getConfig().getString("MySQL.User"),
                    GenshinShop.getInstance().getConfig().getString("MySQL.Password")
            );
        }
    }

    public Connection getConnection() { return connection; }

    public void Disconnect() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isConnected() {
        return connection != null;
    }

    public void createTable() throws SQLException {
        createPlayerDataTable();
        createLogsTable();
    }

    private void createPlayerDataTable() throws SQLException {
        String sql = "CREATE TABLE PlayerData (" +
                "ID int NOT NULL PRIMARY KEY, " +
                "UUID varchar(40), " +
                "Shop mediumtext, " +
                "ItemID mediumtext, " +
                "CooldownUntil bigint)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.executeUpdate();
    }

    private void createLogsTable() throws SQLException {
        String sql = "CREATE TABLE Logs (" +
                "ID int NOT NULL PRIMARY KEY, " +
                "UUID varchar(40), " +
                "Shop mediumtext, " +
                "ItemID mediumtext, " +
                "ItemData longtext, " +
                "BuyDate datetime)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.executeUpdate();
    }


}
