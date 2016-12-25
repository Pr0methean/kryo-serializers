package de.javakaffee.kryoserializers;

import static de.javakaffee.kryoserializers.KryoTest.deserialize;
import static de.javakaffee.kryoserializers.KryoTest.serialize;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Kryo.DefaultInstantiatorStrategy;

import org.objenesis.ObjenesisStd;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.lang.Character.UnicodeBlock;

/**
 * Test for {@link UnicodeBlockSerializer}.
 *
 * @author <a href="mailto:seahen123@gmail.com">Chris Hennick</a>
 */
public class UnicodeBlockSerializerTest {

    private static final String NONEXISTENT_BLOCK_NAME = "RURITANIAN";
    private Kryo kryo;

    private static class ThingWithUnicodeBlock {
        final UnicodeBlock unicodeBlock;

        private ThingWithUnicodeBlock(UnicodeBlock unicodeBlock) {
            this.unicodeBlock = unicodeBlock;
        }
    }

    @BeforeTest
    protected void beforeTest() {
        kryo = new Kryo();
        final DefaultInstantiatorStrategy instantiatorStrategy = new DefaultInstantiatorStrategy();
        instantiatorStrategy.setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());
        kryo.setInstantiatorStrategy(instantiatorStrategy);
        kryo.register(UnicodeBlock.class, new UnicodeBlockSerializer());
    }

    @Test
    public void testBasicRoundTrip() {
        byte[] serialized = serialize(kryo, UnicodeBlock.UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS);
        try {
        assertSame(deserialize(kryo, serialized, UnicodeBlock.class),
                UnicodeBlock.UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS);
        } catch (NullPointerException e) {
            // Temporary debugging code
            e.printStackTrace();
            throw e;
        }
    }

    @Test
    public void testDeserializingUnknownInstanceReturnsNull() {
        byte[] serialized = serialize(kryo, new ObjenesisStd().newInstance(UnicodeBlock.class));
        try {
            assertNull(deserialize(kryo, serialized, UnicodeBlock.class));
        } catch (NullPointerException e) {
            // Temporary debugging code
            e.printStackTrace();
            throw e;
        }
    }

    @Test
    public void testCopyContainingObject() {
        ThingWithUnicodeBlock original = new ThingWithUnicodeBlock(UnicodeBlock.GREEK);
        assertSame(UnicodeBlock.GREEK, kryo.copy(original).unicodeBlock);
    }
}
