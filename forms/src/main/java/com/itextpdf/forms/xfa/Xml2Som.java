package com.itextpdf.forms.xfa;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A class for some basic SOM processing.
 */
public class Xml2Som {
    /**
     * The order the names appear in the XML, depth first.
     */
    protected ArrayList<String> order;
    /**
     * The mapping of full names to nodes.
     */
    protected HashMap<String, Node> name2Node;
    /**
     * The data to do a search from the bottom hierarchy.
     */
    protected HashMap<String, InverseStore> inverseSearch;
    /**
     * A stack to be used when parsing.
     */
    protected Stack2<String> stack;
    /**
     * A temporary store for the repetition count.
     */
    protected int anform;

    /**
     * Escapes a SOM string fragment replacing "." with "\.".
     *
     * @param s the unescaped string
     * @return the escaped string
     */
    public static String escapeSom(String s) {
        if (s == null)
            return "";
        int idx = s.indexOf('.');
        if (idx < 0)
            return s;
        StringBuffer sb = new StringBuffer();
        int last = 0;
        while (idx >= 0) {
            sb.append(s.substring(last, idx));
            sb.append('\\');
            last = idx;
            idx = s.indexOf('.', idx + 1);
        }
        sb.append(s.substring(last));
        return sb.toString();
    }

    /**
     * Unescapes a SOM string fragment replacing "\." with ".".
     *
     * @param s the escaped string
     * @return the unescaped string
     */
    public static String unescapeSom(String s) {
        int idx = s.indexOf('\\');
        if (idx < 0)
            return s;
        StringBuffer sb = new StringBuffer();
        int last = 0;
        while (idx >= 0) {
            sb.append(s.substring(last, idx));
            last = idx + 1;
            idx = s.indexOf('\\', idx + 1);
        }
        sb.append(s.substring(last));
        return sb.toString();
    }

    /**
     * Outputs the stack as the sequence of elements separated
     * by '.'.
     *
     * @return the stack as the sequence of elements separated by '.'
     */
    protected String printStack() {
        if (stack.empty())
            return "";
        StringBuffer s = new StringBuffer();
        for (int k = 0; k < stack.size(); ++k)
            s.append('.').append(stack.get(k));
        return s.substring(1);
    }

    /**
     * Gets the name with the <CODE>#subform</CODE> removed.
     *
     * @param s the long name
     * @return the short name
     */
    public static String getShortName(String s) {
        int idx = s.indexOf(".#subform[");
        if (idx < 0)
            return s;
        int last = 0;
        StringBuffer sb = new StringBuffer();
        while (idx >= 0) {
            sb.append(s.substring(last, idx));
            idx = s.indexOf("]", idx + 10);
            if (idx < 0)
                return sb.toString();
            last = idx + 1;
            idx = s.indexOf(".#subform[", last);
        }
        sb.append(s.substring(last));
        return sb.toString();
    }

    /**
     * Adds a SOM name to the search node chain.
     *
     * @param unstack the SOM name
     */
    public void inverseSearchAdd(String unstack) {
        inverseSearchAdd(inverseSearch, stack, unstack);
    }

    /**
     * Adds a SOM name to the search node chain.
     *
     * @param inverseSearch the start point
     * @param stack         the stack with the separated SOM parts
     * @param unstack       the full name
     */
    public static void inverseSearchAdd(HashMap<String, InverseStore> inverseSearch, Stack2<String> stack, String unstack) {
        String last = stack.peek();
        InverseStore store = inverseSearch.get(last);
        if (store == null) {
            store = new InverseStore();
            inverseSearch.put(last, store);
        }
        for (int k = stack.size() - 2; k >= 0; --k) {
            last = stack.get(k);
            InverseStore store2;
            int idx = store.part.indexOf(last);
            if (idx < 0) {
                store.part.add(last);
                store2 = new InverseStore();
                store.follow.add(store2);
            } else
                store2 = (InverseStore) store.follow.get(idx);
            store = store2;
        }
        store.part.add("");
        store.follow.add(unstack);
    }

    /**
     * Searches the SOM hierarchy from the bottom.
     *
     * @param parts the SOM parts
     * @return the full name or <CODE>null</CODE> if not found
     */
    public String inverseSearchGlobal(ArrayList<String> parts) {
        if (parts.isEmpty())
            return null;
        InverseStore store = inverseSearch.get(parts.get(parts.size() - 1));
        if (store == null)
            return null;
        for (int k = parts.size() - 2; k >= 0; --k) {
            String part = parts.get(k);
            int idx = store.part.indexOf(part);
            if (idx < 0) {
                if (store.isSimilar(part))
                    return null;
                return store.getDefaultName();
            }
            store = (InverseStore) store.follow.get(idx);
        }
        return store.getDefaultName();
    }

    /**
     * Splits a SOM name in the individual parts.
     *
     * @param name the full SOM name
     * @return the split name
     */
    public static Stack2<String> splitParts(String name) {
        while (name.startsWith("."))
            name = name.substring(1);
        Stack2<String> parts = new Stack2<String>();
        int last = 0;
        int pos = 0;
        String part;
        while (true) {
            pos = last;
            while (true) {
                pos = name.indexOf('.', pos);
                if (pos < 0)
                    break;
                if (name.charAt(pos - 1) == '\\')
                    ++pos;
                else
                    break;
            }
            if (pos < 0)
                break;
            part = name.substring(last, pos);
            if (!part.endsWith("]"))
                part += "[0]";
            parts.add(part);
            last = pos + 1;
        }
        part = name.substring(last);
        if (!part.endsWith("]"))
            part += "[0]";
        parts.add(part);
        return parts;
    }

    /**
     * Gets the order the names appear in the XML, depth first.
     *
     * @return the order the names appear in the XML, depth first
     */
    public ArrayList<String> getOrder() {
        return order;
    }

    /**
     * Sets the order the names appear in the XML, depth first
     *
     * @param order the order the names appear in the XML, depth first
     */
    public void setOrder(ArrayList<String> order) {
        this.order = order;
    }

    /**
     * Gets the mapping of full names to nodes.
     *
     * @return the mapping of full names to nodes
     */
    public HashMap<String, Node> getName2Node() {
        return name2Node;
    }

    /**
     * Sets the mapping of full names to nodes.
     *
     * @param name2Node the mapping of full names to nodes
     */
    public void setName2Node(HashMap<String, Node> name2Node) {
        this.name2Node = name2Node;
    }

    /**
     * Gets the data to do a search from the bottom hierarchy.
     *
     * @return the data to do a search from the bottom hierarchy
     */
    public HashMap<String, InverseStore> getInverseSearch() {
        return inverseSearch;
    }

    /**
     * Sets the data to do a search from the bottom hierarchy.
     *
     * @param inverseSearch the data to do a search from the bottom hierarchy
     */
    public void setInverseSearch(HashMap<String, InverseStore> inverseSearch) {
        this.inverseSearch = inverseSearch;
    }
}