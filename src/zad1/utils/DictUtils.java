package zad1.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DictUtils {

    private static final Map<String, Map<String, String>> translationsByLanguage =
            new HashMap<String, Map<String, String>>() {
                {
                    put("en", new HashMap<String, String>() {{
                        put("kot", "cat");
                        put("pies", "dog");
                        put("kwiat", "flower");
                        put("rower", "bike");
                        put("tęcza", "rainbow");
                    }});

                    put("de", new HashMap<String, String>() {{
                        put("kot", "Katze");
                        put("pies", "Hund");
                        put("kwiat", "Blume");
                        put("rower", "Fahrrad");
                        put("tęcza", "Regenbogen");
                    }});

                    put("fr", new HashMap<String, String>() {{
                        put("kot", "chat");
                        put("pies", "chien");
                        put("kwiat", "fleur");
                        put("rower", "vélo");
                        put("tęcza", "arc en ciel");
                    }});

                    put("es", new HashMap<String, String>() {{
                        put("kot", "gato");
                        put("pies", "perro");
                        put("kwiat", "flor");
                        put("rower", "bicicleta");
                        put("tęcza", "arco iris");
                    }});
                }
            };

    public static List<String> listSupportedLanguages() {
        return translationsByLanguage
                .keySet()
                .stream()
                .sorted()
                .collect(Collectors.toList());
    }

    public static Map<String, String> getTranslationsForGivenLang(String language) throws NotSupportedLanguageException {

        if (translationsByLanguage.containsKey(language)) {
            return translationsByLanguage.get(language);
        }

        throw new NotSupportedLanguageException("This language is not supported! " + language);
    }
}
