package org.prometheus.enums;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFColor;

public enum CellStylesEnum {

    // @formatter:off
    NORMAL_ALIGN_CENTER           ( 1, FontsEnum.NORMAL.index,      RGBColorsEnum.WHITE.color(), HorizontalAlignment.CENTER, ValueFormatsEnum.DEFAULT, true), // 
    NORMAL_ALIGN_CENTER_BOLD      ( 2, FontsEnum.NORMAL_BOLD.index, RGBColorsEnum.WHITE.color(), HorizontalAlignment.CENTER, ValueFormatsEnum.DEFAULT, true), //
    NORMAL_ALIGN_LEFT             ( 3, FontsEnum.NORMAL.index,      RGBColorsEnum.WHITE.color(), HorizontalAlignment.LEFT,   ValueFormatsEnum.DEFAULT, true), //
    NORMAL_ALIGN_LEFT_BOLD        ( 4, FontsEnum.NORMAL_BOLD.index, RGBColorsEnum.WHITE.color(), HorizontalAlignment.LEFT,   ValueFormatsEnum.DEFAULT, true), //
    NORMAL_ALIGN_RIGHT            ( 5, FontsEnum.NORMAL.index,      RGBColorsEnum.WHITE.color(), HorizontalAlignment.RIGHT,  ValueFormatsEnum.DEFAULT, true), //
    NORMAL_ALIGN_RIGHT_BOLD       ( 6, FontsEnum.NORMAL_BOLD.index, RGBColorsEnum.WHITE.color(), HorizontalAlignment.RIGHT,  ValueFormatsEnum.DEFAULT, true), //

    ORANGE_ALIGN_CENTER           ( 7, FontsEnum.WHITE.index,       RGBColorsEnum.DARK_ORANGE.color(), HorizontalAlignment.CENTER, ValueFormatsEnum.DEFAULT, true), // 
    ORANGE_ALIGN_CENTER_BOLD      ( 8, FontsEnum.WHITE_BOLD.index,  RGBColorsEnum.DARK_ORANGE.color(), HorizontalAlignment.CENTER, ValueFormatsEnum.DEFAULT, true), //
    ORANGE_ALIGN_LEFT             ( 9, FontsEnum.WHITE.index,       RGBColorsEnum.DARK_ORANGE.color(), HorizontalAlignment.LEFT,   ValueFormatsEnum.DEFAULT, true), //
    ORANGE_ALIGN_LEFT_BOLD        (10, FontsEnum.WHITE_BOLD.index,  RGBColorsEnum.DARK_ORANGE.color(), HorizontalAlignment.LEFT,   ValueFormatsEnum.DEFAULT, true), //
    ORANGE_ALIGN_RIGHT            (11, FontsEnum.WHITE.index,       RGBColorsEnum.DARK_ORANGE.color(), HorizontalAlignment.RIGHT,  ValueFormatsEnum.DEFAULT, true), //
    ORANGE_ALIGN_RIGHT_BOLD       (12, FontsEnum.WHITE_BOLD.index,  RGBColorsEnum.DARK_ORANGE.color(), HorizontalAlignment.RIGHT,  ValueFormatsEnum.DEFAULT, true), //

    GREEN_ALIGN_CENTER            (13, FontsEnum.WHITE.index,       RGBColorsEnum.GREEN.color(), HorizontalAlignment.CENTER, ValueFormatsEnum.DEFAULT, true), // 
    GREEN_ALIGN_CENTER_BOLD       (14, FontsEnum.WHITE_BOLD.index,  RGBColorsEnum.GREEN.color(), HorizontalAlignment.CENTER, ValueFormatsEnum.DEFAULT, true), //
    GREEN_ALIGN_LEFT              (15, FontsEnum.WHITE.index,       RGBColorsEnum.GREEN.color(), HorizontalAlignment.LEFT,   ValueFormatsEnum.DEFAULT, true), //
    GREEN_ALIGN_LEFT_BOLD         (16, FontsEnum.WHITE_BOLD.index,  RGBColorsEnum.GREEN.color(), HorizontalAlignment.LEFT,   ValueFormatsEnum.DEFAULT, true), //
    GREEN_ALIGN_RIGHT             (17, FontsEnum.WHITE.index,       RGBColorsEnum.GREEN.color(), HorizontalAlignment.RIGHT,  ValueFormatsEnum.DEFAULT, true), //
    GREEN_ALIGN_RIGHT_BOLD        (18, FontsEnum.WHITE_BOLD.index,  RGBColorsEnum.GREEN.color(), HorizontalAlignment.RIGHT,  ValueFormatsEnum.DEFAULT, true), //

    WHITE_SMOKE_ALIGN_CENTER      (19, FontsEnum.NORMAL.index,      RGBColorsEnum.WHITE_SMOKE.color(), HorizontalAlignment.CENTER, ValueFormatsEnum.DEFAULT, true), // 
    WHITE_SMOKE_ALIGN_CENTER_BOLD (20, FontsEnum.NORMAL_BOLD.index, RGBColorsEnum.WHITE_SMOKE.color(), HorizontalAlignment.CENTER, ValueFormatsEnum.DEFAULT, true), //
    WHITE_SMOKE_ALIGN_LEFT        (21, FontsEnum.NORMAL.index,      RGBColorsEnum.WHITE_SMOKE.color(), HorizontalAlignment.LEFT,   ValueFormatsEnum.DEFAULT, true), //
    WHITE_SMOKE_ALIGN_LEFT_BOLD   (22, FontsEnum.NORMAL_BOLD.index, RGBColorsEnum.WHITE_SMOKE.color(), HorizontalAlignment.LEFT,   ValueFormatsEnum.DEFAULT, true), //
    WHITE_SMOKE_ALIGN_RIGHT       (23, FontsEnum.NORMAL.index,      RGBColorsEnum.WHITE_SMOKE.color(), HorizontalAlignment.RIGHT,  ValueFormatsEnum.DEFAULT, true), //
    WHITE_SMOKE_ALIGN_RIGHT_BOLD  (24, FontsEnum.NORMAL_BOLD.index, RGBColorsEnum.WHITE_SMOKE.color(), HorizontalAlignment.RIGHT,  ValueFormatsEnum.DEFAULT, true), //

    LIGHT_GREY_ALIGN_CENTER       (25, FontsEnum.NORMAL.index,      RGBColorsEnum.LIGHT_GREY.color(), HorizontalAlignment.CENTER, ValueFormatsEnum.DEFAULT, true), // 
    LIGHT_GREY_ALIGN_CENTER_BOLD  (26, FontsEnum.NORMAL_BOLD.index, RGBColorsEnum.LIGHT_GREY.color(), HorizontalAlignment.CENTER, ValueFormatsEnum.DEFAULT, true), //
    LIGHT_GREY_ALIGN_LEFT         (27, FontsEnum.NORMAL.index,      RGBColorsEnum.LIGHT_GREY.color(), HorizontalAlignment.LEFT,   ValueFormatsEnum.DEFAULT, true), //
    LIGHT_GREY_ALIGN_LEFT_BOLD    (28, FontsEnum.NORMAL_BOLD.index, RGBColorsEnum.LIGHT_GREY.color(), HorizontalAlignment.LEFT,   ValueFormatsEnum.DEFAULT, true), //
    LIGHT_GREY_ALIGN_RIGHT        (29, FontsEnum.NORMAL.index,      RGBColorsEnum.LIGHT_GREY.color(), HorizontalAlignment.RIGHT,  ValueFormatsEnum.DEFAULT, true), //
    LIGHT_GREY_ALIGN_RIGHT_BOLD   (30, FontsEnum.NORMAL_BOLD.index, RGBColorsEnum.LIGHT_GREY.color(), HorizontalAlignment.RIGHT,  ValueFormatsEnum.DEFAULT, true), //

    LIGHT_BLUE_ALIGN_CENTER       (31, FontsEnum.WHITE.index,      RGBColorsEnum.DODGER_BLUE.color(), HorizontalAlignment.CENTER, ValueFormatsEnum.DEFAULT, true), // 
    LIGHT_BLUE_ALIGN_CENTER_BOLD  (32, FontsEnum.WHITE_BOLD.index, RGBColorsEnum.DODGER_BLUE.color(), HorizontalAlignment.CENTER, ValueFormatsEnum.DEFAULT, true), //
    LIGHT_BLUE_ALIGN_LEFT         (33, FontsEnum.WHITE.index,      RGBColorsEnum.DODGER_BLUE.color(), HorizontalAlignment.LEFT,   ValueFormatsEnum.DEFAULT, true), //
    LIGHT_BLUE_ALIGN_LEFT_BOLD    (34, FontsEnum.WHITE_BOLD.index, RGBColorsEnum.DODGER_BLUE.color(), HorizontalAlignment.LEFT,   ValueFormatsEnum.DEFAULT, true), //
    LIGHT_BLUE_ALIGN_RIGHT        (35, FontsEnum.WHITE.index,      RGBColorsEnum.DODGER_BLUE.color(), HorizontalAlignment.RIGHT,  ValueFormatsEnum.DEFAULT, true), //
    LIGHT_BLUE_ALIGN_RIGHT_BOLD   (36, FontsEnum.WHITE_BOLD.index, RGBColorsEnum.DODGER_BLUE.color(), HorizontalAlignment.RIGHT,  ValueFormatsEnum.DEFAULT, true), //

    RED_ALIGN_CENTER              (37, FontsEnum.WHITE.index,      RGBColorsEnum.RED.color(), HorizontalAlignment.CENTER, ValueFormatsEnum.DEFAULT, true), // 
    RED_ALIGN_CENTER_BOLD         (38, FontsEnum.WHITE_BOLD.index, RGBColorsEnum.RED.color(), HorizontalAlignment.CENTER, ValueFormatsEnum.DEFAULT, true), //
    RED_ALIGN_LEFT                (39, FontsEnum.WHITE.index,      RGBColorsEnum.RED.color(), HorizontalAlignment.LEFT,   ValueFormatsEnum.DEFAULT, true), //
    RED_ALIGN_LEFT_BOLD           (40, FontsEnum.WHITE_BOLD.index, RGBColorsEnum.RED.color(), HorizontalAlignment.LEFT,   ValueFormatsEnum.DEFAULT, true), //
    RED_ALIGN_RIGHT               (41, FontsEnum.WHITE.index,      RGBColorsEnum.RED.color(), HorizontalAlignment.RIGHT,  ValueFormatsEnum.DEFAULT, true), //
    RED_ALIGN_RIGHT_BOLD          (42, FontsEnum.WHITE_BOLD.index, RGBColorsEnum.RED.color(), HorizontalAlignment.RIGHT,  ValueFormatsEnum.DEFAULT, true), //

    DATE                          (43, FontsEnum.NORMAL.index,      RGBColorsEnum.WHITE.color(), HorizontalAlignment.CENTER , ValueFormatsEnum.DATE     , true), // 
    DATE_TIME                     (44, FontsEnum.NORMAL.index,      RGBColorsEnum.WHITE.color(), HorizontalAlignment.CENTER , ValueFormatsEnum.DATE_TIME, true), // 
    DECIMAL                       (45, FontsEnum.NORMAL.index,      RGBColorsEnum.WHITE.color(), HorizontalAlignment.GENERAL, ValueFormatsEnum.DECIMAL  , true), // 
    DECIMAL5                      (46, FontsEnum.NORMAL.index,      RGBColorsEnum.WHITE.color(), HorizontalAlignment.GENERAL, ValueFormatsEnum.DECIMAL5 , true), // 
    NUMBER                        (47, FontsEnum.NORMAL.index,      RGBColorsEnum.WHITE.color(), HorizontalAlignment.GENERAL, ValueFormatsEnum.DEFAULT  , true), // 
    PERCENT                       (48, FontsEnum.NORMAL.index,      RGBColorsEnum.WHITE.color(), HorizontalAlignment.GENERAL, ValueFormatsEnum.PERCENT  , true), // 
    TEXT                          (49, FontsEnum.NORMAL.index,      RGBColorsEnum.WHITE.color(), HorizontalAlignment.GENERAL, ValueFormatsEnum.DEFAULT  , true), //
    TEXT_BOLD                     (50, FontsEnum.NORMAL_BOLD.index, RGBColorsEnum.WHITE.color(), HorizontalAlignment.GENERAL, ValueFormatsEnum.DEFAULT  , true), // 
    NOTA                          (51, FontsEnum.NORMAL.index,      RGBColorsEnum.WHITE.color(), HorizontalAlignment.GENERAL, ValueFormatsEnum.DEFAULT  , false), //
    NOTA_BOLD                     (52, FontsEnum.NORMAL_BOLD.index, RGBColorsEnum.WHITE.color(), HorizontalAlignment.GENERAL, ValueFormatsEnum.DEFAULT  , false); // 
    // @formatter:on

    public Integer index;
    public Integer font;
    public XSSFColor bgColor;
    public HorizontalAlignment hAlignment;
    public boolean hasBorders;
    public ValueFormatsEnum format;

    CellStylesEnum(Integer index, Integer font, XSSFColor bgColor, HorizontalAlignment hAlignment, ValueFormatsEnum format, boolean hasBorders) {
        this.index = index;
        this.font = font;
        this.bgColor = bgColor;
        this.hAlignment = hAlignment;
        this.hasBorders = hasBorders;
        this.format = format;
    }
}
