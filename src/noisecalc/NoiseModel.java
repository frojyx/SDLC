package noisecalc;

import java.util.ArrayList;
import java.util.List;

/**
 * АКТИВНАЯ МОДЕЛЬ.
 *
 * "Активная" означает, что модель не пассивно хранит данные, а сама
 * оповещает всех подписчиков (представления) о том, что её состояние
 * изменилось, вызывая listener.onModelChanged(this). Представлению
 * не нужно ничего опрашивать самому — оно просто подписывается один раз.
 */
public class NoiseModel {

    public static final int MIN_VALUE = 0;
    public static final int MAX_VALUE = 200;

    private int talkCount;
    private int musicCount;
    private int swearCount;

    private double noiseScore;
    private String noiseLevel = "";
    private String recommendation = "";

    // Пока пользователь ни разу не ввёл данные — результат показывать нельзя
    private boolean dataEntered = false;

    private final List<ModelListener> listeners = new ArrayList<>();

    public void addListener(ModelListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ModelListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (ModelListener listener : listeners) {
            listener.onModelChanged(this);
        }
    }

    /**
     * Основной метод модели. Контроллер вызывает его с уже распарсенными
     * целыми числами. Модель сама проверяет диапазон значений, и только
     * если данные корректны — пересчитывает результат и уведомляет
     * подписчиков. Если данные некорректны — бросает исключение и
     * состояние модели НЕ меняется (уведомление не отправляется).
     */
    public void setData(int talk, int music, int swear) throws InvalidInputException {
        validateRange(talk, "Количество разговоров в день");
        validateRange(music, "Количество прослушиваний музыки в день");
        validateRange(swear, "Количество случаев ругани в день");

        this.talkCount = talk;
        this.musicCount = music;
        this.swearCount = swear;
        this.dataEntered = true;

        recalculate();
        notifyListeners();
    }

    private void validateRange(int value, String fieldName) throws InvalidInputException {
        if (value < MIN_VALUE || value > MAX_VALUE) {
            throw new InvalidInputException(
                fieldName + " должно быть целым числом от " + MIN_VALUE +
                " до " + MAX_VALUE + ".");
        }
    }

   
    private void recalculate() {
        noiseScore = talkCount * 1.0 + musicCount * 1.5 + swearCount * 2.5;

        if (noiseScore <= 20) {
            noiseLevel = "Низкий";
            recommendation = "Всё спокойно — дополнительные меры не требуются";
        } else if (noiseScore <= 50) {
            noiseLevel = "Средний";
            recommendation = "Можно выпустить пар весело — сходи на караоке";
        } else {
            noiseLevel = "Высокий";
            recommendation = "Шума многовато — стоит завести беруши для отдыха";
        }
    }

    public int getTalkCount() {
        return talkCount;
    }

    public int getMusicCount() {
        return musicCount;
    }

    public int getSwearCount() {
        return swearCount;
    }

    public double getNoiseScore() {
        return noiseScore;
    }

    public String getNoiseLevel() {
        return noiseLevel;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public boolean isDataEntered() {
        return dataEntered;
    }
}
