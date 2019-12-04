package org.kie.remote.command;

import java.io.Serializable;

public class CommonCommand implements Serializable {

  private String entryPoint;

  public CommonCommand(String entryPoint){
    this.entryPoint = entryPoint;
  }

  public String getEntryPoint() {
    return entryPoint;
  }

}
