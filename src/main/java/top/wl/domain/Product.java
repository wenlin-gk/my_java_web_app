package top.wl.domain;

import java.text.SimpleDateFormat;
import java.util.Date;

import top.wl.utils.UUIDUtils;

public class Product {
  public final static int PRODUCT_IS_HOT = 1;
  public final static int PRODUCT_IS_UP = 0;
  public final static int PRODUCT_IS_DOWN = 1;

  private String pid;
  private String pname;
  private Double market_price;

  private Double shop_price;
  private String pimage;
  private String pdate;

  private Integer is_hot; // 是否热门 1:热门 0:不热门
  private String pdesc;
  private Integer pflag; // 是否下架 1:下架 0:未下架

  // 在多的一方放入一个一的一方的对象 用来表示属于那个分类
  private String cid;
  private Category category;

  public Product() {
  }

  public Product(String pid2, String pname2) {
    this.pid = pid2;
    this.pname = pname2;
  }

  public Product(String pid2) {
    this.pid = pid2;
  }

  public String getCid() {
    return cid;
  }

  public void setCid(String cid) {
    this.cid = cid;
  }

  public String getPid() {
    return pid;
  }

  public void setPid(String pid) {
    this.pid = pid;
  }

  public String getPname() {
    return pname;
  }

  public void setPname(String pname) {
    this.pname = pname;
  }

  public Double getMarket_price() {
    return market_price;
  }

  public void setMarket_price(Double market_price) {
    this.market_price = market_price;
  }

  public Double getShop_price() {
    return shop_price;
  }

  public void setShop_price(Double shop_price) {
    this.shop_price = shop_price;
  }

  public String getPimage() {
    return pimage;
  }

  public void setPimage(String pimage) {
    this.pimage = pimage;
  }

  public String getPdate() {
    return pdate;
  }

  public void setPdate(String pdate) {
    this.pdate = pdate;
  }

  public Integer getIs_hot() {
    return is_hot;
  }
  
  public void setIs_hot(Integer is_hot) {
    this.is_hot = is_hot;
  }

  public String getPdesc() {
    return pdesc;
  }

  public void setPdesc(String pdesc) {
    this.pdesc = pdesc;
  }

  public Integer getPflag() {
    return pflag;
  }

  public void setPflag(Integer pflag) {
    this.pflag = pflag;
  }

  public Category getCategory() {
    return category;
  }

  public void setCategory(Category category) {
    this.category = category;
  }

  public String toString() {
    return "Product [pid=" + pid + ", pname=" + pname + ", market_price="
        + market_price + ", shop_price=" + shop_price + ", pimage=" + pimage
        + ", pdate=" + pdate + ", is_hot=" + is_hot + ", pdesc=" + pdesc
        + ", pflag=" + pflag + ", cid=" + cid + ", category=" + category + "]";
  }
  public boolean isValidate() {
    return BeanUtils.isValidate(this.pname, true, 1, 50)
        && BeanUtils.isValidate(this.market_price, true, 1, 20*10000)
        && BeanUtils.isValidate(this.shop_price, true, 1, 20*10000)
        && BeanUtils.isValidate(this.pimage, false, 1, 200)
        && BeanUtils.isValidate4Datetime(this.pdate, false, 1, 50)
        && BeanUtils.isValidate(this.is_hot, false, 0, 1)
        && BeanUtils.isValidate(this.pdesc, false, 1, 255)
        && BeanUtils.isValidate(this.pflag, false, 0, 1)
        && BeanUtils.isValidate(this.cid, true, 1, 32)
        ;
  }

  public boolean isValidate4Update() {
    return BeanUtils.isValidate(this.pid, true, 1, 32)
        && BeanUtils.isValidate(this.pname, false, 1, 50)
        && BeanUtils.isValidate(this.market_price, false, 1, 20*10000)
        && BeanUtils.isValidate(this.shop_price, false, 1, 20*10000)
        && BeanUtils.isValidate(this.pimage, false, 1, 200)
        && BeanUtils.isValidate4Datetime(this.pdate, false, 1, 50)
        && BeanUtils.isValidate(this.is_hot, false, 0, 1)
        && BeanUtils.isValidate(this.pdesc, false, 1, 255)
        && BeanUtils.isValidate(this.pflag, false, 0, 1)
        && BeanUtils.isValidate(this.cid, false, 1, 32)
        ;
  }
  public boolean equalsIgnoreImage(Product p) {
    return BeanUtils.isEqual(this.pid, p.pid)
        && BeanUtils.isEqual(this.pname, p.pname)
        && BeanUtils.isEqual(this.market_price, p.market_price)
        && BeanUtils.isEqual(this.shop_price, p.shop_price)
        && BeanUtils.isEqual(this.pdate, p.pdate)
        && BeanUtils.isEqual(this.is_hot, p.is_hot)
        && BeanUtils.isEqual(this.pdesc, p.pdesc)
        && BeanUtils.isEqual(this.pflag, p.pflag)
        && BeanUtils.isEqual(this.cid, p.cid)
        ;
  }
  public boolean equalsIgnorePdatePdesc(Product p) {
    return BeanUtils.isEqual(this.pimage, p.pimage)
        && BeanUtils.isEqual(this.pid, p.pid)
        && BeanUtils.isEqual(this.pname, p.pname)
        && BeanUtils.isEqual(this.market_price, p.market_price)
        && BeanUtils.isEqual(this.shop_price, p.shop_price)
        && BeanUtils.isEqual(this.is_hot, p.is_hot)
        && BeanUtils.isEqual(this.pflag, p.pflag)
        && BeanUtils.isEqual(this.cid, p.cid)
        ;
  }
  public boolean merge(Product p) {
    boolean flag = false;

    if (BeanUtils.isNeedUpdate(this.pname, p.pname)) {
      this.pname = p.pname;
      flag = true;
    }
    if (BeanUtils.isNeedUpdate(this.market_price, p.market_price)) {
      this.market_price = p.market_price;
      flag = true;
    }
    if (BeanUtils.isNeedUpdate(this.shop_price, p.shop_price)) {
      this.shop_price = p.shop_price;
      flag = true;
    }
    if (BeanUtils.isNeedUpdate(this.pimage, p.pimage)) {
      this.pimage = p.pimage;
      flag = true;
    }
    if (BeanUtils.isNeedUpdate(this.pdate, p.pdate)) {
      this.pdate = p.pdate;
      flag = true;
    }
    if (BeanUtils.isNeedUpdate(this.is_hot, p.is_hot)) {
      this.is_hot = p.is_hot;
      flag = true;
    }
    if (BeanUtils.isNeedUpdate(this.pdesc, p.pdesc)) {
      this.pdesc = p.pdesc;
      flag = true;
    }
    if (BeanUtils.isNeedUpdate(this.pflag, p.pflag)) {
      this.pflag = p.pflag;
      flag = true;
    }
    if (BeanUtils.isNeedUpdate(this.cid, p.cid)) {
      this.cid = p.cid;
      flag = true;
    }
    return flag;
  }

  public static boolean isIdValidate(String v) {
    return BeanUtils.isValidate(v, true, 1, 32);
  }

  public Product revise() {
    this.pid = UUIDUtils.getId();
    this.pdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    this.pflag = PRODUCT_IS_UP;
    this.category = new Category();
    if(this.cid != null)
      this.category.setCid(cid);
    
    return this;
  }
}
