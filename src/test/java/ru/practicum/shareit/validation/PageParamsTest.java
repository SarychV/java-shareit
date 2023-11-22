package ru.practicum.shareit.validation;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.ValidationException;

import static org.junit.jupiter.api.Assertions.*;

class PageParamsTest {

    @Test
    void validate_allVariants() {
        int obviousMistakeFrom = -10;
        int size = 1;
        assertThrows(ValidationException.class, () -> PageParams.validate(obviousMistakeFrom, size));

        int boundaryErrorFrom = -1;
        assertThrows(ValidationException.class, () -> PageParams.validate(boundaryErrorFrom, size));

        int legalFrom = 0;
        assertDoesNotThrow(() -> PageParams.validate(legalFrom, size));

        int from = 10;
        int obviousMistakeSize = -10;
        assertThrows(ValidationException.class, () -> PageParams.validate(size, obviousMistakeSize));

        int boundaryErrorSize = 0;
        assertThrows(ValidationException.class, () -> PageParams.validate(size, boundaryErrorSize));

        int legalSize = 10;
        assertDoesNotThrow(() -> PageParams.validate(size, legalSize));
    }
}