package service;

import exception.CountValues;
import exception.PKException;
import exception.TableNameException;

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
                    fieldsValues[index].trim());
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

    public String toFlywayFormat(String tableName, Map<String, String> fields) throws TableNameException, PKException {
        StringBuilder result = new StringBuilder();

        tableName = tableName.replaceAll("\n", "");

        if (tableName.split("\\.").length != 2){
            throw new TableNameException();
        }

        if(!fields.entrySet().iterator().hasNext()){
            throw new PKException();
        }

        StringBuilder tableNameInNewFormat = new StringBuilder();
        tableNameInNewFormat.append('[');
        tableNameInNewFormat.append(tableName.split("\\.")[0]);
        tableNameInNewFormat.append("].[");
        tableNameInNewFormat.append(tableName.split("\\.")[1]);
        tableNameInNewFormat.append(']');

        result.append("IF NOT EXISTS (SELECT * FROM ");
        result.append(tableNameInNewFormat.toString());
        result.append(" WHERE [");
        result.append(fields.entrySet().iterator().next().getKey());
        result.append("] = ");
        result.append(fields.entrySet().iterator().next().getValue());
        result.append(")\n");

        result.append("  BEGIN\n");

        result.append("    INSERT INTO ");
        result.append(tableNameInNewFormat.toString());
        result.append(" (\n");

        String prefix = "";
        for (Map.Entry<String, String> field
                : fields.entrySet()){
            result.append(prefix);
            result.append("      [");
            result.append(field.getKey());
            result.append(']');
            prefix = ",\n";
        }
        result.append(")\n");

        result.append("VALUES (\n");

        prefix = "";
        for (Map.Entry<String, String> field
                : fields.entrySet()){
            result.append(prefix);
            result.append("      ");
            result.append(field.getValue());
            prefix = ",\n";
        }
        result.append(");\n");

        result.append("END");

        return result.toString();
    }


}
