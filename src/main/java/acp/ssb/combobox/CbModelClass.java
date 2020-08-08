package acp.ssb.combobox;

import java.util.ArrayList;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

public class CbModelClass extends AbstractListModel<CbClass> implements ComboBoxModel<CbClass> {
  private static final long serialVersionUID = 1L;

  private ArrayList<CbClass> anArrayList;
  private CbClass selectedObject;

  public CbModelClass() {
    init();
  }

  public CbModelClass(ArrayList<CbClass> arrayList) {
    setArrayList(arrayList);
  }

  private void init() {
    anArrayList = new ArrayList<>();
    selectedObject = null;
  }

  public void setArrayList(ArrayList<CbClass> arrayList) {
    anArrayList = arrayList;
    if (getSize() > 0) {
      selectedObject = anArrayList.get(0);
    }  
  }

  public int getSize() {
    return anArrayList.size();
  }

  public CbClass getElementAt(int index) {
    if (index < 0 || index >= getSize()) {
      return null;
    }
    return anArrayList.get(index);
  }

  public Object getSelectedItem() {
    return selectedObject;
  }

  public void setSelectedItem(Object newValue) {
    if ((selectedObject != null && !selectedObject.equals(newValue)) ||
        (selectedObject == null && newValue != null)) {
      selectedObject = (CbClass) newValue;
      fireContentsChanged(this, -1, -1);
    }
  }

  public String getKeyStringAt(int index) {
    if (index < 0 || index >= getSize()) {
      return null;
    }
    CbClass item = anArrayList.get(index);
    String key = item.getKey(); 
    return key;
  }

  public void setKeyString(String key) {
    CbClass selObject = null;
    for (CbClass item : anArrayList) {
      String itemKey = item.getKey(); 
      if (itemKey.equals(key)) {
        selObject = item;
        break;
      }
    }
    setSelectedItem(selObject);
  }

  public int getKeyIntAt(int index) {
    int keyInt = -1;
    String key = getKeyStringAt(index); 
    if (key != null) {
      keyInt = Integer.valueOf(key);
    }  
    return keyInt;
  }

  public void setKeyInt(int keyInt) {
    String key = String.valueOf(keyInt);
    setKeyString(key);
  }

}
