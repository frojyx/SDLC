package noisecalc;

import java.awt.*;
import javax.swing.*;


public class InputDialog extends JDialog {

    private final NoiseController controller;

    private JTextField talkField;
    private JTextField musicField;
    private JTextField swearField;

    public InputDialog(JFrame owner, NoiseModel model, NoiseController controller) {
        super(owner, "Ввод данных", true);
        this.controller = controller;
        buildUi(model);
    }

    private void buildUi(NoiseModel model) {
        setResizable(false);
        setLayout(new BorderLayout());

        // Каждая пара "подпись сверху + поле снизу" — так подпись никогда
        // не обрезается, независимо от её длины.
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));

        talkField = new JTextField(restoreValue(model.isDataEntered(), model.getTalkCount()));
        musicField = new JTextField(restoreValue(model.isDataEntered(), model.getMusicCount()));
        swearField = new JTextField(restoreValue(model.isDataEntered(), model.getSwearCount()));

        fieldsPanel.add(createFieldRow("Сколько раз в день разговариваешь:", talkField));
        fieldsPanel.add(Box.createVerticalStrut(10));
        fieldsPanel.add(createFieldRow("Сколько раз в день слушаешь музыку:", musicField));
        fieldsPanel.add(Box.createVerticalStrut(10));
        fieldsPanel.add(createFieldRow("Сколько раз в день ругаешься:", swearField));

        JButton okButton = new JButton("Рассчитать");
        okButton.addActionListener(e -> onSubmit());

        JButton cancelButton = new JButton("Отмена");
        cancelButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 15, 15));
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        add(fieldsPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Подписи разной длины сами определяют, сколько места им нужно,
        // а pack() подгоняет размер окна под это содержимое —
        // ничего не обрезается.
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(getOwner());
    }

    /**
     * Строка "подпись сверху + поле ввода снизу". Подпись занимает всю
     * доступную ширину и переносится по словам, если вдруг не помещается,
     * поэтому текст всегда виден полностью.
     */
    private JPanel createFieldRow(String labelText, JTextField field) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel("<html>" + labelText + "</html>");
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, field.getPreferredSize().height));
        field.setColumns(20);

        row.add(label);
        row.add(Box.createVerticalStrut(4));
        row.add(field);
        return row;
    }

    private String restoreValue(boolean dataEntered, int value) {
        return dataEntered ? String.valueOf(value) : "";
    }

    private void onSubmit() {
        try {
            controller.handleInput(talkField.getText(), musicField.getText(), swearField.getText());
           
            dispose();
        } catch (InvalidInputException ex) {
            JOptionPane.showMessageDialog(
                this, ex.getMessage(), "Некорректные данные", JOptionPane.ERROR_MESSAGE);
        }
    }
}
