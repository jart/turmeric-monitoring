/*******************************************************************************
 * Copyright (c) 2006-2010 eBay Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.ebayopensource.turmeric.monitoring.client.model;

public enum ConsumerMetric {
    CallVolume ("CallCount"), 
    Performance ("ResponseTime"),
    Errors ("ErrorCount"), 
    TopVolume ("CallCount"), 
    LeastPerformance ("ResponseTime"), 
    TopServiceErrors ("ErrorCount"), 
    TopConsumerErrors ("ErrorCount");

    private String metricName;

    private ConsumerMetric (String metricName) {
        this.metricName = metricName;
    }

    public String toMetricName () {
        return this.metricName;
    }
}