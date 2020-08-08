package acp.ssb.table; 

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import acp.utils.DbUtils;

class TbModel extends AbstractTableModel {
  private static final long serialVersionUID = 1L;

  Connection db;
  Statement statement;
  ResultSet rs;

  String strQuery;
  String strQueryCnt;

  String[] headers;

  int colCount;
  int rowCount;
  ArrayList<String[]> cache;

  int recCount;
  int recStart;
  int recOnPage = 20;

  int currPage;
  int pageCount;
  
  public TbModel(Connection conn) {
    db = conn;
    cache = new ArrayList<>();
//    cache = new ArrayList<String[]>();
  }

  public String getColumnName(int i) {
    return headers[i];
  }

  public int getColumnCount() {
    return colCount;
  }

  public int getRowCount() {
    return cache.size();
  }

  public Object getValueAt(int row, int col) {
    return cache.get(row)[col];
  }

  public int getRecOnPage() {
    return recOnPage;
  }

  public void setRecOnPage(int recOnPage) {
    this.recOnPage = recOnPage;
  }

  public int getPageCount() {
    return pageCount;
  }

  public int getCurrPage() {
    return currPage;
  }

  public void calcPages() {
    recCount = DbUtils.getValueN(db, strQueryCnt);
    if (recCount > 0) {
      int fullPageCount = recCount / recOnPage;
      int tail = recCount - fullPageCount * recOnPage;
      if (tail == 0) {
        pageCount = fullPageCount;
      } else {
        pageCount = fullPageCount + 1;
      }
    } else {
      pageCount = 0;
    }
  }

  public void calcRecStart() {
    if (currPage > 0) {
      recStart = (currPage - 1) * recOnPage + 1;
    } else {
      recStart = 0;
    }
  }

  public void setHeaders(String[] headers) {
    this.headers = headers;
    if (headers == null) {
      colCount = 0;
    } else {
      colCount = headers.length;
    }
  }

  public void fillHeaders() {
    try {
      ResultSetMetaData meta = rs.getMetaData();
      colCount = meta.getColumnCount();
      headers = new String[colCount];
      for (int h = 1; h <= colCount; h++) {
        headers[h-1] = meta.getColumnName(h);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
  
  public void setQuery(String strQry, String strQryCnt) {
    strQuery = strQry;
    if (strQryCnt == null) {
    	strQueryCnt = "select count(*) cnt from (" + strQry + ")";
    } else {
      strQueryCnt = strQryCnt;
    }
  }

  public void executeQuery(boolean queryAll) {
    try {
      statement = db.createStatement(
                  ResultSet.TYPE_SCROLL_INSENSITIVE, 
                  ResultSet.CONCUR_UPDATABLE);
      rs = statement.executeQuery(strQuery);
      if (headers == null) {
        fillHeaders();
      }
      if (queryAll) {
        fetchAll();
      } else {
        startPage();
      }
    } catch (SQLException e) {
      cache = new ArrayList<>();
      e.printStackTrace();
    }
  }

  public void startPage() {
    if (rs == null) {
      return;
    }
    calcPages();
    if (pageCount>0) {
      currPage = 1;
    } else {
      currPage = 0;
    }
    fetchPage();
  }

  public void firstPage() {
    if (rs == null) {
      return;
    }
    calcPages();
    if (currPage>1) {
      currPage = 1;
    }
    fetchPage();
  }

  public void previousPage() {
    if (rs == null) {
      return;
    }
    calcPages();
    if (currPage > 1) {
      currPage--;
    }
    fetchPage();
  }

  public void nextPage() {
    if (rs == null) {
      return;
    }
    calcPages();
    if (currPage < pageCount) {
      currPage++;
    }
    fetchPage();
  }

  public void lastPage() {
    if (rs == null) {
      return;
    }
    calcPages();
    if (currPage < pageCount) {
      currPage = pageCount;
    }  
    fetchPage();
  }

  public void fetchPage() {
    cache = new ArrayList<>();
    calcRecStart();
    if (recStart<=0 || recStart>recCount) {
      fireTableChanged(null);
      return;
    }
    try {
      rs.absolute(recStart);
      int recCnt = 0;
      do {
        recCnt++;
        String[] record = new String[colCount];
        for (int i = 0; i < colCount; i++) {
          record[i] = rs.getString(i + 1);
        }
        cache.add(record);
      } while (rs.next() && recCnt < recOnPage);
      fireTableChanged(null);
    } catch (SQLException e) {
      cache = new ArrayList<>();
      e.printStackTrace();
    }
  }

  public void fetchAll() {
    cache = new ArrayList<>();
    try {
      int recCnt = 0;
      while (rs.next()) {
        recCnt++;
        String[] record = new String[colCount];
        for (int i = 0; i < colCount; i++) {
          record[i] = rs.getString(i + 1);
        }
        cache.add(record);
      };
      rowCount = recCnt;
      fireTableChanged(null);
    } catch (SQLException e) {
      cache = new ArrayList<>();
      e.printStackTrace();
    }
  }

}
