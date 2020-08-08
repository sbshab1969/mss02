package acp;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.util.ArrayList;

import javax.swing.*;

import acp.ssb.combobox.*;
import acp.ssb.table.TbPanel;
import acp.utils.*;

public class XmlTableList extends MyInternalFrame {
  private static final long serialVersionUID = 1L;

  private ArrayList<String> params;

  String  tableName = "mss_options";
  String  strAwhere = null;
//  int     seqId = 1000;

  String[] fields;
  String[] fieldnames; 
  String   pkColumn;

  String strFields;
  String strFrom;
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

  JLabel lblSource = new JLabel(Messages.getString("Column.SourceName"), JLabel.TRAILING);
  JComboBox<CbClass> cbdbSource;
  CbModelClassDb cbdbSourceModel;

  JButton btnFilter = new JButton(Messages.getString("Button.Filter"));
  JButton btnFltClear = new JButton(Messages.getString("Button.Clear"));
  JButton btnEdit = new JButton(Messages.getString("Button.Edit"));
  JButton btnRefresh = new JButton(Messages.getString("Button.Refresh"));
  JButton btnClose = new JButton(Messages.getString("Button.Close"));
  
  public XmlTableList(ArrayList<String> pars, String keyTitle) {
    this.params = pars;
    desktop.add(this);
    setTitle(FieldConfig.getString(keyTitle));
    setSize(640, 480);
    setToCenter(); // метод из MyInternalFrame
    setMaximizable(true);
    setResizable(true);

    // --- Table ---
    createFields();
    tabPanel = new TbPanel(dbConnection);
    tabPanel.setHeaders(fieldnames);
    tabPanel.setQueryAllRecords(true);
//    tabPanel.setRecOnPage(25);
    table = tabPanel.getTable();
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    // table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    table.addMouseListener(new MyMouseListener());

    // Filter ---
    cbdbSourceModel = new CbModelClassDb(dbConnection);
    cbdbSource = new JComboBox<CbClass>(cbdbSourceModel);
   
    pnlFilter.setLayout(new BorderLayout());
    lblSource.setLabelFor(cbdbSource);

    pnlFilter_1.setLayout(new SpringLayout());
//    pnlFilter_1.setBorder(new LineBorder(Color.BLACK));
    pnlFilter_1.add(lblSource);
    pnlFilter_1.add(cbdbSource);
    SpringUtilities.makeCompactGrid(pnlFilter_1,1,2,8,8,8,8);

    pnlFilter_2.setLayout(new FlowLayout());
    pnlFilter_2.add(pnlBtnFilter);
    pnlBtnFilter.setLayout(new GridLayout(1,2,5,5));
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

    pnlBtnRecord.add(btnEdit);
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
    btnEdit.addActionListener(myActionListener);
    btnRefresh.addActionListener(myActionListener);
    btnClose.addActionListener(myActionListener);
  }

  private void createFields() {
    String[] path = params.get(0).split("/");
    fields = new String[params.size()+2];
    fieldnames = new String[params.size()+2];
    //---
    fields[0] = "CONFIG_ID";
    fieldnames[0] = "ID";
    pkColumn = fields[0];
    //---
    for (int i = 1; i < params.size(); i++) {
      fields[i] = "P" + i;
      fieldnames[i] = FieldConfig.getString(path[path.length - 1] + "." + params.get(i));
    }
    //---
    fields[params.size()] = "to_char(DATE_BEGIN,'dd.mm.yyyy') DATE_BEGIN";
    fieldnames[params.size()] = Messages.getString("Column.DateBegin");
    //---
    fields[params.size() + 1] = "to_char(DATE_END,'dd.mm.yyyy') DATE_END";
    fieldnames[params.size() + 1] = Messages.getString("Column.DateEnd");
    //---
  }

  private String createTable(long src) {
    String res = "table(mss.spr_options(" + src + ",'" + params.get(0) + "'";
    for (int i = 1; i < params.size(); i++) {
      res += ",'" + params.get(i) + "'";
    }
    for (int i = params.size(); i <= 5; i++) {
      res += ",null";
    }
    res += "))";
    return res;
  }

  public boolean initTable() {
    boolean res = false;
    // --------------------------------------------------
    String queryCbdb = "select msss_id, msss_name from mss_source order by msss_name";
    cbdbSourceModel.executeQuery(queryCbdb);
    cbdbSource.setSelectedIndex(-1);
    // --------------------------------------------------
    strFields = DbUtils.buildSelectFields(fields, null);
    strFrom   = createTable(-1);
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
    XmlTableEdit xmlEdit = new XmlTableEdit(params);
    boolean resInit = false;
    resInit = xmlEdit.initForm(act, recId);
    if (resInit) {
      desktop.add(xmlEdit);
      try {
        xmlEdit.setSelected(true);
      } catch (PropertyVetoException e1) {
      }
      // -----------------------
      xmlEdit.showModal(true);
      // -----------------------
      int resForm = xmlEdit.getResultForm();
      if (resForm == RES_OK) {
        if (act == ACT_NEW) { 
          tabPanel.refreshTable(tabPanel.NAV_LAST);
        } else {
          tabPanel.refreshTable(tabPanel.NAV_CURRENT);
        }
      }
    }
    xmlEdit = null;
  }

  private void clearFilter() {
    cbdbSource.setSelectedIndex(-1);
  }

  private class MyActionListener implements ActionListener {
    public void actionPerformed(ActionEvent ae) {
      Object objSource = ae.getSource();
      if (objSource.equals(btnFilter)) {
        // --------------------------------------------------
        int index = cbdbSource.getSelectedIndex();
        int keyInt = cbdbSourceModel.getKeyIntAt(index);
        strFrom = createTable(keyInt);
        String phWhere = strAwhere;
        strOrder = pkColumn;
        // --------------------------------------------------
        String query = DbUtils.testQuery(dbConnection, strFields, strFrom, phWhere, strOrder);
        if (query != null) {
          strWhere = phWhere;
          String queryCnt = DbUtils.buildQuery("select count(*) cnt", strFrom, strWhere, null);
          tabPanel.setQuery(query, queryCnt);  
            tabPanel.refreshTable(tabPanel.NAV_FIRST);
        }  

      } else if (objSource.equals(btnFltClear)) {
        clearFilter();
        strFrom = createTable(-1);
        strWhere = strAwhere;
        String query = DbUtils.testQuery(dbConnection, strFields, strFrom, strWhere, strOrder);
        String queryCnt = DbUtils.buildQuery("select count(*) cnt", strFrom, strWhere, null);
        tabPanel.setQuery(query, queryCnt);  
        tabPanel.refreshTable(tabPanel.NAV_FIRST);

      } else if (objSource.equals(btnEdit)) {
        Integer recordId = tabPanel.getRecordId();
        if (recordId != null) {
          int recId = recordId.intValue();
          editRecord(ACT_EDIT,recId);
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
