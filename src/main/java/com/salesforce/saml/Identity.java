/*
 * Copyright (c) 2012, Salesforce.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the names salesforce, salesforce.com, nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.salesforce.saml;

import com.salesforce.util.Bag;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;

public class Identity {



    private String subject;
    private Bag attributes;

    public Identity(String identity, boolean encoded) throws UnsupportedEncodingException {

        this.attributes = new Bag();
        if (encoded) {
            byte[] theBytes = Base64.decodeBase64(identity);
            String jsonString = new String(theBytes,"UTF-8");
            JSONObject j = new JSONObject(jsonString);
            this.subject = j.getString("subject");

            Iterator iterator = j.keySet().iterator();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                if (!key.equals("subject")) {
                    JSONArray ja = j.getJSONArray(key);
                    for (int i = 0; i < ja.length(); i++) {
                        this.attributes.put(key, ja.getString(i));
                    }
                }
            }
        } else {
            this.subject = identity;
        }
    }

    public String getSubject() {
        return subject;
    }

    public Bag getAttributes() {
        return attributes;
    }
}
