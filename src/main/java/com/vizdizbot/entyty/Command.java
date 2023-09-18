package com.vizdizbot.entyty;

import lombok.Data;
@Data
public class Command {
    public Command(String botName, String message) {
        if(message.contains("@")){
           command = message.substring(0,message.indexOf("@"));
           this.botName = message.substring(message.indexOf("@")+1);
            System.out.println(command + " " + botName);
        }else{
            command = message;
            this.botName = botName;
        }
    }

    String command;
    String botName;
}
