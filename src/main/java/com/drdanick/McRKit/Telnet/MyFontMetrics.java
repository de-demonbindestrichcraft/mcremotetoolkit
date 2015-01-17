// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   TelnetIOInputStream.java

package com.drdanick.McRKit.Telnet;

import java.awt.*;
import java.util.AbstractMap;
import java.util.HashMap;

class MyFontMetrics
{
    private static class CacheFactory
    {

        static synchronized WidthCache cacheForFontMetrics(FontMetrics fontmetrics)
        {
            WidthCache widthcache = (WidthCache)map.get(fontmetrics);
            if(widthcache == null)
            {
                widthcache = new WidthCache();
                map.put(fontmetrics, widthcache);
            } else
            {
                widthcache.up();
            }
            return widthcache;
        }

        static synchronized void disposeBy(FontMetrics fontmetrics)
        {
            WidthCache widthcache = (WidthCache)map.get(fontmetrics);
            if(widthcache != null)
                widthcache.down();
        }

        private static AbstractMap map = new HashMap();


        private CacheFactory()
        {
        }
    }

    static class WidthCache
    {

        public void up()
        {
            reference_count++;
            if(reference_count == 1)
                cache = new byte[0x10000];
        }

        public void down()
        {
            if(reference_count == 0)
                return;
            reference_count--;
            if(reference_count == 0)
                cache = null;
        }

        public boolean isMultiCell()
        {
            return multiCell;
        }

        public void setMultiCell(boolean flag)
        {
            multiCell = flag;
        }

        byte cache[];
        int reference_count;
        private boolean multiCell;

        WidthCache()
        {
            cache = new byte[0x10000];
            reference_count = 1;
            multiCell = false;
        }
    }


    public MyFontMetrics(Component component, Font font)
    {
        fm = component.getFontMetrics(font);
        width = fm.charWidth('a');
        height = fm.getHeight();
        ascent = fm.getAscent();
        leading = fm.getLeading();
        height -= leading;
        leading = 0;
        cwidth_cache = CacheFactory.cacheForFontMetrics(fm);
    }

    protected void finalize()
    {
        CacheFactory.disposeBy(fm);
    }

    public boolean isMultiCell()
    {
        return cwidth_cache.isMultiCell();
    }

    public int wcwidth(char c)
    {
        int i = cwidth_cache.cache[c];
        if(i == 0)
        {
            int j = fm.charWidth(c);
            if(j == width)
                i = 1;
            else
            if(j == 0)
            {
                i = 1;
            } else
            {
                int k = j % width;
                int l = j;
                if(k != 0)
                    l = j + (width - k);
                i = l / width;
                if(i == 0)
                    i = 1;
                cwidth_cache.setMultiCell(true);
            }
            cwidth_cache.cache[c] = (byte)i;
        }
        return i;
    }

    void checkForMultiCell(char c)
    {
        wcwidth(c);
    }

    public int width;
    public int height;
    public int ascent;
    public int leading;
    public FontMetrics fm;
    private WidthCache cwidth_cache;
}
