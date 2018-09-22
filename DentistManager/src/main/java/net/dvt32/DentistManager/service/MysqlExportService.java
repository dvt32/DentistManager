package net.dvt32.DentistManager.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by seun_ on 24-Feb-18.
 *
 */

/*
 * Modified by dvt32:
 * - removed email send support
 * - removed zip support
 * - added ability to insert NULL value in tables
 * 
 * Original project: https://github.com/SeunMatt/mysql-backup4j
 */
public class MysqlExportService {

    private Statement stmt;
    private String database;
    private String generatedSql = "";
    private Logger logger = LoggerFactory.getLogger(getClass());
    private Properties properties;
    
    public static final String DB_NAME = "DB_NAME";
    public static final String DB_USERNAME = "DB_USERNAME";
    public static final String DB_PASSWORD = "DB_PASSWORD";
    public static final String ADD_IF_NOT_EXISTS = "ADD_IF_NOT_EXISTS";
    public static final String DROP_TABLES = "DROP_TABLES";
    public static final String DELETE_EXISTING_DATA = "DELETE_EXISTING_DATA";
    public static final String JDBC_CONNECTION_STRING = "JDBC_CONNECTION_STRING";
    public static final String JDBC_DRIVER_NAME = "JDBC_DRIVER_NAME";

    public MysqlExportService(Properties properties) {
        this.properties = properties;
    }

    private boolean validateProperties() {
        return 
        	properties != null &&
            properties.containsKey(DB_USERNAME) &&
            properties.containsKey(DB_PASSWORD) &&
            (properties.containsKey(DB_NAME) || properties.containsKey(JDBC_CONNECTION_STRING));
    }
    
    private String getTableInsertStatement(String table) throws SQLException {

        StringBuilder sql = new StringBuilder();
        ResultSet rs;
        boolean addIfNotExists = Boolean.parseBoolean(properties.containsKey(ADD_IF_NOT_EXISTS) ? properties.getProperty(ADD_IF_NOT_EXISTS, "true") : "true");
        boolean dropTable = Boolean.parseBoolean(properties.containsKey(DROP_TABLES) ? properties.getProperty(DROP_TABLES, "false") : "false");

        if(table != null && !table.isEmpty()){
          rs = stmt.executeQuery("SHOW CREATE TABLE `" + database + "`.`" + table + "`;");
          while ( rs.next() ) {
                String qtbl = rs.getString(1);
                String query = rs.getString(2);
                sql.append("\n\n--");
                sql.append("\n").append(MysqlBaseService.SQL_START_PATTERN).append("  table dump : ").append(qtbl);
                sql.append("\n--\n\n");

                if(addIfNotExists) {
                    query = query.trim().replace("CREATE TABLE", "CREATE TABLE IF NOT EXISTS ");
                }

                if(dropTable) {
                    sql.append("DROP TABLE IF EXISTS `").append(database).append("`.`").append(table).append("`;\n");
                }
                sql.append(query).append(";\n\n");
          }
        }

        sql.append("\n\n--");
        sql.append("\n").append(MysqlBaseService.SQL_END_PATTERN).append("  table dump : ").append(table);
        sql.append("\n--\n\n");

        return sql.toString();
    }

    private String getDataInsertStatement(String table) throws SQLException {

        StringBuilder sql = new StringBuilder();

        ResultSet rs = stmt.executeQuery("SELECT * FROM `" + database + "`.`" + table + "`;");
        rs.last();
        int rowCount = rs.getRow();

        //there are no records just return empty string
        if(rowCount <= 0) {
            return sql.toString();
        }

        sql.append("\n--").append("\n-- Inserts of ").append(table).append("\n--\n\n");

        //temporarily disable foreign key constraint
        sql.append("\n/*!40000 ALTER TABLE `").append(table).append("` DISABLE KEYS */;\n");

        boolean deleteExistingData = Boolean.parseBoolean(properties.containsKey(DELETE_EXISTING_DATA) ? properties.getProperty(DELETE_EXISTING_DATA, "false") : "false");

        if(deleteExistingData) {
            sql.append(MysqlBaseService.getEmptyTableSQL(database, table));
        }

        sql.append("\n--\n")
                .append(MysqlBaseService.SQL_START_PATTERN).append(" table insert : ").append(table)
                .append("\n--\n");

        sql.append("INSERT INTO `").append(table).append("`(");

        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        for(int i = 0; i < columnCount; i++) {
           sql.append("`")
                   .append(metaData.getColumnName( i + 1))
                   .append("`, ");
        }

        //remove the last whitespace and comma
        sql.deleteCharAt(sql.length() - 1).deleteCharAt(sql.length() - 1).append(") VALUES \n");

        //build the values
        rs.beforeFirst();
        while(rs.next()) {
           sql.append("(");
            for(int i = 0; i < columnCount; i++) {

                int columnType = metaData.getColumnType(i + 1);
                int columnIndex = i + 1;

                if( columnType == Types.INTEGER || columnType == Types.TINYINT || columnType == Types.BIT) {
                    sql.append(rs.getInt(columnIndex)).append(", ");
                } else {
                    String val = rs.getString(columnIndex) != null ? rs.getString(columnIndex) : "NULL";
                    val = val.replace("'", "\\'");
                    
                    if (val.equals("NULL")) {
                    	sql.append(val).append(", ");
                    }
                    else {
                    	sql.append("'").append(val).append("', ");
                    }    
                }
            }

            //now that we're done with a row

            //let's remove the last whitespace and comma
            sql.deleteCharAt(sql.length() - 1).deleteCharAt(sql.length() - 1);

            if(rs.isLast()) {
              sql.append(")");
            } else {
              sql.append("),\n");
            }
        }

        //now that we are done processing the entire row
        //let's add the terminator
        sql.append(";");

        sql.append("\n--\n")
                .append(MysqlBaseService.SQL_END_PATTERN).append(" table insert : ").append(table)
                .append("\n--\n");

        //enable FK constraint
        sql.append("\n/*!40000 ALTER TABLE `").append(table).append("` ENABLE KEYS */;\n");

        return sql.toString();
    }

    private String exportToSql() throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("--");
        sql.append("\n-- Generated by java-mysql-exporter");
        sql.append("\n-- Date: ").append(new SimpleDateFormat("d-M-Y H:m:s").format(new Date()));
        sql.append("\n--");

        //these declarations are extracted from HeidiSQL
        sql.append("\n\n/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;")
        .append("\n/*!40101 SET NAMES utf8 */;")
        .append("\n/*!50503 SET NAMES utf8mb4 */;")
        .append("\n/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;")
        .append("\n/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;");


        //get the tables
        List<String> tables = MysqlBaseService.getAllTables(database, stmt);

        //get the table insert statement for each table
        for (String s: tables) {
            try {
                sql.append(getTableInsertStatement(s.trim()));
                sql.append(getDataInsertStatement(s.trim()));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

       sql.append("\n/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;")
        .append("\n/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;")
        .append("\n/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;");

        this.generatedSql = sql.toString();
        return sql.toString();
    }

    public String export() throws IOException, SQLException, ClassNotFoundException {

        //check if properties is set or not
        if(!validateProperties()) {
            logger.error("Invalid config properties: The config properties is missing important parameters: DB_NAME, DB_USERNAME and DB_PASSWORD");
            return "";
        }

        //connect to the database
        database = properties.getProperty(DB_NAME);
        String jdbcURL = properties.getProperty(JDBC_CONNECTION_STRING, "");
        String driverName = properties.getProperty(JDBC_DRIVER_NAME, "");

        Connection connection;

        if(jdbcURL.isEmpty()) {
            connection = MysqlBaseService.connect(properties.getProperty(DB_USERNAME), properties.getProperty(DB_PASSWORD),
                    database, driverName);
        }
        else {
            database = jdbcURL.substring(jdbcURL.lastIndexOf("/") + 1, jdbcURL.indexOf("?"));
            logger.debug("database name extracted from connection string: " + database);
            connection = MysqlBaseService.connectWithURL(properties.getProperty(DB_USERNAME), properties.getProperty(DB_PASSWORD),
                    jdbcURL, driverName);
        }

        stmt = connection.createStatement();

        //generate the final SQL
        String sql = exportToSql();
        
        return sql;
    }

    public String getGeneratedSql() {
        return generatedSql;
    }
}
