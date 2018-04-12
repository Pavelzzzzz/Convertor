import exception.CountValues;
import service.Worker;

import javax.swing.*;
import java.awt.Color;
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
        try {
            String insertCommand = textArea1.getText();
            textArea2.setCaretColor(new Color(255, 255, 255));

            Worker worker = new Worker();

            int startTableName = insertCommand.indexOf("INTO ") + 5;
            String tableName = insertCommand.substring(
                    startTableName,
                    insertCommand.indexOf(' ', startTableName));

            Map<String, String> fields = worker.getFieldsFromCommand(insertCommand, startTableName);



            textArea2.setText(worker.fieldsMapToString(tableName, fields));
        }
        catch (CountValues countValues){
            textArea2.setText(
                    "The number of fields must be equal to the number of values");
            textArea2.setCaretColor(new Color(255, 0, 0));
        }
        //dispose();
    }

    public static void main(String[] args) {
        main dialog = new main();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}