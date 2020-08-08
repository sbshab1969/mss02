package acp.ssb.combobox;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class CbModelClassDb extends CbModelClass {
  private static final long serialVersionUID = 1L;

  private Connection db;

  public CbModelClassDb(Connection db) {
    this.db = db;
  }

  public void executeQuery(String strQuery) {
    ArrayList<CbClass> anArrayList = new ArrayList<>();
    try {
      Statement statement = db.createStatement();
      ResultSet rs = statement.executeQuery(strQuery);
      while (rs.next()) {
        String key = rs.getString(1);
        String val = rs.getString(2);
        anArrayList.add(new CbClass(key, val));
      };
      setArrayList(anArrayList);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

}
