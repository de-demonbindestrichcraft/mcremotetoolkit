// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Scheduler.java
package com.drdanick.McRKit;

import java.io.PrintStream;
import java.text.*;
import java.util.*;

// Referenced classes of package com.drdanick.McRKit:
//            ScheduledEvent, ScheduledEventListener, ScheduledTask
public class Scheduler {

    public enum Type {

        MESSAGE, SAVE, RESTART, RESTARTALERT, KILL, HEARTBEAT, HEARTBEATRESTART, SHUTDOWN, SHUTDOWNALERT;

        private Type() {
        }
    }

    public Scheduler(Map map) {
        registeredListeners = new LinkedList();
        scheduler = null;
        messageMap = map;
        refresh();
    }

    public Scheduler(ScheduledEventListener scheduledeventlistener, Map map) {
        registeredListeners = new LinkedList();
        scheduler = null;
        addScheduleListener(scheduledeventlistener);
        messageMap = map;
        refresh();
    }

    public void refresh() {
        if (scheduler != null) {
            scheduler.cancel();
            scheduler = null;
        }
        scheduler = new Timer();
    }

    public ScheduledTask scheduleRestartEvent(String s, boolean flag) {
        if (!flag) {
            return scheduleEvent(new ScheduledEvent(Type.RESTART, (String) messageMap.get("restart-warning")), new Date(System.currentTimeMillis() + convertFormattedTime(s)));
        }
        String as[] = s.split(",");
        long l = 0L;
        try {
            l = ((Long) getOrderedTimes(as).first()).longValue();
        } catch (ParseException parseexception) {
            System.out.println("Error parsing a restart time!\nReverting to 5h periodical restarts!");
            return scheduleRestartEvent("5h", false);
        }
        return scheduleEvent(new ScheduledEvent(Type.RESTART, (String) messageMap.get("restart-warning")), new Date(l));
    }

    public ScheduledTask scheduleShutdownEvent(String s, boolean flag) {
        if (!flag) {
            return scheduleEvent(new ScheduledEvent(Type.SHUTDOWN, (String) messageMap.get("toolkit-shutdown-warning")), new Date(System.currentTimeMillis() + convertFormattedTime(s)));
        }
        String as[] = s.split(",");
        long l = 0L;
        try {
            l = ((Long) getOrderedTimes(as).first()).longValue();
        } catch (ParseException parseexception) {
            System.out.println("Error parsing a shutdown time!\nDisabling shutdown!");
            return null;
        }
        return scheduleEvent(new ScheduledEvent(Type.SHUTDOWN, (String) messageMap.get("toolkit-shutdown-warning")), new Date(l));
    }

    public ScheduledTask scheduleRestartEvent(long l) {
        return scheduleEvent(new ScheduledEvent(Type.RESTART, (String) messageMap.get("restart-warning")), new Date(l));
    }

    public ScheduledTask scheduleKillEvent(String s) {
        return scheduleEvent(new ScheduledEvent(Type.KILL, (String) messageMap.get("restart-warning")), new Date(System.currentTimeMillis() + convertFormattedTime(s)));
    }

    public ScheduledTask scheduleKillEvent(long l) {
        return scheduleEvent(new ScheduledEvent(Type.KILL, (String) messageMap.get("restart-warning")), new Date(l));
    }

    public ScheduledTask scheduleAlertEvent(String s, Long long1, String s1, Type type) {
        Long long2 = Long.valueOf(long1.longValue() - convertFormattedTime(s));
        if (long2.longValue() <= System.currentTimeMillis()) {
            System.err.println((new StringBuilder()).append("[WARNING] Cannot add alert \"").append(s).append("\", it occurs in the past.").toString());
            return null;
        } else {
            String s2 = getDetailedTimeString(long1.longValue() - long2.longValue());
            s1 = s1.replaceAll("%t", s2);
            s1 = s1.replaceAll("%d", formatDate("h:mm a", new Date(long1.longValue())));
            s1 = s1.replaceAll("%D", formatDate("HH:mm", new Date(long1.longValue())));
            return scheduleEvent(new ScheduledEvent(type, s1), new Date(long2.longValue()));
        }
    }

    public ScheduledTask scheduleSaveEvent(String s) {
        if (s.equals("0")) {
            return null;
        }
        long l = convertFormattedTime(s);
        if (l <= 0L) {
            return null;
        } else {
            return scheduleEventAtFixedRate(new ScheduledEvent(Type.SAVE, (String) messageMap.get("auto-save-start")), Long.valueOf(l));
        }
    }

    public ScheduledTask scheduleHeartbeat(long l) {
        if (l <= 0L) {
            return null;
        } else {
            return scheduleEventAtFixedRate(new ScheduledEvent(Type.HEARTBEAT, ""), Long.valueOf(l));
        }
    }

    public ScheduledTask scheduleEvent(ScheduledEvent scheduledevent, Date date) {
        ScheduledTask scheduledtask = new ScheduledTask(scheduledevent) {

            public void run() {
                fireScheduledEvent(e);
            }
        };
        scheduler.schedule(scheduledtask, date);
        return scheduledtask;
    }

    public ScheduledTask scheduleTask(ScheduledTask scheduledtask, Date date) {
        scheduler.schedule(scheduledtask, date);
        return scheduledtask;
    }

    public ScheduledTask scheduleEventAtFixedRate(ScheduledEvent scheduledevent, Long long1) {
        ScheduledTask scheduledtask = new ScheduledTask(scheduledevent) {

            public void run() {
                fireScheduledEvent(e);
            }
        };
        scheduler.scheduleAtFixedRate(scheduledtask, long1.longValue(), long1.longValue());
        return scheduledtask;
    }

    public ScheduledTask scheduleEventAtFixedRate(ScheduledTask scheduledtask, Long long1) {
        scheduler.scheduleAtFixedRate(scheduledtask, long1.longValue(), long1.longValue());
        return scheduledtask;
    }

    public static long convertFormattedTime(String s) {
        long l = 0L;
        String as[];
        int i;
        int j;
        as = s.split(" ");
        i = as.length;
        j = 0;
        _L1:
        if (j >= i) {
            return -1;
        }
        String s1 = as[j];
        String s2 = s1.substring(0, s1.length() - 1);
        if (s1.endsWith("h")) {
            l += Integer.parseInt(s2) * 0x36ee80;
        } else if (s1.endsWith("m")) {
            l += Integer.parseInt(s2) * 60000;
        } else if (s1.endsWith("s")) {
            l += Integer.parseInt(s2) * 1000;
        } else {
            return -1L;
        }
        try {
            j++;
        } catch (Exception exception) {
            System.err.println((new StringBuilder()).append("Error parsing formatted time \"").append(s).append("\" ").append(exception).toString());
            return -1L;
        }
        return l;
    }

    public static String formatDate(String s, Date date) {
        SimpleDateFormat simpledateformat = new SimpleDateFormat(s);
        return simpledateformat.format(date);
    }

    public static TreeSet getOrderedTimes(String as[])
            throws ParseException {
        TreeSet treeset = new TreeSet();
        long l = System.currentTimeMillis();
        String as1[] = as;
        int i = as1.length;
        for (int j = 0; j < i; j++) {
            String s = as1[j];
            long l1 = parseExactTimeMillis(s.trim()) - l;
            treeset.add(Long.valueOf(l1 >= 0L ? parseExactTimeMillis(s.trim()) : parseExactTimeMillis(s.trim()) + 0x5265c00L));
        }

        return treeset;
    }

    public static long parseExactTimeMillis(String s)
            throws ParseException {
        return parseExactTime(s).getTime();
    }

    public static Date parseExactTime(String s)
            throws ParseException {
        SimpleDateFormat simpledateformat = new SimpleDateFormat("HH:mm");
        simpledateformat.parse(s);
        Calendar calendar = Calendar.getInstance();
        calendar.set(11, simpledateformat.getCalendar().get(11));
        calendar.set(12, simpledateformat.getCalendar().get(12));
        calendar.set(13, 0);
        calendar.set(14, 0);
        return calendar.getTime();
    }

    public void purge() {
        scheduler.purge();
    }

    public static String getDetailedTimeString(long l) {
        int i = (int) (l / 0x36ee80L);
        l -= i * 0x36ee80;
        int j = (int) (l / 60000L);
        l -= j * 60000;
        int k = (int) (l / 1000L);
        String s = "";
        if (i > 0) {
            s = (new StringBuilder()).append(s).append(i).toString();
            if (i == 1) {
                s = (new StringBuilder()).append(s).append(" hour").toString();
            } else {
                s = (new StringBuilder()).append(s).append(" hours").toString();
            }
        }
        if (j > 0) {
            if (i > 0 && k > 0) {
                s = (new StringBuilder()).append(s).append(", ").toString();
            } else if (i > 0 && k == 0) {
                s = (new StringBuilder()).append(s).append(" and ").toString();
            }
            s = (new StringBuilder()).append(s).append(j).toString();
            if (j == 1) {
                s = (new StringBuilder()).append(s).append(" minute").toString();
            } else {
                s = (new StringBuilder()).append(s).append(" minutes").toString();
            }
        }
        if (k > 0) {
            if (i > 0 || j > 0) {
                s = (new StringBuilder()).append(s).append(" and ").toString();
            }
            s = (new StringBuilder()).append(s).append(k).toString();
            if (k == 1) {
                s = (new StringBuilder()).append(s).append(" second").toString();
            } else {
                s = (new StringBuilder()).append(s).append(" seconds").toString();
            }
        }
        return s;
    }

    private void fireScheduledEvent(ScheduledEvent scheduledevent) {
        ScheduledEventListener scheduledeventlistener;
        for (Iterator iterator = registeredListeners.iterator(); iterator.hasNext(); scheduledeventlistener.handleScheduledEvent(scheduledevent)) {
            scheduledeventlistener = (ScheduledEventListener) iterator.next();
        }

    }

    public void addScheduleListener(ScheduledEventListener scheduledeventlistener) {
        if (!registeredListeners.contains(scheduledeventlistener)) {
            registeredListeners.add(scheduledeventlistener);
        }
    }

    public void removeScheduleListener(ScheduledEventListener scheduledeventlistener) {
        registeredListeners.remove(scheduledeventlistener);
    }
    private LinkedList registeredListeners;
    private Timer scheduler;
    private Map messageMap;
}
