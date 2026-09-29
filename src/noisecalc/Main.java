package noisecalc;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            NoiseModel model = new NoiseModel();
            NoiseController controller = new NoiseController(model);
            MainFrame frame = new MainFrame(model, controller);
            frame.setVisible(true);
        });
    }
}
