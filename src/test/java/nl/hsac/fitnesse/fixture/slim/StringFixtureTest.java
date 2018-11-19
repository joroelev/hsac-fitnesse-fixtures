package nl.hsac.fitnesse.fixture.slim;

import com.sun.org.apache.xpath.internal.operations.Bool;
import com.sun.xml.internal.bind.v2.TODO;
import net.jqwik.api.arbitraries.IntegerArbitrary;
import net.jqwik.api.arbitraries.StringArbitrary;
import net.jqwik.api.constraints.*;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Test;

import net.jqwik.api.*;
import org.junit.jupiter.api.Assertions;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
//import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests StringFixture.
 */
public class StringFixtureTest {
    private final StringFixture fixture = new StringFixture();

//    @Test
//    public void testValue() {
//        assertEquals("null", null, fixture.valueOf(null));
//        assertEquals("hello", "hello", fixture.valueOf("hello"));
//    }

    @Property
    void lengthOf_returns_string_length_when_string_is_not_null(@ForAll String string) {
        int result = fixture.lengthOf(string);

        assertThat(result).isEqualTo(string.length());
    }

    @Example
    void lengthOf_null_returns_0() {
        int result = fixture.lengthOf(null);

        assertThat(result).isEqualTo(0);
    }

//    @Test
//    public void testValueDiffersFrom() {
//        assertFalse("null - null", fixture.valueDiffersFrom(null, null));
//        assertFalse("hello - hello", fixture.valueDiffersFrom("hello", "hello"));
//        assertTrue("null - hello", fixture.valueDiffersFrom(null, "hello"));
//        assertTrue("hello - null", fixture.valueDiffersFrom("hello", null));
//        assertTrue("hello - hela", fixture.valueDiffersFrom("hello", "hela"));
//    }

    @Property
    void valueDiffersFrom_returns_true_when_given_two_different_strings(
            @ForAll String string1,
            @ForAll String string2
    ) {
        Assume.that(!string1.equals(string2));

        Boolean result = fixture.valueDiffersFrom(string1, string2);

        assertThat(result).isTrue();
    }

    //    @Test
//    public void testValueEquals() {
//        assertTrue("null - null", fixture.valueEquals(null, null));
//        assertTrue("hello - hello", fixture.valueEquals("hello", "hello"));
//        assertFalse("null - hello", fixture.valueEquals(null, "hello"));
//        assertFalse("hello - null", fixture.valueEquals("hello", null));
//        assertFalse("hello - hela", fixture.valueEquals("hello", "hela"));
//    }

    @Property
    void valueEquals_returns_true_when_given_two_equal_strings(@ForAll String string1) {
        Boolean result = fixture.valueEquals(string1, string1);

        assertThat(result).isTrue();
    }

//    @Test
//    public void testTextContains() {
//        //assertFalse("null value", fixture.textContains(null, "Hello"));
//        //assertFalse("bad value", fixture.textContains("World", "world"));
//        assertTrue("good value", fixture.textContains("Hello", "I said 'Hello' to the world"));
//    }

    @Property
    void textContains_returns_true_when_string_contains_given_substring(
            @ForAll @StringLength(min = 10, max = 100) String value,
            @ForAll @IntRange(max = 10) int length,
            @ForAll @IntRange(min = 5, max = 99) int startIndex
    ) {
        Assume.that((startIndex + length) <= value.length());

        String substring = value.substring(startIndex, startIndex + length);
        Boolean result = fixture.textContains(value, substring);

        assertThat(result).isTrue();
    }

    @Property
    void textContains_null_value_returns_false(@ForAll String substring) {

        Boolean result = fixture.textContains(null, substring);

        assertThat(result).isFalse();
    }

    // TODO: fix fixture to return false
//    @Property
//    void textContains_null_substring_returns_false(@ForAll String value) {
//        Boolean result = fixture.textContains(value, null);
//
//        assertThat(result).isFalse();
//    }

//    @Test
//    public void testConvertToInt() {
//        assertNull("null", fixture.convertToInt(null));
//        assertEquals("10", Integer.valueOf(10), fixture.convertToInt("10"));
//    }

    /*
     * Create an int and cast to String because then we always end up with
     * a number <= int.MAX_VALUE
     */
    @Property
    void should_be_converted_to_int(@ForAll int number) {
        String numberString = String.valueOf(number);

        Integer result = fixture.convertToInt(numberString);

        assertThat(result).isEqualTo(number);
    }

    @Property
    void should_trow_number_format_exception_when_(@ForAll Long number) {

        Assume.that(number < Integer.MIN_VALUE || number > Integer.MAX_VALUE);
        String numberString = String.valueOf(number);

        Throwable thrown = catchThrowable(() -> fixture.convertToInt(numberString));

        assertThat(thrown).isInstanceOf(NumberFormatException.class);
    }


//    @Test
//    public void testConvertToDouble() {
//        assertNull("null", fixture.convertToDouble(null));
//        assertEquals("10.23", Double.valueOf(10.23), fixture.convertToDouble("10.23"));
//    }
//
//    @Property
//    void should_convert_string_to_double(@ForAll Double number){
//
//
//    }


//    @Test
//    public void testNormalizeWhitespace() {
//        assertNull("null", fixture.normalizeWhitespace(null));
//        assertEquals("abc", "abc", fixture.normalizeWhitespace("abc"));
//        assertEquals(" 10.23  ", "10.23", fixture.normalizeWhitespace(" 10.23  "));
//        assertEquals(" 1 ==  2  ", "1 == 2", fixture.normalizeWhitespace(" 1 ==  2  "));
//        assertEquals("\t1\t\t ==  2", "1 == 2", fixture.normalizeWhitespace("\t1\t\t ==  2 "));
//        assertEquals(" 1 ==\n\t2\n\n", "1 == 2", fixture.normalizeWhitespace(" 1 ==\n\t2\n\n"));
//    }
//
//    @Test
//    public void testReplaceAllWithRegExIn() {
//        assertNull("null", fixture.replaceAllInWith("b", null, "a"));
//        assertEquals("$1", "23", fixture.replaceAllInWith("\\((\\d+)\\)", "(23)", "$1"));
//        assertEquals("$1$0", "23(23)", fixture.replaceAllInWith("\\((\\d+)\\)", "(23)", "$1$0"));
//        assertEquals("dot matches newline", "23\na", fixture.replaceAllInWith("\\((.*?)\\)", "(23\na)", "$1"));
//        assertEquals("null replace value is empty string", "hallo", fixture.replaceAllInWith(" user", "hallo user", null));
//    }
//
//    @Test
//    public void testExtractIntFromUsingGroup() {
//        assertNull("null", fixture.extractIntFromUsingGroup(null, "(\\d+)", 1));
//        assertEquals("A023", Integer.valueOf(23), fixture.extractIntFromUsingGroup("A023", "A(\\d+)", 1));
//        assertEquals("12A023", Integer.valueOf(23), fixture.extractIntFromUsingGroup("12A023", "(\\d+)[A-Z](\\d+)", 2));
//    }
//
//    @Test
//    public void testConvertToUpperCase() {
//        assertNull("null", fixture.convertToUpperCase(null));
//        assertEquals("abC1", "ABC1", fixture.convertToUpperCase("abC1"));
//    }
//
//    @Test
//    public void testConvertToLowerCase() {
//        assertNull("null", fixture.convertToLowerCase(null));
//        assertEquals("abC1", "abc1", fixture.convertToLowerCase("abC1"));
//    }
}
