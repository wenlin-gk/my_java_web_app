package top.wl.domain;

import java.text.ParseException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import top.wl.utils.UUIDUtils;

public class User {

  private static Logger log = LogManager.getLogger(User.class);
  public final static int USER_IS_NOT_ACTIVE = 0;
  public final static int USER_IS_ACTIVE = 1;

  private String uid;
  private String username;
  private String password;

  private String name;
  private String email;
  private String telephone;

  private String birthday;
  private String sex;
  private Integer state;

  private String code;

  public User() {
  }


  public User(String uid, String username, String password, String name,
      String email, String telephone, String birthday, String sex,
      Integer state, String code) {
    this.uid = uid;
    this.username = username;
    this.password = password;
    this.name = name;
    this.email = email;
    this.telephone = telephone;
    this.birthday = birthday;
    this.sex = sex;
    this.state = state;
    this.code = code;
  }


  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getTelephone() {
    return telephone;
  }

  public void setTelephone(String telephone) {
    this.telephone = telephone;
  }

  public String getBirthday() {
    return birthday;
  }

  public void setBirthday(String birthday) throws ParseException {
    this.birthday = birthday;
  }

  public String getSex() {
    return sex;
  }

  public void setSex(String sex) {
    this.sex = sex;
  }

  public Integer getState() {
    return state;
  }

  public void setState(Integer state) {
    this.state = state;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  @Override
  public String toString() {
    return "User [uid=" + uid + ", username=" + username + ", password="
        + password + ", name=" + name + ", email=" + email + ", telephone="
        + telephone + ", birthday=" + birthday + ", sex=" + sex + ", state="
        + state + ", code=" + code + "]";
  }

  public String toMsg() {
    return "User [uid=" + uid + ", username=" + username + ", name=" + name
        + ", email=" + email + ", telephone=" + telephone + ", birthday="
        + birthday + ", sex=" + sex + ", state=" + state + ", code=" + code
        + "]";
  }

  public boolean isValidate() {
    return BeanUtils.isValidate(this.username, true, 1, 20)
        && BeanUtils.isValidate(this.password, true, 1, 20)
        && BeanUtils.isValidate(this.name, true, 1, 20)
        && BeanUtils.isValidate(this.email, true, 6, 30)
        && BeanUtils.isValidate(this.telephone, false, 11, 11)
        && BeanUtils.isValidate4Date(this.birthday, false, 1, 50)
        && BeanUtils.isValidate(this.sex, false, 1, 10)
        && BeanUtils.isValidate(this.state, false, 0, 1)
        && BeanUtils.isValidate(this.code, false, 1, 64);
  }

  public boolean isValidate4Update() {
    return BeanUtils.isValidate(this.uid, true, 1, 32)
        && BeanUtils.isValidate(this.username, false, 1, 20)
        && BeanUtils.isValidate(this.password, false, 1, 20)
        && BeanUtils.isValidate(this.name, false, 1, 20)
        && BeanUtils.isValidate(this.email, false, 6, 20)
        && BeanUtils.isValidate(this.telephone, false, 11, 11)
        && BeanUtils.isValidate4Date(this.birthday, false, 1, 50)
        && BeanUtils.isValidate(this.sex, false, 1, 10)
        && BeanUtils.isValidate(this.state, false, 0, 1)
        && BeanUtils.isValidate(this.code, false, 1, 64);
  }

  public boolean merge(User u) {
    boolean flag = false;

    if (BeanUtils.isNeedUpdate(this.username, u.username)) {
      this.username = u.username;
      flag = true;
    }
    if (BeanUtils.isNeedUpdate(this.password, u.password)) {
      this.password = u.password;
      flag = true;
    }
    if (BeanUtils.isNeedUpdate(this.name, u.name)) {
      this.name = u.name;
      flag = true;
    }
    if (BeanUtils.isNeedUpdate(this.email, u.email)) {
      this.email = u.email;
      flag = true;
    }
    if (BeanUtils.isNeedUpdate(this.telephone, u.telephone)) {
      this.telephone = u.telephone;
      flag = true;
    }
    if (BeanUtils.isNeedUpdate(this.birthday, u.birthday)) {
      this.birthday = u.birthday;
      flag = true;
    }
    if (BeanUtils.isNeedUpdate(this.sex, u.sex)) {
      this.sex = u.sex;
      flag = true;
    }
    if (BeanUtils.isNeedUpdate(this.state, u.state)) {
      this.state = u.state;
      flag = true;
    }
    if (BeanUtils.isNeedUpdate(this.code, u.code)) {
      this.code = u.code;
      flag = true;
    }
    return flag;
  }

  public static boolean isNameValidate(String v) {
    return BeanUtils.isValidate(v, true, 1, 20);
  }

  public static boolean isIdValidate(String v) {
    return BeanUtils.isValidate(v, true, 1, 32);
  }

  public static boolean isCodeValidate(String v) {
    return BeanUtils.isValidate(v, true, 1, 64);
  }

  public User revise() {
    this.setUid(UUIDUtils.getId());
    this.setState(User.USER_IS_NOT_ACTIVE);
    this.setCode(UUIDUtils.getCode());
    return this;
  }


  public boolean equals(User u) {
    return BeanUtils.isEqual(this.uid, u.uid)
        && BeanUtils.isEqual(this.username, u.username)
        && BeanUtils.isEqual(this.password, u.password)
        && BeanUtils.isEqual(this.name, u.name)
        && BeanUtils.isEqual(this.email, u.email)
        && BeanUtils.isEqual(this.telephone, u.telephone)
        && BeanUtils.isEqual(this.birthday, u.birthday)
        && BeanUtils.isEqual(this.sex, u.sex)
        && BeanUtils.isEqual(this.state, u.state)
        && BeanUtils.isEqual(this.code, u.code)
        ;
  }
  
}
