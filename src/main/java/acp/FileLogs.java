package acp;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import acp.ssb.table.TbPanel;
import acp.utils.*;

public class FileLogs extends MyInternalFrame {
  private static final long serialVersionUID = 1L;

  int fileId;
  
  final String tableName = "mss_logs";
  final String pkColumn = "mssl_id";
  String strAwhere;

  final String[] fields = {"mssl_id", "to_char(mssl_dt_event,'dd.mm.yyyy hh24:mi:ss') mssl_dt_event", "mssl_desc"};
  final String[] fieldnames = {"ID", Messages.getString("Column.Time"), Messages.getString("Column.Desc")};

  String strFields;
  String strFrom = tableName;
  String strWhere;
  String strOrder;

  TbPanel tabPanel;
  JTable  table;
  
  JPanel pnlButtons = new JPanel();
  JPanel pnlBtnExit = new JPanel();
  JButton btnClose = new JButton(Messages.getString("Button.Close"));
  
  public FileLogs(int file_id) {
    fileId = file_id;
    strAwhere = "mssl_ref_id=" + fileId;
//    strAwhere = "mssl_ref_id=" + fileId + " and rownum <=100";

    desktop.add(this);
    if (fileId > 0) {
      setTitle(Messages.getString("Title.AdvFileInfo"));
      setSize(1000, 500);
    } else {  
      setTitle(Messages.getString("Title.OtherLogs"));
      setSize(1200, 650);
    }  
    setToCenter();
    setMaximizable(true);
    setResizable(true);
    
    // --- Table ---
    tabPanel = new TbPanel(dbConnection);
    tabPanel.setHeaders(fieldnames);
    if (fileId > 0) {
      tabPanel.setQueryAllRecords(true);
    } else {
      tabPanel.setQueryAllRecords(false);
      tabPanel.setRecOnPage(30);
    }
    table = tabPanel.getTable();
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    //table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

    // Buttons ---
    pnlButtons.setLayout(new BorderLayout());
    pnlButtons.add(pnlBtnExit, BorderLayout.EAST);
    pnlBtnExit.add(btnClose);

    btnClose.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        dispose();
      }
    });

    // --- Layout ---
    Container cp = getContentPane();
    cp.setLayout(new BorderLayout());
    cp.add(tabPanel, BorderLayout.CENTER);
    cp.add(pnlButtons, BorderLayout.SOUTH);
  }

  public boolean initTable() {
    boolean res = false;
    // --------------------------------------------------
    strFields = DbUtils.buildSelectFields(fields, null);
    strWhere  = strAwhere;
    strOrder  = pkColumn;
//    strOrder  = null;
    // --------------------------------------------------
    String query = DbUtils.testQuery(dbConnection, strFields, strFrom, strWhere, strOrder);
    String queryCnt = DbUtils.buildQuery("select count(*) cnt", strFrom, strWhere, null);
    // --------------------------------------------------
    if (query != null) {
      tabPanel.setQuery(query, queryCnt);  
//    	tabPanel.refreshTable(tabPanel.NAV_FIRST);
      tabPanel.executeQuery();  
      if (fileId == 0) {
        tabPanel.selectFirst();
      }  
      res = true;
    }  
    return res;
  }

}
