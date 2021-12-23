package com.github.ckblck.commons;

import com.github.ckblck.compatibility.Compatible;
import com.github.ckblck.compatibility.Compatible1_13;
import com.github.ckblck.compatibility.Compatible1_13_2;
import com.github.ckblck.compatibility.Compatible1_9;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum Provider {
    COMPATIBILITY_1_9("1.9", new Compatible1_9()),
    COMPATIBILITY_1_10("1.10", new Compatible1_9()), // No API changes.
    COMPATIBILITY_1_11("1.11", new Compatible1_9()), // No API changes.
    COMPATIBILITY_1_12("1.12", new Compatible1_9()), // No API changes.
    COMPATIBILITY_1_13("1.13", new Compatible1_13()),
    COMPATIBILITY_1_13_1("1.13.1", new Compatible1_13()),
    COMPATIBILITY_1_13_2("1.13.2", new Compatible1_13_2()),
    COMPATIBILITY_1_14("1.14", new Compatible1_13_2()), // No API changes.
    COMPATIBILITY_1_15("1.15", new Compatible1_13_2()), // No API changes.
    COMPATIBILITY_1_16("1.16", new Compatible1_13_2()), // No API changes.
    COMPATIBILITY_1_17("1.17", new Compatible1_13_2()), // No API changes.
    COMPATIBILITY_1_18("1.18", new Compatible1_13_2()); // No API changes.

    @Getter
    private static Provider instance;

    private final String serverVersion;

    @Getter
    private final Compatible<?> compatible;

    public static void init(String serverVersion) throws IllegalArgumentException {
        if (instance != null)
            return;

        Stream<Provider> providerStream = Arrays.stream(values());

        Optional<Provider> optionalProvider = providerStream
                .filter(provider -> serverVersion.startsWith(provider.serverVersion))
                .findAny();

        if (optionalProvider.isPresent()) {
            instance = optionalProvider.get();
        } else {
            Provider lastProvider = values()[values().length - 1];

            Function<String, Double> getVersion = (version) -> {
                String decimalPart = version.split("\\.")[1]; // "1.16" -> "16"

                return Double.parseDouble(decimalPart);
            };

            double lastProviderNumericVersion = getVersion.apply(lastProvider.serverVersion);
            double numericVersion = getVersion.apply(serverVersion);

            if (numericVersion < 9) { // < than 1.9
                throw new IllegalArgumentException("Unsupported server version: " + serverVersion);
            }

            if (numericVersion > lastProviderNumericVersion) { // Blindly support upcoming versions.
                instance = lastProvider;
            }
        }

    }

}
