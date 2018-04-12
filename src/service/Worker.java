package service;

import exception.CountValues;

import java.util.LinkedHashMap;
import java.util.Map;

public class Worker {

    public Map<String, String> getFieldsFromCommand(String insertCommand, int fieldsStart) throws CountValues {
        Map<String, String> fields = new LinkedHashMap<String, String>();
        String[] fieldsName = insertCommand.substring(
                insertCommand.indexOf('(', fieldsStart) + 1,
                insertCommand.indexOf(')', fieldsStart))
                .split(",");

        int startFieldsValues = insertCommand.indexOf("VALUES") + 6;
        String[] fieldsValues = insertCommand.substring(
                insertCommand.indexOf('(', startFieldsValues) + 1,
                insertCommand.indexOf(')', startFieldsValues))
                .split(",");

        if (fieldsName.length != fieldsValues.length) {
            throw new CountValues();
        }

        for (int index = 0; index < fieldsName.length; index++) {
            fields.put(
                    fieldsName[index].trim(),
                    (fieldsValues[index].trim().contains("'")) ?
                            fieldsValues[index].trim().split("'")[1]
                            : fieldsValues[index].trim());
        }
        return fields;
    }

    public String fieldsMapToString (String tableName, Map<String, String> fields){
        StringBuilder result  = new StringBuilder();

        result.append(tableName);
        result.append('\n');

        for (Map.Entry<String, String> field
                : fields.entrySet()){
            result.append(field.getKey());
            result.append(" --> ");
            result.append(field.getValue());
            result.append('\n');
        }
        return result.toString();
    }


}
