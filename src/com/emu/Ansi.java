//
// Ansi.java
// CommandParser
//
// Noah Hitz 2025
//

package com.emu;


/**
 * A list of Ansi Escape Codes.
 * Note: Not all of these options are supported by every terminal.
 */
public class Ansi {
    private Ansi() {}

    public static String RESET              = "\033[0m";

    public static String BOLD               = "\033[1m";
    public static String NOBOLD             = "\033[22m";
    public static String DIM                = "\033[2m";
    public static String NODIM              = "\033[22m";
    public static String ITALIC             = "\033[3m";
    public static String NOITALIC           = "\033[23m";
    public static String UNDERLINE          = "\033[4m";
    public static String NOUNDERLINE        = "\033[24m";
    public static String BLINKING           = "\033[5m";
    public static String NOBLINKING         = "\033[25m";
    public static String STRIKETHROUGH      = "\033[9m";
    public static String NOSTRIKETHROUGH    = "\033[29m";

    // Foreground Colors
    public static String DEFAULTCOLOR       = "\033[37m";
    public static String BLACK              = "\033[30m";
    public static String WHITE              = "\033[37m";
    public static String RED                = "\033[31m";
    public static String GREEN              = "\033[32m";
    public static String YELLOW             = "\033[33m";
    public static String BLUE               = "\033[34m";
    public static String MAGENTA            = "\033[35m";
    public static String CYAN               = "\033[36m";

    // Background Colors
    public static String B_DEFAULTCOLOR     = "\033[49m";
    public static String B_BLACK            = "\033[40m";
    public static String B_WHITE            = "\033[47m";
    public static String B_RED              = "\033[41m";
    public static String B_GREEN            = "\033[42m";
    public static String B_YELLOW           = "\033[43m";
    public static String B_BLUE             = "\033[44m";
    public static String B_MAGENTA          = "\033[45m";
    public static String B_CYAN             = "\033[46m";

    // Bright Foreground Colors
    public static String BRIGHT_BLACK       = "\033[1;30m";
    public static String BRIGHT_WHITE       = "\033[1;37m";
    public static String BRIGHT_RED         = "\033[1;31m";
    public static String BRIGHT_GREEN       = "\033[1;32m";
    public static String BRIGHT_YELLOW      = "\033[1;33m";
    public static String BRIGHT_BLUE        = "\033[1;34m";
    public static String BRIGHT_MAGENTA     = "\033[1;35m";
    public static String BRIGHT_CYAN        = "\033[1;36m";

    // Bright Background Colors
    public static String B_BRIGHT_BLACK     = "\033[100m";
    public static String B_BRIGHT_WHITE     = "\033[107m";
    public static String B_BRIGHT_RED       = "\033[101m";
    public static String B_BRIGHT_GREEN     = "\033[102m";
    public static String B_BRIGHT_YELLOW    = "\033[103m";
    public static String B_BRIGHT_BLUE      = "\033[104m";
    public static String B_BRIGHT_MAGENTA   = "\033[105m";
    public static String B_BRIGHT_CYAN      = "\033[106m";

    // Dimmed Foreground Colors
    public static String DIM_BLACK          = "\033[2;30m";
    public static String DIM_WHITE          = "\033[2;37m";
    public static String DIM_RED            = "\033[2;31m";
    public static String DIM_GREEN          = "\033[2;32m";
    public static String DIM_YELLOW         = "\033[2;33m";
    public static String DIM_BLUE           = "\033[2;34m";
    public static String DIM_MAGENTA        = "\033[2;35m";
    public static String DIM_CYAN           = "\033[2;36m";


    public static void printAnsiTestTable() {
        String[] fgColors = {
            Ansi.BLACK, Ansi.RED, Ansi.GREEN, Ansi.YELLOW, Ansi.BLUE,
            Ansi.MAGENTA, Ansi.CYAN, Ansi.WHITE
        };

        String[] dimFgColors = {
            Ansi.DIM_BLACK, Ansi.DIM_RED, Ansi.DIM_GREEN, Ansi.DIM_YELLOW, Ansi.DIM_BLUE,
            Ansi.DIM_MAGENTA, Ansi.DIM_CYAN, Ansi.DIM_WHITE
        };

        String[] brightFgColors = {
            Ansi.BRIGHT_BLACK, Ansi.BRIGHT_RED, Ansi.BRIGHT_GREEN, Ansi.BRIGHT_YELLOW, Ansi.BRIGHT_BLUE,
            Ansi.BRIGHT_MAGENTA, Ansi.BRIGHT_CYAN, Ansi.BRIGHT_WHITE
        };

        String[] bgColors = {
            Ansi.B_BLACK, Ansi.B_RED, Ansi.B_GREEN, Ansi.B_YELLOW, Ansi.B_BLUE,
            Ansi.B_MAGENTA, Ansi.B_CYAN, Ansi.B_WHITE
        };

        String[] brightBgColors = {
            Ansi.B_BRIGHT_BLACK, Ansi.B_BRIGHT_RED, Ansi.B_BRIGHT_GREEN, Ansi.B_BRIGHT_YELLOW,
            Ansi.B_BRIGHT_BLUE, Ansi.B_BRIGHT_MAGENTA, Ansi.B_BRIGHT_CYAN, Ansi.B_BRIGHT_WHITE
        };

        String[] textFormats = {
            Ansi.BOLD, Ansi.ITALIC, Ansi.UNDERLINE, Ansi.STRIKETHROUGH, Ansi.BLINKING
        };


        String str = "test";
        int width = 16;
        printTestStyled(str, dimFgColors, width);
        printTestStyled(str, fgColors, width);
        printTestStyled(str, brightFgColors, width);

        System.out.println();
        printTestStyled(str, bgColors, width);
        printTestStyled(str, brightBgColors, width);

        System.out.println();
        printTestStyled(str, textFormats, width);
    }

    private static void printTestStyled(String word, String[] styles, int width) {
        for (int i = 0; i < styles.length; i++) {
            String style = styles[i];
            System.out.print(style + word + Ansi.RESET + " ");
            if ((i != 0 && i % width == 0) || i == styles.length-1) {
                System.out.println();
            }
        }
    }
}

