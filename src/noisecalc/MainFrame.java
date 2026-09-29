package noisecalc;

import javax.swing.*;
import java.awt.*;

/**
 * Главная форма приложения (View).
 * Подписывается на модель как ModelListener и просто перерисовывает
 * себя при получении уведомления об изменении — сама модель "толкает"
 * данные во View, а не наоборот.
 */
public class MainFrame extends JFrame implements ModelListener {

    private final NoiseModel model;
    private final NoiseController controller;

    private JLabel lastInputLabel;
    private JLabel scoreLabel;
    private JLabel levelLabel;
    private JLabel recommendationLabel;

    public MainFrame(NoiseModel model, NoiseController controller) {
        super("Калькулятор громкости жизни");
        this.model = model;
        this.controller = controller;

        model.addListener(this);

        buildUi();
        onModelChanged(model); // отобразить начальное состояние (данных ещё нет)

        pack();
        setLocationRelativeTo(null);
    }

    private void buildUi() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // BoxLayout вместо GridLayout: каждая строка занимает ровно столько
        // высоты, сколько нужно её тексту, и никогда не обрезается.
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        lastInputLabel = new JLabel();
        scoreLabel = new JLabel();
        levelLabel = new JLabel();
        recommendationLabel = new JLabel();

        for (JLabel label : new JLabel[]{lastInputLabel, scoreLabel, levelLabel, recommendationLabel}) {
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        infoPanel.add(lastInputLabel);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(scoreLabel);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(levelLabel);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(recommendationLabel);

        JButton inputButton = new JButton("Ввести данные");
        inputButton.addActionListener(e -> openInputDialog());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        buttonPanel.add(inputButton);

        add(infoPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Задаём минимальную ширину окна, чтобы самый длинный из
        // возможных текстов результата помещался в одну строку,
        // а высоту подгоняем под фактическое содержимое через pack().
        setMinimumSize(new Dimension(480, 0));
    }

    private void openInputDialog() {
        InputDialog dialog = new InputDialog(this, model, controller);
        dialog.setVisible(true);
    }

    /**
     * Этот метод вызывает сама модель, когда её состояние меняется.
     * MainFrame больше нигде не читает данные модели "по запросу" —
     * только реагирует на уведомления.
     */
    @Override
    public void onModelChanged(NoiseModel model) {
        if (!model.isDataEntered()) {
            lastInputLabel.setText("Данные ещё не введены");
            scoreLabel.setText("Уровень шума: —");
            levelLabel.setText("Категория: —");
            recommendationLabel.setText("Рекомендация: —");
            return;
        }

        lastInputLabel.setText(String.format(
            "Разговоры: %d, музыка: %d, ругань: %d",
            model.getTalkCount(), model.getMusicCount(), model.getSwearCount()));
        scoreLabel.setText(String.format("Уровень шума (баллы): %.1f", model.getNoiseScore()));
        levelLabel.setText("Категория: " + model.getNoiseLevel());
        recommendationLabel.setText("Рекомендация: " + model.getRecommendation());

        // Текст результата меняется в размере (разные числа, разные
        // рекомендации), поэтому пересчитываем размер окна каждый раз,
        // чтобы новый текст точно не обрезался.
        pack();
    }
}
