package com.pinyin.spliter;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * unit test <p>
 * 
 */
public class PinyinSpliterTest
{
    private PinyinSpliter spliter;
    
    @BeforeClass
    public void setUp()
    {
        spliter = new PinyinSpliter();
    }
    @Test
    public void splitPinyin()
    {        
        List<String> result = spliter.splitPinyin("awo");
        String expected = "[a, wo]";
        Assert.assertEquals(expected, result.toString(), "pinyin split failed");
    }
}
