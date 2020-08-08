package acp;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;

import javax.swing.*;
import javax.swing.border.*;

import acp.ssb.table.TbPanel;
import acp.utils.*;

public class ConstList extends MyInternalFrame {
  private static final long serialVersionUID = 1L;

  final String tableName = "mss_const";
  final String pkColumn = "mssc_id";
  String strAwhere = null;
  int seqId = 1000;

  final String[] fields = { "mssc_id", "mssc_name", "mssc_value" };
  final String[] fieldnames = { "ID"
      , Messages.getString("Column.Name")
      , Messages.getString("Column.Value") };
  
  String strFields;
  String strFrom = tableName;
  String strWhere;
  String strOrder;

  TbPanel tabPanel;
  JTable  table;

  JPanel pnlFilter = new JPanel();
  JPanel pnlBtnFilter = new JPanel();
  JPanel pnlButtons = new JPanel();
  JPanel pnlBtnRecord = new JPanel();
  JPanel pnlBtnAct = new JPanel();
  JPanel pnlBtnExit = new JPanel();

  JLabel lblName = new JLabel(Messages.getString("Column.Name"));
  JTextField txtName = new JTextField(20);

  JButton btnFilter = new JButton(Messages.getString("Button.Filter"));
  JButton btnFltClear = new JButton(Messages.getString("Button.Clear"));
  JButton btnAdd = new JButton(Messages.getString("Button.Add"));
  JButton btnEdit = new JButton(Messages.getString("Button.Edit"));
  JButton btnDelete = new JButton(Messages.getString("Button.Delete"));
  JButton btnRefresh = new JButton(Messages.getString("Button.Refresh"));
  JButton btnClose = new JButton(Messages.getString("Button.Close"));
  
  public ConstList() {
    desktop.add(this);
    setTitle(Messages.getString("Title.ConstList"));
    setSize(640, 480);
    setToCenter(); // метод из MyInternalFrame
    setMaximizable(true);
    setResizable(true);
    // setIconifiable(true);
    // setClosable(true);

    // --- Table ---
    tabPanel = new TbPanel(dbConnection);
    tabPanel.setHeaders(fieldnames);
    tabPanel.setQueryAllRecords(true);
//    tabPanel.setRecOnPage(25);
    table = tabPanel.getTable();
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    // table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
//    table.addMouseListener(new MyMouseListener());

    // Filter ---
    // pnlFilter.setBorder(new TitledBorder(new LineBorder(Color.BLACK),""));
    pnlFilter.setBorder(new TitledBorder(new LineBorder(Color.BLACK),Messages.getString("Title.Filter")));
    pnlFilter.setLayout(new GridBagLayout());
    GridBagConstraints cons = new GridBagConstraints();
    cons.insets = new Insets(0, 2, 2, 2);
//    cons.gridwidth = GridBagConstraints.HORIZONTAL;
    cons.weightx = 1.0;

    lblName.setLabelFor(txtName);
    lblName.setHorizontalAlignment(SwingConstants.RIGHT);

    cons.anchor = GridBagConstraints.WEST;
    pnlFilter.add(lblName, cons);
    
    cons.anchor = GridBagConstraints.WEST;
//    cons.gridwidth = GridBagConstraints.HORIZONTAL;
    pnlFilter.add(txtName, cons);

    cons.gridwidth = GridBagConstraints.REMAINDER;
    cons.anchor = GridBagConstraints.EAST;
    // cons.anchor = GridBagConstraints.LINE_START;
    // cons.anchor = GridBagConstraints.LINE_END;
    // cons.fill = GridBagConstraints.HORIZONTAL;
    cons.weightx = 1.0;
    pnlFilter.add(pnlBtnFilter, cons);

//    pnlBtnFilter.setLayout(new FlowLayout(FlowLayout.CENTER,5,0));
    pnlBtnFilter.setLayout(new GridLayout(1, 2, 2, 2));
    pnlBtnFilter.add(btnFilter);
    pnlBtnFilter.add(btnFltClear);
//    pnlBtnFilter.setBorder(new LineBorder(Color.BLACK));

    // Buttons ---
    pnlBtnRecord.add(btnAdd);
    pnlBtnRecord.add(btnEdit);
    pnlBtnRecord.add(btnDelete);
    pnlBtnAct.add(btnRefresh);
    pnlBtnExit.add(btnClose);

    pnlButtons.setLayout(new BorderLayout());
//    pnlButtons.add(pnlBtnRecord, BorderLayout.WEST);  //  !!!!!!
//    pnlButtons.add(pnlBtnAct, BorderLayout.CENTER);   //  !!!!!!
    pnlButtons.add(pnlBtnExit, BorderLayout.EAST);

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
    ConstEdit constEdit = new ConstEdit(tableName);
    boolean resInit = true;
    resInit = constEdit.initForm(act, recId);
    if (resInit) {
      desktop.add(constEdit);
      try {
        constEdit.setSelected(true);
      } catch (PropertyVetoException e1) {
      }
      // -----------------------
      constEdit.showModal(true);
      // -----------------------
      int resForm = constEdit.getResultForm();
      if (resForm == RES_OK) {
        if (act == ACT_NEW) { 
          tabPanel.refreshTable(tabPanel.NAV_LAST);
        } else {
          tabPanel.refreshTable(tabPanel.NAV_CURRENT);
        }
      }
    }
    constEdit = null;
  }

  private void clearFilter() {
    txtName.setText("");
  }

  private String getWherePhrase() {
    String phWhere = strAwhere;
    String str = null;
    if (!(txtName.getText()).equals("")) {
      str = "upper(mssc_name) like upper('" + txtName.getText() + "%')";
      phWhere = DbUtils.strAddAnd(phWhere, str);
    }
    return phWhere;
  }

  private boolean validateRecord(int recId) {
    if (recId < seqId) {
      DialogUtils.errorMsg(Messages.getString("Message.DeleteSystemRecord"));
      return false;
    }
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

/*  private class MyMouseListener extends MouseAdapter {
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
*/
}
