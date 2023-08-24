package top.wl.domain;

public class Category {
  private String cid;
  private String cname;

  public Category() {
  }

  public Category(String cid2, String cname2) {
    this.cid = cid2;
    this.cname = cname2;
  }

  public Category(String cid2) {
    this.cid = cid2;
  }

  public String getCid() {
    return cid;
  }

  public void setCid(String cid) {
    this.cid = cid;
  }

  public String getCname() {
    return cname;
  }

  public void setCname(String cname) {
    this.cname = cname;
  }

  @Override
  public String toString() {
    return "Category [cid=" + cid + ", cname=" + cname + "]";
  }

  public boolean isValidate() {
    return BeanUtils.isValidate(this.cid, true, 1, 32)
        && BeanUtils.isValidate(this.cname, true, 1, 20)
        ;
  }

  public boolean isValidate4Update() {
    return BeanUtils.isValidate(this.cid, true, 1, 32)
        && BeanUtils.isValidate(this.cname, false, 1, 20)
        ;
  }
  
  public boolean merge(Category c) {
    boolean flag = false;
    if (BeanUtils.isNeedUpdate(this.cname, c.cname)) {
      this.cname = c.cname;
      flag = true;
    }
    return flag;
  }

  public static boolean isIdValidate(String v) {
    return BeanUtils.isValidate(v, true, 1, 32);
  }
}
