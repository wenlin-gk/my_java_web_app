package top.wl;

public interface Const {
  String HOST = System.getenv().getOrDefault("STORE_HOST", "localhost");
  String INGRESS = "http://" + HOST + ":8080/my_store";

  String REDIS_HOST = System.getenv().getOrDefault("REDIS_HOST", "localhost");
  int REDIS_PORT = Integer
      .parseInt(System.getenv().getOrDefault("REDIS_PORT", "6379"));
  String KEY_4_STORE_CATEGORY_LIST = "STORE_CATEGORY_LIST";

  String MYSQL_HOST = System.getenv().getOrDefault("MYSQL_HOST", "localhost");
  int MYSQL_PORT = Integer
      .parseInt(System.getenv().getOrDefault("MYSQL_PORT", "3306"));
  String MYSQL_ROOT_PASSWORD = System.getenv()
      .getOrDefault("MYSQL_ROOT_PASSWORD", "888888");
  String STORE_DATABASE = System.getenv().getOrDefault("STORE_DATABASE",
      "store");

  String ADMIN_PASSWD = System.getenv().getOrDefault("ADMIN_PASSWD", "admin");

  String SAVE_NAME = "ok";

}
