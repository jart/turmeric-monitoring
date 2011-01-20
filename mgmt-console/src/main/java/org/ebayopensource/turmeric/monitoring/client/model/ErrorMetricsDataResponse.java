/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.monitoring.client.model;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

/**
 * ErrorMetricsDataResponse
 *
 */
public class ErrorMetricsDataResponse extends JavaScriptObject {
    
    protected ErrorMetricsDataResponse() {
    }
    
    public static final native ErrorMetricsDataResponse fromJSON (String json) /*-{
        try {
            return eval('(' + json + ')');
        } catch (err) {
            return null;
        }
    }-*/;

    public final native JsArray<ErrorViewDataJS> getReturnData () /*-{
        if (!this.getErrorMetricsDataResponse)
            return null;
        return this.getErrorMetricsDataResponse.returnData;
    }-*/;
}
