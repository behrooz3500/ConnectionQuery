package javafxtest;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Callback;
import java.io.PrintStream;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

//UPPERCASE WORDS refer to database variable and data

public class Controller {
    
    //this is a list for Storing rows of the TABLE
    @FXML
    ObservableList<ObservableList> data;
    
    @FXML
    private Button execute_query_btn;

    @FXML
    private TextField query_text;
    
    //table to be populated with DATA
    @FXML
    private TableView res_tbl;

    @FXML
    void OnKeyPressed(ActionEvent event) throws Exception {
        
        //////////////////////////////////////////////
        //Starting the Connection to perform queries//
        //////////////////////////////////////////////
        
        //Defining SQLServerDriver
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        
        
        //Url for connecting to the database
        //encrypt false: due to some restrictions with the old driver
        String connectionUrl =
                        "jdbc:sqlserver://DESKTOP-TS62FSA\\Moein;"
                        + "database=Moein;"
                        + "user=sa;"
                        + "password=arta0@;"
                        + "encrypt=false;"
                        + "trustServerCertificate=true;"
                        + "loginTimeout=30;";
        
        data = FXCollections.observableArrayList();
        try (Connection connection = DriverManager.getConnection(connectionUrl);) {
            
            //Statement Object for storing sql queries
            Statement stm = connection.createStatement();
            
            //obtaining the query text from gui textfield
            String query = query_text.getText();
            
            //ResultSet as a pointer to the table rows 
            ResultSet rs = stm.executeQuery(query);
            
            //fetching table data
            ResultSetMetaData rsmd = rs.getMetaData();
            
            
            ////////////////////////
            //Populating the table//
            ////////////////////////
            
            
            //obtaining TABLE COLUMN titles
            for (int i =0; i < rsmd.getColumnCount(); i++){
                final int j = i;
                TableColumn col = new TableColumn(rsmd.getColumnName(i+1));
                
                col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });
                
                //adding columns to the table and setting their titles
                res_tbl.getColumns().addAll(col);
                
            }
            
            //obtaining TABLE ROWS
            while(rs.next()){
                
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    //Iterate Column
                    //Checking for null values and replacing them with "null" string
                    if(rs.getString(i) == null){
                        row.add("NULL");
                    }
                    else{
                        row.add(rs.getString(i));
                    }
                    
                }
                //adding rows to the data observableList
                data.add(row);
            }
            
            //populating table with ObservableList data
            res_tbl.setItems(data);
            
            
        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
        }
    }

}