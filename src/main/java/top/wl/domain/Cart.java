package top.wl.domain;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Cart {
  private Map<String, CartItem> itemMap = new HashMap<String, CartItem>();
  private Double total = 0.0;

  public Collection<CartItem> getCartItems() {
    return itemMap.values();
  }

  public Map<String, CartItem> getItemMap() {
    return itemMap;
  }

  public void setItemMap(Map<String, CartItem> itemMap) {
    this.itemMap = itemMap;
  }

  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  public void add2cart(CartItem item) {
    String pid = item.getProduct().getPid();

    if (itemMap.containsKey(pid)) {
      CartItem oItem = itemMap.get(pid);
      oItem.setCount(oItem.getCount() + item.getCount());
    } else {
      itemMap.put(pid, item);
    }

    total += item.getSubtotal();
  }

  public void removeFromCart(String pid) {
    CartItem item = itemMap.remove(pid);
    if(null != item)
      total -= item.getSubtotal();
  }

  public void clearCart() {
    itemMap.clear();
    total = 0.0;
  }
}
