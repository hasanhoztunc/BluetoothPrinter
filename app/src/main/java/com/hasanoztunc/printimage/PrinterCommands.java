package com.hasanoztunc.printimage;

public class PrinterCommands {
    public static final byte SET_HORIZONTAL=0x09;
    public static final byte[] SET_HORIZONTAL_POSITION={0x1B,0x44,8,0x00};
    public static final byte PRINT_LINE_FEED=0x0A;
    public static final byte[] SELECT_DEFAULT_LINE_SPACING={0x1B,0x32};
    public static final byte[] SET_LINE_SPACING={0x1B,0x33,24};
    public static final byte PRINT_CARRIGE_RETURN=0x0D;
    public static final byte[] SET_RIGHT_SIDE_CHARACTER_SPACING={0x1B,0x20,0};
    public static final byte[] SELECT_PRINT_MODE={0x1B,0x21,1};
    public static final byte[] SELECT_CANCEL_USER_DEFINED_CHARACTER_SET={0x1B,0x25,0};
    //SON BILESENLERINE BAK---->ALTTAKI DEINE USER DEFINED CHARACTERS
    public static final byte[] DEFINE_USER_DEFINED_CHARACTERS={0x1B,0x26,};
    public static final byte[] SELECT_BIT_IMAGE_MOD={0x1B,0x2A,0, (byte) 255,1};
    public static final byte FEED_LINE=0x0A;
    public static final byte[] ESC_ALIGN_CENTER={0x1B,0x61,48};
}
