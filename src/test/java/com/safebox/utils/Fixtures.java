package com.safebox.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class Fixtures {

    private static final int DEFAULT_LIST_SIZE = 3;

    private static final EasyRandom easyRandom = new EasyRandom();

    public static List<String> getStringList() {
        return easyRandom.objects(String.class, DEFAULT_LIST_SIZE).toList();
    }

    public static <T> List<T> getList(Class<T> entityClass) {
        return getList(entityClass, DEFAULT_LIST_SIZE);
    }

    public static <T> List<T> getList(Class<T> entityClass, EasyRandomParameters easyRandomParameters) {
        return getList(entityClass, DEFAULT_LIST_SIZE, easyRandomParameters);
    }

    public static <T> List<T> getList(Class<T> entityClass, int numValues) {
        return getList(entityClass, numValues, easyRandomParameters());
    }

    public static <T> List<T> getList(Class<T> entityClass, int numValues, EasyRandomParameters parameters) {
        final var localEasyRandom = parameters == null ? easyRandom : new EasyRandom(parameters);
        final List<T> result = localEasyRandom.objects(entityClass, numValues).toList();
        if (parameters == null) {
            result.forEach(entity -> assertThat(entity)
                    .describedAs("Error generating fixture for class: " + entityClass.getName())
                    .hasNoNullFieldsOrProperties());
        }
        return result;
    }

    public static <T> T getObject(Class<T> entityClass) {
        return getObject(entityClass, easyRandomParameters());
    }

    public static <T> T getObject(Class<T> entityClass, EasyRandomParameters parameters) {
        final var localEasyRandom = parameters == null ? easyRandom : new EasyRandom(parameters);
        final T result = localEasyRandom.nextObject(entityClass);
        if (parameters == null) {
            assertThat(result).describedAs("Error generating fixture for class: " + entityClass.getName())
                    .hasNoNullFieldsOrProperties();
        }
        return result;
    }

    public static <T> Page<T> pageOf(Class<T> entityClass) {
        return new PageImpl<>(getList(entityClass));
    }

    public static <T> Page<T> pageOf(Class<T> entityClass, Pageable pageable) {
        return new PageImpl<>(getList(entityClass, pageable.getPageSize()));
    }

    public static <T> Page<T> pageOf(Class<T> entityClass, Pageable pageable, int numTotalValues) {
        return new PageImpl<>(getList(entityClass, pageable.getPageSize()), pageable, numTotalValues);
    }

    public static EasyRandomParameters easyRandomParameters() {
        EasyRandomParameters easyRandomParameters = new EasyRandomParameters()
                .scanClasspathForConcreteTypes(true)
                .ignoreRandomizationErrors(true);
        return easyRandomParameters;
    }

    public static MultipartFile getMultipartFile(String filename) throws FileNotFoundException, IOException {
        File file = new File("src/test/resources/" + filename);
        MultipartFile multipartFile;
        FileInputStream input = new FileInputStream(file);
        multipartFile = new MockMultipartFile("file",
                file.getName(), "text/plain", IOUtils.toByteArray(input));
        return multipartFile;
    }
}
