package property.manager.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

public class StringToEnumIgnoringCaseConverterFactory implements ConverterFactory<String, Enum> {

    @Override
    public <T extends Enum> Converter<String, T> getConverter(Class<T> targetType) {
        return source -> {
            if (source == null) {
                return null;
            }
            for (T enumConstant : targetType.getEnumConstants()) {
                if (enumConstant.name().equalsIgnoreCase(source)) {
                    return enumConstant;
                }
            }
            throw new IllegalArgumentException("No enum constant " + targetType.getSimpleName()
                    + "." + source);
        };
    }
}
