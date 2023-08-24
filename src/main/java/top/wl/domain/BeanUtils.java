package top.wl.domain;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BeanUtils {

  private static Logger log = LogManager.getLogger(BeanUtils.class);
  
  public static boolean isValidate(String v, boolean necessary, int minLen, int maxLen) {
    if(necessary) {
      if(null == v || v.length()<minLen || v.length()>maxLen)
        return false;
    }else {
      if(null != v && (v.length()<minLen || v.length()>maxLen))
        return false;
    }
    return true;
  }
  public static boolean isValidate4Date(String v, boolean necessary, int min,
      int max) {
    if (null == v && necessary)
      return false;
    
    if (null != v) {
      if (v.length()<min || v.length()>max){
        return false;
      }
      try {
        new SimpleDateFormat("yyyy-MM-dd").parse(v);
      } catch (ParseException e) {
        log.error(e.getMessage(), e);
        return false;
      }
    }
    
    return true;
  }

  public static boolean isValidate4Datetime(String v, boolean necessary, int min,
      int max) {
    if (null == v && necessary)
      return false;
    
    if (null != v) {
      if (v.length()<min || v.length()>max){
        return false;
      }
      
      try {
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(v);
      } catch (ParseException e) {
        log.error(e.getMessage(), e);
        return false;
      }
    }
    
    return true;
  }


  public static boolean isValidate(Integer v, boolean necessary, int min,
      int max) {
    if(necessary) {
      if(null == v || v<min || v>max)
        return false;
    }else {
      if(null != v && (v<min || v>max))
        return false;
    }
    return true;
  }

  public static boolean isValidate(Double v, boolean necessary, int min,
      int max) {
    if(necessary) {
      if(null == v || v<min || v>max)
        return false;
    }else {
      if(null != v && (v<min || v>max))
        return false;
    }
    return true;
  }

  public static boolean isNeedUpdate(Integer old, Integer now) {
    return null != now && !now.equals(old);
  }

  public static boolean isNeedUpdate(String old, String now) {
    return null != now && !now.equals(old);
  }

  public static boolean isNeedUpdate(Double old, Double now) {
    return null != now && !now.equals(old);
  }
  public static boolean isEqual(String old, String now) {
    if(old!=null) {
      return old.equals(now);
    }else {
      return now == null;
    }
  }
  public static boolean isEqual(Double old, Double now) {
    if(old!=null) {
      return old.equals(now);
    }else {
      return now == null;
    }
  }
  public static boolean isEqual(Integer old, Integer now) {
    if(old!=null) {
      return old.equals(now);
    }else {
      return now == null;
    }
  }

}
