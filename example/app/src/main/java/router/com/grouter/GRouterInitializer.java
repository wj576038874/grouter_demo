package com.grouter;

import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.HashMap;

@SuppressWarnings("unused")
public class GRouterInitializer extends GRouter {
  private static HashMap<String, String> activityMap = new HashMap<>();

  private static HashMap<String, String> componentMap = new HashMap<>();

  private static HashMap<String, String> taskMap = new HashMap<>();

  static {
    // app
    activityMap.put("asd", "com.grouter.demo.MainActivity");
    activityMap.put("second", "com.grouter.demo.SecondActivity");
  }
  static {
  }
  static {
  }

  public GRouterInitializer() {
    super("joyrun","",activityMap,componentMap,taskMap);
  }
}
