package top.mryan2005.simplifiedjava.SQLs;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.mryan2005.simplifiedjava.SQLs.Exceptions.SQLServerDatabaseNotFoundException;
import top.mryan2005.simplifiedjava.SQLs.Exceptions.SQLServerNotNULLException;
import top.mryan2005.simplifiedjava.SQLs.Exceptions.SQLServerPrimaryKeyException;
import top.mryan2005.simplifiedjava.SQLs.Exceptions.throwSQLServerException;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class SQLServer {
    private static final Logger log = LoggerFactory.getLogger(SQLServer.class);
    public Connection con;
    public String ip;
    public String port;
    public String database;

    public Connection ConnectToSQLServer(String inputIp, String inputPort, String inputDatabase, String inputUsername, String inputPassword, boolean encrypt) throws ClassNotFoundException, SQLException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            con = DriverManager.getConnection("jdbc:sqlserver://" + inputIp + ":" + inputPort + ";databaseName=" + inputDatabase + ";user=" + inputUsername + ";password=" + inputPassword + ";encrypt=" + encrypt + ";");
            ip = inputIp;
            port = inputPort;
            database = inputDatabase;
        } catch (SQLException e) {
            throwSQLServerException.throwExeption(e.getErrorCode(), e.getMessage());
        }
        return con;
    }

    public Connection ConnectToSQLServer(String inputIp, String inputPort, String inputUsername, String inputPassword, boolean encrypt) throws ClassNotFoundException, SQLException {
        Class.forName( "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        con = DriverManager.getConnection("jdbc:sqlserver://" + inputIp + ":" + inputPort + ";user=" + inputUsername + ";password=" + inputPassword + ";"+ "encrypt=" + encrypt + ";");
        return con;
    }

    public void CloseConnection() throws SQLException {
        con.close();
    }

    public ResultSet runSQL(String sql) throws SQLException {
        boolean result = false;
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            result = stmt.execute(sql);
        } catch (SQLServerException e) {
             if (e.getMessage().matches("(.*)PRIMARY KEY(.*)")) {
                 throw new SQLServerPrimaryKeyException(e.getMessage());
             } else if(e.getMessage().matches("(.*)INSERT(.*)")) {
                 if (e.getMessage().matches("(.*)NULL(.*)")) {
                     throw new SQLServerNotNULLException(e.getMessage());
                 }
             } else if(e.getMessage().matches("(.*)数据库(.*)不存在")) {
                     throw new SQLServerDatabaseNotFoundException(e.getMessage());
             } else if(e.getMessage().matches("(.*)1111(.*)")) {
                 throw new SQLServerDatabaseNotFoundException(e.getMessage());
             } else {
                 throw e;
             }
        }
        if (result && stmt != null) {
            return stmt.getResultSet();
        } else {
            return null;
        }
    }

    public Connection getSQLer() {
        return con;
    }

    public HashMap<Object, ArrayList<Object>> runSQL(String sql, String primaryKey) {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            HashMap<Object, ArrayList<Object>> result = new HashMap<>();
            while(rs.next()) {
                ArrayList<Object> row = new ArrayList<>();
                for(int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    row.add(rs.getObject(i));
                }
                result.put(rs.getObject(primaryKey), row);
            }
            return result;
        } catch (Exception e) {
            System.out.println("执行SQL语句时发生错误！");
            System.out.println(e);
            return null;
        }
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        SQLServer sqlServer = new SQLServer();
        sqlServer.ConnectToSQLServer("localhost", "1433", "sa", "123456", false);
        sqlServer.CloseConnection();
        sqlServer.ConnectToSQLServer("localhost", "1433", "1111", "sa", "123456", false);
        sqlServer.CloseConnection();
    }
}
