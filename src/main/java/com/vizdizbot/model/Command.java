package com.vizdizbot.model;

import lombok.Data;
@Data
public class Command {

    private final String command;
    private final String botName;
    public Command(String botName, String message) {
        if(message.contains("@")){
           this.command = message.substring(0,message.indexOf("@"));
           this.botName = message.substring(message.indexOf("@")+1);
        }else{
            this.command = message;
            this.botName = botName;
        }
    }


}
