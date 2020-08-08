package acp;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.*;

import acp.ssb.table.TbPanel;
import acp.utils.*;

public class FileList extends MyInternalFrame {
  private static final long serialVersionUID = 1L;

  final String tableName = "mss_files";
  final String pkColumn = "mssf_id";
  String strAwhere = null;

  final String[] fields = { "mssf_id", "mssf_name", "mssf_md5", "mssf_owner", 
  		"to_char(mssf_dt_work,'dd.mm.yyyy hh24:mi:ss') mssf_dt_work",
      "extract(mssf_statistic,'statistic/records/all/text()').getStringval() rec_count"};
  final String[] fieldnames = { 
        "ID"
      , Messages.getString("Column.FileName")
      , "MD5"
      , Messages.getString("Column.Owner")
      , Messages.getString("Column.DateWork")
      , Messages.getString("Column.RecordCount")};

  String strFields;
  String strFrom = tableName;

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
  JPanel pnlBtnExit = new JPanel();

  SimpleDateFormat formatDate = new SimpleDateFormat("dd.MM.yyyy");
  NumberFormat formatNumb = NumberFormat.getInstance();

  JLabel lblFileName = new JLabel(Messages.getString("Column.FileName"));
  JTextField txtFileName = new JTextField(20);
  JLabel lblOwner = new JLabel(Messages.getString("Column.Owner"));
  JTextField txtOwner = new JTextField(20);

  JLabel lblDtBegin = new JLabel(Messages.getString("Column.DateWork") + 
      Messages.getString("Column.Begin")); // , JLabel.TRAILING
  JLabel lblDtEnd = new JLabel(Messages.getString("Column.End"), JLabel.CENTER);
  JFormattedTextField dtBegin = new JFormattedTextField(formatDate);
  JFormattedTextField dtEnd = new JFormattedTextField(formatDate);

  JLabel lblRecBegin = new JLabel(Messages.getString("Column.RecordCount") + 
      Messages.getString("Column.Begin"));
  JLabel lblRecEnd = new JLabel(Messages.getString("Column.End"), JLabel.CENTER);
  JFormattedTextField recBegin = new JFormattedTextField(formatNumb);
  JFormattedTextField recEnd = new JFormattedTextField(formatNumb);
  
  JButton btnFilter = new JButton(Messages.getString("Button.Filter"));
  JButton btnFltClear = new JButton(Messages.getString("Button.Clear"));
  
  JButton btnInfo = new JButton(Messages.getString("Button.Info"));
  JButton btnLogs = new JButton(Messages.getString("Button.Logs"));
  JButton btnClose = new JButton(Messages.getString("Button.Close"));
  
  public FileList() {
    desktop.add(this);
    setTitle(Messages.getString("Title.FileList"));
    setSize(1200, 650);
    setToCenter(); // метод из MyInternalFrame
    setMaximizable(true);
    setResizable(true);

    // --- Table ---
    tabPanel = new TbPanel(dbConnection);
    tabPanel.setHeaders(fieldnames);
    tabPanel.setQueryAllRecords(false);
    tabPanel.setRecOnPage(25);
    table = tabPanel.getTable();
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    //table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    table.addMouseListener(new MyMouseListener());

    // Filter ---
    pnlFilter.setLayout(new BorderLayout());
//    pnlFilter.setLayout(new GridBagLayout());
//    pnlFilter.setBorder(new TitledBorder(new LineBorder(Color.BLACK),Messages.getString("Title.Filter")));

    lblFileName.setLabelFor(txtFileName);
    lblOwner.setLabelFor(txtOwner);

    Calendar gcBefore = new GregorianCalendar();
    gcBefore.add(Calendar.DAY_OF_YEAR, -7);
//    gcBefore.add(Calendar.MONTH, -1);
//    gcBefore.add(Calendar.YEAR, -2);
    Date dtBefore = gcBefore.getTime();
    Date dtNow = new Date();
    dtBegin.setValue(dtBefore);
    dtEnd.setValue(dtNow);
    
//  recBegin.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT); // default
//  recBegin.setFocusLostBehavior(JFormattedTextField.COMMIT);
//  recBegin.setFocusLostBehavior(JFormattedTextField.REVERT);
//  recBegin.setFocusLostBehavior(JFormattedTextField.PERSIST);
    
    pnlFilter_1.add(lblFileName);
    pnlFilter_1.add(txtFileName);
    pnlFilter_1.add(lblOwner);
    pnlFilter_1.add(txtOwner);

    pnlFilter_1.setLayout(new SpringLayout());
//    pnlFilter_1.setBorder(new LineBorder(Color.BLACK));
    pnlFilter_1.add(lblDtBegin);
    pnlFilter_1.add(dtBegin);
    pnlFilter_1.add(lblDtEnd);
    pnlFilter_1.add(dtEnd);
    
    pnlFilter_1.add(lblRecBegin);
    pnlFilter_1.add(recBegin);
    pnlFilter_1.add(lblRecEnd);
    pnlFilter_1.add(recEnd);
    
    SpringUtilities.makeCompactGrid(pnlFilter_1,3,4,8,8,8,8);

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
    pnlButtons.add(pnlBtnExit, BorderLayout.EAST);

    pnlBtnRecord.add(btnInfo);
    pnlBtnRecord.add(btnLogs);
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
    btnInfo.addActionListener(myActionListener);
    btnLogs.addActionListener(myActionListener);
    btnClose.addActionListener(myActionListener);
  }

  public boolean initTable() {
    boolean res = false;
    // --------------------------------------------------
    strFields = DbUtils.buildSelectFields(fields, null);
    strWhere  = getWherePhrase();
//    strWhere  += " and rownum<=2010";
    strOrder  = null;
//    strOrder  = pkColumn;
//    strOrder  = "mssf_dt_work";
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

  private void showInfo(int recId) {
    FileInfo fileInfo = new FileInfo();
    boolean resInit = true;
    resInit = fileInfo.initForm(ACT_GET,recId);
    if (resInit) {
      desktop.add(fileInfo);
      try {
        fileInfo.setSelected(true);
      } catch (PropertyVetoException e1) {
      }
      // -----------------------
      fileInfo.showModal(true);
      // -----------------------
    }
    fileInfo = null;
  }
  
  private void showLogs(int recId) {
    FileLogs fileLog = new FileLogs(recId);
    boolean resInit = true;
    resInit = fileLog.initTable();
    if (resInit) {
      try {
        fileLog.setSelected(true);
      } catch (PropertyVetoException e1) {
      }
      // -----------------------
      fileLog.showModal(true);
      // -----------------------
    }
    fileLog = null;
  }

  private void clearFilter() {
    txtFileName.setText("");
    txtOwner.setText("");
    dtBegin.setValue(null);
    dtEnd.setValue(null);
    recBegin.setValue(null);
    recEnd.setValue(null);
  }

  private String getWherePhrase() {
    String phWhere = strAwhere;
    String vField = "";
    String vBeg = "";
    String vEnd = "";
    String valueBeg = "";
    String valueEnd = "";
    String str = null;
    // ----------------------
    if (!(txtFileName.getText()).equals("")) {
      vField = "upper(mssf_name)";
      str = vField + " like upper('" + txtFileName.getText() + "%')";
      phWhere = DbUtils.strAddAnd(phWhere, str);
    }
    // ----------------------
    if (!(txtOwner.getText()).equals("")) {
      vField = "upper(mssf_owner)";
      str = vField + " like upper('" + txtOwner.getText() + "%')";
      phWhere = DbUtils.strAddAnd(phWhere, str);
    }
    // ----------------------
    vField = "trunc(mssf_dt_work)";
    vBeg = dtBegin.getText();
    vEnd = dtEnd.getText();
    valueBeg = "to_date('" +  vBeg +"','dd.mm.yyyy')";
    valueEnd = "to_date('" +  vEnd +"','dd.mm.yyyy')";
    if (!vBeg.equals("") || !vEnd.equals("")) {
      if (!vBeg.equals("") && !vEnd.equals("")) {
        str = vField + " between " + valueBeg + " and " + valueEnd;
      } else if (!vBeg.equals("") && vEnd.equals("")) {
        str = vField + " >= " + valueBeg;
      } else if (vBeg.equals("") && !vEnd.equals("")) {
        str = vField + " <= " + valueEnd;
      }
      phWhere = DbUtils.strAddAnd(phWhere, str);
    }  
    // ----------------------
    vField = "to_number(extract(mssf_statistic,'statistic/records/all/text()').getstringval())";
    vBeg = recBegin.getText();
    vEnd = recEnd.getText();
    valueBeg = vBeg;
    valueEnd = vEnd;
    if (!vBeg.equals("") || !vEnd.equals("")) {
      if (!vBeg.equals("") && !vEnd.equals("")) {
        str = vField + " between " + valueBeg + " and " + valueEnd;
      } else if (!vBeg.equals("") && vEnd.equals("")) {
        str = vField + " >= " + valueBeg;
      } else if (vBeg.equals("") && !vEnd.equals("")) {
        str = vField + " <= " + valueEnd;
      }
      phWhere = DbUtils.strAddAnd(phWhere, str);
    }  
    // ----------------------
    return phWhere;
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

      } else if (objSource.equals(btnInfo)) {
        Integer recordId = tabPanel.getRecordId();
        if (recordId != null) {
          int recId = recordId.intValue();
          showInfo(recId);
        }

      } else if (objSource.equals(btnLogs)) {
        Integer recordId = tabPanel.getRecordId();
        if (recordId != null) {
          int recId = recordId.intValue();
          showLogs(recId);
        }

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
          showInfo(recId);
        }
      }
    }
  }

}
