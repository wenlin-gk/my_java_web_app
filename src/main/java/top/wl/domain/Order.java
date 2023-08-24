package top.wl.domain;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import top.wl.utils.UUIDUtils;

public class Order {
  public final static int ORDER_WEIFUKUAN = 0;
  public final static int ORDER_YIFUKUAN = 1;
  public final static int ORDER_YIFAHUO = 2;
  public final static int ORDER_YIWANCHENG = 3;

  private String oid;
  private String ordertime;
  private Double total;

  private Integer state;// 订单状态 0:未付款 1:已付款 2:已发货 3.已完成
  private String address;
  private String name;

  private String telephone;

  private String uid;
  private User user;

  // 表示当前订单包含的订单项
  private List<OrderItem> items = new ArrayList<>();

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public Order() {
    super();
  }

  public Order(String oid, int stat) {
    this.oid = oid;
    this.state = stat;
  }

  public String getOid() {
    return oid;
  }

  public void setOid(String oid) {
    this.oid = oid;
  }

  public String getOrdertime() {
    return ordertime;
  }

  public void setOrdertime(String ordertime) {
    this.ordertime = ordertime;
  }

  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  public Integer getState() {
    return state;
  }

  public void setState(Integer state) {
    this.state = state;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getTelephone() {
    return telephone;
  }

  public void setTelephone(String telephone) {
    this.telephone = telephone;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
    if(null!=user)
      this.uid = user.getUid();
  }

  public List<OrderItem> getItems() {
    return items;
  }

  public void setItems(List<OrderItem> items) {
    this.items = items;
  }

  @Override
  public String toString() {
    return "Order [oid=" + oid + ", ordertime=" + ordertime + ", total=" + total
        + ", state=" + state + ", address=" + address + ", name=" + name
        + ", telephone=" + telephone + ", user=" + user + ", items=" + items
        + "]";
  }

  public boolean equals(Order o) {
    return BeanUtils.isEqual(this.oid, o.oid)
        && BeanUtils.isEqual(this.ordertime, o.ordertime)
        && BeanUtils.isEqual(this.total, o.total)
        && BeanUtils.isEqual(this.state, o.state)
        && BeanUtils.isEqual(this.address, o.address)
        && BeanUtils.isEqual(this.name, o.name)
        && BeanUtils.isEqual(this.telephone, o.telephone)
        && BeanUtils.isEqual(this.uid, o.uid);
  }

  public static boolean isStateValidate(int stat) {
    return stat > -1 && stat < 4;
  }

  /**
   * 忽略Items
   */
  public boolean isValidate() {
    return BeanUtils.isValidate4Datetime(this.ordertime, false, 1, 50)
        && BeanUtils.isValidate(this.total, false, 1, 20 * 10000)
        && BeanUtils.isValidate(this.state, false, 0, 3)
        && BeanUtils.isValidate(this.address, false, 1, 50)
        && BeanUtils.isValidate(this.name, false, 1, 20)
        && BeanUtils.isValidate(this.telephone, false, 11, 11)
        && BeanUtils.isValidate(this.uid, true, 1, 32);
  }

  /**
   * 忽略Items
   */
  public boolean isValidate4Update() {
    return BeanUtils.isValidate(this.oid, true, 1, 32)
        && BeanUtils.isValidate4Datetime(this.ordertime, false, 1, 50)
        && BeanUtils.isValidate(this.total, false, 1, 20 * 10000)
        && BeanUtils.isValidate(this.state, false, 0, 3)
        && BeanUtils.isValidate(this.address, false, 1, 50)
        && BeanUtils.isValidate(this.name, false, 1, 20)
        && BeanUtils.isValidate(this.telephone, false, 11, 11)
        && BeanUtils.isValidate(this.uid, false, 1, 32);
  }

  /**
   * 忽略Items
   */
  public boolean merge(Order o) {
    boolean flag = false;

    if (BeanUtils.isNeedUpdate(this.ordertime, o.ordertime)) {
      this.ordertime = o.ordertime;
      flag = true;
    }
    if (BeanUtils.isNeedUpdate(this.total, o.total)) {
      this.total = o.total;
      flag = true;
    }
    if (BeanUtils.isNeedUpdate(this.state, o.state)) {
      this.state = o.state;
      flag = true;
    }
    if (BeanUtils.isNeedUpdate(this.address, o.address)) {
      this.address = o.address;
      flag = true;
    }
    if (BeanUtils.isNeedUpdate(this.name, o.name)) {
      this.name = o.name;
      flag = true;
    }
    if (BeanUtils.isNeedUpdate(this.telephone, o.telephone)) {
      this.telephone = o.telephone;
      flag = true;
    }
    if (BeanUtils.isNeedUpdate(this.uid, o.uid)) {
      this.uid = o.uid;
      flag = true;
    }

    return flag;
  }

  public static boolean isIdValidate(String v) {
    return BeanUtils.isValidate(v, true, 1, 32);
  }

  public Order revise(int state) {
    this.setOid(UUIDUtils.getId());
    this.setOrdertime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    this.setState(state);
    return this;
  }
}
