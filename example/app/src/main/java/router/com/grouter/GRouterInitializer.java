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
    // joyrun
    activityMap.put("asd", "com.grouter.demo.MainActivity");
    activityMap.put("second2", "com.grouter.demo.SecondActivity");
  }
  static {
  }
  static {
  }

  public GRouterInitializer() {
    super("joyrun","",activityMap,componentMap,taskMap);
  }
}
