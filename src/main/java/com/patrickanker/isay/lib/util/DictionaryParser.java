/*
 * Copyright (C) 2011 - 2012, psanker and contributors
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are 
 * permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this list of 
 *   conditions and the following 
 * * Redistributions in binary form must reproduce the above copyright notice, this list of 
 *   conditions and the following disclaimer in the documentation and/or other materials 
 *   provided with the distribution.
 * * Neither the name of Overcaffeinated Development nor the names of its contributors may be 
 *   used to endorse or promote products derived from this software without specific prior 
 *   written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS 
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE 
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR 
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.patrickanker.isay.lib.util;

import java.util.List;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DictionaryParser {
    
    public static MutableDictionary getDictionaryFromElement(Element element) 
    {
        MutableDictionary ret = new MutableDictionary();

        NodeList l = element.getChildNodes();
        String key;
        
        for (int i = 0; i < l.getLength(); ++i) {
            Node node = l.item(i);
            
            // Get key
            key = node.getNodeName();
            
            // Detect if node is dictionary/list
            NodeList tmplist = node.getChildNodes();
            
            if (tmplist.getLength() > 0) {
                if (tmplist.getLength() == 1 && tmplist.item(0).getNodeName().equalsIgnoreCase("list")) {
                    ret.put(key, ListParser.getListFromElement((Element) tmplist.item(0)));
                    continue;
                } else {
                    ret.put(key, getDictionaryFromElement((Element) node));
                    continue;
                }
            }
            
            // Not list nor dictionary... Insert and move on
            smartCastAndInsert(key, node.getTextContent(), ret);
        }
        
        return ret;
    }
    
    public static void bindDictionaryToElement(MutableDictionary dict, Document doc, Element element)
    {
        for (Map.Entry<String, Object> entry : dict) {
            String k = entry.getKey();
            Object v = entry.getValue();
            
            Element key = doc.createElement(k);
            
            if (v instanceof List<?>) {
                Element value = doc.createElement("list");
                ListParser.bindListToElement((List<Object>) v, doc, value);
                key.appendChild(value);
            } else if (v instanceof MutableDictionary) {
                bindDictionaryToElement((MutableDictionary) v, doc, key);
            } else if (v instanceof Integer) {
                key.setTextContent(((Integer) v).toString());
            } else if (v instanceof Boolean) {
                key.setTextContent(((Boolean) v).toString());
            } else if (v instanceof String) {
                key.setTextContent(v.toString());
            }
            
            element.appendChild(key);
        }
    }
    
    static void smartCastAndInsert(String k, String v, MutableDictionary dict)
    {
        try {
            Integer i = Integer.parseInt(v);
            dict.put(k, i);
        } catch (NumberFormatException ex) {
            if (v.equalsIgnoreCase("true") || v.equalsIgnoreCase("false")) {
                Boolean bool = Boolean.parseBoolean(v);
                dict.put(k, bool);
                return;
            }
            
            dict.put(k, v);
        }
    }
}
