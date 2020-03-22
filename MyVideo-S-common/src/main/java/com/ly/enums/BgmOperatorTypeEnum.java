package com.ly.enums;

public enum BgmOperatorTypeEnum {
    ADD("1","添加bgm"),
    DELETE("2","删除bgm");
    public final String type;
    public final String value;
    BgmOperatorTypeEnum(String type,String value){
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
    public static String getValueByKey(String key){
        for(BgmOperatorTypeEnum type :BgmOperatorTypeEnum.values()){
            if(type.getType().equals(key)){
                return type.value;
            }
        }
        return null;
    }
}
