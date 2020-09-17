package com.salesforce.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;


/**
 * This extension of HashMap support duplicate keys
 */
public class Bag extends LinkedHashMap {
    public List getValues(Object key) {
        if (super.containsKey(key)) {
            return (List) super.get(key);
        } else {
            return new ArrayList();
        }
    }

    public Object get(Object key) {
        ArrayList values = (ArrayList) super.get(key);
        if (values != null && !values.isEmpty()) {
            return values.get(0);
        } else {
            return null;
        }
    }

    public boolean containsValue(Object value) {
        return values().contains(value);
    }

    public int size() {
        int size = 0;
        Iterator keyIterator = super.keySet().iterator();

        while (keyIterator.hasNext()) {
            ArrayList values = (ArrayList) super.get(keyIterator.next());
            size = size + values.size();
        }

        return size;
    }

    public Object put(Object key, Object value) {
        ArrayList values = new ArrayList();

        if (super.containsKey(key)) {
            values = (ArrayList) super.get(key);
            values.add(value);

        } else {
            values.add(value);
        }

        super.put(key, values);

        return null;
    }

    public boolean remove(Object key, Object value) {
        boolean removed = false;
        List values = getValues(key);
        if (values != null) {
            values.remove(value);
            if (values.isEmpty()) {
                remove(key);
            }
            removed = true;
        }
        return removed;
    }

    public Collection values() {
        List values = new ArrayList();
        Iterator keyIterator = super.keySet().iterator();

        while (keyIterator.hasNext()) {
            List keyValues = (List) super.get(keyIterator.next());
            values.addAll(keyValues);
        }

        return values;
    }
}
