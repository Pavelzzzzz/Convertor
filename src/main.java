import exception.CountValues;
import exception.FailCommandFormat;
import exception.PKException;
import exception.TableNameException;
import service.Worker;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class main extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JTextArea textArea1;
    private JTextArea textArea2;
    private final String DEFAULT_TEXT_OF_SQL_COMMAND = "\n" +
            "INSERT INTO DbName.TableName\n" +
            "           (CategoryId\n" +
            "           ,CategoryName\n" +
            "           ,CreatedBy\n" +
            "           ,CreatedDateTime\n" +
            "           ,LastUpdateBy\n" +
            "           ,LastUpdateDateTime)\n" +
            "     VALUES\n" +
            "           (2\n" +
            "           ,'template'\n" +
            "           ,CURRENT_USER\n" +
            "           ,'2017-01-01 00:00:00.0000000'\n" +
            "           ,NULL\n" +
            "           ,NULL);";

    public main() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        textArea1.setText(DEFAULT_TEXT_OF_SQL_COMMAND);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });
    }

    private void onOK() {
        // add your code here
        textArea2.setForeground(Color.black);

        Worker worker = new Worker();

        StringBuilder out = new StringBuilder();

        for (String insertCommand
                : textArea1.getText().split("(?<=;)")) {

            try {

                if (!insertCommand.contains("INTO ")) {
                    throw new FailCommandFormat();
                }

                int startTableName = insertCommand.indexOf("INTO ") + 5;
                String tableName = insertCommand.substring(
                        startTableName,
                        insertCommand.indexOf('(', startTableName)).trim();

                Map<String, String> fields = worker.getFieldsFromCommand(insertCommand, startTableName);

                out.append(worker.toFlywayFormat(tableName, fields) + '\n' + '\n');
//                out.append(worker.fieldsMapToString(tableName, fields));
            } catch (CountValues countValues) {
                out.append(
                        "The number of fields must be equal to the number of values");
                textArea2.setForeground(Color.red);
            } catch (PKException e) {
                out.append(
                        "No PK field found");
                textArea2.setForeground(Color.red);
            } catch (TableNameException e) {
                out.append(
                        "Error. Table name is not valid");
                out.append(Color.red);
            } catch (FailCommandFormat failCommandFormat) {
                out.append(
                        "Error. Command format is nzot valid");
                textArea2.setForeground(Color.red);
            }
        }

        textArea2.setText(out.toString());

        //dispose();
    }

    public static void main(String[] args) {
        main dialog = new main();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}