package io.advantageous.reakt.impl;

import io.advantageous.reakt.Expected;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.*;

public class ExpectedImplTest {


    @Test
    public void isAbsent() throws Exception {

        final Expected<Object> objectExpected = Expected.ofNullable(null);
        assertTrue(objectExpected.isAbsent());
    }

    @Test
    public void isEmpty() throws Exception {

        final Expected<Object> expected1 = Expected.ofNullable(null);
        assertTrue(expected1.isEmpty());
        final Expected<Object> expected2 = Expected.ofNullable(Collections.emptyList());
        assertTrue(expected2.isEmpty());
        final Expected<Object> expected3 = Expected.ofNullable(new Object());
        assertFalse(expected3.isEmpty());
        final Expected<Object> expected4 = Expected.ofNullable("");
        assertTrue(expected4.isEmpty());

        final Expected<Object> expected5 = Expected.ofNullable(new Object[0]);
        assertTrue(expected5.isEmpty());
    }

    @Test
    public void ifEmpty() throws Exception {

        final Expected<Object> expected = Expected.ofNullable(new Object());
        expected.ifEmpty(Assert::fail);
        final Expected<Object> expected2 = Expected.ofNullable(Collections.singleton(new Object()));
        expected2.ifEmpty(Assert::fail);

        final Expected<Object> expected3 = Expected.ofNullable("abc");
        expected3.ifEmpty(Assert::fail);

    }

    @Test
    public void ifNotEmpty() throws Exception {
        final Expected<Object> expected = Expected.ofNullable(null);
        expected.ifNotEmpty((it) -> fail());

        final Expected<Object> expected2 = Expected.ofNullable(Collections.emptyList());
        expected2.ifNotEmpty((it) -> fail());

    }

    @Test
    public void ifAbsent() throws Exception {


        final Expected<Object> expected = Expected.ofNullable(new Object());

        expected.ifAbsent(Assert::fail);
    }

}