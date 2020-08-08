package acp.ssb.combobox;

public class CbClass {
  public String key;
  public String val;

  public CbClass(String key, String val) {
    this.key = key;
    this.val = val;
  }

  public String getKey() {
    return key;
  }

  public String getVal() {
    return val;
  }

  public String toString() {
//    return key + " - " + val;
    return val;
  }
}
