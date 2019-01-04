package me.xwbz.flowable.bean;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Getter
@Setter
public class VariableParam {

    private String nameKey;

    private String name;

    private String valueKey;

    private String value;

    private String valueCompareSymbol = "=";

    public VariableParam(String nameKey, String name, String valueKey, String value){
        this.nameKey = nameKey;
        this.name = name;
        this.valueKey = valueKey;
        this.value = value;
    }

    public VariableParam(String nameKey, String name, String valueKey, String value, String valueCompareSymbol){
        this(nameKey, name, valueKey, value);
        this.valueCompareSymbol = valueCompareSymbol;
    }

    public static String concatVariableSql(String start, List<VariableParam> keys, String end) {
        if (keys.isEmpty()) {
            return "";
        }
        return start
                + StringUtils.join(keys.stream()
                .map(k -> "(var.name_ = #{" + k.getNameKey() + "} AND var.TEXT_ " + k.getValueCompareSymbol() + " #{" + k.getValueKey() + "})")
                .toArray(String[]::new), " OR ")
                + end;
    }
}
