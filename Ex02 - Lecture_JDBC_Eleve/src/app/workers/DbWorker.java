package app.workers;

import app.beans.Personne;
import app.exceptions.MyDBException;
import app.helpers.SystemLib;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DbWorker implements DbWorkerItf {

    private Connection dbConnexion;
    private List<Personne> listePersonnes;
    private int index = 0;

    /**
     * Constructeur du worker
     */
    public DbWorker() {
    }

    @Override
    public void connecterBdMySQL(String nomDB ) throws MyDBException {
        final String url_local = "jdbc:mysql://localhost:3306/" + nomDB;
        final String url_remote = "jdbc:mysql://LAPEMFB37-21.edu.net.fr.ch:3306/" + nomDB;
        final String user = "root";
        final String password = "Emf123";

        System.out.println("url:" + url_local);
        try {
            dbConnexion = DriverManager.getConnection(url_local, user, password);
        } catch (SQLException ex) {
            throw new MyDBException(SystemLib.getFullMethodName(), ex.getMessage());
        }
    }

    @Override
    public void connecterBdHSQLDB(String nomDB) throws MyDBException {
        final String url = "jdbc:hsqldb:file:" + nomDB + ";shutdown=true";
        final String user = "SA";
        final String password = "";
        System.out.println("url:" + url);
        try {
            dbConnexion = DriverManager.getConnection(url, user, password);
        } catch (SQLException ex) {
            throw new MyDBException(SystemLib.getFullMethodName(), ex.getMessage());
        }
    }

    @Override
    public void connecterBdAccess(String nomDB) throws MyDBException {
        final String url = "jdbc:ucanaccess://" + nomDB;
        System.out.println("url=" + url);
        try {
            dbConnexion = DriverManager.getConnection(url);
        } catch (SQLException ex) {
            throw new MyDBException(SystemLib.getFullMethodName(), ex.getMessage());
        }
    }

    @Override
    public void deconnecter() throws MyDBException {
        try {
            if (dbConnexion != null) {
                dbConnexion.close();
            }
        } catch (SQLException ex) {
            throw new MyDBException(SystemLib.getFullMethodName(), ex.getMessage());
        }
    }

    public List<Personne> lirePersonnes() throws MyDBException {
        listePersonnes = new ArrayList<>();
        Statement st;
        try {
            st = dbConnexion.createStatement();
            ResultSet rs = st.executeQuery("select prenom, nom from t_personne");
            while (rs.next()) {
                String nom = rs.getString("Nom");
                String prenom = rs.getString("Prenom");
                Personne personne = new Personne(nom, prenom);
                listePersonnes.add(personne);                
            }
            st.close();
            rs.close();
        } catch (SQLException | NullPointerException ex) {

            throw new MyDBException(SystemLib.getFullMethodName(), ex.getMessage());

       }
        
        return listePersonnes;
    }

    @Override
    public Personne precedentPersonne() throws MyDBException {
       if (listePersonnes == null) {
            lirePersonnes();
        }
        Personne result;
        index = index-1;
        if (index < 0) {
            index = index +1;
        }    
        result = listePersonnes.get(index);      
        return result;

   }

    @Override
    public Personne suivantPersonne() throws MyDBException {
         if (listePersonnes == null) {
            lirePersonnes();
        }        
        Personne result;
        if(index <= listePersonnes.size()){
        index = index+1;
        }
        if (index >= listePersonnes.size()) {
            index = index-1;
            result = listePersonnes.get(index);
        }
        result = listePersonnes.get(index);
return result;
      
   }
}
