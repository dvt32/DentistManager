package net.dvt32.DentistManager.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by seun_ on 01-Mar-18.
 *
 */

/*
 * Modified by dvt32 on 23-Aug-18
 * 	- fixed connecting with JDBC connection string (second argument passed was 'username', instead of 'password')
 *  - allow parameter support with '?' after database name in JDBC URL
 *  
 *  Original project: https://github.com/SeunMatt/mysql-backup4j
 */
public class MysqlImportService {

    private String database;
    private String username;
    private String password;
    private String sqlString;
    private String jdbcConnString;
    private String jdbcDriver;
    private boolean deleteExisting;
    private boolean dropExisting;
    private List<String> tables;
    private Logger logger = LoggerFactory.getLogger(MysqlImportService.class);

    private MysqlImportService() {
        this.deleteExisting = false;
        this.dropExisting = false;
        this.tables = new ArrayList<>();
    }

    public boolean importDatabase() throws SQLException, ClassNotFoundException {

        if(!this.assertValidParams()) {
            logger.error("Required Parameters not set or empty \n" +
                    "Ensure database, username, password, sqlString params are configured \n" +
                    "using their respective setters");
            return false;
        }

        //connect
        //connect to the database

        Connection connection;
        if(jdbcConnString == null || jdbcConnString.isEmpty()) {
            connection = MysqlBaseService.connect(username, password,
                    database, jdbcDriver);
        }
        else {
            database = jdbcConnString.substring(jdbcConnString.lastIndexOf("/") + 1, jdbcConnString.indexOf("?"));
            logger.debug("database name extracted from connection string: " + database);
            connection = MysqlBaseService.connectWithURL(username, password,
                    jdbcConnString, jdbcDriver);
        }

        Statement stmt = connection.createStatement();

        //disable foreign key check
        stmt.addBatch("SET FOREIGN_KEY_CHECKS = 0");


         if(deleteExisting || dropExisting) {

             if(deleteExisting)
                logger.debug("deleteExisting flag is set to TRUE! I believe you know what you're doing");

             if(dropExisting)
                 logger.debug("dropExisting flag is set to TRUE! This will drop any existing table(s) in the database");

            //get all the tables
            tables = MysqlBaseService.getAllTables(database, stmt);

             //execute delete query
            for (String table: tables) {

                //if deleteExisting and dropExisting is true
                //skip the deleteExisting query
                //dropExisting will take care of both

                if(deleteExisting && !dropExisting) {
                    String delQ = "DELETE FROM `" + database + "`.`" + table + "`";
                    logger.debug("adding " + delQ + " to batch");
                    stmt.addBatch(delQ);
                }

                if(dropExisting) {
                    String dropQ = "DROP TABLE `" + database + "`.`" + table + "`";
                    logger.debug("adding " + dropQ + " to batch");
                    stmt.addBatch(dropQ);
                }

            }
        }

        //now process the sql string supplied
        while (sqlString.contains(MysqlBaseService.SQL_START_PATTERN)) {

            //get the chunk of the first statement to execute
            int startIndex = sqlString.indexOf(MysqlBaseService.SQL_START_PATTERN);
            int endIndex = sqlString.indexOf(MysqlBaseService.SQL_END_PATTERN);

            String executable = sqlString.substring(startIndex, endIndex);
            logger.debug("adding extracted executable SQL chunk to batch : \n" + executable);
            stmt.addBatch(executable);

            //remove the chunk from the whole to reduce it
            sqlString = sqlString.substring(endIndex + 1);

            //repeat
        }


        //add enable foreign key check
        stmt.addBatch("SET FOREIGN_KEY_CHECKS = 1");

        //now execute the batch
        long[] result = stmt.executeLargeBatch();

        final String[] resultString = {""};
        Arrays.stream(result).forEach(i -> resultString[0] = resultString[0].concat(i + " "));
        logger.debug( result.length + " queries were executed in batch for provided SQL String with the following result : \n" + resultString[0]);

        stmt.close();
        connection.close();

        return true;
    }

    private boolean assertValidParams() {
        return username != null && !this.username.isEmpty() &&
                password != null &&
                sqlString != null && !this.sqlString.isEmpty() &&
        ( (database != null && !this.database.isEmpty()) || (jdbcConnString != null && !jdbcConnString.isEmpty()) );
    }

    public static MysqlImportService builder() {
        return new MysqlImportService();
    }

    public MysqlImportService setDatabase(String database) {
        this.database = database;
        return this;
    }

    public MysqlImportService setUsername(String username) {
        this.username = username;
        return this;
    }

    public MysqlImportService setPassword(String password) {
        this.password = password;
        return this;
    }

    public MysqlImportService setSqlString(String sqlString) {
        this.sqlString = sqlString;
        return this;
    }

    public MysqlImportService setDeleteExisting(boolean deleteExisting) {
        this.deleteExisting = deleteExisting;
        return this;
    }

    public MysqlImportService setDropExisting(boolean dropExistingTable) {
        this.dropExisting = dropExistingTable;
        return  this;
    }

    public MysqlImportService setJdbcDriver(String jdbcDriver) {
        this.jdbcDriver = jdbcDriver;
        return this;
    }

    public MysqlImportService setJdbcConnString(String jdbcConnString) {
        this.jdbcConnString = jdbcConnString;
        return this;
    }
}
