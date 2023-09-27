package com.vizdizbot.utils;

import com.vizdizbot.entity.Filters;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageUtil {
    public static final String ADD = "/add";
    public static final String DELETE = "/delete";
    public static final String SHOW = "/show";
    public static final String HELP = "/help";
    public static final String UNKNOWN_COMMAND = "Неизвестная команда попробуйте /help";

    public static final String COINCIDENCE = "@%s Отправил сообщение в группу %s с сообщением \"%s\"";
    public static final String ADD_SUCCESS = "Текст \"%s\" успешно добавлен";
    public static final String ADD_FAILED = "Текст \"%s\" уже существует";
    public static final String DELETE_SUCCESS = "Текст \"%s\" успешно удален";
    public static final String DELETE_FAILED = "Текст \"%s\" не найден";


    public enum Menu {
        ADD(MessageUtil.ADD, "Добавить текст для поиска", "Введите текст для добавления"),
        DELETE(MessageUtil.DELETE, "Удалить текст для поиска", "Введите текст для удаления"),
        SHOW(MessageUtil.SHOW, "Отобразить все критерии поиска", "Список пуст"),
        HELP(MessageUtil.HELP, "Отобразить информацию о командах", ADD + " Добавить текст для поиска\n" + DELETE + " Удалить текст для поиска\n" + SHOW + " Отобразить все критерии поиска\n" + MessageUtil.HELP + " повторить это сообщение\n");

        public final String menuItem;
        public final String menuDescription;
        public final String menuMessage;

        Menu(String menuItem, String menuDescription, String menuMessage) {
            this.menuItem = menuItem;
            this.menuDescription = menuDescription;
            this.menuMessage = menuMessage;
        }

        public String getMenuItem() {
            return menuItem;
        }

        public String getMenuDescription() {
            return menuDescription;
        }

        public String getMenuMessage() {
            return menuMessage;
        }
    }


    public static boolean wordsContains(List<Filters> words, String messageText) {
        for (Filters word : words) {
            if (messageText.contains(word.getText())) {
                return true;
            }
        }
        return false;
    }
}
