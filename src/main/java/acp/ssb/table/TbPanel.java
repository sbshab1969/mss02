package acp.ssb.table;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import acp.utils.DialogUtils;
import acp.utils.Messages;

public class TbPanel extends JPanel {
  private static final long serialVersionUID = 1L;

  final public int NAV_NONE = -1;
  final public int NAV_FIRST = 0;
  final public int NAV_LAST = 1;
  final public int NAV_CURRENT = 2;
  final public int NAV_ABSOLUTE = 3;

  JTable table;
  TbModel qtm;

  JToolBar tbNav;
  JButton btnFirst;
  JButton btnPrevious;
  JButton btnNext;
  JButton btnLast;
  JLabel lblCurrent;

  boolean queryAllRecords;

  public TbPanel(Connection conn) {
    setLayout(new BorderLayout());

    qtm = new TbModel(conn);
    table = new JTable(qtm);
    JScrollPane scrollpane = new JScrollPane(table);

    tbNav = new JToolBar();
    tbNav.setRollover(true);

    btnFirst = new JButton(new ImageIcon("images\\first.png"));
    btnPrevious = new JButton(new ImageIcon("images\\previous.png"));
    btnNext = new JButton(new ImageIcon("images\\next.png"));
    btnLast = new JButton(new ImageIcon("images\\last.png"));
    lblCurrent = new JLabel("");

    tbNav.add(btnFirst);
    tbNav.add(btnPrevious);
    tbNav.add(btnNext);
    tbNav.add(btnLast);
    tbNav.add(lblCurrent);

    add(tbNav, BorderLayout.NORTH);
    add(scrollpane, BorderLayout.CENTER);

    MyActionListener myActionListener = new MyActionListener();
    btnFirst.addActionListener(myActionListener);
    btnPrevious.addActionListener(myActionListener);
    btnNext.addActionListener(myActionListener);
    btnLast.addActionListener(myActionListener);

    setQueryAllRecords(false);
  }

  public JTable getTable() {
    return table;
  }

  public void setQueryAllRecords(boolean queryAllRec) {
    this.queryAllRecords = queryAllRec;
    tbNav.setVisible(!queryAllRecords);
  }

  public void setRecOnPage(int recOnPage) {
    qtm.setRecOnPage(recOnPage);;
  }

  public void setHeaders(String[] headers) {
    qtm.setHeaders(headers);
  }

  public String getPagePosition() {
  	String str = "   " + Messages.getString("Message.Page") + " " + qtm.getCurrPage() + 
  			" / " + qtm.getPageCount(); 
    return str;
  }

  public void setQuery(String qry, String qryCnt) {
    qtm.setQuery(qry, qryCnt);
    lblCurrent.setText(getPagePosition());
  }

  public void executeQuery() {
    qtm.executeQuery(queryAllRecords);
    resizeColumns();
    if (!queryAllRecords) {
      lblCurrent.setText(getPagePosition());
    }
  }

  public void refreshTable(int navMode, int absRecNum) {
    int currRecord = table.getSelectedRow();
    // -------------------
    executeQuery();  
    // -------------------
    int selRow = -1;
    int rows = table.getRowCount();
    if (rows > 0) {
      switch (navMode) {
        case NAV_FIRST:
          selRow = 0;
          break;
        case NAV_LAST:
          selRow = rows-1;
          break;
        case NAV_CURRENT:
          selRow = currRecord;
          break;
        case NAV_ABSOLUTE:
          selRow = absRecNum;
          break;
        default:
          selRow = 0;
      }  
      if (selRow < 0) {
        selRow = 0;
      }
      if (selRow >= rows) {
        selRow = rows-1;
      }
    }
    // -------------------
    if (selRow >= 0) {
      selectRow(selRow);
    }
    // -------------------
  }

  public void refreshTable(int navMode) {
    refreshTable(navMode,-1);
  }

  public void selectRow(int rowNum) {
  	int rowCount = table.getRowCount();
  	if (rowNum>=0 && rowNum<rowCount) {
      table.setRowSelectionInterval(rowNum, rowNum);
    }
  }

  public void selectFirst() {
    selectRow(0);
  }

  public void resizeColumns() {
    TableColumnModel columnModel = table.getColumnModel();
    TableColumn column = columnModel.getColumn(0);
    // -------------------
    column.setMinWidth(0);
    column.setMaxWidth(100);
    column.setPreferredWidth(60);
//    column.setWidth(25);
    // -------------------
  }

  public Integer getRecordId() {
    Integer result = null;
    int selectRow = table.getSelectedRow();
    if (selectRow>=0) {
      String res = (String) table.getValueAt(selectRow, 0); // ID
      result = Integer.valueOf(res);
    } else {
      DialogUtils.errorPrint(Messages.getString("Message.NoSelectRecord"));
    }  
    return result;
  }

  private class MyActionListener implements ActionListener {
    public void actionPerformed(ActionEvent ae) {
      Object objSource = ae.getSource();
      if (objSource.equals(btnFirst)) {
        qtm.firstPage();
      } else if (objSource.equals(btnPrevious)) {
        qtm.previousPage();
      } else if (objSource.equals(btnNext)) {
        qtm.nextPage();
      } else if (objSource.equals(btnLast)) {
        int cntPages = qtm.getPageCount();
        if (cntPages > 100) {
          String message = Messages.getString("Message.ManyPageCount") + " /" + cntPages + "/. " + 
          		Messages.getString("Message.Continue");  
          if (DialogUtils.confirmDialog(message, Messages.getString("Title.Warning"), 1) == 0) {
            qtm.lastPage();
          }
        } else {
          qtm.lastPage();
        }
      }
      lblCurrent.setText(getPagePosition());
      selectFirst();    
      resizeColumns();
    }
  }

}
