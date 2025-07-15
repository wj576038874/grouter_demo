package com.grouter;

public class GActivityCenter {
  public static BuilderSet.MainActivityHelper MainActivity() {
    return new BuilderSet.MainActivityHelper();
  }

  public static BuilderSet.SecondActivityHelper SecondActivity() {
    return new BuilderSet.SecondActivityHelper();
  }

  public static class BuilderSet {
    public static class MainActivityHelper extends GActivityBuilder {
      MainActivityHelper() {
        super("com.grouter.demo.MainActivity");
      }
    }

    public static class SecondActivityHelper extends GActivityBuilder {
      SecondActivityHelper() {
        super("com.grouter.demo.SecondActivity");
      }

      public SecondActivityHelper id(int id) {
        put("id",id);
        return this;
      }
    }
  }
}
