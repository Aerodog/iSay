package com.patrickanker.isay.core;

import java.util.ArrayList;
import java.util.List;


public abstract class Formatter {

    private static List<Formatter> activeFormatters = new ArrayList<Formatter>();
    
    public static Formatter selectFormatter(Class<? extends Formatter> cls)
    {
        for (Formatter formatter : activeFormatters) {
            if (formatter.getClass().equals(cls))
                return formatter;
        }
        
        try {
            Formatter formatter = cls.newInstance();
            activeFormatters.add(formatter);
            return formatter;
        } catch (Throwable t) {
            return null;
        }
    }

    public static String encodeColors(String input)
    {
        for (FormatColors color : FormatColors.values()) {
            input = input.replace(color.getColorCode(), color.getBukkitColorCode());
        }

        return input;
    }

    public static String stripColors(String input)
    {
        for (FormatColors color : FormatColors.values()) {
            input = input.replace(color.getColorCode(), "");
            input = input.replace(color.getBukkitColorCode(), "");
        }
        
        return input;
    }

    public abstract String formatMessage(String in, Object... otherArgs);

    public abstract String[] formatMessages(String in, Object... otherArgs);
}
enum FormatColors {

    WHITE("&f"),
    DARK_BLUE("&1"),
    DARK_GREEN("&2"),
    TEAL("&3"),
    DARK_RED("&4"),
    PURPLE("&5"),
    ORANGE("&6"),
    LIGHT_GREY("&7"),
    DARK_GREY("&8"),
    INDIGO("&9"),
    LIGHT_GREEN("&a"),
    CYAN("&b"),
    RED("&c"),
    PINK("&d"),
    YELLOW("&e"),
    BLACK("&0"),
    RANDOMCHAR("&k"),
    BOLD("&l"),
    STRIKETHROUGH("&m"),
    UNDERLINE("&n"),
    ITALIC("&o"),
    RESET("&r");
    
    private String color;

    private FormatColors(String c)
    {
        color = c;
    }

    public String getColorCode()
    {
        return color;
    }

    public String getBukkitColorCode()
    {
        return color.replace("&", "\u00A7");
    }
}
