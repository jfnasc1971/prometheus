package org.prometheus.enums;

import org.apache.poi.xssf.usermodel.XSSFColor;

public enum FontsEnum {

    // @formatter:off
    NORMAL     (1, FontWeightEnum.NORMAL, RGBColorsEnum.BLACK.color()), // 1
    NORMAL_BOLD(2, FontWeightEnum.BOLD,   RGBColorsEnum.BLACK.color()), // 2
    GREY       (3, FontWeightEnum.NORMAL, RGBColorsEnum.GREY.color()), // 3
    GREY_BOLD  (4, FontWeightEnum.BOLD,   RGBColorsEnum.GREY.color()), // 4
    RED        (5, FontWeightEnum.NORMAL, RGBColorsEnum.RED.color()), // 3
    RED_BOLD   (6, FontWeightEnum.BOLD,   RGBColorsEnum.RED.color()), // 4
    WHITE      (7, FontWeightEnum.NORMAL, RGBColorsEnum.WHITE.color()), // 5
    WHITE_BOLD (8, FontWeightEnum.BOLD,   RGBColorsEnum.WHITE.color()); // 6
    // @formatter:on

    public Integer index;
    public short size;
    public String fontFamily;
    public FontWeightEnum fontWeight;
    public XSSFColor color;

    FontsEnum(Integer index, FontWeightEnum fontWeight, XSSFColor color) {
        this.index = index;
        this.size = (short) 10;
        this.fontFamily = "Arial";
        this.fontWeight = fontWeight;
        this.color = color;
    }
}
