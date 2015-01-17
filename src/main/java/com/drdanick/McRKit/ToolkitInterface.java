// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ToolkitInterface.java

package com.drdanick.McRKit;

import java.io.IOException;

// Referenced classes of package com.drdanick.McRKit:
//            ToolkitAction

public interface ToolkitInterface
{

    public abstract void writeStringToConsole(String s)
        throws IOException;

    public abstract boolean performAction(ToolkitAction toolkitaction, String s);
}
