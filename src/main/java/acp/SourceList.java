package acp;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;

import javax.swing.*;

import acp.ssb.table.TbPanel;
import acp.utils.*;

public class SourceList extends MyInternalFrame {
  private static final long serialVersionUID = 1L;

  final String tableName = "mss_source";
  final String pkColumn = "msss_id";
  String strAwhere = null;
  int seqId = 1000;

  final String[] fields = {"msss_id","msss_name","msss_owner"};
  final String[] fieldnames = { "ID"
      , Messages.getString("Column.Name")
      , Messages.getString("Column.Owner") };

  String strFields;
  String strFrom = tableName;;
  String strWhere;
  String strOrder;

  TbPanel tabPanel;
  JTable  table;

  JPanel pnlFilter = new JPanel();
  JPanel pnlFilter_1 = new JPanel();
  JPanel pnlFilter_2 = new JPanel();
  JPanel pnlBtnFilter = new JPanel();

  JPanel pnlButtons = new JPanel();
  JPanel pnlBtnRecord = new JPanel();
  JPanel pnlBtnAct = new JPanel();
  JPanel pnlBtnExit = new JPanel();

  JLabel lblName = new JLabel(Messages.getString("Column.Name"));
  JTextField txtName = new JTextField(20);
  JLabel lblOwner = new JLabel(Messages.getString("Column.Owner"));
//  JLabel lblOwner = new JLabel(Messages.getString("Column.Owner"), JLabel.TRAILING);
  JTextField txtOwner = new JTextField(20);

  JButton btnFilter = new JButton(Messages.getString("Button.Filter"));
  JButton btnFltClear = new JButton(Messages.getString("Button.Clear"));
  JButton btnAdd = new JButton(Messages.getString("Button.Add"));
  JButton btnEdit = new JButton(Messages.getString("Button.Edit"));
  JButton btnDelete = new JButton(Messages.getString("Button.Delete"));
  JButton btnRefresh = new JButton(Messages.getString("Button.Refresh"));
  JButton btnClose = new JButton(Messages.getString("Button.Close"));
  
  public SourceList() {
    desktop.add(this);
    setTitle(Messages.getString("Title.SourceList"));
    setSize(640, 480);
    setToCenter(); // метод из MyInternalFrame
    setMaximizable(true);
    setResizable(true);

    // --- Table ---
    tabPanel = new TbPanel(dbConnection);
    tabPanel.setHeaders(fieldnames);
    tabPanel.setQueryAllRecords(true);
//    tabPanel.setRecOnPage(25);
    table = tabPanel.getTable();
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    // table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    table.addMouseListener(new MyMouseListener());

    // Filter ---
    pnlFilter.setLayout(new BorderLayout());
//    pnlFilter.setLayout(new GridBagLayout());
//    pnlFilter.setBorder(new TitledBorder(new LineBorder(Color.BLACK),Messages.getString("Title.Filter")));
    lblName.setLabelFor(txtName);

    pnlFilter_1.setLayout(new SpringLayout());
//    pnlFilter_1.setBorder(new LineBorder(Color.BLACK));
    pnlFilter_1.add(lblName);
    pnlFilter_1.add(txtName);
    pnlFilter_1.add(lblOwner);
    pnlFilter_1.add(txtOwner);
    SpringUtilities.makeCompactGrid(pnlFilter_1,2,2,8,8,8,8);

    pnlFilter_2.setLayout(new FlowLayout());
//  pnlFilter_2.setLayout(new FlowLayout(FlowLayout.CENTER,6,6));
    pnlFilter_2.add(pnlBtnFilter);

    pnlBtnFilter.setLayout(new GridLayout(2,1,5,5));
    pnlBtnFilter.add(btnFilter);
    pnlBtnFilter.add(btnFltClear);

    pnlFilter.setLayout(new BorderLayout());
    pnlFilter.add(pnlFilter_1,BorderLayout.CENTER);
    pnlFilter.add(pnlFilter_2,BorderLayout.EAST);
    
    // Buttons ---
    pnlButtons.setLayout(new BorderLayout());
    pnlButtons.add(pnlBtnRecord, BorderLayout.WEST);
    pnlButtons.add(pnlBtnAct, BorderLayout.CENTER);
    pnlButtons.add(pnlBtnExit, BorderLayout.EAST);

    pnlBtnRecord.add(btnAdd);
    pnlBtnRecord.add(btnEdit);
    pnlBtnRecord.add(btnDelete);
    pnlBtnAct.add(btnRefresh);
    pnlBtnExit.add(btnClose);

    // --- Layout ---
    Container cp = getContentPane();
    cp.setLayout(new BorderLayout()); // default layout for JFrame
    cp.add(pnlFilter, BorderLayout.NORTH);
    cp.add(tabPanel, BorderLayout.CENTER);
    cp.add(pnlButtons, BorderLayout.SOUTH);

    // Listeners ---
    MyActionListener myActionListener = new MyActionListener();
    btnFilter.addActionListener(myActionListener);
    btnFltClear.addActionListener(myActionListener);
    btnAdd.addActionListener(myActionListener);
    btnEdit.addActionListener(myActionListener);
    btnDelete.addActionListener(myActionListener);
    btnRefresh.addActionListener(myActionListener);
    btnClose.addActionListener(myActionListener);
  }

  public boolean initTable() {
    boolean res = false;
    // --------------------------------------------------
    strFields = DbUtils.buildSelectFields(fields, null);
    strWhere  = strAwhere;
    strOrder  = pkColumn;
    // --------------------------------------------------
    String query = DbUtils.testQuery(dbConnection, strFields, strFrom, strWhere, strOrder);
    if (query != null) {
      String queryCnt = DbUtils.buildQuery("select count(*) cnt", strFrom, strWhere, null);
      tabPanel.setQuery(query, queryCnt);  
      tabPanel.refreshTable(tabPanel.NAV_FIRST);
      res = true;
    }  
    return res;
  }

  private void editRecord(int act, int recId) {
    SourceEdit srcEdit = new SourceEdit(tableName);
    boolean resInit = true;
    resInit = srcEdit.initForm(act, recId);
    if (resInit) {
      desktop.add(srcEdit);
      try {
        srcEdit.setSelected(true);
      } catch (PropertyVetoException e1) {
      }
      // -----------------------
      srcEdit.showModal(true);
      // -----------------------
      int resForm = srcEdit.getResultForm();
      if (resForm == RES_OK) {
        if (act == ACT_NEW) { 
          tabPanel.refreshTable(tabPanel.NAV_LAST);
        } else {
          tabPanel.refreshTable(tabPanel.NAV_CURRENT);
        }
      }
    }
    srcEdit = null;
  }

  private void clearFilter() {
    txtName.setText("");
    txtOwner.setText("");
  }

  private String getWherePhrase() {
    String phWhere = strAwhere;
    String str = null;
    if (!(txtName.getText()).equals("")) {
      str = "upper(msss_name) like upper('" + txtName.getText() + "%')";
      phWhere = DbUtils.strAddAnd(phWhere, str);
    }
    if (!(txtOwner.getText()).equals("")) {
      str = "upper(msss_owner) like upper('" + txtOwner.getText() + "%')";
      phWhere = DbUtils.strAddAnd(phWhere, str);
    }
    return phWhere;
  }

  private boolean validateRecord(int recId) {
//    if (recId < seqId) {
//      DialogUtils.errorMsg(Messages.getString("Message.DeleteSystemRecord"));
//      return false;
//    }
    return true;
  }

  private String createQuery(int act, int recId) {
    StringBuilder query = null;
    if (act == ACT_DELETE) {
      query = new StringBuilder();
      query.append("delete from " + tableName + " where " + pkColumn + "=" + recId); 
    }
//    System.out.println(query);
    if (query == null) {
      return null;
    } else {
      return query.toString();
    }  
  }

  private class MyActionListener implements ActionListener {
    public void actionPerformed(ActionEvent ae) {
      Object objSource = ae.getSource();
      if (objSource.equals(btnFilter)) {
        String phWhere = getWherePhrase();
        String query = DbUtils.testQuery(dbConnection, strFields, strFrom, phWhere, strOrder);
        if (query != null) {
          strWhere = phWhere;
          String queryCnt = DbUtils.buildQuery("select count(*) cnt", strFrom, strWhere, null);
          tabPanel.setQuery(query, queryCnt);  
        	tabPanel.refreshTable(tabPanel.NAV_FIRST);
        }  

      } else if (objSource.equals(btnFltClear)) {
        clearFilter();
        strWhere = strAwhere;
        String query = DbUtils.testQuery(dbConnection, strFields, strFrom, strWhere, strOrder);
        String queryCnt = DbUtils.buildQuery("select count(*) cnt", strFrom, strWhere, null);
        tabPanel.setQuery(query, queryCnt);  
      	tabPanel.refreshTable(tabPanel.NAV_FIRST);

      } else if (objSource.equals(btnAdd)) {
        editRecord(ACT_NEW,-1);

      } else if (objSource.equals(btnEdit)) {
        Integer recordId = tabPanel.getRecordId();
        if (recordId != null) {
          int recId = recordId.intValue();
          editRecord(ACT_EDIT,recId);
        }

      } else if (objSource.equals(btnDelete)) {
        Integer recordId = tabPanel.getRecordId();
        if (recordId != null) {
          int recId = recordId.intValue();
          // editRecord(ACT_DELETE,recId);
          boolean resValidate = validateRecord(recId);  
          if (resValidate) {
            if (DialogUtils.confirmDialog(Messages.getString("Message.DeleteRecord") + " /id=" + recId + "/",
                       Messages.getString("Title.RecordDelete"), 1) == 0) {
              String query = createQuery(ACT_DELETE, recId);
              if (query != null) {
                DbUtils.executeUpdate(dbConnection, query);
              	tabPanel.refreshTable(tabPanel.NAV_CURRENT);
              } else {
                DialogUtils.errorMsg(Messages.getString("Message.EmptySelect"));
              }
            }  
          }
        }

      } else if (objSource.equals(btnRefresh)) {
      	tabPanel.refreshTable(tabPanel.NAV_CURRENT);

      } else if (objSource.equals(btnClose)) {
        dispose();
      }
    }
  }

  private class MyMouseListener extends MouseAdapter {
    public void mouseClicked(MouseEvent e) {
      if (e.getClickCount() == 2) {
        Integer recordId = tabPanel.getRecordId();
        if (recordId != null) {
          int recId = recordId.intValue();
          editRecord(ACT_EDIT,recId);
        }
      }
    }
  }

}
