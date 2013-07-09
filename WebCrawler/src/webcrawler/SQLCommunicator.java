/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webcrawler;

import com.sun.corba.se.impl.util.Version;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import org.apache.log4j.Logger;

/**
 *
 * @author Adeesha
 */
public class SQLCommunicator {
        private static Connection con = null;
        private static Statement st = null;
        private static ResultSet rs = null;

        private static String database="test";
        private static String url = "jdbc:mysql://localhost:3306/"+database+"?useUnicode=true&characterEncoding=utf-8";
        private static String user = "root";
        private static String password = "fun123";
    
    
    public static void InsertInToTable(String table,String[] values){   
        try {            
            con = DriverManager.getConnection(url, user, password);
            st =  con.createStatement();
            
            String insertSQL = "INSERT INTO "+table+"  VALUES (";
            for (int i = 0; i < values.length; i++) {
                insertSQL = insertSQL +"?";
                if(i<values.length-1){
                    insertSQL+=",";
                }
            }
            insertSQL+=")";
            PreparedStatement preparedStatement = con.prepareStatement(insertSQL);
            //System.out.println(insertSQL);
            for (int i = 0; i < values.length; i++) {
                preparedStatement.setString(i+1, values[i]);
            }
            preparedStatement.executeUpdate();

        } catch (SQLException ex) {
            //ex.printStackTrace();
            //Logger lgr = Logger.getLogger(Version.class.getName());
            //lgr.log(Level.SEVERE, ex.getMessage(), ex);

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                //Logger lgr = Logger.getLogger(Version.class.getName());
              //  lgr.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
    }
    
}