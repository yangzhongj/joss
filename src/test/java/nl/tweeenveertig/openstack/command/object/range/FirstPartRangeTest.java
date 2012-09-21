package nl.tweeenveertig.openstack.command.object.range;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class FirstPartRangeTest {

    @Test
    public void headerValue() {
        FirstPartRange range = new FirstPartRange(8);
        assertEquals("bytes: 0-8", range.generateHeaderValue());
    }
}