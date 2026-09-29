package noisecalc;


public class NoiseController {

    private final NoiseModel model;

    public NoiseController(NoiseModel model) {
        this.model = model;
    }

    public void handleInput(String talkText, String musicText, String swearText)
            throws InvalidInputException {

        int talk = parsePositiveInt(talkText, "Количество разговоров в день");
        int music = parsePositiveInt(musicText, "Количество прослушиваний музыки в день");
        int swear = parsePositiveInt(swearText, "Количество случаев ругани в день");

        // Дальнейшую проверку диапазона и пересчёт результата
        // выполняет сама модель.
        model.setData(talk, music, swear);
    }

    private int parsePositiveInt(String text, String fieldName) throws InvalidInputException {
        if (text == null || text.trim().isEmpty()) {
            throw new InvalidInputException(fieldName + " не может быть пустым.");
        }
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            throw new InvalidInputException(fieldName + " должно быть целым числом.");
        }
    }
}
