package acp;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;

import javax.swing.*;

import acp.ssb.combobox.*;
import acp.ssb.table.TbPanel;

import acp.utils.*;

public class ConfigList extends MyInternalFrame {
  private static final long serialVersionUID = 1L;

  final String tableName = "mss_options";
  final String pkColumn = "msso_id";
  String strAwhere = "msso_msss_id=msss_id";
//  int seqId = 1000;

  final String[] fields = {"msso_id"
      , "msso_name"
      , "msss_name"
      , "to_char(msso_dt_begin,'dd.mm.yyyy')"
      , "to_char(msso_dt_end,'dd.mm.yyyy')"
      , "msso_comment"
      , "msso_owner"};
  final String[] fieldnames = {"ID"
      , Messages.getString("Column.Name")
      , Messages.getString("Column.SourceName")
      , Messages.getString("Column.DateBegin")
      , Messages.getString("Column.DateEnd")
      , Messages.getString("Column.Comment")
      , Messages.getString("Column.Owner") };

  String strFields;
  String strFrom = "mss_options, mss_source";
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

  JLabel lblName = new JLabel(Messages.getString("Column.Name"), JLabel.TRAILING);
  JTextField txtName = new JTextField(20);
  JLabel lblOwner = new JLabel(Messages.getString("Column.Owner"), JLabel.TRAILING);
  JTextField txtOwner = new JTextField(20);
  JLabel lblSource = new JLabel(Messages.getString("Column.SourceName"), JLabel.TRAILING);
  JComboBox<CbClass> cbdbSource;
  CbModelClassDb cbdbSourceModel;
  JPanel pnlEmpty41 = new JPanel();
  JPanel pnlEmpty42 = new JPanel();

  JButton btnFilter = new JButton(Messages.getString("Button.Filter"));
  JButton btnFltClear = new JButton(Messages.getString("Button.Clear"));
  JButton btnAdd = new JButton(Messages.getString("Button.Add"));
  JButton btnCopy = new JButton(Messages.getString("Button.Copy"));
  JButton btnEdit = new JButton(Messages.getString("Button.Edit"));
  JButton btnConfig = new JButton(Messages.getString("Button.Config"));
  JButton btnConfigXml = new JButton(Messages.getString("Button.ConfigXml"));
  JButton btnDelete = new JButton(Messages.getString("Button.Delete"));
  JButton btnRefresh = new JButton(Messages.getString("Button.Refresh"));
  JButton btnClose = new JButton(Messages.getString("Button.Close"));
  
  public ConfigList() {
    desktop.add(this);
    setTitle(Messages.getString("Title.ConfigList"));
    setSize(900, 600);
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
    cbdbSourceModel = new CbModelClassDb(dbConnection);
    cbdbSource = new JComboBox<CbClass>(cbdbSourceModel);
    
    lblName.setLabelFor(txtName);
    lblSource.setLabelFor(cbdbSource);
    lblOwner.setLabelFor(txtOwner);
//    pnlEmpty41.setBorder(new LineBorder(Color.BLACK));
//    pnlEmpty42.setBorder(new LineBorder(Color.BLACK));

    pnlFilter_1.setLayout(new SpringLayout());
    pnlFilter_1.add(lblName);
    pnlFilter_1.add(txtName);
    pnlFilter_1.add(lblSource);
    pnlFilter_1.add(cbdbSource);
    pnlFilter_1.add(lblOwner);
    pnlFilter_1.add(txtOwner);
    pnlFilter_1.add(pnlEmpty41);
    pnlFilter_1.add(pnlEmpty42);
    SpringUtilities.makeCompactGrid(pnlFilter_1,2,4,8,8,8,8);

    pnlFilter_2.setLayout(new FlowLayout());
//  pnlFilter_2.setLayout(new FlowLayout(FlowLayout.CENTER,6,6));
    pnlFilter_2.add(pnlBtnFilter);
    pnlBtnFilter.setLayout(new GridLayout(2,1,5,5));
    pnlBtnFilter.add(btnFilter);
    pnlBtnFilter.add(btnFltClear);
    
    pnlFilter.setLayout(new BorderLayout());
    pnlFilter.add(pnlFilter_1,BorderLayout.CENTER);
    pnlFilter.add(pnlFilter_2,BorderLayout.EAST);
//    pnlFilter.setBorder(new TitledBorder(new LineBorder(Color.BLACK),Messages.getString("Title.Filter")));
//    pnlFilter.setBorder(new LineBorder(Color.BLACK));
    
    // Buttons ---
    pnlBtnRecord.add(btnAdd);
    pnlBtnRecord.add(btnEdit);
    pnlBtnRecord.add(btnCopy);
    pnlBtnRecord.add(btnConfig);
    pnlBtnRecord.add(btnConfigXml);
    pnlBtnRecord.add(btnDelete);
    pnlBtnAct.add(btnRefresh);
    pnlBtnExit.add(btnClose);

    pnlButtons.setLayout(new BorderLayout());
    pnlButtons.add(pnlBtnRecord, BorderLayout.WEST);
    pnlButtons.add(pnlBtnAct, BorderLayout.CENTER);
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
    btnCopy.addActionListener(myActionListener);
    btnConfig.addActionListener(myActionListener);
    btnConfigXml.addActionListener(myActionListener);
    btnDelete.addActionListener(myActionListener);
    btnRefresh.addActionListener(myActionListener);
    btnClose.addActionListener(myActionListener);
  }

  public boolean initTable() {
    boolean res = false;
    // --------------------------------------------------
    String queryCbdb = "select msss_id, msss_name from mss_source order by msss_name";
    cbdbSourceModel.executeQuery(queryCbdb);
    cbdbSource.setSelectedIndex(-1);
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
    ConfigEdit cfgEdit = new ConfigEdit(tableName);
    boolean resInit = true;
    resInit = cfgEdit.initForm(act, recId);
    if (resInit) {
      desktop.add(cfgEdit);
      try {
        cfgEdit.setSelected(true);
      } catch (PropertyVetoException e1) {
      }
      // -----------------------
      cfgEdit.showModal(true);
      // -----------------------
      int resForm = cfgEdit.getResultForm();
      if (resForm == RES_OK) {
        if (act == ACT_NEW) { 
          tabPanel.refreshTable(tabPanel.NAV_LAST);
        } else {
          tabPanel.refreshTable(tabPanel.NAV_CURRENT);
        }
      }
    }
    cfgEdit = null;
  }

  private void editConfigTree(int recId) {
    ConfigTree cfgTree = new ConfigTree();
    boolean resInit = true;
    resInit = cfgTree.initForm(ACT_EDIT, recId);
    if (resInit) {
      desktop.add(cfgTree);
      try {
        cfgTree.setSelected(true);
      } catch (PropertyVetoException e1) {
      }
      // -----------------------
      cfgTree.showModal(true);
      // -----------------------
    }
    cfgTree = null;
  }

  private void editConfigXml(int recId) {
    ConfigXml cfgXml = new ConfigXml();
    boolean resInit = true;
    resInit = cfgXml.initForm(ACT_EDIT, recId);
    if (resInit) {
      desktop.add(cfgXml);
      try {
        cfgXml.setSelected(true);
      } catch (PropertyVetoException e1) {
      }
      // -----------------------
      cfgXml.showModal(true);
      // -----------------------
    }
    cfgXml = null;
  }

  private void clearFilter() {
    txtName.setText("");
    txtOwner.setText("");
    cbdbSource.setSelectedIndex(-1);
  }

  private String getWherePhrase() {
    String phWhere = strAwhere;
    String str = null;
    int index = -1;
    if (!(txtName.getText()).equals("")) {
      str = "upper(msso_name) like upper('" + txtName.getText() + "%')";
      phWhere = DbUtils.strAddAnd(phWhere, str);
    }
    if (!(txtOwner.getText()).equals("")) {
      str = "upper(msso_owner) like upper('" + txtOwner.getText() + "%')";
      phWhere = DbUtils.strAddAnd(phWhere, str);
    }
    if (cbdbSource.getSelectedIndex() != -1) {
      index = cbdbSource.getSelectedIndex();
      str = "msso_msss_id=" + cbdbSourceModel.getKeyStringAt(index);
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
    } else if (act == ACT_COPY) {
      query = new StringBuilder();
      query.append("insert into mss_options");
      query.append(" (select msso_seq.nextval, msso_name || '_copy', msso_config");
      query.append(", msso_dt_begin, msso_dt_end, msso_comment, sysdate, sysdate, user, msso_msss_id");
      query.append(" from mss_options where msso_id=" + recId + ")");
    }
    System.out.println(query);
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

      } else if (objSource.equals(btnCopy)) {
        Integer recordId = tabPanel.getRecordId();
        if (recordId != null) {
          int recId = recordId.intValue();
          if (DialogUtils.confirmDialog(Messages.getString("Message.CopyRecord") + " /id=" + recId + "/",
                     Messages.getString("Title.RecordCopy"), 1) == 0) {
            String query = createQuery(ACT_COPY, recId);
            if (query != null) {
              DbUtils.executeUpdate(dbConnection, query);
              tabPanel.refreshTable(tabPanel.NAV_CURRENT);
            } else {
              DialogUtils.errorMsg(Messages.getString("Message.EmptySelect"));
            }
          }  
        }

      } else if (objSource.equals(btnEdit)) {
        Integer recordId = tabPanel.getRecordId();
        if (recordId != null) {
          int recId = recordId.intValue();
          editRecord(ACT_EDIT,recId);
        }

      } else if (objSource.equals(btnConfig)) {
        Integer recordId = tabPanel.getRecordId();
        if (recordId != null) {
          int recId = recordId.intValue();
          editConfigTree(recId);
        }

      } else if (objSource.equals(btnConfigXml)) {
        Integer recordId = tabPanel.getRecordId();
        if (recordId != null) {
          int recId = recordId.intValue();
          if (DialogUtils.confirmDialog(Messages.getString("Message.DirectEditMsg"),
              Messages.getString("Title.Warning"), 1) == 0) {
            editConfigXml(recId);
          }  
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
